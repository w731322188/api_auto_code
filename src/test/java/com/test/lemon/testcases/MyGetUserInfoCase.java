package com.test.lemon.testcases;

import com.test.lemon.base.BaseCase;
import com.test.lemon.data.GlobalEnvironment;
import com.test.lemon.entity.CaseInfo;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

/**
 * @author Mr吴
 * @date 2020/12/16  21:53
 */
public class MyGetUserInfoCase extends BaseCase {


    public List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp() {
        caseInfoList = getCaseInfoList(2);
        //参数化excel替换
        caseInfoList = paramReplace(caseInfoList);
    }

    @Test(dataProvider = "getUserInfoData")
    public void testGetUserInfo(CaseInfo caseInfo) {
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(), caseInfo.getCaseId());
        //把Excel中的requestHeaders（Json字符串）转换成map
        Map<String, Object> headerMap = parseJsonToMap(caseInfo.getRequestHeader());
        Response res =
                given().
                        log().all().
                        headers(headerMap).
                when().
                        get(caseInfo.getUrl()).
                then().
                        log().all().
                        extract().response();

        addLogToAllure(logFilePath);

        //断言
        assertExpected(caseInfo.getExpected(), res);
    }

    @DataProvider
    public Object[] getUserInfoData() {
        return caseInfoList.toArray();
    }


    public static void main(String[] args) {
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
        while (matcher.find()) {
            findStr = matcher.group(0);
            singleStr = matcher.group(1);
        }
        Object replaceStr = GlobalEnvironment.envData.get(singleStr);
        System.out.println(sourceStr.replace(findStr, replaceStr + ""));
    }


}
