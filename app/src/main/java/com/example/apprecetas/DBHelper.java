package com.example.apprecetas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recetas.db";
    private static final int DATABASE_VERSION = 3; // subimos versión por nueva columna

    public static final String TABLE_RECETAS = "recetas";
    public static final String COL_ID = "id";
    public static final String COL_IMAGEN = "imagen";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_INGREDIENTES = "ingredientes";
    public static final String COL_PASOS = "pasos";
    public static final String COL_FAVORITO = "favorito";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_RECETAS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NOMBRE + " TEXT, "
                + COL_INGREDIENTES + " TEXT, "
                + COL_PASOS + " TEXT, "
                + COL_FAVORITO + " INTEGER DEFAULT 0, "
                + COL_IMAGEN + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECETAS);
        onCreate(db);
    }

    public boolean insertarReceta(String nombre, String ingredientes, String pasos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_INGREDIENTES, ingredientes);
        values.put(COL_PASOS, pasos);
        values.put(COL_FAVORITO, 0);
        values.put(COL_IMAGEN, "");
        long resultado = db.insert(TABLE_RECETAS, null, values);
        db.close();
        return resultado != -1;
    }

    public boolean insertarReceta(String nombre, String ingredientes, String pasos, String imagenUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_INGREDIENTES, ingredientes);
        values.put(COL_PASOS, pasos);
        values.put(COL_FAVORITO, 0);
        values.put(COL_IMAGEN, imagenUri);
        long resultado = db.insert(TABLE_RECETAS, null, values);
        db.close();
        return resultado != -1;
    }

    public List<Receta> obtenerRecetas() {
        List<Receta> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECETAS, null);
        if (cursor.moveToFirst()) {
            do {
                Receta receta = new Receta();
                receta.setId(cursor.getInt(0));
                receta.setNombre(cursor.getString(1));
                receta.setIngredientes(cursor.getString(2));
                receta.setPasos(cursor.getString(3));
                receta.setImagen(cursor.getString(5));
                receta.setFavorito(cursor.getInt(4) == 1);
                lista.add(receta);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // Buscar recetas por nombre
    public List<Receta> buscarRecetas(String query) {
        List<Receta> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECETAS + " WHERE " + COL_NOMBRE + " LIKE ?",
                new String[]{"%" + query + "%"}
        );
        if (cursor.moveToFirst()) {
            do {
                Receta receta = new Receta();
                receta.setId(cursor.getInt(0));
                receta.setNombre(cursor.getString(1));
                receta.setIngredientes(cursor.getString(2));
                receta.setPasos(cursor.getString(3));
                receta.setFavorito(cursor.getInt(4) == 1);
                lista.add(receta);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // Obtener solo favoritos
    public List<Receta> obtenerFavoritos() {
        List<Receta> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECETAS + " WHERE " + COL_FAVORITO + "=1", null
        );
        if (cursor.moveToFirst()) {
            do {
                Receta receta = new Receta();
                receta.setId(cursor.getInt(0));
                receta.setNombre(cursor.getString(1));
                receta.setIngredientes(cursor.getString(2));
                receta.setPasos(cursor.getString(3));
                receta.setFavorito(true);
                lista.add(receta);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // Alternar favorito
    public void toggleFavorito(int id, boolean esFavorito) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FAVORITO, esFavorito ? 1 : 0);
        db.update(TABLE_RECETAS, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void insertarDatosIniciales() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECETAS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        if (count == 0) {
            // En insertarDatosIniciales:
            insertarReceta("Tacos de pollo",
                    "Pollo, tortilla, cebolla, cilantro",
                    "1. Cocer pollo\n2. Calentar tortilla\n3. Servir",
                    "drawable:tacos"); // prefijo "drawable:"

            insertarReceta("Pasta Alfredo",
                    "Pasta, crema, queso, mantequilla",
                    "1. Cocer pasta\n2. Preparar salsa\n3. Mezclar",
                    "drawable:pasta");

            insertarReceta("Huevos revueltos",
                    "Huevos, sal, mantequilla",
                    "1. Batir huevos\n2. Cocinar en sartén\n3. Servir",
                    "drawable:huevos");
        }
    }

    public boolean eliminarReceta(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete(TABLE_RECETAS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado > 0;
    }

    public boolean actualizarReceta(int id, String nombre, String ingredientes, String pasos, String imagenUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_INGREDIENTES, ingredientes);
        values.put(COL_PASOS, pasos);
        values.put(COL_IMAGEN, imagenUri);
        int resultado = db.update(TABLE_RECETAS, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado > 0;
    }
}