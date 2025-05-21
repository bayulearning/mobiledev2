package com.example.mobiledev2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginPage extends AppCompatActivity {
    Button btnLogin, btnDaftar;
    EditText EdUser, EdPass;
    TextView TvForgot;
    CheckBox cbRememberMe;
    ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;
    String URL = "http://10.0.2.2/login_akun/login.php";

    private static final String PREF_NAME = "login_pref";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnDaftar = findViewById(R.id.btndaftar);
        btnLogin = findViewById(R.id.btnlogin);
        EdUser = findViewById(R.id.edUsername);
        EdPass = findViewById(R.id.edPassword);
        TvForgot = findViewById(R.id.TvForgot);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedUsername = preferences.getString("username", "");
        String savedPassword = preferences.getString("password", "");
        boolean isRemembered = preferences.getBoolean("remember", false);

        if (isRemembered) {
            EdUser.setText(savedUsername);
            EdPass.setText(savedPassword);
            cbRememberMe.setChecked(true);
        }

        ivTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    EdPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.baseline_visibility_24); // ganti dengan icon mata terbuka
                } else {
                    EdPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.baseline_visibility_off_24); // icon mata tertutup
                }
                EdPass.setSelection(EdPass.getText().length()); // cursor tetap di akhir
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
//                String username = EdUser.getText().toString();
//                String password = EdPass.getText().toString();

//                if (username.equals("admin") && password.equals("1234")) {
//                    Toast.makeText(LoginPage.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
//
//                    Intent intent = new Intent(LoginPage.this, HomePage.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Toast.makeText(LoginPage.this, "Username atau Password salah!", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, RegisterPage.class));
            }
        });

        TvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, LupaPassword.class));
            }
        });

    }

    private void loginUser() {
        String username = EdUser.getText().toString().trim();
        String password = EdPass.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            Toast.makeText(LoginPage.this, message, Toast.LENGTH_SHORT).show();

                            if (status.equals("success")) {
                                SharedPreferences sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("username", username ); // userFromServer = username dari response
                                editor.apply();

                                Intent intent = new Intent(LoginPage.this, HomePage.class);
                                startActivity(intent);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginPage.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginPage.this, "Gagal Terhubung ke Server", Toast.LENGTH_SHORT).show();
                        Log.e("LoginError", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", EdUser.getText().toString().trim()); // Pastikan tidak ada spasi
                params.put("password", EdPass.getText().toString().trim());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}