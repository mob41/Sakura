package com.github.mob41.sakura.page.obj.form.group;

import com.github.mob41.sakura.page.obj.PageObject;

public class FormGroupLabelObject extends PageObject {

	private String text;
	
	public FormGroupLabelObject(){
		this("");
	}
	
	public FormGroupLabelObject(String text) {
		super("FormGroupLabelObject", PageObject.NEW_TAG_TO_END, false);
		this.text = text;
		setLayoutClasses();
	}
	
	public void setLayoutClasses(){
		putAttribute("class", "control-label");
	}
	
	public void setLabel(String text){
		this.text = text;
	}
	
	public String getLabel(){
		return text;
	}

	@Override
	public String getTagName() {
		return "label";
	}

	@Override
	public String getBody(int tabLevel) {
		return text;
	}

	@Override
	public void init() {
		
	}

}
