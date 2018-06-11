package edu.yzk.qrcodestatistics.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.yzk.qrcodestatistics.Login.LoginActivity;
import edu.yzk.qrcodestatistics.R;
import edu.yzk.qrcodestatistics.issueStatistics.IssueActivity;
import edu.yzk.qrcodestatistics.qrcode.GeneralCaptureActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.tv_phone);
        textView.setText("欢迎回来：" + LoginActivity.name);

        LinearLayout scan = findViewById(R.id.llt_to_sao);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GeneralCaptureActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout issue = findViewById(R.id.issue_statistics);
        issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, IssueActivity.class);
                startActivity(intent);
            }
        });


    }
}
