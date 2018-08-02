package com.example.maple.locationupdatefrequent.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.Adapters.GetUserReportsAdapter;
import com.example.maple.locationupdatefrequent.Adapters.QuestionsAdapter;
import com.example.maple.locationupdatefrequent.GPSTracker;
import com.example.maple.locationupdatefrequent.Helper.ZoomableImageView;
import com.example.maple.locationupdatefrequent.Models.Question;
import com.example.maple.locationupdatefrequent.Models.Reports;
import com.example.maple.locationupdatefrequent.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuestionsDisplay extends Activity implements View.OnClickListener {
    TextView counter_tv;
    RecyclerView questions_recyler;
    ImageView msg_back_img, send_img, clear_img;
    EditText et_content;
    GPSTracker gps;
    String latitude, longitude;
    ArrayList<Question> questions;
    QuestionsAdapter questionsAdapter;
    ProgressDialog progress;
    ZoomableImageView myimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions);
        questions = new ArrayList<Question>();

        myimage = findViewById(R.id.myimage);
        BitmapDrawable drawable = (BitmapDrawable)  getBaseContext().getResources().getDrawable(R.drawable.car);
        Bitmap bitmap = drawable.getBitmap();
        myimage.setImageBitmap(bitmap);

        msg_back_img = findViewById(R.id.msg_back_img);
        msg_back_img.setOnClickListener(this);
        questions_recyler = findViewById(R.id.questions_recyler);
        questions_recyler.setLayoutManager(new LinearLayoutManager(this));


        String original = getIntent().getStringExtra("questions").toString();
        String[] lines = original.split("\\r?<br/><br/>");
        for (String line : lines) {
            System.out.println(line);
            String[] sublines = line.split("\\r?<br/>");
            questions.add(new Question(sublines[0],sublines[1]));

        }

        questionsAdapter = new QuestionsAdapter(questions, R.layout.questions_single, getApplicationContext());
        questions_recyler.setAdapter(questionsAdapter);
        questionsAdapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.msg_back_img:
                finish();
                break;
        }
    }



}
