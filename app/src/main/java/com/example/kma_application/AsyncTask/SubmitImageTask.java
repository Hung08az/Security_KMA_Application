package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import static com.example.kma_application.AsyncTask.LoadInfosTask.doPostRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.kma_application.Models.Person;
import com.example.kma_application.Models.ResponseModel;
import com.example.kma_application.Models.Teacher;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class SubmitImageTask extends AsyncTask<Void, Void, String> {
    private Context context;

    Teacher teacher;
    String originalBase64;
    String resizeBase64;
    String activityName;
    String _class;
    AsyncResponse activity;

    public interface AsyncResponse {
        void onSubmitImageTaskFinish();
    }

    public SubmitImageTask(Context context, Teacher teacher, String originalBase64, String resizeBase64, String fromActivity, AsyncResponse activity) {
        this.context = context;
        this.teacher = teacher;
        this.originalBase64 = originalBase64;
        this.resizeBase64 = resizeBase64;
        this.activityName = fromActivity;
        this.activity = activity;
    }

    public SubmitImageTask(Context context, String originalBase64, String resizeBase64, String activityName, String _class, AsyncResponse activity) {
        this.context = context;
        this.originalBase64 = originalBase64;
        this.resizeBase64 = resizeBase64;
        this.activityName = activityName;
        this._class = _class;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            if (activityName.equals("main")){
                return doPostRequest(
                        "https://nodejscloudkenji.herokuapp.com/submitImage",
                        imagesJson(teacher.get_class(),
                                originalBase64,
                                resizeBase64,
                                teacher.getPhone()
                        ),
                        context
                );
            }else
                return doPostRequest(
                        "https://nodejscloudkenji.herokuapp.com/submitImage",
                        imagesJson(_class,
                                originalBase64,
                                resizeBase64,
                                "teacher"
                        ),
                        context
                );


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
            Toast.makeText(this.context, responseModel.getResponse(), Toast.LENGTH_LONG).show();
            if (responseModel.getRes())
                activity.onSubmitImageTaskFinish();
        }
    }

    // post request code here
    String imagesJson(String _class, String originalBase64, String resizeBase64, String phone) {
        return "{\"_class\":\"" + _class + "\","
                + "\"id\":\"" + phone + "\","
                + "\"fromActivity\":\"" + activityName + "\","
                + "\"originalBase64\":\"" + originalBase64 + "\","
                + "\"resizeBase64\":\"" + resizeBase64 + "\"}";
    }
}
