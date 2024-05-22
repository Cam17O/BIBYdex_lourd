package com.example.bibydex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SeConnecter extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_se_connecter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialisation des champs de texte
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
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

    public void login(View view) {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Vérifier si les champs sont vides
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Appel de la méthode d'authentification
        authenticate(username, password);
    }

    private void authenticate(String username, String password) {
        OkHttpClient client = new OkHttpClient();

        // Construction du corps de la requête
        RequestBody formBody = new FormBody.Builder()
                .add("Name", username)
                .add("password", password)
                .build();

        // Construction de la requête
        Request request = new Request.Builder()
                .url("http://172.16.195.254:3000/login")
                .post(formBody)
                .build();

        // Exécution de la requête de manière asynchrone
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SeConnecter.this, "Échec de la connexion", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Code inattendu : " + response);
                }

                // Lire la réponse du serveur
                String responseData = response.body().string();

                // Gérer la réponse du serveur
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    int userId = jsonObject.getInt("id_utilisateur");
                    // Connexion réussie, récupérer l'ID de l'utilisateur
                    saveUserId(userId);
                    // Rediriger vers une autre activité
                    Intent intent = new Intent(SeConnecter.this, Photo.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(SeConnecter.this, "Erreur lors de l'authentification", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void saveUserId(int userId) {
        // Stocker l'ID de l'utilisateur dans les préférences partagées ou toute autre méthode de stockage appropriée
        // Par exemple, vous pouvez utiliser les SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.apply();
    }
}