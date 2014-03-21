package com.hufeng.filemanager;

/**
 * Created by feng on 14-1-22.
 */
public enum ResourceType {
    APP, GAME, DOC;

    public static ResourceType valueOf(int ordinal) {
        int len = ResourceType.values().length;
        if (ordinal >= 0 && ordinal < len) {
            return ResourceType.values()[ordinal];
        } else {
            return null;
        }
    }
}
