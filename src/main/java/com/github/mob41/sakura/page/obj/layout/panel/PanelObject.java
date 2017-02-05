package com.github.mob41.sakura.page.obj.layout.panel;

import com.github.mob41.sakura.page.obj.layout.LayoutObject;

public class PanelObject extends LayoutObject {

	public PanelObject(PanelLayout panelLayout) {
		super(null, panelLayout.getAll(), null);
	}
	
	public PanelObject(PanelLayout panelLayout, String layout) {
		super(null, panelLayout.getAll(), null, layout);
	}

	@Override
	public void setLayoutClasses(String layout) {
		putAttribute("class", "panel panel-" + layout);
	}

}
