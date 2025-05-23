package com.example.mobiledev2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;
import java.util.Map;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {

    private List<Transaksi> transaksiList;
    private OnBayarClickListener bayarClickListener;



    public TransaksiAdapter(List<Transaksi> transaksiList, OnBayarClickListener bayarClickListener) {
        this.transaksiList = transaksiList;
        this.bayarClickListener = bayarClickListener;
    }
    public interface OnBayarClickListener {
        void onBayarClick(Transaksi transaksi);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaksi trx = transaksiList.get(position);
        holder.txtNama.setText("Nama: " + trx.getNama());
        holder.txtBayar.setText("Bayar: " + trx.getBayar());
        holder.txtJam.setText("Jam: " + trx.getJam());
        holder.btnBayar.setOnClickListener(v -> {
            bayarClickListener.onBayarClick(transaksiList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtBayar, txtJam;
        Button btnBayar;
        public ViewHolder(View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.nama);
            txtBayar = itemView.findViewById(R.id.total_bayar);
            txtJam = itemView.findViewById(R.id.total_jam);
            btnBayar = itemView.findViewById(R.id.btnBayar);
        }
    }


}
