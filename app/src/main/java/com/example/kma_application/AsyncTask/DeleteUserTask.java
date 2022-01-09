package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import static com.example.kma_application.AsyncTask.LoadInfosTask.doPostRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DeleteUserTask extends AsyncTask<Void,Void,String> {
    Context context;
    String phone;
    EditText editTextName, editTextTextPersonName6,
            editTextPhoneNumber,editTextEmailUser;

    public DeleteUserTask(Context context, String phone, EditText editTextName, EditText editTextTextPersonName6, EditText editTextPhoneNumber, EditText editTextEmailUser) {
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
                    "https://nodejscloudkenji.herokuapp.com/deleteUser",
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
        String res;
        JSONObject jsonobject = null;
        try {
            jsonobject = new JSONObject(postResponse);
            System.out.println(jsonobject);

            res = jsonobject.getString("response");

            Toast.makeText(this.context, res, Toast.LENGTH_LONG).show();

            boolean success = jsonobject.getBoolean("res");

            if (success){
                editTextPhoneNumber.setText("");
                editTextEmailUser.setText("");
                editTextName.setText("");
                editTextTextPersonName6.setText("");
            }

        } catch (JSONException e) {
            Toast.makeText(this.context, "Chức năng hiện không hoạt động", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        if ( jsonobject == null){
            Toast.makeText(this.context, "Lỗi kết nối!", Toast.LENGTH_LONG).show();

        }

    }



    // post request code here
    String classJson() {
        return "{\"phone\":\"" + phone + "\"}";
    }
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

}

