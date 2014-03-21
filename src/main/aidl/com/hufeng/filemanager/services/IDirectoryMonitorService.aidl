package com.hufeng.filemanager.services;

interface IDirectoryMonitorService {
    void addMonitor(String path);
    boolean isMonitoring(String path);
    void removeMonitor(String path);
    void clearMonitor();
}