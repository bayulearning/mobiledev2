package com.example.mobiledev2;

import android.annotation.SuppressLint;
import android.content.Context;
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
        SdkUIFlowBuilder.init()
                .setClientKey("YOUR_CLIENT_KEY_FROM_MIDTRANS") // Client key kamu
                .setContext(requireActivity())
                .setTransactionFinishedCallback(new TransactionFinishedCallback() {
                    @Override
                    public void onTransactionFinished(TransactionResult result) {
                        // Handle setelah transaksi selesai
                    }
                })
                .setMerchantBaseUrl("https://yourdomain.com/") // URL server PHP kamu
                .enableLog(true) // Untuk debugging
                .buildSDK();

        adapter = new TransaksiAdapter(transaksiList, new TransaksiAdapter.OnBayarClickListener() {
            @Override
            public void onBayarClick(Transaksi transaksi) {
                String URL = "http://10.0.2.2/login_akun/create_transaction.php";

                RequestQueue queue = Volley.newRequestQueue(requireActivity());
                final Transaksi transaksiYangDipilih = transaksi;
                StringRequest request = new StringRequest(Request.Method.POST, URL,
                        response -> {
                            try {
                                Log.e("FULL_RESPONSE", "Response: " + response);
                                JSONObject jsonResponse = new JSONObject(response);
                                if (jsonResponse.has("token")) {
                                    String snapToken = jsonResponse.getString("token");
                                    MidtransSDK.getInstance().startPaymentUiFlow(requireActivity(), snapToken);
                                } else {
                                    Log.e("Error", "Token tidak ditemukan dalam response");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            error.printStackTrace();
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        return null;  // Tidak dipakai
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("amount", transaksiYangDipilih.getBayar()); // langsung ambil dari objek
                            jsonObject.put("username", transaksiYangDipilih.getNama());
                            jsonObject.put("jam", transaksiYangDipilih.getJam());

                            return jsonObject.toString().getBytes("utf-8");
                        } catch (JSONException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };

                queue.add(request);
            }
        });

        recyclerView.setAdapter(adapter);

        // Panggil ambil data dari server
        fetchBookingData();

        return view;
    }

    public void fetchBookingData() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");

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

                            transaksiList.add(new Transaksi(nama, totalBayar, totalJam));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Gagal memproses data JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
                });

        queue.add(stringRequest);
    }

    public void startPayment(String snapToken){
        // 1. Panggil API ke server untuk minta Snap Token
        getSnapTokenDariServer(new second.TokenCallback() {
            @Override
            public void onTokenReceived(String snapToken) {
                // 2. Setelah dapat Snap Token, buka Midtrans UI
                requireActivity().runOnUiThread(() -> {
                    MidtransSDK.getInstance().startPaymentUiFlow(requireActivity(), snapToken);
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), "Gagal dapat token: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSnapTokenDariServer(second.TokenCallback callback) {
        // 🔥 Ini simulasi. Nanti ganti dengan API call kamu, misal pakai Retrofit / Volley.
        String fakeSnapToken = "34d0c13f-76c8-49f1-be18-33ab1221971a";
        callback.onTokenReceived(fakeSnapToken);
    }

    interface TokenCallback {
        void onTokenReceived(String token);
        void onError(String errorMessage);
    }


}