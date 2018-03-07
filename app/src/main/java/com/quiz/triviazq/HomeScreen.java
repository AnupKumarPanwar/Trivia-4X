package com.quiz.triviazq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.unity.IUnityAdsUnityListener;

import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeScreen extends AppCompatActivity {

    Button playButton, invite, gml, leaderboard;

    TextView nextGame, gameReward, usernameHolder, balanceHOlder, livesHolder, unfChar;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String referral_code;
    JSONObject jsonObject, jsonObject2;

    int hours, minutes, seconds;

    Handler handler;

    String baseUrl="http://apniapi.com/anup/API/";
    String username;

    UnityAdsListener unityAdsListener=new UnityAdsListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this).init();
        setContentView(R.layout.activity_home_screen);

        UnityAds.initialize(this, "1714351", unityAdsListener);

//        StrictMode.ThreadPolicy threadPolicy=new StrictMode.ThreadPolicy.Builder().build();
//        StrictMode.setThreadPolicy(threadPolicy);

        handler=new Handler();
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

        referral_code=sharedPreferences.getString("username",null);
        username=sharedPreferences.getString("username",null);
//        username="AnupKumar";

        nextGame=(TextView)findViewById(R.id.tv2);
        gameReward=(TextView)findViewById(R.id.tv3);
        usernameHolder=(TextView)findViewById(R.id.username_holder);
        balanceHOlder=(TextView)findViewById(R.id.balance_holder);
        livesHolder=(TextView)findViewById(R.id.lives_holder);
        unfChar=(TextView)findViewById(R.id.profile_image);


        usernameHolder.setText(username);

        try {
            unfChar.setText(String.valueOf(username.toUpperCase().charAt(0)));
        }
        catch (Exception E)
        {

        }

        playButton=(Button)findViewById(R.id.play_button);
        gml=(Button)findViewById(R.id.gml);
        invite=(Button)findViewById(R.id.invite);
        leaderboard=(Button)findViewById(R.id.leaderboard);



//        hours=new Time(System.currentTimeMillis()).getHours();
//        minutes=new Time(System.currentTimeMillis()).getMinutes();
//
//        if (hours<=17)
//        {
//            if (hours==17)
//            {
//                if (minutes<=15)
//                {
//                    nextGame.setText("Today, 5:15 PM");
//                    gameReward.setText("Rs. 10000 prize");
//                }
//                else
//                {
//                    nextGame.setText("Today, 7:15 PM");
//                    gameReward.setText("Rs. 15000 prize");
//                }
//            }
//            else
//            {
//                nextGame.setText("Today, 5:15 PM");
//                gameReward.setText("Rs. 10000 prize");
//            }
//
//        }
//        else if (hours<=19)
//        {
//            if (hours==19)
//            {
//                if (minutes<=30)
//                {
//                    nextGame.setText("Today, 7:15 PM");
//                    gameReward.setText("Rs. 15000 prize");
//                }
//                else
//                {
//                    nextGame.setText("Tomorrow, 5:15 PM");
//                    gameReward.setText("Rs. 10000 prize");
//                }
//            }
//            else
//            {
//                nextGame.setText("Today, 7:15 PM");
//                gameReward.setText("Rs. 15000 prize");
//            }
//        }
//        else
//        {
//            nextGame.setText("Tomorrow, 5:15 PM");
//            gameReward.setText("Rs. 10000 prize");
//        }



        final DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("gmt"));

        Runnable getQuizCountDown=new Runnable() {
            @Override
            public void run() {
                String gmtTime = df.format(new Date());

                String[] times=gmtTime.split(":");
                hours=Integer.parseInt(times[0]);
                minutes=Integer.parseInt(times[1]);
                seconds=Integer.parseInt(times[2].substring(0, 2));

//                Toast.makeText(getApplicationContext(),String.valueOf(times[2].charAt(times[2].length()-2)), Toast.LENGTH_SHORT).show();

                if (hours!=12) {
                    if ((times[2].charAt(times[2].length() - 2) == 'P') || (times[2].charAt(times[2].length() - 2) == 'p')) {
                        hours -= 12;
                    }
                }


                int remHrs=12-hours;
                int remMin=59-minutes;
                int remSec=59-seconds;

                String showRemHrs=String.valueOf(remHrs);
                String showRemMin=String.valueOf(remMin);
                String showRemSec=String.valueOf(remSec);

                if (showRemHrs.length()==1)
                {
                    showRemHrs="0"+showRemHrs;
                }

                if (showRemMin.length()==1)
                {
                    showRemMin="0"+showRemMin;
                }

                if (showRemSec.length()==1)
                {
                    showRemSec="0"+showRemSec;
                }

                if (remHrs==23)
                {
                    nextGame.setText("01" + " : " + showRemMin + " : " + showRemSec);
                    gameReward.setText("$ 4000 prize");
                }
                else if (remHrs==22)
                {
                    nextGame.setText("00" + " : " + showRemMin + " : " + showRemSec);
                    gameReward.setText("$ 4000 prize");
                }

                else
                {
                    nextGame.setText(showRemHrs + " : " + showRemMin + " : " + showRemSec);
                    gameReward.setText("$ 4000 prize");
                }

                handler.postDelayed(this, 1000);
            }
        };



        handler.postDelayed(getQuizCountDown,1);




        gml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");

                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                intent.putExtra(android.content.Intent.EXTRA_TEXT, "Join "+getString(R.string.app_name)+" - The viral game where you win real cash! Use my referral code \""+referral_code+"\"\nhttps://play.google.com/store/apps/details?id=com.quiz.triviazq");

                try {
                    startActivity(Intent.createChooser(intent, "Get extra life"));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
                }
            }
        });


        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), LeaderboardActivity.class);
                startActivity(intent);
                finish();
            }
        });





        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");

                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                intent.putExtra(android.content.Intent.EXTRA_TEXT, "Join "+getString(R.string.app_name)+" - The viral game where you win real cash! Use my referral code \""+referral_code+"\"\nhttps://play.google.com/store/apps/details?id=com.quiz.triviazq");

                try {
                    startActivity(Intent.createChooser(intent, "Get extra life"));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
                }
            }
        });



        playButton.setVisibility(View.INVISIBLE);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButton.setVisibility(View.INVISIBLE);

                if (UnityAds.isReady())
                {
                    UnityAds.show(HomeScreen.this);
                }
                else
                {
                    Intent intent=new Intent(getApplicationContext(), QuizActivity.class);
                    startActivity(intent);
                }
            }
        });

        JSONAsyncTask2 getData2 = new JSONAsyncTask2();
        getData2.execute();

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
                    .url(baseUrl+"getQuizTime.php")
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
            try {

                if (jsonObject.getString("canPlay").equals("1"))
                {
                    playButton.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                    .url(baseUrl+"getBal.php")
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
            try {

                if (jsonObject2.getJSONObject("result").getString("status").equals("1"))
                {
//                    playButton.setVisibility(View.VISIBLE);
                    balanceHOlder.setText("$ "+jsonObject2.getJSONObject("result").getString("balance"));
                    livesHolder.setText(jsonObject2.getJSONObject("result").getString("lives"));

                    editor.putString("lifes",jsonObject2.getJSONObject("result").getString("lives") );
                    editor.apply();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    private class UnityAdsListener implements IUnityAdsUnityListener
    {

        @Override
        public void onUnityAdsInitiatePurchase(String s) {

        }

        @Override
        public void onUnityAdsReady(String s) {

        }

        @Override
        public void onUnityAdsStart(String s) {

        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
            Intent intent=new Intent(getApplicationContext(), QuizActivity.class);
            startActivity(intent);
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

        }
    }
}
