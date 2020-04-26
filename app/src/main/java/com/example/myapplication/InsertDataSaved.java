package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class InsertDataSaved extends AppCompatActivity {

    // Android elements
    private TextView Longitud;
    private EditText longitudText;

    private TextView peso;
    private EditText pesoText;

    private TextView perCraneal;
    private EditText perCranealText;

    private TextView ResLongitud;
    private TextView ResPeso;
    private TextView ResPerimetroCraneal;
    private TextView ResIMC;

    private Button bCalc;
    private Button bPlotLength;
    private Button bPlotWeigh;
    private Button bPlotCranial;
    private Button bPlotIMC;

    Calendar today = Calendar.getInstance();
    private String falloLongitud = "Introduce la longitud";
    private String falloPeso = "Introduce el peso";
    private String falloPC = "Introduce el diámetro craneal";

    //Information of the baby
    private String name;
    private int gender;
    private String birthDate;

    private double[] medidas = new double[3];

    @Override
    /**
     * What to do when the activity is created:
     * #1.  The android elements are initialized.
     * #2.  It is obtained the baby: its name, gender and birthdate
     * #3.  Configures all the listeners of the buttons
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data_saved);

        // #1
        Longitud = (TextView) findViewById(R.id.length);
        longitudText = (EditText) findViewById(R.id.lengthText);

        peso = (TextView) findViewById(R.id.weight);
        pesoText = (EditText) findViewById(R.id.weightText);

        perCraneal = (TextView) findViewById(R.id.cranial);
        perCranealText = (EditText) findViewById(R.id.cranialText);

        ResLongitud = (TextView) findViewById(R.id.ResLongitud);
        ResPeso = (TextView) findViewById(R.id.ResPeso);
        ResPerimetroCraneal = (TextView) findViewById(R.id.ResPerimetroCraneal);
        ResIMC = (TextView) findViewById(R.id.ResIMC);

        // #2
        Bundle receivedObj = getIntent().getExtras();
        int i = (int)receivedObj.getSerializable("babyNumber");
        getBaby(i);

        // #3
        confBCalc();


    }

    /**
     * Obtains the name, born date and gender of the baby from its document. Step 2
     * @param i The number of the baby, given by the last activity, which knew it because of the
     *          button pressed.
     * How to read the file was known from:
     *         https://developer.android.com/training/data-storage/app-specific#java
     */
    public void getBaby(int i){
        String fileName = getApplicationContext().fileList()[i];
        try{
            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            name = reader.readLine();
            gender = parseInt(reader.readLine());
            birthDate = reader.readLine();

        }catch(IOException e){
            e.printStackTrace();
        }
        setTitle(name);
    }


    /**
     * "Calculate" Button Listener
     * The button considers various scenarios that prevents from different errors
     *  List of errors
     *      #1. Not a valid number in length, weight or cranial perimeter
     * If an error happens, it is displayed a toast informing on the error and no measurements
     * If there are no errors
     *  #1. All the measures are gathered in an array
     *  #2. It is calculated the age of the baby
     *  #3. They are calculated all the percentiles of all the magnitudes
     *  #4. It is informed of all the percentiles in a text.
     *  #5. The variable dato is given parameters. It will be the variable that will be plotted, so
     *      all the information is gathered
     */
    public void confBCalc(){
        bCalc = (Button) findViewById(R.id.bCalculate);
        bCalc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //A continuacion vienen los posibles errores que nos podemos encontrar
                // Errors #1
                if(longitudText.getText()+"" == ""){
                    Toast toast = Toast.makeText(getApplicationContext(), falloLongitud, Toast.LENGTH_SHORT);
                    toast.show();
                    ResLongitud.setText("");
                    ResPeso.setText("");
                    ResPerimetroCraneal.setText("");
                    ResIMC.setText("");
                }
                else if(pesoText.getText()+"" == ""){
                    Toast toast = Toast.makeText(getApplicationContext(), falloPeso, Toast.LENGTH_SHORT);
                    toast.show();
                    ResLongitud.setText("");
                    ResPeso.setText("");
                    ResPerimetroCraneal.setText("");
                    ResIMC.setText("");
                }
                else if(perCranealText.getText()+"" == ""){
                    Toast toast = Toast.makeText(getApplicationContext(), falloPC, Toast.LENGTH_SHORT);
                    toast.show();
                    ResLongitud.setText("");
                    ResPeso.setText("");
                    ResPerimetroCraneal.setText("");
                    ResIMC.setText("");
                }

                // No errors
                else {
                    // #1
                    medidas[0] = Double.parseDouble(longitudText.getText().toString());
                    medidas[1] = Double.parseDouble(pesoText.getText().toString());
                    medidas[2] = Double.parseDouble(perCranealText.getText().toString());

                    // #2
                    double age = getAge();

                    // #3
                    Calculator c1 =  new Calculator(age, medidas, gender);
                    int[] percentiles = c1.getPerc();

                    // #4
                    ResLongitud.setText("El percentil de longitud es: "+percentiles[0]);
                    ResPeso.setText("El percentil de peso es: "+percentiles[1]);
                    ResPerimetroCraneal.setText("El percentil de perímetro craneal es: "+percentiles[2]);
                    ResIMC.setText("El percentil de IMC es: "+percentiles[3]);

                    // #5
//                    d.setYears(age);
//                    d.setMeasures(medidas);
//                    d.setIMC(c1.getIMC());
                }
            }

        });
    }

    /**
     * Gets the born date and today's date and returns the age of the baby.
     * It is used countAge to calculate the age
     * @return the age of the baby
     */
    public double getAge(){
        int[] hoy = {today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH)+1, today.get(Calendar.YEAR)};
        int[] nac = convierteFechaArray(birthDate);
        double age = countAge(hoy, nac);
        return age;
    }

    public int[] convierteFechaArray(String s){
        int[] res = new int[3];
        int posPrevia = 0;
        int cont = 0;

        for(int i = 0; i<s.length(); i++){
            if(s.charAt(i) == '/'){
                if(cont == 0){
                    res[0] = Integer.parseInt(s.substring(posPrevia, i));
                    cont++;
                    posPrevia = i+1;
                }
                else{
                    res[1] = Integer.parseInt(s.substring(posPrevia, i));
                    res[2] = Integer.parseInt(s.substring(i+1));
                    return res;
                }
            }
        }
        return res;

    }

    public double countAge(int[] today, int[] nacimiento){
        double age = 0.0;


        //Ajustamos los dias
        double daysYear = 1.0/365.0;

        if(nacimiento[2] % 400 == 0 || (nacimiento[2] % 4 == 0 && nacimiento[2] % 100 != 0)) daysYear = 1.0/366.0;

        while(true){
            System.out.println(nacimiento[0]);
            if(today[0] == nacimiento[0]) break;

            nacimiento[0]++;
            age = age + daysYear;

            if(nacimiento[0] > 28 && nacimiento[1] == 2 && daysYear == 1/365){//Si es 29/02 en un ano no bisiesto
                nacimiento[0] = 1;
                nacimiento[1]++;
                continue;
            }
            else if(nacimiento[0] > 29 && nacimiento[1] == 2 && daysYear == 1/366){//Si es 30/02 en un ano bisiesto
                nacimiento[0] = 1;
                nacimiento[1]++;
                continue;
            }
            else if(nacimiento[0] > 30 && (nacimiento[1] == 4 || nacimiento[1] == 6 || nacimiento[1] == 9 || nacimiento[1] == 11)){
                nacimiento[0] = 1;
                nacimiento[1]++;
                continue;
            }
            else if(nacimiento[0] > 31){
                nacimiento[0] = 1;
                nacimiento[1]++;
                if(nacimiento[1] > 12){
                    nacimiento[1] = 1;
                    nacimiento[2]++;
                    if(nacimiento[2] % 400 == 0 || (nacimiento[2] % 4 == 0 && nacimiento[2] % 100 != 0)) daysYear = 1.0/366.0;
                    else daysYear = 1.0/365.0;
                }
                continue;
            }

        }

        //Ajustamos los meses
        while(true){
            System.out.println(age);
            if(today [1] == nacimiento[1]) break;

            if(nacimiento[1] == 2 && daysYear == 1/365){
                age += 28.0*daysYear;
                nacimiento[1]++;
                continue;
            }
            else if(nacimiento[1] == 2){
                age += 29.0*daysYear;
                nacimiento[1]++;
                continue;
            }
            else if(nacimiento[1] == 4 || nacimiento[1] == 6 || nacimiento[1] == 9 || nacimiento[1] == 11){
                age += 30.0*daysYear;
                nacimiento[1]++;
                continue;
            }
            else{
                age += 31.0*daysYear;
                nacimiento[1]++;
                if(nacimiento[1] == 12){
                    nacimiento[2]++;
                    nacimiento[1] = 1;
                    if(nacimiento[2] % 400 == 0 || (nacimiento[2] % 4 == 0 && nacimiento[2] % 100 != 0)) daysYear = 1.0/366.0;
                    else daysYear = 1.0/365.0;
                }
            }


        }

        while(true){
            System.out.println(age);
            if(today[2] == nacimiento[2]) return age;
            nacimiento[2]++;
            age++;
        }



    }
}
