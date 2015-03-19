package com.belugamobile.filemanager.utils;

/**
 * Created by Feng Hu on 15-01-26.
 * <p/>
 * TODO: Add a class header comment.
 */
public class SizeUtil {

    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = M * K;

    public static String normalize(long length) {
        if (length > G)
            return String.format("%.2fG", length / (double)G);
        else if (length > M)
            return String.format("%.2fM", length / (double)M);
        else if (length > K)
            return String.format("%.2fK", length / (double)K);
        else
            return String.valueOf(length + "B");
    }


}
