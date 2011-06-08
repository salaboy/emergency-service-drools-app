package com.wordpress.salaboy.emergencyservice.web.task.external;


import java.util.ArrayList;
import java.util.List;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Emergency.EmergencyType;
import com.wordpress.salaboy.smarttasks.formbuilder.api.ExternalData;

public class ModelDataConnector implements ExternalData {

    public List<String> emergencyTypes() {
        List<String> emergencyTypesList = new ArrayList<String>();
        EmergencyType[] types = Emergency.EmergencyType.values();
        for (int i = 0; i < types.length; i++) {
            EmergencyType emergencyType = types[i];
            emergencyTypesList.add(emergencyType.name());
        }
        return emergencyTypesList;
    }
}
