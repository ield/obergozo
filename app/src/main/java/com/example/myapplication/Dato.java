package com.example.myapplication;

import java.io.Serializable;

public class Dato implements Serializable {
    private double years;
    private double[] measures = new double[4];

    public double getYears() {
        return this.years;
    }

    public double[] getMeasures() {
        return this.measures;
    }

    public void setYears(double years) {
        this.years = years;
    }

    public void setMeasures(double[] measures) {
        for(int i = 0; i<measures.length; i++) {
            this.measures[i] = measures[i];
        }
    }
    public void setIMC(double IMC){
        this.measures[3] = IMC;
    }

}
