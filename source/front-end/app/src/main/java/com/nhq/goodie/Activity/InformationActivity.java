package com.nhq.goodie.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nhq.goodie.API.API;
import com.nhq.goodie.Class.LoadingDialog;
import com.nhq.goodie.R;
import com.nhq.goodie.Class.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformationActivity extends AppCompatActivity {


    TextView fullnameTV;
    TextView phoneTV;
    TextView emailTV;
    TextView sexTV;
    TextView dobTV;

    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        mapWidget();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.start();
        getUserInforAndSetTextview();
    }

    private void getUserInforAndSetTextview() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        Call<User> getUser = API.getInstance().getUserInfo(username);
        getUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                fullnameTV.setText(user.getFullname());
                phoneTV.setText(user.getPhone());
                emailTV.setText(user.getMail());
                dobTV.setText(user.getDob());
                switch (user.getSex()) {
                    case 0: {
                        sexTV.setText("nam");
                        break;
                    }
                    case 1: {
                        sexTV.setText("nữ");
                        break;
                    }
                    default: sexTV.setText("khác");
                }

                loadingDialog.stop();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loadingDialog.stop();
                Toast.makeText(InformationActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                Log.d("QUANG", t.toString());
            }
        });
    }


    public void onBackFunction(View view) {
        onBackPressed();
    }

    private void mapWidget() {
        fullnameTV = findViewById(R.id.name);
        phoneTV = findViewById(R.id.phone);
        emailTV = findViewById(R.id.email);
        sexTV = findViewById(R.id.sex);
        dobTV = findViewById(R.id.dob);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in1, R.anim.anim_out1);
    }
}
