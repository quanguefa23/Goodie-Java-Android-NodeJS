package com.nhq.goodie.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.nhq.goodie.R;

public class ReadNotificationActivity extends AppCompatActivity {

    TextView titleTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_notification);

        Intent intent = getIntent();
        titleTV = findViewById(R.id.title);
        String content = "Bạn đang đọc thông báo có ID = "
                + intent.getIntExtra("id_notice", 0) + ", ấn back để trở về";
        titleTV.setText(content);
    }
}
