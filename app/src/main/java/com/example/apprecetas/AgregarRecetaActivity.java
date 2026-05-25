package com.example.apprecetas;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;

public class AgregarRecetaActivity extends AppCompatActivity {

    private EditText etNombre, etIngredientes, etPasos;
    private Button btnGuardar, btnImagen;
    private ImageView ivPreview;
    private DBHelper dbHelper;
    private String imagenUri = "";
    private Uri fotoUri;

    private final ActivityResultLauncher<Intent> galeriaLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    imagenUri = copiarImagenAStorage(uri);
                    ivPreview.setImageBitmap(BitmapFactory.decodeFile(imagenUri));
                    ivPreview.setVisibility(View.VISIBLE);
                }
            });

    private final ActivityResultLauncher<Intent> camaraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    imagenUri = copiarImagenAStorage(fotoUri);
                    ivPreview.setImageBitmap(BitmapFactory.decodeFile(imagenUri));
                    ivPreview.setVisibility(View.VISIBLE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_receta);

        etNombre = findViewById(R.id.etNombre);
        etIngredientes = findViewById(R.id.etIngredientes);
        etPasos = findViewById(R.id.etPasos);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnImagen = findViewById(R.id.btnImagen);
        ivPreview = findViewById(R.id.ivPreview);

        dbHelper = new DBHelper(this);

        btnImagen.setOnClickListener(v -> {
            if (!imagenUri.isEmpty()) {
                Toast.makeText(this, "Quita la imagen actual antes de agregar una nueva", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle("Seleccionar imagen")
                    .setItems(new String[]{"Cámara", "Galería"}, (dialog, which) -> {
                        if (which == 0) verificarPermisoCamara();
                        else abrirGaleria();
                    })
                    .show();
        });

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String ingredientes = etIngredientes.getText().toString();
            String pasos = etPasos.getText().toString();

            if (nombre.isEmpty() || ingredientes.isEmpty() || pasos.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean insertado = dbHelper.insertarReceta(nombre, ingredientes, pasos, imagenUri);

            if (insertado) {
                Toast.makeText(this, "Receta guardada", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeriaLauncher.launch(intent);
    }

    private void verificarPermisoCamara() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
        } else {
            abrirCamara();
        }
    }

    private void abrirCamara() {
        try {
            File foto = File.createTempFile("foto_", ".jpg", getCacheDir());
            fotoUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", foto);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            camaraLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private String copiarImagenAStorage(Uri uri) {
        try {
            File destino = new File(getFilesDir(), "img_" + System.currentTimeMillis() + ".jpg");
            java.io.InputStream in = getContentResolver().openInputStream(uri);
            java.io.FileOutputStream out = new java.io.FileOutputStream(destino);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            in.close();
            out.close();
            return destino.getAbsolutePath();
        } catch (Exception e) {
            return uri.toString();
        }
    }
}