package com.wordpress.salaboy.emergencyservice.web.task.external;

import java.util.ArrayList;
import java.util.List;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Emergency.EmergencyType;

/**
 * This acts as an adaptor for any useful data that can be shown in a form, and
 * it is not in another service.
 * 
 * @author calcacuervo
 * 
 */
public class ModelDataConnector {

	/**
	 * Returns the emergency Types.
	 * @return
	 */
	public List<String> emergencyTypes() {
		List<String> emergencyTypesList = new ArrayList<String>();
		EmergencyType[] types = Emergency.EmergencyType.values();
		for (int i = 0; i < types.length; i++) {
			EmergencyType emergencyType = types[i];
			emergencyTypesList.add(emergencyType.name());
		}
		return emergencyTypesList;
	}
}
