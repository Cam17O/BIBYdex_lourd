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

    // Déclaration des champs de texte pour le nom d'utilisateur et le mot de passe
    private EditText editTextUsername;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_se_connecter);

        // Configuration de la barre d'outils
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialisation des champs de texte
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infler le menu; ceci ajoute les éléments à la barre d'action si elle est présente.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gérer les clics sur les éléments de la barre d'action
        int id = item.getItemId();

        // Si l'utilisateur clique sur "connexion"
        if (id == R.id.connexion) {
            Intent intent1 = new Intent(this, SeConnecter.class);
            this.startActivity(intent1);
            return true;
        }

        // Si l'utilisateur clique sur "photo"
        if (id == R.id.photo) {
            Intent intent1 = new Intent(this, Photo.class);
            this.startActivity(intent1);
            return true;
        }

        // Si l'utilisateur clique sur "galerie"
        if (id == R.id.galerie) {
            Intent intent1 = new Intent(this, Galerie.class);
            this.startActivity(intent1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Méthode pour gérer l'événement de clic sur le bouton de connexion
    public void login(View view) {
        // Récupérer les valeurs des champs de texte
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Vérifier si les champs sont remplis
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Appeler la méthode d'authentification
        authenticate(username, password);
    }

    // Méthode pour authentifier l'utilisateur
    private void authenticate(String username, String password) {
        OkHttpClient client = new OkHttpClient();

        // Construire le corps de la requête avec le nom d'utilisateur et le mot de passe
        RequestBody formBody = new FormBody.Builder()
                .add("Name", username)
                .add("password", password)
                .build();

        // Construire la requête HTTP
        Request request = new Request.Builder()
                .url("http://172.16.195.254:3000/login")
                .post(formBody)
                .build();

        // Exécuter la requête de manière asynchrone
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // En cas d'échec de la requête
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SeConnecter.this, "Échec de la connexion", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // En cas de réponse inattendue
                    throw new IOException("Code inattendu : " + response);
                }

                // Lire la réponse du serveur
                String responseData = response.body().string();

                try {
                    // Analyser la réponse JSON
                    JSONObject jsonObject = new JSONObject(responseData);
                    int userId = jsonObject.getInt("id_utilisateur");
                    saveUserId(userId); // Sauvegarder l'ID de l'utilisateur
                    Intent intent = new Intent(SeConnecter.this, Photo.class);
                    startActivity(intent);
                    finish(); // Fermer l'activité actuelle
                } catch (JSONException e) {
                    // En cas d'erreur lors de l'analyse JSON
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(SeConnecter.this, "Erreur lors de l'authentification", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Méthode pour sauvegarder l'ID de l'utilisateur dans les préférences partagées
    private void saveUserId(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.apply();
    }
}
