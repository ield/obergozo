package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.widget.Button;

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
        if(files == null) return;

        for(int i = 0; i<Math.min(files.length, arrayBabies.size()); i++){
            String babyName = files[i].substring(0, files[i].length()-4);
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


}
