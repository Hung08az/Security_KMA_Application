package com.example.kma_application.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kma_application.Models.Parent;
import com.example.kma_application.Models.Teacher;
import com.example.kma_application.R;
import com.example.kma_application.Fragment.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.kma_application.AsyncTask.LoadInfosTask;
import com.example.kma_application.Models.Person;

public class MainActivity extends AppCompatActivity implements LoadInfosTask.AsyncResponse {
    //data
    String phone;
    String role;
    String _class;

    //model
    Person person;

    //fragment
    HomeFragment homeFragment;
    NotificationFragment notificationFragment;
    UserFragment userFragment;
    NewfeedFragment newfeedFragment;

    // a static variable to get a reference of our application context
    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contextOfApplication = getApplicationContext();

        Intent data = getIntent();
        phone = data.getStringExtra("phone");
        role = data.getStringExtra("role");

        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        userFragment = new UserFragment();
        newfeedFragment = new NewfeedFragment();

        LoadInfosTask loadInfosTask = new LoadInfosTask(this, phone, role, this,homeFragment,newfeedFragment,notificationFragment,userFragment);
        homeFragment.setLoadInfosTask(loadInfosTask);
        newfeedFragment.setLoadInfosTask(loadInfosTask);
        loadInfosTask.execute();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft_add = fm.beginTransaction();
        ft_add.add(R.id.framelayout_container, homeFragment).commit();
    }

    private  BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = homeFragment;
                    break;
                case R.id.nav_contact:
                    onClickBtChat();
                    return true;
                case R.id.nav_newfeed:
                    selectedFragment = newfeedFragment;
                    break;
                case R.id.nav_notification:
                    selectedFragment = notificationFragment;
                    break;
                case R.id.nav_user:
                    selectedFragment = userFragment;
                    break;
            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft_replay = fm.beginTransaction();
            ft_replay.replace(R.id.framelayout_container, selectedFragment).commit();
            return true;
        }
    };

    private void onClickBtChat() {
        Intent intent = new Intent(this, ContactActivity.class);
        if (role.equals("teacher"))
            intent = new Intent(this, TeacherMessengerActivity.class);

        intent.putExtra("class", _class);
        intent.putExtra("role", role);
        intent.putExtra("info", person);

        startActivity(intent);
    }

    @Override
    public void onLoadInfoTaskFinish(Person output, String role) {
        this.role = role;
        if (output == null){
            Toast.makeText(this, "InfoModel is null", Toast.LENGTH_LONG).show();
        }else{
            this.phone = output.getPhone();
            if (role.equals("teacher")) {
                Teacher teacher = (Teacher) output;
                _class = teacher.get_class();
            }else {
                Parent parent = (Parent) output;
                _class = parent.get_class();
                this.person = output;
            }
        }
    }
}