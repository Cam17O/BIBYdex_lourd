package com.example.bibydex;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("/upload") // Assurez-vous que l'URL correspond à votre endpoint d'upload
    Call<ResponseBody> uploadPhoto(
            @Part("id_utilisateur") RequestBody id_utilisateur,
            @Part("id_galerie") RequestBody id_galerie,
            @Part MultipartBody.Part photo
    );
}