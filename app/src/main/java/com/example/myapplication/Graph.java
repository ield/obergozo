package com.example.myapplication;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;


import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

/**
 * Percenciles = p
 */

/**
Para hacer algo que se pueda mandar de una clase a otras:
#1: implements Serializable en la clase del objeto que se quiere enviar
#2: Para pasar de una clase a otra se hace lo siguiente

    Intent i = new Intent(MainActivity.this, Graph.class);
    Objeto o = new Objeto();

    Bundle bundle = new Bundle();
    bundle.putSerializable("objeto", o); //donde "objeto" es la clave y o el objeto que se va a pasar
    i.putExtras(bundle);

    startActivity(i);
#3: En la clase de la actividad nueva se hace lo siguiente

    Bundle objetoRecibido = getIntent().getExtras();
    Objeto o  = null;

    if(objetoRecibido!=null){
        o = (Objeto)objetoRecibido.getSerializable("objeto");
    }
*/

/**
 * Para poner las graficas bien
#1: Crear una lista de entradas por cada una de las lineas:
    List<Entry> entriesMedidas = new ArrayList<Entry>();

#2: Anadir los diferentes puntos a cada una de las listas de entradas:
    entriesMedidas.add(new Entry(ejeX, ejeY));

#3: Crear un LineDataSet con cada una de las listas de entradas que recogera
    las listas de entradas y el label de cada linea.
    LineDataSet lineaMedidas = new LineDataSet(entriesMedidas, "Medidas");

#4: Crear una lista de ILineDataSet que recogera las diferentes lineas:
    List<ILineDataSet> todasMedidas = new ArrayList<ILineDataSet>();

#4: Añadir a esa lista todas las LineDataSets:
    todasMedidas.add(lineaMedidas);

#5: Crear un LineData con la lista de ILineDataSet:
    LineData todasLineas = new LineData(todasMedidas);

#6: lengthGraph.setData(todasLineas);
    lengthGraph.invalidate();
 */

public class Graph extends AppCompatActivity {

    private Dato dato = null;//Este es el dato que se recibe de las medidas tomadas por la app
    private int gen; //Gender, used to select in the 3d array
    private int mag; //Magnitude used to select what is measurable.
    /*
     * Esto se tiene que acabar, hay que hacer un objeto que contenga todos los datos. Esto si no no se sostine.
     */


    private double[] age = {0, 0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18};
    private double[] p50 = {50.06, 60.44, 66.81, 71.1, 75.08, 81.33, 86.68, 90.77, 94.62, 98.41, 102.11, 105.69, 109.11, 113.2, 115.4, 117.33, 120.4, 123.38, 126.18, 129.01, 131.71, 134.18, 136.53, 139.05, 141.53, 143.66, 146.23, 148.96, 152.15, 156.05, 160.92, 165.08, 168.21, 170.18, 171.4, 172.28, 173.23, 173.83, 174.1};
    private double[] p97 = {53.64, 64.4, 70.92, 75.68, 80.01, 86.57, 92.12, 98.62, 102.82, 107.07, 111.28, 115.41, 119.4, 124, 126.18, 129.21, 132.73, 136.13, 139.3, 142.47, 145.44, 148.16, 150.73, 153.43, 156.08, 158.36, 161.07, 163.93, 167.24, 171.25, 176.21, 180.45, 183.64, 185.61, 186.78, 187.53, 188.24, 188.46, 188.46};

    private double[][][] allP50 = new double[2][4][age.length];
    private double[][][] allP97 = new double[2][4][age.length];

    private List<ILineDataSet> todasMedidas = new ArrayList<ILineDataSet>();

    private LineChart lengthGraph;


    private TextView textTitulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Primero gestionamos el tema del paso de referencias entre activities
        lengthGraph = (LineChart) findViewById(R.id.lengthGraph);
        textTitulo = (TextView) findViewById(R.id.textTitulo);

        Bundle objetoRecibido = getIntent().getExtras();
        if (objetoRecibido != null) {
            dato = (Dato) objetoRecibido.getSerializable("dato");
            gen = (int) objetoRecibido.getSerializable("gender");
            mag = (int) objetoRecibido.getSerializable("magnitude");
        }
        //if(dato == null) return;
        /**
         * TODO: Hay que ver que es lo que se hace si se recibe un dato null. En plan,
         * poner un toast o algo asi. Por ahora se termina el metodo.
         * Ademas habra que borrar este dato falso creao para ahorrar tiempo
         */

        generateData();

        double[] medidasTest = {74, 8.8, 46};
        dato.setMeasures(medidasTest);
        dato.setYears(1);

        //Si se recibe un dato se procede a pintarlo
        plotDato();
        plotAllP();

        adjustAxis();
        adjustLegend();

        LineData lineData = new LineData(todasMedidas);
        lengthGraph.setData(lineData);

        lengthGraph.invalidate();
        //lengthGraph.animateXY(2000, 2000);

    }

    private void generateData(){
        generateP50boys();
        generateP50girls();
        generateP97boys();
        generateP97girls();
    }

    private void generateP50girls(){

        //Longitud chicas
        double[] p50_length = {50.06, 60.44, 66.81, 71.1, 75.08, 81.33, 86.68, 90.77, 94.62, 98.41, 102.11, 105.69, 109.11, 113.2, 115.4, 117.33, 120.4, 123.38, 126.18, 129.01, 131.71, 134.18, 136.53, 139.05, 141.53, 143.66, 146.23, 148.96, 152.15, 156.05, 160.92, 165.08, 168.21, 170.18, 171.4, 172.28, 173.23, 173.83, 174.1};
        allP50[1][0] = p50_length;
        //weigh chicas
        double[] p50_weigh = {3.34, 5.79, 7.44, 8.63, 9.6, 10.94, 12.15, 13.3, 14.1, 14.89, 15.59, 16.48, 17.55, 18.77, 20.14, 21.65, 23.27, 24.99, 26.8, 28.68, 30.62, 62.60, 34.61, 36.63, 38.65, 40.66, 42.63, 44.56, 46.43, 48.22, 49.92, 51.52, 53, 54.34, 55.54, 56.57, 57.43, 58.09, 58.55};
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
        double[] p50_weigh = {3.47, 6.26, 8.02, 9.24, 10.15, 11.45, 12.7, 13.84, 14.84, 15.92, 16.9, 17.95, 19.06, 20.24, 21.4, 22.6, 23.26, 24.39, 25.64, 27.04, 28.6, 30.32, 32.22, 34.28, 36.51, 38.88, 41.38, 43.99, 46.68, 49.41, 52.15, 54.86, 87.49, 59.98, 62.27, 64.31, 66.03, 67.35, 68.19};
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




    private void adjustAxis() {
        //Ajustamos el eje x

        XAxis ejeX = lengthGraph.getXAxis();

        ejeX.setPosition(XAxis.XAxisPosition.BOTTOM);
        ejeX.setEnabled(true);
        ejeX.setDrawLabels(true);//Esto es solo para poner los numeros del eje
        ejeX.setDrawAxisLine(true);
        ejeX.setAxisMaximum(18);//TODO quitar numeros magicos
        ejeX.setAxisMinimum(0);


        //Ajustamos el eje y
        YAxis ejeY = lengthGraph.getAxisLeft();
        //ejeY.setPosition(YAxis.YAxisPosition.LEFT);
        ejeY.setDrawLabels(true);
        ejeY.setDrawAxisLine(true);
        ejeY.setAxisMaximum(190);
        ejeY.setAxisMinimum(40);
        lengthGraph.getAxisRight().setEnabled(false);//Asi no se ve el eje de la derecha

        lengthGraph.invalidate();
    }

    private void adjustLegend() {
        Legend leyenda = lengthGraph.getLegend();
        leyenda.setEnabled(false);

        Description d = lengthGraph.getDescription();
        d.setText("age [años]");

        lengthGraph.setPinchZoom(true);
        lengthGraph.invalidate();

    }



    private void plotDato() {
        List<Entry> entriesMedidas = new ArrayList<Entry>();
        entriesMedidas.add(new Entry((float) dato.getYears(), (float) dato.getMeasures()[0]));
        LineDataSet lineaMedidas = new LineDataSet(entriesMedidas, "Medida");
        lineaMedidas.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaMedidas.setColor(ColorTemplate.rgb("0A0A0A"));
        lineaMedidas.setCircleColor(ColorTemplate.rgb("0A0A0A"));
        todasMedidas.add(lineaMedidas);
    }




    private void plotAllP() {
        plotP50();
        plotP97();
        plotP03();
        plotP10();
        plotP25();
        plotP75();
        plotP90();

    }

    private void plotP50() {
        List<Entry> entriesP50 = new ArrayList<Entry>();
        for (int i = 0; i < age.length; i++) {
            entriesP50.add(new Entry((float) age[i], (float) p50[i]));
        }
        LineDataSet lineaP50 = new LineDataSet(entriesP50, "P. 50");
        lineaP50.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaP50.setColor(ColorTemplate.rgb("FF9800"));
        lineaP50.setDrawCircles(false);
        lineaP50.setDrawValues(false);
        lineaP50.setLineWidth(2);
        lineaP50.enableDashedLine(10, 10, 0);

        todasMedidas.add(lineaP50);
    }

    private void plotP03() {
        List<Entry> entriesP03 = new ArrayList<Entry>();

        double z = getZ(0.03);

        for (int i = 0; i < age.length; i++) {
            double sd = (p97[i] - p50[i]) / 1.88;
            double perc = z * sd + p50[i];

            entriesP03.add(new Entry((float) age[i], (float) perc));
        }
        LineDataSet lineaP03 = new LineDataSet(entriesP03, "P 3");
        lineaP03.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaP03.setColor(ColorTemplate.rgb("FF9800"));
        lineaP03.setDrawCircles(false);
        lineaP03.setDrawValues(false);
        lineaP03.setLineWidth(3);

        todasMedidas.add(lineaP03);
    }

    private void plotP97() {
        List<Entry> entriesP97 = new ArrayList<Entry>();
        for (int i = 0; i < age.length; i++) {
            entriesP97.add(new Entry((float) age[i], (float) p97[i]));
        }
        //getResources().getColor(R.color.red);

        LineDataSet lineaP97 = new LineDataSet(entriesP97, "P. 97");
        lineaP97.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaP97.setColor(ColorTemplate.rgb("FF9800"));
        lineaP97.setDrawCircles(false);
        lineaP97.setDrawValues(false);
        lineaP97.setLineWidth(3);

        todasMedidas.add(lineaP97);
    }

    private void plotP10() {
        List<Entry> entriesP10 = new ArrayList<Entry>();

        double z = getZ(0.1);

        for (int i = 0; i < age.length; i++) {
            double sd = (p97[i] - p50[i]) / 1.88;
            double perc = z * sd + p50[i];

            entriesP10.add(new Entry((float) age[i], (float) perc));
        }
        LineDataSet lineaP10 = new LineDataSet(entriesP10, "P 10");
        lineaP10.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaP10.setColor(ColorTemplate.rgb("3b83bd"));
        lineaP10.setDrawCircles(false);
        lineaP10.setDrawValues(false);
        lineaP10.setLineWidth(1);
        lineaP10.enableDashedLine(5, 5, 0);

        todasMedidas.add(lineaP10);
    }

    private void plotP25() {
        List<Entry> entriesP25 = new ArrayList<Entry>();

        double z = getZ(0.25);

        for (int i = 0; i < age.length; i++) {
            double sd = (p97[i] - p50[i]) / 1.88;
            double perc = z * sd + p50[i];

            entriesP25.add(new Entry((float) age[i], (float) perc));
        }
        LineDataSet lineaP25 = new LineDataSet(entriesP25, "P 25");
        lineaP25.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaP25.setColor(ColorTemplate.rgb("3b83bd"));
        lineaP25.setDrawCircles(false);
        lineaP25.setDrawValues(false);
        lineaP25.setLineWidth(1);
        lineaP25.enableDashedLine(5, 5, 0);


        todasMedidas.add(lineaP25);
    }

    private void plotP75() {
        List<Entry> entriesP75 = new ArrayList<Entry>();

        double z = getZ(0.75);

        for (int i = 0; i < age.length; i++) {
            double sd = (p97[i] - p50[i]) / 1.88;
            double perc = z * sd + p50[i];

            entriesP75.add(new Entry((float) age[i], (float) perc));
        }
        LineDataSet lineaP75 = new LineDataSet(entriesP75, "P 75");
        lineaP75.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaP75.setColor(ColorTemplate.rgb("3b83bd"));
        lineaP75.setDrawCircles(false);
        lineaP75.setDrawValues(false);
        lineaP75.setLineWidth(1);
        lineaP75.enableDashedLine(5, 5, 0);


        todasMedidas.add(lineaP75);
    }

    private void plotP90() {
        List<Entry> entriesP90 = new ArrayList<Entry>();

        double z = getZ(0.9);

        for (int i = 0; i < age.length; i++) {
            double sd = (p97[i] - p50[i]) / 1.88;
            double perc = z * sd + p50[i];

            entriesP90.add(new Entry((float) age[i], (float) perc));
        }
        LineDataSet lineaP90 = new LineDataSet(entriesP90, "P 90");
        lineaP90.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaP90.setColor(ColorTemplate.rgb("3b83bd"));
        lineaP90.setDrawCircles(false);
        lineaP90.setDrawValues(false);
        lineaP90.setLineWidth(1);
        lineaP90.enableDashedLine(5, 5, 0);


        todasMedidas.add(lineaP90);
    }


    /**
     * El siguiente metodo lo obtuve de Pablito Coding (https://www.lawebdelprogramador.com/foros/Java/1274298-funciones-estadistica-en-JAVA.html)
     * El de despues lo obtuve partiendo de lo que encontre en el mismo site, modificando otro de los metodos que habia.
     */
    private double calculaz(double v) { //funcion de densidad de probabilidad normal
        double N = Math.exp(-Math.pow(v, 2) / 2) / Math.sqrt(2 * Math.PI);
        return N;
    }

    private double getZ(double p) {

        double acumulador = 0.00000028666;
        double i = -5;
        while (p > acumulador) {
            acumulador += (0.00001 * calculaz(i - 0.000005));
            i += 0.00001;
        }
        return i;
    }
}

