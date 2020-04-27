package com.praditya.appcrud;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.praditya.appcrud.adapters.MahasiswaAdapter;
import com.praditya.appcrud.api.RetrofitClient;
import com.praditya.appcrud.models.ActionResponse;
import com.praditya.appcrud.models.GetMahasiswaResponse;
import com.praditya.appcrud.models.Mahasiswa;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.rv_mahasiswa) RecyclerView rvMahasiswa;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_EDIT = 2;

    private MahasiswaAdapter adapter;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("List Mahasiswa");

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading ...");

        ButterKnife.bind(this);
        rvMahasiswa.setHasFixedSize(true);
        rvMahasiswa.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new MahasiswaAdapter();
        rvMahasiswa.setAdapter(adapter);

        getMahasiswa();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: " + "requestCode : " + requestCode + " resultCode : " + resultCode);
        if (requestCode == REQUEST_ADD && resultCode == RESULT_OK) {
            getMahasiswa();
            showMessage("Mahasiswa has been added successfully!");
        }
        else if(requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            getMahasiswa();
            showMessage("Mahasiswa has been updated successfully!");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_mahasiswa_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_menu :
                Intent addMahasiswa = new Intent(MainActivity.this, AddMahasiswaActivity.class);
                startActivityForResult(addMahasiswa, REQUEST_ADD);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMahasiswa() {
        Call<GetMahasiswaResponse> getMahasiswaResponseCall = RetrofitClient.getInstance().getAPI().getMahasiswa();
        getMahasiswaResponseCall.enqueue(new Callback<GetMahasiswaResponse>() {
            @Override
            public void onResponse(Call<GetMahasiswaResponse> call, Response<GetMahasiswaResponse> response) {
                ArrayList<Mahasiswa> mahasiswaList = response.body().getMahasiswa();
                if (response.body().getError() && mahasiswaList.isEmpty()) {
                    adapter.setMahasiswaList(mahasiswaList);
                    showMessage("Data mahasiswa tidak ditemukan!");
                }else {
                    adapter.setMahasiswaList(mahasiswaList);
                    adapter.setOnItemClickCallback(new MahasiswaAdapter.OnItemClickCallback() {
                        @Override
                        public void editMahasiswa(Mahasiswa mahasiswa) {
                            Intent edit = new Intent(MainActivity.this, EditMahasiswaActivity.class);
                            edit.putExtra("Mahasiswa", mahasiswa);
                            startActivityForResult(edit, REQUEST_EDIT);
                        }

                        @Override
                        public void deleteMahasiswa(Mahasiswa mahasiswa) {
                            actionDeleteMahasiswa(mahasiswa);
                        }

                        @Override
                        public void detailMahasiswa(Mahasiswa mahasiswa) {
                            Intent detail = new Intent(MainActivity.this, DetailMahasiswaActivity.class);
                            detail.putExtra("Mahasiswa", mahasiswa);
                            startActivity(detail);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GetMahasiswaResponse> call, Throwable t) {
                showMessage(t.getMessage());
            }
        });
    }

    private void actionDeleteMahasiswa(Mahasiswa mahasiswa) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Mahasiswa")
            .setIcon(R.drawable.ic_delete_black_24dp)
            .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    showLoading(true);
                    Call<ActionResponse> deleteMahasiswaResponseCall = RetrofitClient.getInstance().getAPI().deleteMahasiswa(mahasiswa.getNrp());
                    deleteMahasiswaResponseCall.enqueue(new Callback<ActionResponse>() {
                        @Override
                        public void onResponse(Call<ActionResponse> call, Response<ActionResponse> response) {
                            showLoading(false);
                            getMahasiswa();
                            showMessage(response.body().getMessage());
                        }

                        @Override
                        public void onFailure(Call<ActionResponse> call, Throwable t) {
                            showLoading(false);
                            showMessage(t.getMessage());
                        }
                    });
                }})
            .setNegativeButton("Cancel", null).show();
    }

    private void showLoading(Boolean state) {
        if (state)
            progressDialog.show();
        else
            progressDialog.dismiss();
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
