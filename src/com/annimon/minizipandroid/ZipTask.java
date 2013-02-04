package com.annimon.minizipandroid;

import java.io.File;
import java.util.Locale;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.MimeTypeMap;

/**
 * Asynchronic task for all zip operations.
 * @author aNNiMON
 */
public class ZipTask extends AsyncTask<String, Void, Integer> {
    
    private Context context;
    private File openedFile;
    private FileBrowser browser;
    
    private ProgressDialog progress;

    public ZipTask(Context context, File openedFile) {
        this.context = context;
        this.openedFile = openedFile;
    }
    
    public void setFileBrowser(FileBrowser browser) {
        this.browser = browser;
    }
    
    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(context);
        progress.setMessage(context.getString(R.string.please_wait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    @Override
    protected Integer doInBackground(String... params) {
        String password = params[0];
        String filePath = openedFile.getAbsolutePath();
        
        if (filePath.toLowerCase(Locale.ENGLISH).endsWith(".zip")) {
            return extractZip(openedFile, password, filePath);
        }
        
        return createZip(openedFile, password, filePath);
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        progress.dismiss();
        
        if (browser != null) {
            browser.rescanCurrentDirectory();
        }
        if (result.intValue() != 0) {
            int errorMessageId = MinizipWrapper.getErrorMessageById(result);
            showMessageDialog(context.getString(errorMessageId));
        }
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
                String message = context.getString(R.string.register_app_not_found);
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

        context.startActivity(intent);
    }

    private void showMessageDialog(String text) {
        new AlertDialog.Builder(context)
            .setCancelable(true)
            .setTitle(android.R.string.dialog_alert_title)
            .setMessage(text)
            .show();
    }
    
}
