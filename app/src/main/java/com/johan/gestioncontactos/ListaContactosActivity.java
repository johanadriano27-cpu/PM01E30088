package com.johan.gestioncontactos;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListaContactosActivity extends AppCompatActivity {

    RecyclerView recyclerContactos;
    EditText etBuscar;
    TextView txtSelNombre, txtSelTelefono;
    Button btnActualizar, btnEliminarSel, btnCompartirSel, btnVerImagenSel, btnLlamarSel;

    DBHelper dbHelper;
    ArrayList<Contacto> todos = new ArrayList<>();
    Contacto seleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        recyclerContactos = findViewById(R.id.recyclerContactos);
        etBuscar = findViewById(R.id.etBuscar);
        txtSelNombre = findViewById(R.id.txtSelNombre);
        txtSelTelefono = findViewById(R.id.txtSelTelefono);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminarSel = findViewById(R.id.btnEliminarSel);
        btnCompartirSel = findViewById(R.id.btnCompartirSel);
        btnVerImagenSel = findViewById(R.id.btnVerImagenSel);
        btnLlamarSel = findViewById(R.id.btnLlamarSel);

        recyclerContactos.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DBHelper(this);

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) { }

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) { }

            @Override
            public void afterTextChanged(Editable s) {
                mostrarLista();
            }
        });

        // ACTUALIZAR
        btnActualizar.setOnClickListener(v -> {
            if (!hayContacto()) return;
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", seleccionado.getId());
            intent.putExtra("nombre", seleccionado.getNombre());
            intent.putExtra("telefono", seleccionado.getTelefono());
            intent.putExtra("pais", seleccionado.getPais());
            intent.putExtra("imagen", seleccionado.getImagen());
            intent.putExtra("nota", seleccionado.getNota());
            startActivity(intent);
        });

        // ELIMINAR
        btnEliminarSel.setOnClickListener(v -> {
            if (!hayContacto()) return;
            dbHelper.eliminarContacto(seleccionado.getId());
            Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
            todos = dbHelper.obtenerContactos();
            mostrarLista();
        });

        // COMPARTIR
        btnCompartirSel.setOnClickListener(v -> {
            if (!hayContacto()) return;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,
                    "Nombre: " + seleccionado.getNombre()
                            + "\nTelefono: " + seleccionado.getTelefono()
                            + "\nPais: " + seleccionado.getPais());
            startActivity(Intent.createChooser(intent, "Compartir contacto"));
        });

        // VER IMAGEN
        btnVerImagenSel.setOnClickListener(v -> {
            if (!hayContacto()) return;
            Intent intent = new Intent(this, VerImagenActivity.class);
            intent.putExtra("imagen", seleccionado.getImagen());
            startActivity(intent);
        });

        // LLAMAR (con confirmación)
        btnLlamarSel.setOnClickListener(v -> {
            if (!hayContacto()) return;
            final Contacto c = seleccionado;
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar llamada")
                    .setMessage("¿Desea llamar a " + c.getNombre() + "?\n" + c.getTelefono())
                    .setPositiveButton("Llamar", (dialog, which) -> {
                        if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.CALL_PHONE)
                                == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + c.getTelefono()));
                            startActivity(intent);
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.CALL_PHONE}, 100);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        todos = dbHelper.obtenerContactos();
        mostrarLista();
    }

    private boolean hayContacto() {
        if (seleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void mostrarLista() {
        String q = etBuscar.getText().toString().trim().toLowerCase();

        ArrayList<Contacto> filtrada = new ArrayList<>();
        for (Contacto c : todos) {
            if (q.isEmpty()
                    || c.getNombre().toLowerCase().contains(q)
                    || c.getTelefono().contains(q)) {
                filtrada.add(c);
            }
        }

        seleccionado = null;
        txtSelNombre.setText("Ninguno");
        txtSelTelefono.setText("");

        recyclerContactos.setAdapter(new ContactoAdapter(this, filtrada, c -> {
            seleccionado = c;
            txtSelNombre.setText(c.getNombre());
            txtSelTelefono.setText(c.getTelefono());
        }));
    }
}