package com.singleton.wikiimage;

import java.util.Vector;

import com.singleton.wikiimage.WikiImagesModel.WikiImage;
import com.singleton.wikiimage.WikiImagesModel.WikiImagesModelObserver;
import com.squareup.picasso.Picasso;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Class used to display images. It observes model, and display images when receive them.
 * @author Tomasz Malinowski
 */
public class WikiImagesView extends ScrollView implements WikiImagesModelObserver, OnClickListener {

	private final static int TOP_IMAGE_MARGIN = 5;
	private final static int BOTTOM_IMAGE_MARGIN = 5;
	
	private MainView mMainView = null;
	
	/**
	 * Constructor of WikiImagesView.
	 * @param context Context.
	 * @param mainView MainView.
	 */
	public WikiImagesView(Context context, MainView mainView) {
		super(context);
		mMainView = mainView;
		WikiImagesModel.getInstance().addObserver(this);
	}
	
	@Override
	protected void finalize() {
		WikiImagesModel.getInstance().removeObserver(this);
	}

	@Override
	public void onEndFetchingImages(Vector<WikiImage> wikiImages) {
		removeAllViews();
		
		TableLayout table = new TableLayout(getContext());
		table.setColumnStretchable(0, true);
		TableRow row = null;
		TableRow.LayoutParams rowLayoutParams =
				new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT,
						TableRow.LayoutParams.WRAP_CONTENT);
		rowLayoutParams.setMargins(0, TOP_IMAGE_MARGIN, 0, BOTTOM_IMAGE_MARGIN);
		for (int i = 0, size = wikiImages.size(); i < size; ++i) {
			WikiImage wikiImage = wikiImages.elementAt(i);
			if (!wikiImage.getUrl().isEmpty()) {
				row = new TableRow(getContext());
				ImageView image = new ImageView(getContext());
				image.setOnClickListener(this);
				Picasso.with(getContext()).load(wikiImage.getUrl()).into(image);
				row.addView(image, rowLayoutParams);
				table.addView(row);
			}
		}
		addView(table);
	}

	@Override
	public void onStartFetchingImages() {
	}

	@Override
	public void onErrorOccurred() {
		Toast.makeText(getContext(), R.string.error_dialog_message, 300).show();
	}

	@Override
	public void onClick(View v) {
		mMainView.showImage((ImageView)v);
	}

}
