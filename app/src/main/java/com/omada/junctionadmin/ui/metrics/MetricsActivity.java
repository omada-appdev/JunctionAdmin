package com.omada.junctionadmin.ui.metrics;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.omada.junctionadmin.R;
import com.omada.junctionadmin.ui.create.CreateActivity;
import com.omada.junctionadmin.ui.institute.InstituteActivity;
import com.omada.junctionadmin.ui.profile.ProfileActivity;


public class MetricsActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metrics_activity_layout);

        setupBottomNavigation();
        setupTriggers();
    }

    private void setupBottomNavigation() {

        BottomNavigationView bottomMenu = findViewById(R.id.metrics_bottom_navigation);
        bottomMenu.getMenu().findItem(R.id.metrics_button).setChecked(true);
        bottomMenu.setOnNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();
            Intent i = null;

            if (itemId == R.id.profile_button){
                i = new Intent(MetricsActivity.this, ProfileActivity.class);
            }
            else if (itemId == R.id.create_button){
                i = new Intent(MetricsActivity.this, CreateActivity.class);
            }
            else if (itemId == R.id.institute_button){
                i = new Intent(MetricsActivity.this, InstituteActivity.class);
            }

            if (i != null) {
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                return true;

            } else {
                return false;
            }

        });

    }

    private void setupTriggers() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomMenu = findViewById(R.id.metrics_bottom_navigation);
        bottomMenu.getMenu().findItem(R.id.metrics_button).setChecked(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
