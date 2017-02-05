package com.github.mob41.sakura.page.obj.form;

import java.util.ArrayList;
import java.util.List;

import com.github.mob41.sakura.page.obj.PageObject;

public class FormObject extends PageObject{
	
	private String beforeObjBody = null;
	
	private String afterObjBody = null;
	
	private final List<PageObject> objs;
	
	public FormObject(){
		this(null, null, null);
	}
	
	public FormObject(PageObject obj){
		this(obj, null);
	}
	
	public FormObject(PageObject obj, String afterObjectBody){
		this(null, new PageObject[]{obj}, afterObjectBody);
	}
	
	public FormObject(PageObject[] objs, String afterObjectBody){
		this(null, objs, afterObjectBody);
	}
	
	public FormObject(String beforeObjectBody, PageObject obj){
		this(beforeObjectBody, new PageObject[]{obj}, "");
	}
	
	public FormObject(String beforeObjectBody, PageObject[] objs){
		this(beforeObjectBody, objs, "");
	}
	
	public FormObject(String beforeObjectBody, PageObject[] objs, String afterObjectBody){
		super("FormObject", NEW_TAG_TO_END, true, false, false, true);
		this.beforeObjBody = beforeObjectBody;
		this.afterObjBody = afterObjectBody;	
		this.objs = new ArrayList<PageObject>(50);
		
		if (objs != null){
			for (PageObject obj : objs){
				this.objs.add(obj);
			}
		}
	}
	
	public FormObject(String body) {
		super("DIVObject", NEW_TAG_TO_END, true, false, false, true);
		this.beforeObjBody = body;
		this.objs = new ArrayList<PageObject>(50);
	}
	
	public void setBeforeObjectText(String beforeObjBody){
		this.beforeObjBody = beforeObjBody;
	}
	
	public void setAfterObjectText(String afterObjBody){
		this.afterObjBody = afterObjBody;
	}
	
	public String getBeforeObjectText(){
		return beforeObjBody;
	}
	
	public String getAfterObjectText(){
		return afterObjBody;
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

	@Override
	public String getTagName() {
		return "form";
	}

	@Override
	public String getBody(int tabLevel) {
		String body = "";
		if (beforeObjBody != null){
			body += beforeObjBody + "\n";
		}

		for (int i = 0; i < objs.size(); i++){
			body += objs.get(i).getCode(tabLevel + 1) + "\n";
		}
		
		if (afterObjBody != null){
			body += afterObjBody; 
		}
		return body;
	}

	@Override
	public void init() {
		putAttribute("role", "form");
	}

}
