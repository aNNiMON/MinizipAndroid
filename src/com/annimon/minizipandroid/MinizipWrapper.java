package com.annimon.minizipandroid;

/**
 * Wrapper for minizip.cpp JNI class.
 * @author aNNiMON
 */
public class MinizipWrapper {
    
    static {
        System.loadLibrary("minizip");
    }
    
    private static final int[] ERROR_ID = {
        -3, -100, -101, -102, -103, -104
    };
    
    private static final int[] MESSAGE_ID = {
        R.string.error_wrong_password, R.string.error_create_zip,
        R.string.error_get_crc32, R.string.error_while_read,
        R.string.error_file_not_found, R.string.error_zip_file_not_found
    };
    
    public static int getErrorMessageById(int errorId) {
        for (int i = 0; i < ERROR_ID.length; i++) {
            if (errorId == ERROR_ID[i]) return MESSAGE_ID[i];
        }
        return R.string.error;
    }
    
    public native int createZip(String zipfilename, String filename, String password);
    
    public native int extractZip(String zipfilename, String dirname, String password);
    
    public native String getFilenameInZip(String zipfilename);
}
