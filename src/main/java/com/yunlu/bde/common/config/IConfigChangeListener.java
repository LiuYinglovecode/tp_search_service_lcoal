package com.yunlu.bde.common.config;

public interface IConfigChangeListener {
    void OnValueChanged(String var1, String var2);

    void OnKeyRemoved(String var1);
}
