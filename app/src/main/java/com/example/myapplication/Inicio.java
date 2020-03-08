package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

public class Inicio extends AppCompatActivity {

    private Button bNuevoBebe;
    private Button bOtroBebe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        configuraBOtroBebe();


    }

    private void configuraBOtroBebe(){
        bOtroBebe = (Button) findViewById(R.id.bOtroBebe);
        bOtroBebe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Inicio.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

}
