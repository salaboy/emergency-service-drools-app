package com.wordpress.salaboy.services.workitemhandlers;

import java.util.List;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.vehicles.Vehicle;
import com.wordpress.salaboy.model.VehicleUpdate;

/**
 * Report for all vehicles in a particular procedure
 * @author calcacuervo
 *
 */
public class ProcedureReportWorkItemHandler implements WorkItemHandler {

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        Emergency emergency = (Emergency) workItem.getParameter("emergency");
        List<Vehicle> vehicles = (List<Vehicle>) workItem.getParameter("emergency.vehicles");
        System.out.println("Procedure Report:");
        if (emergency != null && vehicles != null) {
            for (Vehicle vehicle : vehicles) {
                System.out.println("\tUpdates for vehicle: " + vehicle.getId());
                List<VehicleUpdate> updates = emergency.getUpdatesForVehicle(vehicle.getId());
                if (updates != null) {
                    for (VehicleUpdate vehicleUpdate : updates) {
                        System.out.println("\t\t"+vehicleUpdate);
                    }
                }
            }

        }
        
        manager.completeWorkItem(workItem.getId(), null);
    }
}
