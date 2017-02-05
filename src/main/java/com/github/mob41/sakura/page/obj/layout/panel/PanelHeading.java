package com.github.mob41.sakura.page.obj.layout.panel;

import com.github.mob41.sakura.page.obj.PageObject;

public class PanelHeading extends PanelLayoutObject {

	public PanelHeading(String text) {
		super(null, null, null);
		setBeforeObjectText(text);
	}
	
	public PanelHeading(PageObject[] objs) {
		super(null, objs, null);
	}

	@Override
	public void setLayoutClasses(String layout) {
		putAttribute("class", "panel-heading");
	}

}
