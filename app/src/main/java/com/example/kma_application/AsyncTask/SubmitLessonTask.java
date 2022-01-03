package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class SubmitLessonTask extends AsyncTask<Void,Void,String> {
    Context context;
    ImageView imgLesson;
    Bitmap bitmap;
    String _class, title, content;

    public SubmitLessonTask(Context context, ImageView imgLesson, Bitmap bitmap, String _class, String title, String content) {
        this.context = context;
        this.imgLesson = imgLesson;
        this.bitmap = bitmap;
        this._class = _class;
        this.title = title;
        this.content = content;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/submitLesson",
                    reqJson()
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
        try {
            JSONObject jsonobject = new JSONObject(postResponse);
            if (jsonobject.has("res")){
                Toast.makeText(this.context, jsonobject.getString("response"), Toast.LENGTH_LONG).show();

                if(jsonobject.getBoolean("res")){
                    imgLesson.setImageBitmap(bitmap);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this.context, "Lỗi gửi bài học!", Toast.LENGTH_LONG).show();
        }
    }

    // post request code here

    String reqJson() {
        //get current day for submit today lesson
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        //to String
        String date =
                calendar.get(Calendar.DAY_OF_MONTH)+"/"+
                        calendar.get(Calendar.MONTH)+"/"+
                        calendar.get(Calendar.YEAR);
        String result;

            result = "{\"_class\":\"" + _class + "\","
                    +"\"date\":\"" + date + "\","
                    +"\"title\":\"" + title + "\","
                    +"\"content\":\"" + content + "\","
                    +"\"image\":\"" +  encodeBase64(bitmap)  +"\"}";
        //System.out.println(result);
        return result;
    }

    private String encodeBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
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
