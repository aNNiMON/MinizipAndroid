package com.annimon.minizipandroid;

import java.io.File;
import java.util.Locale;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ListView;

public class MinizipActivity extends ListActivity implements FileOpenEventListener {
    
    private static Handler handler;
    
    private FileBrowser fileBrowser;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         
        fileBrowser = new FileBrowser(this);
        fileBrowser.setFileOpenEventListener(this);
        
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.please_wait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                fileBrowser.rescanCurrentDirectory();
                progress.dismiss();
                
                int result = msg.what;
                if (result != 0) {
                    int errorMessageId = MinizipWrapper.getErrorMessageById(result);
                    showMessageDialog(getResources().getString(errorMessageId));
                }
            }
        };
        
        // Other program sends zip file to this app.
        Intent intent = getIntent();
        if (intent.getData() != null) {
            String zipFilePath = intent.getData().getEncodedPath();
            File zipFile = new File(zipFilePath);
            onFileOpen(zipFile);
        }
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
        final PasswordDialog passwordDialog = new PasswordDialog(this);
        passwordDialog.show();
        passwordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            public void onDismiss(DialogInterface dialog) {
                String password = passwordDialog.getPassword();
                if (password != null) {
                    startZipThread(openedFile, password);
                }
            }
        });
    }
    
    private void startZipThread(final File openedFile, final String password) {
        progress.show();
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                int result = processZip(openedFile, password);
                handler.sendEmptyMessage(result);
                
            }
        }).start();
    }

    private int processZip(File openedFile, String password) {
        String filePath = openedFile.getAbsolutePath();
        
        if (filePath.toLowerCase(Locale.ENGLISH).endsWith(".zip")) {
            return extractZip(openedFile, password, filePath);
        }
        
        return createZip(openedFile, password, filePath);
    }

    private int createZip(File openedFile, String password, String filePath) {
        MinizipWrapper wrapper = new MinizipWrapper();
        
        // Build zip filename from path to archived file.
        String zipFileName;
        int lastPointIndex = openedFile.getName().lastIndexOf('.');
        if (lastPointIndex <= 0) zipFileName = openedFile.getAbsolutePath();
        else {
            lastPointIndex = filePath.lastIndexOf('.');
            zipFileName = filePath.substring(0, lastPointIndex);
        }
        zipFileName = zipFileName + ".zip";
        
        return wrapper.createZip(zipFileName, openedFile.getAbsolutePath(), password);
    }

    private int extractZip(File openedFile, String password, String filePath) {
        MinizipWrapper wrapper = new MinizipWrapper();
        
        String filenameInZip = wrapper.getFilenameInZip(filePath);
        if (filenameInZip == null) return -1;
        
        File extractedFile = new File(openedFile.getParent(), filenameInZip);
        if (extractedFile.exists()) {
            extractedFile.delete();
        }
        
        int result = wrapper.extractZip(filePath, openedFile.getParent(), password);
        if (result == 0) {
            try {
                openFileInRegisterApplication(extractedFile);
            } catch(ActivityNotFoundException ex) {
                String message = getResources().getString(R.string.register_app_not_found);
                showMessageDialog(message);
            }
        }
        
        return result;
    }
    
    private void openFileInRegisterApplication(File file) throws ActivityNotFoundException {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        
        // Get mime-type from file extension.
        Uri fileUri = Uri.fromFile(file);
        String ext = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        intent.setDataAndType(fileUri, type);

        startActivity(intent);
    }
    
    private void showMessageDialog(String text) {
        new AlertDialog.Builder(this)
            .setCancelable(true)
            .setTitle(android.R.string.dialog_alert_title)
            .setMessage(text)
            .show();
    }
}
