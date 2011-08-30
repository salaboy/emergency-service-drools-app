package com.wordpress.salaboy.services.workitemhandlers;

import java.util.List;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.VehicleUpdate;

/**
 * Work item to make the local report of a vehicle updates for an emergency.
 * @author calcacuervo
 *
 */
public class LocalReportWorkItemHandler implements WorkItemHandler {

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Emergency emergency = (Emergency) workItem.getParameter("emergency");
		Vehicle vechicle = (Vehicle) workItem.getParameter("vehicle");
		if (emergency != null && vechicle != null) {
			System.out.println("Updates for vehicle: " + vechicle.getId());
			List<VehicleUpdate> updates = emergency
					.getUpdatesForVehicle(vechicle.getId());
			if (updates != null) {
				for (VehicleUpdate vehicleUpdate : updates) {
					System.out.println(vehicleUpdate);
				}
			}
		}
	}
}
