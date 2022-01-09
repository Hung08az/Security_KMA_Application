package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import static com.example.kma_application.AsyncTask.LoadInfosTask.doPostRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.kma_application.Models.Prescription;
import com.example.kma_application.Models.ResponseModel;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class LoadTomorrowMedicinesTask extends AsyncTask<Void,Void,String> {
    Context context;
    String phone;
    CallbackForMedicine parentPrescriptionActivity;

    public interface CallbackForMedicine{
        void callback(boolean hasData, ArrayList<Prescription.Medicine> medicines);
    }

    public LoadTomorrowMedicinesTask(Context context, String phone, CallbackForMedicine parentPrescriptionActivity) {
        this.context = context;
        this.phone = phone;
        this.parentPrescriptionActivity = parentPrescriptionActivity;
    }

    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                "https://nodejscloudkenji.herokuapp.com/getPrescription",
                    reqJson(),
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
        Prescription prescription = null;
        ResponseModel responseModel= null;
        try {
            responseModel = gson.fromJson(postResponse,ResponseModel.class);
            prescription = gson.fromJson(postResponse, Prescription.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (responseModel.getResponse() == null){
            if (true){
                if (prescription != null){
                    //Toast.makeText(this.context, "Class: "+prescription.get_class(), Toast.LENGTH_LONG).show();
                    try{
                        if (prescription.getMedicines() != null && !prescription.getMedicines().isEmpty())
                            //pass data back to ParentPrescriptionActivity
                            parentPrescriptionActivity.callback(true, prescription.getMedicines());
                        else
                            Toast.makeText(this.context, "Không nạp được danh sách thuốc", Toast.LENGTH_LONG).show();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                        Toast.makeText(this.context, "medicines null", Toast.LENGTH_LONG).show();
                    }
                }else
                    Toast.makeText(this.context, "Không nạp được đơn thuốc", Toast.LENGTH_LONG).show();
            }

        }else
            Toast.makeText(this.context, ""+ responseModel.getResponse(), Toast.LENGTH_LONG).show();
    }

    String reqJson() {
        //get next day of current day for fetching tomorrow medicines
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        //to String
        String startDate =
                calendar.get(Calendar.DAY_OF_MONTH)+"/"+
                calendar.get(Calendar.MONTH)+"/"+
                calendar.get(Calendar.YEAR);

        //return String JSON for request body
        return "{\"phone\":\"" + phone + "\","
                +"\"startDate\":\"" + startDate +"\"}";
    }
}