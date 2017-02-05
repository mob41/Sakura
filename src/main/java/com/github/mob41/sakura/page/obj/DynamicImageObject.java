package com.github.mob41.sakura.page.obj;

import com.github.mob41.sakura.image.Image;

public class DynamicImageObject extends PageObject {
	
	public static final int COLOR_DEFAULT = 0; //No class
	
	public static final int COLOR_LIGHT_BLUE = 1;
	
	public static final int COLOR_GREEN = 2;
	
	public static final int COLOR_BLUE = 3;
	
	public static final int COLOR_YELLOW = 4;
	
	public static final int COLOR_RED = 5;
	
	public static final int COLOR_GREY = 6;
	
	private final Image image;
	
	private final int color;
	
	public DynamicImageObject(Image image){
		this(image, COLOR_DEFAULT);
	}
	
	public DynamicImageObject(Image image, int color) {
		super("DynamicImageObject", image.getImageType() == Image.IMAGE_FONT_AWESOME_ICON ? PageObject.NEW_TAG_TO_END : PageObject.SAME_TAG_SLASH_END, false);
		this.image = image;
		this.color = color;
		
		String colorstr = "";
		if (color != COLOR_DEFAULT){
			switch(color){
			case COLOR_LIGHT_BLUE:
				colorstr = "text-primary";
				break;
			case COLOR_GREEN:
				colorstr = "text-success";
				break;
			case COLOR_BLUE:
				colorstr = "text-info";
				break;
			case COLOR_YELLOW:
				colorstr = "text-warning";
				break;
			case COLOR_RED:
				colorstr = "text-danger";
				break;
			case COLOR_GREY:
				colorstr = "text-muted";
				break;
			}
		}
		
		if (image.getImageType() == Image.IMAGE_FONT_AWESOME_ICON){
			putAttribute("class", "fa fa-" + image.getFontAwesomeString() + (image.getFontAwesomeSize() != null ? " fa-" + image.getFontAwesomeSize() : "") + (color != COLOR_DEFAULT ? " " + colorstr : ""));
		} else {
			putAttribute("src", image.getURI());
		}
	}

	@Override
	public String getTagName() {
		return image.getImageType() == Image.IMAGE_FONT_AWESOME_ICON ? "i" : "img";
	}

	@Override
	public String getBody(int tabLevel) {
		return "";
	}

	@Override
	public void init() {
	}

}
