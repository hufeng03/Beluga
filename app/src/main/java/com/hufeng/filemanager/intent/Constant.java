package com.hufeng.filemanager.intent;

/**
 * Created by feng on 14-11-16.
 */
public class Constant {
    public static final int REQUEST_CODE_PICK_FILE = 1;
    public static final int REQUEST_CODE_PICK_FOLDER_TO_COPY_FILE = 2;
    public static final int REQUEST_CODE_PICK_FOLDER_TO_MOVE_FILE = 3;
    public static final int REQUEST_CODE_PICK_FOLDER_TO_EXTRACT_ARCHIVE = 4;
    public static final int REQUEST_CODE_PICK_FOLDER_TO_CREATE_ARCHIVE = 5;
    public static final String ACTION_PICK_FOLDER_TO_COPY_FILE = "beluga.action.COPY_FILE";
    public static final String ACTION_PICK_FOLDER_TO_MOVE_FILE = "beluga.action.MOVE_FILE";
    public static final String ACTION_PICK_FOLDER_TO_EXTRACT_ARCHIVE = "beluga.action.EXTRACT_ARCHIVE";
    public static final String ACTION_PICK_FOLDER_TO_CREATE_ARCHIVE = "beluga.action.CREATE_ARCHIVE";

    public static final String ACTION_PICK_KANBOX_DIRECTORY_TO_UPLOAD_FILE = "beluga.action.UPLOAD_FILE";
    public static final String ACTION_PICK_FILE = "beluga.action.PICK_FILE";
    public static final String ACTION_CANCEL_PASTE_FILE = "beluga.action.CANCEL_PASTE_FILE";
    public static final String ACTION_CANCEL_UPLOAD_FILE = "beluga.action.CANCEL_UPLOAD_FILE";
    public static final String ACTION_VIEW_REMOTE_FTP = "beluga.action.VIEW_FTP";
    public static final String ACTION_VIEW_REMOTE_HTTP = "beluga.action.VIEW_HTTP";
    public static final String ACTION_AUTHENTICATE_KANBOX = "beluga.action.AUTHENTICATE_KANBOX";

    public static final String INTENT_EXTRA_OPERATION_FILES = "files";

    public static final String IMAGE_TYPE = "image/*";
    public static final String AUDIO_TYPE = "audio/*";
    public static final String VIDEO_TYPE = "video/*";


}
