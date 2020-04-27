package com.praditya.appcrud.api;

import com.praditya.appcrud.models.ActionResponse;
import com.praditya.appcrud.models.GetMahasiswaResponse;
import com.praditya.appcrud.models.Mahasiswa;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIRequest {
    @GET("mahasiswa")
    Call<GetMahasiswaResponse> getMahasiswa();

    @GET("mahasiswa/{nrp}")
    Call<Mahasiswa> showMahasiswa(
        @Path("nrp") String nrp
    );

    @DELETE("mahasiswa/{nrp}")
    Call<ActionResponse> deleteMahasiswa(
        @Path("nrp") String nrp
    );

    @Multipart
    @POST("mahasiswa")
    Call<ActionResponse> addMahasiswa(
        @Part("nrp") RequestBody nrp,
        @Part("nama") RequestBody nama,
        @Part("alamat") RequestBody alamat,
        @Part MultipartBody.Part part
    );

    @FormUrlEncoded
    @POST("mahasiswa/update")
    Call<ActionResponse> updateMahasiswa(
        @Field("nrp") String nrp,
        @Field("nama") String nama,
        @Field("alamat") String alamat
    );

    @Multipart
    @POST("mahasiswa/update")
    Call<ActionResponse> updateMahasiswaWithPhoto(
        @Part("nrp") RequestBody nrp,
        @Part("nama") RequestBody nama,
        @Part("alamat") RequestBody alamat,
        @Part MultipartBody.Part part
    );
}
