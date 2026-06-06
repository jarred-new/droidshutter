package com.jarredapps.shuttercount.android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.app.assist.AssistContent;
import android.widget.AdapterView;
import android.widget.Adapter;

public class MainActivity extends Activity {
    private MetadataParser parser = new MetadataParser();

    // card1
    private LinearLayout card1;
    private TextView assistantTitle;
    private TextView assistantSubtitle;
    private Button openButton;
    private LinearLayout starter;
    private TextView storageText;
    private Button checkButton;

    // card2
    private LinearLayout card2;
    private ListView metadataList;

    private Intent imageImporter = new Intent(Intent.ACTION_GET_CONTENT);
    private Intent previewKey = new Intent(MainActivity.this,
                                         PreviewActivity.class);
    private Intent modelSearchKey = new Intent(MainActivity.this,
                                            ModelSearchActivity.class);

    // #### id's
    private static final int REQUEST_STORAGE_ID = 1000;
    private static final int IMAGE_IMPORTER_ID = 2000;

    private Uri file;
    private String filePath;
    private String filePathUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_ID);
            } else {
                initializeLogic();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_ID) {
            initializeLogic();
        }
    }

    protected void initializeLogic() {
        imageImporter.setType("*/*");
        imageImporter.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        imageImporter.addCategory(Intent.CATEGORY_OPENABLE);

        file = null;

        // card1
        card1 = findViewById(R.id.card1);
        assistantTitle = findViewById(R.id.assistantTitle);
        assistantSubtitle = findViewById(R.id.assistantSubtitle);
        openButton = findViewById(R.id.openButton);
        starter = findViewById(R.id.starter);
        storageText = findViewById(R.id.storageText);
        checkButton = findViewById(R.id.checkButton);

        // card2
        card2 = findViewById(R.id.card2);
        metadataList = findViewById(R.id.metadataList);

        if (starter != null) {
            starter.setVisibility(View.GONE);
        }
        if (card2 != null) {
            card2.setVisibility(View.GONE);
        }
        if (openButton != null) {
            openButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(imageImporter, IMAGE_IMPORTER_ID);
                    }
                });
        }



    }

    private long firstBackTime;

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - firstBackTime > 2000) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            firstBackTime = System.currentTimeMillis();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.showPreview:
                if (filePathUri != null) {
                    previewKey.setClass(this, PreviewActivity.class);
                    previewKey.putExtra("filePath", filePathUri);             
                }
                if (filePath != null) {
                    startActivity(previewKey);
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Oops")
                        .setMessage("You forgot to open image file")
                        .setPositiveButton("Ok", null)              
                        .create();
                    dialog.show();
                }
                return true;

            case R.id.aboutApp:
                final Dialog aboutdlg = new Dialog(this);
                
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.aboutdlg, null);
                Button ok = v.findViewById(R.id.aboutdlgButtonOK);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aboutdlg.dismiss();
                    }
                });
                aboutdlg.setCancelable(true);
                aboutdlg.setContentView(v);
                aboutdlg.show();
                return true;

            case R.id.exitApp:
                AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Are you sure to quit?")
                    //.setMessage("")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dia, int which) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IMAGE_IMPORTER_ID:
                if (resultCode == Activity.RESULT_OK) {
                    final Uri uri;

                    if (data.getClipData() != null) {
                        uri = data.getClipData().getItemAt(0).getUri();
                    } else {
                        uri = data.getData();
                    }

                    if (uri != null) {
                        try {
                            filePath = getPathFromUri(getApplicationContext(), uri);
                            filePathUri = uri.toString();

                            file = uri;
                            starter.setVisibility(View.VISIBLE);
                            storageText.setText(filePath);

                            assistantTitle.setText("Great! You can now start!");
                            assistantSubtitle.setText("Click Start Checking or click on 3 dots and preview image to preview your image");
                            if (checkButton != null) {
                                checkButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            assistantTitle.setText("Here are now your results");
                                            assistantSubtitle.setText("You can see the list of your picture's metadata and you can see the shutter count on the last item. Also, click on camera model to search on google");
                                            String shutterCount = null;
                                            String cameraModel = null;
                                            //String modelToSearch = null;
                                            final String modelToSearch;
                                            //String cameraLens = null;
                                            //String cameraLensModel = null;
                                            //String cameraLensFocalLength = null;
                                            String aperture = null;
                                            String shutterSpeed = null;
                                            String iso = null;
                                            
                                            if (file != null) {
                                                shutterCount = parser.getShutterCount(MainActivity.this, uri);
                                                cameraModel = parser.getCamera(MainActivity.this, uri, true);
                                                modelToSearch = parser.getCamera(MainActivity.this, uri, false);
                                                modelSearchKey.setClass(getApplicationContext(), ModelSearchActivity.class);
                                                modelSearchKey.putExtra("modelToSearch", modelToSearch);
                                                //cameraLens = parser.getCameraLens(MainActivity.this, uri);
                                                //cameraLensModel = parser.getCameraLensModel(MainActivity.this, uri);
                                                //cameraLensFocalLength = parser.getFocalLength(MainActivity.this, uri);
                                                aperture = parser.getAperture(MainActivity.this, uri);
                                                shutterSpeed = parser.getShutterSpeed(MainActivity.this, uri);
                                                iso = parser.getISO(MainActivity.this, uri);
                                            }

                                            if (card2 != null) {
                                                card2.setVisibility(View.VISIBLE);
                                                ArrayList<String> list = new ArrayList<>();
                                                if (!list.isEmpty()) {
                                                    list.clear();
                                                }
                                                list.add(cameraModel);
                                                //list.add(cameraLens);
                                                //list.add(cameraLensModel);
                                                //list.add(cameraLensFocalLength);
                                                list.add(aperture);
                                                list.add(shutterSpeed);
                                                list.add(iso);
                                                list.add(shutterCount);                                               

                                                final ArrayAdapter<String> listStr = new ArrayAdapter<>(
                                                    MainActivity.this, 
                                                    android.R.layout.simple_list_item_1,
                                                    list);

                                                metadataList.setAdapter(listStr);
                                                runOnUiThread(new Runnable(){
                                                        @Override
                                                        public void run() {
                                                            listStr.notifyDataSetChanged();
                                                        }
                                                    });
                                                metadataList.setOnItemClickListener(new ListView.OnItemClickListener() {

                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            if (position == 0) {
                                                                
                                                                startActivity(modelSearchKey);
                                                            }
                                                        }
                                                        
                                                    
                                                });
                                            }
                                        }
                                    });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                }
                break;
            default:
                break;
		}
    }

    public String getPathFromUri(Context context, Uri uri) {
        try {
            // 1. Get the file name from the URI
            String fileName = getFileName(context, uri);

            // 2. Create a temporary file in your app's internal cache
            File tempFile = new File(context.getCacheDir(), fileName);

            // 3. Copy the content from the URI to the temporary file
            try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            return tempFile.getAbsolutePath(); // This is a real file path
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

// Helper to extract filename
    private String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

}
