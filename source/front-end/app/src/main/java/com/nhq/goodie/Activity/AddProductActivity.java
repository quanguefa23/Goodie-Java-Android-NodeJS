package com.nhq.goodie.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.nhq.goodie.API.API;
import com.nhq.goodie.API.ApiService;
import com.nhq.goodie.Class.LoadingDialog;
import com.nhq.goodie.R;
import com.nhq.goodie.Class.SetUIHideKeyboard;
import com.nhq.goodie.Class.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddProductActivity extends AppCompatActivity {

    Button okBT;
    LoadingDialog loadingDialog;

    EditText titleET;
    EditText priceET;
    EditText producerET;
    EditText stateET;
    EditText colorET;
    EditText moreET;
    EditText ownerET;
    EditText phoneET;
    EditText addressET;

    ArrayList<String> listImg = new ArrayList<>();
    int type;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        new SetUIHideKeyboard(AddProductActivity.this,
                findViewById(R.id.parent_add_product)).setupUI();

        mapWidget();

        configSpinnerForTypeProduct();
        configSpinnerForZone();

        configAddImageFunction();
        getContactInformation();
        setOnClickForContinueButton();

        uploadImage();
    }

    private void mapWidget() {
        titleET = findViewById(R.id.name);
        priceET = findViewById(R.id.price);
        producerET = findViewById(R.id.company);
        stateET = findViewById(R.id.levelOfNew);
        colorET = findViewById(R.id.color);
        moreET = findViewById(R.id.other);
        ownerET = findViewById(R.id.name_seller);
        phoneET = findViewById(R.id.phone_seller);
        addressET = findViewById(R.id.address_seller);
    }

    private void setOnClickForContinueButton() {
        okBT = findViewById(R.id.ok);
        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String img = "https://cdn.tgdd.vn/Products/Images/42/190323/iphone-xs-gold-400x460.png";

                //get image
                if (listImg.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Bạn chưa thêm hình ảnh sản phẩm hoặc ảnh chưa được load xong",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                String img = listImg.get(0);

                String title = titleET.getText().toString();
                if (title.equals("")) {
                    Toast.makeText(AddProductActivity.this, "Bạn chưa nhập tên sản phẩm",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String price = priceET.getText().toString();
                if (price.equals("")) {
                    Toast.makeText(AddProductActivity.this, "Bạn chưa nhập giá sản phẩm",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String owner = ownerET.getText().toString();
                if (owner.equals("")) {
                    Toast.makeText(AddProductActivity.this, "Bạn chưa nhập tên người bán",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String phone = phoneET.getText().toString();
                if (phone.equals("")) {
                    Toast.makeText(AddProductActivity.this, "Bạn chưa nhập số điện thoại",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String producer = producerET.getText().toString();
                String state = stateET.getText().toString();
                String color = colorET.getText().toString();
                String more = moreET.getText().toString();
                String address = addressET.getText().toString();

                //show loading dialog
                loadingDialog = new LoadingDialog(AddProductActivity.this);
                loadingDialog.start();

                //send data to server
                Call<String> addProduct = API.getInstance().addProduct(title, type, location, price, producer,
                        state, color, more, img, owner, address, phone);
                addProduct.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        loadingDialog.stop();
                        String s = response.body();
                        if(s.equals("true")) {
                            Toast.makeText(AddProductActivity.this, "Rao bán sản phẩm thành công",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            overridePendingTransition(R.anim.anim_in1, R.anim.anim_out1);
                        }
                        else {
                            Toast.makeText(AddProductActivity.this, "Rao bán sản phẩm không thành công",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        loadingDialog.stop();
                        Log.d("QUANG", t.toString());
                        Toast.makeText(AddProductActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void getContactInformation() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginState", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        Call<User> getUser = API.getInstance().getUserInfo(username);
        getUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                ownerET.setText(user.getFullname());
                phoneET.setText(user.getPhone());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AddProductActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                Log.d("QUANG", t.toString());
            }
        });
    }

    private void configSpinnerForZone() {
        final String[] listZone = {"TP. Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Bắc Ninh",
                "Cần Thơ", "Bình Dương"};
        Spinner spinner = findViewById(R.id.zone);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, listZone);
        spinner.setAdapter(adapter);

        location = listZone[0];
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                location = listZone[position];
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void configSpinnerForTypeProduct() {
        String[] listTypeProduct = {"Điện thoại - Máy tính bảng", "Laptop",
                "Xe máy", "Xe hơi", "Thời trang", "Thú cưng"};
        Spinner spinner = findViewById(R.id.type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, listTypeProduct);
        spinner.setAdapter(adapter);

        type = 1;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                type = position + 1;
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in1, R.anim.anim_out1);
    }

    public void onBackFunction(View view) {
        onBackPressed();
    }

    // ----------- UPLOAD IMAGE -----------------

    ArrayList<ImageView> listIV;
    int count_img = 0;
    int REQUEST_CODE_IMAGE = 123;
    ArrayList<Bitmap> listBitmap = new ArrayList<>();
    ApiService apiService;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;

    private void askPermissions() {
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT);
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        apiService = new Retrofit.Builder().baseUrl("https://goodie-server.herokuapp.com/").
                client(client).build().create(ApiService.class);
    }

    private void multipartImageUpload(Bitmap mBitmap) {
        try {
            //show loading dialog
            Toast.makeText(AddProductActivity.this, "Bắt đầu upload", Toast.LENGTH_LONG).show();
            loadingDialog = new LoadingDialog(AddProductActivity.this);
            loadingDialog.start();

            File filesDir = getApplicationContext().getFilesDir();
            File file = new File(filesDir, "image" + ".png");

            OutputStream os;

            try {
                os = new FileOutputStream(file);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();


            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");

            Call<ResponseBody> req = apiService.postImage(body, name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String[] res = response.toString().split("url=");
                    String url = res[1].replace("}", "");

//                    Log.d("QUANG", url);
                    Toast.makeText(getApplicationContext(),
                            "Upload ảnh thành công", Toast.LENGTH_SHORT).show();
                    listImg.add(url);

                    loadingDialog.stop();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),
                            "Upload ảnh không thành công, vui lòng tải lại trang",
                            Toast.LENGTH_SHORT).show();
                    t.printStackTrace();

                    loadingDialog.stop();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configAddImageFunction() {
        askPermissions();
        initRetrofitClient();

        listIV = new ArrayList<>();

        //generate origin state
        for (int i = 0; i < 5; i++) {
            String IDString = "img_" + i;
            int ID  = getResources().getIdentifier(IDString, "id", getPackageName());
            ImageView newIV = findViewById(ID);

            if (i == 0) {
                String IdImgString = "add_image";
                int IdImg = getResources().getIdentifier(IdImgString,
                        "drawable", getPackageName());
                newIV.setImageResource(IdImg);
            }
            else {
                newIV.setImageResource(android.R.color.transparent);
            }

            listIV.add(newIV);
        }

        //set on click for each imageView
        for(int i = 0; i < 5; i++) {
            final int index = i;
            ImageView imageView = listIV.get(i);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (count_img == index) {
                        //open intent picker
                        startActivityForResult(getPickImageChooserIntent(), REQUEST_CODE_IMAGE + index);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("QUANG", "onActivityResult");
        if (requestCode >= REQUEST_CODE_IMAGE && requestCode <= REQUEST_CODE_IMAGE + 4 &&
                resultCode == RESULT_OK) {

            //set image for current imageView
            int index = requestCode - REQUEST_CODE_IMAGE;
            ImageView imageView = listIV.get(index);

            String filePath = getImageFilePath(data);
            if (filePath != null) {
                Bitmap mBitmap = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(mBitmap);
                listBitmap.add(mBitmap);
            }

            //set default image for the next imageView
            if (count_img < 4) {
                ImageView next = listIV.get(index + 1);
                String IdImgString = "add_image";
                int IdImg = getResources().getIdentifier(IdImgString,
                        "drawable", getPackageName());
                next.setImageResource(IdImg);
            }

            //increase number of image
            count_img++;

            //Toast.makeText(this, "finish activity result", Toast.LENGTH_SHORT).show();
        }
    }

    boolean isUploaded = false;
    public void uploadImage() {
        Button upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUploaded) {
                    Toast.makeText(AddProductActivity.this, "Chỉ được upload 1 lần", Toast.LENGTH_LONG).show();
                    return;
                }
                if (listBitmap.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Bạn chưa chọn ảnh nào", Toast.LENGTH_LONG).show();
                    return;
                }

                isUploaded = true;


//                for (Bitmap bitmap : listBitmap) {
//                    multipartImageUpload(bitmap);
//                }
                multipartImageUpload(listBitmap.get(0));
            }
        });
    }
}
