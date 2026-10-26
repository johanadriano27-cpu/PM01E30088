package com.johan.gestioncontactos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageView imgContacto;
    Spinner spPais;
    EditText etNombre;
    EditText etTelefono;
    EditText etNota;

    Button btnGuardar;
    Button btnVer;
    Button btnImagen;

    DBHelper dbHelper;
    Uri imagenUri;

    // Variables para el modo EDITAR
    int idEditar = -1;
    String imagenActual = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgContacto = findViewById(R.id.imgContacto);
        spPais = findViewById(R.id.spPais);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etNota = findViewById(R.id.etNota);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVer = findViewById(R.id.btnVer);
        btnImagen = findViewById(R.id.btnImagen);

        btnImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        dbHelper = new DBHelper(this);

        String[] paises = {
                "Honduras (504)",
                "Guatemala (502)",
                "El Salvador (503)",
                "Costa Rica (506)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                paises);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spPais.setAdapter(adapter);

        // ===== MODO EDITAR: si viene un id, llenamos los campos =====
        Intent i = getIntent();
        if (i.hasExtra("id")) {

            idEditar = i.getIntExtra("id", -1);

            etNombre.setText(i.getStringExtra("nombre"));
            etTelefono.setText(i.getStringExtra("telefono"));
            etNota.setText(i.getStringExtra("nota"));

            String paisGuardado = i.getStringExtra("pais");
            for (int p = 0; p < paises.length; p++) {
                if (paises[p].equals(paisGuardado)) {
                    spPais.setSelection(p);
                    break;
                }
            }

            String img = i.getStringExtra("imagen");
            if (img != null && !img.isEmpty()) {
                imagenActual = img;
                try {
                    imgContacto.setImageURI(Uri.parse(img));
                } catch (Exception e) {
                    // si ya no hay permiso para ver la imagen, se ignora
                }
            }

            btnGuardar.setText("Actualizar");
        }

        btnGuardar.setOnClickListener(v -> {

            String pais = spPais.getSelectedItem().toString();
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String nota = etNota.getText().toString().trim();

            if (nombre.isEmpty()) {
                etNombre.setError("Debe escribir un nombre");
                return;
            }

            if (telefono.isEmpty()) {
                etTelefono.setError("Debe escribir un telefono");
                return;
            }

            if (nota.isEmpty()) {
                etNota.setError("Debe escribir una nota");
                return;
            }

            if (!nombre.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+$")) {
                etNombre.setError("Solo letras");
                return;
            }

            if (!telefono.matches("^[0-9]+$")) {
                etTelefono.setError("Solo numeros");
                return;
            }

            Contacto contacto = new Contacto();
            contacto.setPais(pais);
            contacto.setNombre(nombre);
            contacto.setTelefono(telefono);
            contacto.setNota(nota);

            // Imagen: la nueva si eligió una, si no la que ya tenía
            if (imagenUri != null) {
                contacto.setImagen(imagenUri.toString());
            } else {
                contacto.setImagen(imagenActual);
            }

            // ===== ACTUALIZAR =====
            if (idEditar != -1) {

                contacto.setId(idEditar);

                boolean actualizado = dbHelper.actualizarContacto(contacto);

                if (actualizado) {
                    Toast.makeText(
                            MainActivity.this,
                            "Contacto actualizado",
                            Toast.LENGTH_SHORT).show();
                    finish(); // vuelve a la lista
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Error al actualizar",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // ===== GUARDAR NUEVO =====
            boolean guardado = dbHelper.insertarContacto(contacto);

            if (guardado) {

                Toast.makeText(
                        MainActivity.this,
                        "Contacto guardado",
                        Toast.LENGTH_SHORT).show();

                etNombre.setText("");
                etTelefono.setText("");
                etNota.setText("");

            } else {

                Toast.makeText(
                        MainActivity.this,
                        "Error al guardar",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnVer.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ListaContactosActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100
                && resultCode == RESULT_OK
                && data != null) {

            imagenUri = data.getData();
            imgContacto.setImageURI(imagenUri);
        }
    }
}