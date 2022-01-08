package com.example.kma_application.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kma_application.AsyncTask.LoginTask;
import com.example.kma_application.R;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    EditText txtPhone,txtPassword;
    Button btLogin;
    ImageView bt_fingerprint;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Init view
        txtPhone = (EditText)findViewById(R.id.editTextPhone);
        txtPassword = (EditText)findViewById(R.id.editTextTextPassword);
        btLogin = (Button)findViewById(R.id.buttonLogin);
        bt_fingerprint = findViewById(R.id.bt_fingerprint);

//        pref = getApplicationContext().getSharedPreferences("KMA_App_Pref", MODE_PRIVATE);

        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            pref = EncryptedSharedPreferences.create(
                    "Secret_KMA_App_Pref",
                    masterKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor = pref.edit();

        //Event handle
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtLogin();
            }
        });
        bt_fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtFingerprint();
            }
        });
    }

    private void onClickBtFingerprint() {
        //kiểm tra đã lưu mật khẩu chưa
        String storedPassword = pref.getString("password", "");
        if (TextUtils.isEmpty(storedPassword)){
            Toast.makeText(this,"Bạn cần đăng nhập bằng mật khẩu ít nhất một lần trước khi sử dụng tính năng này!",Toast.LENGTH_LONG).show();
            return;
        }

        BiometricManager biometricManager = BiometricManager.from(this);
        //kiểm tra cảm biến
        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this,"Thiết bị không có cảm biến vân tay!",Toast.LENGTH_LONG).show();
                return;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this,"Bạn chưa lưu vân tay nào, hãy kiểm tra trong cài đặt bảo mật!",Toast.LENGTH_LONG).show();
                return;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this,"Cảm biến vân tay hiện không hoạt động!",Toast.LENGTH_LONG).show();
                return;
        }

        //tạo box check vân tay
        Executor executor = ContextCompat.getMainExecutor(this);
        final BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }
            //khi xác thực vân tay hợp lệ
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                onAuthFingerprintSucceeded();
            }
            //khi xác thực vân tay KHÔNG hợp lệ
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng nhập")
                .setDescription("Chạm vào cảm biến vân tay để đăng nhập")
                .setNegativeButtonText("Hủy bỏ")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    //khi xác thực vân tay hợp lệ
    private void onAuthFingerprintSucceeded() {
        String storedPassword = pref.getString("password", "");
        String storedPhone = pref.getString("phone", "");
        new LoginTask(
                this,
                storedPhone,
                storedPassword,
                pref,
                editor
        ).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String storedPhone = pref.getString("phone", "");
        if (!TextUtils.isEmpty(storedPhone))
            txtPhone.setText(storedPhone);
    }

    private void onClickBtLogin() {
        loginUser(txtPhone.getText().toString().trim(),
                txtPassword.getText().toString().trim());
    }


    private void loginUser(String phone, String password) {
        if (TextUtils.isEmpty(phone)){

            Toast.makeText(this,"Số điện thoại không được để trống",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)){

            Toast.makeText(this,"Mật khẩu không được để trống",Toast.LENGTH_LONG).show();
            return;
        }
        new LoginTask(
                this,
                phone,
                password,
                pref,
                editor
        ).execute();
    }

}