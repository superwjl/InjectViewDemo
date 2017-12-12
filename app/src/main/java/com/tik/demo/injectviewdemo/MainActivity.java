package com.tik.demo.injectviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tik.demo.inject.ContentView;
import com.tik.demo.inject.OnClick;
import com.tik.demo.inject.ViewInject;
import com.tik.demo.inject.ViewInjectUtils;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.hello)
    TextView hello;

    @OnClick(R.id.hello)
    void clickHello(View view){
        hello.setText("Nice to meet you!");
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);
    }
}
