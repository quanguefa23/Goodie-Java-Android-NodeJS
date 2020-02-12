package com.nhq.goodie.API;

import com.nhq.goodie.Class.Notice;
import com.nhq.goodie.Class.ShortProduct;
import com.nhq.goodie.Class.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonApi {
    @GET("/login")
    Call<String> checkValidAccount(@Query("user") String user,
                             @Query("pass") String pass);

    @GET("/adduser")
    Call<String> registerAccount(@Query("fullname") String fullname,
                                 @Query("phone") String phone,
                                 @Query("mail") String mail,
                                 @Query("sex") int sex,
                                 @Query("pass") String pass,
                                 @Query("dob") String dob);

    @GET("/product")
    Call<List<ShortProduct>> getProduct(@Query("type") int type);

    @POST("/product")
    Call<String> addProduct(@Query("title") String title,
                            @Query("type") int type,
                            @Query("location") String location,
                            @Query("price") String price,
                            @Query("producer") String producer,
                            @Query("state") String state,
                            @Query("color") String color,
                            @Query("more") String more,
                            @Query("img") String img,
                            @Query("owner") String owner,
                            @Query("address") String address,
                            @Query("phone") String phone);

    @GET("/getuserinfo")
    Call<User> getUserInfo(@Query("user") String user);

    @GET("/notice")
    Call<List<Notice>> getNotices(@Query("user") String user);

    @GET("/seen")
    Call<String> setNoticeMark(@Query("id") String id);
}
