package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import static java.lang.Integer.parseInt;
import static java.lang.Math.round;

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
    private int babyNumber;
    private String fileName;
    private String name;
    private int gender;
    private String birthDate;

    private double[] medidas = new double[4];

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
        this.babyNumber = (int)receivedObj.getSerializable("babyNumber");
        getBaby(babyNumber);

        // #3
        confBCalc();
        confBPlotLength();
        confBPlotWeigh();
        confBPlotCranial();
        confBPlotIMC();

    }

    /**
     * Obtains the name, born date and gender of the baby from its document. Step 2
     * @param i The number of the baby, given by the last activity, which knew it because of the
     *          button pressed.
     * How to read the file was known from:
     *         https://developer.android.com/training/data-storage/app-specific#java
     */
    public void getBaby(int i){
        fileName = getApplicationContext().fileList()[i];
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
                    medidas[3] = round(c1.getIMC()*1000.0)/1000.0;

                    // #4
                    ResLongitud.setText("El percentil de longitud es: "+percentiles[0]);
                    ResPeso.setText("El percentil de peso es: "+percentiles[1]);
                    ResPerimetroCraneal.setText("El percentil de perímetro craneal es: "+percentiles[2]);
                    ResIMC.setText("El percentil de IMC es: "+percentiles[3]);

                    // #5
                    saveData();
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

    /**
     * Saves the data in the file of the baby
     * Checks the date is not yet introduced. If it is it replaces it. Step 1, 2 are checked
     * #1   Looks for the last date introduced
     * #2   Looks if the last date introduced was today or not.
     * #3   If it is the first of the day, it writes the datum. If it is not, it deletes the last
     *      line and writes the new one
     */
    private void saveData(){
        try{
            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            // #1
            String lastLine = "";
            String data = "";
            String dataNoLastLine = "";//Here is saves the data without the last line
            String line = reader.readLine();
            while (line != null) {
                dataNoLastLine = data;
                data += (line+"\n");
                lastLine = line;
                line = reader.readLine();
            }
            int space = lastLine.indexOf(" ");

            // #2
            if(space == -1) insertData(data); //When it is the first datum, the line before has no space
            else{ //When the line before has another data it checks if the dates are the same
                String date = lastLine.substring(0, space);
                boolean used = checkUsed(date);

                // #3
                if(!used) insertData(data);//When it is the first measure of the day it is inserted
                else{//When it is not the first measure of the day it is deleted the last line
                    insertData(dataNoLastLine);
//                    Toast toast = Toast.makeText(getApplicationContext(), "We are OK", Toast.LENGTH_SHORT);
//                    toast.show();
                }

            }

        }catch(IOException e){
            e.printStackTrace();
        }


    }

    /**
     * Check if the date passed is today
     * @param s the date given
     * @return true if the date is used
     */
    private boolean checkUsed(String s){
        int[] hoy = {today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH)+1, today.get(Calendar.YEAR)};
        int[] date = convierteFechaArray(s);

        if(hoy[0] == date[0] && hoy[1] == date[1] && hoy[2] == date[2]) return true;

        return false;
    }

    /**
     * Inserts the data into the baby file
     * @param previousData The string to insert
     * It deletes all the information in the txt file and inserts the new one. This is not very
     *                     efficient but it the only solution I came up with. The new one is the
     *                     previous information with or without the last line + the datum recorded
     *                     today:
     *                     date age length weight crannial imc
     *                     #1. Creates the string that will be in the file
     *                     #2. Inserts the string in the file
     * @// TODO: 07/05/2020 make it more efficient!! (only write the last line, not rewrite the whole document)
     */
    private void insertData(String previousData){

        // #1
        int[] hoy = {today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH)+1, today.get(Calendar.YEAR)};
        String todayDate = (hoy[0] + "/" + hoy[1] + "/" + hoy[2]);
        double ageRound = round(getAge()*1000.0)/1000.0;
        String newInfo = todayDate + " " + ageRound;

        for(int i = 0; i<medidas.length; i++){
            newInfo += " " + medidas[i];
        }
        newInfo += " ";//So that there are no errors when looking for the last parameter

        previousData = previousData + newInfo;

        // #2
        try{
            FileOutputStream fos = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.append(previousData);
            writer.close();
            fos.close();

            Toast toast = Toast.makeText(getApplicationContext(), "Se han guardado las medidas", Toast.LENGTH_SHORT);
            toast.show();
        }catch(IOException e){
            e.printStackTrace();
            Toast toast = Toast.makeText(getApplicationContext(), "There has been a problem", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    /**
     * Listeners for all the graphing buttons
     * It is launched the new class, where it is given what it needs to be ploted:
     *  Dato:       the datum just introduces. Maybe it is null: it is posible to see the graphs
     *              with no data
     *  Gender:     The gender of the baby. By default will be a girl
     *  Magnitude:  The magnitude to plot: 0 = length, 1 = weight, 2 = cranial, 3 = imc. Depends on
     *              the button it will be set one or another.
     */
    public void confBPlotLength(){
        bPlotLength = (Button) findViewById(R.id.bPlotLength);
        bPlotLength.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(InsertDataSaved.this, Graph.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("babyNumber", babyNumber); //donde "dato" es la clave y dato el objeto que se va a pasar
                bundle.putSerializable("magnitude", 0);
                i.putExtras(bundle);

                startActivity(i);

            }

        });
    }

    public void confBPlotWeigh(){
        bPlotWeigh = (Button) findViewById(R.id.bPlotWeigh);
        bPlotWeigh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(InsertDataSaved.this, Graph.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("babyNumber", babyNumber); //donde "dato" es la clave y dato el objeto que se va a pasar
                bundle.putSerializable("magnitude", 1);
                i.putExtras(bundle);

                startActivity(i);

            }

        });
    }

    public void confBPlotCranial(){
        bPlotCranial = (Button) findViewById(R.id.bPlotCranial);
        bPlotCranial.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(InsertDataSaved.this, Graph.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("babyNumber", babyNumber); //donde "dato" es la clave y dato el objeto que se va a pasar
                bundle.putSerializable("magnitude", 2);
                i.putExtras(bundle);

                startActivity(i);
            }

        });
    }

    public void confBPlotIMC(){
        bPlotIMC = (Button) findViewById(R.id.bPlotIMC);
        bPlotIMC.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(InsertDataSaved.this, Graph.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("babyNumber", babyNumber); //donde "dato" es la clave y dato el objeto que se va a pasar
                bundle.putSerializable("magnitude", 3);
                i.putExtras(bundle);

                startActivity(i);

            }

        });
    }



}
