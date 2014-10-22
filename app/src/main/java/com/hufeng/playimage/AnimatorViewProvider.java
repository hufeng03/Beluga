package com.hufeng.playimage;

import android.view.View;

public interface AnimatorViewProvider {

    AnimatedImageView getAnimatedImageView();

    View getRootView();

    View getCoverView();

}
