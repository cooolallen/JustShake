package com.example.cooolallen1.sensor;


import android.content.DialogInterface;
import android.support.annotation.IntegerRes;
//import android.support.v7.app.AlertDialog;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;
import com.github.nisrulz.sensey.ChopDetector;

import com.github.nisrulz.sensey.MovementDetector;
import com.github.nisrulz.sensey.RotationAngleDetector;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;
import com.github.nisrulz.sensey.WaveDetector;


import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class MainActivity extends AppCompatActivity {
    EditText et1;
    EditText et2;
    TextView tv3,tv12,tv22,tv32,tv42;


    Timer T;
    int chop_time;
    int twi_time;
    int get_cycle;
    float last_x, th_f;
    boolean start_flag, shake_flag, move_flag, flip_flag;
    String hostIP, gamestatus, uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        get_cycle = 5000; // 1 Second
        T = new Timer();
        start_flag = false;
        shake_flag = false;
        move_flag = false;
        flip_flag = false;
        et1 = (EditText)findViewById(R.id.et1);
        et2 = (EditText)findViewById(R.id.et2);
        tv3 = (TextView)findViewById(R.id.tv3);
        tv12 = (TextView)findViewById(R.id.tv12);
        tv22 = (TextView)findViewById(R.id.tv22);
        tv32 = (TextView)findViewById(R.id.tv32);
        tv42 = (TextView)findViewById(R.id.tv42);
        Button button_ready = (Button)findViewById(R.id.button_ready);
        button_ready.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(et1.getEditableText().length()!=0 &&
                        et2.getEditableText().length()!=0) {
                    uid = et1.getEditableText().toString();
                    hostIP = "http://"+et2.getEditableText().toString();

                    PostRequest(uid,"","");


                    T.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            GetRequest();
                        }
                    }, get_cycle,get_cycle);

                    setContentView(R.layout.layout_game);
                }else{
                    tv3.setText("All blanks are required!!");
                }
            }
        });


        float th_s = (float)0.03;
        float th_m = (float)1;
        float th_c = (float)0.00001;
        th_f = 180;
        chop_time = 0;
        twi_time = 0;







        Sensey.getInstance().init(this);
        Sensey.getInstance().startShakeDetection(th_s,3000,shakeListener);
        Sensey.getInstance().startMovementDetection(th_m,500,movementListener);
        Sensey.getInstance().startChopDetection(th_c,10,chopListener);
//        Sensey.getInstance().startRotationAngleDetection(rotationAngleListener);
    }

    ShakeDetector.ShakeListener shakeListener=new ShakeDetector.ShakeListener() {
        @Override public void onShakeDetected() {
            // Shake detected, do something
            if(!shake_flag && start_flag) {
                PostRequest(uid, "shake",getTime());
                shake_flag = true;
//                tv12.setText("true");
            }
        }

        @Override public void onShakeStopped() {
            // Shake stopped, do something
            if (start_flag) {
                shake_flag = false;
//                tv12.setText("false");
            }

        }

    };

    WaveDetector.WaveListener waveListener=new WaveDetector.WaveListener() {
        @Override public void onWave() {
            // Wave of hand gesture detected
            if(start_flag) {
                PostRequest(uid, "wave",getTime());
//                tv12.setText("true");
            }
        }
    };


    MovementDetector.MovementListener movementListener=new MovementDetector.MovementListener() {
        @Override public void onMovement() {
            // Movement detected, do something
            if(!move_flag && start_flag) {
                PostRequest(uid, "move",getTime());
                move_flag = true;
                Log.d("data","move appear");
//                tv22.setText("true");
            }
        }

        @Override public void onStationary() {
            // Movement stopped, do something
            if (start_flag) {
                move_flag = false;
//                tv22.setText("false");
            }

        }

    };

    ChopDetector.ChopListener chopListener=new ChopDetector.ChopListener() {
        @Override public void onChop() {
            // Chop gesture detected, do something
            if(start_flag){
                PostRequest(uid,"chop",getTime());
                Log.d("data","chop appear");
//                tv32.setText("true"+Integer.toString(chop_time++));
            }
        }
    };

//    RotationAngleDetector.RotationAngleListener rotationAngleListener =new RotationAngleDetector.RotationAngleListener() {
//        @Override
//        public void onRotation(float angleInAxisX, float angleInAxisY, float angleInAxisZ) {
//            // Do something with the angles, values are in degrees
//
//
//            if(Math.abs(angleInAxisX-last_x)>th_f){
//                //Flipping
//                if(start_flag&&!flip_flag){
//                    flip_flag = true;
//                    PostRequest(uid,"flip",getTime());
//                    Log.d("data","post appear");
////                    tv42.setText("True");
//                }
//
//            }else{
//                //None Flipping
//                if(start_flag) {
//                    flip_flag = false;
////                    tv42.setText("false");
//                }
//            }
//
//            last_x = angleInAxisX;
//        }
//    };

    public void GetRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                doGetRequest();
            }
        }).start();
    }

    private void doGetRequest() {
        final Request request = new Request.Builder()
                .url(hostIP+"/gamestatus/")
                .build();
        OkHttpClient client = new OkHttpClient();

        try {
            Response response = client.newCall(request).execute();
            if(gamestatus=="GAME START" && response.body().string()=="GAME OVER")


            gamestatus = response.body().string();

            Log.d("Get Request", " "+gamestatus);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Get Request","Failed");
        }
    }


    public void PostRequest(final String uid, final String data, final String time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                doPostRequest(uid,data,time);
            }
        }).start();
    }

    private void doPostRequest (String uid, String data, String time) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject actulData = new JSONObject();
        try{
            if(uid!="")
                actulData.put("userID",uid);
            if(data!="")
                actulData.put("data",data);
            if(time!="")
                actulData.put("time",time);

        } catch(JSONException e){
            Log.d("Worning","Json try failed");
        }

        RequestBody body = RequestBody.create(JSON,actulData.toString());
        Request newReq;
        if(start_flag) {// Passing the action
            newReq = new Request.Builder()
                    .url(hostIP+ "/action/")
                    .post(body)
                    .build();
        }else{ // Passing the uid for the first time
            newReq = new Request.Builder()
                    .url(hostIP)
                    .post(body)
                    .build();
            start_flag = true;
            Log.d("Initialize","start_flag seted up ");
            Log.d("InitialPost","UserID Sent");
        }

        try {
            Response response = client.newCall(newReq).execute();
            Log.d("OKHTTP3", uid+" "+data+" "+time+" "+response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("OKHTTP3", "Request Failed");
        }
    }


    public String getTime(){
        double unixTime = System.currentTimeMillis()/1000;
        return Double.toString(unixTime);
    }
}