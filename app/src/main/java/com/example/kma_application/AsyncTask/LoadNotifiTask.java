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
import android.widget.ListView;
import android.widget.Toast;

import com.example.kma_application.Activity.AdminCreateNotificationActivity;
import com.example.kma_application.Activity.ContactActivity;
import com.example.kma_application.Models.Parent;
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

public class LoadNotifiTask extends AsyncTask<Void,Void,String> {
    Context context;
    ListView listView;
    String role;

    public LoadNotifiTask(Context context, ListView listView, String role) {
        this.context = context;
        this.listView = listView;
        this.role = role;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/getAllNotify",
                    classJson(""),
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

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();
        ArrayList<String> imageIds = new ArrayList<>();
        try {
            JSONArray jsonarray = new JSONArray(postResponse);
            System.out.println(jsonarray);
            for (int i = jsonarray.length()-1 ; i >= 0 ; i--) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                titles.add(jsonobject.getString("title"));
                contents.add(jsonobject.getString("content"));
                imageIds.add(jsonobject.getString("imageId"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ( !titles.isEmpty()){
            ArrayAdapter adapter = new ArrayAdapter(
                    context,
                    android.R.layout.simple_list_item_1,
                    titles
            );
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(context, AdminCreateNotificationActivity.class);
                    System.out.println("title: "+titles.get(position));
                    System.out.println("content: "+contents.get(position));
                    System.out.println("imageId: "+imageIds.get(position));
                    System.out.println("role: "+role);

                    intent.putExtra("title", titles.get(position));
                    intent.putExtra("content", contents.get(position));
                    intent.putExtra("imageId", imageIds.get(position));
                    intent.putExtra("role", role);

                    try{
                        context.startActivity(intent);
                    }catch (Exception e){
                        System.out.println("co loi startActivity tu load Notify task");

                        e.printStackTrace();
                    }
                }
            });
        }else
            Toast.makeText(this.context, "Không có thông báo", Toast.LENGTH_LONG).show();

    }

    String classJson(String _class) {
        return "{\"_class\":\"" + _class + "\"}";
    }
}
