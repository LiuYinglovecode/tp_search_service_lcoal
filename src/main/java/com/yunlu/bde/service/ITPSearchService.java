package com.yunlu.bde.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ITPSearchService {

//    List<Map<String, String>> searchTraceParts(String var1, int var2);

//    List<Map<String, String>> searchTracePartsByPageNum(int var1, int var2, int var3, String var4, String var5);

    Map<String, String> getTracepartsInfo(String var1);

    Map<String, Object> getPic(String var1);

    Map<String, Object> getCad(String var1);

    boolean uploadCad(Map<String, HashMap<String, byte[]>> var1);

    boolean uploadPic(Map<String, HashMap<String, byte[]>> var1);

//    boolean addUpdateTPData(Map<String, Object> var1, HashMap<String, Serializable> var2);

//    boolean deleteTPData(String var1);

//    List<Map<String, String>> searchTracepartsByProductName(String var1, int var2, int var3);

//    List<Map<String, String>> searchTracepartsByProductId(String var1, int var2, int var3);
}
