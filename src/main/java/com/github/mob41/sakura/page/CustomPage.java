package com.github.mob41.sakura.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.github.mob41.sakura.page.obj.PageObject;

public abstract class CustomPage {
	
	private final String name;
	
	private final String fa_icon_str;
	
	private final List<PageObject> _objs;
	
	private final Map<String, PageObject> _regUptObjs;

	private final List<CustomPageHandler> _handlers;
	
	private final Map<String, String> _headers;
	
	private boolean autoRefreshPostedPage = false;
	
	private CustomPage redirectPage = null;
	
	public CustomPage(String name){
		this(name, "fa-file-text");
	}
	
	public CustomPage(String name, String page_fa_icon_str) {
		this.name = name;
		this.fa_icon_str = page_fa_icon_str;
		_objs = new ArrayList<PageObject>(50);
		_regUptObjs = new HashMap<String, PageObject>(50);
		_handlers = new ArrayList<CustomPageHandler>(50);
		_headers = new LinkedHashMap<String, String>(50);
		init();
	}
	
	public abstract void init();
	
	public void update(HttpServletRequest request){
		if (redirectPage != null){
			redirectPage.update(request);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String getFontAwesomeIconStr(){
		return fa_icon_str;
	}
	
	public void handleAll(HttpServletRequest request){
		for (CustomPageHandler handler : _handlers){
			handler.handle(request);
		}
	}
	
	public final boolean isAutoRefreshPostedPage(){
		return autoRefreshPostedPage;
	}
	
	public final void setAutoRefreshPostedPage(boolean arg0){
		this.autoRefreshPostedPage = arg0;
	}
	
	public final void redirectPage(CustomPage page){
		redirectPage = page;
	}
	
	public final void disableRedirect(){
		redirectPage = null;
	}
	
	public final String addHeader(String key, String value){
		return _headers.put(key, value);
	}
	
	public final String removeHeader(String key){
		return _headers.remove(key);
	}
	
	public final Map<String, String> getHeaders(){
		return _headers;
	}
	
	public final void addHandler(CustomPageHandler handler){
		_handlers.add(handler);
	}
	
	public final CustomPageHandler getHandler(int index){
		return _handlers.get(index);
	}
	
	public final void removeHandler(int index){
		_handlers.remove(index);
	}
	
	public final void prepareObjectForUpdate(String prepareName, PageObject arg0){
		_regUptObjs.put(prepareName, arg0);
	}
	
	public final PageObject getPreparedObject(String prepareName){
		return _regUptObjs.get(prepareName);
	}
	
	public final void removedPreparedObject(String prepareName){
		_regUptObjs.remove(prepareName);
	}
	
	public final void addObject(PageObject arg0){
		_objs.add(arg0);
	}
	
	public final void removeObject(int index){
		_objs.remove(index);
	}
	
	public final List<PageObject> getObjects(){
		return _objs;
	}
	
	public final PageObject getObject(int index){
		return _objs.get(index);
	}
	
	public final String getCode(){
		return getCode(0);
	}
	
	public String getCode(int tabLevel){
		if (redirectPage != null){
			return redirectPage.getCode(tabLevel);
		}
		
		String tabs = PageObject.getTabs(tabLevel);
		String para = tabs + "<!-- The following code was dynamically generated with the power of CustomPage -->\n";
		PageObject obj;
		for (int i = 0; i < _objs.size(); i++){
			obj = _objs.get(i);
			para += obj.getCode(tabLevel) + "\n";
		}
		para += tabs + "<!-- End of generated CustomPage code -->\n";
		
		return para;
	}

}
