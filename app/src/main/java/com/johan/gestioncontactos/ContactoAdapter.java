package com.johan.gestioncontactos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ViewHolder> {

    Context context;
    ArrayList<Contacto> lista;

    public ContactoAdapter(Context context, ArrayList<Contacto> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_contacto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Contacto contacto = lista.get(position);

        holder.txtNombre.setText(contacto.getNombre());
        holder.txtTelefono.setText(contacto.getTelefono());


        holder.btnVerImagen.setOnClickListener(v -> {
            Intent intent = new Intent(context, VerImagenActivity.class);
            intent.putExtra("imagen", contacto.getImagen());
            context.startActivity(intent);
        });


        holder.btnEliminar.setOnClickListener(v -> {
            DBHelper dbHelper = new DBHelper(context);
            dbHelper.eliminarContacto(contacto.getId());
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                lista.remove(pos);
                notifyItemRemoved(pos);
            }
        });


        holder.btnCompartir.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,
                    "Nombre: " + contacto.getNombre()
                            + "\nTelefono: " + contacto.getTelefono()
                            + "\nPais: " + contacto.getPais());
            context.startActivity(Intent.createChooser(intent, "Compartir contacto"));
        });


        holder.btnLlamar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmar llamada")
                    .setMessage("¿Desea llamar a " + contacto.getNombre() + "?")
                    .setPositiveButton("Llamar", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + contacto.getTelefono()));

                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.CALL_PHONE)
                                == PackageManager.PERMISSION_GRANTED) {
                            context.startActivity(intent);
                        } else {
                            ActivityCompat.requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    100);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });


        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class); // tu Activity del formulario
            intent.putExtra("id", contacto.getId());
            intent.putExtra("nombre", contacto.getNombre());
            intent.putExtra("telefono", contacto.getTelefono());
            intent.putExtra("pais", contacto.getPais());
            intent.putExtra("imagen", contacto.getImagen());
            intent.putExtra("nota", contacto.getNota());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtTelefono;
        Button btnVerImagen, btnEliminar, btnCompartir, btnLlamar, btnEditar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtTelefono = itemView.findViewById(R.id.txtTelefono);
            btnVerImagen = itemView.findViewById(R.id.btnVerImagen);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnCompartir = itemView.findViewById(R.id.btnCompartir);
            btnLlamar = itemView.findViewById(R.id.btnLlamar);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}