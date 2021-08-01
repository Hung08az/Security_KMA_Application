package com.example.kma_application.AsyncTask;

import android.app.Activity;
import android.content.Context;
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
                postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/deleteImage",
                    requestJson()
                );
            else
                postResponse = doPostRequest(
                        "https://nodejscloudkenji.herokuapp.com/deleteNotify",
                        requestJson()
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

    // post request code here
    String requestJson() {
        return "{\"id\":\"" + imageId + "\"}";
    }
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String doPostRequest(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}

