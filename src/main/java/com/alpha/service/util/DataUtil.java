package com.alpha.service.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/*
 * Created by: fkusu
 * Date: 12/16/2024
 */
@Component
public class DataUtil {

    @Autowired
    ServiceTool serviceTool;

    @Autowired
    public DataUtil(Environment env) {
        this.serviceTool = new ServiceTool(env);
    }

    public final String getPathUpload() {
        String urlPath = "";
        if (serviceTool.getActiveProfile().equalsIgnoreCase(Constants.SYSTEM_ENVIRONMENT_TYPE.PRODUCTION.getValue())) {
            urlPath = serviceTool.getProperty("path.url.folder.upload");
        } else {
            urlPath = serviceTool.getProperty("path.url.folder.upload");
        }
        return urlPath;
    }
}
