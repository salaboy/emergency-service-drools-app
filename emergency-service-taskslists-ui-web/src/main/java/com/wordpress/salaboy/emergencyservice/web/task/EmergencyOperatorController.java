package com.wordpress.salaboy.emergencyservice.web.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
		// taskInfo.remove("Call Id");
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
		String ti = super.taskInfo(id, entity, name, profile, model);
		// As we don't have in smart tasks the notion of dependency between
		// inputs.. let's make it here.
		Map<String, Object> children = new HashMap<String, Object>();
		Map<String, List<Object>> parent = new HashMap<String, List<Object>>();
		Map<String, Object> newTaskOutput = new HashMap<String, Object>();
		for (String key : taskOutput.keySet()) {
			if (key.contains(":")) {
				String[] splitted = key.split(":");
				children.put(splitted[1], splitted[0]);
				List current = parent.get(splitted[0]);
				if (current == null) {
					current = new ArrayList<Object>();
					parent.put(splitted[0], current);
				}
				current.add(splitted[1]);
//				parent.put(splitted[0], splitted[1]);
				newTaskOutput.put(splitted[1], taskOutput.get(key));
			} else {
				newTaskOutput.put(key, taskOutput.get(key));
			}
		}
		model.addAttribute("taskOutput", newTaskOutput);
		model.addAttribute("children", children);
		model.addAttribute("parents", parent);
		return ti;
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

		// persists the emergency
		Location location = new Location();
		location.setLocationX(Integer.parseInt(data.get("Location X")));
		location.setLocationY(Integer.parseInt(data.get("Location Y")));
		emergency.setLocation(location);
		if (data.get("Number Of People") != null) {
			emergency.setNroOfPeople(Integer.valueOf(Integer.parseInt(data
					.get("Number Of People"))));
		} else {
			// this patch is because in the process we currently use fire
			// intensity taken from nro of people :S
			emergency.setNroOfPeople(Integer.valueOf(Integer.parseInt(data
					.get("Intensity"))));
		}
		emergency.setType(data.get("Emergency Type"));
		emergency.setCall(callsById.get(data.get("callId")));
		new DistributedService().getDistributedService().storeEmergency(
				emergency);
		info.put("emergency", emergency);
		return info;
	}

}
