package com.hufeng.filemanager.services;

interface IFileSyncService {
    void startScan();
    boolean isScanning();
}