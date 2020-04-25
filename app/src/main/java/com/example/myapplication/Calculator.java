package com.example.myapplication;

public class Calculator {
    private double[] ageArray = {0, 0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18};

    private double[][][] allP50 = new double[2][4][ageArray.length];
    private double[][][] allP97 = new double[2][4][ageArray.length];

    private double[] measures;
    private double[] perc;//Los percentiles que se devuelven

    private int gen;
    private double age;

    private int interval;
    private double fraction;//The fraction of the year to approximate linearly


    public Calculator(double age, double[] measures, int gender){
        generateData();
        this.gen = gender;
        this.age = age;
        this.measures = measures;

        this.perc = new double[4];

        this.interval = interval(age);
        this.fraction = fraction(interval, age);

        if(age <= 2){
            for(int i = 0; i<perc.length; i++){
                perc[i] = calcPerc(i);
            }
        }else{
            for(int i = 0; i<perc.length; i++){
                if(i == 2) continue;
                perc[i] = calcPerc(i);
            }
        }



    }

    private double calcPerc(int mag){
        double measure;
        if(mag != 3) {
            measure = measures[mag];
        }else{
            measure = measures[1]/Math.pow(measures[0]/100, 2);
        }

        double p50 = getP50(interval, fraction, mag);
        double p97 = getP97(interval, fraction, mag);
        double sd = (p97-p50)/1.88;
        double z = (measure-p50)/sd;
        return (CNDF(z));

    }

    private void generateData(){
        generateP50boys();
        generateP50girls();
        generateP97boys();
        generateP97girls();
    }

    private void generateP50girls(){

        //Longitud chicas
        double[] p50_length = {49.34, 59.18, 65.33, 69.52, 73.55, 80.05, 85.4, 89.87, 93.93, 97.73, 101.33, 104.76, 108.07, 111.28, 114.41, 117.5, 120.54, 123.54, 126.52, 129.48, 132.4, 135.28, 138.11, 140.4, 142.98, 146.09, 149.03, 151.73, 154.14, 156.21, 157.88, 159.15, 160.01, 160.5, 160.68, 160.7, 160.72, 160.75, 160.78};
        allP50[1][0] = p50_length;
        //weigh chicas
        double[] p50_weigh = {3.34, 5.79, 7.44, 8.63, 9.6, 10.94, 12.15, 13.3, 14.1, 14.89, 15.59, 16.48, 17.55, 18.77, 20.14, 21.65, 23.27, 24.99, 26.8, 28.68, 30.62, 32.60, 34.61, 36.63, 38.65, 40.66, 42.63, 44.56, 46.43, 48.22, 49.92, 51.52, 53, 54.34, 55.54, 56.57, 57.43, 58.09, 58.55};
        allP50[1][1] = p50_weigh;
        //PerimetroCraneal chicas
        double[] p50_cranial = {34.18, 40.1, 42.83, 44.69, 45.98, 47.31, 48.25};
        allP50[1][2] = p50_cranial;
        //IMC Chicas
        double[] p50_IMC = {13.74, 16.61, 17.46, 17.81, 17.81, 17.12, 16.61, 16.38, 16.28, 16.22, 16.08, 15.98, 15.96, 15.96, 16.04, 16.18, 16.39, 16.64, 16.91, 17.21, 17.51, 17.8, 18.09, 18.37, 18.64, 18.91, 19.17, 19.45, 19.73, 20.04, 20.37, 20.72, 21.08, 21.42, 21.7, 21.89, 21.9, 21.63, 20.97};
        allP50[1][3] = p50_IMC;
    }

    private void generateP50boys(){

        //Longitud chicos
        double[] p50_longitud = {50.06, 60.44, 66.81, 71.1, 75.08, 81.33, 86.68, 90.77, 94.62, 98.41, 102.11, 105.69, 109.11, 113.2, 115.4, 117.33, 120.4, 123.38, 126.18, 129.01, 131.71, 134.18, 136.53, 139.05, 141.53, 143.66, 146.23, 148.96, 152.15, 156.05, 160.92, 165.08, 168.21, 170.18, 171.4, 172.28, 173.23, 173.83, 174.1};
        allP50[0][0] = p50_longitud;
        //weigh chicos
        double[] p50_weigh = {3.47, 6.26, 8.02, 9.24, 10.15, 11.45, 12.7, 13.84, 14.84, 15.92, 16.9, 17.95, 19.06, 20.24, 21.4, 22.6, 23.26, 24.39, 25.64, 27.04, 28.6, 30.32, 32.22, 34.28, 36.51, 38.88, 41.38, 43.99, 46.68, 49.41, 52.15, 54.86, 57.49, 59.98, 62.27, 64.31, 66.03, 67.35, 68.19};
        allP50[0][1] = p50_weigh;
        //PerimetroCraneal chicos
        double[] p50_cranial = {34.84, 41.2, 44.15, 46.02, 47.31, 48.7, 49.59};
        allP50[0][2] = p50_cranial;
        //IMC Chicos
        double[] p50_IMC = {13.87, 17.12, 17.99, 18.36, 17.99, 17.46, 16.95, 16.61, 16.44, 16.28, 16.13, 16.05, 15.87, 15.82, 15.87, 16, 16.2, 16.44, 16.71, 17, 17.3, 17.59, 17.87, 18.13, 18.39, 18.64, 18.88, 19.14, 19.42, 19.72, 20.06, 20.43, 20.83, 21.23, 21.61, 21.91, 22.07, 21.99, 21.54};
        allP50[0][3] = p50_IMC;
    }

    private void generateP97girls(){

        //Longitud chicas
        double[] p97_length = {53.24, 62.93, 69.29, 73.84, 78.39, 85.22, 90.84, 97.85, 101.89, 105.96, 110.02, 114.04, 118.02, 121.93, 125.76, 129.49, 133.12, 136.65, 140.07, 143.37, 146.55, 149.62, 152.58, 155.29, 158.65, 161.88, 164.89, 167.6, 169.95, 171.89, 173.38, 174.42, 175.04, 175.31, 175.33, 175.33, 175.33, 175.33, 175.4};
        allP97[1][0] = p97_length;
        //weigh chicas
        double[] p97_weigh = {4.34, 7.08, 9.18, 10.83, 11.92, 13.58, 15.09, 17.89, 19.52, 20.81, 22.31, 24.00, 25.85, 27.87, 30.02, 32.29, 34.67, 37.14, 39.67, 42.26, 44.88, 47.51, 50.15, 52.77, 55.35, 57.87, 60.33, 62.69, 64.95, 67.09, 69.09, 70.92, 72.59, 74.05, 75.31, 76.34, 77.13, 77.65, 77.89};
        allP97[1][1] = p97_weigh;
        //PerimetroCraneal chicas
        double[] p97_cranial = {36.58, 42.07, 44.88, 46.89, 48.2, 49.57, 50.53};
        allP97[1][2] = p97_cranial;
        //IMC Chicas
        double[] p97_IMC = {15.97, 19.31, 20.3, 20.71, 20.71, 19.9, 19.31, 19.12, 19.12, 19.31, 19.64, 19.92, 20.13, 20.39, 20.76, 21.23, 21.75, 22.31, 22.87, 23.41, 23.92, 24.37, 24.76, 25.1, 25.38, 25.62, 25.83, 26.04, 26.25, 26.48, 26.75, 27.06, 27.41, 27.75, 28.05, 28.22, 28.17, 27.75, 26.75};
        allP97[1][3] = p97_IMC;
    }

    private void generateP97boys(){

        //Longitud chicos
        double[] p97_length = {53.64, 64.4, 70.92, 75.68, 80.01, 86.57, 92.12, 98.62, 102.82, 107.07, 111.28, 115.41, 119.4, 124, 126.18, 129.21, 132.73, 136.13, 139.3, 142.47, 145.44, 148.16, 150.73, 153.43, 156.08, 158.36, 161.07, 163.93, 167.24, 171.25, 176.21, 180.45, 183.64, 185.61, 186.78, 187.53, 188.24, 188.46, 188.46};
        allP97[0][0] = p97_length;
        //weigh chicos
        double[] p97_weigh = {4.41, 7.78, 9.9, 11.43, 12.51, 13.92, 15.55, 16.7, 18.25, 19.95, 20.79, 22.07, 23.44, 25.36, 27.78, 30.07, 31.5, 33.63, 35.64, 37.83, 40.2, 41.76, 45.5, 48.42, 51.51, 54.73, 58.07, 61.5, 64.98, 68.46, 71.91, 75.26, 78.45, 81.43, 84.11, 86.43, 88.31, 89.65, 90.36};
        allP97[0][1] = p97_weigh;
        //PerimetroCraneal chicos
        double[] p97_cranial = {37.11, 43.37, 46.3, 48.18, 49.47, 51.09, 52};
        allP97[0][2] = p97_cranial;
        //IMC Chicos
        double[] p97_IMC = {16.13, 19.9, 20.92, 21.34, 20.92, 19.92, 19.33, 18.95, 18.76, 18.57, 18.49, 18.45, 18.76, 19.04, 19.29, 19.65, 20.11, 20.62, 21.18, 21.75, 22.32, 22.87, 23.39, 23.88, 24.34, 24.77, 25.2, 25.63, 26.07, 26.54, 27.04, 27.58, 28.13, 28.68, 29.17, 29.53, 29.65, 29.4, 28.59};
        allP97[0][3] = p97_IMC;
    }



    public int[] getPerc(){
        int[] percInt = new int[perc.length];

        for(int i = 0; i<perc.length; i++){
            percInt[i] = (int)Math.round(perc[i]*100);
        }
        return percInt;
    }

    /*
    Este metodo devuelve la posicion en el array age en donde se encuentra la age
     */
    private int interval(double age){
        int i;
        for(i = 0; i<this.ageArray.length; i++){
            if(age<this.ageArray[i]) return (i-1);

        }
        return i;
    }

    /*
    Este metodo devuelve la fraction en donde esta nuestra age.
    Es decir, (age-liminferior)/(limsup-liminf)
    Este dato es util porque, al ser lineal, lo usaremos despues
    para calcular el p50 y p97 proporcionales
     */
    private double fraction(int i, double age){
        return (age-this.ageArray[i])/(this.ageArray[i+1]-this.ageArray[i]);
    }

    /*
    Calcula el p50 proporcional a la fraction fr a partir de
    la measure que esta en la posicion i
     */
    private double getP50(int i, double fr, int mag){
        return fr*(allP50[gen][mag][i+1]-allP50[gen][mag][i])+allP50[gen][mag][i];
    }

    /*
    Calcula el p97 proporcional a la fraction fr a partir de
    la measure que esta en la posicion i
     */
    private double getP97(int i, double fr, int mag){
        return fr*(allP97[gen][mag][i+1]-allP97[gen][mag][i])+allP97[gen][mag][i];
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

