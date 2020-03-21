package com.example.myapplication;

/**
 * Engineer: ield
 *
 * Class CalculadoraIMCChicas
 *  Calculates IMC in girls using lear approximation from the data provided
 *  by official sources
 *
 * Attributes:
 *  edad: array of the ages of the babys in years
 *  p50: 50th percentile of the data
 *  p97: 97th percentile of the data
 */

public class CalculadoraIMCChicas {
    //El percentil 97 son 1.88
    private double[] edad = {0, 0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18};
    private double[] p50 = {13.74, 16.61, 17.46, 17.81, 17.81, 17.12, 16.61, 16.38, 16.28, 16.22, 16.08, 15.98, 15.96, 15.96, 16.04, 16.18, 16.39, 16.64, 16.91, 17.21, 17.51, 17.8, 18.09, 18.37, 18.64, 18.91, 19.17, 19.45, 19.73, 20.04, 20.37, 20.72, 21.08, 21.42, 21.7, 21.89, 21.9, 21.63, 20.97};
    private double[] p97 = {15.97, 19.31, 20.3, 20.71, 20.71, 19.9, 19.31, 19.12, 19.12, 19.31, 19.64, 19.92, 20.13, 20.39, 20.76, 21.23, 21.75, 22.31, 22.87, 23.41, 23.92, 24.37, 24.76, 25.1, 25.38, 25.62, 25.83, 26.04, 26.25, 26.48, 26.75, 27.06, 27.41, 27.75, 28.05, 28.22, 28.17, 27.75, 26.75};

    private double percentil;

    public CalculadoraIMCChicas(double age, double peso, double longitud){
        double medida = peso/(Math.pow(longitud/100, 2));

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
