package com.wordpress.salaboy.emergencyservice.web.task;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wordpress.salaboy.emergencyservice.web.task.exception.FormValidationException;
import com.wordpress.salaboy.emergencyservice.web.task.external.DistributedService;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Location;

/**
 * Controller to handle the emergency operator requests.
 * 
 * @author calcacuervo
 */
@Controller
public class EmergencyOperatorController extends AbstractTaskFormController {
    @Override
    protected void addCustomFormLogic(Model model) {
//        taskInfo.remove("Call Id");
    	String id = String.valueOf(System.currentTimeMillis());
        callsById.put(id, (Call) taskInfo.get("Call"));
        taskInfo.remove("Call");
        model.addAttribute("callId", id);
    }

    @Override
    protected String getTaskType() {
        return "Phone";
    }

    @Override
    protected String getViewPrefix() {
        return viewPrefix;
    }

    private static final String viewPrefix = "op_";
    private static final Logger logger = LoggerFactory
            .getLogger(EmergencyOperatorController.class);

    private static Map<String, Call> callsById = new HashMap<String, Call>();

    public EmergencyOperatorController() {
        super();
    }

    @RequestMapping(value = "/task/op/{entity}/{profile}/{id}/{name}", method = RequestMethod.GET)
    public String taskInfo(@PathVariable("id") String id,
            @PathVariable("entity") String entity,
            @PathVariable("name") String name,
            @PathVariable("profile") String profile, Model model) {
        return super.taskInfo(id, entity, name, profile, model);
    }

    @Override
    @RequestMapping(value = "/task/op/execute/{entity}/{profile}/{id}/{name}/{action}/{document}", method = RequestMethod.GET)
    public String executeTask(@PathVariable("id") String taskId,
            @PathVariable("action") String action,
            @PathVariable("entity") String entity,
            @PathVariable("name") String name,
            @PathVariable("document") String document,
            @PathVariable("profile") String profile, Model model) {
        return super.executeTask(taskId, action, entity, name, document,
                profile, model);
    }

    @RequestMapping(value = "/task/op/execute/{entity}/{profile}/{id}/{name}/{action}", method = RequestMethod.GET)
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
        // TODO for now, only for phone call form
        Map<String, Object> info = new HashMap<String, Object>();
        Emergency emergency = new Emergency();
        
        //persists the emergency
        new DistributedService().getDistributedService().storeEmergency(emergency);
        
        Location location = new Location();
        location.setLocationX(Integer.parseInt(data.get("Location X")));
        location.setLocationY(Integer.parseInt(data.get("Location Y")));
        emergency.setLocation(location);
        emergency.setNroOfPeople(Integer.valueOf(Integer.parseInt(data
                .get("Number Of People"))));
        emergency.setType(data.get("Emergency Type"));
        emergency.setCall(callsById.get(data.get("callId")));
        info.put("emergency", emergency);
        return info;
    }
    
    @Override
    protected void validate(Map<String, String> formSubmittedData)
    		throws FormValidationException {
//    	Integer people = Integer.valueOf(Integer.parseInt(formSubmittedData
//                .get("Number Of People")));
//    	if (!people.equals(1)) {
//    		throw new FormValidationException("Only supported one person form now.");
//    	}
    }

}
