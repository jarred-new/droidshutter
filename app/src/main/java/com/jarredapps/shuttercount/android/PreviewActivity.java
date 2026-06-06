package com.jarredapps.shuttercount.android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

public class PreviewActivity extends Activity {

    public static final String TAG = "PreviewActivity";

    private ImageView previewImageView;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        setTitle("Preview Image");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) 
                == PackageManager.PERMISSION_DENIED) {
                requestPermissions(
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    100
                );
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_DENIED) {
                requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100
                );
            }
        }

        previewImageView = findViewById(R.id.previewImageView);
        filePath = getIntent().getStringExtra("filePath");

        if (filePath != null) {

            try {

                Uri uri = Uri.parse(filePath);

                ImageDecoder.Source source =
                    ImageDecoder.createSource(
                    getContentResolver(),
                    uri
                );

                Bitmap bitmap =
                    ImageDecoder.decodeBitmap(source);

                if (previewImageView != null)
                    previewImageView.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("An error occured")
                .setMessage("You did not load your image")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        finish();
                    }
                })              
                .create();
            dialog.show();
        }
    }

}
