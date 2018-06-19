package edu.yzk.qrcodestatistics.myStatistics;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import edu.yzk.qrcodestatistics.Login.LoginActivity;
import edu.yzk.qrcodestatistics.R;
import edu.yzk.qrcodestatistics.main.MainActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyStatisticsActivity extends AppCompatActivity {

    private MyHandler mHandler = new MyHandler(this);
    private String responseString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_statistics);

        doGetRequest(LoginActivity.name);

    }

    private void doGetRequest(final String userid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://139.199.117.141/userstatistics.php?userid=" + userid)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        Message message = new Message();
                        Bundle b = new Bundle();
                        b.putCharSequence("response", response.body().string());
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

    private void setResponseString(String s) {
        responseString = s;
        List<UserStatisticsModel> list = stringToList(responseString);
        RecyclerView mRecyclerView =   findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MyRecyclerViewAdapter mAdapter = new MyRecyclerViewAdapter(list);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

    }

    private List<UserStatisticsModel> stringToList(String string) {

        Log.e("yang",string);
        String[] args = string.split(" ");
        for(int i= 0;i <args.length;i++){
            Log.e("yang",args[i]);
        }
        List<UserStatisticsModel> list = new ArrayList<>();
        for (int i = 0; i < args.length; i+=2) {
            UserStatisticsModel model = new UserStatisticsModel();
            model.setStatisticsId(args[i]);
            model.setStatisticsName(args[i+1]);
        }
        return list;
    }

    static class MyHandler extends Handler {
        WeakReference<MyStatisticsActivity> mActivity;

        public MyHandler(MyStatisticsActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String s = msg.getData().getCharSequence("response").toString();
                mActivity.get().setResponseString(s);
            }

        }

    }
}
