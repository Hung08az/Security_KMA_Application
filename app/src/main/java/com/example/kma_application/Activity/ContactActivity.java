package com.example.kma_application.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kma_application.Adapter.MessageAdapter;
import com.example.kma_application.AsyncTask.LoadMessagesTask;
import com.example.kma_application.Models.Parent;
import com.example.kma_application.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Calendar;

public class ContactActivity extends AppCompatActivity {
    String phone, role, coupleUserPhone;
    Parent parent;
    EditText editText;
    ImageButton btCall, btnSend, btnSendImage;
    RecyclerView recyclerView;
    TextView txtChatName;
    MessageAdapter messageAdapter;

    private  final int IMAGE_REQUEST_ID = 1;
    private  final  String CLIENT_SEND_CHAT = "CLIENT_SEND_CHAT";
    private  final  String SERVER_SEND_CHAT = "SERVER_SEND_CHAT";

    public static Socket getSocket() {
        return mSocket;
    }

    public static Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://nodejscloudkenji.herokuapp.com");
            //mSocket = IO.socket("http://192.168.1.68:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        anhxa();

        getInfo();

        mSocket.connect();
        mSocket.on(SERVER_SEND_CHAT,onRetrieveData);
        mSocket.on("ping",onRetrieveHeartBeat);
        mSocket.on("server_req_cp_phone",onServerReqCpPhone);

        messageAdapter = new MessageAdapter(phone, getLayoutInflater());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSend();
            }
        });
        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendImage();
            }
        });
        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCall();
            }
        });
    }

    private void onClickCall() {
        String phone;
        if (role.equals("parent")){
            phone = parent.getTeacherPhone();
        }else {
            phone = parent.getPhone();
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //mSocket.disconnect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if (!mSocket.connected())   mSocket.connect();
    }
    private Emitter.Listener onServerReqCpPhone = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("coupleUserPhone",coupleUserPhone);
        }
    };
    private Emitter.Listener onRetrieveHeartBeat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            //to String
            String timeNow =
                    calendar.get(Calendar.HOUR_OF_DAY)+":"+
                            calendar.get(Calendar.MINUTE)+":"+
                            calendar.get(Calendar.SECOND);
            mSocket.emit("pong",timeNow+": client "+phone);
        }
    };

    private Emitter.Listener onRetrieveData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];

                    try {
                        if (jsonObject.getString("coupleUserPhone")
                                .equals(coupleUserPhone))
                        {
                            messageAdapter.addItem(jsonObject);
                            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private void getInfo(){
        txtChatName = (TextView)findViewById(R.id.textChatName);
        Intent data = getIntent();
        role = data.getStringExtra("role");
        parent = (Parent) data.getSerializableExtra("info");

        coupleUserPhone = parent.getTeacherPhone()+"+"+parent.getPhone();

        if (role.equals("parent")){
            txtChatName.setText("Cô "+parent.getTeacherName()+" - Lớp "+parent.get_class());
            phone = parent.getPhone();
        }else {
            txtChatName.setText("Phụ huynh bé "+parent.getChildName());
            phone = parent.getTeacherPhone();
        }
    }

    private  void loadMessages(){
        new LoadMessagesTask(
            this,
            coupleUserPhone,
            messageAdapter,
            recyclerView
        ).execute();
    }
    private void onClickSend() {
        String content = editText.getText().toString().trim();
        if ( !TextUtils.isEmpty(content) ){
            JSONObject jsonObject= new JSONObject();
            try {
                jsonObject.put("coupleUserPhone",coupleUserPhone);
                jsonObject.put("senderPhone",phone);
                jsonObject.put("type","text");
                jsonObject.put("content",content);

                if (jsonObject.length() == 4){
                    mSocket.emit(CLIENT_SEND_CHAT,jsonObject);
                    editText.setText("");
                }else
                    System.out.println(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void onClickSendImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, "Pick image"),
                IMAGE_REQUEST_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_ID && resultCode == RESULT_OK) {

            try {
                InputStream is = getContentResolver().openInputStream(data.getData());
                Bitmap image = BitmapFactory.decodeStream(is);

                sendImage(image);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    private void sendImage(Bitmap image) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

        String base64String = Base64.encodeToString(outputStream.toByteArray(),
                Base64.NO_WRAP);

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("coupleUserPhone",coupleUserPhone);
            jsonObject.put("senderPhone",phone);
            jsonObject.put("type","image");
            jsonObject.put("content",base64String);

            if (jsonObject.length() == 4){
                mSocket.emit(CLIENT_SEND_CHAT,jsonObject);
            }else
                System.out.println(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void anhxa() {
        recyclerView = findViewById(R.id.recyclerView);
        editText = (EditText)findViewById(R.id.editextContent);
        btCall = (ImageButton)findViewById(R.id.btCall);
        btnSend = (ImageButton)findViewById(R.id.buttonSend);
        btnSendImage = (ImageButton)findViewById(R.id.buttonSendImage);
    }


}