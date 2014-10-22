package com.hufeng.filemanager;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by feng on 14-1-24.
 */
public class FileSearchUtil {

    public static SpannableStringBuilder highlightSearchText(Context context, String text, String search) {
        if (!TextUtils.isEmpty(search)) {
            if (!TextUtils.isEmpty(text)) {
                Pattern pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(text);
                if (matcher != null) {
                    SpannableStringBuilder buf = new SpannableStringBuilder();
                    buf.append(text);
                    while (matcher.find()) {
                        int j = matcher.start();
                        int k = matcher.end();
                        buf.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.highlight_color)), j, k, 0);
                    }
                    return buf;
                }
            }
        }
        return null;
    }
}
