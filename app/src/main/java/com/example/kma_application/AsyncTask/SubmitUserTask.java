package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import static com.example.kma_application.AsyncTask.LoadInfosTask.doPostRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.kma_application.Models.ResponseModel;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class SubmitUserTask extends AsyncTask<Void,Void,String> {
    private Context context;
    String role, phone, password, name, email, _class, childName;

    public SubmitUserTask(Context context, String role, String phone, String password, String name, String email, String _class, String childName) {
        this.context = context;
        this.role = role;
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.email = email;
        this._class = _class;
        this.childName = childName;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/submitUser",
                    userJson(role),
                    context
            );
            //String postResponse = doPostRequest("http://192.168.1.68:3000/login", jsons[0]);
            return postResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String userJson(String role) {
        if (role.equals("teacher"))
            return "{\"name\":\"" + name + "\","
                    +"\"phone\":\"" + phone +"\","
                    +"\"password\":\"" + password +"\","
                    +"\"email\":\"" + email +"\","
                    +"\"_class\":\"" + _class +"\","
                    +"\"role\":\"" + role +"\"}";
        else
            return "{\"name\":\"" + name + "\","
                    +"\"phone\":\"" + phone +"\","
                    +"\"password\":\"" + password +"\","
                    +"\"email\":\"" + email +"\","
                    +"\"_class\":\"" + _class +"\","
                    +"\"childName\":\"" + childName +"\","
                    +"\"role\":\"" + role +"\"}";
    }

    @Override
    protected void onPostExecute(String postResponse) {
        Gson gson = new Gson();
        ResponseModel responseModel= null;
        try {
            responseModel = gson.fromJson(postResponse,ResponseModel.class);
        }catch (Exception e){
            Toast.makeText(this.context, "Lỗi kết nối", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            this.cancel(true);
        }

        if (responseModel != null){
            Toast.makeText(this.context, responseModel.getResponse(), Toast.LENGTH_LONG).show();
        }
    }
}

