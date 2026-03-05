package com.example.learnkeep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddKnowledgeActivity.class))
        );

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_topics) {
                selectedFragment = new TopicsFragment();
            }
            if (item.getItemId() == R.id.nav_stats) {
                selectedFragment = new StatsFragment();
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment)
                    .commit();
            return true;
        });

        // Default screen
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new TopicsFragment())
                .commit();

        // Notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }
    }
}