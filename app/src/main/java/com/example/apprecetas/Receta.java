package com.example.apprecetas;

public class Receta {

    private int id;
    private String nombre;
    private String ingredientes;
    private String pasos;
    private boolean favorito;
    private String imagen;

    public Receta() {}

    public Receta(int id, String nombre, String ingredientes, String pasos, boolean favorito) {
        this.id = id;
        this.nombre = nombre;
        this.ingredientes = ingredientes;
        this.pasos = pasos;
        this.favorito = favorito;
    }

    public Receta(String nombre, String ingredientes, String pasos) {
        this.nombre = nombre;
        this.ingredientes = ingredientes;
        this.pasos = pasos;
        this.favorito = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIngredientes() { return ingredientes; }
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }

    public String getPasos() { return pasos; }
    public void setPasos(String pasos) { this.pasos = pasos; }

    public boolean isFavorito() { return favorito; }
    public void setFavorito(boolean favorito) { this.favorito = favorito; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}