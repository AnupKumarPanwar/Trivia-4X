package com.quiz.triviazq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateProfile extends AppCompatActivity {

    Button conti;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    EditText phoneText;
    String phoneNo;
    PhoneAuthCredential credential;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth mAuth;

    ProgressBar isVerifying;

    JSONObject jsonObject;
    String baseUrl="http://apniapi.com/anup/API/";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(Color.parseColor("#36399a"));

//        StrictMode.ThreadPolicy threadPolicy=new StrictMode.ThreadPolicy.Builder().build();
//        StrictMode.setThreadPolicy(threadPolicy);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

        isVerifying=(ProgressBar)findViewById(R.id.is_verifying);
        phoneText=(EditText)findViewById(R.id.phone);
        mAuth = FirebaseAuth.getInstance();
//        Toast.makeText(getApplicationContext(), "Auth Success", Toast.LENGTH_SHORT).show();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
//                Toast.makeText(getApplicationContext(), "Verification Successful", Toast.LENGTH_SHORT).show();

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                    // ...
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                    // ...
//                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
//                Toast.makeText(getApplicationContext(), "Auth Sent", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, String.valueOf(mResendToken));
                signInWithPhoneAuthCredential(credential);
                // ...
            }
        };




        conti=(Button)findViewById(R.id.conti);
        conti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNo=phoneText.getText().toString();
                if (!TextUtils.isEmpty(phoneNo)) {
//                Toast.makeText(getApplicationContext(), phoneNo, Toast.LENGTH_SHORT).show();
                    sendOTP(phoneNo);
                    conti.setEnabled(false);
                    conti.setText("Verifying...");
                    isVerifying.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void sendOTP(String num)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                num,            // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                CreateProfile.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Verification Successful", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = task.getResult().getUser();
//                            Intent intent=new Intent(getApplicationContext(), HomeScreen.class);
//                            startActivity(intent);
//                            finish();
                            conti.setEnabled(false);
                            conti.setText("Verifying...");
                            isVerifying.setVisibility(View.VISIBLE);
                            editor.putString("phone", phoneNo);
                            editor.apply();
                            JSONAsyncTask getData = new JSONAsyncTask();
                            getData.execute();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(getApplicationContext(), "Verification failed", Toast.LENGTH_SHORT).show();

//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                conti.setEnabled(true);
                                conti.setText("Continue");
                                isVerifying.setVisibility(View.GONE);
//                            }
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
                    .build();

            Request request = new Request.Builder()
                    .url(baseUrl+"login.php")
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
                    String username=jsonObject.getJSONObject("result").getString("username");
                    editor.putString("username", username);
                    editor.apply();
                    Intent intent=new Intent(getApplicationContext(), HomeScreen.class);
                    startActivity(intent);
                    finish();
                }
                else if(jsonObject.getJSONObject("result").getString("status").equals("2"))
                {
                    Intent intent=new Intent(getApplicationContext(), SignupActivity.class);
                    startActivity(intent);
//                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), jsonObject.getJSONObject("result").getString("data"), Toast.LENGTH_SHORT).show();
                    conti.setEnabled(true);
                    conti.setText("Continue");
                    isVerifying.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
