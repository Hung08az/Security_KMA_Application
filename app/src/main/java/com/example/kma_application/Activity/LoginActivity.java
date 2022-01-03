package com.example.kma_application.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kma_application.AsyncTask.LoginTask;
import com.example.kma_application.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import java.net.URISyntaxException;

public class LoginActivity extends AppCompatActivity {

    EditText txtPhone,txtPassword;
    Button btLogin;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Init view
        txtPhone = (EditText)findViewById(R.id.editTextPhone);
        txtPassword = (EditText)findViewById(R.id.editTextTextPassword);
        btLogin = (Button)findViewById(R.id.buttonLogin);

        pref = getApplicationContext().getSharedPreferences("KMA_App_Pref", MODE_PRIVATE);
        editor = pref.edit();
        
        //Event handle
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtLogin();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String storedPhone = pref.getString("phone", "");
        if (!TextUtils.isEmpty(storedPhone))
            txtPhone.setText(storedPhone);
    }

    private void onClickBtLogin() {
        loginUser(txtPhone.getText().toString().trim(),
                txtPassword.getText().toString().trim());
    }


    private void loginUser(String phone, String password) {
        if (TextUtils.isEmpty(phone)){

            Toast.makeText(this,"Số điện thoại không được để trống",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)){

            Toast.makeText(this,"Mật khẩu không được để trống",Toast.LENGTH_LONG).show();
            return;
        }
        new LoginTask(
                this,
                phone,
                password,
                pref,
                editor
        ).execute();
    }
}