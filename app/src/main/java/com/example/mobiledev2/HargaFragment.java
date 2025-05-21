package com.example.mobiledev2;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.midtrans.sdk.corekit.core.MidtransSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HargaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HargaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView hargaTextView;
    private int hargaPerJam = 80000;
    int amount = 0;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HargaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HargaFragment newInstance(String param1, String param2) {
        HargaFragment fragment = new HargaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        View view = inflater.inflate(R.layout.fragment_harga, container, false);
        hargaTextView = view.findViewById(R.id.tvnominal);


        // Jika sebelumnya sudah ada jumlah jam, tampilkan
        if (jumlahJamPending >= 0) {
            updateHarga(jumlahJamPending);
        }

        return view;
    }
    private int jumlahJamPending = -1;  // -1 artinya belum di-set
    private int hargaTotalTerakhir = 0; // <- simpan nilai terbaru di sini

    public String getHarga() {
        if (hargaTextView != null) {
            String text = hargaTextView.getText().toString();
            Log.d("GET_HARGA", "TextView value: " + text);
            return text.replaceAll("[^0-9]", "");
        } else {
            Log.e("GET_HARGA", "hargaTextView masih null");
        }
        return "0";
    }



    // Fungsi untuk update harga
    public void updateHarga(int jumlahJam) {
        int jamBayar = Math.max(jumlahJam - 1, 0);
        int totalHarga = jamBayar * hargaPerJam;

        Log.d("HARGA", "jumlahJam = " + jumlahJam + ", totalHarga = " + totalHarga);

        if (hargaTextView != null) {
            hargaTextView.setText("Rp" + totalHarga + ",-");
        } else {
            Log.e("HARGA", "hargaTextView masih null di updateHarga()");
        }
    }



//    keteranganJam + " dipilih\n" +

    // Fungsi untuk mengambil nilai harga dan mengirimkan ke server
    public void kirimRequest() {
        String amountString = hargaTextView.getText().toString().replaceAll("[^0-9]", ""); // Mengambil nilai angka saja

        try {
            amount = Integer.parseInt(amountString);  // Mengonversi string menjadi integer
        } catch (NumberFormatException e) {
            Log.e("Error", "Amount tidak valid: " + amountString);
            return;  // Jangan lanjutkan jika amount invalid
        }

        // Mengirimkan request ke server
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    try {
                        Log.e("FULL RESPONSE", response);  // Tampilkan seluruh response
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("token")) {
                            String token = jsonResponse.getString("token");
                            // Proses token pembayaran
                            MidtransSDK.getInstance().startPaymentUiFlow(requireActivity(), token);
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
                return null;  // Tidak perlu menggunakan getParams() karena kita mengirimkan data dalam JSON body
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
                    jsonObject.put("amount", amount);  // Kirimkan amount yang sudah diparsing sebagai integer
                    return jsonObject.toString().getBytes("utf-8");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return super.getBody();
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // Menambahkan request ke queue
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}