package com.yunlu.bde.service.impl;

import com.yunlu.bde.dao.TracePartsDao;
//import com.yunlu.bde.dao.TracePartsSearchDao;
import com.yunlu.bde.service.ITPSearchService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;

@Service
public class TPSearchServiceImpl implements ITPSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TPSearchServiceImpl.class);

    public TPSearchServiceImpl() {
    }

//    public List<Map<String, String>> searchTraceParts(String keyword, int size) {
//        return TracePartsSearchDao.searchTraceParts(keyword, size);
//    }

//    public List<Map<String, String>> searchTracePartsByPageNum(int pagesize, int pagenum, int levelNum, String category, String keyword) {
//        return TracePartsSearchDao.searchTracePartsByPageNum(pagesize, pagenum, levelNum, category, keyword);
//    }

    public Map<String, String> getTracepartsInfo(String id) {
        return TracePartsDao.getTracePartsInfoById(id);
    }

    public Map<String, Object> getPic(String id) {
        byte[] pic = TracePartsDao.getPicById(id);
        String type = TracePartsDao.getPicTypeById(id);
        Map<String, Object> result = new HashMap();
        result.put("pic", pic);
        result.put("type", type);
        return result;
    }

    public Map<String, Object> getCad(String id) {
        byte[] pic = TracePartsDao.getCadById(id);
        String type = TracePartsDao.getCadTypeById(id);
        Map<String, Object> result = new HashMap();
        result.put("cad", pic);
        result.put("type", type);
        return result;
    }

    public boolean uploadCad(Map<String, HashMap<String, byte[]>> datas) {
        return TracePartsDao.uploadCad(datas);
    }

    public boolean uploadPic(Map<String, HashMap<String, byte[]>> datas) {
        return TracePartsDao.uploadPic(datas);
    }

//    public boolean addUpdateTPData(Map<String, Object> data, HashMap<String, Serializable> datas) {
//        try {
//            TracePartsSearchDao.addTpdata(data.get("id").toString(), data);
//            TracePartsDao.addTPData(datas);
//            return true;
//        } catch (Exception var4) {
//            LOGGER.error(var4.getMessage());
//            return false;
//        }
//    }

//    public boolean deleteTPData(String id) {
//        return TracePartsDao.deleteTPData(id);
//    }

//    public List<Map<String, String>> searchTracepartsByProductName(String productName, int pageSize, int pageNum) {
//        StringBuilder cypher = (new StringBuilder("match(n:Product)-[r:hasCategory]->(m:Category) where n.name='")).append(productName).append("' return m;");
//        List<Map<String, String>> resList = Neo4jCommonDao.getNameAndResIdList(cypher.toString());
//        return this.getTraceparts(resList, pageSize, pageNum);
//    }

//    public List<Map<String, String>> searchTracepartsByProductId(String productId, int pageSize, int pageNum) {
//        StringBuilder cypher = (new StringBuilder("match(n:Product)-[r:hasCategory]->(m:Category) where n.id='")).append(productId).append("' return m;");
//        List<Map<String, String>> resList = Neo4jCommonDao.getNameAndResIdList(cypher.toString());
//        return this.getTraceparts(resList, pageSize, pageNum);
//    }

    private List<Map<String, String>> getTraceparts(List<Map<String, String>> resList, int pageSize, int pageNum) {
        int flag = 0;
        String categoryName = null;
        if (null != resList && resList.size() > 0) {
            Iterator var6 = resList.iterator();

            while(var6.hasNext()) {
                Map<String, String> map = (Map)var6.next();
                if (StringUtils.isNotEmpty((String)map.get("level")) && !"null".equals(map.get("level"))) {
                    int level = Integer.parseInt((String)map.get("level"));
                    if (flag <= level) {
                        flag = level;
                        categoryName = (String)map.get("name");
                    }
                }
            }

//            if (StringUtils.isNotEmpty(categoryName)) {
//                StringBuilder cypherSub = (new StringBuilder("match(n:Tarceparts)-[r:hasCategory]->(m:Category) where m.name='")).append(categoryName).append("' return n;");
//                List<Map<String, String>> tracepartsList = Neo4jCommonDao.getNameAndResIdList(cypherSub.toString());
//                if (null != tracepartsList && tracepartsList.size() > 0) {
//                    tracepartsList.subList(pageSize * (pageNum - 1), pageNum * pageSize > tracepartsList.size() ? tracepartsList.size() : pageNum * pageSize);
//                    List<Map<String, String>> tracepartsResList = new ArrayList();
//                    Iterator var9 = tracepartsList.iterator();
//
//                    while(var9.hasNext()) {
//                        Map<String, String> map = (Map)var9.next();
//                        String resId = (String)map.get("id");
//                        if (StringUtils.isNotEmpty(resId) && !"null".equals(resId)) {
//                            Map<String, String> res = TracePartsDao.getTracepartsInfobyResId(resId);
//                            tracepartsResList.add(res);
//                        }
//                    }
//
//                    return tracepartsResList;
//                }
//            }
        }

        return null;
    }
}
