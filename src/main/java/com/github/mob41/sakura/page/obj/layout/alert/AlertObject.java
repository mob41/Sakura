package com.github.mob41.sakura.page.obj.layout.alert;

import com.github.mob41.sakura.page.obj.layout.LayoutObject;

public class AlertObject extends LayoutObject {

	public AlertObject(String body){
		super(body, null, null);
	}
	
	public AlertObject(String body, String layout){
		super(body, null, null, layout);
	}

	@Override
	public void setLayoutClasses(String layout) {
		putAttribute("class", "alert alert-" + layout);
	}

}
