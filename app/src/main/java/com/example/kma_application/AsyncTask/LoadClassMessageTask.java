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

public class LoadClassMessageTask extends AsyncTask<Void,Void,String> {
    Context context;
    ListView lvClass;
    String _class;
    String role;


    public LoadClassMessageTask(Context context, ListView lvClass, String _class, String role) {
        this.context = context;
        this.lvClass = lvClass;
        this._class = _class;
        this.role = role;
    }

    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/getClassMessage",
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

        ArrayList<Parent> parents = new ArrayList<>();
        ArrayList<String> childrenName = new ArrayList<>();
        try {
            JSONArray jsonarray = new JSONArray(postResponse);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Parent parent = gson.fromJson(jsonobject.toString(),Parent.class);
                parents.add(parent);
                childrenName.add(parent.getChildName());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ( !parents.isEmpty()){
            ArrayAdapter adapter = new ArrayAdapter(
                    context,
                    android.R.layout.simple_list_item_1,
                    childrenName
            );
            lvClass.setAdapter(adapter);
            lvClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(context, ContactActivity.class);
                    intent.putExtra("info", parents.get(position));
                    intent.putExtra("role", role);
                    context.startActivity(intent);
                }
            });
        }else
            Toast.makeText(this.context, "parents.isEmpty", Toast.LENGTH_LONG).show();

    }

    String classJson(String _class) {
        return "{\"_class\":\"" + _class + "\"}";
    }

}
