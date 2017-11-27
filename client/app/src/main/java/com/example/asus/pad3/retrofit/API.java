package com.example.asus.pad3.retrofit;

import com.example.asus.pad3.model.Book;
import com.example.asus.pad3.model.BookAddResponse;
import com.example.asus.pad3.model.BooksResponse;
import com.example.asus.pad3.model.ReponseStudent;
import com.example.asus.pad3.model.Response;
import com.example.asus.pad3.model.Student;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface API {
    @FormUrlEncoded
    @POST("/students")
    Call<Response> createNewStudent(@Field("name") String name, @Field("year") String year,
                                    @Field("phone") String phone );

    @FormUrlEncoded
    @POST("/students/{studentId}/books")
    Call<BookAddResponse> createBook(@Path("studentId") String name, @Field("year") String year,
                                     @Field("title") String title, @Field("author") String author, @Field("desc") String desc );

    @GET("/students")
    Call<ReponseStudent> getStudents();

    @GET("/students/{studentId}/books")
    Call<BooksResponse> getBooks(@Path("studentId") String studentId);
}