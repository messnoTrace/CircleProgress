package com.notrace.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.notrace.library.RoundProgressBar;

public class MainActivity extends AppCompatActivity {
    private RoundProgressBar mRoundProgressBar;
    private int progress = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRoundProgressBar = (RoundProgressBar)findViewById(R.id.roundProgressBar);
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressBar();
            }
        });

    }



    public void startProgressBar() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (progress <= 99) {
                    progress += 1;

                    System.out.println(progress);
                    mRoundProgressBar.setProgress(progress);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
