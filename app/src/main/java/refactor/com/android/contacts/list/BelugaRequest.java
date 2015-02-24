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

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parsed form of the intent sent to the Contacts application.
 */
public class BelugaRequest implements Parcelable {

    /** Default mode: browse files */
    public static final int ACTION_DEFAULT = 10;

    public static final int ACTION_VIEW_FILE = 15;

    public static final int ACTION_PICK_DIRECTORY_TO_COPY_FILE = 20;
    public static final int ACTION_PICK_DIRECTORY_TO_MOVE_FILE = 21;
    public static final int ACTION_PICK_KANBOX_DIRECTORY_TO_UPLOAD_FILE = 22;
    public static final int ACTION_PICK_DIRECTORY_CANCEL = 23;

    public static final int ACTION_PICK_FILE_OR_DIRECTORY = 31;
    public static final int ACTION_PICK_DIRECTORY_ONLY = 32;
    public static final int ACTION_PICK_FILE_ONLY = 33;
    public static final int ACTION_PICK_IMAGE_FILE = 34;
    public static final int ACTION_PICK_AUDIO_FILE = 35;
    public static final int ACTION_PICK_VIDEO_FILE = 36;
    public static final int ACTION_PICK_APK_FILE = 37;
    public static final int ACTION_PICK_ZIP_FILE = 38;
    public static final int ACTION_PICK_DOCUMENT_FILE = 39;

    public static final int ACTION_CREATE_SHORTCUT_DIRECTORY = 80;

    public static final int ACTION_VIEW_REMOTE_FTP = 100;
    public static final int ACTION_VIEW_REMOTE_HTTP = 101;


    private boolean mValid = true;
    private int mActionCode = ACTION_DEFAULT;
//    private Intent mRedirectIntent;
//    private CharSequence mTitle;
    private boolean mSearchMode;
    private String mQueryString;
//    private boolean mIncludeProfile;
//    private boolean mLegacyCompatibilityMode;
//    private boolean mDirectorySearchEnabled = true;
//    private Uri mFileUri;
    private String[] mOperationFiles;

    @Override
    public String toString() {
        return "{BelugaRequest:mValid=" + mValid
                + " mActionCode=" + mActionCode
//                + " mRedirectIntent=" + mRedirectIntent
//                + " mTitle=" + mTitle
                + " mSearchMode=" + mSearchMode
                + " mQueryString=" + mQueryString
//                + " mIncludeProfile=" + mIncludeProfile
//                + " mLegacyCompatibilityMode=" + mLegacyCompatibilityMode
//                + " mDirectorySearchEnabled=" + mDirectorySearchEnabled
//                + " mContactUri=" + mContactUri
                + "}";
    }

    /**
     * Copies all fields.
     */
    public void copyFrom(BelugaRequest request) {
        mValid = request.mValid;
        mActionCode = request.mActionCode;
//        mRedirectIntent = request.mRedirectIntent;
//        mTitle = request.mTitle;
        mSearchMode = request.mSearchMode;
        mQueryString = request.mQueryString;
        mOperationFiles = request.mOperationFiles;
//        mIncludeProfile = request.mIncludeProfile;
//        mLegacyCompatibilityMode = request.mLegacyCompatibilityMode;
//        mDirectorySearchEnabled = request.mDirectorySearchEnabled;
//        mContactUri = request.mContactUri;
    }

    public static Parcelable.Creator<BelugaRequest> CREATOR = new Creator<BelugaRequest>() {

        public BelugaRequest[] newArray(int size) {
            return new BelugaRequest[size];
        }

        public BelugaRequest createFromParcel(Parcel source) {
            ClassLoader classLoader = this.getClass().getClassLoader();
            BelugaRequest request = new BelugaRequest();
            request.mValid = source.readInt() != 0;
            request.mActionCode = source.readInt();
//            request.mRedirectIntent = source.readParcelable(classLoader);
//            request.mTitle = source.readCharSequence();
            request.mSearchMode = source.readInt() != 0;
            request.mQueryString = source.readString();
            request.mOperationFiles = source.createStringArray();
//            request.mIncludeProfile = source.readInt() != 0;
//            request.mLegacyCompatibilityMode  = source.readInt() != 0;
//            request.mDirectorySearchEnabled = source.readInt() != 0;
//            request.mContactUri = source.readParcelable(classLoader);
            return request;
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mValid ? 1 : 0);
        dest.writeInt(mActionCode);
//        dest.writeParcelable(mRedirectIntent, 0);
//        dest.writeCharSequence(mTitle);
        dest.writeInt(mSearchMode ? 1 : 0);
        dest.writeString(mQueryString);
        dest.writeStringArray(mOperationFiles);
//        dest.writeInt(mIncludeProfile ? 1 : 0);
//        dest.writeInt(mLegacyCompatibilityMode ? 1 : 0);
//        dest.writeInt(mDirectorySearchEnabled ? 1 : 0);
//        dest.writeParcelable(mContactUri, 0);
    }

    public int describeContents() {
        return 0;
    }

    public boolean isValid() {
        return mValid;
    }

    public void setValid(boolean flag) {
        mValid = flag;
    }

//    public Intent getRedirectIntent() {
//        return mRedirectIntent;
//    }
//
//    public void setRedirectIntent(Intent intent) {
//        mRedirectIntent = intent;
//    }
//
//    public void setActivityTitle(CharSequence title) {
//        mTitle = title;
//    }
//
//    public CharSequence getActivityTitle() {
//        return mTitle;
//    }

    public int getActionCode() {
        return mActionCode;
    }

    public void setActionCode(int actionCode) {
        mActionCode = actionCode;
    }

    public boolean isSearchMode() {
        return mSearchMode;
    }

    public void setSearchMode(boolean flag) {
        mSearchMode = flag;
    }

    public String getQueryString() {
        return mQueryString;
    }

    public void setQueryString(String string) {
        mQueryString = string;
    }

//    public boolean shouldIncludeProfile() {
//        return mIncludeProfile;
//    }
//
//    public void setIncludeProfile(boolean includeProfile) {
//        mIncludeProfile = includeProfile;
//    }
//
//    public boolean isLegacyCompatibilityMode() {
//        return mLegacyCompatibilityMode;
//    }
//
//    public void setLegacyCompatibilityMode(boolean flag) {
//        mLegacyCompatibilityMode = flag;
//    }

    /**
     * Determines whether this search request should include directories or
     * is limited to local contacts only.
     */
//    public boolean isDirectorySearchEnabled() {
//        return mDirectorySearchEnabled;
//    }
//
//    public void setDirectorySearchEnabled(boolean flag) {
//        mDirectorySearchEnabled = flag;
//    }
//
    public String[] getOperationFiles() {
        return mOperationFiles;
    }

    public void setOperationFiles(String[] operationFiles) {
        this.mOperationFiles = operationFiles;
    }
}

