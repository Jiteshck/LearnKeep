package com.example.learnkeep;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsFragment extends Fragment {

    TextView txtStreak;
    TextView txtRetention;
    TextView txtTopicsCount;
    TextView txtReviewsDue;
    TextView txtStrongSubjects;
    TextView txtWeakSubjects;
    LineChart chart;
    RecyclerView recyclerReview;
    ArrayList<ReviewItem> reviewList = new ArrayList<>();

    public StatsFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        txtStreak = view.findViewById(R.id.txtStreak);
        txtRetention = view.findViewById(R.id.txtRetention);
        txtTopicsCount = view.findViewById(R.id.txtTopicsCount);
        txtReviewsDue = view.findViewById(R.id.txtReviewsDue);
        txtStrongSubjects = view.findViewById(R.id.txtStrongSubjects);
        txtWeakSubjects = view.findViewById(R.id.txtWeakSubjects);
        chart = view.findViewById(R.id.chart);
        recyclerReview = view.findViewById(R.id.recyclerReview);
        recyclerReview.setLayoutManager(new LinearLayoutManager(getContext()));

        chart.setNoDataText("No study data yet");
        loadStats();
        return view;
    }

    private void loadStats() {
        new Thread(() -> {
            List<KnowledgeEntity> topics =
                    AppDatabase.getInstance(requireContext())
                            .knowledgeDao()
                            .getAll();
            requireActivity().runOnUiThread(() -> {
                txtTopicsCount.setText(String.valueOf(topics.size()));
                int reviews = 0;
                for (KnowledgeEntity t : topics) {
                    try {
                        int conf = Integer.parseInt(t.confidence);
                        if (conf <= 3)
                            reviews++;
                    } catch (Exception ignored) {}
                }
                txtReviewsDue.setText(String.valueOf(reviews));
                reviewList.clear();

                long now = System.currentTimeMillis();
                for (KnowledgeEntity t : topics) {
                    int confidence = 5;
                    try {
                        confidence = Integer.parseInt(t.confidence);
                    } catch (Exception ignored) {}

                    int days;
                    if (confidence <= 3) days = 1;
                    else if (confidence <= 6) days = 3;
                    else if (confidence <= 8) days = 7;
                    else days = 14;
                    long reviewTime = t.createdAt + (days * 24L * 60 * 60 * 1000);
                    long diff = reviewTime - now;

                    String day;
                    int priority;
                    if (diff < 0){
                        day = "Overdue";
                        priority = 0;
                    }
                    else if (diff <= 86400000) {
                        day = "Today";
                        priority = 1;
                    }
                    else if (diff <= 2 * 86400000) {
                        day = "Tomorrow";
                        priority = 2;
                    }
                    else {
                        day = "Upcoming";
                        priority = 3;
                    }
                    reviewList.add(
                            new ReviewItem(t.title,day,t.tags,priority)
                    );
                }
                reviewList.sort((a, b) -> a.priority - b.priority);
                ReviewAdapter adapter = new ReviewAdapter(reviewList);
                recyclerReview.setAdapter(adapter);
                calculateRetention(topics);
                calculateStreak(topics);
                analyzeSubjects(topics);
                loadChart(topics);
            });
        }).start();
    }

    private void calculateRetention(List<KnowledgeEntity> topics) {
        if (topics == null || topics.isEmpty()) {
            txtRetention.setText("0%");
            return;
        }
        int sum = 0;
        for (KnowledgeEntity t : topics) {
            try {
                sum += Integer.parseInt(t.confidence);
            } catch (Exception ignored) {}
        }
        int avg = sum / topics.size();
        txtRetention.setText((avg * 10) + "%");
    }

    private void calculateStreak(List<KnowledgeEntity> topics) {
        int streak = Math.min(topics.size(), 7);
        txtStreak.setText(streak + " Days");
    }

    private void analyzeSubjects(List<KnowledgeEntity> topics) {
        Map<String, Integer> subjectScore = new HashMap<>();
        Map<String, Integer> subjectCount = new HashMap<>();

        for (KnowledgeEntity t : topics) {
            if (t.tags == null) continue;
            int confidence = 0;
            try {
                confidence = Integer.parseInt(t.confidence);
            } catch (Exception ignored) {}

            subjectScore.put(
                    t.tags,
                    subjectScore.getOrDefault(t.tags, 0) + confidence
            );
            subjectCount.put(
                    t.tags,
                    subjectCount.getOrDefault(t.tags, 0) + 1
            );
        }

        StringBuilder strong = new StringBuilder();
        StringBuilder weak = new StringBuilder();

        for (String tag : subjectScore.keySet()) {
            int avg = subjectScore.get(tag) / subjectCount.get(tag);
            if (avg >= 7)
                strong.append(tag).append("\n");
            if (avg <= 3)
                weak.append(tag).append("\n");
        }
        txtStrongSubjects.setText(
                strong.length() == 0 ? "None" : strong.toString()
        );
        txtWeakSubjects.setText(
                weak.length() == 0 ? "None" : weak.toString()
        );
    }

    private void loadChart(List<KnowledgeEntity> topics) {
        List<Entry> entries = new ArrayList<>();
        int index = 0;
        for (KnowledgeEntity t : topics) {
            try {
                float confidence = Float.parseFloat(t.confidence);
                entries.add(new Entry(index++, confidence));
            } catch (Exception ignored) {}
        }

        LineDataSet dataSet = new LineDataSet(entries, "Confidence");
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(4f);
        dataSet.setColor(Color.parseColor("#3B28CC"));
        dataSet.setCircleColor(Color.parseColor("#3B28CC"));
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateX(1200);
        chart.invalidate();
    }
}