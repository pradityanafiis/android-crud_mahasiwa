package com.praditya.appcrud.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetMahasiswaResponse {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("mahasiswa")
    @Expose
    private ArrayList<Mahasiswa> mahasiswa;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Mahasiswa> getMahasiswa() {
        return mahasiswa;
    }

    public void setMahasiswa(ArrayList<Mahasiswa> mahasiswa) {
        this.mahasiswa = mahasiswa;
    }
}
