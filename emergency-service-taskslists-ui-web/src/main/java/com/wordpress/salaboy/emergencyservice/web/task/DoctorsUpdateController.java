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

/**
 * Controller to handle doctor's update task form.
 * 
 * @author calcacuervo
 */
@Controller
public class DoctorsUpdateController extends AbstractTaskFormController {

	@Override
	protected void addCustomFormLogic(Model model) {
	}

	@Override
	protected String getTaskType() {
		return "Doctor";
	}

	@Override
	protected String getViewPrefix() {
		return viewPrefix;
	}

	private static final String viewPrefix = "";
	private static final Logger logger = LoggerFactory
			.getLogger(DoctorsUpdateController.class);

	public DoctorsUpdateController() {
		super();
	}

	@Override
	@RequestMapping(value = "/task/do/{entity}/{profile}/{id}/{name}", method = RequestMethod.GET)
	public String taskInfo(@PathVariable("id") String id,
			@PathVariable("entity") String entity,
			@PathVariable("name") String name,
			@PathVariable("profile") String profile, Model model) {
		return super.taskInfo(id, entity, name, profile, model);
	}

	@RequestMapping(value = "/task/do/execute/{entity}/{profile}/{id}/{name}/{action}/{document}", method = RequestMethod.GET)
	public String executeTask(@PathVariable("id") String taskId,
			@PathVariable("action") String action,
			@PathVariable("entity") String entity,
			@PathVariable("name") String name,
			@PathVariable("document") String document,
			@PathVariable("profile") String profile, Model model) {
		return super.executeTask(taskId, action, entity, name, document,
				profile, model);
	}

	@RequestMapping(value = "/task/do/execute/{entity}/{profile}/{id}/{name}/{action}", method = RequestMethod.GET)
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
		info.put("emergency.severity", Integer.parseInt(data.get("Severity")));
		info.put("emergency.updatedNotes", data.get("Update Situation"));
		return info;
	}

	@Override
	protected void validate(Map<String, String> formSubmittedData)
			throws FormValidationException {
		String notes = formSubmittedData.get("Update Situation");
		if (notes == null || notes.isEmpty()) {
			throw new FormValidationException(
					"Doctor, please include an update!");
		}
		try {
			Integer.parseInt(formSubmittedData.get("Severity"));
		} catch (NumberFormatException nfe) {
			throw new FormValidationException("Severity must be a number");
		}
	}
}
