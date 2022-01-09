package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import static com.example.kma_application.AsyncTask.LoadInfosTask.doPostRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.kma_application.Adapter.ListCommentAdapter;
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

public class LoadCommentsTask extends AsyncTask<Void,Void,String> {
    Context context;
    String postId;
    ListCommentAdapter listCommentAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public LoadCommentsTask(Context context, String postId, ListCommentAdapter listCommentAdapter, SwipeRefreshLayout mSwipeRefreshLayout) {
        this.context = context;
        this.postId = postId;
        this.listCommentAdapter = listCommentAdapter;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    public LoadCommentsTask(Context context, String postId, ListCommentAdapter listCommentAdapter) {
        this.context = context;
        this.postId = postId;
        this.listCommentAdapter = listCommentAdapter;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/getComments", reqJSON(),
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
                listCommentAdapter.clear();
                JSONArray jsonarray = new JSONArray(postResponse);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    listCommentAdapter.addItem(jsonobject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this.context, "Lỗi tải bình luận", Toast.LENGTH_LONG).show();
            }
        }
        //mSwipeRefreshLayout.setRefreshing(false);
    }

    // post request code here
    String reqJSON() {
        return "{\"postId\":\"" + postId + "\"}";
    }
}
