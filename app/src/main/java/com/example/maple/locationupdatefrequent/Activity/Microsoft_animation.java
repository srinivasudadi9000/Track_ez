package com.example.maple.locationupdatefrequent.Activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maple.locationupdatefrequent.R;

public class Microsoft_animation extends Activity implements View.OnClickListener {
    TextView counter_tv;
    RecyclerView messages_recyler;
    ImageView msg_back_img, send_img, clear_img;
    EditText et_content;
    private TextView windowsTvOne;
    private TextView windowsTvThree;
    private TextView windowsTvTwo;
    private int screenWidth;
    private AnimatorSet windowsAnimatorSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
        windowsTvOne = (TextView) findViewById(R.id.windowsTvOne);
        windowsTvTwo = (TextView) findViewById(R.id.windowsTvTwo);
        windowsTvThree = (TextView) findViewById(R.id.windowsTvThree);

        windowsTvOne.setVisibility(View.GONE);
        windowsTvTwo.setVisibility(View.GONE);
        windowsTvThree.setVisibility(View.GONE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        windowsAnimation();
      /*  counter_tv = (TextView) findViewById(R.id.counter_tv);
        msg_back_img = (ImageView) findViewById(R.id.msg_back_img);
        send_img = (ImageView) findViewById(R.id.send_img);
        clear_img = (ImageView) findViewById(R.id.clear_img);
        messages_recyler = (RecyclerView) findViewById(R.id.messages_recyler);
        et_content = (EditText) findViewById(R.id.et_content);
        msg_back_img.setOnClickListener(this);
        send_img.setOnClickListener(this);
        clear_img.setOnClickListener(this);
*/

/*
        counter_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCountAnimation();
            }
        });
*/

    }

    @Override
    public void onClick(View view) {
/*
        switch (view.getId()) {
            case R.id.msg_back_img:
                finish();
                break;
            case R.id.send_img:
                DBHelper dbHelper = new DBHelper(Messages.this);
                dbHelper.insertMessage(et_content.getText().toString(),"12:12:2018","local",Messages.this);
                Toast.makeText(getBaseContext(),et_content.getText().toString(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.clear_img:
                et_content.setText("");
                break;
        }
*/
    }

    private void startCountAnimation() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 600);
        animator.setDuration(10000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                counter_tv.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }


    public void windowsAnimation() {

        final ValueAnimator valueTvOne_x = ObjectAnimator.ofFloat(windowsTvOne, "x", windowsTvOne.getX() - 40, windowsTvOne.getX() - 50, (screenWidth / 2) + 10f, (screenWidth / 2) + 25f,
                (screenWidth / 2) + 50f//,(screenWidth / 2)+55f//,(screenWidth / 2)+80f//,(screenWidth/2 )+25f,(screenWidth / 2) +30f//, (screenWidth / 2)+35f,(screenWidth / 2)+40f,(screenWidth / 2)+45f //,(screenWidth / 2)+6.6f,(screenWidth / 2)+7.7f,(screenWidth / 2)+8.8f //, (screenWidth / 2)+9,(screenWidth / 2)+10,
                , screenWidth * .92f, screenWidth + 5);

        valueTvOne_x.setDuration(5200);
        valueTvOne_x.setRepeatCount(0);
        valueTvOne_x.setRepeatMode(ValueAnimator.REVERSE);

        valueTvOne_x.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                windowsTvOne.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

        final ValueAnimator valueTvTwo_x = ObjectAnimator.ofFloat(windowsTvTwo, "x", windowsTvTwo.getX() - 50, (screenWidth / 2.1f) + 10f, (screenWidth / 2.1f) + 25f, (screenWidth / 2.1f) + 50f//, (screenWidth / 2.1f) +55f//, (screenWidth / 2.1f) +80f//, (screenWidth / 2.1f) +25f,(screenWidth / 2.1f) +30f
                , screenWidth * .94f, screenWidth + 5);

        valueTvTwo_x.setDuration(6000);
        valueTvTwo_x.setRepeatCount(0);
        valueTvTwo_x.setStartDelay(200);
        valueTvTwo_x.setRepeatMode(ValueAnimator.REVERSE);

        valueTvTwo_x.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                windowsTvTwo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

        final ValueAnimator valueTvThree_x = ObjectAnimator.ofFloat(windowsTvThree, "x", windowsTvThree.getX() - 50, (screenWidth / 2.2f) + 10f, (screenWidth / 2.2f) + 25f, (screenWidth / 2.2f) + 50f//,(screenWidth / 2.2f) +55f//,(screenWidth / 2.2f) +80f//,(screenWidth / 2.2f) +25f,(screenWidth / 2.2f) +30f
                , screenWidth * .94f, screenWidth + 5);

        valueTvThree_x.setDuration(6500);
        valueTvThree_x.setRepeatCount(0);
        valueTvTwo_x.setStartDelay(500);
        valueTvThree_x.setRepeatMode(ValueAnimator.REVERSE);

        valueTvThree_x.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                windowsTvThree.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

        windowsAnimatorSet = new AnimatorSet();
        windowsAnimatorSet.playTogether(valueTvTwo_x, valueTvThree_x, valueTvOne_x);

        windowsAnimatorSet.start();
        windowsAnimatorSet.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                windowsAnimatorSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

    }
}
