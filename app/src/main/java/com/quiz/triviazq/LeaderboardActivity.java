package com.quiz.triviazq;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LeaderboardActivity extends AppCompatActivity {

    LinearLayout container;
    JSONObject jsonObject;
    String baseUrl="http://apniapi.com/anup/API/";

    JSONArray jsonArray;

    ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

//        StrictMode.ThreadPolicy threadPolicy=new StrictMode.ThreadPolicy.Builder().build();
//        StrictMode.setThreadPolicy(threadPolicy);

        container=(LinearLayout)findViewById(R.id.container);
        loader=(ProgressBar)findViewById(R.id.loader);

        JSONAsyncTask getData = new JSONAsyncTask();
        getData.execute();
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(intent);
        finish();
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
                    .url(baseUrl+"leaderboard.php")
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

                if (jsonObject.getJSONObject("result").getString("status").equals("1"))
                {
                    loader.setVisibility(View.GONE);
                    jsonArray=jsonObject.getJSONObject("result").getJSONArray("data");

                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        LinearLayout linearLayout=new LinearLayout(getApplicationContext());
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setPadding(0, 10, 0, 10);

                        TextView rank=new TextView(getApplicationContext());
                        rank.setText(String.valueOf(i+1));
                        rank.setTextColor(Color.BLACK);
                        rank.setTextSize(20);
                        rank.setWidth(container.getWidth()/7);
//                        rank.setGravity(Gravity.CENTER);

                        TextView username=new TextView(getApplicationContext());
                        username.setTextColor(Color.BLACK);
                        username.setText(jsonArray.getJSONObject(i).getString("username"));
                        username.setTextSize(20);
                        username.setWidth(container.getWidth()-3*container.getWidth()/7);
//                        username.setGravity(Gravity.CENTER);

                        TextView balance=new TextView(getApplicationContext());
                        balance.setTextColor(Color.BLACK);
                        balance.setText("$ "+jsonArray.getJSONObject(i).getString("balance"));
                        balance.setTextSize(20);
                        balance.setWidth(2*container.getWidth()/7);
//                        balance.setGravity(Gravity.CENTER);


                        linearLayout.addView(rank);
                        linearLayout.addView(username);
                        linearLayout.addView(balance);

                        container.addView(linearLayout);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
