package com.quiz.triviazq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.progress.AnimatedProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuizActivity extends AppCompatActivity {

    Runnable runnable, showAnsRunnable, hideContainerRunnable, nextQuestionRunnable;
    AnimatedProgressBar progressBar;
    Handler handler;

    Button op1, op2, op3, a_op1, a_op2, a_op3 ;
    int progressNum=10;

    TextView countDown, question, ans1, ans2, ans3, ch1, ch2, ch3, warningMsg;

    LinearLayout container, ansContainer, chContainer;

    float containerY;

    boolean isAnswered=false;

    List<Question> questionList=new ArrayList<>();;

    int question_no;

    float scale;

    int user_response;

    Button ur_button;

    int score=0, reward=0, lifes;

    boolean isEliminated=false;

    boolean lifeUsed=false;

    SharedPreferences sharedPreferences;

    RelativeLayout warningContainer;

    SharedPreferences.Editor editor;

    String baseUrl="http://192.168.4.145/HQ/";

    JSONObject jsonObject, jsonObject2;

    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        StrictMode.ThreadPolicy threadPolicy=new StrictMode.ThreadPolicy.Builder().build();
        StrictMode.setThreadPolicy(threadPolicy);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

        username=sharedPreferences.getString("username", null);

        lifes=Integer.parseInt(sharedPreferences.getString("lifes","0"));

        scale=getApplicationContext().getResources().getDisplayMetrics().density;

        handler=new Handler();
        container=(LinearLayout)findViewById(R.id.container);
        ansContainer=(LinearLayout)findViewById(R.id.ans_container);
        chContainer=(LinearLayout)findViewById(R.id.ch_container);
        warningContainer=(RelativeLayout)findViewById(R.id.warning_container);


        ansContainer.setVisibility(View.GONE);
        chContainer.setVisibility(View.GONE);
        warningContainer.setVisibility(View.GONE);

        containerY=container.getY();
        container.setY(container.getHeight());
        question_no=0;

        progressBar = (AnimatedProgressBar) findViewById(R.id.progress_view);
        progressBar.setProgress(100);

        warningMsg=(TextView)findViewById(R.id.warning_msg);
        countDown=(TextView)findViewById(R.id.count_down);
        question=(TextView)findViewById(R.id.question);

        ans1=(TextView)findViewById(R.id.ans_op1);
        ans2=(TextView)findViewById(R.id.ans_op2);
        ans3=(TextView)findViewById(R.id.ans_op3);


        ch1=(TextView)findViewById(R.id.ch1);
        ch2=(TextView)findViewById(R.id.ch2);
        ch3=(TextView)findViewById(R.id.ch3);



        op1=(Button)findViewById(R.id.op1);
        op2=(Button)findViewById(R.id.op2);
        op3=(Button)findViewById(R.id.op3);

        a_op1=(Button)findViewById(R.id.a_op1);
        a_op2=(Button)findViewById(R.id.a_op2);
        a_op3=(Button)findViewById(R.id.a_op3);

        a_op1.setMinWidth(0);
        a_op2.setMinWidth(0);
        a_op3.setMinWidth(0);


        a_op1.setVisibility(View.GONE);
        a_op2.setVisibility(View.GONE);
        a_op3.setVisibility(View.GONE);

        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! isAnswered && !isEliminated) {
                    op1.setBackground(getDrawable(R.drawable.blue_button));
                    op1.setTextColor(Color.WHITE);
                    isAnswered=true;
                    user_response=1;
                    ur_button=a_op1;
                }
            }
        });


        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! isAnswered && !isEliminated) {
                    op2.setBackground(getDrawable(R.drawable.blue_button));
                    op2.setTextColor(Color.WHITE);
                    isAnswered=true;
                    user_response=2;
                    ur_button=a_op2;
                }
            }
        });


        op3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if(! isAnswered && !isEliminated) {
                    op3.setBackground(getDrawable(R.drawable.blue_button));
                    op3.setTextColor(Color.WHITE);
                    isAnswered=true;
                    user_response=3;
                    ur_button=a_op3;
                }
            }
        });

        container.setVisibility(View.GONE);



        runnable=new Runnable() {
            @Override
            public void run() {
                if (progressNum<=100) {
                    progressBar.setProgress(100 - progressNum);
                    countDown.setText(String.valueOf((100 - progressNum) / 10));
                    progressNum += 10;
                    handler.postDelayed(this, 100);
                }
                else
                {
                    container.animate()
                            .translationY(container.getHeight())
                            .setInterpolator(new AccelerateInterpolator())
                            .setDuration(250);

                    handler.postDelayed(showAnsRunnable, 700);
                }
            }
        };


        showAnsRunnable=new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                showAnswer(question_no);
            }
        };


        hideContainerRunnable=new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                hideContainer();
            }
        };

        nextQuestionRunnable=new Runnable() {
            @Override
            public void run() {
                if (question_no<questionList.size()-1) {
                    question_no++;
                    updateQuestion(question_no);
                }
                else
                {
                    if (score==10) {
                        JSONAsyncTask3 getData = new JSONAsyncTask3();
                        getData.execute();
                    }

                    Intent intent=new Intent(getApplicationContext(), ScoreCard.class);
                    intent.putExtra("score", score);
                    intent.putExtra("reward", reward);
                    startActivity(intent);
                    finish();
                }
            }
        };

        JSONAsyncTask getData = new JSONAsyncTask();
        getData.execute();

    }



    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://www.jasonbase.com/things/JoDb.json")
                    .build();



            try {
                Response response;
                response = client.newCall(request).execute();
                String jsonString=response.body().string();

                JSONObject jsonObject=new JSONObject(jsonString);
                reward=jsonObject.getInt("reward");
                JSONArray jsonArray=jsonObject.getJSONArray("data");


                for (int i=0; i<jsonArray.length(); i++)
                {
                    String q=jsonArray.getJSONObject(i).getString("q");
                    String op1=jsonArray.getJSONObject(i).getString("op1");
                    String op2=jsonArray.getJSONObject(i).getString("op2");
                    String op3=jsonArray.getJSONObject(i).getString("op3");
                    int ans=jsonArray.getJSONObject(i).getInt("ans");
                    int ch1=jsonArray.getJSONObject(i).getInt("ch1");
                    int ch2=jsonArray.getJSONObject(i).getInt("ch2");
                    int ch3=jsonArray.getJSONObject(i).getInt("ch3");
                    questionList.add(new Question(q, op1, op2, op3, ans, ch1, ch2, ch3));
                }

            }
            catch (Exception E)
            {
//                    Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show();
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {
            if (questionList.size()>0) {
                startQuiz();
            }
            else {
                warningMsg.setText("Unable to connect!");
                warningContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    void startQuiz()
    {
        updateQuestion(question_no);
        container.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void updateQuestion(int i)
    {
        isAnswered=false;
        progressNum=0;
        user_response=-1;
        ur_button=null;
        a_op1.setVisibility(View.GONE);
        a_op2.setVisibility(View.GONE);
        a_op3.setVisibility(View.GONE);


        Question q_q=questionList.get(i);
        question.setText(q_q.q);
        op1.setText(q_q.op1);
        op2.setText(q_q.op2);
        op3.setText(q_q.op3);

        op1.setBackground(getDrawable(R.drawable.rounded_button));
        op2.setBackground(getDrawable(R.drawable.rounded_button));
        op3.setBackground(getDrawable(R.drawable.rounded_button));

        op1.setTextColor(Color.parseColor("#131313"));
        op2.setTextColor(Color.parseColor("#131313"));
        op3.setTextColor(Color.parseColor("#131313"));

        ans1.setText(q_q.op1);
        ans2.setText(q_q.op2);
        ans3.setText(q_q.op3);

        ch1.setText(String.valueOf(q_q.ch1));
        ch2.setText(String.valueOf(q_q.ch2));
        ch3.setText(String.valueOf(q_q.ch3));

        ansContainer.setVisibility(View.GONE);
        chContainer.setVisibility(View.GONE);

        handler.postDelayed(runnable, 125);

        if (question_no>=0) {
            showContainer();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void showAnswer(int i)
    {
        Question q_a=questionList.get(i);
        op1.setBackground(getDrawable(R.drawable.grey_button));
        op2.setBackground(getDrawable(R.drawable.grey_button));
        op3.setBackground(getDrawable(R.drawable.grey_button));

//        op1.setTextColor(Color.WHITE);
//        op2.setTextColor(Color.WHITE);
//        op3.setTextColor(Color.WHITE);

        op1.setText("");
        op2.setText("");
        op3.setText("");

        a_op1.setVisibility(View.VISIBLE);
        a_op2.setVisibility(View.VISIBLE);
        a_op3.setVisibility(View.VISIBLE);

        int sum=q_a.ch1+q_a.ch2+q_a.ch3;

        int p1=q_a.ch1*250/sum;
        int p2=q_a.ch2*250/sum;
        int p3=q_a.ch3*250/sum;

        if (p1<60)
        {
            p1 = 60;
        }

        if (p2<60)
        {
           p2 = 60;
        }

        if (p3<60)
        {
            p3 = 60;
        }

        ansContainer.setVisibility(View.VISIBLE);
        chContainer.setVisibility(View.VISIBLE);

        a_op1.setLayoutParams(new RelativeLayout.LayoutParams((int) (p1*scale), LinearLayout.LayoutParams.WRAP_CONTENT));
        a_op2.setLayoutParams(new RelativeLayout.LayoutParams((int) (p2*scale), LinearLayout.LayoutParams.WRAP_CONTENT));
        a_op3.setLayoutParams(new RelativeLayout.LayoutParams((int) (p3*scale), LinearLayout.LayoutParams.WRAP_CONTENT));

        if (q_a.ans==1)
        {
            a_op1.setBackground(getDrawable(R.drawable.green_button));

            a_op2.setBackground(getDrawable(R.drawable.dark_grey_button));

            a_op3.setBackground(getDrawable(R.drawable.dark_grey_button));
        }
        if (q_a.ans==2)
        {
            a_op2.setBackground(getDrawable(R.drawable.green_button));

            a_op1.setBackground(getDrawable(R.drawable.dark_grey_button));
            a_op3.setBackground(getDrawable(R.drawable.dark_grey_button));
        }
        if (q_a.ans==3)
        {
            a_op3.setBackground(getDrawable(R.drawable.green_button));

            a_op1.setBackground(getDrawable(R.drawable.dark_grey_button));
            a_op2.setBackground(getDrawable(R.drawable.dark_grey_button));
        }

        if (q_a.ans!=user_response)
        {
            if (lifes==0) {
                isEliminated = true;
                warningMsg.setText("You are eliminated!");
                warningContainer.setVisibility(View.VISIBLE);
                if (ur_button != null) {
                    ur_button.setBackground(getDrawable(R.drawable.red_button));
                }
            }
            else {
                if (!lifeUsed && question_no!=9) {
                    lifes--;
                    lifeUsed = true;
                    editor.putString("lifes", String.valueOf(lifes));

                    JSONAsyncTask2 getData2 = new JSONAsyncTask2();
                    getData2.execute();


                    Toast.makeText(getApplicationContext(), "1 life used", Toast.LENGTH_SHORT).show();
                    if (ur_button != null) {
                        ur_button.setBackground(getDrawable(R.drawable.red_button));
                    }
                }
                else {
                    isEliminated = true;
                    warningMsg.setText("You are eliminated!");
                    warningContainer.setVisibility(View.VISIBLE);
                    if (ur_button != null) {
                        ur_button.setBackground(getDrawable(R.drawable.red_button));
                    }
                }
            }
        }
        else {
            score++;
        }


        container.animate()
                .translationY(containerY)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(250);

        handler.postDelayed(hideContainerRunnable, 500);

    }

    void hideContainer()
    {
        container.animate()
                .translationY(container.getHeight())
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(250);

//        ansContainer.animate()
//                .translationY(container.getHeight())
//                .setInterpolator(new AccelerateInterpolator())
//                .setDuration(250);
//
//        chContainer.animate()
//                .translationY(container.getHeight())
//                .setInterpolator(new AccelerateInterpolator())
//                .setDuration(250);

        ansContainer.setVisibility(View.GONE);
        chContainer.setVisibility(View.GONE);

        handler.postDelayed(nextQuestionRunnable, 500);
    }

    void showContainer()
    {
        container.animate()
                .translationY(containerY)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(250);
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }



    class JSONAsyncTask2 extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", username)
                    .build();

            Request request = new Request.Builder()
                    .url(baseUrl+"updateLives.php")
                    .build();



            try {
                Response response;
                response = client.newCall(request).execute();
                String jsonString=response.body().string();

                jsonObject=new JSONObject(jsonString);


            }
            catch (Exception E)
            {
//                    Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show();
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {

        }
    }


    class JSONAsyncTask3 extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", username)
                    .addFormDataPart("amount", String.valueOf(reward))
                    .build();


            Request request = new Request.Builder()
                    .url(baseUrl+"updateBalance.php")
                    .post(requestBody)
                    .build();



            try {
                Response response;
                response = client.newCall(request).execute();
                String jsonString=response.body().string();

                jsonObject2=new JSONObject(jsonString);


            }
            catch (Exception E)
            {
//                    Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show();
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {

        }
    }

}
