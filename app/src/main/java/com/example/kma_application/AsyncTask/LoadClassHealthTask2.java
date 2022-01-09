package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import static com.example.kma_application.AsyncTask.LoadInfosTask.doPostRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

public class LoadClassHealthTask2 extends AsyncTask<Void,Void,String> {
    Context context;
    String _class, date;
    ImageView imgMainMeal, imgSubMeal;

    public LoadClassHealthTask2(Context context, String _class, String date, ImageView imgMainMeal, ImageView imgSubMeal) {
        this.context = context;
        this._class = _class;
        this.date = date;
        this.imgMainMeal = imgMainMeal;
        this.imgSubMeal = imgSubMeal;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/getClassMeal",
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
        try {
            JSONObject jsonobject = new JSONObject(postResponse);
            if (jsonobject.has("res")){
                Toast.makeText(this.context, jsonobject.getString("response"), Toast.LENGTH_LONG).show();
            }else {
                String mainMeal = jsonobject.getString("mainMeal");
                String subMeal = jsonobject.getString("subMeal");
                if( !mainMeal.isEmpty())
                    imgMainMeal.setImageBitmap(
                        getBitmapFromString(mainMeal));
                if( !subMeal.isEmpty())
                    imgSubMeal.setImageBitmap(
                        getBitmapFromString(subMeal));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this.context, "Lỗi tải bữa ăn!", Toast.LENGTH_LONG).show();
        }
    }



    // post request code here
    String classJson() {

        String result = "{\"_class\":\"" + _class + "\","
                          +"\"date\":\"" + date +"\"}";
        //System.out.println(result);
        return result;
    }

    private Bitmap getBitmapFromString(String image) {

        byte[] bytes = Base64.decode(image, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
