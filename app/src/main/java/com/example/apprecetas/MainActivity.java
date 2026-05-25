package com.example.apprecetas;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnEntrar, btnCreditos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEntrar = findViewById(R.id.btnEntrar);
        btnCreditos = findViewById(R.id.btnCreditos);

        btnEntrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecetasActivity.class);
            startActivity(intent);
        });

        btnCreditos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreditosActivity.class);
            startActivity(intent);
        });
    }
}