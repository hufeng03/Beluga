package com.belugamobile.filemanager.services;

interface IFileSyncService {
    void forceScan();
    boolean isScanning();
}