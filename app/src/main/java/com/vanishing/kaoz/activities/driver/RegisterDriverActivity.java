package com.vanishing.kaoz.activities.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vanishing.kaoz.R;
import com.vanishing.kaoz.activities.client.RegisterActivity;
import com.vanishing.kaoz.include.MyToolbar;
import com.vanishing.kaoz.models.Client;
import com.vanishing.kaoz.models.Driver;
import com.vanishing.kaoz.providers.AuthProvider;
import com.vanishing.kaoz.providers.ClientProvider;
import com.vanishing.kaoz.providers.DriverProvider;

import dmax.dialog.SpotsDialog;

public class RegisterDriverActivity extends AppCompatActivity {



    AuthProvider mAuthProvider;
    DriverProvider mDriverProvider;

    //views

    Button mButtonRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputName;
    TextInputEditText mTextInputVehicleBrand;
    TextInputEditText mTextInputVehiclePlate;
    TextInputEditText mTextInputPassword;

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        MyToolbar.show(this, "Registro de conductor", true);

        mAuthProvider = new AuthProvider();
        mDriverProvider = new DriverProvider();


        mDialog = new SpotsDialog.Builder().setContext(RegisterDriverActivity.this).setMessage("Espere un momento").build();


        mButtonRegister = findViewById(R.id.btnRegister);
        mTextInputName = findViewById(R.id.textInputName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputVehicleBrand = findViewById(R.id.textInputVehicleBrand);
        mTextInputVehiclePlate = findViewById(R.id.textInputVehiclePlate);
        mTextInputPassword = findViewById(R.id.textInputPassword);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickRegister();
            }
        });
    }

    void clickRegister() {
        final String name = mTextInputName.getText().toString();
        final String email = mTextInputEmail.getText().toString();
        final String vehiclePlate = mTextInputVehicleBrand.getText().toString();
        final String vehicleBrand = mTextInputVehiclePlate.getText().toString();
        final String password = mTextInputPassword.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                mDialog.show();
                register(name, email, password, vehicleBrand, vehiclePlate);

            } else {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();

            }

        } else {

            Toast.makeText(this, "Ingrese todo los campos", Toast.LENGTH_SHORT).show();
        }

    }

    void register(final String name, final String email, String password, String vehicleBrand, String vehiclePlate) {
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.hide();
                if (task.isSuccessful()) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Driver driver = new Driver(id, name, email, vehiclePlate, vehicleBrand);
                    create(driver);

                } else {
                    Toast.makeText(RegisterDriverActivity.this, "No se pudo registrar el conductor", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    void create(Driver driver) {
        mDriverProvider.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(RegisterDriverActivity.this, "se registro el usuario correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterDriverActivity.this, MapDriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Toast.makeText(RegisterDriverActivity.this, "No se pudo crear el cliente", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}