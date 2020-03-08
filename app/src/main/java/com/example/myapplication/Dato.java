package com.example.myapplication;

import java.io.Serializable;

public class Dato implements Serializable {
    private double anos;
    private double[] medidas;

    public double getAnos() {
        return this.anos;
    }

    public double[] getMedidas() {
        return this.medidas;
    }

    public void setAnos(double anos) {
        this.anos = anos;
    }

    public void setMedidas(double[] medidas) {
        this.medidas = medidas;
    }

}
