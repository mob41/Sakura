package com.github.mob41.sakura.page.obj.form;

public class FormInputResetButtonObject extends FormInputObject {

	public FormInputResetButtonObject(){
		this("Submit");
	}
	
	public FormInputResetButtonObject(String buttonName){
		this(buttonName, "default");
	}
	
	public FormInputResetButtonObject(String buttonName, String layout) {
		super("reset", null, buttonName);
		
		putAttribute("value", buttonName);
		
		setTypeLayoutClasses(layout);
	}
	
	@Override
	public void setTypeLayoutClasses(String layout){
		putAttribute("class", "btn btn-" + layout);
	}

}
