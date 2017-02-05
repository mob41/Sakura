package com.github.mob41.sakura.page.obj.form;

public class FormInputHiddenObject extends FormInputObject {
	
	public FormInputHiddenObject(String name, String value) {
		super("hidden", name, value);
		putAttribute("name", name);
		putAttribute("value", value);
	}

}
