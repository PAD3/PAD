package com.example.asus.pad3;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.asus.pad3.model.BookAddResponse;
import com.example.asus.pad3.model.ReponseStudent;
import com.example.asus.pad3.model.Response;
import com.example.asus.pad3.model.Student;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class StudentFragment extends Fragment implements StudentAdapter.ItemClickChild, StudentAdapter.ItemClickChildDelete, StudentAdapter.ItemClickChildChange, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.students_recycler_view)
    RecyclerView studentsList;
    @BindView(R.id.buttonAdd)
    Button buttonAdd;
    @BindView(R.id.progressBar)
    View progress;
    private PopupWindow mPopupWindow;
    API student;
    String idChange;
    StudentAdapter categoryAdapter;
    private Retrofit retrofit;
    SharedPreferences sPref;
    List<Student> studentses = new ArrayList<>();
    private Navigator navigator;
    private String mParam1;
    private String mParam2;
    private boolean loading;
    private int offset = 0;
    private static final int DEFAULT_LIMIT = 6;

    public StudentFragment() {
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
        View v = inflater.inflate(R.layout.fragment_student, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);
        loadMore();
        studentsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (loading)
                    return;
                int visibleCount = recyclerView.getChildCount();
                int total = recyclerView.getLayoutManager().getItemCount();
                int firstVisible = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (firstVisible + visibleCount == total && total > 0 && total > visibleCount)
                    loadMore(); //novie nado gruziti po etomu usloviu... a voobshe fragment nado perepisati. tut mojno povesitsya.

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return v;
    }

    private void loadMore() {
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
        categoryAdapter = new StudentAdapter(getActivity(), this, this, this);
        studentsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        studentsList.setAdapter(categoryAdapter);
        student = retrofit.create(API.class);
        student.getStudents("", offset, DEFAULT_LIMIT).enqueue(new Callback<ReponseStudent>() {
            @Override
            public void onResponse(Call<ReponseStudent> call, retrofit2.Response<ReponseStudent> response) {
                if (response.body() != null) {
                    progress.setVisibility(View.GONE);
                    //studentses.addAll();
                    categoryAdapter.addItems(response.body().getResponse());
                    studentsList.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<ReponseStudent> call, Throwable t) {

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            navigator = (Navigator) getActivity();
        } catch (ClassCastException e) {
            throw new RuntimeException("Parent activity should implement Navigator!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onChildClick(String studentId) {
        BooksFragment booksFragment = new BooksFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", studentId);
        booksFragment.setArguments(bundle);
        navigator.navigate(booksFragment);
    }

//v klasse doljen bity toliko odin retrofit hotea bi!!! a voobshe ih ne doljno byti vnutri fragmentov! koshmar!! ujas! pizdets!! :D

    @OnClick(R.id.buttonAdd)
    public void addStudent(View view) {
        progress.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView1 = inflater.inflate(R.layout.add_student, null);
        LinearLayout mainRev = customView1.findViewById(R.id.mainRev);
        final EditText name = customView1.findViewById(R.id.nameEditText);
        final EditText year = customView1.findViewById(R.id.yearEditText);
        final EditText phone = customView1.findViewById(R.id.phoneEditText);
        Button addStudentButton = customView1.findViewById(R.id.addStudentButton);
        final String phone1 = phone.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(customView1);
        final AlertDialog alert = builder.create();
        alert.show();
        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(name.getText().toString())) {
                    name.setError("Is Empty");
                }

                if (TextUtils.isEmpty(year.getText().toString())) {
                    year.setError("Is Empty");
                }
                if (TextUtils.isEmpty(phone.getText().toString()) && !phone1.contains("+")) {
                    phone.setError("Is Empty or add +");
                }
                if (!TextUtils.isEmpty(name.getText().toString()) &
                        !TextUtils.isEmpty(year.getText().toString())
                        && !TextUtils.isEmpty(phone.getText().toString())) {
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
                    student.createNewStudent(name.getText().toString(), year.getText().toString(), phone.getText().toString()).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if (response.body() != null) {
                                progress.setVisibility(View.GONE);
                                studentses.add(new Student(response.body().getResponse().getPhone(), response.body().getResponse().getYear(), response.body().getResponse().getName(),
                                        response.body().getResponse().getId()));
                                categoryAdapter.swap(studentses);
                                alert.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "Validate not correct", Toast.LENGTH_SHORT).show();
                                progress.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<com.example.asus.pad3.model.Response> call, Throwable t) {
//blin... takimi tempami mojet poehati udalenie i vstavka. spisok etot vnutri adaptera doljen byti, ty s adapterom razgovarivaeshi a ego ne trogaiesh
                        }
                    });


                }

            }
        });
    }

    @Override
    public void onChildClickDelete(String studentId) {
        deleteStudent(studentId);
    }

    public void deleteStudent(String id) {
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
        student.deleteStudent(id).enqueue(new Callback<BookAddResponse>() {
            @Override
            public void onResponse(Call<BookAddResponse> call, retrofit2.Response<BookAddResponse> response) {
                Toast.makeText(getActivity(), "id delete succes", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BookAddResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onChildClickChange(String studentId, final int pos) {
        idChange = studentId;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView1 = inflater.inflate(R.layout.add_student, null);
        LinearLayout mainRev = customView1.findViewById(R.id.mainRev);
        final EditText name = customView1.findViewById(R.id.nameEditText);
        final EditText year = customView1.findViewById(R.id.yearEditText);
        final EditText phone = customView1.findViewById(R.id.phoneEditText);
        Button addStudentButton = customView1.findViewById(R.id.addStudentButton);
        final String phone1 = phone.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(customView1);
        final AlertDialog alert = builder.create();
        alert.show();
        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                student.changeStudent(idChange, "Alex4545", "1995", "199556565").enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "ok", Toast.LENGTH_SHORT).show();
                        alert.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        alert.dismiss();
                        Toast.makeText(getActivity(), "fall", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
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
        categoryAdapter = new StudentAdapter(getActivity(), this, this, this);
        studentsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        studentsList.setAdapter(categoryAdapter);
        student = retrofit.create(API.class);
        student.getStudents(newText, 0, 30).enqueue(new Callback<ReponseStudent>() {
            @Override
            public void onResponse(Call<ReponseStudent> call, retrofit2.Response<ReponseStudent> response) {
                if (response.body() != null) {
                    progress.setVisibility(View.GONE);
                    studentses.addAll(response.body().getResponse());
                    categoryAdapter.addItems(studentses);
                    studentsList.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<ReponseStudent> call, Throwable t) {

            }
        });
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");

        super.onCreateOptionsMenu(menu, inflater);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }
}
