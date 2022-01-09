package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DeleteImageTask extends AsyncTask<Void,Void,String> {
    Context context;
    String imageId;
    String fromActivity;

    public DeleteImageTask(Context context, String imageId) {
        this.context = context;
        this.imageId = imageId;
    }

    public DeleteImageTask(Context context, String imageId, String fromActivity) {
        this.context = context;
        this.imageId = imageId;
        this.fromActivity = fromActivity;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse;
            if(fromActivity == null)
                postResponse = LoadInfosTask.doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/deleteImage",
                    requestJson(),
                    context
                );
            else
                postResponse = LoadInfosTask.doPostRequest(
                        "https://nodejscloudkenji.herokuapp.com/deleteNotify",
                        requestJson(),
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
        String res;
        JSONObject jsonobject = null;
        try {
            jsonobject = new JSONObject(postResponse);
            System.out.println(jsonobject);

            res = jsonobject.getString("response");

            Toast.makeText(this.context, res, Toast.LENGTH_LONG).show();

            boolean success = jsonobject.getBoolean("res");

            if (success){
                Activity activity = (Activity) context;
                activity.finish();
            }

        } catch (JSONException e) {
            Toast.makeText(this.context, "Chức năng hiện không hoạt động", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        if ( jsonobject == null){
            Toast.makeText(this.context, "Lỗi kết nối!", Toast.LENGTH_LONG).show();

        }

    }

    String requestJson() {
        return "{\"id\":\"" + imageId + "\"}";
    }

}

