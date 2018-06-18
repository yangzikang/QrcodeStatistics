package edu.yzk.qrcodestatistics.issueStatistics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.util.Date;

import edu.yzk.qrcodestatistics.Login.LoginActivity;
import edu.yzk.qrcodestatistics.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EncodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);
        ImageView imageView = findViewById(R.id.qrcode);

        Intent intent = getIntent();
        String name = intent.getCharSequenceExtra("name").toString();
        int type = intent.getIntExtra("type",0);
        String question = intent.getCharSequenceExtra("question").toString();
        String statisticsid = LoginActivity.name + new Date().getTime();

        doGetRequset(statisticsid,name,LoginActivity.name,type,question);


        String qrcodeMesisage = statisticsid+ " " + question;
        try{
            Bitmap bitmap = CodeCreator.createQRCode(qrcodeMesisage);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doGetRequset(final String statisticsid, final String statisticsname, final String userid, final int type, final String question){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String getUrl = "http://139.199.117.141/issuestatistics.php?statisticsid="+statisticsid+"&statisticsname="+statisticsname+"&userid="+userid+"&statisticstype="+type+"&question="+question;
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
