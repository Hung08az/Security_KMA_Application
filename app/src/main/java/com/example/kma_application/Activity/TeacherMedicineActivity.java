package com.example.kma_application.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.kma_application.Models.Teacher;
import com.example.kma_application.R;

public class TeacherMedicineActivity extends AppCompatActivity {

    Teacher teacher;
    ListView lvClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_medicine);
    }
}