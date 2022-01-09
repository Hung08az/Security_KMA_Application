package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import static com.example.kma_application.AsyncTask.LoadInfosTask.doPostRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.kma_application.Adapter.ListNewfeedAdapter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoadPostsTask extends AsyncTask<Void,Void,String> {
    Context context;
    String _class;
    ListNewfeedAdapter listNewfeedAdapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public LoadPostsTask(Context context, String _class, ListNewfeedAdapter listNewfeedAdapter, RecyclerView recyclerView, SwipeRefreshLayout mSwipeRefreshLayout) {
        this.context = context;
        this._class = _class;
        this.listNewfeedAdapter = listNewfeedAdapter;
        this.recyclerView = recyclerView;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/getPosts",
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
                listNewfeedAdapter.clear();
                JSONArray jsonarray = new JSONArray(postResponse);
                for (int i = jsonarray.length() -1 ; i >= 0 ; i--) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    listNewfeedAdapter.addItem(jsonobject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this.context, "Lỗi tải Bảng tin", Toast.LENGTH_LONG).show();
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    // post request code here
    String reqJSON() {
        return "{\"_class\":\"" + _class + "\"}";
    }
}

