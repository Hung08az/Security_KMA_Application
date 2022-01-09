package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import static com.example.kma_application.AsyncTask.LoadInfosTask.doPostRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kma_application.Models.Image;
import com.example.kma_application.Models.ResponseModel;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class LoadImageTask extends AsyncTask<Void,Void,String> {
    Context context;
    ImageView imageView;
    String id;
    String activityName;
    Button btDeleteImage;
    String role;

    public LoadImageTask(Context context, ImageView imageView, String id, String fromActivity) {
        this.context = context;
        this.imageView = imageView;
        this.id = id;
        this.activityName = fromActivity;
    }

    public LoadImageTask(Context context, ImageView imageView, String id, String activityName, Button btDeleteImage, String role) {
        this.context = context;
        this.imageView = imageView;
        this.id = id;
        this.activityName = activityName;
        this.btDeleteImage = btDeleteImage;
        this.role = role;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse =
                doPostRequest(
                "https://nodejscloudkenji.herokuapp.com/getImage",
                    imageJSON(id),
                        context
                );
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
        Image image = null;
        ResponseModel responseModel= null;
        try {
            responseModel = gson.fromJson(postResponse,ResponseModel.class);
            image = gson.fromJson(postResponse,Image.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (responseModel != null && responseModel.getResponse() == null){
            if (image != null){
                //Toast.makeText(this.context, "Class: "+image.getData(), Toast.LENGTH_LONG).show();
                imageView.setImageBitmap(
                    getBitmapFromString(image.getData())
                );
                if (activityName.equals("ViewImage") && role.equals("teacher"))
                    btDeleteImage.setVisibility(View.VISIBLE);
            }else
                Toast.makeText(this.context, "Image: "+ null, Toast.LENGTH_LONG).show();
        }else if (responseModel != null && responseModel.getResponse() != null)
            Toast.makeText(this.context, ""+ responseModel.getResponse(), Toast.LENGTH_LONG).show();
    }

    // post request code here
    String imageJSON( String id)  {
        return "{\"id\":\"" + id + "\","
                +"\"fromActivity\":\"" + activityName +"\"}";
    }

    private Bitmap getBitmapFromString(String image) {

        byte[] bytes = Base64.decode(image, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}