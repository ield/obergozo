package com.example.myapplication;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
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
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
/*
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

/*
Para poner las graficas bien
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

    private double[] edad = {0, 0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18};
    private double[] p50 = {50.06, 60.44, 66.81, 71.1, 75.08, 81.33, 86.68, 90.77, 94.62, 98.41, 102.11, 105.69, 109.11, 113.2, 115.4, 117.33, 120.4, 123.38, 126.18, 129.01, 131.71, 134.18, 136.53, 139.05, 141.53, 143.66, 146.23, 148.96, 152.15, 156.05, 160.92, 165.08, 168.21, 170.18, 171.4, 172.28, 173.23, 173.83, 174.1};
    private double[] p97 = {53.64, 64.4, 70.92, 75.68, 80.01, 86.57, 92.12, 98.62, 102.82, 107.07, 111.28, 115.41, 119.4, 124, 126.18, 129.21, 132.73, 136.13, 139.3, 142.47, 145.44, 148.16, 150.73, 153.43, 156.08, 158.36, 161.07, 163.93, 167.24, 171.25, 176.21, 180.45, 183.64, 185.61, 186.78, 187.53, 188.24, 188.46, 188.46};

    private List<Entry> entriesMedidas;
    private List<Entry> entriesP50;
    private List<Entry> entriesP97;

    private LineDataSet lineaMedidas;
    private LineDataSet lineaP50;
    private LineDataSet lineaP97;

    private List<ILineDataSet> todasMedidas = new ArrayList<ILineDataSet>();

    private LineData lineData;

    private LineChart lengthGraph;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        lengthGraph=(LineChart)findViewById(R.id.lengthGraph);

        Bundle objetoRecibido = getIntent().getExtras();
        if(objetoRecibido!=null){
            dato = (Dato)objetoRecibido.getSerializable("dato");
        }
        //if(dato == null) return;
        /**
         * TODO: Hay que ver que es lo que se hace si se recibe un dato null. En plan,
         * poner un toast o algo asi. Por ahora se termina el metodo.
         * Ademas habra que borrar este dato falso creao para ahorrar tiempo
         */

        double[] medidasTest = {74, 8.8, 46};
        dato.setMedidas(medidasTest);
        dato.setAnos(1);
        //Si se recibe un dato se procede a pintarlo
        dibujaDato();
        dibujaPercentiles();

        ajustaEjes();

        lineData = new LineData(todasMedidas);
        lengthGraph.setData(lineData);
        lengthGraph.invalidate();

    }

    private void ajustaEjes(){
        //Ajustamos el eje x
        XAxis ejeX = lengthGraph.getXAxis();
        ejeX.setPosition(XAxis.XAxisPosition.BOTTOM);
        ejeX.setEnabled(true);
        ejeX.setDrawLabels(true);
        ejeX.setDrawAxisLine(true);
        ejeX.setAxisMaximum(18);
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

    private void dibujaDato(){
        entriesMedidas = new ArrayList<Entry>();
        entriesMedidas.add(new Entry((float)dato.getAnos(), (float)dato.getMedidas()[0]));
        lineaMedidas = new LineDataSet(entriesMedidas, "Medida");
        lineaMedidas.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaMedidas.setColor(ColorTemplate.rgb("0A0A0A"));
        lineaMedidas.setCircleColor(ColorTemplate.rgb("0A0A0A"));
        todasMedidas.add(lineaMedidas);
    }

    private void dibujaPercentiles(){
        dibujaPercentil50();
        dibujaPercentil97();
    }

    private void dibujaPercentil50(){
        entriesP50 = new ArrayList<Entry>();
        for(int i = 0; i<edad.length; i++){
            entriesP50.add(new Entry((float)edad[i], (float)p50[i]));
        }
        lineaP50 = new LineDataSet(entriesP50, "Percentil 50");
        lineaP50.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaP50.setColor(ColorTemplate.rgb("FF9800"));
        lineaP50.setDrawCircles(false);
        lineaP50.setDrawValues(false);
        lineaP50.setLineWidth(1);
        lineaP50.enableDashedLine(10,10,0);

        todasMedidas.add(lineaP50);
    }

    private void dibujaPercentil97(){
        entriesP97 = new ArrayList<Entry>();
        for(int i = 0; i<edad.length; i++){
            entriesP97.add(new Entry((float)edad[i], (float)p97[i]));
        }
        //getResources().getColor(R.color.red);

        lineaP97 = new LineDataSet(entriesP97, "Percentil 97");
        lineaP97.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineaP97.setColor(ColorTemplate.rgb("FF9800"));
        lineaP97.setDrawCircles(false);
        lineaP97.setDrawValues(false);
        lineaP97.setLineWidth(3);

        todasMedidas.add(lineaP97);
    }


}

