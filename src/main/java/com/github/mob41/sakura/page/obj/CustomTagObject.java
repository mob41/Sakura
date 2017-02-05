package com.github.mob41.sakura.page.obj;

public class CustomTagObject extends PageObject {

	private String body = "";
	
	private String tagName = "";
	
	public CustomTagObject(String tag){
		this(tag, null, PageObject.SAME_TAG_SLASH_END, true);
	}
	
	public CustomTagObject(String tag, String body, int end_tag_mode){
		this(tag, body, end_tag_mode, true);
	}
	
	public CustomTagObject(String tag, String body, int end_tag_mode, boolean codeTidyTweaks) {
		super("CustomTag-" + tag, end_tag_mode, codeTidyTweaks);
		this.body = body;
		this.tagName = tag;
	}

	@Override
	public String getTagName() {
		return tagName;
	}

	@Override
	public String getBody(int tabLevel) {
		return getTabs(tabLevel) + body;
	}

	@Override
	public void init() {
		
	}

}
