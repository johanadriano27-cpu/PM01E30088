package com.johan.gestioncontactos;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class VerImagenActivity extends AppCompatActivity {

    ImageView imgGrande;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_imagen);

        imgGrande = findViewById(R.id.imgGrande);

        String uriImagen =
                getIntent().getStringExtra("imagen");

        if(uriImagen != null){

            imgGrande.setImageURI(
                    Uri.parse(uriImagen));
        }
    }
}