package com.yunlu.bde.server;

import com.yunlu.bde.search.util.PropertiesTool;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class InitServer
{
    @PostConstruct
    public void initParamters() {
        System.setProperty("yunlu.bde.zookeeper", PropertiesTool.getzkCon());
    }
}
