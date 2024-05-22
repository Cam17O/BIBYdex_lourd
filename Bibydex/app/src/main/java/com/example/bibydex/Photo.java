package com.example.bibydex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.Manifest;
import android.content.pm.PackageManager;
import com.example.bibydex.RetrofitInstance;


public class Photo extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_FROM_GALLERY = 2;
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 99;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        apiService = RetrofitInstance.getApiService();


        // Demande de permissions de stockage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.connexion) {
            Intent intent1 = new Intent(this, SeConnecter.class);
            this.startActivity(intent1);
            return true;
        }

        if (id == R.id.photo) {
            Intent intent1 = new Intent(this, Photo.class);
            this.startActivity(intent1);
            return true;
        }

        if (id == R.id.galerie) {
            Intent intent1 = new Intent(this, Galerie.class);
            this.startActivity(intent1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void takePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Aucune application pour prendre des photos n'est disponible", Toast.LENGTH_SHORT).show();
        }
    }

    public void chooseFromGallery(View view) {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhotoIntent.setType("image/*");
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            File photoFile = bitmapToFile(this, imageBitmap); // Convertir le Bitmap en File
            uploadPhoto(photoFile); // Appeler uploadPhoto avec le File et les IDs appropriés
        }
        else if (requestCode == REQUEST_IMAGE_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            // Convertir l'URI en Bitmap si nécessaire et appeler uploadPhoto(imageBitmap);

            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                File photoFile = bitmapToFile(this, imageBitmap); // Convertir le Bitmap en File
                uploadPhoto(photoFile); // Appeler uploadPhoto avec le File et les IDs appropriés
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadPhoto(File photoFile) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int loggedInUserId = sharedPreferences.getInt("userId", -1); // -1 est une valeur par défaut si l'ID de l'utilisateur n'est pas trouvé

        int galleryId = 1;

        MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

        OkHttpClient client = new OkHttpClient();

        // Construction de la requête
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id_utilisateur", String.valueOf(loggedInUserId))
                .addFormDataPart("id_galerie", String.valueOf(galleryId))
                .addFormDataPart("photo", "photo.jpg", RequestBody.create(photoFile, MEDIA_TYPE_JPEG))
                .build();

        Request request = new Request.Builder()
                .url("http://172.16.195.254:3000/upload")
                .post(requestBody)
                .build();

        // Exécution de la requête de manière asynchrone
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Gérer les échecs de connexion
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Lire la réponse si nécessaire
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String responseBodyString = responseBody.string();
                    // Gérer la réponse de l'API
                }
            }
        });
    }


    public File bitmapToFile(Context context, Bitmap bitmap) {
        File filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(filesDir, generateFileName());
        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "IMG_" + timeStamp + ".jpg";
    }
}