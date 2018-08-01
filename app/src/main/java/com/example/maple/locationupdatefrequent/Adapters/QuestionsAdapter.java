package com.example.maple.locationupdatefrequent.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maple.locationupdatefrequent.Models.Question;
import com.example.maple.locationupdatefrequent.Models.Reports;
import com.example.maple.locationupdatefrequent.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.CheckIn> {
    ArrayList<Question> questions;
    int Rowlayout;
    Context context;
    public QuestionsAdapter(ArrayList<Question> questions, int check_single, Context applicationContext) {
        this.context = applicationContext;
        this.Rowlayout = check_single;
        this.questions = questions;

    }


    @Override
    public CheckIn onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(Rowlayout, parent, false);
        return new CheckIn(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final CheckIn holder, final int position) {

        holder.answer_tv.setText(questions.get(position).getAnswer());
        holder.question_tv.setText(questions.get(position).getQuestion());

    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class CheckIn extends RecyclerView.ViewHolder {
        TextView question_tv, answer_tv;
        ImageView statsu_img;
        CardView card_question;

        public CheckIn(View itemView) {
            super(itemView);
            card_question = itemView.findViewById(R.id.card_question);
            question_tv = itemView.findViewById(R.id.question_tv);
            answer_tv = itemView.findViewById(R.id.answer_tv);
         }
    }

}
