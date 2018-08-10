package com.example.maple.locationupdatefrequent.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.R;

public class QD extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qd);
         showDialog(getIntent().getStringExtra("question"),getIntent().getStringExtra("answer"),getIntent().getStringExtra("state"));
    }

    public void showDialog(String question, String answer,String state) {
        final Dialog dialog = new Dialog(QD.this, R.style.simpledialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        if (state.equals("message")){
            dialog.setContentView(R.layout.custom_dialog_message);
        }else {
            dialog.setContentView(R.layout.custom_dialog_question);
        }


        TextView question_tv_cdq = dialog.findViewById(R.id.question_tv_cdq);
        question_tv_cdq.setText(question);

        question_tv_cdq.setMovementMethod(new ScrollingMovementMethod());

        TextView answ = dialog.findViewById(R.id.answer_tv_cdq);
        answ.setText(answer);
        answ.setMovementMethod(new ScrollingMovementMethod());

        TextView dialogButton = dialog.findViewById(R.id.close_tv);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();

    }

}
