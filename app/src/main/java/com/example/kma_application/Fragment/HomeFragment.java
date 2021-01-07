package com.example.kma_application.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kma_application.Activity.GalleryActivity;
import com.example.kma_application.Activity.ParentAbsentActivity;
import com.example.kma_application.Activity.TeacherAbsentActivity;
import com.example.kma_application.Activity.TeacherHealthActivity;
import com.example.kma_application.Activity.ParentHealthActivity;
import com.example.kma_application.Activity.ParentMedicineActivity;
import com.example.kma_application.Activity.TeacherMedicineActivity;
import com.example.kma_application.AsyncTask.LoadInfosTask;
import com.example.kma_application.Models.InfoModel;
import com.example.kma_application.R;


public class HomeFragment extends Fragment implements LoadInfosTask.AsyncResponse{
    String role, phone;
    InfoModel infoModel;
    Context context = getActivity();
    Button btHealth, btAbsent, btMedicine;
    TextView txtName, btViewGallery;

    public void setLoadInfosTask(LoadInfosTask loadInfosTask) {
        this.loadInfosTask = loadInfosTask;
    }

    LoadInfosTask loadInfosTask;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btHealth = (Button)view.findViewById(R.id.buttonHomeHealth);
        btAbsent = (Button)view.findViewById(R.id.buttonHomeAbsent);
        btMedicine = (Button)view.findViewById(R.id.buttonHomeMedicine);
        txtName = (TextView) view.findViewById(R.id.textHome);
        btViewGallery = (TextView) view.findViewById(R.id.buttonViewGallery);

        loadInfosTask.setTxtName(txtName);

        btHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtHealth();
            }
        });
        btMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtMedicine();
            }
        });
        btAbsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtAbsent();
            }
        });
        btViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtViewGallery();
            }
        });
        return view;
    }

    private void onClickBtViewGallery() {
        Intent intent = new Intent(getActivity(), GalleryActivity.class);

        intent.putExtra("info", infoModel);
        intent.putExtra("role", role);
        startActivity(intent);
    }

    private void onClickBtHealth() {
        Intent intent = new Intent(getActivity(), ParentHealthActivity.class);
        if (role.equals("teacher"))
            intent = new Intent(getActivity(), TeacherHealthActivity.class);

        intent.putExtra("info", infoModel);
        startActivity(intent);
    }
    private void onClickBtMedicine() {
        Intent intent = new Intent(getActivity(), ParentMedicineActivity.class);
        if (role.equals("teacher"))
            intent = new Intent(getActivity(), TeacherMedicineActivity.class);

        intent.putExtra("info", infoModel);
        startActivity(intent);
    }
    private void onClickBtAbsent() {
        Intent intent = new Intent(getActivity(), ParentAbsentActivity.class);
        if (role.equals("teacher"))
            intent = new Intent(getActivity(), TeacherAbsentActivity.class);

        intent.putExtra("info", infoModel);
        startActivity(intent);
    }

    @Override
    public void onLoadInfoTaskFinish(InfoModel output, String role) {
        this.infoModel = output;
        this.role = role;
        if (infoModel == null){
            Toast.makeText(this.context, "InfoModel is null", Toast.LENGTH_LONG).show();
        }else
            this.phone = infoModel.getPhone();

    }
}