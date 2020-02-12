package com.nhq.goodie.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.nhq.goodie.API.API;
import com.nhq.goodie.Class.LoadingDialog;
import com.nhq.goodie.R;
import com.nhq.goodie.Class.SetUIHideKeyboard;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText fullnameET;
    EditText dobET;
    RadioGroup sexualityRG;
    EditText phone_numberET;
    EditText emailET;
    EditText passET;
    EditText confirm_passET;
    CheckBox accept_ruleCB;
    Button cancelBT;
    Button okBT;

    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        new SetUIHideKeyboard(RegisterActivity.this,
                findViewById(R.id.parent_register)).setupUI();

        mapWidget();
        configDOBEditText();

        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sexCode;
                switch (sexualityRG.getCheckedRadioButtonId()) {
                    case R.id.isMale: {
                        sexCode = 0;
                        break;
                    }
                    case R.id.isFemale: {
                        sexCode = 1;
                        break;
                    }
                    default:
                        sexCode = 2;
                }

                String fullname = fullnameET.getText().toString();
                if (fullname.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Bạn chưa nhập họ và tên",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String dob = dobET.getText().toString();
                if (dob.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Bạn chưa nhập ngày sinh",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String phone_number = phone_numberET.getText().toString();
                if (phone_number.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Bạn chưa nhập số điện thoại",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (phone_number.length() < 10) {
                    Toast.makeText(RegisterActivity.this, "Số điện thoại không hợp lệ",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String email = emailET.getText().toString();
                if (email.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Bạn chưa nhập email",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String pass = passET.getText().toString();
                if (pass.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Bạn chưa nhập mật khẩu",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (pass.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu quá ngắn",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (pass.length() > 24) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu quá dài",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String confirm_pass = confirm_passET.getText().toString();
                if (!pass.equals(confirm_pass)) {
                    Toast.makeText(RegisterActivity.this, "Xác nhận mật khẩu không khớp",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (!accept_ruleCB.isChecked()) {
                    Toast.makeText(RegisterActivity.this, "Bạn chưa chấp nhận điều khoản",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                //show loading dialog
                loadingDialog = new LoadingDialog(RegisterActivity.this);
                loadingDialog.start();

                //String sexString = Integer.toString(sexCode);
                //send data to server
                Call<String> register = API.getInstance().registerAccount(fullname, phone_number, email, sexCode, pass, dob);
                register.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        loadingDialog.stop();
                        String s = response.body();
                        if(s.equals("true")) {
                            Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thành công",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            overridePendingTransition(R.anim.anim_in1, R.anim.anim_out1);
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Email hoặc số điện thoại đã tồn tại, đăng ký không thành công",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        loadingDialog.stop();
                        Log.d("QUANG", t.toString());
                        Toast.makeText(RegisterActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void configDOBEditText() {
        dobET.setKeyListener(null);

        dobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogInputDate();
            }
        });
    }

    private void showDialogInputDate() {
        final Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog dialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
                dobET.setText(formatDate.format(calendar.getTime()));
            }
        }, year, month, date);

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in1, R.anim.anim_out1);
    }

    private void mapWidget() {
        fullnameET = findViewById(R.id.fullname);
        dobET = findViewById(R.id.dob);
        sexualityRG = findViewById(R.id.sexuality);
        phone_numberET = findViewById(R.id.phone_number);
        emailET = findViewById(R.id.email);
        passET = findViewById(R.id.password);
        confirm_passET = findViewById(R.id.confirm_password);
        accept_ruleCB = findViewById(R.id.checkbox_rule);
        cancelBT = findViewById(R.id.cancel);
        okBT = findViewById(R.id.ok);
    }
}
