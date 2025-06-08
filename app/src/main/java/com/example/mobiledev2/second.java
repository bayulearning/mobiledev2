package com.example.mobiledev2;



import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;

import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;
import com.midtrans.sdk.uikit.external.UiKitApi;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link second#newInstance} factory method to
 * create an instance of this fragment.
 */
public class second extends Fragment {
    private FlexboxLayout timeContainer;
//    private String selectedTime = "";
    private List<String> selectedTimes = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CLIENT_KEY = "SB-Mid-client-TeCLukjvzrZc2gm4";
    private static final String MERCHANT_URL = "https://your-backend.com/payment/callback";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public second() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment second.
     */
    // TODO: Rename and change types and number of parameters
    public static second newInstance(String param1, String param2) {
        second fragment = new second();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Set<String> selectedTimesLapangan1 = new HashSet<>();
    private Set<String> selectedTimesLapangan2 = new HashSet<>();
    private int currentLapangan = 1; // default lapangan
    Map<String, Set<String>> bookedTimesLapangan1 = new HashMap<>();
    Map<String, Set<String>> bookedTimesLapangan2 = new HashMap<>();
    private String tanggalDipilih = ""; // untuk menyimpan tanggal yang dipilih


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

        // Inflate layout fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        // Inisialisasi komponen
        Button btnPilihTanggal = view.findViewById(R.id.btnPilihTanggal);
        TextView tvTanggalDipilih = view.findViewById(R.id.tvTanggalDipilih);
        timeContainer = view.findViewById(R.id.gridJam); // gunakan "view" bukan "timeContainer.findViewById"
        Button btnLapangan1 = view.findViewById(R.id.lap1);
        Button btnLapangan2 = view.findViewById(R.id.lap2);
        Button btnReset = view.findViewById(R.id.btnreset);


         // ID dari RelativeLayout




// Inisialisasi MidtransSDK// Ganti dengan clientKey dan environment yang sesuai

        // Setelah token diterima dari server, panggil startPaymentUiFlow
        // Dapatkan snapToken yang valid dari server

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



        // Awal, sembunyikan jam
        timeContainer.setVisibility(View.GONE);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.hargacontainer, new HargaFragment());
        transaction.commit();


        // Daftar jam tersedia
        List<String> timeList = Arrays.asList("09:00", "10:00", "11:00", "12:00", "13:00", "14:00",
                "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");

        // Generate tombol jam, tapi sembunyikan
        generateTimeButtons(timeList);

        // --- Atur listener tombol Lapangan ---
        btnLapangan1.setOnClickListener(v -> {
            switchLapangan(1);
            highlightLapanganButton(btnLapangan1, btnLapangan2);
        });

        btnLapangan2.setOnClickListener(v -> {
            switchLapangan(2);
            highlightLapanganButton(btnLapangan2, btnLapangan1);
        });

        // --- Atur listener tombol pilih tanggal ---
        btnPilihTanggal.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Pilih Tanggal Booking")
                    .build();

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                tanggalDipilih = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date(selection));

                btnPilihTanggal.setText(tanggalDipilih);
                tvTanggalDipilih.setText("Tanggal Dipilih: " + tanggalDipilih);

                // Bersihkan pilihan jam user (jika ganti tanggal)
                selectedTimesLapangan1.clear();
                selectedTimesLapangan2.clear();

                // Hapus semua jam di UI
                resetAllSelections();

                // Sembunyikan dulu tombol jam kalau belum pilih lapangan
                timeContainer.setVisibility(currentLapangan == 0 ? View.GONE : View.VISIBLE);

                if (currentLapangan != 0) {
                    updateJamUI();
                }

            });
        });

        // Setelah tombol lapangan dibuat
//        loadHargaFragment();

//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            updateHargaCheckout();
//        }, 300);

        Button btnBookingNow = view.findViewById(R.id.bookingnow);

        btnBookingNow.setOnClickListener(v -> {
            HargaFragment hargaFragment = (HargaFragment) getChildFragmentManager().findFragmentById(R.id.hargacontainer);

            if (hargaFragment != null) {
                String hargaString = hargaFragment.getHarga(); // misalnya method ini mengembalikan isi dari TextView
                Log.d("TAG", "Harga saat ini: " + hargaString);
            }
            // Cek apakah tombol sudah pernah diklik sebelumnya untuk mencegah klik ganda
            btnBookingNow.setEnabled(false);  // Nonaktifkan tombol agar tidak bisa diklik dua kali
            // Menampilkan konfirmasi sebelum melanjutkan booking
            new AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi Booking")
                    .setMessage("Apakah Anda yakin ingin melakukan booking?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // Proses booking
                        String URL = "http://10.0.2.2/login_akun/booking.php"; // URL server untuk proses booking

                        if (selectedTimes.isEmpty()) {
                            Toast.makeText(requireContext(), "Pilih minimal 1 jam", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 1. Urutkan dan ambil jam awal dan akhir
                        Collections.sort(selectedTimes);
                        String jamAwal = selectedTimes.get(0);
                        String jamAkhir = selectedTimes.get(selectedTimes.size() - 1);
                        String jamFinal = jamAwal.equals(jamAkhir) ? jamAwal : jamAwal + "-" + jamAkhir ;

                        // Lanjutkan proses booking ke server, misalnya:
                        Log.d("JamFinal", jamFinal);

                        if (hargaFragment != null) {
                            int jumlahJamDipilih = (currentLapangan == 1) ? selectedTimesLapangan1.size() : selectedTimesLapangan2.size();
                            hargaFragment.updateHarga(jumlahJamDipilih);
                        }

                        SharedPreferences sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                        String user = sharedPref.getString("username", ""); // "" jika belum login

                        // Data yang ingin dikirim
                        // Bisa ambil dari SharedPreferences atau EditText
                        String tanggal = tanggalDipilih;
                        String status = "menunggu pembayaran";


                        RequestQueue queue = Volley.newRequestQueue(requireActivity());
                        StringRequest request = new StringRequest(Request.Method.POST, URL,
                                response -> {
                                    Log.d("Response", response);
                                    try {
                                        // Parse response dari server
                                        JSONObject obj = new JSONObject(response);
                                        String resultStatus = obj.getString("status");
                                        String message = obj.getString("message");

                                        // Tampilkan pesan status ke user
                                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();

                                        // Jika booking sukses, ubah status atau tampilan di fragment yang sama
                                        if (resultStatus.equals("success")) {
                                            // Menonaktifkan tombol booking agar tidak bisa diklik lagi
                                            btnBookingNow.setEnabled(false); // Menonaktifkan tombol
                                        }
                                    } catch (JSONException e) {
                                        Log.e("JSON Error", e.getMessage());
                                        Toast.makeText(requireContext(), "Format respon error", Toast.LENGTH_SHORT).show();
                                        btnBookingNow.setEnabled(true); // Aktifkan tombol lagi jika terjadi error
                                    }
                                },
                                error -> {
                                    Toast.makeText(requireContext(), "Request error", Toast.LENGTH_SHORT).show();
                                    btnBookingNow.setEnabled(true); // Aktifkan tombol lagi jika request gagal
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("nama", user); // sesuaikan dengan nama POST di PHP
                                params.put("tanggal", tanggal);
                                params.put("lapangan", String.valueOf(currentLapangan));
                                params.put("jam", jamFinal );
                                params.put("status_pembayaran", status);
                                String bayar = hargaFragment != null ? hargaFragment.getHarga() : "0"; // Ambil harga dari fragment
                                params.put("bayar", bayar);
                                return params;
                            }
                        };


//                        try {
//                            Log.e("FULL_RESPONSE", "Response: " + response);
//                            JSONObject jsonResponse = new JSONObject(response);
//                            if (jsonResponse.has("token")) {
//                                String snapToken = jsonResponse.getString("token");
//                                MidtransSDK.getInstance().startPaymentUiFlow(requireActivity(), snapToken);
//                            } else {
//                                Log.e("Error", "Token tidak ditemukan dalam response");
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    },
//                    error -> {
//                        error.printStackTrace();
//                    }
//            ) {
//                @Override
//                protected Map<String, String> getParams() {
//                    return null;  // Tidak dipakai
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Content-Type", "application/json");
//                    return headers;
//                }
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    try {
//                        HargaFragment hargaFragment = (HargaFragment) getChildFragmentManager().findFragmentById(R.id.hargacontainer);
//                        String harga = (hargaFragment != null) ? hargaFragment.getHarga() : "0";
//
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("amount", harga);
//
//                        return jsonObject.toString().getBytes("utf-8");
//                    } catch (JSONException | UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//                }

                        queue.add(request);

                    })
                    .setNegativeButton("Batal", (dialog, which) -> {
                        // Mengaktifkan tombol kembali jika pengguna membatalkan
                        btnBookingNow.setEnabled(true);
                        dialog.dismiss(); // Tutup dialog
                    })
                    .show(); // Tampilkan dialog konfirmasi
        });
        // --- Atur listener tombol Reset ---
        btnReset.setOnClickListener(v -> {
            if (currentLapangan == 1) {
                selectedTimesLapangan1.clear();
                bookedTimesLapangan1.remove(tanggalDipilih);
            } else if (currentLapangan == 2) {
                selectedTimesLapangan2.clear();
                bookedTimesLapangan2.remove(tanggalDipilih);
            }

            updateJamUI();



        });

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Panggil setelah layout selesai dibuat
        loadHargaFragment();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            HargaFragment hargaFragment = (HargaFragment) getChildFragmentManager()
                    .findFragmentById(R.id.hargacontainer);

            if (hargaFragment != null) {
                int jumlahJamDipilih = (currentLapangan == 1) ? selectedTimesLapangan1.size() : selectedTimesLapangan2.size();
                hargaFragment.updateHarga(jumlahJamDipilih);
            } else {
                Log.e("HARGA", "HargaFragment masih null");
            }
        }, 300); // tunda 300ms

    }

        public void startPayment(String snapToken){
                // 1. Panggil API ke server untuk minta Snap Token
                getSnapTokenDariServer(new TokenCallback() {
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

            private void getSnapTokenDariServer(TokenCallback callback) {
                // 🔥 Ini simulasi. Nanti ganti dengan API call kamu, misal pakai Retrofit / Volley.
                String fakeSnapToken = "34d0c13f-76c8-49f1-be18-33ab1221971a";
                callback.onTokenReceived(fakeSnapToken);
            }

            interface TokenCallback {
                void onTokenReceived(String token);
                void onError(String errorMessage);
            }


    private void updateHargaCheckout() {
        HargaFragment hargaFragment = (HargaFragment) getChildFragmentManager()
                .findFragmentById(R.id.hargacontainer);

        if (hargaFragment != null) {
            int jumlahJamDipilih = (currentLapangan == 1) ? selectedTimesLapangan1.size() : selectedTimesLapangan2.size();
            hargaFragment.updateHarga(jumlahJamDipilih);
        }
    }


    private HargaFragment hargaFragment;

    private void loadHargaFragment() {
        hargaFragment = new HargaFragment(); // ← simpan ke variabel global
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.hargacontainer, hargaFragment);
        transaction.commit();
    }

    private void updateJamUI() {
        // Jika belum memilih lapangan (misalnya 0 = belum dipilih)
        if (currentLapangan == 0) {
            timeContainer.setVisibility(View.GONE);
            TextView textInfo = getView().findViewById(R.id.pilih_lapangan); // Pastikan ID sesuai
            textInfo.setVisibility(View.VISIBLE);
            return; // keluar dari fungsi
        }

        // Jika lapangan sudah dipilih
        timeContainer.setVisibility(View.VISIBLE);
        TextView textInfo = getView().findViewById(R.id.pilih_lapangan);
        textInfo.setVisibility(View.GONE);

        Set<String> bookedTimes = currentLapangan == 1
                ? bookedTimesLapangan1.getOrDefault(tanggalDipilih, new HashSet<>())
                : bookedTimesLapangan2.getOrDefault(tanggalDipilih, new HashSet<>());

        Set<String> selectedTimes = currentLapangan == 1
                ? selectedTimesLapangan1
                : selectedTimesLapangan2;

        for (int i = 0; i < timeContainer.getChildCount(); i++) {
            View child = timeContainer.getChildAt(i);
            if (child instanceof Button) {
                Button jamBtn = (Button) child;
                String jam = jamBtn.getText().toString();

                if (bookedTimes.contains(jam)) {
                    jamBtn.setEnabled(false);
                    jamBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                } else {
                    jamBtn.setEnabled(true);
                    if (selectedTimes.contains(jam)) {
                        jamBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    } else {
                        jamBtn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    }
                }
            }
        }
    }


    private void pilihJam(Button button) {
        String jam = button.getText().toString();

        // Toggle selected times (untuk highlight hijau)
        Set<String> selectedTimes = currentLapangan == 1
                ? selectedTimesLapangan1
                : selectedTimesLapangan2;

        if (selectedTimes.contains(jam)) {
            selectedTimes.remove(jam);
        } else {
            selectedTimes.add(jam);
        }

        // Bisa skip bagian booked kalau kamu hanya ingin selected saja
        // Tapi kalau tetap ingin handle booking logic juga:
        Map<String, Set<String>> bookedMap = currentLapangan == 1
                ? bookedTimesLapangan1
                : bookedTimesLapangan2;

        Set<String> bookedSet = bookedMap.getOrDefault(tanggalDipilih, new HashSet<>());

        if (bookedSet.contains(jam)) {
            bookedSet.remove(jam);
        } else {
            bookedSet.add(jam);
        }
        bookedMap.put(tanggalDipilih, bookedSet);

            updateHargaCheckout();

        // Refresh UI
        updateJamUI();
    }


    private void highlightLapanganButton(Button selected, Button unselected) {
        selected.getBackgroundTintList();
        unselected.getBackgroundTintList();

        selected.setTextColor(Color.WHITE);
        unselected.setTextColor(Color.BLACK);
    }

    private void generateTimeButtons(List<String> timeList) {
        for (String time : timeList) {
            Button btn = new Button(getContext());
            btn.setText(time);
            btn.setTextColor(Color.BLACK);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                Button clickedButton = (Button) v;
                String jam = clickedButton.getText().toString();

                if (selectedTimes.contains(jam)) {
                    selectedTimes.remove(jam);
                    clickedButton.setBackgroundColor(Color.BLACK); // tidak dipilih
                } else {
                    selectedTimes.add(jam);
                    clickedButton.setBackgroundColor(Color.GREEN); // dipilih
                }
                pilihJam((Button) v);
                Log.d("SelectedTimes", selectedTimes.toString());
            });


            timeContainer.addView(btn);
        }
    }


    private void resetAllSelections() {
        for (int i = 0; i < timeContainer.getChildCount(); i++) {
            View child = timeContainer.getChildAt(i);
            if (child instanceof Button) {
                child.setBackgroundResource(R.drawable.background_stack); // background default
                child.setSelected(false); // tandai sebagai tidak terpilih
            }
        }
    }


    private void updateSelectedButton(Button btn) {
        // Cek apakah tombol sedang dalam keadaan terpilih
        String time = btn.getText().toString();

        Set<String> currentSelection = (currentLapangan == 1) ? selectedTimesLapangan1 : selectedTimesLapangan2;

        if (currentSelection.contains(time)) {
            currentSelection.remove(time);
            btn.setBackgroundResource(R.drawable.background_stack);
            btn.setSelected(false);
        } else {
            currentSelection.add(time);
            btn.setBackgroundResource(R.drawable.arrow);
            btn.setSelected(true);
        }

    }

    private void switchLapangan(int lapangan) {
        currentLapangan = lapangan;

        // Tampilkan container tombol jam
        timeContainer.setVisibility(View.VISIBLE);

        // Update tampilan jam sesuai lapangan yang dipilih
        updateJamUI();

        // Highlight lapangan aktif
        Button btnLapangan1 = getView().findViewById(R.id.lap1);
        Button btnLapangan2 = getView().findViewById(R.id.lap2);
        highlightLapanganButton(lapangan == 1 ? btnLapangan1 : btnLapangan2,
                lapangan == 1 ? btnLapangan2 : btnLapangan1);
    }




}