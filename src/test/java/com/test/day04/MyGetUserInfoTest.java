package com.test.day04;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.day02.CaseInfo;
import com.test.day03.GlobalEnvironment;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

/**
 * @author Mr吴
 * @date 2020/12/16  21:53
 */
public class MyGetUserInfoTest {


    public List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp(){
        caseInfoList = getCaseInfoList(2);
        //参数化excel替换
        caseInfoList = paramReplace(caseInfoList);
    }

    @Test(dataProvider = "getUserInfoData")
    public void testGetUserInfo(CaseInfo caseInfo) throws JsonProcessingException {
        //把Excel中的requestHeaders（Json字符串）转换成map
        ObjectMapper objectMapper = new ObjectMapper();
        Map headerMap = objectMapper.readValue(caseInfo.getRequestHead(), Map.class);
        String url = caseInfo.getUrl();
        String expected = caseInfo.getExpected();
        Response res =
                given().
                        headers(headerMap).
                when().
                        get("http://api.lemonban.com/futureloan" + url).
                then().
                        extract().response();
        //断言
        //把数据转换为map
        Map expectedMap = objectMapper.readValue(expected, Map.class);
        Set<Map.Entry<String,Object>> set = expectedMap.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            String key = entry.getKey();
            Object expectedValue =  entry.getValue();
            Object actualValue = res.path(key);
            Assert.assertEquals(actualValue, expectedValue);
        }
    }

    @DataProvider
    public Object[] getUserInfoData(){
        return caseInfoList.toArray();
    }

    public List<CaseInfo> getCaseInfoList(int index) {
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(index);
        importParams.setSheetNum(1);
        File file = new File("src/test/resources/api_testcases_futureloan_v1.xls");
        return ExcelImportUtil.importExcel(file, CaseInfo.class, importParams);
    }


    public String regexReplace(String sourceStr, String newStr){
        //参数化替换功能替换
        //正则表达式：
        //"." 匹配任意的字符
        //"*" 匹配前面的字符零次或者任意次数
        //"?" 贪婪匹配
        // .*?
        //1、定义正则表达式
        String regex = "\\{\\{(.*?)\\}\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sourceStr);
        String findStr = "";
        //连续查找、连续匹配
        while (matcher.find()){
            findStr = matcher.group(0);
        }
        return sourceStr.replace(findStr, newStr);
    }

    public String regexReplace(String sourceStr){
        //参数化替换功能替换
        //正则表达式：
        //"." 匹配任意的字符
        //"*" 匹配前面的字符零次或者任意次数
        //"?" 贪婪匹配
        // .*?
        //1、定义正则表达式
        String regex = "\\{\\{(.*?)\\}\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sourceStr);
        String findStr = "";
        String singleStr = "";
        //连续查找、连续匹配
        while (matcher.find()){
            findStr = matcher.group(0);
            singleStr = matcher.group(1);
        }
        Object replaceStr = GlobalEnvironment1.envData.get(singleStr);
        return sourceStr.replace(findStr, replaceStr + "");
    }


    public static void main(String[] args){
        String sourceStr = "{\"X-Lemonban-Media-Type\":\"lemonban.v2\",\"Content-Type\":\"application/json\",\"Authorization\":\"Bearer {{token}}\"}";
        //参数化替换功能替换
        //正则表达式：
        //"." 匹配任意的字符
        //"*" 匹配前面的字符零次或者任意次数
        //"?" 贪婪匹配
        // .*?
        //1、定义正则表达式
        String regex = "\\{\\{(.*?)\\}\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sourceStr);
        String findStr = "";
        String singleStr = "";
        //连续查找、连续匹配
        while (matcher.find()){
            findStr = matcher.group(0);
            singleStr = matcher.group(1);
        }
        Object replaceStr = GlobalEnvironment1.envData.get(singleStr);
        System.out.println(sourceStr.replace(findStr,  replaceStr + ""));
    }


    public List<CaseInfo> paramReplace(List<CaseInfo> caseInfoList){
        for (CaseInfo caseInfo : caseInfoList) {
            if (caseInfo.getRequestHead() != null){
                String requestHead = regexReplace(caseInfo.getRequestHead());
                caseInfo.setRequestHead(requestHead);
            }
            if (caseInfo.getUrl() != null){
                String url = regexReplace(caseInfo.getUrl());
                caseInfo.setUrl(url);
            }
            if (caseInfo.getInputParams() != null){
                String inputParams = regexReplace(caseInfo.getInputParams());
                caseInfo.setInputParams(inputParams);
            }
            if (caseInfo.getExpected() != null){
                String expected = regexReplace(caseInfo.getExpected());
                caseInfo.setExpected(expected);
            }

        }

        return caseInfoList;
    }
}
