package com.yunlu.bde.common.config;

public interface IConfigManager {
    String DEFUALT_CONFIG_PROPERTY = "yunlu.bde.zookeeper";

    String get(String var1, String var2, String var3);

    String get(String var1, String var2);

    void set(String var1, String var2, String var3);

    IConfig config(String var1);
}
