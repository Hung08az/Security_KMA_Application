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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

public class FindUserTask extends AsyncTask<Void,Void,String> {
    Context context;
    String phone;
    EditText editTextName, editTextTextPersonName6,
            editTextPhoneNumber,editTextEmailUser;

    public FindUserTask(Context context, String phone, EditText editTextName, EditText editTextTextPersonName6, EditText editTextPhoneNumber, EditText editTextEmailUser) {
        this.context = context;
        this.phone = phone;
        this.editTextName = editTextName;
        this.editTextTextPersonName6 = editTextTextPersonName6;
        this.editTextPhoneNumber = editTextPhoneNumber;
        this.editTextEmailUser = editTextEmailUser;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/search",
                    classJson(),
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
        String name, phone, email, password;
        JSONObject jsonobject = null;
        try {
             jsonobject = new JSONObject(postResponse);
            System.out.println(jsonobject);

             if (jsonobject.has("response"))
                Toast.makeText(this.context, jsonobject.getString("response"), Toast.LENGTH_LONG).show();

             name = jsonobject.getString("name");
             phone = jsonobject.getString("phone");
             email = jsonobject.getString("email");
             password = jsonobject.getString("password");

             editTextName.setText(name);
             editTextPhoneNumber.setText(phone);
             editTextEmailUser.setText(email);
             editTextTextPersonName6.setText(password);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ( jsonobject == null){
            Toast.makeText(this.context, "Không tìm thấy", Toast.LENGTH_LONG).show();
        }


    }

    // post request code here
    String classJson() {
        return "{\"phone\":\"" + phone + "\"}";
    }

}
