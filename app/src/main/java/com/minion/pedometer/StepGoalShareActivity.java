package com.minion.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StepGoalShareActivity extends AppCompatActivity {

    ImageView imgResultImage;
    Integer steps;
    String cal,dist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_goal_share);
        Button share = findViewById(R.id.share);
        final LinearLayout layout_view = findViewById(R.id.layout_view);
        final ImageView imgResultImage = findViewById(R.id.imgResultImage);
        TextView date = findViewById(R.id.date);
        TextView step_goals = findViewById(R.id.OnedayStepGoal);
        TextView calories = findViewById(R.id.calories);
        TextView distance = findViewById(R.id.distance);


        SharedPreferences shared = getSharedPreferences("steps_share", MODE_PRIVATE);
        steps = shared.getInt("steps",0);
        cal = shared.getString("calories", "");
        dist= shared.getString("distance", "");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        DateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy", Locale.getDefault());
        String yesterdayAsString = dateFormat.format(calendar.getTime());
        date.setText(yesterdayAsString);
        calories.setText(cal);
        distance.setText(dist);
        step_goals.setText(String.valueOf(steps));

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap image = Bitmap.createBitmap(layout_view.getWidth(),layout_view.getHeight(),Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(image);
                layout_view.draw(canvas);
                imgResultImage.setImageBitmap(image);
                BitmapDrawable drawable = (BitmapDrawable) imgResultImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"tittle",null);
                Uri imageUri = Uri.parse(bitmapPath);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(shareIntent, "send"));

            }
        });







    }
}