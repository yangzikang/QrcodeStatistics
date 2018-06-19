package edu.yzk.qrcodestatistics.form;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.yzk.qrcodestatistics.Login.LoginActivity;
import edu.yzk.qrcodestatistics.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FormActivity extends AppCompatActivity {

    private TextView answerText;
    private EditText resultText;
    private Button commitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Intent intent = getIntent();
        String result = intent.getCharSequenceExtra("result").toString();
        final String []values = result.split(" ");
        answerText = findViewById(R.id.text_question);
        answerText.setText(values[1]);
        commitButton = findViewById(R.id.commit_answer);
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGetRequest(values[0], resultText.getText().toString());
            }
        });
        resultText = findViewById(R.id.edit_answer);
    }
    private void doGetRequest(final String statisticsId, final String answer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String getUrl = "http://139.199.117.141/formsubmit.php?userid="+ LoginActivity.name+"&statisticsid="+statisticsId+"&answer="+answer;
                    Log.e("kwwl",getUrl);
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(getUrl)
                            .build();//创建Request 对象
                    Response response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        Log.e("kwwl", "response.code()==" + response.code());
                        Log.e("kwwl", "response.message()==" + response.message());
                        Log.e("kwwl", "res==" + response.body().string());
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
