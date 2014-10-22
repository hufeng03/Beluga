package com.hufeng.filemanager.services;

interface IUi {
    void scanStarted();
    void scanCompleted();
    void changeMonitored(String dir);
    void storageChanged();
}