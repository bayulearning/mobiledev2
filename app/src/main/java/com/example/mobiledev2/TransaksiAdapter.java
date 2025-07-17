package com.example.mobiledev2;

import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

        Log.d("BindView", "Menampilkan: " + trx.getNama());
        holder.txtNama.setText(trx.getNama());
        holder.txtBayar.setText(trx.getBayar());
        holder.txtJam.setText(trx.getJam());
        holder.txtlapangan.setText("lapangan   " + trx.getLapangan() +"\n"+ trx.getTanggal());
//        holder.txttanggal.setText(trx.getTanggal());

        // Status Pembayaran
        String status = trx.getStatus() != null ? trx.getStatus().toLowerCase() : "";
        holder.txtStatus.setText("Status Pembayaran: \n" + status);

        // Ubah warna background tergantung status
        GradientDrawable bgDrawable = (GradientDrawable) holder.txtStatus.getBackground();

        switch (status.toLowerCase()) {
            case "pending":
                bgDrawable.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.biru_status));
                break;
            case "settlement":
                bgDrawable.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.hijau_status));
                break;
            case "cancel":
            case "expire":
                bgDrawable.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.merah_status));
                break;
            default:
                bgDrawable.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.abu_status));
                break;
        }


        holder.txtId.setText(trx.getIdTransaksi());


        // Atur tombol bayar berdasarkan status pembayaran
        if (status.equals("settlement")) {
            holder.btnBayar.setEnabled(false);
            holder.btnBayar.setAlpha(0.5f); // tampilan tombol transparan, terlihat disable
            holder.btnBayar.setText("Sudah Dibayar");
        } else {
            holder.btnBayar.setEnabled(true);
            holder.btnBayar.setAlpha(1.0f);
            holder.btnBayar.setText("Bayar Sekarang");
        }

        holder.btnBayar.setOnClickListener(v -> {
            if (holder.btnBayar.isEnabled()) {
                bayarClickListener.onBayarClick(transaksiList.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtBayar, txtJam, txtStatus, txtId, txtlapangan, txttanggal;
        Button btnBayar;
        public ViewHolder(View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.nama);
            txtBayar = itemView.findViewById(R.id.total_bayar);
            txtJam = itemView.findViewById(R.id.total_jam);
            txtStatus = itemView.findViewById(R.id.updatestatus);
            txtId = itemView.findViewById(R.id.idTransaksi);
            txtlapangan = itemView.findViewById(R.id.lapangan);
            txttanggal = itemView.findViewById(R.id.tanggal);
            btnBayar = itemView.findViewById(R.id.btnBayar);

        }
    }


}
