package com.example.mobiledev2;

public class Transaksi {
    private String nama, bayar, jam, idTransaksi, status, orderId;

    public Transaksi(String nama, String bayar, String jam, String idTransaksi, String status, String orderId) {
        this.nama = nama;
        this.bayar = bayar;
        this.jam = jam;
        this.idTransaksi = idTransaksi;
        this.status = status;
        this.orderId = orderId;
    }

    public String getNama() { return nama; }
    public String getBayar() { return bayar; }
    public String getJam() { return jam; }
    public String getIdTransaksi() {return idTransaksi; }
    public String getStatus() {return status; }
    public String getOrderId() {return orderId; }
}

