package com.example.maple.locationupdatefrequent.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.Models.Admin;
import com.example.maple.locationupdatefrequent.Models.Reports;
import com.example.maple.locationupdatefrequent.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetUserReportsAdapter extends RecyclerView.Adapter<GetUserReportsAdapter.CheckIn> {
    ArrayList<Reports> reports;
    int Rowlayout;
    Context context;

    public GetUserReportsAdapter(ArrayList<Reports> reports, int check_single, Context applicationContext) {
        this.context = applicationContext;
        this.Rowlayout = check_single;
        this.reports = reports;
    }


    @Override
    public CheckIn onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(Rowlayout, parent, false);
        return new CheckIn(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(CheckIn holder, final int position) {
        holder.message_tv.setText(reports.get(position).getMessagedescription());

        DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a ");
        Date date;
        try {
            date = originalFormat.parse(reports.get(position).getMessagedatetime());
            String formattedDate = targetFormat.format(date);  // 20120821
            holder.status_tv.setText(formattedDate);
        } catch (ParseException e) {
            holder.status_tv.setText(reports.get(position).getMessagedatetime());
            e.printStackTrace();
        }

        holder.message_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String original = reports.get(position).getMessagedescription();

                StringTokenizer tokens = new StringTokenizer(original, "<br/><br/>");
                for (int i=0;i<tokens.countTokens();i++){
                    System.out.println(tokens.nextToken());
                }
               /* String first = tokens.nextToken();// this will contain "Fruit"
                String second = tokens.nextToken();
                System.out.println(first);
                System.out.println(second);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class CheckIn extends RecyclerView.ViewHolder {
        TextView message_tv, status_tv;
        ImageView statsu_img;

        public CheckIn(View itemView) {
            super(itemView);
            statsu_img = (ImageView) itemView.findViewById(R.id.reportstatsu_img);
            message_tv = (TextView) itemView.findViewById(R.id.reportmessage_tv);
            status_tv = (TextView) itemView.findViewById(R.id.reportstatus_tv);
        }
    }

}
