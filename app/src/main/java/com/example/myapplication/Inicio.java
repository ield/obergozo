package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

public class Inicio extends AppCompatActivity {

    private Button bNewBaby;
    private Button bOtherBaby;

    private Button bBaby1;
    private Button bBaby2;
    private Button bBaby3;
    private Button bBaby4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        confBOtherBaby();
        confBNewBaby();




    }

    private void confBOtherBaby(){
        bOtherBaby = (Button) findViewById(R.id.bOtherBaby);
        bOtherBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Inicio.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

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


}
