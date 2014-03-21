package com.hufeng.filemanager.kanbox;

import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by feng on 13-11-22.
 */
public class KanBoxFileEntry extends FileEntry {

    public String hash;
    public String local_file_path;
    public byte[] icon_data;

    public KanBoxFileEntry(String local_path) {
        super(local_path);
    }

    public KanBoxFileEntry(String hs, JSONObject obj) {
        hash = hs;
        try {
            path = obj.getString("fullPath");
            int idx = path.lastIndexOf("/");
            if ( idx >= 0 ){
                name = path.substring(idx + 1);
                parent_path = path.substring(0,idx + 1);
            }
            String dStr = obj.getString("modificationDate");
            Date d = null;
            DateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            try {
                d = sdf.parse(dStr);
            } catch (ParseException pe) {
                //pe.printStackTrace();
            }
            if(d == null) {
                DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                try {
                    d = sdf2.parse(dStr);
                } catch (ParseException pe) {
                   // pe.printStackTrace();
                }
            }
            if(d!=null) {
                lastModified =d.getTime();
            }
            size = obj.getLong("fileSize");
            is_directory = obj.getBoolean("isFolder");
            if(size>0) {
                is_directory = false;
            }
            if (!is_directory) {
                type = FileUtils.getFileType(name);
            }

        }catch(JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "hash("+this.hash+")"+super.toString();
    }
}
