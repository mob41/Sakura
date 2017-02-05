package com.github.mob41.sakura.page.obj.layout.panel;

import com.github.mob41.sakura.page.obj.PageObject;

public class PanelFooter extends PanelLayoutObject {

	public PanelFooter(String text) {
		super(null, null, null);
		setBeforeObjectText(text);
	}
	
	public PanelFooter(PageObject[] objs) {
		super(null, objs, null);
	}

	@Override
	public void setLayoutClasses(String layout) {
		putAttribute("class", "panel-footer");
	}

}
