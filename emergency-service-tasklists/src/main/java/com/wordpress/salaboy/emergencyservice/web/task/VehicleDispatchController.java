package com.wordpress.salaboy.emergencyservice.web.task;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.serviceclient.PersistenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import java.util.*;

/**
 * Controller to handle selection of vehicle task.
 * 
 * @author calcacuervo
 */
@Controller
public class VehicleDispatchController extends AbstractTaskFormController {
	
    @Override
    protected void addCustomFormLogic(Model model) {
    	//NOT NEEDED.
    }
    
    @Override
    protected String getTaskType() {
        return "Vehicle";
    }

    @Override
    protected String getViewPrefix() {
        return viewPrefix;
    }

    private static final String viewPrefix = "";
    private PersistenceService distributedService;
    
    public VehicleDispatchController() {
        super();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ContextTrackingImplementation", ContextTrackingProvider.ContextTrackingServiceType.IN_MEMORY);
        distributedService = PersistenceServiceProvider.getPersistenceService();
    }

    @Override
    @RequestMapping(value = "/task/ga/{entity}/{profile}/{id}/{name}", method = RequestMethod.GET)
    public String taskInfo(@PathVariable("id") String id,
            @PathVariable("entity") String entity,
            @PathVariable("name") String name,
            @PathVariable("profile") String profile, Model model) {
        return super.taskInfo(id, entity, name, profile, model);
    }

    @RequestMapping(value = "/task/ga/execute/{entity}/{profile}/{id}/{name}/{action}/{document}", method = RequestMethod.GET)
    public String executeTask(@PathVariable("id") String taskId,
            @PathVariable("action") String action,
            @PathVariable("entity") String entity,
            @PathVariable("name") String name,
            @PathVariable("document") String document,
            @PathVariable("profile") String profile, Model model) {
        return super.executeTask(taskId, action, entity, name, this.getVehiclesIds(document),
                profile, model);
    }

    @RequestMapping(value = "/task/ga/execute/{entity}/{profile}/{id}/{name}/{action}", method = RequestMethod.GET)
    public String executeTask(@PathVariable("id") String taskId,
            @PathVariable("action") String action,
            @PathVariable("entity") String entity,
            @PathVariable("name") String name,
            @PathVariable("profile") String profile, Model model) {
        return this.executeTask(taskId, action, entity, name, null, profile,
                model);
    }

    protected Map<String, Object> generateOutputForForm(String form,
            Map<String, String> data) {
        
        String ids = data.get("ids");
        
        if (ids == null){
            throw new IllegalStateException("Missing 'ids' attribute");
        }
        
        //separate each id and get the corresponding vehicle
    	List<Vehicle> selectedVehicles = new ArrayList<Vehicle>();
        
        StringTokenizer st = new StringTokenizer(ids, "||");
        while (st.hasMoreTokens()){
            selectedVehicles.add(distributedService
				.loadVehicle(st.nextToken()));
        }
        
        if (selectedVehicles.isEmpty()){
            throw new IllegalStateException("Empty Vehicle list for "+ids);
        }
		
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("emergency.vehicles", selectedVehicles);
        return info;
    }
    
    private String getVehiclesIds(String selected) {
    	String results="ids=";
        String separator = "";
        StringTokenizer st = new StringTokenizer(selected,"_");
        while (st.hasMoreTokens()){
            String vehicle = st.nextToken();
            int start= vehicle.indexOf("{");
            int finish= vehicle.indexOf("}");
            if (finish < 0){
                finish = vehicle.length();
            }
            vehicle = vehicle.substring(start + 1, finish);

            String[] props = vehicle.split(",");
            for (String string : props) {
                String[] keyval = string.trim().split("=");
                if (keyval[0].trim().equalsIgnoreCase("id")) {
                        results += separator+keyval[1].trim();
                        if (separator.equals("")){
                            separator = "||";
                        }
                }
            }
        }
        
        System.out.println("Selected Vehicles Ids: "+results);
        
    	return results;
    }

}