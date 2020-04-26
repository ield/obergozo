package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class NewBaby extends AppCompatActivity {

    private TextView bornDate;
    private EditText dateText;
    Calendar today = Calendar.getInstance();

    private TextView gender;
    private CheckBox girl;
    private CheckBox boy;

    private TextView name;
    private EditText nameText;

    private Button bSave;
    private String falloFecha = "Fecha no válida";
    private String falloCheckBox = "Marca si es chico o chica";
    private String falloName = "Introduce un nombre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_baby);
        setTitle("Nuev@ bebé");

        // Configure the born date
        bornDate = (TextView) findViewById(R.id.bornDate);
        dateText = (EditText) findViewById(R.id.dateText);
        dateText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new DatePickerDialog(NewBaby.this, date, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();

            }});

        // Configure the checkbox girl and boy: when boy is checked girl is unchecked and viceversa
        gender = (TextView) findViewById(R.id.gender);
        girl = (CheckBox) findViewById(R.id.girl);
        girl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(girl.isChecked()) boy.setChecked(false);
            }});
        boy = (CheckBox) findViewById(R.id.boy);
        boy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(boy.isChecked()) girl.setChecked(false);
            }});

        //Configure the name of the baby
        name = (TextView) findViewById(R.id.name);
        nameText = (EditText) findViewById(R.id.nameText);

        //Configures the button
        confBSave();


    }

    /**
     * Este metodo se usa para decir lo que pasa una vez se coloca una fecha en la caja de texto
     * de la fecha
     */
    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day){
            dateText.setText(day+"/"+(month+1)+"/"+year);
        }
    };


    /**
     * "Save" Button Listener
     * The button considers various scenarios that prevents from different errors
     *  List of errors
     *      #1. Not a valid date
     *      #2. Not selected girl or boy
     *      #3. Not a name
     * If an error happens, it is displayed a toast informing on the error and no measurements
     * If there are no errors
     *  #1. All the measures are gathered in an array
     *  #2. It is calculated the age of the baby
     *  #3. They are calculated all the percentiles of all the magnitudes
     *  #4. It is informed of all the percentiles in a text.
     *  #5. The variable dato is given parameters. It will be the variable that will be plotted, so
     *      all the information is gathered
     */
    public void confBSave(){
        bSave = (Button) findViewById(R.id.bSave);
        bSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //A continuacion vienen los posibles errores que nos podemos encontrar
                // Error #1
                if(!fechaValida() || dateText.getText()+"" == ""){
                    Toast toast = Toast.makeText(getApplicationContext(), falloFecha, Toast.LENGTH_SHORT);
                    toast.show();
                }
                // Error #2
                else if(!girl.isChecked() && !boy.isChecked()){
                    Toast toast = Toast.makeText(getApplicationContext(), falloCheckBox, Toast.LENGTH_SHORT);
                    toast.show();
                }
                // Errors #3
                else if(nameText.getText()+"" == ""){
                    Toast toast = Toast.makeText(getApplicationContext(), falloName, Toast.LENGTH_SHORT);
                    toast.show();
                }

                // No errors
                else {
                    createNewBabyFile();

                    Intent i = new Intent(NewBaby.this, Inicio.class);
                    startActivity(i);

                }
            }

        });
    }

    /**
     * Checks if a date is valid
     * This method considers:
     *  #1. The day selected belongs to the future
     *  #2. Unvalid days or months (ie: month 15 or 30 feb). This included 29 feb in lap years     *
     * @return true if the date is valid
     */
    public boolean fechaValida(){
        int[] born = convierteFechaArray(dateText.getText().toString());
        int[] hoy = {today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH)+1, today.get(Calendar.YEAR)};

        // #1
        if(born[2]>hoy[2] || born[2]<hoy[2]-18) return false;
        if(born[2]==hoy[2] && born[1]>hoy[1]) return false;
        if(born[2]==hoy[2] && born[1]==hoy[1] && born[0]>hoy[0] ) return false;

        // #2
        if(born[1]<1 || born [1]>12 || born[0]<1) return false;
        if(born[1]==2 && born[0]>29) return false;
        if(born[1]==2 && born[0]>28 && !(born[2] % 400 == 0 || (born[2] % 4 == 0 && born[2] % 100 != 0))) return false;
        if((born[1] == 4 || born[1] == 6 || born[1] == 9 || born[1] == 11) && born[0]>30) return false;
        if(born[0]>31) return false;

        return true;

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

    /**
     * Information obtained in how to create, edit and read files from
     * https://stackoverflow.com/questions/20753600/creating-writing-and-editing-same-text-file-in-java
     * import java.io.BufferedReader;
     * import java.io.BufferedWriter;
     * import java.io.File;
     * import java.io.FileReader;
     * import java.io.FileWriter;
     * import java.io.IOException;
     *
     * public class EditFile {
     *
     *     public static void main(String[] args) {
     *
     *         try{
     *             String verify, putData;
     *             File file = new File("file.txt");
     *             file.createNewFile();
     *             FileWriter fw = new FileWriter(file);
     *             BufferedWriter bw = new BufferedWriter(fw);
     *             bw.write("Some text here for a reason");
     *             bw.flush();
     *             bw.close();
     *             FileReader fr = new FileReader(file);
     *             BufferedReader br = new BufferedReader(fr);
     *
     *             while( (verify=br.readLine()) != null ){ //***editted
     *                        //**deleted**verify = br.readLine();**
     *                 if(verify != null){ //***edited
     *                     putData = verify.replaceAll("here", "there");
     *                     bw.write(putData);
     *                 }
     *             }
     *             br.close();
     *
     *
     *         }catch(IOException e){
     *         e.printStackTrace();
     *         }
     *     }
     *
     * }
     *
     ** Official site to view how to write files
     * https://developer.android.com/training/data-storage/app-specific#java
     */
    public void createNewBabyFile(){

        try{
            File newBaby = new File(getApplicationContext().getFilesDir(), nameText.getText()+".txt");
            newBaby.createNewFile();
            FileWriter fw = new FileWriter(newBaby);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(nameText.getText() + "\n");
            bw.write(getGender() + "\n");
            bw.write(dateText.getText().toString() + "\n");
            bw.flush();
            bw.close();

            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

        }catch(IOException e){
            e.printStackTrace();
        }

    }
    /**
     * Informs of the gender of the baby
     * @return 0 if the baby is a boy and 1 if the baby is a girl
     */
    public int getGender(){
        int gender;
        if(boy.isChecked()) gender = 0;
        else gender = 1;
        return gender;
    }

}
