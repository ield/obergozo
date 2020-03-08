package com.example.myapplication;

/**
 * Created by Nacho on 24/12/2018.
 */

public class CalculadoraPerimetroCranealChicas {
    //El percentil 97 son 1.88
    private double[] edad = {0, 0.25, 0.5, 0.75, 1, 1.5, 2};
    private double[] p50 = {34.18, 40.1, 42.83, 44.69, 45.98, 47.31, 48.25};
    private double[] p97 = {36.58, 42.07, 44.88, 46.89, 48.2, 49.57, 50.53};
    private double percentil;

    public CalculadoraPerimetroCranealChicas(double age, double medida){
        int intervalo = intervalo(age);
        double fraccion = fraccion(intervalo, age);
        double p50 = getP50(intervalo, fraccion);
        double p97 = getP97(intervalo, fraccion);
        double sd = (p97-p50)/1.88;
        double z = (medida-p50)/sd;
        percentil = (CNDF(z));
    }

    public double getPercentil(){return this.percentil;}

    /*
    Este metodo devuelve la posicion en el array edad en donde se encuentra la edad
     */
    private int intervalo(double age){
        int i;
        for(i = 0; i<edad.length; i++){
            if(age<edad[i]) return (i-1);

        }
        return i;
    }

    /*
    Este metodo devuelve la fraccion en donde esta nuestra edad.
    Es decir, (age-liminferior)/(limsup-liminf)
    Este dato es util porque, al ser lineal, lo usaremos despues
    para calcular el p50 y p97 proporcionales
     */
    private double fraccion(int i, double age){
        return (age-edad[i])/(edad[i+1]-edad[i]);
    }

    /*
    Calcula el p50 proporcional a la fraccion fr a partir de
    la medida que esta en la posicion i
     */
    private double getP50(int i, double fr){
        return fr*(p50[i+1]-p50[i])+p50[i];
    }

    /*
    Calcula el p97 proporcional a la fraccion fr a partir de
    la medida que esta en la posicion i
     */
    private double getP97(int i, double fr){
        return fr*(p97[i+1]-p97[i])+p97[i];
    }


    /*
    Devuelve el valor de la tabla de distribucion de probabilidad normal
    Tengo que meter z = (x-media)/ds
     */
    private double CNDF(double x){
        int neg = (x < 0d) ? 1 : 0;
        if (neg == 1)
            x *= -1d;

        double k = (1d/(1d + 0.2316419 * x));
        double y = ((((1.330274429 * k - 1.821255978) * k + 1.781477937) *
                k - 0.356563782) * k + 0.319381530) * k;
        y = 1.0 - 0.398942280401 * Math.exp(-0.5 * x * x) * y;

        return (1d - neg) * y + neg * (1d - y);
    }
}
