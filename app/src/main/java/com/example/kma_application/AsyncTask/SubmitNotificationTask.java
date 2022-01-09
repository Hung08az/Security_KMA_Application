package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import static com.example.kma_application.AsyncTask.LoadInfosTask.doPostRequest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.kma_application.Activity.AdminNotificationActivity;
import com.example.kma_application.Models.ResponseModel;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class SubmitNotificationTask extends AsyncTask<Void,Void,String> {
    private Context context;
    String title, content, image;

    public SubmitNotificationTask(Context context, String title, String content, String image) {
        this.context = context;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/submitNotify",
                    userJson(),
                    context
            );
            //String postResponse = doPostRequest("http://192.168.1.68:3000/login", jsons[0]);
            return postResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String userJson() {
            return "{\"title\":\"" + title + "\","
                    +"\"content\":\"" + content +"\","
                    +"\"image\":\"" + image +"\"}";
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
            if (responseModel.getRes())
                turnBack();
        }
    }
    private void turnBack(){
        Intent intent = new Intent(context, AdminNotificationActivity.class);
        context.startActivity(intent);
    }
}
