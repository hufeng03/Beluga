package com.belugamobile.filemanager.helper;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * Created by Feng Hu on 15-03-08.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaHighlightHelper {

    public static void setTextWithHighlight(TextView view, String contentString, String highlightString) {
        int idx = -1;
        if (!TextUtils.isEmpty(contentString) && !TextUtils.isEmpty(highlightString)) {
            idx = contentString.toLowerCase().indexOf(highlightString.toLowerCase());
        }
        if (idx < 0) {
            view.setText(contentString);
        } else {
            Spannable spannable = new SpannableString(contentString);
            spannable.setSpan(new ForegroundColorSpan(Color.BLUE), idx, idx + highlightString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            view.setText(spannable);
        }
    }
}
