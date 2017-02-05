package com.github.mob41.sakura.page.obj.layout;

import com.github.mob41.sakura.page.obj.DivObject;
import com.github.mob41.sakura.page.obj.PageObject;

public class ColumnObject extends DivObject {
	
	public static final int MAXIMUM_WIDTH = 12;
	
	private final int width;

	private final String size;
	
	public ColumnObject(int width){
		this(width, "lg");
	}
	
	public ColumnObject(int width, String size){
		this(width, size, null);
	}
	
	public ColumnObject(int width, String size, PageObject[] objs) {
		super(null, objs, null);
		this.width = width;
		this.size = size;
		this.putAttribute("class", "col-" + size + "-" + width);
	}
	
	public int getWidth(){
		return width;
	}
	
	public String getSize(){
		return size;
	}
	
	public final boolean isMaximumWidth(){
		return width == MAXIMUM_WIDTH;
	}
	
	public final boolean isValidWidth(){
		return width != 0 && width <= MAXIMUM_WIDTH;
	}

}
