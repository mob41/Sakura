package com.github.mob41.sakura.page.obj;

public class ButtonObject extends PageObject {

	private String value;
	
	public ButtonObject(String value) {
		super("ButtonObject", PageObject.NEW_TAG_TO_END, false, false, false, false);
		this.value = value;
		putAttribute("type", "button");
	}
	
	public String getValue(){
		return value;
	}
	
	public void setValue(String value){
		this.value = value;
	}

	@Override
	public String getTagName() {
		return "button";
	}

	@Override
	public String getBody(int tabLevel) {
		return value;
	}

	@Override
	public void init() {
		
	}

}
