package com.example.mobiledev2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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

        return view;
    }
}