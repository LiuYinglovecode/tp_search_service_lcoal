package com.yunlu.bde.common.db.hbase;

import com.yunlu.bde.common.config.impl.ConfigClient;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.exceptions.IllegalArgumentIOException;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class HbaseClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HbaseClient.class);
    public static final byte[] POSTFIX = new byte[]{0};
    public static final int RegionCount = 10000;
    private Connection connection;
    private Configuration _config;
    private static HashMap<String, HbaseClient> _instances = new HashMap();

    public static HbaseClient getInstance() {
        return getInstance(null);
    }

    public static HbaseClient getInstance(String hbaseName) {
        if (hbaseName == null || hbaseName.length() == 0) {
            hbaseName = "hbase";
        }

        if (!_instances.containsKey(hbaseName)) {
            synchronized(_instances) {
                if (!_instances.containsKey(hbaseName)) {
                    HbaseClient client = new HbaseClient(hbaseName);
                    _instances.put(hbaseName, client);
                }
            }
        }


        return _instances.get(hbaseName);
    }

    public HbaseClient(String hbaseName) {
        this.init(hbaseName);
    }

    public void init() {
        this.init("hbase");
    }

    public void init(String propertyPath) {
        this._config = HBaseConfiguration.create();
        // the four lines below is for local dev service
        this._config.set("hbase.zookeeper.quorum","master,slave1,slave2,slave3,slave4");
        this._config.set("hbase.zookeeper.property.clientPort","2181");
        this._config.set("hbase.defaults.for.version.skip","true");
        this._config.set("zookeeper.znode.parent","/hbase-unsecure");


        Properties properties = ConfigClient.instance().config(propertyPath).getAll();
        Iterator iterator = properties.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry)iterator.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            this._config.set(key, value);
        }

        try {
            this.connection = ConnectionFactory.createConnection(this._config);
        } catch (IOException var7) {
            var7.printStackTrace();
        }

    }

    public void insertData(String tableName, String familyName, String rowKey, Map<String, Serializable> data) throws IOException {
        ArrayList<String> columns = new ArrayList();
        ArrayList<Serializable> values = new ArrayList();
        Iterator var7 = data.entrySet().iterator();

        while(var7.hasNext()) {
            Map.Entry<String, Serializable> d = (Map.Entry)var7.next();
            columns.add(d.getKey());
            values.add(d.getValue());
        }

        this.insertData(tableName, familyName, rowKey, columns, values);
    }

    public void insertData(String tableName, String familyName, List<String> rowKeys, List<String> columns, List<List<Serializable>> valueList) throws IOException {
        if (columns != null && columns.size() != 0) {
            if (valueList != null && valueList.size() != 0) {
                if (((List)valueList.get(0)).size() == columns.size() && valueList.size() == rowKeys.size()) {
                    Table table = this.connection.getTable(TableName.valueOf(tableName));
                    List<Put> puts = new ArrayList(valueList.size());
                    int dataCount = rowKeys.size();

                    for(int i = 0; i < dataCount; ++i) {
                        String rowKey = (String)rowKeys.get(i);
                        List<Serializable> values = (List)valueList.get(i);
                        Put put = new Put(Bytes.toBytes(rowKey));

                        for(int j = 0; j < values.size(); ++j) {
                            Serializable value = (Serializable)values.get(j);
                            if (value != null) {
                                byte[] bytes = toBytes(value);
                                put.add(Bytes.toBytes(familyName), Bytes.toBytes((String)columns.get(j)), bytes);
                            }
                        }

                        puts.add(put);
                    }

                    table.put(puts);

                    try {
                        table.close();
                    } catch (IOException var16) {
                        LOGGER.error(var16.getMessage());
                    }

                } else {
                    throw new IllegalArgumentIOException("insertData paramater length not equal");
                }
            } else {
                throw new IllegalArgumentIOException("insertData valueList");
            }
        } else {
            throw new IllegalArgumentIOException("insertData columns");
        }
    }

    public void insertData(String tableName, String familyName, Map<String, HashMap<String, byte[]>> datas) throws IOException {
        Table table = this.connection.getTable(TableName.valueOf(tableName));
        List<Put> puts = new ArrayList(datas.size());
        byte[] family = Bytes.toBytes(familyName);
        Iterator var7 = datas.entrySet().iterator();

        while(var7.hasNext()) {
            Map.Entry<String, HashMap<String, byte[]>> data = (Map.Entry)var7.next();
            String rowKey = (String)data.getKey();
            Put put = new Put(Bytes.toBytes(rowKey));
            Iterator var11 = ((HashMap)data.getValue()).entrySet().iterator();

            while(var11.hasNext()) {
                Map.Entry<String, byte[]> val = (Map.Entry)var11.next();
                put.addColumn(family, Bytes.toBytes((String)val.getKey()), (byte[])val.getValue());
            }

            puts.add(put);
        }

        table.put(puts);

        try {
            table.close();
        } catch (IOException var13) {
            var13.printStackTrace();
        }

    }

    public void insertData(String tableName, String familyName, String rowKey, ArrayList<String> columns, ArrayList<Serializable> values) throws IOException {
        if (columns != null && columns.size() != 0) {
            if (values != null && values.size() == columns.size()) {
                List<List<Serializable>> valueList = new ArrayList(1);
                valueList.add(values);
                List<String> rowKeys = new ArrayList(1);
                rowKeys.add(rowKey);
                this.insertData(tableName, familyName, (List)rowKeys, (List)columns, (List)valueList);
            } else {
                throw new IllegalArgumentIOException("insertData values");
            }
        } else {
            throw new IllegalArgumentIOException("insertData columns");
        }
    }

    public void deleteData(String tableName, String rowKey) throws IOException {
        Table table = this.connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        table.delete(delete);
    }

    public HashMap<String, byte[]> getData(String tableName, String familyName, String rowKey) throws IOException {
        Table table = this.connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes(familyName));
        Result result = table.get(get);
        HashMap resultValues = getDataFromResult(result);

        try {
            table.close();
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        return resultValues;
    }

    public ResultScanner fullScan(String tableName, String familyName) throws IOException {
        Table table = this.connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes(familyName));
        return table.getScanner(scan);
    }

    public List<HashMap<String, byte[]>> prefixScan(String tableName, String familyName, String prefix, String startRow, String endRow) throws IOException {
        List<HashMap<String, byte[]>> resultData = null;
        Table table = this.connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        if (prefix != null && prefix.length() > 0) {
            FilterList filterList = new FilterList(new Filter[0]);
            filterList.addFilter(new PrefixFilter(Bytes.toBytes(prefix)));
            scan.setFilter(filterList);
        }

        scan.addFamily(Bytes.toBytes(familyName));
        scan.setStartRow(Bytes.toBytes(startRow));
        scan.setStopRow(Bytes.toBytes(endRow));
        ResultScanner resultScanner = table.getScanner(scan);
        if (resultScanner == null) {
            try {
                table.close();
            } catch (IOException var12) {
                var12.printStackTrace();
            }

            return null;
        } else {
            resultData = new ArrayList();

            while(true) {
                Result result = resultScanner.next();
                if (result == null) {
                    resultScanner.close();

                    try {
                        table.close();
                    } catch (IOException var13) {
                        var13.printStackTrace();
                    }

                    return resultData;
                }

                HashMap<String, byte[]> resultValues = getDataFromResult(result);
                resultData.add(resultValues);
            }
        }
    }

    public Map<String, HashMap<String, byte[]>> pagingScan(String tableName, String familyName, String startRow, StringBuilder lastRowBuidler, int pageSize) throws IOException {
        return this.pagingScan(tableName, familyName, (String)null, startRow, lastRowBuidler, pageSize);
    }

    public Map<String, HashMap<String, byte[]>> pagingScan(String tableName, String familyName, String columnName, String startRow, StringBuilder lastRowBuidler, int pageSize) throws IOException {
        List<String> columnNames = null;
        if (null != columnName) {
            columnNames = new ArrayList(1);
            columnNames.add(columnName);
        }

        return this.pagingScan(tableName, familyName, (List)columnNames, startRow, lastRowBuidler, pageSize);
    }

    public Map<String, HashMap<String, byte[]>> pagingScan(String tableName, String familyName, List<String> columnNames, String startRow, StringBuilder lastRowBuidler, int pageSize) throws IOException {
        Map<String, HashMap<String, byte[]>> resultData = null;
        Connection conn = ConnectionFactory.createConnection(this._config);
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        if (columnNames != null && columnNames.size() > 0) {
            Iterator var11 = columnNames.iterator();

            while(var11.hasNext()) {
                String columnName = (String)var11.next();
                scan.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
            }
        } else {
            scan.addFamily(Bytes.toBytes(familyName));
        }

        if (pageSize > 0) {
            scan.setCaching(pageSize);
        }

        if (!StringUtils.isEmpty(startRow)) {
            scan.setStartRow(Bytes.add(Bytes.toBytes(startRow), POSTFIX));
        }

        ResultScanner resultScanner = table.getScanner(scan);
        if (resultScanner == null) {
            table.close();
            conn.close();
            return null;
        } else {
            resultData = new HashMap();
            byte[] lastRow = null;
            int localRows = 0;

            do {
                Result result = resultScanner.next();
                if (result == null) {
                    break;
                }

                lastRow = result.getRow();
                HashMap<String, byte[]> resultValues = getDataFromResult(result);
                resultData.put(Bytes.toString(lastRow), resultValues);
                ++localRows;
            } while(localRows < pageSize);

            resultScanner.close();
            table.close();
            conn.close();
            if (lastRow != null) {
                lastRowBuidler.append(new String(lastRow, "UTF-8"));
            }

            return resultData;
        }
    }

    public Map<String, HashMap<String, byte[]>> pagingScanWithFilter(String tableName, String familyName, List<String> columnNames, String startRow, StringBuilder lastRowBuidler, int pageSize, FilterList filterList) throws IOException {
        Map<String, HashMap<String, byte[]>> resultData = null;
        Connection conn = ConnectionFactory.createConnection(this._config);
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        if (columnNames != null && columnNames.size() > 0) {
            Iterator var12 = columnNames.iterator();

            while(var12.hasNext()) {
                String columnName = (String)var12.next();
                scan.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
            }
        } else {
            scan.addFamily(Bytes.toBytes(familyName));
        }

        if (null != filterList) {
            scan.setFilter(filterList);
        }

        if (pageSize > 0) {
            scan.setCaching(pageSize);
        }

        if (!StringUtils.isEmpty(startRow)) {
            scan.setStartRow(Bytes.add(Bytes.toBytes(startRow), POSTFIX));
        }

        ResultScanner resultScanner = table.getScanner(scan);
        if (resultScanner == null) {
            table.close();
            conn.close();
            return null;
        } else {
            resultData = new HashMap();
            byte[] lastRow = null;
            int localRows = 0;

            do {
                Result result = resultScanner.next();
                if (result == null) {
                    break;
                }

                lastRow = result.getRow();
                HashMap<String, byte[]> resultValues = getDataFromResult(result);
                resultData.put(Bytes.toString(lastRow), resultValues);
                ++localRows;
            } while(localRows < pageSize);

            resultScanner.close();
            table.close();
            conn.close();
            if (lastRow != null) {
                lastRowBuidler.append(new String(lastRow, "UTF-8"));
            }

            return resultData;
        }
    }

    public static HashMap<String, byte[]> getDataFromResult(Result result) {
        if (result == null) {
            return null;
        } else {
            HashMap<String, byte[]> resultValues = new HashMap();
            List<Cell> cells = result.listCells();
            if (cells != null && cells.size() > 0) {
                Iterator var3 = cells.iterator();

                while(var3.hasNext()) {
                    Cell cell = (Cell)var3.next();
                    String columnName = Bytes.toString(CellUtil.cloneQualifier(cell));
                    byte[] columnValue = CellUtil.cloneValue(cell);
                    resultValues.put(columnName, columnValue);
                }
            }

            return resultValues;
        }
    }

    public boolean exists(String tableName, String familyName, String rowKey) throws IOException {
        Table table = null;
        table = this.connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes(familyName));
        Result result = null;
        result = table.get(get);
        boolean exists = false;
        if (result != null) {
            exists = result.getExists();
        }

        try {
            table.close();
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        return exists;
    }

    public String getData(String tableName, String familyName, String rowKey, String columnName) throws IOException {
        String columnValue = null;
        Table table = this.connection.getTable(TableName.valueOf(tableName));
        Throwable var7 = null;

        try {
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
            Result result = null;
            result = table.get(get);
            if (result != null && result.size() > 0) {
                byte[] value = result.getValue(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
                if (value.length > 0) {
                    columnValue = Bytes.toString(value);
                }
            }
        } catch (Throwable var18) {
            var7 = var18;
            throw var18;
        } finally {
            if (table != null) {
                if (var7 != null) {
                    try {
                        table.close();
                    } catch (Throwable var17) {
                        var7.addSuppressed(var17);
                    }
                } else {
                    table.close();
                }
            }

        }

        return columnValue;
    }

    public static String getStopRow(String rowKey) {
        if (rowKey != null && rowKey.length() != 0) {
            char lastChar = rowKey.charAt(rowKey.length() - 1);
            String stopRow = null;
            if (rowKey.length() > 1) {
                char nextChar = (char)(lastChar + 1);
                stopRow = rowKey.substring(0, rowKey.length() - 1) + nextChar;
            } else {
                stopRow = (new char[]{lastChar}).toString();
            }

            return stopRow;
        } else {
            throw new IllegalArgumentException("getStopRow rowKey");
        }
    }

    public static String getRowKey(long id) {
        return String.format("%04d_%d", id % 10000L, id / 10000L);
    }

    public static byte[] toBytes(Serializable value) throws IOException {
        byte[] bytes;
        if (value instanceof Byte) {
            bytes = new byte[]{(Byte)value};
        } else if (value instanceof Short) {
            bytes = Bytes.toBytes((Short)value);
        } else if (value instanceof Integer) {
            bytes = Bytes.toBytes((Integer)value);
        } else if (value instanceof Long) {
            bytes = Bytes.toBytes((Long)value);
        } else if (value instanceof Boolean) {
            bytes = Bytes.toBytes((Boolean)value);
        } else if (value instanceof String) {
            bytes = Bytes.toBytes((String)value);
        } else if (value instanceof Float) {
            bytes = Bytes.toBytes((Float)value);
        } else if (value instanceof Double) {
            bytes = Bytes.toBytes((Double)value);
        } else if (value instanceof byte[]) {
            bytes = (byte[])((byte[])value);
        } else {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(value);
            bytes = byteStream.toByteArray();
        }

        return bytes;
    }
}
