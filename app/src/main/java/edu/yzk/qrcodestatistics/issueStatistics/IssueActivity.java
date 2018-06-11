package edu.yzk.qrcodestatistics.issueStatistics;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.yzk.qrcodestatistics.Login.LoginActivity;
import edu.yzk.qrcodestatistics.R;
import edu.yzk.qrcodestatistics.qrcode.Intents;

public class IssueActivity extends AppCompatActivity {

    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);

        final EditText editQuestion = findViewById(R.id.edit_question);
        TextView name = findViewById(R.id.issue_name);
        name.setText(LoginActivity.name);

        Button commit = findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = editQuestion.getText().toString();
                if(question.isEmpty()){
                    Toast.makeText(IssueActivity.this,"问题是空的",Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(IssueActivity.this, EncodeActivity.class);
                    intent.putExtra("question",question);
                    startActivity(intent);
                }
            }
        });


        spinner = findViewById(R.id.spinner);

        //数据
        data_list = new ArrayList<String>();
        data_list.add("签到");
        data_list.add("投票");
        data_list.add("回答");
        data_list.add("问题");
        data_list.add("收集");
        data_list.add("其它");

        //适配器
        arr_adapter= new ArrayAdapter<String>(this, R.layout.spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);

    }
}
