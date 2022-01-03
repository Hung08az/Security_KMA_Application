package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kma_application.Adapter.ListCommentAdapter;
import com.google.gson.JsonObject;
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

public class SubmitCommentTask extends AsyncTask<Void,Void,String> {
    Context context;
    JSONObject commentJSON;
    ListCommentAdapter commentAdapter;
    RecyclerView recyclerView;
    EditText txtEnterComment;

    public SubmitCommentTask(Context context, JSONObject commentJSON, ListCommentAdapter commentAdapter, RecyclerView recyclerView, EditText txtEnterComment) {
        this.context = context;
        this.commentJSON = commentJSON;
        this.commentAdapter = commentAdapter;
        this.recyclerView = recyclerView;
        this.txtEnterComment = txtEnterComment;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/submitComment",
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
                    commentAdapter.addItem(commentJSON);
                    recyclerView.smoothScrollToPosition(commentAdapter.getItemCount() - 1);
                    txtEnterComment.setText("");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this.context, "Lỗi gửi bình luận!", Toast.LENGTH_LONG).show();
        }
    }

    // post request code here

    String reqJson() {
        //get current day for submit today comment
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        //to String
        String date =
                        calendar.get(Calendar.DAY_OF_MONTH)+"/"+
                        calendar.get(Calendar.MONTH)+"/"+
                        calendar.get(Calendar.YEAR)+"  "+
                        calendar.get(Calendar.HOUR_OF_DAY)+":"+
                        calendar.get(Calendar.MINUTE);
        String postId, name, type, content;
        postId = "";
        name = "";
        type = "";
        content = "";
        try {
            postId = commentJSON.getString("postId");
            name = commentJSON.getString("name");
            type = commentJSON.getString("type");
            content = commentJSON.getString("content");
            commentJSON.put("date",date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "{\"postId\":\"" + postId + "\","
                +"\"name\":\"" + name + "\","
                +"\"date\":\"" + date + "\","
                +"\"type\":\"" + type +"\","
                + "\"content\":\"" + content + "\"}";
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
