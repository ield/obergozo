package com.example.myapplication;


import java.util.ArrayList;
import java.util.List;

public class Bebe {
    private String nombre;
    private int[] nacimiento;
    private List<Dato> datos;

    public Bebe(String nombre, int[] nacimiento){
        this.nombre = nombre;
        this.nacimiento = nacimiento;
        this.datos = new ArrayList<Dato>();

    }

    public void addDato(Dato d){
        datos.add(d);
    }
}
