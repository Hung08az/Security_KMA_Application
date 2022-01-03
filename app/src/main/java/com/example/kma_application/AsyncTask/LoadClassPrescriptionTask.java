package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kma_application.Activity.TeacherChildMedicineActivity;
import com.example.kma_application.Models.Prescription;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class LoadClassPrescriptionTask extends AsyncTask<Void,Void,String> {
    Context context;
    ListView lvClass;
    String _class;

    public LoadClassPrescriptionTask(Context context, ListView lvClass, String _class) {
        this.context = context;
        this.lvClass = lvClass;
        this._class = _class;
    }

    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/getClassPrescription",
                    classJson()
            );
            //String postResponse = doPostRequest("http://192.168.1.68:3000/login", jsons[0]);
            return postResponse;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.context, "Lỗi kết nối", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String postResponse) {
        Gson gson = new Gson();

        boolean hasData = true;
        try {
            JSONObject jsonObject = new JSONObject(postResponse);
            Toast.makeText(this.context, jsonObject.getString("response"), Toast.LENGTH_LONG).show();
            hasData = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (hasData){
            ArrayList<Prescription.Medicine> childrenMedicine = new ArrayList<>();
            try {
                JSONArray jsonarray = new JSONArray(postResponse);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    ArrayList<Prescription.Medicine> medicines = gson.fromJson(jsonobject.toString(), Prescription.class).getMedicines();

                    for (int j = 0; j < medicines.size(); j++) {
                        childrenMedicine.add(medicines.get(j));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if ( !childrenMedicine.isEmpty()){
                Comparator<Prescription.Medicine> compareByTime = (Prescription.Medicine medicine1, Prescription.Medicine medicine2) ->
                        medicine1.getTime().compareTo( medicine2.getTime() );

                Collections.sort(childrenMedicine, compareByTime);

                ArrayList<String> times = new ArrayList<>();

                times.add(childrenMedicine.get(0).getTime());

                int t = 0;

                for (int j = 0; j < childrenMedicine.size(); j++) {
                    Prescription.Medicine medicine = childrenMedicine.get(j);

                    if (!medicine.getTime().equals(times.get(t))){
                        times.add(medicine.getTime());
                        t++;
                    }

                }

                ArrayAdapter adapter = new ArrayAdapter(
                        context,
                        android.R.layout.simple_list_item_1,
                        times
                );
                lvClass.setAdapter(adapter);
                lvClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ArrayList<Prescription.Medicine> mediciness = new ArrayList<>();
                        for (int j = 0; j < childrenMedicine.size(); j++) {

                            Prescription.Medicine medicine = childrenMedicine.get(j);

                            if (medicine.getTime().equals(times.get(position)))
                                mediciness.add(medicine);

                        }
                        Comparator<Prescription.Medicine> compareByName =
                                (Prescription.Medicine medicine1, Prescription.Medicine medicine2) ->
                                medicine1.getChildName().compareTo( medicine2.getChildName() );

                        Collections.sort(mediciness, compareByName);

                        Intent intent = new Intent(context, TeacherChildMedicineActivity.class);
                        intent.putExtra("info", mediciness);
                        context.startActivity(intent);
                    }
                });
            }else
                Toast.makeText(this.context, "Lỗi nạp danh sách thuốc", Toast.LENGTH_LONG).show();
        }

    }



    // post request code here
    String classJson() {
        //get current day for fetch today medicines
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        //to String
        String startDate =
                calendar.get(Calendar.DAY_OF_MONTH)+"/"+
                        calendar.get(Calendar.MONTH)+"/"+
                        calendar.get(Calendar.YEAR);
        String result = "{\"_class\":\"" + _class + "\","
                        +"\"startDate\":\"" + startDate +"\"}";
        System.out.println(result);
        return result;
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
