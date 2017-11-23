package com.example.asus.pad3;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.asus.pad3.retrofit.API;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements StudentAdapter.ItemClickChild {
    @BindView(R.id.students_recycler_view)
    RecyclerView studentsList;
    @BindView(R.id.buttonAdd)
    Button buttonAdd;
    List<com.example.asus.pad3.model.Student> studentses;
    private PopupWindow mPopupWindow;
    API student;
    StudentAdapter categoryAdapter;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        ButterKnife.bind(this);
        studentses = new ArrayList<>();
        studentses.add(new com.example.asus.pad3.model.Student("Name:Alex", "Year:1995", "+37369487615"));
        studentses.add(new com.example.asus.pad3.model.Student("Name:Alex", "Year:1995", "+37369487615"));
        studentses.add(new com.example.asus.pad3.model.Student("Name:Alex", "Year:1995", "+37369487615"));
        studentses.add(new com.example.asus.pad3.model.Student("Name:Alex", "Year:1995", "+37369487615"));
        studentses.add(new com.example.asus.pad3.model.Student("Name:Alex", "Year:1995", "+37369487615"));
        categoryAdapter = new StudentAdapter(studentses, this);
        studentsList.setLayoutManager(new LinearLayoutManager(this));
        studentsList.setAdapter(categoryAdapter);
    }

    @Override
    public void onChildClick(String position) {
        Intent intent = new Intent(MainActivity.this,BooksActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @OnClick(R.id.buttonAdd)
    public void addStudent(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView1 = inflater.inflate(R.layout.add_student, null);
        LinearLayout mainRev = (LinearLayout)customView1.findViewById(R.id.mainRev);
        final EditText name = (EditText)customView1.findViewById(R.id.nameEditText);
        final EditText year = (EditText)customView1.findViewById(R.id.yearEditText);
        final EditText phone = (EditText)customView1.findViewById(R.id.phoneEditText);
        Button addStudentButton = (Button)customView1.findViewById(R.id.addStudentButton);
        final String phone1 = phone.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false)
                .setView(customView1);
        final AlertDialog alert = builder.create();
        alert.show();
        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(name.getText().toString())){
                    name.setError("Is Empty");
                }

                if (TextUtils.isEmpty(year.getText().toString())){
                    year.setError("Is Empty");
                }
                if (TextUtils.isEmpty(phone.getText().toString()) && !phone1.contains("+")){
                    phone.setError("Is Empty or add +");
                }
                if (!TextUtils.isEmpty(name.getText().toString()) &
                        !TextUtils.isEmpty(year.getText().toString())
                        &&!TextUtils.isEmpty(phone.getText().toString())){
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                            .addInterceptor(new Interceptor() {
                                @Override
                                public okhttp3.Response intercept(Chain chain) throws IOException {
                                    Request request = chain.request().newBuilder().addHeader("Accept","application/json").build();
                                    return chain.proceed(request);
                                }
                            })
                            .addInterceptor(interceptor)
                            .build();
                    retrofit = new Retrofit.Builder()
                            .baseUrl("http://appapi.eu:4567")
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .client(okHttpClient)
                            .build();
                    student = retrofit.create(API.class);
                    student.createNewStudent(name.getText().toString(),year.getText().toString(),phone.getText().toString()).enqueue(new Callback<com.example.asus.pad3.model.Response>() {
                        @Override
                        public void onResponse(Call<com.example.asus.pad3.model.Response> call, Response<com.example.asus.pad3.model.Response> response) {
                            Log.d("response",response.body().getResponse().getName());
                        }

                        @Override
                        public void onFailure(Call<com.example.asus.pad3.model.Response> call, Throwable t) {

                        }
                    });

                    alert.dismiss();
                }

            }
        });
    }

}
