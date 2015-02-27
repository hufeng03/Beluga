package com.hufeng.filemanager.services;

interface IFileSyncService {
    void forceScan();
    boolean isScanning();
}