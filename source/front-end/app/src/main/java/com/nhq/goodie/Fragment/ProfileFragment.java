package com.nhq.goodie.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhq.goodie.API.API;
import com.nhq.goodie.Activity.InformationActivity;
import com.nhq.goodie.Activity.LoginActivity;
import com.nhq.goodie.R;
import com.nhq.goodie.Class.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }


    LinearLayout informationLL;
    LinearLayout logoutLL;
    TextView fullNameTV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        informationLL = view.findViewById(R.id.information);
        logoutLL = view.findViewById(R.id.logout);
        fullNameTV = view.findViewById(R.id.name);

        //get user infor
        getUserInforAndSetTextview();

        //information function
        setOnClickForInformation();

        //logout function
        setOnClickForLogout();

        return view;
    }

    private void getUserInforAndSetTextview() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginState", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        Call<User> getUser = API.getInstance().getUserInfo(username);
        getUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                fullNameTV.setText(user.getFullname());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                Log.d("QUANG", t.toString());
            }
        });
    }

    private void setOnClickForInformation() {
        informationLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InformationActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }
        });
    }

    private void setOnClickForLogout() {
        logoutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogLogout();
            }
        });
    }

    private void showDialogLogout() {
        Log.d("State", "showDialog");
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_out);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button bt_cancel = dialog.findViewById(R.id.out_cancel);
        Button bt_ok = dialog.findViewById(R.id.out_ok);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                //change data preferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginState", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("state", 0);
                editor.putString("username", "");
                editor.apply();

                //switch intent
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_in1, R.anim.anim_out1);
                getActivity().finish();
            }
        });

        dialog.show();
    }

}
