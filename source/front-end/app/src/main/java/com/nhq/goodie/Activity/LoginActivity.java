package com.nhq.goodie.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhq.goodie.API.API;
import com.nhq.goodie.Class.LoadingDialog;
import com.nhq.goodie.R;
import com.nhq.goodie.Class.SetUIHideKeyboard;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText usernameET;
    EditText passwordET;
    Button confirmBT;
    TextView forgetPassTV;
    ImageView facebookIV;
    ImageView googleIV;
    Button registerBT;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new SetUIHideKeyboard(LoginActivity.this,
                findViewById(R.id.parent_login)).setupUI();

        SharedPreferences sharedPreferences = getSharedPreferences("loginState", MODE_PRIVATE);
        int state = sharedPreferences.getInt("state", 0);
        if (state == 1) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", sharedPreferences.getString("username", "null"));
            startActivity(intent);
            finish();
        }

        mapWidget();

        confirmBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameET.getText().toString();
                String password = passwordET.getText().toString();

                if (username.equals("")) {
                    Toast.makeText(LoginActivity.this, "Bạn chưa nhập tên đăng nhập",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Mật khẩu quá ngắn",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (password.length() > 24) {
                    Toast.makeText(LoginActivity.this, "Mật khẩu quá dài",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                loadingDialog = new LoadingDialog(LoginActivity.this);
                loadingDialog.start();
                Call<String> checkLogin = API.getInstance().checkValidAccount(username, password);
                checkLogin.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String s = response.body();
                        Log.d("QUANGLOGIN", s);
                        if(s.equals("true")) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công",
                                    Toast.LENGTH_LONG).show();

                            //remember login
                            SharedPreferences sharedPreferences = getSharedPreferences("loginState", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("state", 1);
                            editor.putString("username", username);
                            editor.apply();

                            loadingDialog.stop();

                            //switch intent
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                            finish();
                        }
                        else {
                            loadingDialog.stop();
                            Toast.makeText(LoginActivity.this, "Tên đăng nhập hoặc mật khẩu không hợp lệ",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        loadingDialog.stop();
                        Log.d("QUANG", t.toString());
                        Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        registerBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }
        });
    }

    private void mapWidget() {
        usernameET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);
        confirmBT = findViewById(R.id.confirm);
        forgetPassTV = findViewById(R.id.forgetPass);
        facebookIV = findViewById(R.id.facebook);
        googleIV = findViewById(R.id.google);
        registerBT = findViewById(R.id.register);
    }
}
