// IWebService.aidl
package com.belugamobile.filemanager.services;

// Declare any non-default types here with import statements

interface IWebService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void startServer();
     void stopServer();
     boolean isRunning();
}
