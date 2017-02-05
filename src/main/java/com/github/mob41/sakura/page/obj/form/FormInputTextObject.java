package com.github.mob41.sakura.page.obj.form;

public class FormInputTextObject extends FormInputObject {

	public FormInputTextObject(String name){
		this(name, null);
	}
	
	public FormInputTextObject(String name, String initialValue){
		this(name, initialValue, "default");
	}
	
	public FormInputTextObject(String name, String initialValue, String layout) {
		super("text", name, initialValue);
		if (name != null){
			putAttribute("name", name);
		}
		
		if (initialValue != null){
			putAttribute("value", initialValue);
		}
		
		setTypeLayoutClasses(layout);
	}

}
