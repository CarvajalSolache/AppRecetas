package com.example.apprecetas;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

public class DetalleRecetaActivity extends AppCompatActivity {

    private TextView tvNombre, tvIngredientes, tvPasos;
    private TextView tvTemporizador;
    private Button btnIniciarTimer, btnPausarTimer, btnCancelarTimer;
    private Button btnWhatsApp, btnVolver;
    private String ingredientesBase = "";
    private String nombre = "";
    private String pasos = "";
    private CountDownTimer countDownTimer;
    private long tiempoRestante = 30 * 60 * 1000; // 30 minutos por defecto
    private boolean timerActivo = false;
    private boolean timerPausado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_receta);

        // Referencias
        tvNombre = findViewById(R.id.tvNombre);
        tvIngredientes = findViewById(R.id.tvIngredientes);
        tvPasos = findViewById(R.id.tvPasos);
        tvTemporizador = findViewById(R.id.tvTemporizador);
        btnIniciarTimer = findViewById(R.id.btnIniciarTimer);
        btnPausarTimer = findViewById(R.id.btnPausarTimer);
        btnCancelarTimer = findViewById(R.id.btnCancelarTimer);
        btnWhatsApp = findViewById(R.id.btnWhatsApp);
        btnVolver = findViewById(R.id.btnVolver);

        // Recibir datos
        nombre = getIntent().getStringExtra("nombre");
        ingredientesBase = getIntent().getStringExtra("ingredientes");
        pasos = getIntent().getStringExtra("pasos");

        tvNombre.setText(nombre);
        tvIngredientes.setText(ingredientesBase);
        tvPasos.setText(pasos);
        // Mostrar imagen si existe
        ImageView ivImagen = findViewById(R.id.ivImagenDetalle);
        String imagenUri = getIntent().getStringExtra("imagen");
        if (imagenUri != null && !imagenUri.isEmpty()) {
            ivImagen.setVisibility(android.view.View.VISIBLE);
            if (imagenUri.startsWith("drawable:")) {
                String nombre = imagenUri.replace("drawable:", "");
                int resId = getResources().getIdentifier(nombre, "drawable", getPackageName());
                ivImagen.setImageResource(resId);
            } else {
                ivImagen.setImageURI(android.net.Uri.parse(imagenUri));
            }
        }
        tvTemporizador.setText("30:00");

        // Temporizador
        // Un EditText donde el usuario escribe los minutos
// Un EditText donde el usuario escribe los minutos
        EditText etMinutos = findViewById(R.id.etMinutos);
        EditText etSegundos = findViewById(R.id.etSegundos);

        btnIniciarTimer.setOnClickListener(v -> {
            if (!timerActivo) {
                String minStr = etMinutos.getText().toString();
                String segStr = etSegundos.getText().toString();
                long minutos = minStr.isEmpty() ? 0 : Long.parseLong(minStr);
                long segundos = segStr.isEmpty() ? 0 : Long.parseLong(segStr);
                tiempoRestante = (minutos * 60 + segundos) * 1000;
                if (tiempoRestante <= 0) tiempoRestante = 30 * 60 * 1000;
                iniciarTemporizador(tiempoRestante);
            }
        });

        btnPausarTimer.setOnClickListener(v -> {
            if (timerActivo && !timerPausado) {
                countDownTimer.cancel();
                timerActivo = false;
                timerPausado = true;
                btnPausarTimer.setText("Reanudar");
            } else if (timerPausado) {
                iniciarTemporizador(tiempoRestante);
                timerPausado = false;
                btnPausarTimer.setText("Pausar");
            }
        });

        btnCancelarTimer.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            timerActivo = false;
            timerPausado = false;
            tiempoRestante = 30 * 60 * 1000;
            tvTemporizador.setText("30:00");
            btnPausarTimer.setText("Pausar");
        });

        // WhatsApp
        btnWhatsApp.setOnClickListener(v -> compartirWhatsApp());

        // Volver
        btnVolver.setOnClickListener(v -> finish());
    }

    private void iniciarTemporizador(long milisegundos) {
        countDownTimer = new CountDownTimer(milisegundos, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestante = millisUntilFinished;
                long minutos = millisUntilFinished / 60000;
                long segundos = (millisUntilFinished % 60000) / 1000;
                tvTemporizador.setText(String.format("%02d:%02d", minutos, segundos));
            }

            @Override
            public void onFinish() {
                timerActivo = false;
                timerPausado = false;
                tiempoRestante = 30 * 60 * 1000;
                tvTemporizador.setText("¡Listo!");
                btnPausarTimer.setText("Pausar");

                // Vibrar al terminar
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(new long[]{0, 500, 200, 500, 200, 500}, -1);
                }
                Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                Ringtone ringtone = RingtoneManager.getRingtone(DetalleRecetaActivity.this, sonido);
                if (ringtone != null) {
                    ringtone.play();
                }

                Toast.makeText(DetalleRecetaActivity.this,
                        "⏰ ¡El tiempo terminó!", Toast.LENGTH_LONG).show();
            }
        }.start();
        timerActivo = true;
    }

    private void compartirWhatsApp() {
        String texto = "*" + nombre + "*\n\n"
                + "*Ingredientes:*\n" + ingredientesBase + "\n\n"
                + "*Pasos:*\n" + pasos;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_TEXT, texto);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}