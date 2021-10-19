package com.yunlu.bde.common.config.impl;

import com.yunlu.bde.common.config.IConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class InMemConfig implements IConfig {
    protected Properties properties;

    public InMemConfig() {
        this.properties = new Properties();
    }

    public InMemConfig(String configFile) throws IOException {
        this.properties = new Properties();
        File confFile = new File(configFile);
        if (!confFile.exists()) {
            throw new IllegalArgumentException("config file not exists");
        } else {
            Properties prop = new Properties();
            prop.load(new FileInputStream(confFile));
            Set<Object> keys = prop.keySet();
            Iterator var5 = keys.iterator();

            while(var5.hasNext()) {
                Object key = var5.next();
                this.properties.setProperty(String.valueOf(key), String.valueOf(prop.get(key)));
            }

        }
    }

    public InMemConfig(IConfig config) {
        this(config.getAll());
    }

    public InMemConfig(Properties prop) {
        this.properties = new Properties();
        Set<Object> keys = prop.keySet();
        Iterator var3 = keys.iterator();

        while(var3.hasNext()) {
            Object key = var3.next();
            this.properties.setProperty(String.valueOf(key), String.valueOf(prop.get(key)));
        }

    }

    public void set(String key, String value) {
        this.properties.setProperty(key, value);
    }

    public String get(String key) {
        return this.properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        if (this.properties.containsKey(key)) {
            return this.properties.getProperty(key);
        } else {
            this.properties.setProperty(key, defaultValue);
            return defaultValue;
        }
    }

    public void remove(String key) {
        this.properties.remove(key);
    }

    public Properties getAll() {
        return this.properties;
    }

    public boolean hasKey(String key) {
        return this.properties.containsKey(key);
    }
}
