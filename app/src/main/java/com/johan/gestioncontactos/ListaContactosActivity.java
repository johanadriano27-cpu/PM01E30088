package com.johan.gestioncontactos;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListaContactosActivity extends AppCompatActivity {

    RecyclerView recyclerContactos;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        recyclerContactos =
                findViewById(
                        R.id.recyclerContactos);

        recyclerContactos.setLayoutManager(
                new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<Contacto> lista =
                dbHelper.obtenerContactos();

        ContactoAdapter adapter =
                new ContactoAdapter(
                        this,
                        lista);

        recyclerContactos.setAdapter(adapter);
    }
}