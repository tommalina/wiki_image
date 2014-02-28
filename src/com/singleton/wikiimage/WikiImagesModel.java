package com.singleton.wikiimage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

/**
 * This class is used to fetch data from server.
 * @author Tomasz Malinowski
 */
public class WikiImagesModel {
	
	private final static String JSON_VARIABLE_QUERY = "query";
	private final static String JSON_VARIABLE_PAGES = "pages";
	private final static String JSON_VARIABLE_TITLE = "title";
	private final static String JSON_VARIABLE_THUMBNAIL = "thumbnail";
	private final static String JSON_VARIABLE_SOURCE = "source";				
	
	/**
	 * Base data type used to store information about image.
	 * @author Tomasz Malinowski
	 */
	public class WikiImage {
		
		private String mTitle = "";
		
		private String mUrl = "";
		
		public WikiImage(String title, String url) {
			mTitle = title;
			mUrl = url;
		}
		
		public String getTitle() {
			return mTitle;
		}
		
		public String getUrl() {
			return mUrl;
		}
	}
	
	/**
	 * Interface used to observe model. Only via this interface we can get
	 * data from model.
	 * @author Tomasz Malinowski
	 */
	public interface WikiImagesModelObserver {
		public void onStartFetchingImages();
		public void onErrorOccurred();
		public void onEndFetchingImages(Vector<WikiImage> wikiImages);
	}
	
	private class MessageStartFetchingImages implements Runnable {

		@Override
		public void run() {
			try {
				synchronized(mObservers) {
					Iterator<WikiImagesModelObserver> iterator = mObservers.iterator();
					while (iterator.hasNext()) {
						iterator.next().onStartFetchingImages();			
					}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				sendMessageToObservers(new MessageErrorOccurred());
			}
		}
	}
	
	private class MessageErrorOccurred implements Runnable {

		@Override
		public void run() {
			try {
				synchronized(mObservers) {
					Iterator<WikiImagesModelObserver> iterator = mObservers.iterator();
					while (iterator.hasNext()) {
						iterator.next().onErrorOccurred();			
					}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				sendMessageToObservers(new MessageErrorOccurred());
			}
		}
	}
	
	private class MessageEndFetchingImages implements Runnable {
		
		Vector<WikiImage> mWikiImages;
		
		public MessageEndFetchingImages(Vector<WikiImage> wikiImages) {
			mWikiImages = wikiImages;
		}
		
		@Override
		public void run() {
			try {
				synchronized(mObservers) {
					Iterator<WikiImagesModelObserver> iterator = mObservers.iterator();
					while (iterator.hasNext()) {
						iterator.next().onEndFetchingImages(mWikiImages);			
					}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				sendMessageToObservers(new MessageErrorOccurred());
			}
		}
		
	}
	
	private final static int IMAGES_AMOUNT_LIMIT = 50;
	
	private static WikiImagesModel instance = null;
		
	private List<WikiImagesModelObserver> mObservers;
	
	private WikiImagesModel() {
		mObservers = Collections.synchronizedList(new ArrayList<WikiImagesModelObserver>());
	}
	
	/**
	 * This class is singleton, so we use this method to get only one
	 * created instance of this class.
	 * @return Returns WikiImagesModel.
	 */
	public static WikiImagesModel getInstance() {
		if (instance == null)
			instance = new WikiImagesModel();

		return instance;
	}
	
	/**
	 * Add observer
	 * @param observer Instance of WikiImagesModelObserver.
	 */
	public void addObserver(WikiImagesModelObserver observer) {
		mObservers.add(observer);
	}
	
	/**
	 * Remove observer
	 * @param observer Instance of WikiImagesModelObserver.
	 */
	public void removeObserver(WikiImagesModelObserver observer) {
		mObservers.remove(observer);
	}

	/**
	 * Private method used to get JSON from server.
	 * @param url Url to server.
	 * @return JSONObject.
	 */
	private JSONObject getJSONFromUrl(String url) {
		InputStream inputStream = null;
		String jsonString = "";
		JSONObject jsonObject = null;
		boolean sendMessageError = false;
		
		try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            sendMessageError = true;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            sendMessageError = true;
        } catch (IOException e) {
            e.printStackTrace();
            sendMessageError = true;
        }
		
		try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
            		inputStream, "iso-8859-1"), 8);
            StringBuilder stringbuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
            	stringbuilder.append(line + "\n");
            }
            jsonString = stringbuilder.toString();
        } catch (Exception e) {
        	e.printStackTrace();
        	sendMessageError = true;
        }
		
		try {
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			sendMessageError = true;
		}

		try {
			jsonObject = new JSONObject(jsonString);
        } catch (Exception e) {
        	e.printStackTrace();
        	sendMessageError = true;
        }
		
		if (sendMessageError)
			sendMessageToObservers(new MessageErrorOccurred());
		
		return jsonObject;
	}
	
	/**
	 * Private method used to get informations about image from JSON.
	 * @param jsonObject JSONObject.
	 * @return Vector with information about images.
	 */
	private Vector<WikiImage> getImagesFromJSON(JSONObject jsonObject) {
		Vector<WikiImage> images = new Vector<WikiImage>();
		try {
			JSONObject query = jsonObject.getJSONObject(JSON_VARIABLE_QUERY);
			if (query != null) {
				JSONObject pages = query.getJSONObject(JSON_VARIABLE_PAGES);
				if (pages != null) {
					Iterator<String> i = pages.keys();
					while(i.hasNext()) {
						JSONObject page = pages.getJSONObject(i.next());
						String title = page.getString(JSON_VARIABLE_TITLE);
						String imageUrl = "";
						if (page.has(JSON_VARIABLE_THUMBNAIL)) {
							JSONObject thumbnail = page.getJSONObject(JSON_VARIABLE_THUMBNAIL);
							if (thumbnail != null) {
								imageUrl = thumbnail.getString(JSON_VARIABLE_SOURCE);
							}
						}
						images.add(new WikiImage(title, imageUrl));
					}
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			sendMessageToObservers(new MessageErrorOccurred());
		}
		
		return images;
	}
	
	/**
	 * Method used to send asynchronous message to UI thread.
	 * @param message Runnable class.
	 */
	private void sendMessageToObservers(Runnable message) {
		new Handler(Looper.getMainLooper()).post(message);
	}
	
	/**
	 * Main method of this class, used to fetch images from server.
	 * @param searchTerm Search terms.
	 * @param imageSize Max width or height of one image.
	 */
	public void fetchWikiImages(String searchTerm, int imageSize) {
		Vector<WikiImage> images;
		if (searchTerm.isEmpty()) {
			images = new Vector<WikiImagesModel.WikiImage>();
			sendMessageToObservers(new MessageEndFetchingImages(images));
			return;
		} 
		sendMessageToObservers(new MessageStartFetchingImages());
		String url = UrlFactory.CreateUrlSearchImages(Uri.encode(searchTerm), imageSize, IMAGES_AMOUNT_LIMIT);
		JSONObject jsonObject = getJSONFromUrl(url);
		if (jsonObject != null) {
			images = getImagesFromJSON(jsonObject);
		} else {
			images = new Vector<WikiImagesModel.WikiImage>();
		}
		sendMessageToObservers(new MessageEndFetchingImages(images));
	}
}
