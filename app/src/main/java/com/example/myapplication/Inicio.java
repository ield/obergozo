package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class Inicio extends AppCompatActivity {

    private Button bNewBaby;
    private Button bOtherBaby;

    private Button bBaby1;
    private Button bBaby2;
    private Button bBaby3;
    private Button bBaby4;

    private List<Button> arrayBabies = new ArrayList<Button>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        setTitle("Inicio");

        bBaby1 = (Button) findViewById(R.id.bBaby1);
        arrayBabies.add(bBaby1);
        bBaby2 = (Button) findViewById(R.id.bBaby2);
        arrayBabies.add(bBaby2);
        bBaby3 = (Button) findViewById(R.id.bBaby3);
        arrayBabies.add(bBaby3);
        bBaby4 = (Button) findViewById(R.id.bBaby4);
        arrayBabies.add(bBaby4);

        checkBabies();

        confBOtherBaby();
        confBNewBaby();

        setTitle("Inicio");

        createTestBabyFile();//This file is used for tests

    }

    private void confBOtherBaby(){
        bOtherBaby = (Button) findViewById(R.id.bOtherBaby);
        bOtherBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Inicio.this, InsertDataUnsaved.class);
                startActivity(i);
            }
        });
    }

    /**
     * When it is wanted to add a new baby it goes to a different class
     */
    private void confBNewBaby(){
        bNewBaby = (Button) findViewById(R.id.bNewBaby);
        bNewBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Inicio.this, NewBaby.class);
                startActivity(i);
            }
        });
    }

    /**
     * Checks the saved babies
     * Checks all the files and for every file, it sets visible a button with the name of the baby.
     * Como ver directorios: https://developer.android.com/studio/debug/device-file-explorer
     *
     */
    private void checkBabies(){
        String[] files = getApplicationContext().fileList();
        String[] txtFiles = getTxt(files);
        if(txtFiles == null) return;

        for(int i = 0; i<Math.min(txtFiles.length, arrayBabies.size()); i++){
            String babyName = txtFiles[i].substring(0, txtFiles[i].length()-4);
            arrayBabies.get(i).setText(babyName);
            arrayBabies.get(i).setVisibility(View.VISIBLE);

            final int finalI = i;
            arrayBabies.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Inicio.this, InsertDataSaved.class);
                    Bundle b = new Bundle();
                    b.putSerializable("babyNumber", finalI);
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }
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
     * Test file created for future checks
     */
    public void createTestBabyFile(){

        try{
            File newBaby = new File(getApplicationContext().getFilesDir(), "BabyTest.txt");
            newBaby.createNewFile();
            FileWriter fw = new FileWriter(newBaby);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("BebeTest" + "\n");
            bw.write(0 + "\n");
            bw.write("1/4/2019" + "\n");
            bw.write("1/4/2020 0 51 3.5 35 14 " + "\n");
            bw.write("1/4/2020 0.25 61 7 42 18 " + "\n");
            bw.write("1/5/2020 0.5 67 8 45 17 " + "\n");
            bw.write("1/5/2020 0.75 72 10 46 18 " + "\n");
            bw.flush();
            bw.close();

        }catch(IOException e){
            e.printStackTrace();
        }

    }


}
