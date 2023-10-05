package com.minion.pedometer;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SecondActivity extends AppCompatActivity {

    boolean flag = true,one_day=false;
    ImageButton btn, close;
    Button share;

    TextView text, steps, step_goals,step_count,time_hrs,distance,calories;
    Integer stepCount = 0,onedayStep=0;
    double MagnitudePrevious;
    ProgressBar simpleProgressBar;
    double dist,cal,onedayCal,onedayDist;
    long starttime = 0;
    TextView date;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView nav_button,profile;
    TextView age,weight,height,weight_unit,height_unit;
    String sname,sage,sgender,sweight,sheight,weightunit,heightunit;
    

    Spinner sens;
    String[] sensitivity = {"High","Medium","Low"};
    String sensitivity_level;
    Integer isteps = 500 ;
    Integer sensitivity_range = 30;
    final List<String> s = new ArrayList<String>();
    ImageView edit,edit_step_goal;
    EditText stepGoals;
    LinearLayout gif;
    Integer i,sum;
    Float avg;
    TextView Time;
    String day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.appbackground));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel (channel);
        }

        nav_button = findViewById(R.id.nav_button);



        nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        SharedPreferences shared= getSharedPreferences("myKey", MODE_PRIVATE);
        sname = shared.getString("name","");
        sage = shared.getString("age","");
        sgender = shared.getString("gender","");
        sweight = shared.getString("weight","");
        sheight = shared.getString("height","");
        weightunit = shared.getString("weight_unit","");
        heightunit = shared.getString("height_unit","");


        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        TextView navUsername = (TextView) headerView.findViewById(R.id.name);
        profile = headerView.findViewById(R.id.profile);
        age = headerView.findViewById(R.id.age);
        weight = headerView.findViewById(R.id.weight);
        height = headerView.findViewById(R.id.height);
        height_unit = headerView.findViewById(R.id.height_unit);
        weight_unit = headerView.findViewById(R.id.weight_unit);
        edit = headerView.findViewById(R.id.edit);
        navUsername.setText(sname);
        age.setText(sage);
        weight.setText(sweight);
        height.setText(sheight);
        weight_unit.setText(weightunit);
        height_unit.setText(heightunit);


        sens = (Spinner) headerView.findViewById(R.id.sensitivity);

        for (int i = 0; i < sensitivity.length; i++) {
            s.add(sensitivity[i]);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.activity_more_spinner_layout, sensitivity);
        adapter.setDropDownViewResource(R.layout.activity_more_spinner_layout_dropdown);
        sens.setAdapter(adapter);

        sens.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int i, long id) {
                String ssensitivity =  adapterView.getItemAtPosition(i).toString();
                SharedPreferences sharedPref = getSharedPreferences("step", MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.putInt("position", i);
                edit.putString("sensitivity",ssensitivity);
                edit.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        SharedPreferences  sharedPref = getSharedPreferences("step", MODE_PRIVATE);
        int position = sharedPref.getInt("position",0);
        sensitivity_level = sharedPref.getString("sensitivity","");
        sens.setSelection(position);

        if(sensitivity_level.equals("high")){
            sensitivity_range = 16;
        }else if(sensitivity_level.equals("Medium")){
            sensitivity_range = 25;
        }else if(sensitivity_level.equals("Low")){
            sensitivity_range = 30;
        }


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SecondActivity.this, MainActivity.class);
                String prevStarted = "yes";
                SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(prevStarted, Boolean.FALSE);
                editor.apply();
                startActivity(intent1);
            }
        });


        btn = findViewById(R.id.pass_play_button);
        text = findViewById(R.id.state);
        step_count = findViewById(R.id.step_count);
        distance = findViewById(R.id.distance);
        calories = findViewById(R.id.calories);
        gif = findViewById(R.id.gif);
        edit_step_goal = findViewById(R.id.edit_step_goal);

        Time = findViewById(R.id.time);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String time = new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(date.getTime());
        if(time.equals("4:32 PM")){
            Time.setText("hi");
        }



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String prevStarted = "yes";
                SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                if (!sharedpreferences.getBoolean(prevStarted, false)) {
                    flag = false;
                    text.setText("PASSED");
                    gif.setVisibility(View.GONE);
                    btn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(prevStarted, Boolean.TRUE);
                    editor.apply();
                } else {
                    flag = true;
                    text.setText("ACTIVE");
                    gif.setVisibility(View.VISIBLE);
                    btn.setImageResource(R.drawable.ic_baseline_pause_24);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(prevStarted, Boolean.FALSE);
                    editor.apply();
                }

            }
        });


        edit_step_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prevStarted = "yes";
                SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                if (!sharedpreferences.getBoolean(prevStarted, false)) {
                    steps.setEnabled(true);
                    steps.setTextColor(Color.parseColor("#CACACA"));
                    if (steps.getText().toString().trim().length() <= 2){
                        Toast.makeText(getBaseContext(), "Step Goals should 3 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }else if (steps.getText().toString().trim().length() >= 6){
                        Toast.makeText(getBaseContext(), "Step Goals should Maximum 5 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    edit_step_goal.setImageResource(R.drawable.save);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(prevStarted, Boolean.TRUE);
                    editor.apply();
                } else {
                    if (steps.getText().toString().trim().length() <= 2){
                        Toast.makeText(getBaseContext(), "Step Goals should 3 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }else if (steps.getText().toString().trim().length() >= 6){
                        Toast.makeText(getBaseContext(), "Step Goals should Maximum 5 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        isteps = Integer.valueOf(steps.getText().toString());
                        simpleProgressBar = findViewById(R.id.progress);
                        simpleProgressBar.setMax(isteps);
                        steps.setEnabled(false);
                        edit_step_goal.setImageResource(R.drawable.ic_baseline_edit_24);
                        steps.setTextColor(Color.parseColor("#070D3D"));
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(prevStarted, Boolean.FALSE);
                        editor.apply();
                    }

                }

            }
        });

        steps = findViewById(R.id.totalstep);

        simpleProgressBar = findViewById(R.id.progress);
        simpleProgressBar.setMax(isteps);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);




        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if (sensorEvent != null) {
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];


                    double Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                    double MagnitudeDelta = Magnitude - MagnitudePrevious ;
                    MagnitudePrevious = Magnitude;

                    if (MagnitudeDelta > 6){
                        if(flag==true){
                            stepCount++;
                            dist = (double) (stepCount / 1312.33595801);
                            cal = (double) (stepCount * 0.043);
                            simpleProgressBar.setProgress(stepCount);
                        }
                    }
                    onedayStep = stepCount;
                    onedayCal =  cal;
                    onedayDist = dist;
                    report(stepCount);
                    step_count.setText(String.valueOf(stepCount));
                    distance.setText(String.format("%.2f", dist));
                    calories.setText(String.format("%.2f", cal));


                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.putFloat("distance", (float) dist);
        editor.putFloat("calories", (float) cal);
        editor.apply();
    }

    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.putFloat("distance", (float) dist);
        editor.putFloat("calories", (float) cal);
        editor.apply();
    }

    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("stepCount", 0);
        dist = sharedPreferences.getFloat("distance", 0);
        cal = sharedPreferences.getFloat("calories", 0);
    }

    private void day() {
        SharedPreferences shared = getSharedPreferences("steps_share", MODE_PRIVATE);
        SharedPreferences.Editor edit = shared.edit();
        edit.putInt("steps", onedayStep);
        edit.putString("calories", String.format("%.2f", onedayCal));
        edit.putString("distance", String.format("%.2f", onedayDist));
        edit.apply();
        stepCount = 0;
        dist = 0;
        cal = 0;
        simpleProgressBar.setProgress(0);

    }
    private void Notification() {
        Intent intent = new Intent(this, SecondActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo_blue)
                        .setContentTitle("Yesterday Total Step Counts" + onedayStep.toString() + "Steps")
                        .setContentText("Check your weekly walking report !")
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());

    }



    private void report(Integer stepCount) {

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String dayName = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        ProgressBar progressBar_sat = findViewById(R.id.progress_bar_sat);
        ProgressBar progressBar_sun = findViewById(R.id.progress_bar_sun);
        ProgressBar progressBar_mon = findViewById(R.id.progress_bar_mon);
        ProgressBar progressBar_tue = findViewById(R.id.progress_bar_tue);
        ProgressBar progressBar_wed = findViewById(R.id.progress_bar_wed);
        ProgressBar progressBar_thu = findViewById(R.id.progress_bar_thu);
        ProgressBar progressBar_fri = findViewById(R.id.progress_bar_fri);

        TextView Average = findViewById(R.id.average);

        i = 0;
        sum = 0;

        if(dayName.equals("Monday")){
            i = 0;
            sum = 0;
        }

        if(dayName.equals("Monday")){
            i++;
            progressBar_sat.setProgress(0);
            progressBar_sun.setProgress(0);
            progressBar_mon.setProgress(0);
            progressBar_tue.setProgress(0);
            progressBar_wed.setProgress(0);
            progressBar_thu.setProgress(0);
            progressBar_fri.setProgress(0);
            progressBar_mon.setMax(isteps);
            progressBar_mon.setProgress(stepCount);
            LinearLayout ll = findViewById(R.id.ll_mon);
            if(isteps <= stepCount){
                ll.setBackgroundResource(R.drawable.ic_baseline_star_24);
            }else if (isteps > stepCount || stepCount==0){
                ll.setBackgroundColor(Color.parseColor("#FAFAFA"));
            }
            SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
            if (isFirstRun)
            {

                Notification();
                displayDialog();
                day();

                SharedPreferences.Editor editor = wmbPreference.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.commit();
            }else{
            }
        }else if(dayName.equals("Tuesday")){
            i++;
            progressBar_tue.setMax(isteps);
            progressBar_tue.setProgress(stepCount);
            LinearLayout ll = findViewById(R.id.ll_tue);
            if(isteps <= stepCount){
                ll.setBackgroundResource(R.drawable.ic_baseline_star_24);
            }else if (isteps > stepCount || stepCount==0){
                ll.setBackgroundColor(Color.parseColor("#FAFAFA"));
            }
            SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
            if (isFirstRun)
            {

                Notification();
                displayDialog();
                day();

                SharedPreferences.Editor editor = wmbPreference.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.commit();
            }else{
            }
        }else if(dayName.equals("Wednesday")){
            i++;
            progressBar_wed.setMax(isteps);
            progressBar_wed.setProgress(stepCount);
            LinearLayout ll = findViewById(R.id.ll_wed);
            if(isteps <= stepCount){
                ll.setBackgroundResource(R.drawable.ic_baseline_star_24);
            }else if (isteps > stepCount || stepCount==0){
                ll.setBackgroundColor(Color.parseColor("#FAFAFA"));
            }
            SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
            if (isFirstRun)
            {

                Notification();
                displayDialog();
                day();

                SharedPreferences.Editor editor = wmbPreference.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.commit();
            }else{
            }
        }else if(dayName.equals("Thursday")){
            i++;
            progressBar_thu.setMax(isteps);
            progressBar_thu.setProgress(stepCount);
            LinearLayout ll = findViewById(R.id.ll_thu);
            if(isteps <= stepCount){
                ll.setBackgroundResource(R.drawable.ic_baseline_star_24);
            }else if (isteps > stepCount || stepCount==0){
                ll.setBackgroundColor(Color.parseColor("#FAFAFA"));
            }
            SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
            if (isFirstRun)
            {

                Notification();
                displayDialog();
                day();

                SharedPreferences.Editor editor = wmbPreference.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.commit();
            }else{
            }
        }else if(dayName.equals("Friday")){
            i++;
            progressBar_fri.setMax(isteps);
            progressBar_fri.setProgress(stepCount);
            LinearLayout ll = findViewById(R.id.ll_fri);
            if(isteps <= stepCount){
                ll.setBackgroundResource(R.drawable.ic_baseline_star_24);
            }else if (isteps > stepCount || stepCount==0){
                ll.setBackgroundColor(Color.parseColor("#FAFAFA"));
            }
            SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
            if (isFirstRun)
            {
                Notification();
                displayDialog();
                day();

                SharedPreferences.Editor editor = wmbPreference.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.commit();
            }else{
            }
        }else if(dayName.equals("Saturday")){
            i++;
            progressBar_sat.setMax(isteps);
            progressBar_sat.setProgress(stepCount);
            LinearLayout ll = findViewById(R.id.ll_sat);
            if(isteps <= stepCount){
                progressBar_sat.setBackgroundResource(R.drawable.ic_baseline_star_24);
            }else if (isteps > stepCount || stepCount==0){
                progressBar_sat.setBackgroundResource(R.drawable.circular_shape);
            }
            SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
            if (isFirstRun)
            {

                Notification();
                displayDialog();
                day();

                SharedPreferences.Editor editor = wmbPreference.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.commit();
            }else{
            }
        }else if(dayName.equals("Sunday")){
            i++;
            progressBar_sun.setMax(isteps);
            progressBar_sun.setProgress(stepCount);
            LinearLayout ll = findViewById(R.id.ll_sun);
            if(isteps <= stepCount){
                ll.setBackgroundResource(R.drawable.ic_baseline_star_24);
            }else if (isteps > stepCount || stepCount==0){
                ll.setBackgroundColor(Color.parseColor("#FAFAFA"));
            }
            SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
            if (isFirstRun)
            {

                Notification();
                displayDialog();
                day();

                SharedPreferences.Editor editor = wmbPreference.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.commit();
            }else{
            }
        }
        if(i==2){
            one_day=true;
        }
        sum += stepCount;
        avg = (float) sum / i;
        Average.setText(String.format("%.2f",avg));
    }

    private void displayDialog() {
        Dialog dialog = new Dialog(SecondActivity.this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.day_step_count_display);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        step_goals = dialog.findViewById(R.id.OnedayStepGoal);
        share = dialog.findViewById(R.id.share);
        close = dialog.findViewById(R.id.close);
        date = dialog.findViewById(R.id.date);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        DateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy", Locale.getDefault());
        String yesterdayAsString = dateFormat.format(calendar.getTime());
        date.setText(yesterdayAsString);
        step_goals.setText(String.valueOf(onedayStep));
        share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, StepGoalShareActivity.class);
                startActivity(intent);

            }
        });
        dialog.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

    }

}

