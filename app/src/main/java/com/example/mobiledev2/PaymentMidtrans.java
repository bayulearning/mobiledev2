package com.example.mobiledev2;


import static android.app.PendingIntent.getActivity;

import android.app.Application;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;
import com.midtrans.sdk.uikit.external.UiKitApi;

public class PaymentMidtrans extends Application{
    String clientKey = "SB-Mid-client-TeCLukjvzrZc2gm4";
    String serverKey = "SB-Mid-server-s1tzePI-rE7WxQH-j0NbkJYf";

    String transactionToken = "your_transaction_token";
        @Override
        public void onCreate() {
            super.onCreate();

            MidtransSDK midtransSDK = MidtransSDK.getInstance();
            if (midtransSDK != null) {
                midtransSDK.startPaymentUiFlow(this, transactionToken);
            } else {
                // Tangani error ini, misalnya log atau tampilkan pesan
            }

            if (transactionToken != null && !transactionToken.isEmpty()) {
                midtransSDK.startPaymentUiFlow(this, transactionToken);
            } else {
                Log.e("Midtrans", "MidtransSDK belum diinisialisasi dengan benar."); // Tangani skenario token tidak valid
            }

            SdkUIFlowBuilder.init()
                    .setClientKey(clientKey) // Ganti dengan Client Key Sandbox kamu
                    .setContext(this)
                    .setTransactionFinishedCallback(transactionResult -> {
                        if (transactionResult.isTransactionCanceled()) {
                            Toast.makeText(this, "Transaksi Dibatalkan", Toast.LENGTH_SHORT).show();
                        } else if (transactionResult.getResponse() != null) {
                            if (transactionResult.getStatus().equalsIgnoreCase("success")) {
                                Toast.makeText(this, "Transaksi Berhasil", Toast.LENGTH_SHORT).show();
                            } else if (transactionResult.getStatus().equalsIgnoreCase("pending")) {
                                Toast.makeText(this, "Transaksi Pending", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Transaksi Gagal", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setMerchantBaseUrl("http://10.0.2.2/login_akun/create_transaction.php")
                    .setLanguage("id")
                    .setColorTheme(new CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
                    .buildSDK();



        }


    }

