package com.github.mob41.sakura.page.obj.form;

public class FormInputPasswordObject extends FormInputObject {

	public FormInputPasswordObject(String name){
		this(name, null);
	}
	
	public FormInputPasswordObject(String name, String initialValue){
		this(name, initialValue, "default");
	}
	
	public FormInputPasswordObject(String name, String initialValue, String layout) {
		super("password", name, initialValue);
		if (name != null){
			putAttribute("name", name);
		}
		
		if (initialValue != null){
			putAttribute("value", initialValue);
		}
		
		setTypeLayoutClasses(layout);
	}

}
