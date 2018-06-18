package edu.yzk.qrcodestatistics.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Set;

import edu.yzk.qrcodestatistics.R;
import edu.yzk.qrcodestatistics.main.MainActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    public static String name;
    private Button login;
    private Button toSignUp;
    private EditText account;
    private EditText password;

    private MyHandler mHandler = new MyHandler(this);

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            if (password.getText().length() >= 6) {
                login.setEnabled(true);
                login.setBackgroundResource(R.drawable.button_style_login_blue);
            } else {
                login.setEnabled(false);
                login.setBackgroundResource(R.drawable.button_style_login_gray);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    };

    private void initView() {
        login = (Button) findViewById(R.id.login);
        toSignUp = (Button) findViewById(R.id.toSignUp);
        account = (EditText) findViewById(R.id.loginAccount);
        password = (EditText) findViewById(R.id.loginPassword);
        toSignUp = (Button) findViewById(R.id.toSignUp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = account.getText().toString();
                String psw = password.getText().toString();

                doGetRequest(name, psw);
            }
        });

        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        password.addTextChangedListener(mTextWatcher);
    }

    private void doGetRequest(final String userid, final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://139.199.117.141/login.php?userid="+userid+"&password="+password)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response  = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        Log.d("kwwl","response.code()=="+response.code());
                        Log.d("kwwl","response.message()=="+response.message());
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                        Message message = new Message();
                        Bundle b = new Bundle();
                        b.putCharSequence("response",response.body().string());
                        message.setData(b);
                        message.what = 1;
                        mHandler.sendMessage(message);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void doSave(){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("name",name);
        editor.apply();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }


    static class MyHandler extends Handler{
        WeakReference<LoginActivity> mActivity;
        public MyHandler(LoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                if(!msg.getData().getCharSequence("response").toString().equals("login error")){
                    mActivity.get().doSave();
                } else {
                    name = null;
                }

            }

        }

    }

}
