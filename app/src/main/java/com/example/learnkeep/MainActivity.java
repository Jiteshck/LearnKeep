package com.example.learnkeep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerTopics;
    private List<KnowledgeEntity> fullList = new ArrayList<>();
    private TopicAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //Always Dark mode
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerTopics = findViewById(R.id.recyclerTopics);
        recyclerTopics.setLayoutManager(new LinearLayoutManager(this));
        EditText searchBox = findViewById(R.id.searchBox);
        ImageView btnClear = findViewById(R.id.btnClearSearch);

        searchBox.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnClear.setVisibility(View.VISIBLE);
                } else {
                    btnClear.setVisibility(View.GONE);
                }
                filterTopics(s.toString());
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        btnClear.setOnClickListener(v -> searchBox.setText(""));

        searchBox.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTopics(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddKnowledgeActivity.class))
        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTopics();
    }

    private void loadTopics() {

        fullList = AppDatabase.getInstance(this)
                .knowledgeDao()
                .getAll();

        adapter = new TopicAdapter(fullList);
        recyclerTopics.setAdapter(adapter);
    }
    private void filterTopics(String query) {
        if (adapter == null) return;
        List<KnowledgeEntity> filteredList = new ArrayList<>();

        for (KnowledgeEntity item : fullList) {
            if (item.title.toLowerCase().contains(query.toLowerCase())
                    || (item.tags != null &&
                    item.tags.toLowerCase().contains(query.toLowerCase()))) {

                filteredList.add(item);
            }
        }
        adapter = new TopicAdapter(filteredList);
        recyclerTopics.setAdapter(adapter);
    }
}

