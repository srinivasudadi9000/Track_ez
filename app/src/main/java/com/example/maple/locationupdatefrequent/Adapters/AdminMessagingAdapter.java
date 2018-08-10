package com.example.maple.locationupdatefrequent.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.Activity.QD;
import com.example.maple.locationupdatefrequent.Models.Admin;
import com.example.maple.locationupdatefrequent.Models.Messaging;
import com.example.maple.locationupdatefrequent.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

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
    public void onBindViewHolder(final CheckIn holder, int position) {
        holder.message_tv.setText(admins.get(position).getMessage());
        holder.message_tv.setSelected(true);

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

        if (holder.message_tv.getText().length() > 25) {
            holder.adminstatsu_txt.setVisibility(View.VISIBLE);
        }
        holder.adminstatsu_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qd = new Intent(context, QD.class);
                qd.putExtra("question", "  Message ");
                qd.putExtra("answer", holder.message_tv.getText().toString() + "\n" + holder.status_tv.getText().toString());
                qd.putExtra("state", "message");
                qd.setFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(qd);
            }
        });

    }

    @Override
    public int getItemCount() {
        return admins.size();
    }

    public class CheckIn extends RecyclerView.ViewHolder {
        TextView message_tv, status_tv;
        TextView adminstatsu_txt;

        public CheckIn(View itemView) {
            super(itemView);
            adminstatsu_txt = itemView.findViewById(R.id.adminstatsu_txt);
            message_tv = itemView.findViewById(R.id.adminmessage_tv);
            status_tv = itemView.findViewById(R.id.adminstatus_tv);
        }
    }

}
