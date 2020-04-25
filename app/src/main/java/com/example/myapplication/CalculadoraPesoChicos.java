package com.example.myapplication;

/**
 * Created by Nacho on 24/12/2018.
 */

public class CalculadoraPesoChicos {

    //El percentil 97 son 1.88
    private double[] edad = {0, 0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18};
    private double[] p50 = {3.47, 6.26, 8.02, 9.24, 10.15, 11.45, 12.7, 13.84, 14.84, 15.92, 16.9, 17.95, 19.06, 20.24, 21.4, 22.6, 23.26, 24.39, 25.64, 27.04, 28.6, 30.32, 32.22, 34.28, 36.51, 38.88, 41.38, 43.99, 46.68, 49.41, 52.15, 54.86, 57.49, 59.98, 62.27, 64.31, 66.03, 67.35, 68.19};
    private double[] p97 = {4.41, 7.78, 9.9, 11.43, 12.51, 13.92, 15.55, 16.7, 18.25, 19.95, 20.79, 22.07, 23.44, 25.36, 27.78, 30.07, 31.5, 33.63, 35.64, 37.83, 40.2, 41.76, 45.5, 48.42, 51.51, 54.73, 58.07, 61.5, 64.98, 68.46, 71.91, 75.26, 78.45, 81.43, 84.11, 86.43, 88.31, 89.65, 90.36};

    private double percentil;

    public CalculadoraPesoChicos(double age, double medida){
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
