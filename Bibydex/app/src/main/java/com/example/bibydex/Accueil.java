package com.example.bibydex;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Accueil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            Intent intent1 = new Intent(this,Accueil.class);
            this.startActivity(intent1);
            return true;
        }

        if (id == R.id.photo) {
            Intent intent1 = new Intent(this,Photo.class);
            this.startActivity(intent1);
            return true;
        }

        if (id == R.id.galerie) {
            Intent intent1 = new Intent(this,Galerie.class);
            this.startActivity(intent1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}