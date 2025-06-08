package com.example.mobiledev2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatusPayment extends AppCompatActivity {

    private TextView orderIdTextView;
    private Button btnKembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_payment);

        orderIdTextView = findViewById(R.id.orderIdTextView);
        btnKembali = findViewById(R.id.btnKembali);

        // Ambil order_id dari intent
        String orderId = getIntent().getStringExtra("order_id");
        if (orderId != null) {
            orderIdTextView.setText("Order ID: #" + orderId);
        }

        // Kembali ke MainActivity atau halaman utama
        btnKembali.setOnClickListener(v -> {
            Intent intent = new Intent(StatusPayment.this, third.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
