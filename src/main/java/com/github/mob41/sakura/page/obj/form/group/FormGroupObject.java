package com.github.mob41.sakura.page.obj.form.group;

import com.github.mob41.sakura.misc.MiscKit;
import com.github.mob41.sakura.page.obj.DivObject;
import com.github.mob41.sakura.page.obj.PageObject;
import com.github.mob41.sakura.page.obj.form.FormInputObject;

public class FormGroupObject extends DivObject {

	public FormGroupObject(FormGroupLabelObject label, PageObject[] objs){
		super(null, convertArray(MiscKit.arrayInsert(0, objs, label)), null);
		putAttribute("class", "form-group");
	}
	
	public static PageObject[] convertArray(Object[] arr){
		PageObject[] out = new PageObject[arr.length];
		for (int i = 0; i < out.length; i++){
			out[i] = (PageObject) arr[i];
		}
		return out;
	}
}
