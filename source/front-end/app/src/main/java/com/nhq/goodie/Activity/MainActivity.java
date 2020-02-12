package com.nhq.goodie.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nhq.goodie.Fragment.HomeFragment;
import com.nhq.goodie.Fragment.ProfileFragment;
import com.nhq.goodie.R;
import com.nhq.goodie.Fragment.SearchFragment;
import com.nhq.goodie.Class.SetUIHideKeyboard;
import com.nhq.goodie.Fragment.NoticeFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    RelativeLayout line;
    int activeFragmentIndex;

    ArrayList<Fragment> listFragment;
    ArrayList<RelativeLayout> listLayoutForFragment;

    String username;
    ImageView addProductIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up UI
        new SetUIHideKeyboard(MainActivity.this,
                findViewById(R.id.parent_main)).setupUI();

        //remember login
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        //initial listLayout
        listLayoutForFragment = new ArrayList<>();
        initialListLayout();

        //add fragment to list
        listFragment = new ArrayList<>();
        listFragment.add(new HomeFragment());
        listFragment.add(new SearchFragment());
        listFragment.add(new NoticeFragment());
        listFragment.add(new ProfileFragment());

        //add fragment for each layout
        for (int i = 0; i < 4; i++)
            addFragment(listFragment.get(i), i);

        //r.setVisibility(View.INVISIBLE);
        //set firstFragment active
        line = findViewById(R.id.line0);
        activeFragmentIndex = 0;

        //add product function
        addProductIV = findViewById(R.id.addProduct);
        setOnClickForAddProduct();
    }

    private void setOnClickForAddProduct() {
        addProductIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }
        });
    }

    private void addFragment(Fragment fragment, int index) {
        RelativeLayout layout = listLayoutForFragment.get(index);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(layout.getId(), fragment, index + "");
        fragmentTransaction.commit();
    }

    private void initialListLayout() {
        for (int i = 0; i < 4; i++) {
            String IDString = "fragment_" + i;
            int ID = getResources().getIdentifier(IDString, "id", getPackageName());
            RelativeLayout newLayout = findViewById(ID);
            listLayoutForFragment.add(newLayout);
        }
    }


    private void doInEachCase(int indexInThisCase) {
        if (activeFragmentIndex != indexInThisCase) {
            line.setBackgroundResource(R.color.transparent);
            listLayoutForFragment.get(activeFragmentIndex).setVisibility(View.INVISIBLE);

            String IDString = "line" + indexInThisCase;
            int ID = getResources().getIdentifier(IDString, "id", getPackageName());
            line = findViewById(ID);
            activeFragmentIndex = indexInThisCase;
        }
    }

    public void switchFragment(View view) {
        switch (view.getId()) {
            case R.id.home: {
                doInEachCase(0);
                break;
            }
            case R.id.search: {
                doInEachCase(1);
                break;
            }
            case R.id.notification: {
                doInEachCase(2);
                break;
            }
            case R.id.profile: {
                doInEachCase(3);
                break;
            }
        }

        listLayoutForFragment.get(activeFragmentIndex).setVisibility(View.VISIBLE);
        line.setBackgroundResource(R.color.colorMain);
    }

    public void switchToDetailActivity(View view) {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("0");
        homeFragment.switchToDetailActivityInFragment(view);
    }
}
