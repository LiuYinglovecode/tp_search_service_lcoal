package com.yunlu.bde.dao;

import com.yunlu.bde.common.db.hbase.HbaseClient;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TracePartsDao extends BaseDao{
    private static final String TABLE_NAME = "traceparts_v1";
    private static final String PIC_TABLE_NAME = "traceparts_pic_v1";
    private static final String CAD_TABLE_NAME = "traceparts_cad_v1";
    private static final String PIC_COLUMN_NAME = "value";
    private static final String CAD_COLUMN_NAME = "value";
    private static final String TYPE_COLUMN_NAME = "type";
    private static final String FAMILY_NAME = "t";

    public TracePartsDao() {
    }

    public static Map<String, String> getTracePartsInfoById(String id) {
        return getData("traceparts_v1", "t", id);
    }

    public static byte[] getPicById(String id) {
        HbaseClient client = HbaseClient.getInstance();

        try {
            Map<String, byte[]> data = client.getData("traceparts_pic_v1", "t", id);
            byte[] result = (byte[])data.get("value");
            return result;
        } catch (IOException var4) {
            LOGGER.error(var4.getMessage());
            return null;
        }
    }

    public static Map<String, String> getTracepartsInfobyResId(String resId) {
        return getData("traceparts_v1", "t", resId);
    }

    public static String getPicTypeById(String id) {
        HbaseClient client = HbaseClient.getInstance();

        try {
            Map<String, byte[]> data = client.getData("traceparts_pic_v1", "t", id);
            byte[] result = (byte[])data.get("type");
            String type = Bytes.toString(result);
            return type;
        } catch (IOException var5) {
            LOGGER.error(var5.getMessage());
            return null;
        }
    }

    public static byte[] getCadById(String id) {
        HbaseClient client = HbaseClient.getInstance();

        try {
            Map<String, byte[]> data = client.getData("traceparts_cad_v1", "t", id);
            byte[] result = (byte[])data.get("value");
            return result;
        } catch (IOException var4) {
            LOGGER.error(var4.getMessage());
            return null;
        }
    }

    public static String getCadTypeById(String id) {
        HbaseClient client = HbaseClient.getInstance();

        try {
            Map<String, byte[]> data = client.getData("traceparts_cad_v1", "t", id);
            byte[] result = (byte[])data.get("type");
            String type = Bytes.toString(result);
            return type;
        } catch (IOException var5) {
            LOGGER.error(var5.getMessage());
            return null;
        }
    }

    public static boolean uploadCad(Map<String, HashMap<String, byte[]>> datas) {
        HbaseClient client = HbaseClient.getInstance();

        try {
            client.insertData("traceparts_cad_v1", "t", datas);
            return true;
        } catch (IOException var3) {
            LOGGER.error(var3.getMessage());
            return false;
        }
    }

    public static boolean uploadPic(Map<String, HashMap<String, byte[]>> datas) {
        HbaseClient client = HbaseClient.getInstance();

        try {
            client.insertData("traceparts_pic_v1", "t", datas);
            return true;
        } catch (IOException var3) {
            LOGGER.error(var3.getMessage());
            return false;
        }
    }

    public static boolean addTPData(HashMap<String, Serializable> datas) {
        if (datas != null) {
            try {
                String rowKey = ((Serializable)datas.get("id")).toString();
                if (StringUtils.isNotEmpty(rowKey)) {
                    HbaseClient.getInstance().insertData("traceparts_v1", "t", rowKey, datas);
                    return true;
                }
            } catch (Exception var2) {
                LOGGER.error(var2.getMessage());
            }
        }

        return false;
    }

//    public static boolean deleteTPData(String id) {
//        if (StringUtils.isNotEmpty(id)) {
//            try {
//                TracePartsSearchDao.deleteTPdata(id);
//                HbaseClient.getInstance().deleteData("traceparts_v1", id);
//                return true;
//            } catch (Exception var2) {
//                LOGGER.error(var2.getMessage());
//            }
//        }
//
//        return false;
//    }
}
