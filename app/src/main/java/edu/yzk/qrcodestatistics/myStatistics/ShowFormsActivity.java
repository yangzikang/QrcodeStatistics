package edu.yzk.qrcodestatistics.myStatistics;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import edu.yzk.qrcodestatistics.Login.LoginActivity;
import edu.yzk.qrcodestatistics.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShowFormsActivity extends AppCompatActivity {


    MyHandler mHandler = new MyHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_forms);
        Intent intent = getIntent();
        String statisticsId = intent.getCharSequenceExtra("sid").toString();
        doGetRequset(statisticsId);
    }

    private void showStatistics(String s){

        final List<UserStatisticsModel> list = stringToList(s);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_show);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MyRecyclerViewAdapter mAdapter = new MyRecyclerViewAdapter(list);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<UserStatisticsModel> stringToList(String string) {

        Log.e("yang", string);
        String[] args = string.split(" ");
        for (int i = 0; i < args.length; i++) {
            Log.e("yang", args[i]);
        }
        List<UserStatisticsModel> list = new ArrayList<>();
        for (int i = 0; i < args.length; i += 2) {
            UserStatisticsModel model = new UserStatisticsModel();
            model.setStatisticsId(args[i]);
            if (i + 1 < args.length) {
                model.setStatisticsName(args[i + 1]);
            }
            list.add(model);
        }

        Log.e("list",String.valueOf(list.size()));

        return list;
    }


    private void doGetRequset(final String sid){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://139.199.117.141/getformanswer.php?statisticsid=" + sid)//请求接口。如果需要传参拼接到接口后面。
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

    static class MyHandler extends Handler{
        WeakReference<ShowFormsActivity> mActivity;
        public MyHandler(ShowFormsActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                String s = msg.getData().getCharSequence("response").toString();
                mActivity.get().showStatistics(s);
            }

        }

    }
}
