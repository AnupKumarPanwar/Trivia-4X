package com.quiz.triviazq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    EditText username,referral, email;
    Button conti;
    ProgressBar isVerifying;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String val_username, val_referral, phoneNo, val_email;
    JSONObject jsonObject;

    String baseUrl="http://apniapi.com/anup/API/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        StrictMode.ThreadPolicy threadPolicy=new StrictMode.ThreadPolicy.Builder().build();
//        StrictMode.setThreadPolicy(threadPolicy);
        
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

        phoneNo=sharedPreferences.getString("phone", null);


        username=(EditText) findViewById(R.id.username);
        referral=(EditText) findViewById(R.id.referral);
        email=(EditText) findViewById(R.id.email);

        conti=(Button)findViewById(R.id.conti);

        isVerifying=(ProgressBar)findViewById(R.id.is_verifying);

        conti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                phoneNo=edit
                val_username=username.getText().toString();
                val_referral=referral.getText().toString();
                val_email=email.getText().toString();

                if (!TextUtils.isEmpty(val_username) && !TextUtils.isEmpty(val_email))
                {


                        if (val_referral == null) {
                            val_referral = "";
                        }

                        conti.setEnabled(false);
                        conti.setText("Signing up...");
                        isVerifying.setVisibility(View.VISIBLE);

                        JSONAsyncTask jsonAsyncTask = new JSONAsyncTask();
                        jsonAsyncTask.execute();
                }
            }
        });
    }


    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("phone", phoneNo)
                    .addFormDataPart("username", val_username)
                    .addFormDataPart("referral", val_referral)
                    .addFormDataPart("email", val_email)
                    .build();

            Request request = new Request.Builder()
                    .url(baseUrl+"signup.php")
                    .post(requestBody)
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
                    editor.putString("username",val_username);
                    editor.apply();
                    Intent intent=new Intent(getApplicationContext(), HomeScreen.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), jsonObject.getJSONObject("result").getString("data"), Toast.LENGTH_SHORT).show();
                    conti.setEnabled(true);
                    conti.setText("Continue");
                    isVerifying.setVisibility(View.GONE);
                }

            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }
}
