package ru.mirea.andreyashkin.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class MyLooper extends Thread{
    public Handler mHandler;
    private Handler mainHandler;
    public MyLooper(Handler mainThreadHandler) {
        mainHandler = mainThreadHandler;
    }

    public void run() {
        Log.d("MyLooper", "run");
        Looper.prepare();
        mHandler = new Handler(Looper.myLooper()) {
            public void handleMessage(Message msg) {
                String data = msg.getData().getString("KEY");
                Log.d("MyLooper get message: ", data);

                int age = Integer.parseInt(data.split(" ")[0]);
                Message message = new Message();
                Bundle bundle = new Bundle();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            TimeUnit.SECONDS.sleep(age);
                            bundle.putString("result", "age: " + age);
                            message.setData(bundle);

                            mainHandler.sendMessage(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };
        Looper.loop();
    }
}
