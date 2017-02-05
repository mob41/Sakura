package com.github.mob41.sakura.page.obj.form;

import com.github.mob41.sakura.page.obj.PageObject;

public class FormSelectOptionObject extends PageObject {

	private String optionValue;
	
	private String optionLabel;
	
	public FormSelectOptionObject(String optionLabel, String optionValue) {
		super("FormSelectOptionObject", PageObject.NEW_TAG_TO_END, false, false, false, false);
		this.optionLabel = optionLabel;
		this.optionValue = optionValue;
		
		putAttribute("value", optionValue);
	}
	
	public void setValue(String value){
		this.optionValue = value;
		putAttribute("value", value);
	}
	
	public String getValue(){
		return optionValue;
	}
	
	public void setLabel(String label){
		this.optionLabel = label;
	}
	
	public String getLabel(){
		return optionLabel;
	}
	
	public void markAsSelected(){
		putAttribute("selected", "true");
	}
	
	public void markAsNotSelected(){
		putAttribute("selected", null);
	}

	@Override
	public String getTagName() {
		return "option";
	}

	@Override
	public String getBody(int tabLevel) {
		return optionLabel;
	}

	@Override
	public void init() {
		
	}

}
