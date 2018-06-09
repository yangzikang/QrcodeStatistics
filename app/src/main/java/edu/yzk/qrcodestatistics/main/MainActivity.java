package edu.yzk.qrcodestatistics.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.yzk.qrcodestatistics.R;
import edu.yzk.qrcodestatistics.issueStatistics.IssueActivity;
import edu.yzk.qrcodestatistics.qrcode.GeneralCaptureActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        Button scan = findViewById(R.id.scan_button);
//        scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, GeneralCaptureActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        Button issue = findViewById(R.id.issue_statistics);
//        issue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, IssueActivity.class);
//                startActivity(intent);
//            }
//        });

    }
}
