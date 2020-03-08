package com.example.myapplication;

/**
 * Created by Nacho on 24/12/2018.
 */

public class CalculadoraLongitudChicas {

    //El percentil 90 son 1.88
    private double[] edad = {0, 0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18};
    private double[] p50 = {49.34, 59.18, 65.33, 69.52, 73.55, 80.05, 85.4, 89.87, 93.93, 97.73, 101.33, 104.76, 108.07, 111.28, 114.41, 117.5, 120.54, 123.54, 126.52, 129.48, 132.4, 135.28, 138.11, 140.4, 142.98, 146.09, 149.03, 151.73, 154.14, 156.21, 157.88, 159.15, 160.01, 160.5, 160.68, 160.7, 160.72, 160.75, 160.78};
    private double[] p97 = {53.24, 62.93, 69.29, 73.84, 78.39, 85.22, 90.84, 97.85, 101.89, 105.96, 110.02, 114.04, 118.02, 121.93, 125.76, 129.49, 133.12, 136.65, 140.07, 143.37, 146.55, 149.62, 152.58, 155.29, 158.65, 161.88, 164.89, 167.6, 169.95, 171.89, 173.38, 174.42, 175.04, 175.31, 175.33, 175.33, 175.33, 175.33, 175.4};

    private double percentil;

    public CalculadoraLongitudChicas(double age, double medida){
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
