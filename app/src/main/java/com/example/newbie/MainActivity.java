package com.example.newbie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button scanbtn;
    ImageView image;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        scanbtn = findViewById(R.id.button2);

        image=findViewById(R.id.imageView);

        scanbtn.setOnClickListener(v -> {
                    checkPermissions();
                });


    }

    public void checkPermissions(){
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            // Permissions are already granted
            // Proceed with your logic here
            scanbtn.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, Scan.class);
                startActivity(intent);
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // Proceed with your logic here
                scanbtn.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, Scan.class);
                    startActivity(intent);
                });
            } else {
                // Permission denied
                // Handle the scenario when the user denies the permissions
                Toast.makeText(MainActivity.this, "Please grant the required permissions",Toast.LENGTH_SHORT).show();
            }
        }
    }
}