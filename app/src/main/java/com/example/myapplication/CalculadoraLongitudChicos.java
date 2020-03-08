package com.example.myapplication;

/**
 * Created by Nacho on 23/12/2018.
 */

public class CalculadoraLongitudChicos {

    //El percentil 97 son 1.88
    private double[] edad = {0, 0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18};
    private double[] p50 = {50.06, 60.44, 66.81, 71.1, 75.08, 81.33, 86.68, 90.77, 94.62, 98.41, 102.11, 105.69, 109.11, 113.2, 115.4, 117.33, 120.4, 123.38, 126.18, 129.01, 131.71, 134.18, 136.53, 139.05, 141.53, 143.66, 146.23, 148.96, 152.15, 156.05, 160.92, 165.08, 168.21, 170.18, 171.4, 172.28, 173.23, 173.83, 174.1};
    private double[] p97 = {53.64, 64.4, 70.92, 75.68, 80.01, 86.57, 92.12, 98.62, 102.82, 107.07, 111.28, 115.41, 119.4, 124, 126.18, 129.21, 132.73, 136.13, 139.3, 142.47, 145.44, 148.16, 150.73, 153.43, 156.08, 158.36, 161.07, 163.93, 167.24, 171.25, 176.21, 180.45, 183.64, 185.61, 186.78, 187.53, 188.24, 188.46, 188.46};

    double percentil;

    public CalculadoraLongitudChicos(double age, double medida){
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
    Source: https://www.codeproject.com/Messages/2622967/Re-NORMSDIST-function.aspx
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
