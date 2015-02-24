/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package refactor.com.android.contacts.list;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.Intents.Insert;
import android.text.TextUtils;
import android.util.Log;

import com.hufeng.filemanager.intent.Constant;

/**
 * Parses a Beluga intent, extracting all relevant parts and packaging them
 * as a {@link BelugaRequest} object.
 */
@SuppressWarnings("deprecation")
public class BelugaIntentResolver {

    private static final String TAG = "ContactsIntentResolver";

    private final Activity mContext;

    public BelugaIntentResolver(Activity context) {
        this.mContext = context;
    }

    public BelugaRequest resolveIntent(Intent intent) {
        BelugaRequest request = new BelugaRequest();

        String action = intent.getAction();

        Log.i(TAG, "Called with action: " + action);

        if (Constant.ACTION_PICK_FOLDER_TO_COPY_FILE.equals(action)) {
            request.setActionCode(BelugaRequest.ACTION_PICK_DIRECTORY_TO_COPY_FILE);
            request.setOperationFiles(intent.getStringArrayExtra(Constant.INTENT_EXTRA_OPERATION_FILES));
        } else if (Constant.ACTION_PICK_FOLDER_TO_MOVE_FILE.equals(action)) {
            request.setActionCode(BelugaRequest.ACTION_PICK_DIRECTORY_TO_MOVE_FILE);
            request.setOperationFiles(intent.getStringArrayExtra(Constant.INTENT_EXTRA_OPERATION_FILES));
        } else if (Constant.ACTION_PICK_KANBOX_DIRECTORY_TO_UPLOAD_FILE.equals(action)) {
            request.setActionCode(BelugaRequest.ACTION_PICK_KANBOX_DIRECTORY_TO_UPLOAD_FILE);
            request.setOperationFiles(intent.getStringArrayExtra(Constant.INTENT_EXTRA_OPERATION_FILES));
        } else if (Constant.ACTION_CANCEL_PASTE_FILE.equals(action)) {

        } else if (Constant.ACTION_CANCEL_UPLOAD_FILE.equals(action)) {

        } else if (Constant.ACTION_VIEW_REMOTE_FTP.equals(action)) {
            request.setActionCode(BelugaRequest.ACTION_VIEW_REMOTE_FTP);
        } else if (Constant.ACTION_VIEW_REMOTE_HTTP.equals(action)) {
            request.setActionCode(BelugaRequest.ACTION_VIEW_REMOTE_HTTP);
        } else if (Intent.ACTION_PICK.equals(action)) {
            final String resolvedType = intent.resolveType(mContext);
            if (Constant.IMAGE_TYPE.equals(resolvedType)) {
                request.setActionCode(BelugaRequest.ACTION_PICK_IMAGE_FILE);
            } else if (Constant.AUDIO_TYPE.equals(resolvedType)) {
                request.setActionCode(BelugaRequest.ACTION_PICK_AUDIO_FILE);
            } else if (Constant.VIDEO_TYPE.equals(resolvedType)) {
                request.setActionCode(BelugaRequest.ACTION_PICK_AUDIO_FILE);
            } else {
                // TODO: do something for other types
            }
        } else if (Intent.ACTION_CREATE_SHORTCUT.equals(action)) {
            request.setActionCode(BelugaRequest.ACTION_CREATE_SHORTCUT_DIRECTORY);
        } else if (Intent.ACTION_GET_CONTENT.equals(action)) {
            final String resolvedType = intent.resolveType(mContext);
            if (Constant.IMAGE_TYPE.equals(resolvedType)) {
                request.setActionCode(BelugaRequest.ACTION_PICK_IMAGE_FILE);
            } else if (Constant.AUDIO_TYPE.equals(resolvedType)) {
                request.setActionCode(BelugaRequest.ACTION_PICK_AUDIO_FILE);
            } else if (Constant.VIDEO_TYPE.equals(resolvedType)) {
                request.setActionCode(BelugaRequest.ACTION_PICK_AUDIO_FILE);
            } else {
                // TODO: do something for other types
            }
        } else if (Intent.ACTION_SEARCH.equals(action)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // If the {@link SearchManager.QUERY} is empty, then check if a phone number
            // or email is specified, in that priority.
            if (TextUtils.isEmpty(query)) {
                query = intent.getStringExtra(Insert.PHONE);
            }
            if (TextUtils.isEmpty(query)) {
                query = intent.getStringExtra(Insert.EMAIL);
            }
            request.setQueryString(query);
            request.setSearchMode(true);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            request.setActionCode(BelugaRequest.ACTION_VIEW_FILE);
//            request.setFileUri(intent.getData());
            intent.setAction(Intent.ACTION_DEFAULT);
            intent.setData(null);
        } else if (Intents.SEARCH_SUGGESTION_CLICKED.equals(action)) {
            Uri data = intent.getData();
            request.setActionCode(BelugaRequest.ACTION_VIEW_FILE);
//            request.setFileUri(data);
            intent.setAction(Intent.ACTION_DEFAULT);
            intent.setData(null);
        }
        return request;
    }
}
