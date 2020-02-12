package com.nhq.goodie.Fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhq.goodie.API.API;
import com.nhq.goodie.Activity.ProductDetailActivity;
import com.nhq.goodie.Class.LoadingDialog;
import com.nhq.goodie.R;
import com.nhq.goodie.Class.ShortProduct;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    ArrayList<ShortProduct> generalList;
    ArrayList<ShortProduct> phoneAndTabletList;
    ArrayList<ShortProduct> laptopList;

    LoadingDialog loadingDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //swipe to refresh
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createView();
            }
        });

        //show loading dialog
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.start();

        //get data and create view
        createView();

        return view;
    }


    public void createView() {
        //get data from API and set them on start_view
        generalList = new ArrayList<>();
        getData(generalList, 0, "general");
        phoneAndTabletList = new ArrayList<>();
        getData(phoneAndTabletList, 1, "phone_tablet");
        laptopList = new ArrayList<>();
        getData(laptopList, 2, "laptop");
    }

    public void switchToDetailActivityInFragment(View view) {
        String idOfView_String = getId(view);
        String[] split = idOfView_String.split("_");

        if (split.length < 2)
            return;

        String type = split[0];
        int idInArray = Integer.parseInt(split[1]);
        String idProduct = "";
        switch (type) {
            case "general": {
                if (generalList.isEmpty())
                    return;
                idProduct = generalList.get(idInArray).getId();
                break;
            }
            case "phoneTablet": {
                if (phoneAndTabletList.isEmpty())
                    return;
                idProduct = phoneAndTabletList.get(idInArray).getId();
                break;
            }
            case "laptop": {
                if (laptopList.isEmpty())
                    return;
                idProduct = laptopList.get(idInArray).getId();
                break;
            }
        }

        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtra("id", idProduct);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    private static String getId(View view) {
        if (view.getId() == View.NO_ID)
            return "no-id";
        String[] id = view.getResources().getResourceName(view.getId()).split("/");
        return id[1];
    }

    private void getData(final ArrayList<ShortProduct> list, final int type, final String field) {
        Call<List<ShortProduct>> getProduct = API.getInstance().getProduct(type);
        getProduct.enqueue(new Callback<List<ShortProduct>>() {
            @Override
            public void onResponse(Call<List<ShortProduct>> call, Response<List<ShortProduct>> response) {
                List<ShortProduct> newList = response.body();
                for (int i = 0; i < newList.size(); i++ ) {
                    ShortProduct newProduct = newList.get(i).getDuplicate();
                    list.add(newProduct);

                    //set title
                    String titleIDString = field + "_title_" + i;
                    int titleID = getResources().getIdentifier(titleIDString, "id", getActivity().getPackageName());
                    TextView titleTV = getView().findViewById(titleID);
                    String titleContent = newProduct.getTitle();
                    if(titleContent.length() > 28)
                        titleContent = titleContent.substring(0,28) + "...";
                    titleTV.setText(titleContent);

                    //set price
                    String priceIDString = field + "_price_" + i;
                    int priceID = getResources().getIdentifier(priceIDString, "id", getActivity().getPackageName());
                    TextView priceTV = getView().findViewById(priceID);
                    String priceContent = newProduct.getPrice();
                    String[] split = priceContent.split(" ", 2);
                    priceContent = split[0];
                    priceTV.setText(priceContent);

                    //set image
                    String imgIDString = field + "_img_" + i;
                    new LoadImage().execute(newProduct.getImg(), imgIDString);
                }
            }

            @Override
            public void onFailure(Call<List<ShortProduct>> call, Throwable t) {
                loadingDialog.stop();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                Log.d("QUANG", t.toString());
            }
        });
    }

    public class LoadImage extends AsyncTask<String, Void, Bitmap> {

        String imgString;
        int imgID;
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(strings[0]);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }

            imgString = strings[1];
            imgID  = getResources().getIdentifier(imgString, "id", getActivity().getPackageName());
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imgIV = getView().findViewById(imgID);
            imgIV.setImageBitmap(bitmap);
            if (imgString.equals("laptop_img_2")) {
                Log.d("QUANG", "dialog cancel");
                loadingDialog.stop();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
