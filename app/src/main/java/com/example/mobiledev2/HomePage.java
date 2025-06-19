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
private static final String PREFS_NAME = "fragment_prefs";
private static final  String KEY_ACTIVE_FRAGMENT = "active_fragment";
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

        // Ambil fragment yang terakhir aktif
        int savedFragmentId = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getInt(KEY_ACTIVE_FRAGMENT, R.id.home);

        // Tampilkan fragment sesuai ID
        Fragment initialFragment = getFragmentById(savedFragmentId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView2, initialFragment)
                .commit();

        // Set icon terpilih
        BottomNavigationView.setSelectedItemId(savedFragmentId);

        BottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = getFragmentById(item.getItemId());

                // Simpan ID fragment yang aktif
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .edit()
                        .putInt(KEY_ACTIVE_FRAGMENT, item.getItemId())
                        .apply();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView2, selectedFragment)
                        .commit();

                return true;
            }
        });
    }

    private Fragment getFragmentById(int id) {
        if (id == R.id.book) return new second();
        else if (id == R.id.notif) return new third();
        else if (id == R.id.profil) return new fourth();
        else return new first(); // default: home
    }

}
