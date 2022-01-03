package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ModifyUserTask extends AsyncTask<Void,Void,String> {
    Context context;
    EditText  txtName, txtPassword, txtPhone, txtEmail;
    Button btAddUser, btModifyUser, btDeleteUser, btAdminLogout, btDoneModify;
    ImageButton btSearch;
    final String oldPhone;
    public ModifyUserTask(Context context, EditText txtName, EditText txtPassword, EditText txtPhone, EditText txtEmail, Button btAddUser, Button btModifyUser, Button btDeleteUser, Button btAdminLogout, Button btDoneModify, ImageButton btSearch, String oldPhone) {
        this.context = context;
        this.txtName = txtName;
        this.txtPassword = txtPassword;
        this.txtPhone = txtPhone;
        this.txtEmail = txtEmail;
        this.btAddUser = btAddUser;
        this.btModifyUser = btModifyUser;
        this.btDeleteUser = btDeleteUser;
        this.btAdminLogout = btAdminLogout;
        this.btDoneModify = btDoneModify;
        this.btSearch = btSearch;
        this.oldPhone = oldPhone;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/modifyUser",
                    requestJson()
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

            if (success)
                exitModifyMode();

        } catch (JSONException e) {
            Toast.makeText(this.context, "Chức năng hiện không hoạt động", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        if ( jsonobject == null){
            Toast.makeText(this.context, "Lỗi kết nối!", Toast.LENGTH_LONG).show();

        }

    }


    // post request code here
    String requestJson() {
        String  name = txtName.getText().toString().trim();
        String phone = txtPhone.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        return "{\"name\":\"" + name + "\","
                +"\"phone\":\"" + phone +"\","
                +"\"oldPhone\":\"" + oldPhone +"\","
                +"\"password\":\"" + password +"\","
                +"\"email\":\"" + email +"\"}";
    }
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public void exitModifyMode(){
        txtEmail.setText("");
        txtName.setText("");
        txtPassword.setText("");
        txtPhone.setText("");
        btAddUser.setVisibility(View.VISIBLE);
        btModifyUser.setVisibility(View.VISIBLE);
        btDeleteUser.setVisibility(View.VISIBLE);
        btAdminLogout.setVisibility(View.VISIBLE);
        btDoneModify.setVisibility(View.INVISIBLE);
        btSearch.setVisibility(View.VISIBLE);
        txtPassword.setEnabled(false);
        txtName.setEnabled(false);
        txtEmail.setEnabled(false);
        txtPhone.setEnabled(false);
    }

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

