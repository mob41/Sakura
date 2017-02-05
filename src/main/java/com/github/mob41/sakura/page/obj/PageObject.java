package com.github.mob41.sakura.page.obj;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class PageObject {
	
	public static final int NEW_TAG_TO_END = 0;
	
	public static final int SAME_TAG_SLASH_END = 1;
	
	private int end_tag_mode;
	
	private final boolean newLineAfterTag;
	
	private final boolean newLineAfterBody;
	
	private final boolean tabBeforeBody;
	
	private final boolean autoFillTabsBeforeEndTag;
	
	private final String name;
	
	private final Map<String, String> attr;
	
	public PageObject(String name, int end_tag_mode){
		this(name, end_tag_mode, true, true, true);
	}
	
	public PageObject(String name, int end_tag_mode, boolean codeTidyTweaks){
		this(name, end_tag_mode, codeTidyTweaks, codeTidyTweaks, codeTidyTweaks);
	}
	
	public PageObject(String name, int end_tag_mode, boolean newLineAfterTagAfterBody, boolean tabBeforeBody){
		this(name, end_tag_mode, newLineAfterTagAfterBody, newLineAfterTagAfterBody, tabBeforeBody, true);
	}
	
	public PageObject(String name, int end_tag_mode, boolean newLineAfterTagAfterBody, boolean tabBeforeBody, boolean autoFillTabsBeforeEndTag){
		this(name, end_tag_mode, newLineAfterTagAfterBody, newLineAfterTagAfterBody, tabBeforeBody, autoFillTabsBeforeEndTag);
	}
	
	public PageObject(String name, int end_tag_mode, boolean newLineAfterTag, boolean newLineAfterBody, boolean tabBeforeBody, boolean autoFillTabsBeforeEndTag){
		this.end_tag_mode = end_tag_mode;
		this.name = name;
		this.newLineAfterTag = newLineAfterTag;
		this.newLineAfterBody = newLineAfterBody;
		this.tabBeforeBody = tabBeforeBody;
		this.autoFillTabsBeforeEndTag = autoFillTabsBeforeEndTag;
		attr = new HashMap<String, String>(50);
		init();
	}
	
	public abstract String getTagName();
	
	public abstract String getBody(int tabLevel);
	
	public abstract void init();
	
	public String getName(){
		return name;
	}
	
	public final void putAttribute(String name, String value){
		attr.put(name, value);
	}
	
	public final String getAttribute(String name){
		return attr.get(name);
	}
	
	public final Map<String, String> getTagAttributes(){
		return attr;
	}
	
	public final void setEndTagMode(int mode){
		this.end_tag_mode = mode;
	}
	
	public final int getEndTagMode(){
		return end_tag_mode;
	}
	
	public boolean isNewLineAfterTag(){
		return newLineAfterTag;
	}
	
	public boolean isNewLineAfterBody(){
		return newLineAfterBody;
	}
	
	public boolean isTabBeforeBody(){
		return tabBeforeBody;
	}
	
	public boolean isAutoFillTabsBeforeEndTag(){
		return autoFillTabsBeforeEndTag;
	}
	
	public String getCode(){
		return getCode(0);
	}
	
	public static String getTabs(int tabLevel){
		String tabs = "";
		for (int i = 0; i < tabLevel; i++){
			tabs += "\t";
		}
		return tabs;
	}
	
	public String getCode(int tabLevel){
		String tabs = getTabs(tabLevel);
				
		String objtag = tabs + "<" + getTagName();
		
		Map<String, String> attrs = getTagAttributes();
		Iterator<String> it = attrs != null ? attrs.keySet().iterator() : null;
		
		if (attrs != null){
			String key;
			String val;
			while (it.hasNext()){
				key = it.next();
				val = attrs.get(key);
				if (val != null){
					objtag += " " + key + "=\"" + val + "\"";
				}
			}
		}
		
		if (getEndTagMode() == PageObject.NEW_TAG_TO_END){
			objtag += ">";
			if (isNewLineAfterTag()){
				objtag += "\n";
			}
			if (isTabBeforeBody()){
				objtag += "\t";
			}
			objtag += getBody(tabLevel);
			if (isNewLineAfterBody()){
				objtag += "\n";
			}
			objtag += (isAutoFillTabsBeforeEndTag() ? tabs : "") + "</" + getTagName() + ">";
			
		} else if (getEndTagMode() == PageObject.SAME_TAG_SLASH_END){
			objtag += " />";
		} else {
			objtag = tabs + "<!-- Error:  \"" + getName() + "\" cannot be parsed: Unknown End Tag Mode: " + getEndTagMode() + " -->";
		}
		
		return objtag;
	}

}
