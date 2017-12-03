package com.example.asus.pad3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.asus.pad3.model.BookAddResponse;
import com.example.asus.pad3.model.BooksResponse;
import com.example.asus.pad3.model.PayLoad;
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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class BooksFragment extends Fragment implements BooksAdapter.ItemClickChildDelete{
    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.students_recycler_view)
    RecyclerView studentsList;
    @BindView(R.id.buttonAdd)
    Button buttonAdd;
    List<PayLoad> studentses;
    private PopupWindow mPopupWindow;
    API student;
    String id = "";
    @BindView(R.id.progressBar)
    View progress;
    BooksAdapter categoryAdapter;
    private Retrofit retrofit;

    private String mParam1;
    private String mParam2;
    SharedPreferences sPref;

    private BooksFragment mListener;

    public BooksFragment() {
        // Required empty public constructor
    }

    public static StudentFragment newInstance(String param1, String param2) {
        StudentFragment fragment = new StudentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_books, container, false);
        ButterKnife.bind(this, v);
        Bundle bundle = this.getArguments();
        sPref = getActivity().getSharedPreferences("save", Context.MODE_PRIVATE);
        if(bundle!=null) {
            id = bundle.getString("id");
            Log.d("id",id);
            init(bundle.getString("id"));
        }
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void init(final String id) {
        studentses = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(interceptor)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://padlab.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        student = retrofit.create(API.class);
        categoryAdapter = new BooksAdapter(this);
        studentsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        studentsList.setAdapter(categoryAdapter);
        student.getBooks(id).enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, retrofit2.Response<BooksResponse> response) {
                progress.setVisibility(View.GONE);
                studentses.addAll(response.body().getPayload());
                categoryAdapter.addItems(studentses);
                studentsList.setAdapter(categoryAdapter);
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.buttonAdd)
    public void addStudent(View view) {
        progress.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView1 = inflater.inflate(R.layout.add_book, null);
        LinearLayout mainRev = customView1.findViewById(R.id.mainRev);
        final EditText title = customView1.findViewById(R.id.titleBook);
        final EditText author = customView1.findViewById(R.id.author);
        final EditText desc = customView1.findViewById(R.id.desc);
        final EditText yearOfBook = customView1.findViewById(R.id.yearOfBook);
        Button addStudentButton = customView1.findViewById(R.id.addStudentButton);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(customView1);
        final AlertDialog alert = builder.create();
        alert.show();
        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(title.getText().toString())) {
                    title.setError("Is Empty");
                }

                if (TextUtils.isEmpty(author.getText().toString())) {
                    author.setError("Is Empty");
                }
                if (TextUtils.isEmpty(desc.getText().toString())) {
                    desc.setError("Is Empty or add +");
                }
                if (!TextUtils.isEmpty(title.getText().toString()) &
                        !TextUtils.isEmpty(author.getText().toString())
                        && !TextUtils.isEmpty(desc.getText().toString())) {
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                            .addInterceptor(new Interceptor() {
                                @Override
                                public okhttp3.Response intercept(Chain chain) throws IOException {
                                    Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                                    return chain.proceed(request);
                                }
                            })
                            .addInterceptor(interceptor)
                            .build();
                    retrofit = new Retrofit.Builder()
                            .baseUrl("https://padlab.herokuapp.com")
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .client(okHttpClient)
                            .build();
                    student = retrofit.create(API.class);
                    student.createBook(id,yearOfBook.getText().toString(),title.getText().toString(),author.getText().toString(),desc.getText().toString()).enqueue(new Callback<BookAddResponse>() {
                        @Override
                        public void onResponse(Call<BookAddResponse> call, retrofit2.Response<BookAddResponse> response) {
                            if(response.body().getPayload()!=null) {
                                progress.setVisibility(View.GONE);
                                studentses.add(new PayLoad(response.body().getPayload().getTitle()
                                        , response.body().getPayload().getAuthor(), response.body().getPayload().getYear(), response.body().getPayload().getDesc()));
                                categoryAdapter.swap(studentses);
                                alert.dismiss();
                            }else {
                                Toast.makeText(getActivity(),"Validate not correct",Toast.LENGTH_SHORT).show();
                                progress.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<BookAddResponse> call, Throwable t) {

                        }
                    });

                }

            }
        });
    }

    @Override
    public void onChildClickDelete(String bookId) {
        deleteStudent(bookId);
    }

    public void deleteStudent(String bookid) {
        progress.setVisibility(View.VISIBLE);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(interceptor)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://padlab.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        student = retrofit.create(API.class);
        student.deleteBook(id,bookid).enqueue(new Callback<BookAddResponse>() {
            @Override
            public void onResponse(Call<BookAddResponse> call, Response<BookAddResponse> response) {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BookAddResponse> call, Throwable t) {

            }
        });
    }
}
