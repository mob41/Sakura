package com.github.mob41.sakura.page;

import java.util.ArrayList;
import java.util.List;

import com.github.mob41.sakura.api.SakuraServer;

public class CustomPageManager {
	
	private final List<CustomPage> pages;

	public CustomPageManager(SakuraServer srv) {
		pages = new ArrayList<CustomPage>(50);
	}
	
	public void registerPage(CustomPage page){
		pages.add(page);
	}
	
	public void unregisterPage(int index){
		pages.remove(index);
	}
	
	public boolean isPageExist(String pageName){
		return getPageIndex(pageName) != -1;
	}
	
	public CustomPage getPage(int index){
		return index < pages.size() && index > 0 ? pages.get(index) : null;
	}
	
	public CustomPage getPage(String pageName){
		int index = getPageIndex(pageName);
		return index != -1 ? pages.get(index) : null;
	}
	
	public CustomPage[] getPages(){
		CustomPage[] arr = new CustomPage[pages.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = pages.get(i);
		}
		return arr;
	}
	
	public int getPageIndex(String pageName){
		for (int i = 0; i < pages.size(); i++){
			if (pages.get(i).getName().equals(pageName)){
				return i;
			}
		}
		return -1;
	}

}
