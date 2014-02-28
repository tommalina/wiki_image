package com.singleton.wikiimage;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

/**
 * MainView is class which displays all content and components of
 * application 
 * @author Tomasz Malinowski
 */
public class MainView extends RelativeLayout implements TextWatcher {
	
	private final static int TIME_TO_TRIGER_SEARCH = 1000;
	
	private EditText mSearchEditText = null;
	
	private WikiImagesView mWikiImagesView = null;
	
	/**
	 * Timer used to trigger search.
	 */
	private Timer mTimer = null;
	
	/**
	 * RelativeLayout used to display and animate focus state
	 * of image.
	 */
	private RelativeLayout mAnimationLayout = null;
	
	/**
	 * Class used to trigger search.
	 * @author Tomasz Malinowski
	 */
	private class SearchTimerTask extends TimerTask {
		
		private String mSearchTerm = "";
		
		private int mImageSize = 0;
		
		public SearchTimerTask(String searchTerm, int imageSize) {
			mSearchTerm = searchTerm;
			mImageSize = imageSize;
		}
		
		@Override
		public void run() {
			WikiImagesModel.getInstance().fetchWikiImages(mSearchTerm, mImageSize);
			mTimer.cancel();
		}
		
	}
	
	public MainView(Context context) {
		super(context);
		mTimer = null;
		init();
	}
	
	private void init() {
		TableLayout table = new TableLayout(getContext());
		table.setStretchAllColumns(true);
		
		TableLayout.LayoutParams tableParams =
				new TableLayout.LayoutParams(
						TableLayout.LayoutParams.MATCH_PARENT,
						TableLayout.LayoutParams.MATCH_PARENT);
		table.setLayoutParams(tableParams);
		
		// Create search edit text.
		mSearchEditText = new EditText(getContext());
		mSearchEditText.setHint(R.string.edit_text_hint);
		mSearchEditText.setInputType(InputType.TYPE_CLASS_TEXT);
		mSearchEditText.addTextChangedListener(this);
		table.addView(mSearchEditText);
		
		// Create scroll view which will be used to display all images.
		mWikiImagesView = new WikiImagesView(getContext(), this);
		table.addView(mWikiImagesView);
		addView(table);
	}
	
	@Override
	public void afterTextChanged(Editable arg0) {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
		}
		mTimer = new Timer();
		
		int imageSize = mWikiImagesView.getWidth();
		imageSize -= imageSize >> 2;
		
		mTimer.schedule(new SearchTimerTask(arg0.toString(), imageSize), TIME_TO_TRIGER_SEARCH);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}
	
	private final static int IMAGE_ANIMATION_DURATION = 300;
	
	private class AnimateImageController implements OnClickListener, AnimationListener {
		
		@Override
		public void onClick(View v) {
			AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
			animation.setDuration(IMAGE_ANIMATION_DURATION);
			animation.setAnimationListener(this);
			if (mAnimationLayout.getAnimation() != null)
				mAnimationLayout.getAnimation().cancel();
			mAnimationLayout.startAnimation(animation);
		}

		@Override
		public void onAnimationEnd(Animation arg0) {
			removeView(mAnimationLayout);
			mAnimationLayout = null;
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
		}

		@Override
		public void onAnimationStart(Animation arg0) {
		}
		
	}
	
	public void showImage(ImageView image) {
		if (mAnimationLayout != null)
			return;

		// Create animation layout.
		mAnimationLayout = new RelativeLayout(getContext());
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mAnimationLayout.setLayoutParams(params);
		Drawable background = new ShapeDrawable();
		mAnimationLayout.setBackgroundDrawable(background);
		
		ImageView ImageView = new ImageView(getContext());
		ImageView.setLayoutParams(params);
		ImageView.setOnClickListener(new AnimateImageController());
		ImageView.setImageDrawable(image.getDrawable());
		mAnimationLayout.addView(ImageView);
		addView(mAnimationLayout);
		
		// Set and start animation.
		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(IMAGE_ANIMATION_DURATION);
		mAnimationLayout.startAnimation(animation);
	}
}
