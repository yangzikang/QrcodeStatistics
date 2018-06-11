package edu.yzk.qrcodestatistics.issueStatistics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Date;

import edu.yzk.qrcodestatistics.Login.LoginActivity;
import edu.yzk.qrcodestatistics.R;

public class EncodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);
        ImageView imageView = findViewById(R.id.qrcode);

        Intent intent = getIntent();
        String question = intent.getCharSequenceExtra("question").toString();

        String qrcodeMesisage = LoginActivity.name + " " + question + " " +new Date().getTime();
        try{
            Bitmap bitmap = CodeCreator.createQRCode(qrcodeMesisage);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
