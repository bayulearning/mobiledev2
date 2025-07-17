package com.example.mobiledev2;

public class Transaksi {
    private String nama, bayar, jam, idTransaksi, status, lapangan, tanggal;

    public Transaksi(String nama, String bayar, String jam, String idTransaksi, String status, String lapangan, String tanggal) {
        this.nama = nama;
        this.bayar = bayar;
        this.jam = jam;
        this.idTransaksi = idTransaksi;
        this.status = status;
        this.lapangan = lapangan;
        this.tanggal = tanggal;
    }

    public String getNama() { return nama; }
    public String getBayar() { return bayar; }
    public String getJam() { return jam; }
    public String getIdTransaksi() {return idTransaksi; }
    public String getStatus() {return status; }
    public String getLapangan() {return lapangan; }
    public String getTanggal() {return tanggal; }
}

