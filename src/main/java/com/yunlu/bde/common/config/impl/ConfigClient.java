package com.yunlu.bde.common.config.impl;

import com.yunlu.bde.common.config.IConfig;
import com.yunlu.bde.common.config.IConfigChangeListener;
import com.yunlu.bde.common.config.IConfigManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConfigClient implements Watcher, IConfigManager {
    public static final int kDefaultZKTimeout = 300000;
    public static final String kDefaultZKConfigPath = "conf/zookeeper.properties";
    public static final String kDefaultZKConnectionString = "172.17.60.108:2181";
    public static final String kDefaultConfigRoot = "/conf_yunlu.bde";
    private static Logger logger = LoggerFactory.getLogger(ConfigClient.class);
    private static final Object lockobj = new Object();
    private static ConfigClient configClient = null;
    private String zkConnectionString;
    private ZooKeeper zooKeeper = null;
    private boolean readOnly = true;
    private Map<String, IConfig> configMap;

    public ConfigClient(String hostPort, boolean readOnly) throws IOException {
        this.configMap = new HashMap();
        this.zooKeeper = new ZooKeeper(hostPort, 300000, this);
        this.zkConnectionString = hostPort;
        this.readOnly = readOnly;
    }

    public ConfigClient(String hostPort) throws IOException {
        this.zkConnectionString = hostPort;
        this.configMap = new HashMap();
        this.zooKeeper = new ZooKeeper(hostPort, 300000, this);
    }

    public static synchronized ConfigClient initConfigClient(String zkHostAndPort) {
        if (configClient != null) {
            configClient.close();
            configClient = null;
        }

        try {
            configClient = new ConfigClient(zkHostAndPort);
        } catch (IOException var2) {
            logger.error("create config client error", var2);
            var2.printStackTrace();
        }

        return configClient;
    }

    public static synchronized ConfigClient initConfigClient(String zkHostAndPort, boolean readOnly) {
        if (configClient != null) {
            configClient.close();
            configClient = null;
        }

        try {
            configClient = new ConfigClient(zkHostAndPort, readOnly);
        } catch (IOException var3) {
            logger.error("create config client error", var3);
            var3.printStackTrace();
        }

        return configClient;
    }

    public static synchronized ConfigClient instance() {
        if (configClient == null) {
            try {
                String zkQuorum = loadZooKeeperQuorum();
                configClient = new ConfigClient(zkQuorum);
            } catch (IOException var1) {
                logger.error("create config client error", var1);
                var1.printStackTrace();
            }
        }

        return configClient;
    }

    private static String loadZooKeeperQuorum() {
        String zkQuorum = System.getProperty("yunlu.bde.zookeeper");
        if (StringUtils.isNotEmpty(zkQuorum)) {
            System.out.println("when zkQuorum is not empty: "+zkQuorum);
            return zkQuorum;
        } else {
            zkQuorum = System.getenv("yunlu.bde.zookeeper");
            if (StringUtils.isNotEmpty(zkQuorum)) {
                System.out.println("2nd try when zkQuorum is not empty: "+zkQuorum);
                return zkQuorum;
            } else {
                File confFile = new File("conf/zookeeper.properties");
                if (confFile.exists()) {
                    Properties prop = new Properties();

                    try {
                        prop.load(new FileInputStream(confFile));
                        zkQuorum = prop.getProperty("yunlu.bde.zookeeper");
                        if (zkQuorum != null && !zkQuorum.isEmpty()) {
                            return zkQuorum;
                        }
                    } catch (FileNotFoundException var6) {
                        logger.error("loadZooKeeperQuorum error", var6);
                    } catch (IOException var7) {
                        logger.error("loadZooKeeperQuorum error", var7);
                    }
                }

                InputStream propertiesStream = ConfigClient.class.getResourceAsStream("/app.properties");
                if (propertiesStream != null) {
                    Properties p = new Properties();

                    try {
                        p.load(propertiesStream);
                    } catch (IOException var5) {
                        var5.printStackTrace();
                    }

                    zkQuorum = p.getProperty("yunlu.bde.zookeeper");
                    if (zkQuorum != null && !zkQuorum.isEmpty()) {
                        return zkQuorum;
                    }
                }

                if (zkQuorum == null || zkQuorum.isEmpty()) {
                    zkQuorum = "172.17.60.108:2181";
                }
                System.out.println("3rd try when zkQuorum is not empty: "+zkQuorum);
                return zkQuorum;
            }
        }
    }

    public synchronized void close() {
        if (this.zooKeeper != null) {
            try {
                this.zooKeeper.close();
            } catch (InterruptedException var2) {
                logger.error("close config client error", var2);
                var2.printStackTrace();
            }

            this.zooKeeper = null;
        }

    }

    public IConfig config(String configRoot) {
        IConfig config = null;
        synchronized(lockobj) {
            if (this.configMap.containsKey(configRoot)) {
                config = (IConfig)this.configMap.get(configRoot);
            } else {
                // for local purpose
                ZKConfig zkConfig = new ZKConfig("/" + configRoot + "-unsecure", this.zooKeeper);
//                ZKConfig zkConfig = new ZKConfig("/conf_yunlu.bde/" + configRoot, this.zooKeeper);
                zkConfig.setReadOnly(this.readOnly);
                this.configMap.put(configRoot, zkConfig);
                config = zkConfig;
            }

            System.out.println("config from ConfigClient: " + config);
            return (IConfig)config;
        }
    }

    public String get(String configRoot, String key) {
        IConfig config = this.config(configRoot);
        return config.get(key);
    }

    public int getIntValue(String configRoot, String key, int defaultValue) {
        IConfig config = this.config(configRoot);
        String value = config.get(key);
        int result = defaultValue;

        try {
            if (StringUtils.isNotEmpty(value)) {
                result = Integer.parseInt(value);
            }
        } catch (NumberFormatException var8) {
            var8.printStackTrace();
        }

        return result;
    }

    public String get(String configRoot, String key, String defaultValue) {
        IConfig config = this.config(configRoot);
        String res = config.get(key);
        if (res == null || res.isEmpty()) {
            res = defaultValue;
        }

        return res;
    }

    public void set(String configRoot, String key, String value) {
        IConfig config = this.config(configRoot);
        config.set(key, value);
    }

    public void process(WatchedEvent event) {
        String CONF = "/conf_yunlu.bde/";
        String path = event.getPath();
        logger.debug("process event " + path + " with " + event.getType());
        if (!StringUtils.isEmpty(path) && path.startsWith("/conf_yunlu.bde/")) {
            int startIndex = "/conf_yunlu.bde/".length();
            int endIndex = path.indexOf("/", "/conf_yunlu.bde/".length());
            if (endIndex != -1) {
                String configRoot = path.substring(startIndex, endIndex);
                String configKey = path.substring(endIndex + 1);
                IConfig config;
                if (event.getType() == Event.EventType.NodeDataChanged) {
                    synchronized(lockobj) {
                        if (this.configMap.containsKey(configRoot)) {
                            config = (IConfig)this.configMap.get(configRoot);
                            Object stat = null;

                            try {
                                String value = new String(this.zooKeeper.getData(path, true, (Stat)stat), StandardCharsets.UTF_8);
                                if (config instanceof IConfigChangeListener) {
                                    ((IConfigChangeListener)config).OnValueChanged(configKey, value);
                                }
                            } catch (KeeperException var15) {
                                logger.error("process event set config error", var15);
                                var15.printStackTrace();
                            } catch (InterruptedException var16) {
                                logger.error("process event set config error", var16);
                                var16.printStackTrace();
                            }
                        }
                    }
                } else if (event.getType() == Event.EventType.NodeDeleted) {
                    synchronized(lockobj) {
                        config = (IConfig)this.configMap.get(configRoot);
                        if (config instanceof IConfigChangeListener) {
                            ((IConfigChangeListener)config).OnKeyRemoved(configKey);
                        }
                    }
                }

            }
        }
    }

    public Map getPropertiesMap() {
        HashMap map = new HashMap();

        try {
            List<String> configRoot = this.zooKeeper.getChildren("/conf_yunlu.bde", false);
            Iterator var3 = configRoot.iterator();

            while(var3.hasNext()) {
                String root = (String)var3.next();
                Map<String, String> insideMap = new HashMap();
                Properties prop = instance().config(root).getAll();

                String key;
                String value;
                for(Iterator itr = prop.entrySet().iterator(); itr.hasNext(); insideMap.put(key, value)) {
                    Map.Entry<?, ?> e = (Map.Entry)itr.next();
                    key = (String)e.getKey();
                    value = (String)e.getValue();
                    if (key.startsWith("sec.") && value != null && !"".equals(value.trim())) {
                        if (value.length() > 2) {
                            char start = value.charAt(0);
                            char end = value.charAt(value.length() - 1);
                            value = start + "**********" + end;
                        } else {
                            value = "*";
                        }
                    }
                }

                map.put(root, insideMap);
            }
        } catch (KeeperException var13) {
            var13.printStackTrace();
        } catch (InterruptedException var14) {
            var14.printStackTrace();
        }

        return map;
    }

    public String getZkConnectionString() {
        return this.zkConnectionString;
    }
}
