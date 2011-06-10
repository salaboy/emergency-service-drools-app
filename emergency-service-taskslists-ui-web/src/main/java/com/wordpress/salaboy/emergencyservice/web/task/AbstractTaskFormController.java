package com.wordpress.salaboy.emergencyservice.web.task;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.yaml.snakeyaml.Yaml;

import com.wordpress.salaboy.emergencyservice.web.task.exception.FormValidationException;
import com.wordpress.salaboy.smarttasks.formbuilder.api.ConnectionData;
import com.wordpress.salaboy.smarttasks.formbuilder.api.SmartTaskBuilder;
import com.wordpress.salaboy.smarttasks.formbuilder.api.TaskOperationsDefinition;
import com.wordpress.salaboy.smarttasks.formbuilder.api.exception.InvalidTaskException;
import com.wordpress.salaboy.smarttasks.formbuilder.api.output.TaskForm;
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

	/**
	 * Task inputs.
	 */
	protected Map<String, Object> taskInfo;

	/**
	 * Task outputs.
	 */
	protected Map<String, Object> taskOutput;

	/**
	 * Concrete classes will implement this method with the task type they are
	 * intended to render. This task type will be used to select the form
	 * configuration with smart tasks.
	 * 
	 * @return
	 */
	protected abstract String getTaskType();

	/**
	 * {@link Yaml} instance is used the deserialize the data.
	 */
	protected Yaml yaml = new Yaml();

	/**
	 * Inyected builder.
	 */
	@Autowired
	protected SmartTaskBuilder helper;

	/**
	 * This is the main method to show task form. It will put in the model the
	 * task input and outputs extracted with smart tasks, and also the
	 * operations to show buttons. This method should be overriden by concrete
	 * controllers, which will also make additional things and configure the url
	 * mapping.
	 * 
	 * @param id
	 * @param entity
	 * @param name
	 * @param profile
	 * @param model
	 * @return
	 */
	public String taskInfo(String id, String entity, String name,
			String profile, Model model) {
		try {
			this.getTaskForm(entity, id, profile);
			TaskOperationsDefinition operationsDef = helper
					.getTaskOperations(id);
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

	/**
	 * Gets the task form {@link SmartTasksTaskFormBuilder}.
	 * 
	 * @param entity
	 * @param id
	 * @param profile
	 * @throws InvalidTaskException
	 *             if a task is not found
	 */
	protected void getTaskForm(String entity, String id, String profile)
			throws InvalidTaskException {
		ConnectionData connectionData = new ConnectionData(entity);
		helper.connect(connectionData);
		String taskType = this.getTaskType();

		String stringTaskform = helper.getTaskForm(id, taskType, profile);
		TaskForm deserializedForm = (TaskForm) yaml.load(stringTaskform);
		taskInfo = deserializedForm.getInputs();

		taskOutput = deserializedForm.getOutputs();
	}

	/**
	 * Executes a given action over a given task. It assumes it receives the
	 * data (only for complete action) in the form data1=info1,data2=info2. If
	 * the action is "complete", it transform this data string with a map, and
	 * calls an abstract method which will transform this data with task
	 * specific data.
	 * 
	 * @param taskId
	 * @param action
	 * @param entity
	 * @param name
	 * @param data
	 * @param profile
	 * @param model
	 * @return
	 */
	public String executeTask(String taskId, String action, String entity,
			String name, String data, String profile, Model model) {
		try {
			if ("complete".equalsIgnoreCase(action)) {
				Map<String, String> map = new HashMap<String, String>();
				String[] params = data.split(",");
				for (int i = 0; i < params.length; i++) {
					String[] param = params[i].split("=");
					if (param.length == 2 && param[0] != null
							&& param[1] != null) {
						map.put(param[0], param[1]);
					}
				}

				try {
					this.validate(map);
				} catch (FormValidationException ex) {
					model.addAttribute("validationError", ex.getMessage());
					return this.taskInfo(taskId, entity, name, profile, model);
				}
				Map<String, Object> taskData = this.generateOutputForForm("",
						map);
				helper.executeTaskAction(action, taskData, taskId);
			} else {
				helper.executeTaskAction(action, null, taskId);
			}
		} catch (InvalidTaskException e) {
			return "redirect:new";
		}
		return this.taskInfo(taskId, entity, name, profile, model);
	}

	/**
	 * Transform the form data in data valuable for the specific task.
	 * 
	 * @param string
	 * @param map
	 * @return
	 */
	protected abstract Map<String, Object> generateOutputForForm(String string,
			Map<String, String> map);

	/**
	 * The view prefix is used it find the .ftl to show the task form. It will
	 * always try with <prefix>tasl.ftl
	 * 
	 * @return
	 */
	protected abstract String getViewPrefix();

	/**
	 * Concrete classes can fill the model with some specific data for it.
	 * 
	 * @param model
	 */
	protected abstract void addCustomFormLogic(Model model);

	/**
	 * Validates a given form.
	 * 
	 * @param formSubmittedData
	 * @throws FormValidationException
	 */
	protected void validate(Map<String, String> formSubmittedData)
			throws FormValidationException {
		// DEFAULT DOES NOTHING
	}
}
