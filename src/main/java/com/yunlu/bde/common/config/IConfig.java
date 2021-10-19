package com.yunlu.bde.common.config;

import java.util.Properties;

public interface IConfig {
    boolean hasKey(String var1);

    void set(String var1, String var2);

    String get(String var1);

    String get(String var1, String var2);

    void remove(String var1);

    Properties getAll();
}
