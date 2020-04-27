package com.praditya.appcrud;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.praditya.appcrud.api.RetrofitClient;
import com.praditya.appcrud.models.ActionResponse;
import com.praditya.appcrud.models.Mahasiswa;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMahasiswaActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.edt_nrp) EditText edtNrp;
    @BindView(R.id.edt_name) EditText edtName;
    @BindView(R.id.edt_address) EditText edtAddress;
    @BindView(R.id.img_mahasiswa) ImageView ivFotoMahasiswa;
    @BindView(R.id.btn_add) Button btnAdd;

    private static final int REQUEST_CHOOSE_IMAGE = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;

    private ProgressDialog progressDialog;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mahasiswa);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Mahasiswa");

        progressDialog = new ProgressDialog(AddMahasiswaActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading ...");

        ButterKnife.bind(this);
        ivFotoMahasiswa.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            currentPhotoPath = getRealPathFromURI(selectedImage);
            Glide.with(this).load(BitmapFactory.decodeFile(getRealPathFromURI(selectedImage))).into(ivFotoMahasiswa);
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            File capturedImage = new File(currentPhotoPath);
            Glide.with(this).load(capturedImage).into(ivFotoMahasiswa);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                inputValidation();
                break;
            case R.id.img_mahasiswa:
                dialogImage();
                break;
        }
    }

    private void dialogImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.imageOptions, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else if (which == 1) {
                        openGallery();
                    }
                });
        builder.create().show();
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_CHOOSE_IMAGE);
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            showMessage(ex.getMessage());
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,"com.praditya.appcrud.fileprovider", photoFile);
            camera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(camera, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void inputValidation() {
        String nrp = edtNrp.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        boolean valid = true;

        if (TextUtils.isEmpty(nrp)) {
            edtNrp.setError("NRP must be filled!");
            edtNrp.requestFocus();
            valid = false;
        }

        if (TextUtils.isEmpty(name)) {
            edtName.setError("Name must be filled!");
            edtName.requestFocus();
            valid = false;
        }

        if (TextUtils.isEmpty(address)) {
            edtAddress.setError("Address must be filled!");
            edtAddress.requestFocus();
            valid = false;
        }

        if (!TextUtils.isDigitsOnly(nrp)) {
            edtNrp.setError("NRP must be numbers only!");
            edtNrp.requestFocus();
            valid = false;
        }

        if (currentPhotoPath == null) {
            showMessage("Select image!");
            valid = false;
        }

        if (valid) {
            Mahasiswa mahasiswa = new Mahasiswa(nrp, name, address);
            storeMahasiswa(mahasiswa);
        }
    }

    private void storeMahasiswa(Mahasiswa mahasiswa) {
        showLoading(true);
        File file = new File(currentPhotoPath);
        RequestBody requestNrp = createPartFromString(mahasiswa.getNrp());
        RequestBody requestName = createPartFromString(mahasiswa.getNama());
        RequestBody requestAlamat = createPartFromString(mahasiswa.getAlamat());
        RequestBody requestPhoto = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part photo = MultipartBody.Part.createFormData("part", file.getName(), requestPhoto);

        Call<ActionResponse> addMahasiswaResponseCall = RetrofitClient.getInstance().getAPI().addMahasiswa(requestNrp, requestName, requestAlamat, photo);
        addMahasiswaResponseCall.enqueue(new Callback<ActionResponse>() {
            @Override
            public void onResponse(Call<ActionResponse> call, Response<ActionResponse> response) {
                showLoading(false);
                if (!response.body().getError()) {
                    setResult(RESULT_OK);
                    finish();
                }else {
                    showMessage(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ActionResponse> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
    }

    private RequestBody createPartFromString(String part) {
        return RequestBody.create(MediaType.parse("text/plain"), part);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".png", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
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
