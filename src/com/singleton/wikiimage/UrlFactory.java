package com.singleton.wikiimage;

public class UrlFactory {
	
	private final static String URL_DOT = ".";
	private final static String URL_QM = "?";
	private final static String URL_EQUALS = "=";
	private final static String URL_AND = "&";
	private final static String URL_PROTOCOL = "https://";
	private final static String URL_LOCALIZE = "en";
	private final static String URL_API = "wikipedia.org/w/api.php";
	private final static String URL_VARIABLE_ACTION = "action";
	private final static String URL_VARIABLE_ACTION_QUERY = "query";
	private final static String URL_VARIABLE_PROP = "prop";
	private final static String URL_VARIABLE_PROP_PAGEIMAGES = "pageimages";
	private final static String URL_VARIABLE_FORMAT = "format";
	private final static String URL_VARIABLE_FORMAT_JSON = "json";
	private final static String URL_VARIABLE_PIPROP = "piprop";
	private final static String URL_VARIABLE_PIPROP_THUMBNAIL = "thumbnail";
	private final static String URL_VARIABLE_PILIMIT = "pilimit";
	private final static String URL_VARIABLE_GENERATOR = "generator";
	private final static String URL_VARIABLE_GENERATOR_ALLPAGES = "allpages";
	private final static String URL_VARIABLE_GAPPREFIX = "gapprefix";
	private final static String URL_VARIABLE_GAPLIMIT = "gaplimit";
	private final static String URL_VARIABLE_PITHUMBNAILSIZE = "pithumbsize";
	
	private UrlFactory() {}
	
	/**
	 * Static method used to compose url string.
	 * @param searchTerm String with search terms.
	 * @param imageSize Max width or height of one image. 
	 * @param imagesAmountLimit Describes how many images we can download.
	 * @return Returns url string.
	 */
	public static String CreateUrlSearchImages(String searchTerm, int imageSize, int imagesAmountLimit) {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(URL_PROTOCOL);
		urlBuilder.append(URL_LOCALIZE);
		urlBuilder.append(URL_DOT);
		urlBuilder.append(URL_API);
		urlBuilder.append(URL_QM);
		urlBuilder.append(URL_VARIABLE_ACTION);
		urlBuilder.append(URL_EQUALS);
		urlBuilder.append(URL_VARIABLE_ACTION_QUERY);
		urlBuilder.append(URL_AND);
		urlBuilder.append(URL_VARIABLE_PROP);
		urlBuilder.append(URL_EQUALS);
		urlBuilder.append(URL_VARIABLE_PROP_PAGEIMAGES);
		urlBuilder.append(URL_AND);
		urlBuilder.append(URL_VARIABLE_FORMAT);
		urlBuilder.append(URL_EQUALS);
		urlBuilder.append(URL_VARIABLE_FORMAT_JSON);
		urlBuilder.append(URL_AND);
		urlBuilder.append(URL_VARIABLE_PIPROP);
		urlBuilder.append(URL_EQUALS);
		urlBuilder.append(URL_VARIABLE_PIPROP_THUMBNAIL);
		urlBuilder.append(URL_AND);
		urlBuilder.append(URL_VARIABLE_PILIMIT);
		urlBuilder.append(URL_EQUALS);
		urlBuilder.append(imagesAmountLimit);
		urlBuilder.append(URL_AND);
		urlBuilder.append(URL_VARIABLE_GENERATOR);
		urlBuilder.append(URL_EQUALS);
		urlBuilder.append(URL_VARIABLE_GENERATOR_ALLPAGES);
		urlBuilder.append(URL_AND);
		urlBuilder.append(URL_VARIABLE_GAPPREFIX);
		urlBuilder.append(URL_EQUALS);
		urlBuilder.append(searchTerm);
		urlBuilder.append(URL_AND);
		urlBuilder.append(URL_VARIABLE_GAPLIMIT);
		urlBuilder.append(URL_EQUALS);
		urlBuilder.append(imagesAmountLimit);
		urlBuilder.append(URL_AND);
		urlBuilder.append(URL_VARIABLE_PITHUMBNAILSIZE);
		urlBuilder.append(URL_EQUALS);
		urlBuilder.append(imageSize);
						
		return urlBuilder.toString();
	}
}
