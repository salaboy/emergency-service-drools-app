package com.wordpress.salaboy.emergencyservice.web.task;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.yaml.snakeyaml.Yaml;

import com.wordpress.salaboy.smarttasks.formbuilder.api.ConnectionData;
import com.wordpress.salaboy.smarttasks.formbuilder.api.SmartTaskBuilder;
import com.wordpress.salaboy.smarttasks.formbuilder.api.TaskFormBuilder;
import com.wordpress.salaboy.smarttasks.formbuilder.api.TaskOperationsDefinition;
import com.wordpress.salaboy.smarttasks.formbuilder.api.exception.InvalidTaskException;
import com.wordpress.salaboy.smarttasks.formbuilder.api.output.TaskFormInput;
import com.wordpress.salaboy.smarttasks.formbuilder.api.output.TaskFormOutput;
import com.wordpress.salaboy.smarttasks.metamodel.MetaTaskDecoratorBase;
import com.wordpress.salaboy.smarttasks.metamodel.MetaTaskDecoratorService;

/**
 * Abstract controller which has all the common mechanism for the different task
 * form controllers. Extending classes will reimplement the methods, also adding
 * mappings for urls.
 * 
 * @author calcacuervo
 * 
 */
public abstract class AbstractTaskFormController {
	public AbstractTaskFormController() {
		MetaTaskDecoratorService.getInstance().registerDecorator("base",
				new MetaTaskDecoratorBase());
	}

	protected abstract String getTaskType();

	protected abstract String getViewPrefix();

	protected abstract void addCustomFormLogic(Model model);

	protected Map<String, Object> taskInfo;

	protected Map<String, Object> taskOutput;

	protected Yaml yaml = new Yaml();

	@Autowired
	protected SmartTaskBuilder helper;

	protected TaskFormBuilder taskHelper;

	public String taskInfo(String id, String entity, String name,
			String profile, Model model) {
		try {
			this.getTaskForm(entity, id, profile);
			TaskOperationsDefinition operationsDef = taskHelper
					.getTaskOperations();
			model.addAttribute("operations", operationsDef);
			model.addAttribute("taskInput", taskInfo);
			model.addAttribute("taskOutput", taskOutput);
			model.addAttribute("user", entity);
			model.addAttribute("profile", profile);
			model.addAttribute("name", name);
			model.addAttribute("id", taskInfo.get("Id"));
		} catch (InvalidTaskException e) {
			Logger.getLogger(AbstractTaskFormController.class.getName()).log(
					Level.SEVERE, "Task not found", e);
			return "redirect:/new/";
		}
		this.addCustomFormLogic(model);
		return this.getViewPrefix() + "task";
	}

	protected void getTaskForm(String entity, String id, String profile)
			throws InvalidTaskException {
		ConnectionData connectionData = new ConnectionData();
		connectionData.setEntityId(entity);
		helper.connect(connectionData);
		String taskType = this.getTaskType();
		taskHelper = helper.getTaskSupportHelper(id, taskType, profile);

		String stringTaskInfo = taskHelper.getTaskInput();
		TaskFormInput deserializedInput = (TaskFormInput) yaml
				.load(stringTaskInfo);
		taskInfo = deserializedInput.getInputs();

		String stringTaskOutput = taskHelper.getTaskOutput();
		TaskFormOutput deserializedOutput = (TaskFormOutput) yaml
				.load(stringTaskOutput);
		taskOutput = deserializedOutput.getOutputs();
	}

	public String executeTask(@PathVariable("id") String taskId,
			@PathVariable("action") String action,
			@PathVariable("entity") String entity,
			@PathVariable("name") String name,
			@PathVariable("document") String document,
			@PathVariable("profile") String profile, Model model) {
		if (taskHelper != null) {
			try {
				if ("complete".equalsIgnoreCase(action)) {
					Map<String, String> map = new HashMap<String, String>();
					String[] params = document.split(",");
					for (int i = 0; i < params.length; i++) {
						String[] param = params[i].split("=");
						if (param.length == 2 && param[0] != null
								&& param[1] != null) {
							map.put(param[0], param[1]);
						}
					}

					taskHelper.executeTaskAction(action,
							this.generateOutputForForm("", map));
				} else {
					taskHelper.executeTaskAction(action, null);
				}
			} catch (InvalidTaskException e) {
				return "redirect:new";
			}
		}
		return this.taskInfo(taskId, entity, name, profile, model);
	}

	protected abstract Map<String, Object> generateOutputForForm(String string,
			Map<String, String> map);

}
