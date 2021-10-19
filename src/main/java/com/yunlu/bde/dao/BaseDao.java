package com.yunlu.bde.dao;

import com.yunlu.bde.common.db.hbase.HbaseClient;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseDao {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseDao.class);

    public BaseDao() {
    }

    protected static Map<String, String> getData(String tableName, String familyName, String id) {
        HbaseClient client = HbaseClient.getInstance();

        try {
            Map<String, byte[]> data = client.getData(tableName, familyName, id);
            Map<String, String> result = new HashMap();
            Iterator var6 = data.keySet().iterator();

            while(var6.hasNext()) {
                String columnName = (String)var6.next();
                String columnValue = Bytes.toString((byte[])data.get(columnName));
                result.put(columnName, columnValue);
            }

            return result;
        } catch (IOException var9) {
            LOGGER.error(var9.getMessage());
            return null;
        }
    }
}
