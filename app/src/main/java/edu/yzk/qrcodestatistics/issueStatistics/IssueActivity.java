package edu.yzk.qrcodestatistics.issueStatistics;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import edu.yzk.qrcodestatistics.main.MainActivity;
import edu.yzk.qrcodestatistics.qrcode.Intents;

public class IssueActivity extends AppCompatActivity {

    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private int type = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);

        final EditText editQuestion = findViewById(R.id.edit_question);
        final EditText editName = findViewById(R.id.edit_name);

        Button commit = findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = editQuestion.getText().toString();
                String name = editName.getText().toString();
                if (question.isEmpty() || type == 9 || name.isEmpty()) {
                    Toast.makeText(IssueActivity.this, "问题／类别／名称 \n都需要填写", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(IssueActivity.this, EncodeActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("type", type);
                    intent.putExtra("question", question);
                    startActivity(intent);
                }
            }
        });


        spinner = findViewById(R.id.spinner);

        //数据
        data_list = new ArrayList<String>();
        data_list.add("其它");
        data_list.add("签到");
        data_list.add("投票");
        data_list.add("回答");
        data_list.add("收集");


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(IssueActivity.this, "点击了" + data_list.get(position), Toast.LENGTH_SHORT).show();
                type = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //适配器
        arr_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);

    }
}
