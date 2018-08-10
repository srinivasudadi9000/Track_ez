package com.example.maple.locationupdatefrequent.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.GeoFencingDemo;
import com.example.maple.locationupdatefrequent.Helper.DBHelper;
import com.example.maple.locationupdatefrequent.Helper.Typewriter;
import com.example.maple.locationupdatefrequent.R;

public class Home extends Activity implements View.OnClickListener{
TextView connect_tv;
Button okay_btn;
Typewriter network_tv;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        okay_btn = findViewById(R.id.okay_btn);
        connect_tv = findViewById(R.id.connect_tv);
        connect_tv.setSelected(true);
      /*  Animation marquee = AnimationUtils.loadAnimation(this, R.anim.marquee);
        connect_tv.startAnimation(marquee);*/
      //  okay_btn.setOnClickListener(this);
        network_tv = findViewById(R.id.network_tv);
        network_tv.setCharacterDelay(150);
        network_tv.animateText("Network Setup");

        DBHelper dbHelper = new DBHelper(Home.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent dashboard = new Intent(Home.this,Dashboard.class);
               // Intent dashboard = new Intent(Home.this,GeoFencingDemo.class);
                SharedPreferences s = getSharedPreferences("Userdetails",MODE_PRIVATE);
                if (s.getString("deviceno","").toString().length()>1){
                    Intent dashboard = new Intent(Home.this,GeoFencingDemo.class);
                    startActivity(dashboard);
                    finish();
                }else {
                    Intent dashboard = new Intent(Home.this,EditPhone.class);
                    startActivity(dashboard);
                    finish();
                }
            }
        },5000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.okay_btn:
                Intent dashboard = new Intent(Home.this,Dashboard.class);
                startActivity(dashboard);
                break;
        }
    }

}
