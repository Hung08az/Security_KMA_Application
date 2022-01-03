package com.example.kma_application.AsyncTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

import com.example.kma_application.Activity.AdminHomeActivity;
import com.example.kma_application.Activity.MainActivity;
import com.example.kma_application.Models.ResponseModel;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class LoginTask extends AsyncTask<Void,Void,String> {
    private Context context;
    private String phone;
    private String password;
    private String role;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public LoginTask(Context context, String phone, String password, SharedPreferences pref, SharedPreferences.Editor editor) {
        this.context = context;
        this.phone = phone;
        this.password = password;
        this.pref = pref;
        this.editor = editor;
    }

    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest("https://nodejscloudkenji.herokuapp.com/login", userJson());
            //String postResponse = doPostRequest("http://192.168.1.68:3000/login", jsons[0]);
            return postResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            if (responseModel.getRes()){
                //Toast.makeText(this.context, responseModel.getResponse(), Toast.LENGTH_LONG).show();
                this.role = responseModel.getRole();
                editor.putString("phone", phone);
                editor.putString("password", password);
                editor.putString("role", role);
                editor.putString("token", responseModel.getToken());
                editor.apply();

                startMainActivities();
            }else
                Toast.makeText(this.context, responseModel.getResponse(), Toast.LENGTH_LONG).show();
        }
    }
    private void startMainActivities() {
        Intent mainActivity = new Intent(this.context, MainActivity.class);

        if (this.role.equals("admin") )
            mainActivity = new Intent(this.context, AdminHomeActivity.class);

        mainActivity.putExtra("phone", this.phone);
        mainActivity.putExtra("role", this.role);
        context.startActivity(mainActivity);
        ((Activity)context).finish();
        //this.cancel(true);
    }
    String userJson() {
        return "{\"phone\":\"" + phone + "\","
                +"\"password\":\"" + password +"\"}";
    }
    // post request code here

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
