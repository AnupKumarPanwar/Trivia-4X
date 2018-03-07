package com.quiz.triviazq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.unity3d.ads.UnityAds;
import com.unity3d.ads.unity.IUnityAdsUnityListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScoreCard extends AppCompatActivity {

    TextView msg, score, reward, cont, congrats;
    Button share;
    int eR, eS;
    View rootView;
    SharedPreferences sharedPreferences;

    String referral_code;
    String baseUrl="http://apniapi.com/anup/API/";

    UnityAdsListener unityAdsListener=new UnityAdsListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_card);


        UnityAds.initialize(this, "1714351", unityAdsListener);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        referral_code=sharedPreferences.getString("username",null);

        msg=(TextView)findViewById(R.id.msg);
        score=(TextView)findViewById(R.id.score);
        reward=(TextView)findViewById(R.id.reward);
        cont=(TextView)findViewById(R.id.conti);
        congrats=(TextView)findViewById(R.id.congrats);
        share=(Button) findViewById(R.id.share);

        eS=getIntent().getExtras().getInt("score", 0);
        eR=getIntent().getExtras().getInt("reward", 0);
//        eR=0;
//        eS=0;

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), HomeScreen.class);
                startActivity(intent);
                finish();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                Bitmap bitmap=getScreenShot(rootView);
                Uri filePath=store(bitmap,"score_card.png");
                shareImage(filePath);
            }
        });

        reward.setText("$ "+String.valueOf(eR));
        score.setText(String.valueOf(eS)+"/10");
        if (eS!=10)
        {
            msg.setText("You Lost!");
            congrats.setText("Better luck next time!\nTell your friends!");
        }


    }

    private void shareImage(Uri file){
        Uri uri = file;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Join "+getString(R.string.app_name)+" - The viral game where you win real cash! Use my referral code \""+referral_code+"\"\nhttps://play.google.com/store/apps/details?id=com.quiz.triviazq");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share score"));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public Uri store(Bitmap bm, String fileName){
        Uri bmpUri=null;
        try {
            // This way, you don't need to request external read/write permission.
            File file =  new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "score_card.png");
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private class UnityAdsListener implements IUnityAdsUnityListener
    {

        @Override
        public void onUnityAdsInitiatePurchase(String s) {

        }

        @Override
        public void onUnityAdsReady(String s) {

            UnityAds.show(ScoreCard.this);

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
