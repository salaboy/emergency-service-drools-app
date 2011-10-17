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
import com.wordpress.salaboy.model.SelectedProcedures;

/**
 * Controller to handle control operator requests.
 * 
 * @author calcacuervo
 */
@Controller
public class ControlOperatorController extends AbstractTaskFormController {
	@Override
	protected void addCustomFormLogic(Model model) {
		this.emergencyId = (String)this.taskInfo.get("EmergencyId");
		this.taskInfo.remove("EmergencyId");
		model.addAttribute("suggestedProcedure", this
				.getCleanSuggestedProcedure((String) this.taskInfo
						.get("suggestedProcedures")));
		this.taskInfo.remove("suggestedProcedures");

	}

	private String getCleanSuggestedProcedure(String suggestedProceduresString) {
		suggestedProceduresString = suggestedProceduresString.trim();
		if (suggestedProceduresString.startsWith("[")) {
			suggestedProceduresString = suggestedProceduresString.substring(1);
		}
		if (suggestedProceduresString.endsWith("]")) {
			suggestedProceduresString = suggestedProceduresString.substring(0,
					suggestedProceduresString.length() - 1);
		}
		suggestedProceduresString = suggestedProceduresString.trim();
		return suggestedProceduresString.split(":")[0].trim();
	}

	@Override
	protected String getTaskType() {
		return "Control";
	}

	@Override
	protected String getViewPrefix() {
		return viewPrefix;
	}

	private static final String viewPrefix = "co_";
	private static final Logger logger = LoggerFactory
			.getLogger(ControlOperatorController.class);

	public ControlOperatorController() {
		super();
	}

	private String emergencyId;

	@Override
	@RequestMapping(value = "/task/co/{entity}/{profile}/{id}/{name}", method = RequestMethod.GET)
	public String taskInfo(@PathVariable("id") String id,
			@PathVariable("entity") String entity,
			@PathVariable("name") String name,
			@PathVariable("profile") String profile, Model model) {
		return super.taskInfo(id, entity, name, profile, model);
	}

	@RequestMapping(value = "/task/co/execute/{entity}/{profile}/{id}/{name}/{action}/{document}", method = RequestMethod.GET)
	public String executeTask(@PathVariable("id") String taskId,
			@PathVariable("action") String action,
			@PathVariable("entity") String entity,
			@PathVariable("name") String name,
			@PathVariable("document") String document,
			@PathVariable("profile") String profile, Model model) {
		return super.executeTask(taskId, action, entity, name, document,
				profile, model);
	}

	@RequestMapping(value = "/task/co/execute/{entity}/{profile}/{id}/{name}/{action}", method = RequestMethod.GET)
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
		Map<String, Object> info = new HashMap<String, Object>();
		SelectedProcedures selectedProcedures = new SelectedProcedures(emergencyId);
		String[] sps =data.get("Suggested Procedures").substring(1).split("_");
		for (String string : sps) {
			if (string != null && string != "") {
				selectedProcedures.addSelectedProcedureName(string);				
			}
		}
		info.put("selectedProcedures", selectedProcedures);
		return info;
	}

	@Override
	protected void validate(Map<String, String> formSubmittedData)
			throws FormValidationException {
		String selected = formSubmittedData.get("Suggested Procedures");
		if (selected == null || selected.isEmpty()) {
			throw new FormValidationException("Not recognized procedure");
		}

	}
}
