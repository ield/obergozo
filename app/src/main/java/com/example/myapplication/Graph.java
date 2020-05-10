//Engineer: ield
//

/**
 Para hacer algo que se pueda mandar de una clase a otras:
 #1: implements Serializable en la clase del objeto que se quiere enviar
 #2: Para pasar de una clase a otra se hace lo siguiente

 Intent i = new Intent(InsertDataUnsaved.this, Graph.class);
 Objeto o = new Objeto();

 Bundle bundle = new Bundle();
 bundle.putSerializable("objeto", o); //donde "objeto" es la clave y o el objeto que se va a pasar
 i.putExtras(bundle);

 startActivity(i);
 #3: En la clase de la actividad nueva se hace lo siguiente

 Bundle receivedObj = getIntent().getExtras();
 Objeto o  = null;

 if(receivedObj!=null){
 o = (Objeto)receivedObj.getSerializable("objeto");
 }
 */

package com.example.myapplication;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


import androidx.appcompat.app.AppCompatActivity;


import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.Math.round;

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
    private int mag; //Magnitude used to select what measure is to be plotted

    /*
    Data related to the baby
     */
    private int babyNumber;
    private String fileName;
    private String name;
    private String birthDate;

    /*
    Two different age arrays are created. This is because when it is plotted the cranial perimeter,
    there is only data until 2 years old. When it is worked with cranial, it is used the short
    array. Otherwise, it is used the long one.
    Age will be equal to the ageLong or ageShort depending on the magnitude.
     */
    private double[] ageLong = {0, 0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18};
    private double[] ageShort = {0, 0.25, 0.5, 0.75, 1, 1.5, 2};
    private double[] age;


    /*
    All the data (length, weight, cranial and IMC) is stored in these 3D arrays. The positioning is:
    array[gender][magnitude][value] where
        gender = [male female]
        magnitude = [length, weight, cranial, IMC]
        value: ordered with the age
     */
    private double[][][] allP50 = new double[2][4][ageLong.length];
    private double[][][] allP97 = new double[2][4][ageLong.length];


    /*
    In these arrays will be stored the maxima and minima of each magnitude for deciding the limits
    when plotting the axis
     */
    private double[]maxY = new double[4];
    private double[]minY = new double[4];


    //The title will be magnitude + gender (ej: longitud chico)
    private String[] titleMag = {"Longitud", "Peso", "Perímetro Craneal", "IMC"};
    private String[] titleGen = {"Chico", "Chica"};
    private TextView textTitulo;

    /*
    Here it is stored all the data needed for plotting. The instructions to do so are obtained from
    com.github.mikephil.charting
     */
    private List<ILineDataSet> todasMedidas = new ArrayList<ILineDataSet>();
    private LineChart lengthGraph;

    @Override
    /**
     * What to do when the activity is created:
     * #1.  The android elements are initialized: the graph and the textView of the title
     * #2.  It is obtained the datum to be plotted: its measures, the gender and the magitude
     * #3.  Depending on the magnitude it is selected whether to use ageLong or short. If it is
     *      cranial, it will be used age short.
     * #4.  There are two 3D arrays generated with all the data allP50 and allP97. They will be used
     *      to plot the p50 and p97 as well as to obtain the other percentiles.
     * #5.  It is configured the element (lineData) that contains all the data to be plotted:
     *      the datum received and all the percentiles (p03, 10, 25, 50, 75, 90, 97).
     * #6.  The graph is adjusted to the desired graph: the axis, the legend etc.
     * #7.  The graph is plotted.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);


        // #1
        lengthGraph = (LineChart) findViewById(R.id.lengthGraph);
        textTitulo = (TextView) findViewById(R.id.textTitulo);

        // #2
        Bundle receivedObj = getIntent().getExtras();
        if((Dato) receivedObj.getSerializable("dato") == null){
            babyNumber = (int)receivedObj.getSerializable("babyNumber");
            getBaby(babyNumber);
            mag = (int) receivedObj.getSerializable("magnitude");
        }else{
            dato = (Dato) receivedObj.getSerializable("dato");
            gen = (int) receivedObj.getSerializable("gender");
            mag = (int) receivedObj.getSerializable("magnitude");
//        double[] medidasTest = {74, 8.8, 46};
//        dato.setMeasures(medidasTest);
//        dato.setYears(1);
        }

        // #3
        if(mag == 2){
            this.age = this.ageShort;
        }else{
            this.age = this.ageLong;
        }

        // #4
        generateData();

//        Toast toast = Toast.makeText(getApplicationContext(), "Hasta aquí llegamos", Toast.LENGTH_SHORT);
//        toast.show();

        // #5
        if((Dato) receivedObj.getSerializable("dato") == null) plotData();
        else plotDato();

        plotAllP();

        // #6
        adjustGraph();

        // #7
        LineData lineData = new LineData(todasMedidas);
        lengthGraph.setData(lineData);
        lengthGraph.invalidate();

    }

    /**
     * Obtains the name, born date and gender of the baby from its document. Step 2
     * @param i The number of the baby, given by the last activity, which knew it because of the
     *          button pressed.
     * How to read the file was known from:
     *         https://developer.android.com/training/data-storage/app-specific#java
     */
    public void getBaby(int i){
        String[] files = getApplicationContext().fileList();
        String[] txtFiles = getTxt(files);
        fileName = txtFiles[i];

        try{
            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            this.name = reader.readLine();
            this.gen = parseInt(reader.readLine());
            this.birthDate = reader.readLine();

        }catch(IOException e){
            e.printStackTrace();
        }
        setTitle(name);
    }

    /**
     * From all the files in the folder selects only the txt ones
     * #1. Counts the number of files that end in .txt
     * #2. Saves in an array all the .txt files
     */
    private String[] getTxt(String[] allFiles) {
        int numTxtFiles = 0;

        for(int i = 0; i<allFiles.length; i++) {
            try {//May be exceptions if the tring is shorter than 4 characters and not a .txt
                String end = allFiles[i].substring(allFiles[i].length()-4);

                if(end.contentEquals(".txt")) {
                    numTxtFiles++;
                }

            }catch(Exception e) {}

        }

        String[] txtFiles = new String[numTxtFiles];
        int pos = 0;

        for(int i = 0; i<allFiles.length; i++) {
            try {//May be exceptions if the tring is shorter than 4 characters and not a .txt
                String end = allFiles[i].substring(allFiles[i].length()-4);

                if(end.contentEquals(".txt")) {
                    txtFiles[pos] = allFiles[i];
                    pos++;
                }

            }catch(Exception e) {}

        }

        return txtFiles;
    }

    /**
     * Generating Data: Step #4
     * There are two 3D arrays generated with all the data of all maginitudes: allP50 and allP97.
     * The function generateData calls the other functions that generate the data
     * Each function just stores the data
     */
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

    /**
     * Plotting Data: Step #5
     * Plotting data consists on adding LineDataSet, which are formed by arrayList of entries.
     * An entry has an x and a y.
     * The data is obtained from the baby file.
     * All data is plotted as a circle. The color is set to black
     *  #1. Select the file to read
     *  #2. Omit the first three lines: the name, age and gender of the baby
     *  #3. For every line obtain the age and the magnitud wanted to plot and create and entry with
     *      that measure.
 *      #4. Add all the entries
     */
    private void plotData(){
        List<Entry> entriesMedidas = new ArrayList<Entry>();
        try{
            // #1
            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            // #2. Skips the name, birthdate and gender of the baby
            reader.readLine();
            reader.readLine();
            reader.readLine();

            // #3
            String line = reader.readLine();//First line with data

            while (line != null) {
                double[] datum = obtainSubstring(line, mag);
                entriesMedidas.add(new Entry((float) datum[0], (float) datum[1]));

                line = reader.readLine();
            }

            reader.close();
            isr.close();
            fis.close();

        }catch(IOException e){
            e.printStackTrace();
        }

        // #4
        LineDataSet lineaMedidas = new LineDataSet(entriesMedidas, this.name);
        lineaMedidas.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaMedidas.setColor(ColorTemplate.rgb("0A0A0A"));
        lineaMedidas.setCircleColor(ColorTemplate.rgb("0A0A0A"));
        todasMedidas.add(lineaMedidas);

    }

    /**
     * Obtains the age and the magnitude that must be plotted
     * @param s the line containin all the data (date, age, length, weight, crannial and imc)
     * @param mag the magnitud that must be obtained
     * @return an array [age magnitude]
     *
     * It looks in the string for the age: the number between the 1st and 2nd space
     * It looks in the string for the magnitude, the number between the n and n+1 where n depends on
     *  the magnitude. It is written date age length wright crannial imc (with a space at the end)
     */
    private double[] obtainSubstring(String s, int mag) {
        double[] result = new double[2];

        //Look for magnitude
        int lastPos = 0;
        for(int i = 0; i<(mag + 2); i++) {
            lastPos = s.indexOf(" ", lastPos+1);
        }

        int nextSpace = s.indexOf(" ", lastPos+1);
        result[1] = Double.parseDouble(s.substring(lastPos+1, nextSpace + 1));

        //Look for age
        int firstSpace = s.indexOf(" ");
        int secondSpace = s.indexOf(" ", firstSpace+1);

        result[0] = Double.parseDouble(s.substring(firstSpace+1, secondSpace + 1));

        return result;
    }

    /**
     * Plotting Data: Step #5
     * Plotting data consists on adding LineDataSet, which are formed by arrayList of entries.
     * An entry has an x and a y.
     * The datum plotted has only one x (the age) and one y (the value of the magnitude), both
     *  contained in dato.
     * Since it is only one datum, it is plotted as a circle. The color is set to black
     */
    private void plotDato() {
        List<Entry> entriesMedidas = new ArrayList<Entry>();
        entriesMedidas.add(new Entry((float) dato.getYears(), (float) dato.getMeasures()[mag]));
        LineDataSet lineaMedidas = new LineDataSet(entriesMedidas, "Medida");
        lineaMedidas.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaMedidas.setColor(ColorTemplate.rgb("0A0A0A"));
        lineaMedidas.setCircleColor(ColorTemplate.rgb("0A0A0A"));
        todasMedidas.add(lineaMedidas);
    }

    /**
     * Plotting Curves: Step #5
     * Plotting data consists on adding LineDataSet, which are formed by arrayList of entries.
     * An entry has an x and a y.
     * For P50 and P97 all it is done is add the values of the percentiles and the age to form
     *  entries. Then they are plotted with different colors and line effects.
     * For the rest of the percentiles the process is more complex. First it is calculated the
     *  percentile value. This is done using the p50 and the p97 with the function getZ, to
     *  calculate any percentile
     */
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
            entriesP50.add(new Entry((float) age[i], (float) allP50[gen][mag][i]));
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

        double yMin = 500;

        for (int i = 0; i < age.length; i++) {
            double sd = (allP97[gen][mag][i] - allP50[gen][mag][i]) / 1.88;
            double perc = z * sd + allP50[gen][mag][i];

            if(perc < yMin) yMin = perc;

            entriesP03.add(new Entry((float) age[i], (float) perc));
        }
        minY[mag] = yMin;

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

        double yMax = 0;

        for (int i = 0; i < age.length; i++) {
            entriesP97.add(new Entry((float) age[i], (float) allP97[gen][mag][i]));
            if(allP97[gen][mag][i] > yMax) yMax = allP97[gen][mag][i];
        }
        //getResources().getColor(R.color.red);
        maxY[mag] = yMax;

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
            double sd = (allP97[gen][mag][i] - allP50[gen][mag][i]) / 1.88;
            double perc = z * sd + allP50[gen][mag][i];

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
            double sd = (allP97[gen][mag][i] - allP50[gen][mag][i]) / 1.88;
            double perc = z * sd + allP50[gen][mag][i];

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
            double sd = (allP97[gen][mag][i] - allP50[gen][mag][i]) / 1.88;
            double perc = z * sd + allP50[gen][mag][i];

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
            double sd = (allP97[gen][mag][i] - allP50[gen][mag][i]) / 1.88;
            double perc = z * sd + allP50[gen][mag][i];

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
     * Adjusting the graph: Step #5
     * It is adjusted first the xaxis and then the y axis: the position, etc.
     * The limits of the xaxis depend on the magnitude being measured: if it is cranial, the maximum
     *  is 2 years. Otherwise the maximum is 18 years.
     * The limits of the yaxis depend on the limits of the magnitude being measured. These limits
     *  are calculated while they were going to be plotted: in plotP97 and plotP03. The limit is set
     *  to this values +-5
     * It is also adjusted the title of the graph
     * The legend of the graph is set so that there is no legend and the xaxis sets to be in years.
     * The zoom is configured. Right now, there is zoom in vertical or horizonta. Otherwise just in
     *  diagonal (without the comments)
     */
    private void adjustGraph() {
        //Ajustamos el eje x
        XAxis ejeX = lengthGraph.getXAxis();
        ejeX.setPosition(XAxis.XAxisPosition.BOTTOM);
        ejeX.setEnabled(true);
        ejeX.setDrawLabels(true);//Esto es solo para poner los numeros del eje
        ejeX.setDrawAxisLine(true);
        if(this.mag == 2){
            ejeX.setAxisMaximum(2);
        }else{
            ejeX.setAxisMaximum(18);
        }
        ejeX.setAxisMinimum(0);

        //Ajustamos el eje y
        YAxis ejeY = lengthGraph.getAxisLeft();
        //ejeY.setPosition(YAxis.YAxisPosition.LEFT);
        ejeY.setDrawLabels(true);
        ejeY.setDrawAxisLine(true);
        ejeY.setAxisMaximum(round(maxY[mag])+5);
        ejeY.setAxisMinimum(round(minY[mag])-5);
        lengthGraph.getAxisRight().setEnabled(false);//Asi no se ve el eje de la derecha

        //Adjusting the title
        String title;
        if(dato == null) title = titleMag[mag] + " " + this.name;
        else title = titleMag[mag] + " " + titleGen[gen];
        textTitulo.setText(title);

        //Adjusting the legend
        Legend leyenda = lengthGraph.getLegend();
        leyenda.setEnabled(false);

        Description d = lengthGraph.getDescription();
        d.setText("edad [años]");

        //Adjusting the zoom
        //lengthGraph.setPinchZoom(true); //This is to make zoom in all directions


        lengthGraph.invalidate();
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

