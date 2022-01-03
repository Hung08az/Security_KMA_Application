package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kma_application.Models.Absent;
import com.example.kma_application.Models.ResponseModel;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class LoadAbsentTask extends AsyncTask<Void,Void,String> {
    Context context;
    String phone;
    private EditText txtContent;
    private EditText txtStartDate;
    private EditText txtEndDate;

    public LoadAbsentTask(Context context, String phone, EditText txtContent, EditText txtStartDate, EditText txtEndDate) {
        this.context = context;
        this.phone = phone;
        this.txtContent = txtContent;
        this.txtStartDate = txtStartDate;
        this.txtEndDate = txtEndDate;
    }

    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest("https://nodejscloudkenji.herokuapp.com/getAbsent", userJson(phone));
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
        Absent absent = null;
        ResponseModel responseModel= null;
        try {
            responseModel = gson.fromJson(postResponse,ResponseModel.class);
            absent = gson.fromJson(postResponse,Absent.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (responseModel.getResponse() == null){
            if (true){
                if (absent != null){
                    //Toast.makeText(this.context, "Class: "+absent.get_class(), Toast.LENGTH_LONG).show();
                    //String birth = invertedDate(absent.getBirth());
                    txtContent.setText(absent.getContent());
                    txtStartDate.setText(absent.getStartDate());
                    txtEndDate.setText(absent.getEndDate());

                }else
                    Toast.makeText(this.context, "Absent: "+absent, Toast.LENGTH_LONG).show();
            }

        }else
            Toast.makeText(this.context, "Response: "+ responseModel.getResponse(), Toast.LENGTH_LONG).show();
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