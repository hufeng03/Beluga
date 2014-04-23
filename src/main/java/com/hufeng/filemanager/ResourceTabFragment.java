package com.hufeng.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class ResourceTabFragment extends FileTabFragment{

    ResourceFragment mCurrentChildFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.selected_tab_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        showSelectedGame();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public void showSelectedGame() {
        ResourceFragment fragment = new ResourceFragment();
        //mRootView.removeAllViews();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        mCurrentChildFragment = fragment;
    }

    @Override
    protected void showFile(String path) {

    }

    @Override
    protected void closeFile(String path) {

    }

    @Override
    public String[] getAllFiles() {
        return new String[0];
    }

    @Override
    public String getParentFile() {
        return null;
    }

    @Override
    public void refreshFiles() {

    }

//    @Override
//    public void onDialogDone(DialogInterface paramDialogInterface, int paramInt1, int paramInt2, Object param) {
//
//    }


    @Override
    public void onFileBrowserDirShown(String path) {

    }
}
