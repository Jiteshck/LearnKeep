package com.example.learnkeep;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TopicsFragment extends Fragment {

    private RecyclerView recyclerTopics;
    private TopicAdapter adapter;
    private List<KnowledgeEntity> fullList = new ArrayList<>();

    public TopicsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topics, container, false);
        recyclerTopics = view.findViewById(R.id.recyclerTopics);
        recyclerTopics.setLayoutManager(new LinearLayoutManager(getContext()));
        EditText searchBox = view.findViewById(R.id.searchBox);
        ImageView btnClear = view.findViewById(R.id.btnClearSearch);


        // Search logic
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    btnClear.setVisibility(View.VISIBLE);
                else
                    btnClear.setVisibility(View.GONE);
                filterTopics(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        btnClear.setOnClickListener(v -> searchBox.setText(""));
        loadTopics();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTopics();
    }

    private void loadTopics() {
        new Thread(() -> {
            List<KnowledgeEntity> list =
                    AppDatabase.getInstance(requireContext())
                            .knowledgeDao()
                            .getAll();
            requireActivity().runOnUiThread(() -> {
                fullList.clear();
                if(list != null){
                    fullList.addAll(list);
                }
                if(adapter == null){
                    adapter = new TopicAdapter(fullList);
                    recyclerTopics.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            });
        }).start();
    }

    private void filterTopics(String query) {
        if (adapter == null) return;
        List<KnowledgeEntity> filteredList = new ArrayList<>();
        for (KnowledgeEntity item : fullList) {
            if (item.title.toLowerCase().contains(query.toLowerCase())
                    || (item.tags != null && item.tags.toLowerCase().contains(query.toLowerCase()))) {
                filteredList.add(item);
            }
        }
        adapter = new TopicAdapter(filteredList);
        recyclerTopics.setAdapter(adapter);
    }
}