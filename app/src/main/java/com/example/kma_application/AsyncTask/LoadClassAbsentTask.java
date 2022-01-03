package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kma_application.Activity.TeacherChildAbsentActivity;
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

public class LoadClassAbsentTask extends AsyncTask<Void,Void,String> {
    Context context;
    ListView lvClass;
    String _class;
    ArrayList<Absent> absents;

    public LoadClassAbsentTask(Context context, ListView lvClass, String _class, ArrayList<Absent> absents) {
        this.context = context;
        this.lvClass = lvClass;
        this._class = _class;
        this.absents = absents;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/getClassAbsent",
                    classJson(_class)
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

        ArrayList<String> childrenName = new ArrayList<>();
        try {
            JSONArray jsonarray = new JSONArray(postResponse);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Absent absent = gson.fromJson(jsonobject.toString(),Absent.class);
                absents.add(absent);
                childrenName.add(absent.getName());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ( !absents.isEmpty()){
            ArrayAdapter adapter = new ArrayAdapter(
                    context,
                    android.R.layout.simple_list_item_1,
                    childrenName
            );
            lvClass.setAdapter(adapter);

        }else
            Toast.makeText(this.context, "absents.isEmpty", Toast.LENGTH_LONG).show();

    }



    // post request code here
    String classJson(String _class) {
        return "{\"_class\":\"" + _class + "\"}";
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
