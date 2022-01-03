package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kma_application.Activity.MainActivity;
import com.example.kma_application.Models.Child;
import com.example.kma_application.Models.ResponseModel;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class LoadHealthTask extends AsyncTask<Void,Void,String> {
    Context context;
    String phone;
    EditText txtName, txtBirth, txtClass, txtHeight, txtWeight, txtBodyRatio;

    public LoadHealthTask(Context context, String phone, EditText txtName, EditText txtBirth, EditText txtClass, EditText txtHeight, EditText txtWeight, EditText txtBodyRatio) {
        this.context = context;
        this.phone = phone;
        this.txtName = txtName;
        this.txtBirth = txtBirth;
        this.txtClass = txtClass;
        this.txtHeight = txtHeight;
        this.txtWeight = txtWeight;
        this.txtBodyRatio = txtBodyRatio;
    }

    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest("https://nodejscloudkenji.herokuapp.com/getHealth", userJson(phone));
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
        Child child = gson.fromJson(postResponse,Child.class);

        if (child != null){
            //Toast.makeText(this.context, "Class: "+child.get_class(), Toast.LENGTH_LONG).show();
            //String birth = invertedDate(child.getBirth());
            txtName.setText(child.getName());
            txtBirth.setText(child.getBirth());
            txtClass.setText(child.get_class());
            txtHeight.setText(child.getHeight());
            txtWeight.setText(child.getWeight());
            txtBodyRatio.setText(child.getBody_ratio());
        }else
            Toast.makeText(this.context, "Child: "+child, Toast.LENGTH_LONG).show();
    }

    private String invertedDate(String birth) {
        String[] strings = birth.split("-",3);
        String result = strings[2]+"-"+strings[1]+"-"+strings[0];
        return result;
    }

    // post request code here
    String userJson(String phone) {
        return "{\"phone\":\"" + phone + "\"}";
    }
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String doPostRequest(String url, String json) throws IOException {
        SharedPreferences pref = context.getSharedPreferences("KMA_App_Pref", MODE_PRIVATE);
        String token = pref.getString("token", null);

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-access-token", token)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}