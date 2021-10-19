//package com.yunlu.bde.dao;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.elasticsearch.client.transport.TransportClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class TracePartsSearchDao {
//    private static final Logger LOGGER = LoggerFactory.getLogger(TracePartsSearchDao.class);
//    private static final String INDEX_NAME = "bde_v1";
//    private static final String TYPE_NAME = "traceparts";
//    private static final String CATEGORY_FIELD_NAME = "tag";
//
//    public TracePartsSearchDao() {
//    }
//
//    public static List<Map<String, String>> searchTracePartsByPageNum(int pagesize, int pagenum, int levelNum, String category, String keyword) {
//        TransportClient client = (new ESClientBuilder()).createESClient().getClient();
//        SearchRequestBuilder searchRequest = client.prepareSearch(new String[]{"bde_v1"}).setTypes(new String[]{"traceparts"}).setSearchType(SearchType.QUERY_THEN_FETCH).setIndicesOptions(IndicesOptions.fromOptions(true, false, false, false)).setExplain(true);
//        BoolQueryBuilder query = QueryBuilders.boolQuery();
//        if (!"all".equals(category)) {
//            String fieldName = "level" + Integer.toString(levelNum);
//            query.must(QueryBuilders.matchQuery(fieldName, category));
//        }
//
//        if (!"all".equals(keyword)) {
//            query.must(QueryBuilders.matchQuery("tag", keyword));
//        }
//
//        SearchResponse response = (SearchResponse)searchRequest.setSource((new SearchSourceBuilder()).from(pagenum).query(query).from((pagenum - 1) * pagesize).size(pagesize)).addSort("partName", SortOrder.ASC).execute().actionGet();
//        List<Map<String, String>> result = new ArrayList();
//        ObjectMapper mapper = new ObjectMapper();
//        long count = response.getHits().getTotalHits();
//        SearchHit[] var13 = response.getHits().getHits();
//        int var14 = var13.length;
//
//        for(int var15 = 0; var15 < var14; ++var15) {
//            SearchHit hit = var13[var15];
//
//            try {
//                Map<String, String> source = (Map)mapper.readValue(hit.getSourceAsString(), Map.class);
//                source.put("totalAmount", Long.toString(count));
//                result.add(source);
//            } catch (IOException var18) {
//                LOGGER.error(var18.getMessage());
//            }
//        }
//
//        return result;
//    }
//
//    public static List<Map<String, String>> searchTraceParts(String keyword, int size) {
//        TransportClient client = (new ESClientBuilder()).createESClient().getClient();
//        SearchRequestBuilder searchRequest = client.prepareSearch(new String[]{"bde_v1"}).setTypes(new String[]{"traceparts"}).setSearchType(SearchType.QUERY_THEN_FETCH).setFrom(0).setSize(size).setExplain(true);
//        QueryBuilder query = QueryBuilders.matchQuery("tag", keyword);
//        SearchResponse response = (SearchResponse)searchRequest.setSource((new SearchSourceBuilder()).query(query).size(size)).execute().actionGet();
//        long count = response.getHits().getTotalHits();
//        List<Map<String, String>> result = new ArrayList();
//        ObjectMapper mapper = new ObjectMapper();
//        SearchHit[] var10 = response.getHits().getHits();
//        int var11 = var10.length;
//
//        for(int var12 = 0; var12 < var11; ++var12) {
//            SearchHit hit = var10[var12];
//
//            try {
//                Map<String, String> source = (Map)mapper.readValue(hit.getSourceAsString(), Map.class);
//                source.put("totalAmount", Long.toString(count));
//                result.add(source);
//            } catch (IOException var15) {
//                LOGGER.error(var15.getMessage());
//            }
//        }
//
//        return result;
//    }
//
//    public static boolean createTracePartsIndex(String jsonString) {
//        ObjectMapper mapper = new ObjectMapper();
//
//        try {
//            Map<String, Object> map = (Map)mapper.readValue(jsonString, Map.class);
//            String category = "";
//            if (map.containsKey("level1")) {
//                category = category + map.get("level1");
//            }
//
//            if (map.containsKey("level2")) {
//                category = category + map.get("level2");
//            }
//
//            if (map.containsKey("level3")) {
//                category = category + map.get("level3");
//            }
//
//            if (map.containsKey("level4")) {
//                category = category + map.get("level4");
//            }
//
//            if (map.containsKey("level5")) {
//                category = category + map.get("level5");
//            }
//
//            if (map.containsKey("level5")) {
//                category = category + map.get("level5");
//            }
//
//            if (map.containsKey("level6")) {
//                category = category + map.get("level6");
//            }
//
//            map.put("category", category);
//            TransportClient transportClient = (new ESClientBuilder()).createESClient().getClient();
//            transportClient.prepareIndex("bde_v1", "traceparts").setSource(map, XContentType.JSON).execute().actionGet();
//            return true;
//        } catch (IOException var5) {
//            LOGGER.error(var5.getMessage());
//            return false;
//        }
//    }
//
//    public static boolean deleteTPdata(String id) {
//        try {
//            TransportClient client = (new ESClientBuilder()).createESClient().getClient();
//            SearchRequestBuilder searchRequest = client.prepareSearch(new String[]{"bde_v1"}).setTypes(new String[]{"traceparts"}).setSearchType(SearchType.QUERY_THEN_FETCH).setExplain(true);
//            BoolQueryBuilder query = QueryBuilders.boolQuery();
//            query.must(QueryBuilders.matchQuery("id", id));
//            SearchResponse response = (SearchResponse)searchRequest.setSource((new SearchSourceBuilder()).query(query).size(10)).execute().actionGet();
//            SearchHit[] var5 = response.getHits().getHits();
//            int var6 = var5.length;
//
//            for(int var7 = 0; var7 < var6; ++var7) {
//                SearchHit hit = var5[var7];
//                DeleteResponse var9 = (DeleteResponse)client.prepareDelete("bde_v1", "traceparts", hit.getId()).execute().actionGet();
//            }
//
//            return true;
//        } catch (Exception var10) {
//            LOGGER.error(var10.getMessage());
//            return false;
//        }
//    }
//
//    public static boolean addTpdata(String id, Map<String, Object> data) {
//        try {
//            TransportClient client = (new ESClientBuilder()).createESClient().getClient();
//            SearchRequestBuilder searchRequest = client.prepareSearch(new String[]{"bde_v1"}).setTypes(new String[]{"traceparts"}).setSearchType(SearchType.QUERY_THEN_FETCH).setExplain(true);
//            BoolQueryBuilder query = QueryBuilders.boolQuery();
//            query.must(QueryBuilders.matchQuery("id", id));
//            SearchResponse response = (SearchResponse)searchRequest.setSource((new SearchSourceBuilder()).query(query).size(10)).execute().actionGet();
//            if (response.getHits().getHits().length == 0) {
//                CommonSearchDao.createIndex_v1("bde_v1", "traceparts", data);
//            }
//
//            return true;
//        } catch (Exception var6) {
//            LOGGER.error(var6.getMessage());
//            return false;
//        }
//    }
//}
