package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private TextView FechaNacimiento;
    private EditText dateText;

    private TextView sexo;
    private CheckBox chica;
    private CheckBox chico;

    private TextView Longitud;
    private EditText longitudText;

    private TextView peso;
    private EditText pesoText;

    private TextView perCraneal;
    private EditText perCranealText;

    private Button bCalcula;
    private Button bPlotLength;
    private Button bPlotWeigh;
    private Button bPlotCranial;
    private Button bPlotIMC;

    private TextView ResLongitud;
    private TextView ResPeso;
    private TextView ResPerimetroCraneal;
    private TextView ResIMC;

    private TextView testDato;

    Calendar today = Calendar.getInstance();
    private String falloFecha = "Fecha no válida";
    private String falloCheckBox = "Marca si es chico o chica";
    private String falloLongitud = "Introduce la longitud";
    private String falloPeso = "Introduce el peso";
    private String falloPC = "Introduce el diámetro craneal";

    //Esto es para la prueba de enviar un dato a la otra clase y que despues haga el plot
    private Dato d = new Dato();
    private double[] medidas = new double[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FechaNacimiento = (TextView) findViewById(R.id.FechaNacimineto);
        dateText = (EditText) findViewById(R.id.dateText);
        dateText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new DatePickerDialog(MainActivity.this, date, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();

            }});

        sexo = (TextView) findViewById(R.id.sexo);
        chica = (CheckBox) findViewById(R.id.chica);
        chica.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(chica.isChecked()) chico.setChecked(false);
            }});
        chico = (CheckBox) findViewById(R.id.chico);
        chico.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(chico.isChecked()) chica.setChecked(false);
            }});

        Longitud = (TextView) findViewById(R.id.Longitud);
        longitudText = (EditText) findViewById(R.id.longitudText);

        peso = (TextView) findViewById(R.id.peso);
        pesoText = (EditText) findViewById(R.id.pesoText);

        perCraneal = (TextView) findViewById(R.id.perCraneal);
        perCranealText = (EditText) findViewById(R.id.perCranealText);

        ResLongitud = (TextView) findViewById(R.id.ResLongitud);
        ResPeso = (TextView) findViewById(R.id.ResPeso);
        ResPerimetroCraneal = (TextView) findViewById(R.id.ResPerimetroCraneal);
        ResIMC = (TextView) findViewById(R.id.ResIMC);


        configuraBCalcula();
        confBPlotLength();
        confBPlotWeigh();
        confBPlotCranial();
        confBPlotIMC();


    }

    /**
     * Este metodo se usa para decir lo que pasa una vez se coloca una fecha en la caja de texto de la fecha
     */
    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day){
            dateText.setText(day+"/"+(month+1)+"/"+year);
        }
    };

    public void configuraBCalcula(){
        bCalcula = (Button) findViewById(R.id.bCalcula);
        bCalcula.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //A continuacion vienen los posibles errores que nos podemos encontrar
                if(!fechaValida() || dateText.getText()+"" == ""){
                    Toast toast = Toast.makeText(getApplicationContext(), falloFecha, Toast.LENGTH_SHORT);
                    toast.show();
                    ResLongitud.setText("");
                    ResPeso.setText("");
                    ResPerimetroCraneal.setText("");
                    ResIMC.setText("");
                }

                else if(!chica.isChecked() && !chico.isChecked()){
                    Toast toast = Toast.makeText(getApplicationContext(), falloCheckBox, Toast.LENGTH_SHORT);
                    toast.show();
                    ResLongitud.setText("");
                    ResPeso.setText("");
                    ResPerimetroCraneal.setText("");
                    ResIMC.setText("");
                }
                else if(longitudText.getText()+"" == ""){
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


                else {
                    //Lo primero se pasan las medidas, porque siempre se va a pasar todo
                    medidas[0] = Double.parseDouble(longitudText.getText().toString());
                    medidas[1] = Double.parseDouble(pesoText.getText().toString());
                    medidas[2] = Double.parseDouble(perCranealText.getText().toString());

                    double age = getAge();

                    Calculator c1 =  new Calculator(age, medidas, getGender());
                    int[] percentiles = c1.getPerc();

                    ResLongitud.setText("El percentil de longitud es: "+percentiles[0]);
                    ResPeso.setText("El percentil de peso es: "+percentiles[1]);
                    ResPerimetroCraneal.setText("El percentil de perímetro craneal es: "+percentiles[2]);

                    if (chica.isChecked()) {
                        ResIMC.setText(calculaIMCChica());
                    } else if (chico.isChecked()) {
                        ResIMC.setText(calculaIMCChico());
                    }

                    d.setYears(age);
                    d.setMeasures(medidas);
                }
            }

        });
    }

    public void confBPlotLength(){
        bPlotLength = (Button) findViewById(R.id.bPlotLength);
        bPlotLength.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this, Graph.class);
                int gender = getGender();

                Bundle bundle = new Bundle();
                bundle.putSerializable("dato", d); //donde "dato" es la clave y dato el objeto que se va a pasar
                bundle.putSerializable("gender", gender);
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
                Intent i = new Intent(MainActivity.this, Graph.class);
                int gender = getGender();

                Bundle bundle = new Bundle();
                bundle.putSerializable("dato", d); //donde "dato" es la clave y dato el objeto que se va a pasar
                bundle.putSerializable("gender", gender);
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
                Intent i = new Intent(MainActivity.this, Graph.class);
                int gender = getGender();

                Bundle bundle = new Bundle();
                bundle.putSerializable("dato", d); //donde "dato" es la clave y dato el objeto que se va a pasar
                bundle.putSerializable("gender", gender);
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
                Intent i = new Intent(MainActivity.this, Graph.class);
                int gender = getGender();

                Bundle bundle = new Bundle();
                bundle.putSerializable("dato", d); //donde "dato" es la clave y dato el objeto que se va a pasar
                bundle.putSerializable("gender", gender);
                bundle.putSerializable("magnitude", 3);
                i.putExtras(bundle);

                startActivity(i);

            }

        });
    }


    public int getGender(){
        int gender;
        if(chico.isChecked()) gender = 0;
        else gender = 1;
        return gender;
    }

    

    public String calculaIMCChica(){
        double age = getAge();

        CalculadoraIMCChicas c1 = new CalculadoraIMCChicas(age, Double.parseDouble(pesoText.getText().toString()),Double.parseDouble(longitudText.getText().toString()));
        d.setIMC(c1.getMedida());

        return "El percentil de IMC es: "+Math.round(c1.getPercentil()*100.0);

    }

    public String calculaIMCChico(){

        double age = getAge();


        CalculadoraIMCChicos c1 = new CalculadoraIMCChicos(age, Double.parseDouble(pesoText.getText().toString()),Double.parseDouble(longitudText.getText().toString()));
        d.setIMC(c1.getMedida());
        return "El percentil de IMC es: "+Math.round(c1.getPercentil()*100.0);

    }


    public boolean fechaValida(){
        int[] nacimiento = convierteFechaArray(dateText.getText().toString());
        int[] hoy = {today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH)+1, today.get(Calendar.YEAR)};

        if(nacimiento[2]>hoy[2] || nacimiento[2]<hoy[2]-18) return false;
        if(nacimiento[2]==hoy[2] && nacimiento[1]>hoy[1]) return false;
        if(nacimiento[2]==hoy[2] && nacimiento[1]==hoy[1] && nacimiento[0]>hoy[0] ) return false;

        if(nacimiento[1]<1 || nacimiento [1]>12 || nacimiento[0]<1) return false;
        if(nacimiento[1]==2 && nacimiento[0]>29) return false;
        if(nacimiento[1]==2 && nacimiento[0]>28 && !(nacimiento[2] % 400 == 0 || (nacimiento[2] % 4 == 0 && nacimiento[2] % 100 != 0))) return false;
        if((nacimiento[1] == 4 || nacimiento[1] == 6 || nacimiento[1] == 9 || nacimiento[1] == 11) && nacimiento[0]>30) return false;
        if(nacimiento[0]>31) return false;

        return true;

    }

    public double getAge(){
        int[] hoy = {today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH)+1, today.get(Calendar.YEAR)};
        String nacimiento = dateText.getText().toString();
        int[] nac = convierteFechaArray(nacimiento);
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
