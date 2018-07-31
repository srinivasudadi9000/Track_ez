package com.example.maple.locationupdatefrequent.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.Models.Admin;
import com.example.maple.locationupdatefrequent.Models.Messaging;
import com.example.maple.locationupdatefrequent.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminMessagingAdapter extends RecyclerView.Adapter<AdminMessagingAdapter.CheckIn> {
    ArrayList<Admin> admins;
    int Rowlayout;
    Context context;

    public AdminMessagingAdapter(ArrayList<Admin> admins, int check_single, Context applicationContext) {
        this.context = applicationContext;
        this.Rowlayout = check_single;
        this.admins = admins;
    }


    @Override
    public CheckIn onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(Rowlayout, parent, false);
        return new CheckIn(view);

    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(CheckIn holder, int position) {
        holder.message_tv.setText(admins.get(position).getMessage());

        DateFormat originalFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a ");
        Date date;
        try {
            date = originalFormat.parse(admins.get(position).getMessageDateTime());
            String formattedDate = targetFormat.format(date);  // 20120821
            holder.status_tv.setText(formattedDate);
        } catch (ParseException e) {
            holder.status_tv.setText(admins.get(position).getMessageDateTime());
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return admins.size();
    }

    public class CheckIn extends RecyclerView.ViewHolder {
        TextView message_tv, status_tv;
        ImageView statsu_img;

        public CheckIn(View itemView) {
            super(itemView);
            statsu_img = (ImageView) itemView.findViewById(R.id.adminstatsu_img);
            message_tv = (TextView) itemView.findViewById(R.id.adminmessage_tv);
            status_tv = (TextView) itemView.findViewById(R.id.adminstatus_tv);
        }
    }

}
