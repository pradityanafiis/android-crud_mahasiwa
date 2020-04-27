package com.praditya.appcrud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.praditya.appcrud.api.RetrofitClient;
import com.praditya.appcrud.models.Mahasiswa;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailMahasiswaActivity extends AppCompatActivity {
    @BindView(R.id.img_mahasiswa) ImageView imgMahasiswa;
    @BindView(R.id.tv_mahasiswa_nrp) TextView tvMahasiswaNrp;
    @BindView(R.id.tv_mahasiswa_name) TextView tvMahasiswaName;
    @BindView(R.id.tv_mahasiswa_address) TextView tvMahasiswaAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mahasiswa);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Mahasiswa");

        ButterKnife.bind(this);
        Mahasiswa mahasiswa = (Mahasiswa) getIntent().getSerializableExtra("Mahasiswa");

        Glide.with(DetailMahasiswaActivity.this)
                .load(RetrofitClient.getImageUrl(mahasiswa.getFoto()))
                .placeholder(R.drawable.ic_account_circle_black_512dp)
                .into(imgMahasiswa);
        tvMahasiswaNrp.setText("NRP : " + mahasiswa.getNrp());
        tvMahasiswaName.setText(mahasiswa.getNama());
        tvMahasiswaAddress.setText("Alamat : " + mahasiswa.getAlamat());
    }
}
