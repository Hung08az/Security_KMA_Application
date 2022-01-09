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
import android.widget.GridView;
import android.widget.Toast;

import com.example.kma_application.Activity.ViewImageActivity;
import com.example.kma_application.Adapter.GridViewAdapter;
import com.example.kma_application.Models.Image;
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

public class LoadClassImageTask extends AsyncTask<Void,Void,String> {
    Context context;
    GridView gridView;
    String _class;
    String activityName;
    String role;

    public LoadClassImageTask(Context context, GridView gridView, String _class, String activityName, String role) {
        this.context = context;
        this.gridView = gridView;
        this._class = _class;
        this.activityName = activityName;
        this.role = role;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/getClassImage",
                    classJson(_class),
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

        ArrayList<Image> images = new ArrayList<>();
        try {
            JSONArray jsonarray = new JSONArray(postResponse);
            if (jsonarray.length() < 4 || activityName.equals("gallery")){
                for (int i = jsonarray.length() - 1; i >= 0; i--) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    Image image = gson.fromJson(jsonobject.toString(),Image.class);
                    images.add(image);
                }
            }else {
                for (int i = jsonarray.length() - 1; i >= jsonarray.length() - 4; i--) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    Image image = gson.fromJson(jsonobject.toString(),Image.class);
                    images.add(image);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ( !images.isEmpty()){
            GridViewAdapter adapter = new GridViewAdapter(
                    context,
                    images
            );
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(context, ViewImageActivity.class);
                    intent.putExtra("info", images.get(position));
                    intent.putExtra("role", role);
                    context.startActivity(intent);
                }
            });
        }
    }

    String classJson(String _class) {
        return "{\"_class\":\"" + _class + "\"}";
    }

}
