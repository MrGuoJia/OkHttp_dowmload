package com.example.jia.okhttp_dowmload;


import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private Button btn_download;
    private ProgressBar mProgressBar;
    private OkHttpClient httpClient;

    public String URL="http://yixin.dl.126.net/update/installer/yixin.apk";
    public String fileName="yixin.apk";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        httpClient=new OkHttpClient();
        mProgressBar= (ProgressBar) findViewById(R.id.bar);
        btn_download= (Button) findViewById(R.id.btn_DownLoad);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Request request=new Request.Builder().url(URL).build();
                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("LoginActivity","请求文件出错");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        writeFile(response);
                    }
                });


            }
        });
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                int progress=msg.arg1;
                mProgressBar.setProgress(progress);
            }
        }
    };
    private void writeFile(Response response) {
        InputStream is= null;
        FileOutputStream fos=null;
        is=response.body().byteStream(); //从response中得到输入流，inputStream
        String path= Environment.getExternalStorageDirectory().getAbsolutePath();
        File file=new File(path,fileName);
        try {
            fos=new FileOutputStream(file);
            byte[] bytes=new byte[1024];
            int len=0;
            long totalSize=response.body().contentLength();
            long sum=0;
            while ((len=is.read(bytes)) !=-1){
                fos.write(bytes);
                sum+=len;
                int progress=(int) ((sum * 1.0f / totalSize) * 100);
                Message msg=mHandler.obtainMessage(1);
                msg.arg1=progress;
                mHandler.sendMessage(msg);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
             if(is !=null){

                 try {
                     is.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

             }if(fos !=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
