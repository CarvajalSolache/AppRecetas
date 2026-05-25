package com.example.apprecetas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CreditosActivity extends AppCompatActivity {
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos);

        btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(CreditosActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // evita que se quede en pila
        });
    }
}