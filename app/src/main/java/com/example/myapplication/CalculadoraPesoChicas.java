package com.example.myapplication;

/**
 * Created by Nacho on 24/12/2018.
 */

public class CalculadoraPesoChicas {

    //El percentil 97 son 1.88
    private double[] edad = {0, 0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18};
    private double[] p50 = {3.34, 5.79, 7.44, 8.63, 9.6, 10.94, 12.15, 13.3, 14.1, 14.89, 15.59, 16.48, 17.55, 18.77, 20.14, 21.65, 23.27, 24.99, 26.8, 28.68, 30.62, 62.60, 34.61, 36.63, 38.65, 40.66, 42.63, 44.56, 46.43, 48.22, 49.92, 51.52, 53, 54.34, 55.54, 56.57, 57.43, 58.09, 58.55};
    private double[] p97 = {4.34, 7.08, 9.18, 10.83, 11.92, 13.58, 15.09, 17.89, 19.52, 20.81, 22.31, 24.00, 25.85, 27.87, 30.02, 32.29, 34.67, 37.14, 39.67, 42.26, 44.88, 47.51, 50.15, 52.77, 55.35, 57.87, 60.33, 62.69, 64.95, 67.09, 69.09, 70.92, 72.59, 74.05, 75.31, 76.34, 77.13, 77.65, 77.89};

    private double percentil;

    public CalculadoraPesoChicas(double age, double medida){
        int intervalo = intervalo(age);
        double fraccion = fraccion(intervalo, age);
        double p50 = getP50(intervalo, fraccion);
        double p97 = getP97(intervalo, fraccion);
        double sd = (p97-p50)/1.88;
        double z = (medida-p50)/sd;
        percentil = CNDF(z);
    }

    public double getPercentil(){return this.percentil;}

    /*
    Este metodo devuelve la posicion en el array edad en donde se encuentra la edad.
    todo No tiene sentido que se repita en todas las clase, habria que acabar con esto.
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
