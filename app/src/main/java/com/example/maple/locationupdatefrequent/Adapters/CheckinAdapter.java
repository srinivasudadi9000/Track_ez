package com.example.maple.locationupdatefrequent.Adapters;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.Models.Checkins;
import com.example.maple.locationupdatefrequent.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class CheckinAdapter extends RecyclerView.Adapter<CheckinAdapter.CheckIn> {
  ArrayList<Checkins> checkIns;
  int Rowlayout;
  Context context;
    public CheckinAdapter(ArrayList<Checkins> checkins, int check_single, Context applicationContext) {
        this.context = applicationContext; this.Rowlayout = check_single;this.checkIns = checkins;
    }

    @Override
    public CheckIn onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(Rowlayout, parent, false);
        return new CheckIn(view);

    }

    @SuppressLint({"NewApi", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(CheckIn holder, int position) {


        holder.lng_tv.setText("Lng : "+checkIns.get(position).getLatitude());
        SharedPreferences se = context.getSharedPreferences("Userdetails", MODE_PRIVATE);
        String cs =checkIns.get(position).getRun();
        System.out.println("srinivasu  "+cs);
        switch (cs){
            case "0":
                holder.view_ld.setBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            case "1":
                holder.view_ld.setBackgroundColor(context.getResources().getColor(R.color.yellow));
                break;
            case "2":
                holder.view_ld.setBackgroundColor(context.getResources().getColor(R.color.forestgreen));
                break;
        }

        holder.lat_tv.setText("Lat : "+checkIns.get(position).getLongitude());

        DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a ");
        Date date;
        try {
            date = originalFormat.parse(checkIns.get(position).getCdate());
            String formattedDate = targetFormat.format(date);  // 20120821
            holder.cdt_tv.setText(formattedDate);
        } catch (ParseException e) {
            holder.cdt_tv.setText(checkIns.get(position).getCdate());
            e.printStackTrace();
        }


        if (checkIns.get(position).getStatus().equals("local")){
            holder.status_tv.setText("Updated Locally !");
            holder.statsu_img.setImageDrawable(context.getResources().getDrawable(R.drawable.local));
        }else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                holder.status_tv.setTextColor(context.getColor(R.color.forestgreen));
                holder.status_tv.setText("Updated To Server !!");
            }else {
                holder.status_tv.setTextColor(R.color.forestgreen);
                holder.status_tv.setText("Updated To Server !!");
            }

            holder.statsu_img.setImageDrawable(context.getResources().getDrawable(R.drawable.done_green));

        }



        Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.slidein);
        if (position %2 ==0){
            animation.setDuration(1000);
        }else {
            animation.setDuration(500);
        }
        holder.single_checkin_cv.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return checkIns.size();
    }

    public class CheckIn extends RecyclerView.ViewHolder{
       TextView lat_tv,lng_tv,cdt_tv,status_tv;
       ImageView statsu_img;
       CardView single_checkin_cv;
       View view_ld;
        public CheckIn(View itemView) {
            super(itemView);
            view_ld = itemView.findViewById(R.id.view_ld);
            single_checkin_cv = itemView.findViewById(R.id.single_checkin_cv);
            statsu_img = itemView.findViewById(R.id.statsu_img);
            cdt_tv = itemView.findViewById(R.id.cdt_tv);
            status_tv = itemView.findViewById(R.id.status_tv);
            lat_tv = itemView.findViewById(R.id.lat_tv);
            lng_tv = itemView.findViewById(R.id.lng_tv);
        }
    }
}
