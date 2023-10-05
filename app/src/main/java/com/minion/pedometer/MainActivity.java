package com.minion.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    String[] wght = { "Kg", "Pounds"};
    String[] hght = { "Cm", "Feet"};
    List<String> w = new ArrayList<String>();
    List<String> h = new ArrayList<String>();
    boolean gender = false;
    boolean flag = false;
    boolean iscolor = true;
    boolean decolor = false;
    LinearLayout[] img = new LinearLayout[3];
    TextView[] txt = new TextView[3];
    LinearLayout[] ll = new LinearLayout[3];
    EditText mname,mage,mweight,mheight,g;
    Button next;
    String name,age,weight,height,gender_name,weight_unit,height_unit;
    Spinner weight_spin,height_spin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.appbackground));
        }

        txt[0] = findViewById(R.id.male_txt);
        txt[1] = findViewById(R.id.female_txt);
        txt[2] = findViewById(R.id.other_txt);
        img[0] = findViewById(R.id.male_pic);
        img[1] = findViewById(R.id.female_pic);
        img[2] = findViewById(R.id.other_pic);
        ll[0] = findViewById(R.id.male);
        ll[1] = findViewById(R.id.female);
        ll[2] = findViewById(R.id.other);

        mname = findViewById(R.id.name);
        mage = findViewById(R.id.age);
        mweight = findViewById(R.id.weight);
        mheight = findViewById(R.id.height);
        next = findViewById(R.id.next_button);
        g = findViewById(R.id.gender);
        weight_spin = (Spinner) findViewById(R.id.weight_spinner);
        height_spin = (Spinner) findViewById(R.id.height_spinner);

        for (int i = 0; i < wght.length; i++) {
            w.add(wght[i]);
        }
        for (int i = 0; i < hght.length; i++) {
            h.add(hght[i]);
        }


        ll[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = true;
                gender_name = "Male";
                selected(0);
                notselected(1);
                notselected(2);
            }
        });

        ll[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = true;
                gender_name = "Female";
                selected(1);
                notselected(0);
                notselected(2);
            }
        });

        ll[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = true;
                gender_name = "Other";
                selected(2);
                notselected(1);
                notselected(0);
            }
        });



        String prevStarted = "yes";
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!sharedpreferences.getBoolean(prevStarted, false)) {

        } else {
            moveToSecondary();
        }


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( mname.getText().toString().length() == 0){
                    mname.setError( "Name is required!" );
                }else if ( mage.getText().toString().length() == 0){
                    mage.setError( "Age is required!" );
                }else if ( mweight.getText().toString().length() == 0){
                    mweight.setError( "Weight is required!" );
                }else if ( mheight.getText().toString().length() == 0){
                    mheight.setError( "Height is required!" );
                }else if ( gender==false){
                    g.setError( "Gender is required!" );
                } else{
                    flag=true;
                }

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(prevStarted, Boolean.TRUE);
                editor.apply();

                if(flag==true){
                    name = mname.getText().toString();
                    age = mage.getText().toString();
                    weight = mweight.getText().toString();
                    height = mheight.getText().toString();
                    SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedPref.edit();
                    edit.putString("name",name);
                    edit.putString("age",age);
                    edit.putString("gender",gender_name);
                    edit.putString("weight",weight);
                    edit.putString("height",height);
                    edit.putString("weight_unit",weight_unit);
                    edit.putString("height_unit",height_unit);
                    edit.apply();
                    Intent i = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(i);
                }


            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_layout, wght);
        adapter.setDropDownViewResource(R.layout.spinner_layout_dropdown);
        weight_spin.setAdapter(adapter);

        weight_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                weight_unit = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.spinner_layout, hght);
        ad.setDropDownViewResource(R.layout.spinner_layout_dropdown);
        height_spin.setAdapter(ad);

        height_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                height_unit = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


    }

    void selected(int index)
    {
        ColorStateList csl = new ColorStateList(new int[][]{{}}, new int[]{Color.parseColor("#1364BF")});
        img[index].setBackgroundTintList(csl);
        txt[index].setTextColor(Color.parseColor("#1364BF"));

    }
    void notselected(int index)
    {
        ColorStateList csl = new ColorStateList(new int[][]{{}}, new int[]{Color.parseColor("#FAFAFA")});
        img[index].setBackgroundTintList(csl);
        txt[index].setTextColor(Color.parseColor("#070D3D"));
    }

    public void moveToSecondary(){
        Intent i = new Intent(this,SecondActivity.class);
        startActivity(i);
    }
}