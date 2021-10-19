package com.yunlu.bde.common.config.impl;

import com.yunlu.bde.common.config.IConfigChangeListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class ZKConfig extends InMemConfig implements IConfigChangeListener {
    private static Logger logger = LoggerFactory.getLogger(ZKConfig.class);
    private ZooKeeper zooKeeper;
    private String configRoot;
    private boolean readOnly = true;

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public ZKConfig(String configRoot, ZooKeeper zk) {
        this.zooKeeper = zk;
        this.configRoot = configRoot;
    }

    public synchronized void set(String key, String value) {
        super.set(key, value);
        if (!this.readOnly) {
            Stat res = null;

            try {
                String path = this.configRoot + "/" + key;
                res = this.zooKeeper.exists(path, true);
                if (res == null) {
                    this.createNodeRecursivly(path);
                    res = this.zooKeeper.exists(path, true);
                }

                if (res != null) {
                    System.out.println("output path: " + path);
                    this.zooKeeper.setData(path, value.getBytes(StandardCharsets.UTF_8), -1);
                }

            } catch (KeeperException var5) {
                logger.error("set config error", var5);
            } catch (InterruptedException var6) {
                logger.error("set config error", var6);
            }
        }
    }

    public synchronized String get(String key) {
        if (super.hasKey(key)) {
            return super.get(key);
        } else {
            try {
                String path = this.configRoot + "/" + key;
                Stat res = this.zooKeeper.exists(path, false);
                if (res != null) {
                    String value = new String(this.zooKeeper.getData(path, true, res), StandardCharsets.UTF_8);
                    super.set(key, value);
                    return value;
                }
            } catch (KeeperException var5) {
                logger.error("get config error", var5);
            } catch (InterruptedException var6) {
                logger.error("get config error", var6);
            }

            return "";
        }
    }

    public synchronized String get(String key, String defaultValue) {
        if (super.hasKey(key)) {
            return super.get(key);
        } else {
            String config = this.get(key);
            if (StringUtils.isEmpty(config)) {
                config = defaultValue;
                super.set(key, defaultValue);
            }

            return config;
        }
    }

    public synchronized void remove(String key) {
        super.remove(key);
        if (!this.readOnly) {
            try {
                String path = this.configRoot + "/" + key;
                if (this.zooKeeper.exists(path, false) != null) {
                    this.zooKeeper.delete(path, -1);
                    return;
                }
            } catch (KeeperException var3) {
                logger.error("remove key error", var3);
            } catch (InterruptedException var4) {
                logger.error("remove key error", var4);
            }

        }
    }

    public Properties getAll() {
        Properties prop = new Properties();

        try {
            Stat res = this.zooKeeper.exists(this.configRoot, false);
            if (res != null) {
                List<String> children = this.zooKeeper.getChildren(this.configRoot, false);
                Iterator var4 = children.iterator();

                while(var4.hasNext()) {
                    String child = (String)var4.next();
                    String childPath = this.configRoot;
                    if (!childPath.endsWith("/")) {
                        childPath = childPath + "/";
                    }

                    childPath = childPath + child;
                    byte[] buffer = this.zooKeeper.getData(childPath, true, res);
                    if (buffer != null) {
                        String value = new String(buffer, StandardCharsets.UTF_8);
                        if (value != null) {
                            prop.setProperty(child, value);
                        }
                    }
                }
            }
        } catch (Exception var10) {
            logger.error("get all config error", var10);
        }

        Properties cacheProp = super.getAll();
        Enumeration enums = cacheProp.keys();

        while(enums.hasMoreElements()) {
            Object key = enums.nextElement();
            prop.setProperty((String)key, (String)cacheProp.get(key));
        }

        System.out.println("this is getAll() returned properties: "+prop);
        return prop;
    }

    private void createNodeRecursivly(String path) {
        try {
            logger.debug("begin create znode: " + path);
            if (path.length() <= 0 || this.zooKeeper.exists(path, false) != null) {
                return;
            }

            String temp = path.substring(0, path.lastIndexOf("/"));
            this.createNodeRecursivly(temp);
            this.zooKeeper.create(path, (byte[])null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException var3) {
            logger.error("createNodeRecursivly error", var3);
        } catch (InterruptedException var4) {
            logger.error("createNodeRecursivly error", var4);
        }

    }

    public synchronized void OnValueChanged(String key, String value) {
        super.set(key, value);
    }

    public synchronized void OnKeyRemoved(String key) {
        super.remove(key);
    }

    public boolean hasKey(String key) {
        boolean result = super.hasKey(key);
        if (result) {
            return result;
        } else {
            String path = this.configRoot + "/" + key;

            try {
                Stat res = this.zooKeeper.exists(path, false);
                result = res != null;
            } catch (KeeperException var5) {
                var5.printStackTrace();
            } catch (InterruptedException var6) {
                var6.printStackTrace();
            }

            return result;
        }
    }
}
