package com.annimon.minizipandroid;

import java.io.File;

/**
 * Listener for event, when user select a file.
 * @author aNNiMON
 */
public interface FileOpenEventListener {
    
    void onFileOpen(File openedFile);
}
