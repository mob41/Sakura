package com.github.mob41.sakura.page.obj.layout;

import com.github.mob41.sakura.page.obj.DivObject;
import com.github.mob41.sakura.page.obj.PageObject;

public abstract class LayoutObject extends DivObject {
	
	public LayoutObject(String beforeObjectBody, PageObject[] objs, String afterObjectBody){
		this(beforeObjectBody, objs, afterObjectBody, "default");
	}
	
	public LayoutObject(String beforeObjectBody, PageObject[] objs, String afterObjectBody, String layout) {
		super(beforeObjectBody, objs, afterObjectBody);
		setLayoutClasses(layout);
	}
	
	public void setLayoutClasses(){
		setLayoutClasses("default");
	}
	
	public abstract void setLayoutClasses(String layout);

}
