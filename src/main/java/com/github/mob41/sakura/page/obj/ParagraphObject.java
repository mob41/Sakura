package com.github.mob41.sakura.page.obj;

import java.util.ArrayList;
import java.util.List;

public class ParagraphObject extends PageObject {

	private String text = "";
	
	private final List<PageObject> objs;
	
	public ParagraphObject(){
		this("");
	}
	
	public ParagraphObject(String text) {
		super("ParagraphObject", PageObject.NEW_TAG_TO_END, false);
		this.objs = new ArrayList<PageObject>(50);
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public void addInternalObject(int index, PageObject obj){
		objs.add(index, obj);
	}
	
	public void setInternalObject(int index, PageObject obj){
		objs.set(index, obj);
	}
	
	public void addInternalObject(PageObject obj){
		objs.add(obj);
	}
	
	public PageObject[] getInternalObjects(){
		PageObject[] arr = new PageObject[objs.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = objs.get(i);
		}
		return arr;
	}
	
	public PageObject getInternalObject(int index){
		return objs.get(index);
	}
	
	public void removeInternalObject(int index){
		objs.remove(index);
	}
	
	public void setText(String text){
		this.text = text;
	}

	@Override
	public String getTagName() {
		return "p";
	}

	@Override
	public String getBody(int tabLevel) {
		String tabs = getTabs(tabLevel + 1);
		
		String body = "";
		if (text != null){
			body += tabs + text + "\n";
		}

		for (int i = 0; i < objs.size(); i++){
			body += objs.get(i).getCode(tabLevel + 1) + "\n";
		}
		
		return body;
	}

	@Override
	public void init() {
		
	}

}
