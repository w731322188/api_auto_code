package com.test.day02;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import java.io.File;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * @author Mr吴
 * @date 2020/12/16  21:53
 */
public class MyLoginTest {

    @Test()
    public void testLogin(){
        Map<String, String> map = Maps.newHashMap();
        map.put("mobile_phone","18515669601");
        map.put("pwd","lemon666");
        given().
                header("X-Lemonban-Media-Type","lemonban.v1").
                body(map).contentType("application/json;charset=utf-8").
        when().
                post("http://api.lemonban.com/futureloan/member/login").
        then().
                log().body();
    }

    @Test(dataProvider = "getLoginData01")
    public void testLogin01(String url, String method, String contentType, String requestHeader, String jsonStr){
        given().
                header("X-Lemonban-Media-Type",requestHeader).
                body(jsonStr).contentType(contentType).
        when().
                post(url).
        then().
                log().body();
    }

    @DataProvider
    public Object[][] getLoginData01(){
        //1、请求接口地址 2、请求方式 3、请求头 4、请求数据
        Object[][] datas= {{"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\":\"13323231011\",\"pwd\":\"12345678\"}"},
                {"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\":\"133232310112\",\"pwd\":\"12345678\"}"},
                {"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\":\"1332323101a\",\"pwd\":\"12345678\"}"},
                {"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\":\"11015541764\",\"pwd\":\"12345678\"}"}
        };
        return datas;
    }

    @Test(dataProvider = "getExcelData")
    public void testLogin02(CaseInfo caseInfo) throws JsonProcessingException {
        String inputParams = caseInfo.getInputParams();
        String requestHead = caseInfo.getRequestHead();
        String url = caseInfo.getUrl();
//        String[] split = requestHead.split("\n");
//        Map<String, Object> requestHeaderMap = Maps.newHashMap();
//        for (String str : split){
//            String[] split1 = str.split(":");
//            requestHeaderMap.put(split1[0], split1[1]);
//        }
//        System.out.println(requestHeaderMap);
        ObjectMapper objectMapper = new ObjectMapper();
        Map headerMap = objectMapper.readValue(requestHead, Map.class);
        given().
                headers(headerMap).
                body(inputParams).
        when().
                post("http://api.lemonban.com/futureloan" + url).
        then().
                log().body();


    }

    @DataProvider
    public Object[] getExcelData(){
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(0);
        importParams.setSheetNum(2);
        File file = new File("src/test/resources/api_testcases_futureloan.xls");
        List<CaseInfo> data = ExcelImportUtil.importExcel(file, CaseInfo.class, importParams);
        return data.toArray();
    }



}
