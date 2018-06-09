package edu.yzk.qrcodestatistics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.yzk.qrcodestatistics.Login.LoginActivity;
import edu.yzk.qrcodestatistics.main.MainActivity;
import edu.yzk.qrcodestatistics.qrcode.GeneralCaptureActivity;

public class WelcomeActivity extends AppCompatActivity {
    private final int SLEEP_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onStart(){
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEP_TIME);
                    SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                    LoginActivity.name = sharedPreferences.getString("name","");
                    Intent intent;

                    if(LoginActivity.name == ""){
                        intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    } else{
                        intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    }

                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
