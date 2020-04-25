package com.example.myapplication;

/**
 * Created by Nacho on 24/12/2018.
 */

public class CalculadoraIMCChicos {
    //El percentil 97 son 1.88
    private double[] edad = {0, 0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18};
    private double[] p50 = {13.87, 17.12, 17.99, 18.36, 17.99, 17.46, 16.95, 16.61, 16.44, 16.28, 16.13, 16.05, 15.87, 15.82, 15.87, 16, 16.2, 16.44, 16.71, 17, 17.3, 17.59, 17.87, 18.13, 18.39, 18.64, 18.88, 19.14, 19.42, 19.72, 20.06, 20.43, 20.83, 21.23, 21.61, 21.91, 22.07, 21.99, 21.54};
    private double[] p97 = {16.13, 19.9, 20.92, 21.34, 20.92, 19.92, 19.33, 18.95, 18.76, 18.57, 18.49, 18.45, 18.76, 19.04, 19.29, 19.65, 20.11, 20.62, 21.18, 21.75, 22.32, 22.87, 23.39, 23.88, 24.34, 24.77, 25.2, 25.63, 26.07, 26.54, 27.04, 27.58, 28.13, 28.68, 29.17, 29.53, 29.65, 29.4, 28.59};

    private double percentil;
    private double medida;

    public CalculadoraIMCChicos(double age, double peso, double longitud){
        this.medida = peso/(Math.pow(longitud/100, 2));

        int intervalo = intervalo(age);
        double fraccion = fraccion(intervalo, age);
        double p50 = getP50(intervalo, fraccion);
        double p97 = getP97(intervalo, fraccion);
        double sd = (p97-p50)/1.88;
        double z = (medida-p50)/sd;
        percentil = (CNDF(z));
    }

    public double getMedida(){return this.medida;}

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
