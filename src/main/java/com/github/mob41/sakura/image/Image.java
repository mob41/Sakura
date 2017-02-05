package com.github.mob41.sakura.image;

import com.github.mob41.sakura.exception.InvalidImageException;

public class Image {

	public static final int IMAGE_FONT_AWESOME_ICON = 0;
	
	public static final int IMAGE_URL_IMAGE = 1;
	
	private final int image_type;
	
	private final String url;
	
	private final String fa_str;
	
	private final String fa_size;
	
	public Image(String fa_icon_str, String fa_icon_size){
		this(IMAGE_FONT_AWESOME_ICON, fa_icon_str, fa_icon_size);
	}
	
	public Image(String uri){
		this(IMAGE_URL_IMAGE, uri, null);
	}
	
	public Image(int image_type, String arg0, String arg1) throws InvalidImageException{
		this.image_type = image_type;
		if (image_type == IMAGE_FONT_AWESOME_ICON){
			url = null;
			fa_str = arg0;
			fa_size = arg1;
		} else if (image_type == IMAGE_URL_IMAGE) {
			url = arg0;
			fa_str = null;
			fa_size = null;
		} else {
			throw new InvalidImageException("The image type \"" + image_type + "\" is invalid. Are you using a field?");
		}
	}
	
	public int getImageType(){
		return image_type;
	}
	
	public String getURI(){
		if (image_type != IMAGE_URL_IMAGE){
			throw new InvalidImageException("Getting a URL is not appliable to the image type \"" + image_type + "\".");
		}
		return url;
	}
	
	public String getFontAwesomeString(){
		if (image_type != IMAGE_FONT_AWESOME_ICON){
			throw new InvalidImageException("Getting a font awesome string is not appliable to the image type \"" + image_type + "\".");
		}
		return fa_str;
	}
	
	public String getFontAwesomeSize(){
		if (image_type != IMAGE_FONT_AWESOME_ICON){
			throw new InvalidImageException("Getting a font awesome icon size is not appliable to the image type \"" + image_type + "\".");
		}
		return fa_size;
	}

}
