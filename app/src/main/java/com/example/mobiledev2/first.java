package com.example.mobiledev2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link first;#newInstance} factory method to
 * create an instance of this fragment.
 */
public class first extends Fragment {
    CardView BannerCardView;
    ImageView BannerImage;
    public first() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_first, container, false);
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        BannerCardView = view.findViewById(R.id.bannerCardView);
        BannerImage = view.findViewById(R.id.bannerImage);

        // Event klik pada banner
        BannerCardView.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Banner diklik!", Toast.LENGTH_SHORT).show();

            // Contoh buka link
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://iklan-anda.com"));
            startActivity(intent);

        });
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RelativeLayout layoutToProfil = view.findViewById(R.id.layout_to_booking);
        layoutToProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke fragment fourth
                Fragment fragment = new second();
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.fragmentContainerView2, fragment);
                transaction.addToBackStack(null); // optional jika ingin bisa kembali
                transaction.commit();
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottomNavigationView);
                bottomNav.setSelectedItemId(R.id.book); // sesuaikan dengan ID item profil

            }
        });

        return view;
    }

}