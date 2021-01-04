package com.test.lemon.base;


import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.lemon.data.Constants;
import com.test.lemon.data.GlobalEnvironment;
import com.test.lemon.entity.CaseInfo;
import com.test.lemon.util.JDBCUtil;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @author Mr吴
 * @date 2020/12/21  22:15
 */
public class BaseCase {

    @BeforeTest
    public void globalSetUp() throws FileNotFoundException {
        //设置项目请求公共url
        RestAssured.baseURI = Constants.BASE_URL;
        //设置RestAssured 请求返回的json串中小数为BigDecimal类型的。
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //全局重定向输出日志到指定文件中
//        PrintStream fileOutPutStream = new PrintStream(new File("log/test_all.log"));
//        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream), new ResponseLoggingFilter(fileOutPutStream));

    }

    /**
     * 把日志重定向到对应的文件中去
     *
     * @param interfaceName
     * @param caseId
     * @return
     */
    public String addLogToFile(String interfaceName, int caseId) {
        String logFilePath = "";
        if (!Constants.IS_DEBUG) {
            //提前创建好目录层级 target/log/+interfaceName
            String dirPath = "target/log/" + interfaceName;
            File dirFile = new File(dirPath);
            //如果文件夹不存在就创建文件夹
            if (!dirFile.isDirectory()) {
                dirFile.mkdirs();
            }
            PrintStream fileOutPutStream = null;
            logFilePath = dirPath + "/" + interfaceName + "_" + caseId + ".log";

            try {
                fileOutPutStream = new PrintStream(logFilePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
        }
        return logFilePath;
    }

    public void addLogToAllure(String logFilePath) {
        if (!Constants.IS_DEBUG) {
            try {
                Allure.addAttachment("接口请求响应信息", new FileInputStream(logFilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把json字符串转换为Map
     *
     * @param jsonStr
     * @return
     */
    public Map<String, Object> parseJsonToMap(String jsonStr) {
        Map<String, Object> map = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            map = objectMapper.readValue(jsonStr, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 数据库断言
     *
     * @param caseInfo
     */
    public void assertSql(CaseInfo caseInfo) {
        String checkSql = caseInfo.getCheckSql();
        if (checkSql != null) {
            Map<String, Object> map = parseJsonToMap(caseInfo.getCheckSql());
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String sql = entry.getKey();
                Object expected = entry.getValue();
                Object actual = JDBCUtil.querySingle(sql);
                if (actual instanceof Long) {
                    Long expectedValue = new Long(expected.toString());
                    Assert.assertEquals(actual, expectedValue, "数据库断言失败");
                } else if (actual instanceof BigDecimal) {
                    BigDecimal expectedValue = new BigDecimal(expected.toString());
                    Assert.assertEquals(actual, expectedValue, "数据库断言失败");
                } else if (actual instanceof Boolean) {

                } else {
                    Assert.assertEquals(actual, expected, "数据库断言失败");
                }
            }
        }

    }

    /**
     * 接口响应断言
     *
     * @param expected
     * @param res
     */
    public void assertExpected(String expected, Response res) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map expectedMap = null;
        try {
            expectedMap = objectMapper.readValue(expected, Map.class);
            Set<Map.Entry<String, Object>> set = expectedMap.entrySet();
            for (Map.Entry<String, Object> entry : set) {
                String key = entry.getKey();
                Object expectedValue = entry.getValue();
                Object actualValue = res.path(key);
                if (expectedValue instanceof Double || expectedValue instanceof Float) {
                    //把excel中的期望值（小数类型）转换为BigDecimal类型的
                    BigDecimal bigDecimalData = new BigDecimal(expectedValue.toString());
                    Assert.assertEquals(actualValue, bigDecimalData, "接口响应断言失败");
                } else {
                    Assert.assertEquals(actualValue, expectedValue, "接口响应断言失败");
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }


    /**
     * 参数化多条case
     *
     * @param caseInfoList
     * @return
     */
    public List<CaseInfo> paramReplace(List<CaseInfo> caseInfoList) {
        for (CaseInfo caseInfo : caseInfoList) {

            String requestHead = regexReplace(caseInfo.getRequestHeader());
            caseInfo.setRequestHeader(requestHead);

            String url = regexReplace(caseInfo.getUrl());
            caseInfo.setUrl(url);

            String inputParams = regexReplace(caseInfo.getInputParams());
            caseInfo.setInputParams(inputParams);

            String expected = regexReplace(caseInfo.getExpected());
            caseInfo.setExpected(expected);

            String checkSql = regexReplace(caseInfo.getCheckSql());
            caseInfo.setCheckSql(checkSql);

        }
        return caseInfoList;
    }

    /**
     * 参数化case
     *
     * @param caseInfo
     * @return
     */
    public CaseInfo paramReplace(CaseInfo caseInfo) {

        String requestHead = regexReplace(caseInfo.getRequestHeader());
        caseInfo.setRequestHeader(requestHead);

        String url = regexReplace(caseInfo.getUrl());
        caseInfo.setUrl(url);

        String inputParams = regexReplace(caseInfo.getInputParams());
        caseInfo.setInputParams(inputParams);

        String expected = regexReplace(caseInfo.getExpected());
        caseInfo.setExpected(expected);

        String checkSql = regexReplace(caseInfo.getCheckSql());
        caseInfo.setCheckSql(checkSql);
        return caseInfo;
    }


    public String regexReplace(String sourceStr, String newStr) {
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
        while (matcher.find()) {
            findStr = matcher.group(0);
        }
        return sourceStr.replace(findStr, newStr);
    }

    public String regexReplace(String sourceStr) {
        if (sourceStr == null) {
            return null;
        }
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
        while (matcher.find()) {
            findStr = matcher.group(0);
            singleStr = matcher.group(1);
            Object replaceStr = GlobalEnvironment.envData.get(singleStr);
            sourceStr = sourceStr.replace(findStr, replaceStr + "");
        }
        return sourceStr;
    }


    public List<CaseInfo> getCaseInfoList(int index) {
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(index);
        importParams.setSheetNum(1);
        File file = new File(Constants.EXCEL_PATH);
        return ExcelImportUtil.importExcel(file, CaseInfo.class, importParams);
    }

//    public static void main(String[] args) {
//        String str = "{\n" +
//                "  \"mobile_phone\": \"{{mobile_phone}}\",\n" +
//                "  \"pwd\": \"{{pwd}}\"\n" +
//                "}";
//        String s = regexReplace(str);
//        System.out.println(s);
//    }
}
