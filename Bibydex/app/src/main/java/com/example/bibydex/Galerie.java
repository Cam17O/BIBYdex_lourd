package com.example.bibydex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class Galerie extends AppCompatActivity {

    // RecyclerView pour afficher les photos
    private RecyclerView recyclerViewPhotos;
    // Adaptateur pour la RecyclerView
    private PhotoAdapter photoAdapter;
    // Tag pour le logging
    private static final String TAG = "Galerie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galerie);

        // Configuration de la barre d'outils
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialisation de la RecyclerView et définition de son layout
        recyclerViewPhotos = findViewById(R.id.recyclerViewPhotos);
        recyclerViewPhotos.setLayoutManager(new LinearLayoutManager(this));

        // Récupération de l'ID de l'utilisateur connecté à partir des SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int idUtilisateur = sharedPreferences.getInt("userId", -1);

        // Si l'utilisateur n'est pas connecté, afficher un message et retourner
        if (idUtilisateur == -1) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        // URL pour récupérer les photos de l'utilisateur
        String url = "http://172.16.195.254:3000/utilisateurs/" + idUtilisateur + "/photos";

        // Création d'une requête pour récupérer les photos de l'utilisateur
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // En cas de succès, loguer la réponse et définir l'adaptateur de la RecyclerView
                        Log.d(TAG, "Photos received: " + response.toString());
                        photoAdapter = new PhotoAdapter(Galerie.this, response);
                        recyclerViewPhotos.setAdapter(photoAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // En cas d'erreur, loguer l'erreur et afficher un message
                        Log.e(TAG, "Error loading photos", error);
                        Toast.makeText(Galerie.this, "Erreur de chargement des photos", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Ajout de la requête à la file d'attente des requêtes
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
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
}