package com.github.mob41.sakura.page.obj.layout.panel;

import com.github.mob41.sakura.page.obj.PageObject;
import com.github.mob41.sakura.page.obj.layout.LayoutObject;

public abstract class PanelLayoutObject extends LayoutObject {

	public PanelLayoutObject(String beforeObjectBody, PageObject[] objs, String afterObjectBody) {
		super(beforeObjectBody, objs, afterObjectBody);
	}
}
