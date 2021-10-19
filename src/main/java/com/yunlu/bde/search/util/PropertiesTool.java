package com.yunlu.bde.search.util;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesTool {
    public PropertiesTool() {
    }

    public static String getzkCon() {
        InputStream propertiesStream = PropertiesTool.class.getResourceAsStream("/app.properties");
        if (propertiesStream != null) {
            Properties p = new Properties();

            try {
                p.load(propertiesStream);
            } catch (IOException var3) {
                var3.printStackTrace();
            }

            String zkConn = p.getProperty("web.zkConn");
            if (StringUtils.isNotEmpty(zkConn)) {
                return zkConn;
            }
        }

        return null;
    }

    public static String getProperityName(String p_name) {
        InputStream propertiesStream = PropertiesTool.class.getResourceAsStream("/app.properties");
        if (propertiesStream != null) {
            Properties p = new Properties();

            try {
                p.load(propertiesStream);
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            String p_value = p.getProperty(p_name);
            if (StringUtils.isNotEmpty(p_value)) {
                return p_value;
            }
        }

        return null;
    }

    public static void main(String... args) throws Exception {
        System.out.println(getzkCon());
    }
}
