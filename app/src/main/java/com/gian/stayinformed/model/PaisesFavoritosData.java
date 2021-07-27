package com.gian.stayinformed.model;

public class PaisesFavoritosData {

    private String casosActivos;
    private String casosConfirmados;
    private String muertes;
    private String nombre;
    private String nuevasMuertes;
    private String nuevosCasos;
    private String flag;

    public PaisesFavoritosData(){

    }

    public PaisesFavoritosData(String casosActivos, String casosConfirmados, String muertes, String nombre, String nuevasMuertes, String nuevosCasos, String flag) {
        this.casosActivos = casosActivos;
        this.casosConfirmados = casosConfirmados;
        this.muertes = muertes;
        this.nombre = nombre;
        this.nuevasMuertes = nuevasMuertes;
        this.nuevosCasos = nuevosCasos;
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public String getCasosActivos() {
        return casosActivos;
    }

    public String getCasosConfirmados() {
        return casosConfirmados;
    }

    public String getMuertes() {
        return muertes;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNuevasMuertes() {
        return nuevasMuertes;
    }

    public String getNuevosCasos() {
        return nuevosCasos;
    }
}

