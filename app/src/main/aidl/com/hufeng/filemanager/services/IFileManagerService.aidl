package com.hufeng.filemanager.services;

interface IFileManagerService {
    IBinder getService();
    IBinder getFileSyncService();
    IBinder getFolderMonitorService();
}