package com.example.kma_application.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kma_application.Activity.ChangePasswordActivity;
import com.example.kma_application.Activity.LoginActivity;
import com.example.kma_application.Activity.MainActivity;
import com.example.kma_application.AsyncTask.LoadImageTask;
import com.example.kma_application.AsyncTask.LoadInfosTask;
import com.example.kma_application.AsyncTask.SubmitImageTask;
import com.example.kma_application.Models.Person;
import com.example.kma_application.Models.Parent;
import com.example.kma_application.Models.Teacher;
import com.example.kma_application.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;
import static com.example.kma_application.Activity.ContactActivity.getSocket;


public class UserFragment extends Fragment implements LoadInfosTask.AsyncResponse, SubmitImageTask.AsyncResponse {
    Button btLogout, btChangePassword, btEditAvatar;
    EditText txtName, txtPhone, txtId;
    ImageView imgAvatar;
    String phone, role;
    Person person;
    TextView txtStillThinkAboutTheName;
    String newName;

    private final int IMAGE_REQUEST_ID = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        loadInfoToView(view);

//        Button btEditName = (Button)view.findViewById(R.id.buttonEditPersonName);
        Button btSubmitName = (Button)view.findViewById(R.id.buttonSubmitName);
        EditText editTextName = (EditText)view.findViewById(R.id.editTextPersonName);

//        btEditName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editTextName.setEnabled(true);
//                btLogout.setVisibility(view.GONE);
//                btSubmitName.setVisibility(view.VISIBLE);
//            }
//        });

        btSubmitName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextName.setEnabled(false);
                btLogout.setVisibility(view.VISIBLE);
                btSubmitName.setVisibility(view.GONE);
                newName = editTextName.getText().toString();
            }
        });

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtLogout();
            }
        });

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChangePass();
            }
        });

        btEditAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEditAvatar();
            }
        });
        return view;
    }

    private void loadInfoToView(View view) {

        btLogout = (Button)view.findViewById(R.id.buttonLogout);
        btChangePassword = (Button)view.findViewById(R.id.buttonChangePass);
        btEditAvatar = (Button)view.findViewById(R.id.buttonEditAvatar);
        txtPhone = (EditText)view.findViewById(R.id.editTextPersonPhone);
        txtId = (EditText)view.findViewById(R.id.editTextPersonID);
        txtName = (EditText)view.findViewById(R.id.editTextPersonName);
        txtStillThinkAboutTheName = (TextView) view.findViewById(R.id.textInfoChildName);
        imgAvatar = (ImageView)view.findViewById(R.id.avatar);

        txtName.setText(person.getName());
        txtId.setText(person.getEmail());
        txtPhone.setText(person.getPhone());

        if (role.equals("parent")){
            Parent parent = (Parent) person;
            txtStillThinkAboutTheName.setText(parent.getChildName()+" - Lớp "+parent.get_class());
        }else if (role.equals("teacher")){
            Teacher teacher = (Teacher) person;
            txtStillThinkAboutTheName.setText("Lớp "+teacher.get_class());
        }else
            txtStillThinkAboutTheName.setText("Admin");

        getImagesFromServer();
    }

    private void onClickEditAvatar() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_ID);
    }

    private void onClickChangePass() {
        Intent changePassActivity = new Intent(getActivity(), ChangePasswordActivity.class);
        changePassActivity.putExtra("phone",person.getPhone());
        startActivity(changePassActivity);
    }

    private void onClickBtLogout() {
        Intent loginActivity = new Intent(getActivity(), LoginActivity.class);
        getSocket().close();
        getSocket().disconnect();
        startActivity(loginActivity);
        getActivity().finish();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "RQ code: " + requestCode + "\tRS code: " + resultCode);
        //Toast.makeText(this,"RQ code: "+requestCode+"\tRS code: "+resultCode,Toast.LENGTH_LONG).show();
        if (requestCode == IMAGE_REQUEST_ID && resultCode == RESULT_OK) {
            try {
                Uri imageURI = data.getData();
                Context applicationContext = MainActivity.getContextOfApplication();

                InputStream inputStream =
                    applicationContext.getContentResolver().openInputStream(imageURI);
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap resizeBitmap = resize(originalBitmap, 400, 400);

                sendImage(resizeBitmap);
                //getImagesFromServer();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendImage(Bitmap resizeBitmap) {

        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream2);
        String resizeBase64 = Base64.encodeToString(outputStream2.toByteArray(), Base64.NO_WRAP);
        System.out.println(resizeBase64);

        SubmitImageTask submitImageTask = new SubmitImageTask(
                getContext(),
                (Teacher) person,
                resizeBase64,
                "",
                "main",
                this
        );
        submitImageTask.execute();
    }

    private void getImagesFromServer() {
        new LoadImageTask(
                getActivity(),
                imgAvatar,
                person.getPhone(),
                "main"
        ).execute();
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
    @Override
    public void onLoadInfoTaskFinish(Person output, String role) {
        this.person = output;
        this.role = role;
    }

    @Override
    public void onSubmitImageTaskFinish() {
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Do something after 3s
//            }
//        }, 3000);
        getImagesFromServer();
    }
}