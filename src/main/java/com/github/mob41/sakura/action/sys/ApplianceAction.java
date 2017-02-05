package com.github.mob41.sakura.action.sys;

import com.github.mob41.sakura.action.Action;
import com.github.mob41.sakura.action.ActionResponse;
import com.github.mob41.sakura.action.ParameterType;
import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.appliance.ApplianceManager;

import edu.emory.mathcs.backport.java.util.Arrays;

public class ApplianceAction extends Action {
	
	private final ParameterType[] types = {
			new ParameterType("Appliance name", buildApplianceStr()),
			new ParameterType("Action", new String[]{"Turn on", "Turn off"})
	};

	public ApplianceAction(SakuraServer srv) {
		super(srv, "Control appliances");
	}
	
	public String[] buildApplianceStr(){
		ApplianceManager am = getServer().getApplianceManager();
		String[] arr = new String[am.getAppliances().size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = am.getAppliance(i).getName();
		}
		System.out.println(Arrays.toString(arr));
		return arr;
	}

	@Override
	public ParameterType[] getParameterTypes() {
		return new ParameterType[]{
				new ParameterType("Appliance name", buildApplianceStr()),
				new ParameterType("Action", new String[]{"Turn on", "Turn off"})
		};
	}

	@Override
	public ActionResponse run(Object[] args) {
		if (args.length < 2 || args.length > 2){
			return new ActionResponse(ActionResponse.STATUS_FAILED, "Too many/few arguments specified: " + args.length);
		} else if (!(args[0] instanceof String) || !(args[1] instanceof String)){
			return new ActionResponse(ActionResponse.STATUS_FAILED, "Invalid arguments specified.");
		}
		ApplianceManager am = getServer().getApplianceManager();
		boolean success = false;
		if (args[1].equals("Turn on")){
			success = am.getAppliance((String) args[0]).turnOn(getServer());
		} else if (args[1].equals("Turn off")){
			success = am.getAppliance((String) args[0]).turnOff(getServer());
		} else {
			return new ActionResponse(ActionResponse.STATUS_FAILED, "Invalid action specified: " + args[1]);
		}
		return success ? new ActionResponse(ActionResponse.STATUS_SUCCESS, "Light turned on successfully.") :
			new ActionResponse(ActionResponse.STATUS_FAILED, "Could not turn on light successfully.");
	}

}
