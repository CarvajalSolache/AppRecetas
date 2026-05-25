package com.example.apprecetas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

public class RecetaAdapter extends ArrayAdapter<Receta> {

    static class ViewHolder {
        TextView tvNombre;
        TextView tvIngredientes;
        Button btnEditar;
        Button btnEliminar;
        Button btnFavorito;
        Button btnDetalle;
    }

    Activity context;
    List<Receta> lista;

    public RecetaAdapter(Activity context, List<Receta> lista) {
        super(context, R.layout.item_receta, lista);
        this.context = context;
        this.lista = lista;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_receta, parent, false);

            holder = new ViewHolder();
            holder.tvNombre = convertView.findViewById(R.id.tvNombreReceta);
            holder.tvIngredientes = convertView.findViewById(R.id.tvIngredientesReceta);
            holder.btnEditar = convertView.findViewById(R.id.btnEditar);
            holder.btnEliminar = convertView.findViewById(R.id.btnEliminar);
            holder.btnFavorito = convertView.findViewById(R.id.btnFavorito);
            holder.btnDetalle = convertView.findViewById(R.id.btnDetalle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Receta receta = lista.get(position);

        holder.tvNombre.setText(receta.getNombre());
        holder.tvIngredientes.setText(receta.getIngredientes());

        // Icono favorito según estado
        holder.btnFavorito.setText(receta.isFavorito() ? "⭐" : "☆");

        // EDITAR
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarRecetaActivity.class);
            intent.putExtra("id", receta.getId());
            intent.putExtra("nombre", receta.getNombre());
            intent.putExtra("ingredientes", receta.getIngredientes());
            intent.putExtra("pasos", receta.getPasos());
            intent.putExtra("imagen", receta.getImagen()); // agrega esta línea
            context.startActivity(intent);
        });

        holder.btnDetalle.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleRecetaActivity.class);
            intent.putExtra("nombre", receta.getNombre());
            intent.putExtra("ingredientes", receta.getIngredientes());
            intent.putExtra("pasos", receta.getPasos());
            intent.putExtra("imagen", receta.getImagen());
            context.startActivity(intent);
        });

        // ELIMINAR con AlertDialog
        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar receta")
                    .setMessage("¿Estás seguro de que deseas eliminar \"" + receta.getNombre() + "\"?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        DBHelper dbHelper = new DBHelper(context);
                        dbHelper.eliminarReceta(receta.getId());
                        lista.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // FAVORITO
        holder.btnFavorito.setOnClickListener(v -> {
            boolean nuevoEstado = !receta.isFavorito();
            receta.setFavorito(nuevoEstado);
            DBHelper dbHelper = new DBHelper(context);
            dbHelper.toggleFavorito(receta.getId(), nuevoEstado);
            notifyDataSetChanged();
        });

        return convertView;
    }
}