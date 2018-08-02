package com.example.maple.locationupdatefrequent.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maple.locationupdatefrequent.Activity.QuestionsDisplay;
import com.example.maple.locationupdatefrequent.Models.Admin;
import com.example.maple.locationupdatefrequent.Models.Reports;
import com.example.maple.locationupdatefrequent.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

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
    DisplayImageOptions options;

    public GetUserReportsAdapter(ArrayList<Reports> reports, int check_single, Context applicationContext) {
        this.context = applicationContext;
        this.Rowlayout = check_single;
        this.reports = reports;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher_round)
                .showImageForEmptyUri(R.drawable.clear)
                .showImageOnFail(R.drawable.clear)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
    }


    @Override
    public CheckIn onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(Rowlayout, parent, false);
        return new CheckIn(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final CheckIn holder, final int position) {
        String original = reports.get(position).getMessagedescription();
        String[] lines = original.split("\\r?<br/><br/>");
        System.out.println("lines count " + lines.length);
        for (String line : lines) {
            System.out.println(line);
            String[] sublines = line.split("\\r?<br/>");
            if (sublines.length > 1) {
                holder.message_tv.setText(sublines[0]);
                holder.answer_tv.setText(sublines[1]);
                reports.get(position).setType("yes");
            } else {
                holder.message_tv.setText(sublines[0]);
                holder.answer_tv.setText("No Answer");
                reports.get(position).setType("no");
            }
            break;
        }
/*
        for (String line : lines) {
            System.out.println(line);
            String[] sublines = line.split("\\r?<br/>");
            String answer="";
            for (String subline : sublines) {
                holder.message_tv.setText(subline);
                break;
            }
            break;
        }
*/

        //holder.message_tv.setText(reports.get(position).getMessagedescription());

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

        holder.card_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reports.get(position).getType().equals("no")) {
                    Toast.makeText(context.getApplicationContext(), "good", Toast.LENGTH_SHORT).show();
                } else {
                    Intent questions = new Intent(context, QuestionsDisplay.class);
                    questions.putExtra("questions", reports.get(position).getMessagedescription());
                    questions.putExtra("image",reports.get(position).getPhoto());
                    questions.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(questions);
                }
            }
        });
        if (reports.get(position).getPhoto().equals("")) {
            holder.statsu_img.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.statsu_img.setBackground(context.getResources().getDrawable(R.drawable.imgnoavailable));

        } else {
            ImageLoader.getInstance()
                    .displayImage("http://125.62.194.181/tracker/" + reports.get(position).getPhoto(), holder.statsu_img, options);
        }

    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class CheckIn extends RecyclerView.ViewHolder {
        TextView message_tv, status_tv, answer_tv;
        ImageView statsu_img;
        CardView card_question;

        public CheckIn(View itemView) {
            super(itemView);
            card_question = itemView.findViewById(R.id.card_question);
            answer_tv = itemView.findViewById(R.id.answer_tv);
            statsu_img = itemView.findViewById(R.id.reportstatsu_img);
            message_tv = itemView.findViewById(R.id.reportmessage_tv);
            status_tv = itemView.findViewById(R.id.reportstatus_tv);
        }
    }

}
