package com.nhq.goodie.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nhq.goodie.Class.LoadingDialog;
import com.nhq.goodie.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ProductDetailActivity extends AppCompatActivity {

    private static final int REQUEST_PHONE_CALL = 2222;
    TextView priceTV;
    TextView timeTV;
    TextView nameTV;
    TextView locationTV;
    TextView companyTV;
    TextView stateTV;
    TextView colorTV;
    TextView moreTV;
    TextView soldTV;
    TextView typeTagTV;
    TextView locationTagTV;
    TextView nameSellerTV;
    TextView phoneSellerTV;
    TextView addressSellerTV;

    String productID;
    String phoneSeller = "0123456789";
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Intent intent = getIntent();
        productID = intent.getStringExtra("id");

        mapWidget();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.start();
        getDataAndSetView();
    }

    public void onBackFunction(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in1, R.anim.anim_out1);
    }

    private void mapWidget() {
        priceTV = findViewById(R.id.product_price);
        timeTV = findViewById(R.id.product_time);
        nameTV = findViewById(R.id.product_name);
        locationTV = findViewById(R.id.product_location);
        stateTV = findViewById(R.id.product_state);
        colorTV = findViewById(R.id.product_color);
        moreTV = findViewById(R.id.product_more);
        soldTV = findViewById(R.id.product_sold);
        typeTagTV = findViewById(R.id.tag_type);
        companyTV = findViewById(R.id.product_company);
        locationTagTV = findViewById(R.id.tag_location);
        nameSellerTV = findViewById(R.id.name_seller);
        phoneSellerTV = findViewById(R.id.phone_seller);
        addressSellerTV = findViewById(R.id.address_seller);
    }

    private void getDataAndSetView() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = getURL();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //image
                            String imgURL = response.getString("img");
                            new LoadImage().execute(imgURL, "product_img");

                            //price
                            String priceString = response.getString("price");
                            String[] split = priceString.split(" ");
                            priceTV.setText("Giá: " + split[0] +"đ");

                            //time
                            String timeString = response.getString("date") + " "
                                    + response.getString("time");
                            timeTV.setText(timeString);

                            //title
                            nameTV.setText(response.getString("title"));

                            //location
                            locationTV.setText(response.getString("location"));

                            //state
                            stateTV.setText(response.getString("state"));

                            //company
                            companyTV.setText(response.getString("producer"));

                            //color
                            colorTV.setText(response.getString("color"));

                            //more
                            moreTV.setText(response.getString("more"));

                            //sold
                            int soldInt = Integer.parseInt(response.getString("sold"));
                            if (soldInt == 0) {
                                soldTV.setText("đang bán");
                                soldTV.setTextColor(getResources().getColor(R.color.green));
                            }
                            else {
                                soldTV.setText("đã bán");
                                soldTV.setTextColor(getResources().getColor(R.color.red));
                            }

                            //tag
                            typeTagTV.setText(response.getString("type"));
                            locationTagTV.setText(response.getString("location"));

                            //seller
                            nameSellerTV.setText(response.getString("owner"));
                            phoneSeller = response.getString("phone");
                            phoneSellerTV.setText(phoneSeller);
                            addressSellerTV.setText(response.getString("address"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.stop();
                        Log.d("QUANG", error.toString());
                        Toast.makeText(ProductDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }

    private String getURL() {
        return "https://goodie-server.herokuapp.com/detail/?id=" + productID;
    }

    public void CallFunction(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneSeller));
        if (ContextCompat.checkSelfPermission(ProductDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProductDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Chuẩn bị gọi đến " + phoneSeller, Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        Log.d("QUANG", "call");
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
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
            imgID  = getResources().getIdentifier(imgString, "id", getPackageName());
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imgIV = findViewById(imgID);
            imgIV.setImageBitmap(bitmap);
            if (imgString.equals("product_img")) {
                loadingDialog.stop();
            }
        }
    }
}
