package com.github.mob41.sakura.page.obj.layout;

import com.github.mob41.sakura.page.obj.DivObject;
import com.github.mob41.sakura.page.obj.PageObject;

public class RowObject extends DivObject {
	
	public RowObject() {
		this.putAttribute("class", "row");
	}
	
	public RowObject(ColumnObject[] objs) {
		super(null, null, null);
		int sum = 0;
		for (int i = 0; i < objs.length; i++){
			sum += objs[i].getWidth();
			if (sum > ColumnObject.MAXIMUM_WIDTH){
				break;
			}
			addInternalObject(objs[i]);
		}
		putAttribute("class", "row");
	}
	
	public void addColumn(ColumnObject obj){
		int sum = 0;
		PageObject[] objs = getInternalObjects();
		ColumnObject col;
		for (int i = 0; i < objs.length; i++){
			if (!(objs[i] instanceof ColumnObject)){
				continue;
			}
			col = (ColumnObject) objs[i];
			sum += col.getWidth();
			if (sum >= ColumnObject.MAXIMUM_WIDTH){
				return;
			}
		}
		
		addInternalObject(obj);
	}
	
	public ColumnObject getColumn(int index){
		PageObject[] objs = getInternalObjects();
		
		if (index < 0 || index >= objs.length){
			throw new ArrayIndexOutOfBoundsException(index);
		}
		
		PageObject obj = objs[index];
		
		return obj instanceof ColumnObject ? (ColumnObject) obj : null;
	}
	
	public void removeColumn(int index){
		removeInternalObject(index);
	}
}
