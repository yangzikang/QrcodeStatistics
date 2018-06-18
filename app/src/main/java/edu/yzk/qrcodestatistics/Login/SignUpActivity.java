package edu.yzk.qrcodestatistics.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.yzk.qrcodestatistics.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    private Button signUp;
    private EditText account;
    private EditText realName;
    private EditText confirmPassword;
    private EditText password;
    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            if (password.getText().length() >= 6) {
                signUp.setEnabled(true);
                signUp.setBackgroundResource(R.drawable.button_style_login_blue);
            } else {
                signUp.setEnabled(false);
                signUp.setBackgroundResource(R.drawable.button_style_login_gray);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    };

    private void initView() {
        signUp = (Button) findViewById(R.id.signUp);
        account = (EditText) findViewById(R.id.signUpAccount);
        password = (EditText) findViewById(R.id.signUpPassword);
        realName = findViewById(R.id.realname);
        confirmPassword = findViewById(R.id.certainPassword);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = account.getText().toString();
                String realname = realName.getText().toString();
                String psw1 = password.getText().toString();
                String psw2 = confirmPassword.getText().toString();

                if (psw1.equals(psw2)) {
                    doGetRequest(userid, realname, psw1);
                    Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, "密码不同", Toast.LENGTH_SHORT).show();
                }
            }
        });

        password.addTextChangedListener(mTextWatcher);
    }

    private void doGetRequest(final String userid, final String realname, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String getUrl = "http://139.199.117.141/signup.php?userid="+userid+"&realname="+realname+"&password="+password;
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
