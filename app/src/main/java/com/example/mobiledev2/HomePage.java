package com.example.mobiledev2;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {
BottomNavigationView BottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView2, new first())
                .commit();

        BottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;

                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    selectedFragment = new first();
                } else if (itemId == R.id.book) {
                    selectedFragment = new second();
                } else if (itemId == R.id.notif) {
                    selectedFragment = new third();
                } else if (itemId == R.id.profil) {
                    selectedFragment = new fourth();
                } else {
                    return false;
                }

                // Ganti fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView2, selectedFragment)
                        .commit();

                return true;
            }
        });
    }
}
