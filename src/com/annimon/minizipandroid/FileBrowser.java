package com.annimon.minizipandroid;

import android.app.ListActivity;
import android.os.Environment;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * File browser.
 * @author aNNiMON
 */
public class FileBrowser {
    
    // Comparator for sorting files.
    private final FilesComparator filesComparator;
    // Filter for directories and files.
    private final FileFilter dirFilter, fileFilter;
    
    private String startDir;
    private ListActivity activity;
    
    private FileOpenEventListener fileOpenEventListener;
    
    private List<String> item, path;
    private File currentDir;

    public FileBrowser(ListActivity activity) {
        this(activity, Environment.getExternalStorageDirectory().getPath());
    }

    public FileBrowser(ListActivity activity, String startDir) {
        this.activity = activity;
        this.startDir = startDir;
        
        item = new ArrayList<String>();
        path = new ArrayList<String>();
        
        filesComparator = new FilesComparator();
        dirFilter = new FileFilter() {
            // Filter only readable directories.
            public boolean accept(File file) {
                return (file.isDirectory() && !file.isHidden() && file.canRead());
            }
        };
        fileFilter = new FileFilter() {
         // Filter only readable files.
            public boolean accept(File file) {
                return (!file.isDirectory() && !file.isHidden() && file.canRead());
            }
        };
        // Begin scan files.
        scanDirectory(startDir);
    }

    public void setFileOpenEventListener(FileOpenEventListener fileOpenEvent) {
        this.fileOpenEventListener = fileOpenEvent;
    }
    
    public String getCurrentDir() {
        return currentDir.getAbsolutePath();
    }
    
    public void setCurrentDir(String path) {
        scanDirectory(path);
    }
    
    public void itemSelected(int index) {
        File file = new File(path.get(index));

        if (file.isDirectory()) {
            scanDirectory(path.get(index));
            return;
        }
        
        if (fileOpenEventListener != null) {
            fileOpenEventListener.onFileOpen(file);
        }
    }
    
    public void upDirectory() {
        if (currentDir.getPath().equals(startDir)) {
            activity.finish();
        } else {
            scanDirectory(currentDir.getParent());
        }
    }
    
    public void rescanCurrentDirectory() {
        scanDirectory(currentDir.getPath());
    }
    
    private void scanDirectory(String dirPath) {
        activity.setTitle(dirPath);
        
        item.clear();
        path.clear();
        
        File f = new File(dirPath);
        currentDir = f;
        
        if (!dirPath.equals(startDir)) {
            item.add("../");
            path.add(f.getParent());
        }
        
        addDirectories(f);
        addFiles(f);

        FileListAdapter fileListAdapter = new FileListAdapter(activity, item);
        activity.setListAdapter(fileListAdapter);
    }
    
    private void addDirectories(File file) {
        File[] directories = file.listFiles(dirFilter);
        sortFiles(directories);

        for (int i = 0; i < directories.length; i++) {
            File f = directories[i];
            path.add(f.getPath());
            item.add(f.getName() + "/");
        }
    }
    
    private void addFiles(File file) {
        File[] files = file.listFiles(fileFilter);
        sortFiles(files);

        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            path.add(f.getPath());
            item.add(f.getName());
        }
    }
    
    private void sortFiles(File[] files) {
        Arrays.sort(files, filesComparator);
    }
    
    
    private class FilesComparator implements Comparator<File> {

        public int compare(File file1, File file2) {
            return file1.getName().compareToIgnoreCase(file2.getName());
        }
        
    }
}
