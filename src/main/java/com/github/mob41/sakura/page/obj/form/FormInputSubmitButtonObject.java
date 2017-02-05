package com.github.mob41.sakura.page.obj.form;

public class FormInputSubmitButtonObject extends FormInputObject {

	public FormInputSubmitButtonObject(){
		this("Submit");
	}
	
	public FormInputSubmitButtonObject(String buttonName){
		this(buttonName, "default");
	}
	
	public FormInputSubmitButtonObject(String buttonName, String layout) {
		super("submit", null, buttonName);
		
		putAttribute("value", buttonName);
		
		setTypeLayoutClasses(layout);
	}

}
