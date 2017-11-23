package com.example.asus.pad3.retrofit;

import com.example.asus.pad3.model.Response;
import com.example.asus.pad3.model.Student;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Ironman on 22.11.2017.
 */

public interface API {
    @FormUrlEncoded
    @POST("/students")
    //@Headers("Accept: application/json")
    Call<Response> createNewStudent(@Field("name") String name, @Field("year") String year,
                                    @Field("phone") String phone );
}
