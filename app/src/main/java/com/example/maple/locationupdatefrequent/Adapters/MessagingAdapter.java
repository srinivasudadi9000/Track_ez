package com.example.maple.locationupdatefrequent.Adapters;

import android.annotation.SuppressLint;
import android.arch.core.executor.TaskExecutor;
import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.Activity.Messages;
import com.example.maple.locationupdatefrequent.Models.Checkins;
import com.example.maple.locationupdatefrequent.Models.Messaging;
import com.example.maple.locationupdatefrequent.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessagingAdapter extends RecyclerView.Adapter<MessagingAdapter.CheckIn> {
    ArrayList<Messaging> messagings;
    int Rowlayout;
    Context context;

    public MessagingAdapter(ArrayList<Messaging> messagings, int check_single, Context applicationContext) {
        this.context = applicationContext;
        this.Rowlayout = check_single;
        this.messagings = messagings;
    }


    @Override
    public CheckIn onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(Rowlayout, parent, false);
        return new CheckIn(view);

    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final CheckIn holder, int position) {
        holder.message_tv.setText(messagings.get(position).getMessage());
        holder.message_tv.setSelected(true);
        DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a ");
        Date date;
        try {
            date = originalFormat.parse(messagings.get(position).getCdt());
            String formattedDate = targetFormat.format(date);  // 20120821
            holder.status_tv.setText(formattedDate);
        } catch (ParseException e) {
            holder.status_tv.setText(messagings.get(position).getCdt());
            e.printStackTrace();
        }

        if (messagings.get(position).getStatus().equals("local")) {
            // holder.status_tv.setText("Updated Locally !");
            holder.statsu_img.setImageDrawable(context.getResources().getDrawable(R.drawable.local));
        } else {
            // holder.status_tv.setTextColor(context.getColor(R.color.forestgreen));
            //   holder.status_tv.setText("Updated To Server !!");
            holder.statsu_img.setImageDrawable(context.getResources().getDrawable(R.drawable.done_green));
        }

        Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.slidein);
        if (position % 2 == 0) {
            animation.setDuration(1000);
        } else {
            animation.setDuration(500);
        }
        holder.single_message_cv.startAnimation(animation);
        holder.click_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.shake_anim);
                holder.single_message_cv.startAnimation(animation);
            }
        });

    }

    @Override
    public int getItemCount() {
        return messagings.size();
    }

    public class CheckIn extends RecyclerView.ViewHolder {
        TextView message_tv, status_tv;
        ImageView statsu_img;
        CardView single_message_cv;
        LinearLayout click_ll;

        public CheckIn(View itemView) {
            super(itemView);
            click_ll = (LinearLayout) itemView.findViewById(R.id.click_ll);
            single_message_cv = (CardView) itemView.findViewById(R.id.single_message_cv);
            statsu_img = (ImageView) itemView.findViewById(R.id.statsu_img);
            message_tv = (TextView) itemView.findViewById(R.id.message_tv);
            status_tv = (TextView) itemView.findViewById(R.id.status_tv);
        }
    }

    public void removeItem(int position) {
        messagings.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

}
