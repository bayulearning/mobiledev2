package com.example.mobiledev2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link third#newInstance} factory method to
 * create an instance of this fragment.
 */
public class third extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CLIENT_KEY = "SB-Mid-client-TeCLukjvzrZc2gm4";
    private static final String MERCHANT_URL = "https://your-backend.com/payment/callback";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public third() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment third.
     */
    // TODO: Rename and change types and number of parameters
    public static third newInstance(String param1, String param2) {
        third fragment = new third();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView recyclerView;
    private TransaksiAdapter adapter;
    private List<Transaksi> transaksiList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_third, container, false);
        recyclerView = view.findViewById(R.id.recyclerTransaksi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        transaksiList = new ArrayList<>();

// Inisialisasi Midtrans SDK
        SdkUIFlowBuilder.init()
                .setClientKey("YOUR_CLIENT_KEY_FROM_MIDTRANS") // Client key kamu
                .setContext(requireActivity())
                .setTransactionFinishedCallback(new TransactionFinishedCallback() {
                    @Override
                    public void onTransactionFinished(TransactionResult result) {
                        Log.d("MidtransCallback", "Callback dijalankan: " + (result != null ? result.getStatus() : "null"));
                        if (result != null && result.getResponse() != null) {
                            switch (result.getStatus()) {
                                case TransactionResult.STATUS_SUCCESS:
                                    Log.d("Midtrans", "Pembayaran berhasil: " + result.getResponse().getTransactionId());
                                    Toast.makeText(getContext(), "Pembayaran sukses!", Toast.LENGTH_LONG).show();

                                    // Pindah ke halaman sukses
                                    Intent intent = new Intent(getActivity(), StatusPayment.class);
                                    intent.putExtra("order_id", result.getResponse().getTransactionId());
                                    startActivity(intent);
                                    break;

                                case TransactionResult.STATUS_PENDING:
                                    Log.d("Midtrans", "Menunggu pembayaran");
                                    Toast.makeText(getContext(), "Menunggu pembayaran", Toast.LENGTH_LONG).show();
                                    break;

                                case TransactionResult.STATUS_FAILED:
                                    Log.d("Midtrans", "Transaksi gagal");
                                    Toast.makeText(getContext(), "Transaksi gagal", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        } else if (result != null && result.isTransactionCanceled()) {
                            Toast.makeText(getContext(), "Transaksi dibatalkan", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Transaksi tidak valid", Toast.LENGTH_LONG).show();
                        }
                    }
                })

                .setMerchantBaseUrl("https://app.sandbox.midtrans.com/snap/v4/redirection/") // URL server PHP kamu
                .enableLog(true) // Untuk debugging
                .buildSDK();

        adapter = new TransaksiAdapter(transaksiList, transaksi -> {
            String url = "http://10.0.2.2/login_akun/create_transaction.php";
            mintaSnapTokenDariServer(transaksi.getIdTransaksi(), transaksi.getNama(), url);
        });

        recyclerView.setAdapter(adapter);
        fetchBookingData(); // Ambil data dari server

        return view;
    }
    private void mintaSnapTokenDariServer(String idTransaksi,String username, String url) {
        Log.d("SnapRequest", "ID Transaksi: " + idTransaksi);
        try {
            JSONObject json = new JSONObject();
            json.put("id_transaksi", idTransaksi);
            json.put("username", username);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                    response -> {
                        Log.d("SnapTokenResponse", response.toString());
                        try {
                            if (response.has("token")) {
                                String token = response.getString("token");
                                Log.d("SNAP_TOKEN", "Token: " + token);
                                MidtransSDK.getInstance().startPaymentUiFlow(getActivity(), token);
                            } else {
                                Toast.makeText(requireContext(), "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        if (error instanceof AuthFailureError) {
                            // Tangani error otentikasi (misalnya header API key yang salah)
                            Log.e("AuthError", "Authentication failed: " + error.getMessage());
                            Toast.makeText(requireContext(), "Otentikasi gagal", Toast.LENGTH_SHORT).show();
                        } else if (error.networkResponse != null && error.networkResponse.data != null) {
                            // Ambil body response error dalam bentuk string
                            String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.e("VolleyError", responseBody);
                        } else {
                            Toast.makeText(requireContext(), "Gagal meminta token", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json"); // Ini WAJIB
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json"; // Pastikan body dianggap JSON oleh server
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchBookingData() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        Log.d("DebugUsername", "Username dari SharedPreferences: " + username);

        if (username.isEmpty()) {
            Toast.makeText(getActivity(), "Username tidak ditemukan di session", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/login_akun/invoice.php?nama=" + Uri.encode(username);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("ServerResponse", "Response: " + response);
                    try {
                        // Cek apakah response berupa objek (error)
                        if (response.trim().startsWith("{")) {
                            JSONObject errorObj = new JSONObject(response);
                            String msg = errorObj.optString("message", "Error tidak diketahui");
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Kalau bukan error, proses JSONArray
                        JSONArray jsonArray = new JSONArray(response);
                        transaksiList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject bookingJson = jsonArray.getJSONObject(i);
                            String nama = bookingJson.getString("nama");
                            String totalBayar = bookingJson.getString("bayar");
                            String totalJam = bookingJson.getString("jam");
                            String idTransaksi = bookingJson.getString("id");
                            String lapangan = bookingJson.getString("lapangan");
                            String tanggal = bookingJson.getString("tanggal");
                            String status = bookingJson.getString("status_pembayaran");
                            Log.d("ParseTransaksi", "Item ke-" + i + ": " + nama + ", bayar: " + totalBayar);
                            transaksiList.add(new Transaksi(nama, totalBayar, totalJam, idTransaksi, status, lapangan, tanggal));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Gagal memproses data JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("ThirdFragment", "Fragment tidak lagi terhubung saat menerima error.");
                    }

    });

        queue.add(stringRequest);
    }

}