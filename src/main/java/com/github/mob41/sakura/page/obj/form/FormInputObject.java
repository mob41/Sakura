package com.github.mob41.sakura.page.obj.form;

import com.github.mob41.sakura.page.obj.PageObject;

public class FormInputObject extends PageObject {
	
	private String type = "";
	
	public FormInputObject(String type, String value){
		this(type, null, value);
	}
	
	public FormInputObject(String type, String name, String value) {
		super("InputObject", PageObject.SAME_TAG_SLASH_END);
		this.type = type;
		putAttribute("type", type);
		
		if (name != null){
			putAttribute("name", name);
		}
		
		putAttribute("value", value);
	}
	
	public void setType(String type){
		this.type = type;
		putAttribute("type", type);
	}
	
	public void setName(String name){
		putAttribute("name", name);
	}
	
	public void setValue(String value){
		putAttribute("value", value);
	}
	
	public String getType(){
		return getAttribute("type");
	}
	
	public String getName(){
		return getAttribute("name");
	}
	
	public String getValue(){
		return getAttribute("value");
	}
	
	public void setTypeLayoutClasses(){
		setTypeLayoutClasses(null);
	}
	
	public void setTypeLayoutClasses(String layout){
		if (type.equals("button") || type.equals("submit")){
			putAttribute("class", "btn btn-" + (layout != null ? layout : "default"));
		} else {
			putAttribute("class", "form-control");
		}
	}
	
	@Override
	public String getTagName() {
		return "input";
	}

	@Override
	public String getBody(int tabLevel) {
		return null;
	}

	@Override
	public void init() {
		
	}

}
