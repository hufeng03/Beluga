package com.hufeng.playimage;

public interface ImagePreviewFragmentLifeCycleListener {
    void onDestroy();

	void onPause();

	void onResume();

	void onPageSelected(int index);

    void onImageShown();
}