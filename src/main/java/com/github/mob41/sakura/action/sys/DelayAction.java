package com.github.mob41.sakura.action.sys;

import com.github.mob41.sakura.action.Action;
import com.github.mob41.sakura.action.ActionResponse;
import com.github.mob41.sakura.action.ParameterType;
import com.github.mob41.sakura.api.SakuraServer;

public class DelayAction extends Action {
	
	private static final ParameterType[] types = new ParameterType[]{
			new ParameterType("Milliseconds",
			new String[]{"500", "1000", "1500",
					"2000", "2500", "3000", "3500", "4000", "4500","5000"})};

	public DelayAction(SakuraServer srv) {
		super(srv, "Delay");
	}

	@Override
	public ParameterType[] getParameterTypes() {
		return types;
	}

	@Override
	public ActionResponse run(Object[] args) {
		if (args == null || args.length == 0 || args.length > 1){
			return new ActionResponse(ActionResponse.STATUS_FAILED, "Not enough/too many parameters specified.");
		}
		
		String msstr = (String) args[0];
		
		int ms = -1;
		
		try {
			ms = Integer.parseInt(msstr);
		} catch (NumberFormatException ignore){}
		
		if (ms == -1){
			return new ActionResponse(ActionResponse.STATUS_FAILED, "Argument invalid: not a integer");
		}
		
		long st = System.currentTimeMillis();
		while ((System.currentTimeMillis() - st) <= ms){
			System.out.print("");
		}
		return ActionResponse.getDefaultResponse();
	}

}
