package com.yunlu.bde.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunlu.bde.service.ITPSearchService;
import com.yunlu.bde.util.SearchServiceException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/tpsearch"})
@ComponentScan(basePackages = {"com.yunlu.bde.service.impl"})
public class TPSearchController {
    private static final Logger logger = LogManager.getLogger(TPSearchController.class);

    @Autowired
    ITPSearchService tpSearchService;

//    @GetMapping({"traceparts"})
//    public Map<String, Object> getRealData(@RequestParam("t") String keyword, @RequestParam("num") Integer num) {
//        logger.info("search param: " + keyword + " size :" + num);
//        Integer code = Integer.valueOf(0);
//        if (StringUtils.isNotEmpty(keyword)) {
//            List<Map<String, String>> result_list = this.tpSearchService.searchTraceParts(keyword, num.intValue());
//            return buildResult(code.intValue(), result_list);
//        }
//
//        throw new SearchServiceException(1, "param.is.null");
//    }



//    @GetMapping({"tracepartsByCategory"})
//    public Map<String, Object> getTracepartsDataByPageNum(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestParam("levelnum") Integer levelnum, @RequestParam("keyword") String keyword, @RequestParam("category") String category) {
//        logger.debug("search levelnum: " + levelnum + "search keyword: " + keyword + "search pageNum: " + pageNum + " pageSize: " + pageSize + " catetory:" + category);
//
//
//        Integer code = Integer.valueOf(0);
//        if (StringUtils.isNotEmpty(category) && StringUtils.isNotEmpty(keyword)) {
//            List<Map<String, String>> result_list = this.tpSearchService.searchTracePartsByPageNum(pageSize.intValue(), pageNum.intValue(), levelnum.intValue(), category, keyword);
//            return buildResult(code.intValue(), result_list);
//        }
//        throw new SearchServiceException(1, "param.is.null");
//    }


    @GetMapping({"info"})
    public Map<String, Object> getPatentInfo(@RequestParam("id") String id) {
        logger.info("search param: " + id);
        Integer code = Integer.valueOf(0);
        if (StringUtils.isNotEmpty(id)) {
            Map<String, String> result_map = this.tpSearchService.getTracepartsInfo(id);
            return buildResult_Object(code.intValue(), result_map);
        }
        throw new SearchServiceException(1, "param.is.null");
    }


//    @GetMapping({"tracepartsByProductName"})
//    public Map<String, Object> searchTracepartsByProductName(@RequestParam("productName") String productName, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
//        logger.info("search param: " + productName);
//        Integer code = Integer.valueOf(0);
//        if (StringUtils.isNotEmpty(productName)) {
//            List<Map<String, String>> result_map = this.tpSearchService.searchTracepartsByProductName(productName, pageSize.intValue(), pageNum.intValue());
//            return buildResult_Object(code.intValue(), result_map);
//        }
//        throw new SearchServiceException(1, "param.is.null");
//    }


//    @GetMapping({"tracepartsByProductId"})
//    public Map<String, Object> searchTracepartsByProductId(@RequestParam("productId") String productId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
//        logger.info("search param: " + productId);
//        Integer code = Integer.valueOf(0);
//        if (StringUtils.isNotEmpty(productId)) {
//            List<Map<String, String>> result_map = this.tpSearchService.searchTracepartsByProductId(productId, pageSize.intValue(), pageNum.intValue());
//            return buildResult_Object(code.intValue(), result_map);
//        }
//        throw new SearchServiceException(1, "param.is.null");
//    }


//    @GetMapping({"delete"})
//    public Map<String, Object> delete(@RequestParam("id") String id) {
//        logger.info("search param: " + id);
//        Integer code = Integer.valueOf(0);
//        if (StringUtils.isNotEmpty(id)) {
//            boolean result = this.tpSearchService.deleteTPData(id);
//            return buildResult_Object(code.intValue(), Boolean.valueOf(result));
//        }
//        throw new SearchServiceException(1, "param.is.null");
//    }



//    @PostMapping({"addUpdateTracepartData"})
//    public Map<String, Object> addTracepartData_(@RequestParam("tracepartData") String tracepartData) {
//        boolean isOK = false;
//        int code = 0;
//        HashMap<String, Serializable> map = new HashMap<>();
//        Map<String, Object> data = new HashMap<>();
//        ObjectMapper mapper = new ObjectMapper();
//        if (StringUtils.isNotEmpty(tracepartData)) {
//
//            try {
//                tracepartData = URLDecoder.decode(tracepartData, "UTF-8");
//                logger.info("tracepartData : " + tracepartData);
//
//                Map<String, Object> tmpMap = (Map<String, Object>)mapper.readValue(tracepartData, Map.class);
//                if (tmpMap != null && tmpMap.size() > 0) {
//                    if (!tmpMap.containsKey("id")) {
//                        throw new SearchServiceException(1, "ID is null");
//                    }
//                    for (Map.Entry<String, Object> entry : tmpMap.entrySet()) {
//                        String key = entry.getKey();
//                        Object val = entry.getValue();
//                        if (StringUtils.isNotEmpty(key) &&
//                                val != null) {
//                            map.put(key, val.toString());
//                            data.put(key, val.toString());
//                        }
//                    }
//                }
//
//                isOK = this.tpSearchService.addUpdateTPData(data, map);
//                if (!isOK) {
//                    code = 1;
//
//                }
//            }
//            catch (Exception e) {
//                logger.error(e.getMessage());
//            }
//        } else {
//            throw new SearchServiceException(1, "tracepartData is null");
//        }
//        return buildResult_Object(code, Boolean.valueOf(isOK));
//    }



    @PostMapping({"uploadPic"})
    public Map<String, Object> uploadPic(@RequestParam("file") MultipartFile file, @RequestParam("picID") String picID, @RequestParam("picType") String picType) {
        int code = 0;
        boolean isOK = false;
        try {
            Map<String, HashMap<String, byte[]>> datas = new HashMap<>();
            HashMap<String, byte[]> dataContent = (HashMap)new HashMap<>();

            if (file != null && !file.isEmpty()) {
                ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4028];
                int readInfo = 0;
                InputStream fio = file.getInputStream();
                while ((readInfo = fio.read(buffer)) != -1) {
                    byteArrayInputStream.write(buffer, 0, readInfo);
                }
                byteArrayInputStream.flush();
                byte[] file_content = byteArrayInputStream.toByteArray();
                fio.close();
                byteArrayInputStream.close();

                dataContent.put("value", file_content);
            } else {
                dataContent.put("value", new byte[0]);
            }

            if (StringUtils.isNotEmpty(picType)) {
                dataContent.put("type", picType.getBytes());
            } else {
                dataContent.put("type", new byte[0]);
            }

            if (StringUtils.isNotEmpty(picID)) {
                datas.put(picID, dataContent);
                isOK = this.tpSearchService.uploadPic(datas);
                if (!isOK) {
                    code = 1;
                }
            } else {

                throw new SearchServiceException(1, "picID is null");
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new SearchServiceException(1, "other.error");
        }

        return buildResult_Object(code, Boolean.valueOf(isOK));
    }


    @PostMapping({"uploadCard"})
    public Map<String, Object> uploadCard(@RequestParam("file") MultipartFile file, @RequestParam("cardID") String cardID, @RequestParam("cardType") String cardType) {
        int code = 0;
        boolean isOK = false;

        try {
            Map<String, HashMap<String, byte[]>> datas = new HashMap<>();
            HashMap<String, byte[]> dataContent = (HashMap)new HashMap<>();
            if (file != null) {
                ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4028];
                int readInfo = 0;
                InputStream fio = file.getInputStream();
                while ((readInfo = fio.read(buffer)) != -1) {
                    byteArrayInputStream.write(buffer, 0, readInfo);
                }
                byteArrayInputStream.flush();
                byte[] file_content = byteArrayInputStream.toByteArray();
                file.getInputStream().close();
                byteArrayInputStream.close();

                dataContent.put("value", file_content);
            } else {
                dataContent.put("value", new byte[0]);
            }
            if (StringUtils.isNotEmpty(cardType)) {
                dataContent.put("type", cardType.getBytes());
            } else {
                dataContent.put("type", new byte[0]);
            }
            if (StringUtils.isNotEmpty(cardID)) {
                datas.put(cardID, dataContent);
                isOK = this.tpSearchService.uploadCad(datas);
                if (!isOK) {
                    code = 1;
                }
            } else {

                throw new SearchServiceException(1, "cardID is null");
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new SearchServiceException(1, "other.error");
        }
        return buildResult_Object(code, Boolean.valueOf(isOK));
    }

    @GetMapping({"pic"})
    public String getPic(@RequestParam("id") String id, HttpServletRequest request, HttpServletResponse response) {
        logger.debug("search param: " + id);
        if (StringUtils.isNotEmpty(id)) {
            ServletOutputStream servletOutputStream = null; String pic_file = id;
            Map<String, Object> picInfo = this.tpSearchService.getPic(id);
            byte[] picData = (byte[])picInfo.get("pic");
            if (picData == null) {
                throw new SearchServiceException(1, "the result is null");
            }
            String type = (String)picInfo.get("type");
            pic_file = pic_file + "." + type;
            OutputStream os = null;
            try {
                response.setContentType("application/force-download");
                response.addHeader("Content-Disposition", "attachment;fileName=" + new String(pic_file.getBytes("gbk"), "iso-8859-1"));
                servletOutputStream = response.getOutputStream();
                servletOutputStream.write(picData);
                logger.info("success");
            }
            catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
            }
            catch (Exception e) {
                logger.error(e.getMessage());
            } finally {

                if (servletOutputStream != null) {
                    try {
                        servletOutputStream.close();
                    }
                    catch (Exception e) {
                        logger.error(e.getMessage());
                    }

                }
            }
        } else {

            throw new SearchServiceException(1, "param.is.null");
        }
        return null;
    }








    public Map<String, Object> buildResult_Object(int code, Object result) {
        Map<String, Object> result_map = new HashMap<>();
        result_map.put("code", Integer.valueOf(code));
        result_map.put("result", result);
        return result_map;
    }








    public Map<String, Object> buildResult(int code, List<Map<String, String>> result_list) {
        Map<String, Object> result_map = new HashMap<>();
        String totalAmount = "";
        result_map.put("code", Integer.valueOf(code));
        if (result_list != null && result_list.size() > 0) {
            Map<String, String> value_temp = result_list.get(0);
            totalAmount = value_temp.get("totalAmount");
            for (Map<String, String> map : result_list) {
                map.remove("totalAmount");
            }
            result_map.put("totalAmount", Long.valueOf(Long.parseLong(totalAmount)));
        } else {
            result_map.put("totalAmount", Integer.valueOf(0));
        }
        result_map.put("result", result_list);
        return result_map;
    }

    @GetMapping({"card"})
    public String getCard(@RequestParam("id") String id, HttpServletRequest request, HttpServletResponse response) {
        logger.debug("search param: " + id);
        if (StringUtils.isNotEmpty(id)) {
            ServletOutputStream servletOutputStream = null; String card_file = id;
            Map<String, Object> cardInfo = this.tpSearchService.getCad(id);
            byte[] cadData = (byte[])cardInfo.get("cad");
            if (cadData == null) {
                throw new SearchServiceException(2, "the result is null");
            }
            String type = (String)cardInfo.get("type");
            card_file = card_file + "." + type;
            OutputStream os = null;
            try {
                response.setContentType("application/force-download");
                response.addHeader("Content-Disposition", "attachment;fileName=" + new String(card_file.getBytes("gbk"), "iso-8859-1"));
                servletOutputStream = response.getOutputStream();
                servletOutputStream.write(cadData);
                logger.info("success");
            }
            catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
            }
            catch (Exception e) {
                logger.error(e.getMessage());
            } finally {

                if (servletOutputStream != null) {
                    try {
                        servletOutputStream.close();
                    }
                    catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        } else {

            throw new SearchServiceException(1, "param.is.null");
        }
        return null;
    }
}
