package com.example.kma_application.AsyncTask;

import static android.content.Context.MODE_PRIVATE;

import static com.example.kma_application.AsyncTask.LoadInfosTask.doPostRequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kma_application.Models.Parent;
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

public class SubmitPrescriptionTask extends AsyncTask<Void,Void,String> {
    Context context;
    Button buttonSendPrescription, buttonUndoAddMedicines;
    Parent parent;
    ArrayList<Prescription.Medicine> medicines;

    public SubmitPrescriptionTask(Context context, Button buttonSendPrescription, Button buttonUndoAddMedicines, Parent parent, ArrayList<Prescription.Medicine> medicines) {
        this.context = context;
        this.buttonSendPrescription = buttonSendPrescription;
        this.buttonUndoAddMedicines = buttonUndoAddMedicines;
        this.parent = parent;
        this.medicines = medicines;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String postResponse = doPostRequest(
                    "https://nodejscloudkenji.herokuapp.com/submitPrescription",
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
        ResponseModel responseModel= null;
        try {
            responseModel = gson.fromJson(postResponse,ResponseModel.class);
        }catch (Exception e){
            Toast.makeText(this.context, "Lỗi kết nối", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            this.cancel(true);
        }

        if (responseModel != null){
            if (responseModel.getRes()){
                Toast.makeText(this.context, responseModel.getResponse(), Toast.LENGTH_LONG).show();
                buttonSendPrescription.setVisibility(View.INVISIBLE);
                buttonUndoAddMedicines.setVisibility(View.INVISIBLE);
            }
            else
                Toast.makeText(this.context, responseModel.getResponse(), Toast.LENGTH_LONG).show();
        }
    }

    // post request code here

    String reqJson() {
        //get next day of current day for submit tomorrow medicines
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        //to String
        String startDate =
                calendar.get(Calendar.DAY_OF_MONTH)+"/"+
                        calendar.get(Calendar.MONTH)+"/"+
                        calendar.get(Calendar.YEAR);
        String result =
                "{\"phone\":\"" + parent.getPhone() + "\","
                +"\"name\":\"" + parent.getChildName() + "\","
                +"\"_class\":\"" + parent.get_class() + "\","
                +"\"startDate\":\"" + startDate + "\","
                +"\"medicines\":" + jsonArrayString() +"}";
        System.out.println(result);
        return result;
    }

    String jsonArrayString(){
        String result = "[";

        for (int i = 0;i <= medicines.size() -1; i++) {
            Prescription.Medicine medicine = medicines.get(i);
            result = result.concat(medicine.toJsonString());
            if (medicine != null)
                System.out.println(medicine.toJsonString());
            else
                System.out.println("medicine "+i+" null");

        }

        if (medicines.isEmpty())
            System.out.println("medicines null");

        if (result.length() > 1)
            result = result.substring(0, result.length() - 1);

        System.out.println(result.concat("]"));


        return result.concat("]");
    }
}
