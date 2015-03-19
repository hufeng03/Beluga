package com.belugamobile.filemanager.services;

interface IFolderMonitorService {
    void addMonitor(String path);
    boolean isMonitoring(String path);
    void removeMonitor(String path);
    void clearMonitor();
}