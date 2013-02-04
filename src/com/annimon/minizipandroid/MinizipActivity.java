package com.annimon.minizipandroid;

import java.io.File;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class MinizipActivity extends ListActivity implements FileOpenEventListener {
    
    private FileBrowser fileBrowser;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        fileBrowser = new FileBrowser(this);
        fileBrowser.setFileOpenEventListener(this);
        
        // Other program sends zip file to this app.
        Intent intent = getIntent();
        if (intent.getData() != null) {
            String zipFilePath = intent.getData().getEncodedPath();
            File zipFile = new File(zipFilePath);
            onFileOpen(zipFile);
        }
        
        // Using ActionBar on Android >= 3.0 to navigate backward.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Clicked on ActionBar.
                fileBrowser.upDirectory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("dir_path", fileBrowser.getCurrentDir());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        String dir = savedInstanceState.getString("dir_path");
        fileBrowser.setCurrentDir(dir);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int index, long id) {
        fileBrowser.itemSelected(index);
    }

    @Override
    public void onBackPressed() {
        fileBrowser.upDirectory();
    }

    public void onFileOpen(final File openedFile) {
        PasswordDialog passwordDialog = new PasswordDialog(this) {

            @Override
            protected void onPasswordEntered(String password) {
                if (password != null) {
                    ZipTask task = new ZipTask(MinizipActivity.this, openedFile);
                    task.setFileBrowser(fileBrowser);
                    task.execute(password);
                }
            }
            
        };
        passwordDialog.show();
    }

}
