package com.example.mobiledev2;

public class Transaksi {
    private String nama, bayar, jam;

    public Transaksi(String nama, String bayar, String jam) {
        this.nama = nama;
        this.bayar = bayar;
        this.jam = jam;
    }

    public String getNama() { return nama; }
    public String getBayar() { return bayar; }
    public String getJam() { return jam; }
}

