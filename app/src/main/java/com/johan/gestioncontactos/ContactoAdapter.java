package com.johan.gestioncontactos;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ViewHolder> {

    public interface OnSeleccion {
        void alSeleccionar(Contacto contacto);
    }

    Context context;
    ArrayList<Contacto> lista;
    OnSeleccion listener;
    int seleccionado = -1;

    public ContactoAdapter(Context context, ArrayList<Contacto> lista, OnSeleccion listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
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

        // Foto circular (o ícono si no hay imagen válida)
        boolean conFoto = false;
        String img = contacto.getImagen();
        if (img != null && !img.isEmpty()) {
            try {
                holder.imgFoto.setPadding(0, 0, 0, 0);
                holder.imgFoto.setImageURI(Uri.parse(img));
                conFoto = holder.imgFoto.getDrawable() != null;
            } catch (Exception e) {
                conFoto = false;
            }
        }
        if (!conFoto) {
            int p = (int) (12 * context.getResources().getDisplayMetrics().density);
            holder.imgFoto.setPadding(p, p, p, p);
            holder.imgFoto.setImageResource(android.R.drawable.ic_menu_myplaces);
        }

        // Resaltar el contacto seleccionado
        holder.itemRaiz.setBackgroundColor(
                position == seleccionado ? Color.parseColor("#DCEBFA") : Color.TRANSPARENT);

        holder.itemView.setOnClickListener(v -> {
            int anterior = seleccionado;
            seleccionado = holder.getBindingAdapterPosition();
            if (anterior != -1) notifyItemChanged(anterior);
            notifyItemChanged(seleccionado);
            if (listener != null) listener.alSeleccionar(lista.get(seleccionado));
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View itemRaiz;
        ImageView imgFoto;
        TextView txtNombre, txtTelefono;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemRaiz = itemView.findViewById(R.id.itemRaiz);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtTelefono = itemView.findViewById(R.id.txtTelefono);
        }
    }
}