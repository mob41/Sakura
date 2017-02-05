package com.github.mob41.sakura.page.obj.form;

import java.util.ArrayList;
import java.util.List;

import com.github.mob41.sakura.page.obj.PageObject;

public class FormSelectObject extends PageObject {

	private final List<FormSelectOptionObject> options;
	
	private String name = null;
	
	public FormSelectObject(){
		this(null, new String[0][]);
	}
	
	public FormSelectObject(String name){
		this(name, new String[0][]);
	}
	
	public FormSelectObject(String name, String[][] initialOptions){
		this(name, convertToOptionObjs(initialOptions));
	}
	
	public FormSelectObject(String name, FormSelectOptionObject[] initialOptions) {
		super("FormSelectObject", PageObject.NEW_TAG_TO_END, true, false, false, true);
		options = new ArrayList<FormSelectOptionObject>(50);
		
		if (name != null){
			this.name = name;
		}
		
		if (initialOptions != null && initialOptions.length != 0){
			for (int i = 0; i < initialOptions.length; i++){
				options.add(initialOptions[i]);
			}
		}
		
		putAttribute("class", "form-control");
		putAttribute("name", name);
	}
	
	public List<FormSelectOptionObject> getOptions(){
		return options;
	}
	
	public void putOption(String label, String value){
		if (getOptionIndexViaLabel(label) == -1 && getOptionIndexViaValue(value) == -1){
			options.add(new FormSelectOptionObject(label, value));
		}
	}
	
	public void removeOption(String value){
		int index = getOptionIndexViaValue(value);
		
		if (index != -1){
			options.remove(index);
		}
	}
	
	public void markAllNotSelected(){
		for (FormSelectOptionObject obj : options){
			obj.markAsNotSelected();
		}
	}
	
	public void markAsSelected(String optionValue){
		int index = getOptionIndexViaValue(optionValue);
		
		if (index != -1){
			System.out.println("Index not -1. marking");
			options.get(index).markAsSelected();
		}
	}
	
	public void markAsNotSelected(String optionValue){
		int index = getOptionIndexViaValue(optionValue);
		
		if (index != -1){
			options.get(index).markAsSelected();
		}
	}
	
	public int getOptionIndexViaLabel(String label){
		for (int i = 0; i < options.size(); i++){
			if (label.equals(options.get(i).getLabel())){
				return i;
			}
		}
		return -1;
	}
	
	public int getOptionIndexViaValue(String value){
		for (int i = 0; i < options.size(); i++){
			System.out.println("i: " + i);
			if (value.equals(options.get(i).getValue())){
				return i;
			}
		}
		return -1;
	}

	@Override
	public String getTagName() {
		return "select";
	}

	@Override
	public String getBody(int tabLevel) {
		String body = "";
		
		if (name != null){
			putAttribute("name", name);
		}
		
		for (int i = 0; i < options.size(); i++){
			body += options.get(i).getCode(tabLevel + 1) + "\n";
		}
		
		return body;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public static final FormSelectOptionObject[] convertToOptionObjs(String[][] keysValues){
		if (keysValues == null){
			return null;
		}
		
		FormSelectOptionObject[] out = new FormSelectOptionObject[keysValues.length];
		
		FormSelectOptionObject obj;
		for (int i = 0; i < keysValues.length; i++){
			obj = new FormSelectOptionObject(keysValues[i][0], keysValues[i][1]);
			out[i] = obj;
		}
		
		return out;
	}

}
