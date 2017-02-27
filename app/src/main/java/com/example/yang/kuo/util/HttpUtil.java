package com.example.yang.kuo.util;

import java.io.BufferedReader;
import java.io.IOException;
import  java.io.InputStream;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yang on 2017/2/27.
 */

public class HttpUtil{

    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){

        new Thread(new Runnable() {
            @Override
            public void run() {
        //通过httpurlconnection获取接口数据
                HttpURLConnection connection=null;

                try {
                    URL url=new URL(address);
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");//设置请求方式
                    connection.setConnectTimeout(8000);//设置连接延时
                    connection.setReadTimeout(8000);//设置读取延时
                    InputStream in=connection.getInputStream();//读取字节流
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));//缓冲流包裹
                    StringBuilder response=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    if (listener!=null){
                        listener.onFinish(response.toString());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


}
