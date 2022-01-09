package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import static com.example.kma_application.AsyncTask.LoadInfosTask.doPostRequest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kma_application.Activity.TeacherChildAbsentActivity;
import com.example.kma_application.Adapter.MessageAdapter;
import com.example.kma_application.Models.Absent;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class LoadMessagesTask extends AsyncTask<Void,Void,String> {
    Context context;
    String coupleUserPhone;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;

    public LoadMessagesTask(Context context, String coupleUserPhone, MessageAdapter messageAdapter, RecyclerView recyclerView) {
        this.context = context;
        this.coupleUserPhone = coupleUserPhone;
        this.messageAdapter = messageAdapter;
        this.recyclerView = recyclerView;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                "https://nodejscloudkenji.herokuapp.com/getMessages",
                    reqJSON(),
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
        boolean hasData = true;
        try {
            JSONObject jsonObject = new JSONObject(postResponse);
            Toast.makeText(this.context, jsonObject.getString("response"), Toast.LENGTH_LONG).show();
            hasData = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (hasData){
            try {
                JSONArray jsonarray = new JSONArray(postResponse);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    messageAdapter.addItem(jsonobject);
                }
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this.context, "Lỗi tải tin nhắn", Toast.LENGTH_LONG).show();
            }
        }
    }

    // post request code here
    String reqJSON() {
        return "{\"coupleUserPhone\":\"" + coupleUserPhone + "\"}";
    }

}
