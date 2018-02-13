package com.quiz.triviahd;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anthonycr.progress.AnimatedProgressBar;

public class QuizActivity extends AppCompatActivity {

    Runnable runnable;
    AnimatedProgressBar progressBar;
    Handler handler;

    Button op1, op2, op3;
    int progressNum=10;

    TextView countDown;

    LinearLayout container;

    float containerY;

    boolean isAnswered=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        handler=new Handler();
        container=(LinearLayout)findViewById(R.id.container);

        containerY=container.getY();

        progressBar = (AnimatedProgressBar) findViewById(R.id.progress_view);
        progressBar.setProgress(100);

        countDown=(TextView)findViewById(R.id.count_down);

        op1=(Button)findViewById(R.id.op1);
        op2=(Button)findViewById(R.id.op2);
        op3=(Button)findViewById(R.id.op3);

        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! isAnswered) {
                    op1.setBackground(getDrawable(R.drawable.blue_button));
                    op1.setTextColor(Color.WHITE);
                    isAnswered=true;
                }
            }
        });


        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! isAnswered) {
                    op2.setBackground(getDrawable(R.drawable.blue_button));
                    op2.setTextColor(Color.WHITE);
                    isAnswered=true;
                }
            }
        });


        op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(! isAnswered) {
                    op3.setBackground(getDrawable(R.drawable.blue_button));
                    op3.setTextColor(Color.WHITE);
                    isAnswered=true;
                }
            }
        });



        runnable=new Runnable() {
            @Override
            public void run() {
                if (progressNum<=100) {
                    progressBar.setProgress(100 - progressNum);
                    countDown.setText(String.valueOf((100 - progressNum) / 10));
                    progressNum += 10;
                    handler.postDelayed(this, 1000);
                }
                else
                {
                    container.animate()
                            .translationY(container.getHeight())
                            .setInterpolator(new AccelerateInterpolator())
                            .setDuration(250);

                }
            }
        };

        handler.postDelayed(runnable, 1000);

    }
}
