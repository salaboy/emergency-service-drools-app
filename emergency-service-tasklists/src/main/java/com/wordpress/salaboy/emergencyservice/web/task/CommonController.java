package com.wordpress.salaboy.emergencyservice.web.task;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.yaml.snakeyaml.Yaml;

import com.wordpress.salaboy.smarttasks.formbuilder.api.ConnectionData;
import com.wordpress.salaboy.smarttasks.formbuilder.api.SmartTaskBuilder;
import com.wordpress.salaboy.smarttasks.formbuilder.api.output.TaskListsData;

/**
 * Controller that will resolve views for start page and task lists.
 * @author calcacuervo
 *
 */
@Controller
public class CommonController {

	/**
	 * The builder is inyected using spring.
	 */
    @Autowired
    private SmartTaskBuilder helper;

    Yaml yaml = new Yaml();

    @RequestMapping(value = "/")
    public String home(Model model) {
        return "redirect:/new/";
    }
    
    @RequestMapping(value = "/new/")
    public String start(Model model) {
        return "start";
    }

    /**
     * This method gets the task list using the UIHelper classes. TODO move it
     * to common!
     * 
     * @param entity
     * @param profile
     * @param model
     * @return
     */
    @RequestMapping(value = "/list/{entity}/{profile}", method = RequestMethod.GET)
    public String list(@PathVariable("entity") String entity,
            @PathVariable("profile") String profile, Model model) {
        helper.connect(new ConnectionData(entity));
        String stringTaskList = helper.getTaskList(profile);

        TaskListsData taskListdata = (TaskListsData) yaml.load(stringTaskList);
        Object[][] data = taskListdata.getData();
        model.addAttribute("data", data);

        String[] headers = taskListdata.getColumnHeaders();
        model.addAttribute("headers", headers);
        int idIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            String string = headers[i];
            if (string.equalsIgnoreCase("id")) {
                model.addAttribute("idIndex", i);
                idIndex = i;
            }
        }
       
        for (int i = 0; i < headers.length; i++) {
            String string = headers[i];
            if (string.equalsIgnoreCase("status")) {
                model.addAttribute("Idstatus", i);
            }
        }
        Map<String, String> taskNames = new HashMap();
        int nameIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            String string = headers[i];
            if (string.equalsIgnoreCase("description")) {
                nameIndex = i;
            }
        }
        for (int i = 0; i < data.length; i++) {
            Object[] allData = data[i];
            if (allData[idIndex] instanceof String) {
                taskNames.put((String) allData[idIndex],
                        ((String) allData[nameIndex]).replaceAll(" ", ""));
            }
        }
        model.addAttribute("taskNames", taskNames);
        model.addAttribute("user", entity);
        model.addAttribute("profile", profile);
        return "list";
    }

}
