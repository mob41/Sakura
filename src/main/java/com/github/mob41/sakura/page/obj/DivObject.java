package com.github.mob41.sakura.page.obj;

import java.util.ArrayList;
import java.util.List;

public class DivObject extends PageObject{
	
	private String beforeObjBody = null;
	
	private String afterObjBody = null;
	
	private final List<PageObject> objs;
	
	public DivObject(){
		this(null, null, null);
	}
	
	public DivObject(PageObject obj){
		this(obj, null);
	}
	
	public DivObject(PageObject obj, String afterObjectBody){
		this(null, new PageObject[]{obj}, afterObjectBody);
	}
	
	public DivObject(PageObject[] objs, String afterObjectBody){
		this(null, objs, afterObjectBody);
	}
	
	public DivObject(String beforeObjectBody, PageObject obj){
		this(beforeObjectBody, new PageObject[]{obj}, "");
	}
	
	public DivObject(String beforeObjectBody, PageObject[] objs){
		this(beforeObjectBody, objs, "");
	}
	
	public DivObject(String beforeObjectBody, PageObject[] objs, String afterObjectBody){
		super("DIVObject", NEW_TAG_TO_END, true, false, false, true);
		this.beforeObjBody = beforeObjectBody;
		this.afterObjBody = afterObjectBody;	
		this.objs = new ArrayList<PageObject>(50);
		
		if (objs != null){
			for (PageObject obj : objs){
				this.objs.add(obj);
			}
		}
	}
	
	public DivObject(String body) {
		super("DivObject", NEW_TAG_TO_END, true, false, false, true);
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

	@Override
	public String getTagName() {
		return "div";
	}

	@Override
	public String getBody(int tabLevel) {
		String tabs = getTabs(tabLevel + 1);
		
		String body = "";
		if (beforeObjBody != null){
			body += tabs + beforeObjBody + "\n";
		}

		for (int i = 0; i < objs.size(); i++){
			body += objs.get(i).getCode(tabLevel + 1) + "\n";
		}
		
		if (afterObjBody != null){
			body += tabs + afterObjBody; 
		}
		return body;
	}

	@Override
	public void init() {
		
	}

}
