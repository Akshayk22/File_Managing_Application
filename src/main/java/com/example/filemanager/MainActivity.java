package com.example.filemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAMES_PREF = "FileNamesPref";
    private static final String FILE_CONTENT_PREF = "FileContentPref";

    private EditText editTextFileName;
    private EditText editTextFileContent;
    private Button buttonCreate;
    private Button buttonSave;
    private Button buttonOpen;
    private TextView textView;

    private SharedPreferences fileNamesPref;
    private SharedPreferences fileContentPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        editTextFileName = findViewById(R.id.editTextFileName);
        editTextFileContent = findViewById(R.id.editTextFileContent);
        buttonCreate = findViewById(R.id.buttonCreate);
        buttonSave = findViewById(R.id.buttonSave);
        buttonOpen = findViewById(R.id.buttonOpen);
        textView = findViewById(R.id.textView);

        fileNamesPref = getSharedPreferences(FILE_NAMES_PREF, Context.MODE_PRIVATE);
        fileContentPref = getSharedPreferences(FILE_CONTENT_PREF, Context.MODE_PRIVATE);

        Button buttonShowFiles = findViewById(R.id.buttonShowFiles);
        buttonShowFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileList();
            }
        });






        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = editTextFileName.getText().toString().trim();
                if (fileName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a file name", Toast.LENGTH_SHORT).show();
                } else if (!fileExists(fileName)) {
                    createFile(fileName);
                    saveFileNames(fileName);
                    saveFileContent(fileName, "");
                    Toast.makeText(MainActivity.this, "File created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "File already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = editTextFileName.getText().toString().trim();
                String fileContent = editTextFileContent.getText().toString();
                if (fileName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a file name", Toast.LENGTH_SHORT).show();
                } else if (fileExists(fileName)) {
                    saveFileContent(fileName, fileContent);
                    Toast.makeText(MainActivity.this, "File saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "First create a file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = editTextFileName.getText().toString().trim();
                if (fileName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a file name", Toast.LENGTH_SHORT).show();
                } else if (fileExists(fileName)) {
                    String fileContent = getFileContent(fileName);
                    editTextFileContent.setText(fileContent);
                    Toast.makeText(MainActivity.this, "File opened successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "File not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean fileExists(String fileName) {
        Set<String> fileNames = getFileNames();
        return fileNames.contains(fileName);
    }

    private void createFile(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(this, "Please enter a file name", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText editText = findViewById(R.id.editTextFileName);
        String fileContent = editText.getText().toString();

        if (TextUtils.isEmpty(fileContent)) {
            Toast.makeText(this, "Please enter file content", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(getExternalFilesDir(null), fileName);

        try {
            FileWriter writer = new FileWriter(file);
            writer.append(fileContent);
            writer.flush();
            writer.close();
            Toast.makeText(this, "File created: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }



    private void saveFileNames(String fileName) {
        Set<String> fileNames = getFileNames();
        fileNames.add(fileName);
        SharedPreferences.Editor editor = fileNamesPref.edit();
        editor.putStringSet("fileNames", fileNames);
        editor.apply();
    }

    private Set<String> getFileNames() {
        return fileNamesPref.getStringSet("fileNames", new HashSet<String>());
    }

    private void saveFileContent(String fileName, String fileContent) {
        SharedPreferences.Editor editor = fileContentPref.edit();
        editor.putString(fileName, fileContent);
        editor.apply();
    }

    private String getFileContent(String fileName) {
        return fileContentPref.getString(fileName, "");
    }

    private void showFileList() {
        Set<String> fileNames = getFileNames();
        StringBuilder fileListBuilder = new StringBuilder();
        for (String fileName : fileNames) {
            fileListBuilder.append(fileName).append("\n");
        }
        String fileList = fileListBuilder.toString().trim();

        // Display the file list using a dialog or any other appropriate UI element
        // For example, you can use an AlertDialog to show the list of files
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Saved Files");
        builder.setMessage(fileList);
        builder.setPositiveButton("OK", null);
        builder.show();
    }


}