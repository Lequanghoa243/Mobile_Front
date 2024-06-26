package com.example.mobileproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.Pages.VideoPlay;
import com.example.mobileproject.R;
import com.example.mobileproject.model.Lesson;
import com.example.mobileproject.model.LessonRequest;
import com.example.mobileproject.retrofit.ApiInterface;
import com.example.mobileproject.retrofit.RetrofitClient;
import com.example.mobileproject.utils.SharedPreferencesManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private static final String TAG = "LessonAdapter";
    private Context context;
    private List<Lesson> lessonList;
    private String courseId;
    private String activeVideoUrl;
    private ApiInterface apiInterface;
    private SharedPreferencesManager sharedPreferencesManager;

    public LessonAdapter(Context context, List<Lesson> lessonList, String courseId) {
        this.context = context;
        this.lessonList = lessonList;
        this.courseId = courseId;
        this.apiInterface = RetrofitClient.getRetrofitClient().create(ApiInterface.class);
        this.sharedPreferencesManager = new SharedPreferencesManager(context);
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lesson_item, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessonList.get(position);
        holder.lessonTitle.setText(lesson.getTitle());

        if (lesson.getVideoURL().equals(activeVideoUrl)) {
            holder.itemView.setBackgroundResource(R.drawable.shapeshadow_active); // Background for active video
        } else {
            holder.itemView.setBackgroundResource(R.drawable.shapeshadow); // Background for inactive video
        }

        holder.itemView.setOnClickListener(v -> {
            activeVideoUrl = lesson.getVideoURL();
            Intent intent = new Intent(context, VideoPlay.class);
            intent.putExtra("VIDEO_URL", lesson.getVideoURL());
            intent.putExtra("COURSE_ID", courseId);
            fetchLessonDetails(lesson.getId());
            context.startActivity(intent);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public void setActiveVideoUrl(String activeVideoUrl) {
        this.activeVideoUrl = activeVideoUrl;
        notifyDataSetChanged();
    }

    private void fetchLessonDetails(String id) {
        String _id = sharedPreferencesManager.getUserId();
        LessonRequest lessonRequest = new LessonRequest(_id,id);
        apiInterface.getOneLesson(lessonRequest).enqueue(new Callback<Lesson>() {
            @Override
            public void onResponse(Call<Lesson> call, Response<Lesson> response) {
                if (response.isSuccessful() && response.body() != null) {
                } else {
                    Log.e(TAG, "Failed to get lesson details: " + response.message());
                    Toast.makeText(context, "Failed to get lesson details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Lesson> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(context, "Error fetching lesson details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView lessonTitle;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonTitle = itemView.findViewById(R.id.lesson_title);
        }
    }
}
