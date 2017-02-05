package com.github.mob41.sakura.page.obj.form;

public class FormInputButtonObject extends FormInputObject {

	public FormInputButtonObject(String name){
		this(name, null);
	}
	
	public FormInputButtonObject(String name, String initialValue){
		this(name, initialValue, "default");
	}
	
	public FormInputButtonObject(String name, String initialValue, String layout) {
		super("button", name, initialValue);
		if (name != null){
			putAttribute("name", name);
		}
		
		if (initialValue != null){
			putAttribute("value", initialValue);
		}
		
		setTypeLayoutClasses(layout);
	}

}
