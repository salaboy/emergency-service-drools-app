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

import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Location;
import com.wordpress.salaboy.model.Patient;

/**
 * Controller to handle the emergency operator requests.
 * 
 * @author calcacuervo
 */
@Controller
public class EmergencyOperatorController extends AbstractTaskFormController {
    @Override
    protected void addCustomFormLogic(Model model) {
        Integer callId = (Integer) taskInfo.get("Call Id");
        taskInfo.remove("Call Id");
        callsById.put(callId, (Call) taskInfo.get("Call"));
        taskInfo.remove("Call");
        model.addAttribute("callId", callId);
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

    private static Map<Integer, Call> callsById = new HashMap<Integer, Call>();

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
        Location location = new Location();
        location.setLocationX(Integer.parseInt(data.get("Location X")));
        location.setLocationY(Integer.parseInt(data.get("Location Y")));
        emergency.setLocation(location);
        emergency.setNroOfPeople(Integer.valueOf(Integer.parseInt(data
                .get("Number Of people"))));
        emergency.setType(data.get("Emergency Type"));
        emergency.setCall(callsById.get(Integer.parseInt(data.get("callId"))));
        info.put("emergency", emergency);
        if (data.get("Number Of people").equals("1")) {
            Patient patient = new Patient();
            patient.setAge(Integer.valueOf(data.get("Age")));
            patient.setGender(data.get("Gender"));
            info.put("patient", patient);
        }
        return info;
    }

}
