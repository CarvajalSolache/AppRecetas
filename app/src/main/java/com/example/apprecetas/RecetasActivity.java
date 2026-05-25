package com.example.apprecetas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class RecetasActivity extends AppCompatActivity {

    private RecetaAdapter adapter;
    private DBHelper dbHelper;
    private EditText etBuscar;
    private Button btnAgregarReceta, btnVolver, btnFavoritos;
    private ListView lvRecetas;
    private boolean mostrandoFavoritos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recetas);

        dbHelper = new DBHelper(this);
        dbHelper.insertarDatosIniciales();

        etBuscar = findViewById(R.id.etBuscar);
        btnAgregarReceta = findViewById(R.id.btnAgregarReceta);
        btnVolver = findViewById(R.id.btnVolver);
        btnFavoritos = findViewById(R.id.btnFavoritos);
        lvRecetas = findViewById(R.id.lvRecetas);

        cargarRecetas();

        lvRecetas.setOnItemClickListener((parent, view, position, id) -> {
            Receta receta = adapter.getItem(position);
            Intent intent = new Intent(RecetasActivity.this, DetalleRecetaActivity.class);
            intent.putExtra("nombre", receta.getNombre());
            intent.putExtra("ingredientes", receta.getIngredientes());
            intent.putExtra("pasos", receta.getPasos());
            intent.putExtra("imagen", receta.getImagen()); // solo agrega esta línea
            startActivity(intent);
        });

        // Buscador en tiempo real
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    cargarRecetas();
                } else {
                    List<Receta> resultados = dbHelper.buscarRecetas(query);
                    adapter = new RecetaAdapter(RecetasActivity.this, resultados);
                    lvRecetas.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filtro favoritos
        btnFavoritos.setOnClickListener(v -> {
            if (mostrandoFavoritos) {
                mostrandoFavoritos = false;
                btnFavoritos.setText("Favoritos");
                cargarRecetas();
            } else {
                mostrandoFavoritos = true;
                btnFavoritos.setText("Todas");
                List<Receta> favoritos = dbHelper.obtenerFavoritos();
                adapter = new RecetaAdapter(RecetasActivity.this, favoritos);
                lvRecetas.setAdapter(adapter);
            }
        });

        // Agregar receta
        btnAgregarReceta.setOnClickListener(v -> {
            Intent intent = new Intent(RecetasActivity.this, AgregarRecetaActivity.class);
            startActivity(intent);
        });

        // Volver
        btnVolver.setOnClickListener(v -> {
            startActivity(new Intent(RecetasActivity.this, MainActivity.class));
            finish();
        });
    }

    private void cargarRecetas() {
        List<Receta> lista = dbHelper.obtenerRecetas();
        adapter = new RecetaAdapter(this, lista);
        lvRecetas.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRecetas();
    }

}