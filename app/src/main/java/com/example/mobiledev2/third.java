package com.example.mobiledev2;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.midtrans.sdk.corekit.core.MidtransSDK;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerTransaksi);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Dummy data (ganti dengan data dari server nantinya)
        transaksiList = new ArrayList<>();
        transaksiList.add(new Transaksi("2025-05-19", "Lapangan A", "15:00 - 16:00"));
        transaksiList.add(new Transaksi("2025-05-20", "Lapangan B", "16:00 - 17:00"));

        adapter = new TransaksiAdapter(transaksiList, new TransaksiAdapter.OnBayarClickListener() {
            @Override
            public void onBayarClick(Transaksi transaksi) {
                String URL = "http://10.0.2.2/login_akun/create_transaction.php";

                RequestQueue queue = Volley.newRequestQueue(requireActivity());

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
                            HargaFragment hargaFragment = (HargaFragment) getChildFragmentManager().findFragmentById(R.id.hargacontainer);
                            String harga = (hargaFragment != null) ? hargaFragment.getHarga() : "0";

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("amount", harga);

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
        
        return view;
    }


}