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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
                R.layout.spinner_item,
                paises);

        adapter.setDropDownViewResource(R.layout.spinner_item);

        spPais.setAdapter(adapter);


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
                    mostrarFoto(Uri.parse(img));
                } catch (Exception e) {
                    // si no se puede leer la imagen, se ignora
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


            if (imagenUri != null) {
                contacto.setImagen(imagenUri.toString());
            } else {
                contacto.setImagen(imagenActual);
            }


            if (idEditar != -1) {

                contacto.setId(idEditar);

                boolean actualizado = dbHelper.actualizarContacto(contacto);

                if (actualizado) {
                    Toast.makeText(
                            MainActivity.this,
                            "Contacto actualizado",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Error al actualizar",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }


            boolean guardado = dbHelper.insertarContacto(contacto);

            if (guardado) {

                Toast.makeText(
                        MainActivity.this,
                        "Contacto guardado",
                        Toast.LENGTH_SHORT).show();

                etNombre.setText("");
                etTelefono.setText("");
                etNota.setText("");


                imagenUri = null;
                int p = (int) (30 * getResources().getDisplayMetrics().density);
                imgContacto.setPadding(p, p, p, p);
                imgContacto.setImageResource(android.R.drawable.ic_menu_camera);

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


    private void mostrarFoto(Uri uri) {
        imgContacto.setPadding(0, 0, 0, 0);
        imgContacto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgContacto.setImageURI(uri);
    }


    private String copiarImagen(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            File archivo = new File(getFilesDir(),
                    "img_" + System.currentTimeMillis() + ".jpg");
            OutputStream out = new FileOutputStream(archivo);

            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();

            return Uri.fromFile(archivo).toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            String ruta = copiarImagen(data.getData());

            if (ruta != null) {
                imagenUri = Uri.parse(ruta);
                mostrarFoto(imagenUri);
            } else {
                Toast.makeText(this, "No se pudo cargar la imagen",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}