package com.hufeng.playimage;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.browser.ImageEntry;
import com.hufeng.filemanager.data.DataStructures;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.File;

public class ImageScaleAnimationController {

	static final int DURATION = 250;
	private int[] mPositions = new int[2];

	private View mBlackCover;
	private View mRoot;
	private int mStatusBarHeight;
	private AnimatedImageView mAnimatedImageView;

	private boolean mImageAnimated = false;

	private FragmentManager mFragmentManager;
	private ImageGalleryFragment mImageGalleryFragment;
//	private WineVideoPlayerFragment mWineVideoPlayerFragment;
	private ImageEntry mMedia;
	private int mInitX, mInitY, mInitWidth, mInitHeight;


	public ImageScaleAnimationController(FragmentManager fm,
                                         AnimatorViewProvider provider) {
		mBlackCover = provider.getCoverView();
		mRoot = provider.getRootView();
		mAnimatedImageView = provider.getAnimatedImageView();
//		mStatusBarHeight = (int) (mRoot.getContext().getResources()
//				.getDisplayMetrics().density * 25 + 0.5f);
        mStatusBarHeight = 0;
		mBlackCover.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mImageAnimated) {
                    animateImageViewRevert();
				}
			}
		});
		mFragmentManager = fm;
	}

	private void initPosition(ImageView img) {
        img.getLocationOnScreen(mPositions);
        mInitX = mPositions[0];
        mInitY = mPositions[1] - mStatusBarHeight;
        mInitWidth = img.getWidth();
        mInitHeight = img.getHeight();
	}

	public void animateImageView(final ImageView image,
			final ImageEntry media, final int type) {
		animate(image, media, new Runnable() {
            public void run() {
                previewImage(media.path, type);
            }});
	}

	private void animate(final ImageView image,
			final ImageEntry media,
			final Runnable runnable) {

		initPosition(image);
		mMedia = media;

		mImageAnimated = true;
		mAnimatedImageView.setBitmapRate(1.0f * media.getWidth()
				/ media.getHeight());
        Drawable drawable = image.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap thumbnail = ((BitmapDrawable) drawable).getBitmap();
            MemoryCache.getInstance().putBitmap(ImageLoader.THUMBNAIL_PREFIX+media.path, thumbnail);
            mAnimatedImageView.getImage().setImageBitmap(thumbnail);
        } else {
    		mAnimatedImageView.getImage().requestDisplayLocalThumbnail(
                media.path);
        }
//        mAnimatedImageView.getImage().requestDisplayLocalThumbnailOnUIThread(media.path);
		mAnimatedImageView.setImage(mInitX, mInitY, mInitWidth, mInitHeight);
		mAnimatedImageView.setVisibility(View.VISIBLE);
		mAnimatedImageView.startAnimate(false);

		mBlackCover.setVisibility(View.VISIBLE);
		ObjectAnimator coverAlphaAnimator = ObjectAnimator.ofFloat(mBlackCover,
				"alpha", 0.0f, 1.0f).setDuration(DURATION + 20);
		AnimatorSet rootSet = new AnimatorSet();
		ObjectAnimator rootScaleXAnimator = ObjectAnimator.ofFloat(mRoot,
				"scaleX", 1.0f, 0.96f).setDuration(DURATION);
		ObjectAnimator rootScaleYAnimator = ObjectAnimator.ofFloat(mRoot,
				"scaleY", 1.0f, 0.96f).setDuration(DURATION);
		rootSet.playTogether(rootScaleXAnimator, rootScaleYAnimator);
		rootSet.setInterpolator(new DecelerateInterpolator());
		rootSet.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animator) {
			}

			@Override
			public void onAnimationEnd(Animator animator) {
                if (runnable != null) {
				    runnable.run();
                }
			}

			@Override
			public void onAnimationRepeat(Animator animator) {
			}

			@Override
			public void onAnimationStart(Animator animator) {
//				image.setVisibility(View.INVISIBLE);
			}

		});

        mAnimatedImageView.setOnClickListener(mOnImageSignleTapListener);
		coverAlphaAnimator.start();
		rootSet.start();
	}

	private void previewImage(final String path, final int type) {
        Uri uri = DataStructures.ImageColumns.CONTENT_URI;
        if (FileUtils.FILE_TYPE_DIRECTORY == type) {
            uri = Uri.fromFile(new File(path).getParentFile());
        }
		mImageGalleryFragment = ImageGalleryFragment
				.newImageGalleryFragment(uri, path);
		mImageGalleryFragment
				.setOnImageSingleTapListener(mOnImageSignleTapListener);
		mImageGalleryFragment.setLifeCycleListener(mListener);
		mFragmentManager.beginTransaction()
				.add(/*android.R.id.content*/mBlackCover.getId(), mImageGalleryFragment, null)
				.setCustomAnimations(0, 0, 0, 0).addToBackStack("default")
				.commit();
	}

	private View.OnClickListener mOnImageSignleTapListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
            animateImageViewRevert();
		}
	};

//	private void previewVideo(final ArrayList<MediaEntry> media) {
//		mWineVideoPlayerFragment = WineVideoPlayerFragment
//				.newWineVideoPlayerFragment(media.get(0).mContentResourceUrl,
//						media.get(0).mContentThumbnailUrl);
//		mWineVideoPlayerFragment.setLifeCycleListener(mListener);
//		mFragmentManager.beginTransaction()
//				.add(android.R.id.content, mWineVideoPlayerFragment, null)
//				.setCustomAnimations(0, 0, 0, 0).addToBackStack("default")
//				.commit();
//	}

	ImagePreviewFragmentLifeCycleListener mListener = new ImagePreviewFragmentLifeCycleListener() {

		@Override
		public void onPause() {
			mAnimatedImageView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onResume() {
//			mAnimatedImageView.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onPageSelected(int index) {
//			mIndex = index;
		}

        @Override
        public void onImageShown() {
            mAnimatedImageView.setVisibility(View.INVISIBLE);
        }

        @Override
		public void onDestroy() {
//			animateImageViewRevert();
		}

	};

//	public boolean animateRevert() {
//		if (!mImageAnimated) {
//			return false;
//		}
//
//		if (mWineImageGalleryFragment != null
//				&& mWineImageGalleryFragment.isAdded()) {
//			if (mWineImageGalleryFragment.onBackPressed()) {
//				return true;
//			}
//
//			final BasicLazyLoadImageView nextImage = mMultiImageView
//					.getImageAt(mIndex);
//			if (nextImage != mImage) {
//				mImage.setVisibility(View.VISIBLE);
//				nextImage.setVisibility(View.INVISIBLE);
//				mImage = nextImage;
//				final MediaEntry feed = mMedias.get(mIndex);
//				mAnimatedImageView.setBitmapRate(1.0f * feed.getWidth()
//						/ feed.getHeight());
//				mAnimatedImageView.getImage().requestDisplayURL(
//						feed.mContentThumbnailUrl);
//				mAnimatedImageView.setImage(mInitX, mInitY, mInitWidth,
//						mInitHeight);
//			}
//
//			mFragmentManager.popBackStack();
//			return true;
//		}
////        else if (mWineVideoPlayerFragment != null
////				&& mWineVideoPlayerFragment.isAdded()) {
////			mFragmentManager.popBackStack();
////		}
//
//		return true;
//	}

	public boolean animateImageViewRevert() {
        if (mImageAnimated == false) {
            return false;
        }
        mFragmentManager.popBackStack();
		mImageAnimated = false;
		ObjectAnimator rootScaleXAnimator = ObjectAnimator.ofFloat(mRoot,
				"scaleX", 0.96f, 1.0f).setDuration(DURATION);
		ObjectAnimator rootScaleYAnimator = ObjectAnimator.ofFloat(mRoot,
				"scaleY", 0.96f, 1.0f).setDuration(DURATION);
		AnimatorSet rootSet = new AnimatorSet();
		rootSet.playTogether(rootScaleXAnimator, rootScaleYAnimator);

		ObjectAnimator coverAnimator = ObjectAnimator.ofFloat(mBlackCover,
				"alpha", 1.0f, 0.0f).setDuration(DURATION);
		coverAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animator) {
			}

			@Override
			public void onAnimationRepeat(Animator animator) {
			}

			@Override
			public void onAnimationEnd(Animator animator) {
				mBlackCover.clearAnimation();
				mBlackCover.setVisibility(View.GONE);
                mAnimatedImageView.clearAnimation();
                mAnimatedImageView.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(
					Animator animator) {
			}
		});

		coverAnimator.start();
		rootSet.start();
		mAnimatedImageView.startAnimate(true);
        return true;
	}

}
