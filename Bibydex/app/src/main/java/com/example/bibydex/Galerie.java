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

    private RecyclerView recyclerViewPhotos;
    private PhotoAdapter photoAdapter;
    private static final String TAG = "Galerie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galerie);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerViewPhotos = findViewById(R.id.recyclerViewPhotos);
        recyclerViewPhotos.setLayoutManager(new LinearLayoutManager(this));

        // Récupérer l'ID de l'utilisateur connecté depuis SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int idUtilisateur = sharedPreferences.getInt("user_id", -1);

        if (idUtilisateur == -1) {
            // ID utilisateur non trouvé, gestion de l'erreur
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://172.16.195.254:3000/utilisateurs/" + idUtilisateur + "/photos";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Photos received: " + response.toString());
                        photoAdapter = new PhotoAdapter(Galerie.this, response);
                        recyclerViewPhotos.setAdapter(photoAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error loading photos", error);
                        Toast.makeText(Galerie.this, "Erreur de chargement des photos", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Ajout de la requête à la file d'attente Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
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
}
