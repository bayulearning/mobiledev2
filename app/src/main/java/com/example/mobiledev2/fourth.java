package com.example.mobiledev2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fourth#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fourth extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
//    Button mode, changepass, language, share, privacy, term, about;
//    TextView profil, proemail;
    // TODO: Rename and change types of parameters
    public static final String PREFS_NAME = "settings";
    public static final String KEY_DARK_MODE = "dark_mode";
    private String mParam1;
    private String mParam2;

    public fourth() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fourth.
     */
    // TODO: Rename and change types and number of parameters
    public static fourth newInstance(String param1, String param2) {
        fourth fragment = new fourth();
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
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_fourth, container, false);

        Switch switchmode = view.findViewById(R.id.mode);
        ImageButton changepass = view.findViewById(R.id.btnchangepass);
        ImageButton language = view.findViewById(R.id.btnlanguage);
        ImageButton share = view.findViewById(R.id.btnshare);
        ImageButton policy = view.findViewById(R.id.btnpolicy);
        ImageButton term = view.findViewById(R.id.btnterm);
        ImageButton about = view.findViewById(R.id.btnabout);

        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean IsDarkMode = prefs.getBoolean(KEY_DARK_MODE,false);
        switchmode.setChecked(IsDarkMode);

        AppCompatDelegate.setDefaultNightMode(IsDarkMode
        ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        switchmode.setOnCheckedChangeListener((buttonView, isChecked) -> {
          SharedPreferences.Editor editor = prefs.edit();
          editor.putBoolean(KEY_DARK_MODE, isChecked);
          editor.apply();

          AppCompatDelegate.setDefaultNightMode(isChecked
          ? AppCompatDelegate.MODE_NIGHT_YES
                  :AppCompatDelegate.MODE_NIGHT_NO);
        });
simpanprofil();
        return view;
    }

    public void simpanprofil() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        Log.d("DebugUsername", "Username dari SharedPreferences: " + username);

        if (username.isEmpty()) {
            Toast.makeText(getActivity(), "Username tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/login_akun/profil.php?nama="+ username;

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("success")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");

                                // Ambil username dari SharedPreferences
                                SharedPreferences sharedPref = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                                String usernameSession = sharedPref.getString("username", "");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject user = dataArray.getJSONObject(i);
                                    String username = user.getString("username");

                                    // Cocokkan dengan user yang sedang login
                                    if (username.equals(usernameSession)) {
                                        String email = user.getString("email");

                                        // Set ke TextView
                                        TextView textUsername = getView().findViewById(R.id.usernama);
                                        TextView textEmail = getView().findViewById(R.id.useremail);

                                        textUsername.setText(username);
                                        textEmail.setText(email);
                                        break; // data ditemukan, keluar dari loop
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "Data user tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(stringRequest);
    }

}