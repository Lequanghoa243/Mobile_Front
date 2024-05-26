package com.example.mobileproject.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.MainActivity;
import com.example.mobileproject.R;
import com.example.mobileproject.adapter.CategoryAdapter;
import com.example.mobileproject.adapter.CategoryAdapterMain;
import com.example.mobileproject.adapter.CourseAdapter;
import com.example.mobileproject.adapter.CourseAdapterCategory;
import com.example.mobileproject.model.Course;
import com.example.mobileproject.retrofit.ApiInterface;
import com.example.mobileproject.retrofit.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Category extends AppCompatActivity implements CategoryAdapterMain.OnCategoryClickListener {
    BottomNavigationView bottomNavigationView;
    RecyclerView categoryRecyclerView, courseRecyclerView;
    CourseAdapterCategory courseAdapter;
    CategoryAdapterMain categoryAdapter;
    ApiInterface apiInterface;
    List<Course> courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiInterface = RetrofitClient.getRetrofitClient().create(ApiInterface.class);
        categoryRecyclerView = findViewById(R.id.category_recycler);
        courseRecyclerView = findViewById(R.id.course_of_category_recycler);
        loadCategories();
        loadCourses();

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.category);
        // Programmatically find the menu items and set onClickListeners
        bottomNavigationView.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Category.this, MainActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView.findViewById(R.id.my_course).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Category.this, MyCourse.class);
                startActivity(intent);

            }
        });

        bottomNavigationView.findViewById(R.id.category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        bottomNavigationView.findViewById(R.id.user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Profile activity
                Intent intent = new Intent(Category.this, Profile.class);
                startActivity(intent);
            }
        });
    }

    private void loadCategories() {
        Call<List<com.example.mobileproject.model.Category>> call = apiInterface.getAllCategory();
        call.enqueue(new Callback<List<com.example.mobileproject.model.Category>>() {
            @Override
            public void onResponse(Call<List<com.example.mobileproject.model.Category>> call, Response<List<com.example.mobileproject.model.Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.mobileproject.model.Category> categoryList = response.body();
                    getAllCategory(categoryList);
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<com.example.mobileproject.model.Category>> call, Throwable t) {
                Toast.makeText(Category.this, "No response from server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCourses() {
        Call<List<Course>> callCourse = apiInterface.getAllCourse();
        callCourse.enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    courseList = response.body();
                    getAllCourse(courseList);
                } else {
                    if (response.body() == null) {

                    }
                }
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                Toast.makeText(Category.this, "No response from server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllCategory(List<com.example.mobileproject.model.Category> categoryList) {
        if (categoryList == null) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryRecyclerView.setHasFixedSize(true);
        categoryAdapter = new CategoryAdapterMain(this, categoryList, this);
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();
    }

    private void getAllCourse(List<Course> courseList) {
        if (courseList == null) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        courseRecyclerView.setLayoutManager(layoutManager);
        courseRecyclerView.setHasFixedSize(true);
        courseAdapter = new CourseAdapterCategory(this, courseList);
        courseRecyclerView.setAdapter(courseAdapter);
        courseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCategoryClick(com.example.mobileproject.model.Category category) {
        if (courseList == null) {
            return;
        }
        List<Course> filteredCourses = new ArrayList<>();
        for (Course course : courseList) {
            if (course.getCategory().equals(category.getTitle())) {
                filteredCourses.add(course);
            }
        }
        getAllCourse(filteredCourses);
    }
}
