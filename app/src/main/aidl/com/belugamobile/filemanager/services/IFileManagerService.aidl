package com.belugamobile.filemanager.services;

interface IFileManagerService {
    IBinder getService();
    IBinder getFileSyncService();
    IBinder getFolderMonitorService();
    IBinder getWebService();
}