package com.hufeng.filemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.hufeng.filemanager.data.BelugaFileEntry;
import com.hufeng.filemanager.dialog.BelugaDialogFragment;
import com.hufeng.filemanager.intent.Constant;
import com.hufeng.filemanager.utils.MimeUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Feng Hu on 15-02-09.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaActionDelegate {

    public static void cut(Context context, BelugaFileEntry... entries) {
        Intent intent = new Intent(Constant.ACTION_PICK_FOLDER_TO_MOVE_FILE);
        intent.putExtra(BelugaDialogFragment.FILE_ARRAY_DATA, entries);
        ((Activity)context).startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FOLDER_TO_MOVE_FILE);
    }

    public static void copy(Context context, BelugaFileEntry... entries) {
        Intent intent = new Intent(Constant.ACTION_PICK_FOLDER_TO_COPY_FILE);
        intent.putExtra(BelugaDialogFragment.FILE_ARRAY_DATA, entries);
        ((Activity)context).startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FOLDER_TO_COPY_FILE);
    }

    public static void delete(FragmentActivity activity, BelugaFileEntry... entries) {
        BelugaDialogFragment.showDeleteDialog(activity, entries);
    }

    public static void rename(FragmentActivity activity, BelugaFileEntry... entries) {
        if (entries.length > 1) {
            Toast.makeText(activity, R.string.can_not_rename_multiple, Toast.LENGTH_SHORT);
            return;
        }
        BelugaDialogFragment.showRenameDialog(activity, entries[0]);
    }

    public static void details(FragmentActivity activity, BelugaFileEntry... entries) {
        if (entries.length > 1) {
            Toast.makeText(activity, R.string.can_not_details_multiple, Toast.LENGTH_SHORT);
            return;
        }
        BelugaDialogFragment.showDetailsDialog(activity, entries[0]);
    }

//    public static void markFavorite(FileEntry entry) {
//        FileAction.addToFavorite(entry.path);
//    }
//
//    public static void undoFavorite(FileEntry entry) {
//        FileAction.removeFromFavorite(entry.path);
//    }

    public static void view(Context context, BelugaFileEntry entry) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(entry.path)), MimeUtil.getMimeType(entry.path));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Can not open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void share(Context context, BelugaFileEntry... entries) {
        if (entries.length == 0) {
            return;
        }
        final boolean multiple = entries.length > 1;
        final Intent intent = new Intent(multiple ? android.content.Intent.ACTION_SEND_MULTIPLE
                : android.content.Intent.ACTION_SEND);

        if (multiple) {
            ArrayList<Uri> arrayUri = new ArrayList<Uri>();
            String mimeType = null;
            String mimeTypeFirstPart = null;
            boolean sameMimeType = true;
            boolean sameMimeTypeFirstPart = true;
            for (BelugaFileEntry entry : entries) {
                arrayUri.add(Uri.fromFile(new File(entry.path)));
                if (mimeType == null) {
                    mimeType = MimeUtil.getMimeType(entry.path);
                    mimeTypeFirstPart = mimeType.split("/")[0];
                } else {
                    if (sameMimeType || sameMimeTypeFirstPart) {
                        String thisMimeType = MimeUtil.getMimeType(entry.path);
                        if (sameMimeType && mimeType.equals(thisMimeType)) {
                            continue;
                        } else {
                            sameMimeType = false;
                        }
                        String thisMimeTypeFirstPart = MimeUtil.getMimeType(entry.path);
                        if (sameMimeTypeFirstPart && thisMimeTypeFirstPart.equals(mimeTypeFirstPart)) {
                            continue;
                        } else {
                            sameMimeTypeFirstPart = false;
                        }
                    }
                }
            }
            if (sameMimeType) {
                intent.setType(mimeType);
            } else if (sameMimeTypeFirstPart) {
                intent.setType(mimeTypeFirstPart+"/*");
            } else {
                intent.setType("*/*");
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayUri);
        } else {
            intent.setType(MimeUtil.getMimeType(entries[0].path));
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(entries[0].path)));
        }
        context.startActivity(intent);
    }

}
