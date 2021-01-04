package com.test.lemon.testcases;

import com.test.lemon.base.BaseCase;
import com.test.lemon.entity.CaseInfo;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * @author Mr吴
 * @date 2020/12/31  19:39
 */
public class InvestCase extends BaseCase {


    public List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp() {
        caseInfoList = getCaseInfoList(6);
        //参数化excel替换
        caseInfoList = paramReplace(caseInfoList);
    }

    @Test(dataProvider = "getInvestData")
    public void testInvest(CaseInfo caseInfo) {
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(), caseInfo.getCaseId());
        //把Excel中的requestHeaders（Json字符串）转换成map
        Map<String, Object> headerMap = parseJsonToMap(caseInfo.getRequestHeader());
        Response res =
                given().
                        log().all().
                        headers(headerMap).
                        body(caseInfo.getInputParams()).
                when().
                        post(caseInfo.getUrl()).
                then().
                        log().all().
                        extract().response();

        addLogToAllure(logFilePath);

        //断言
        assertExpected(caseInfo.getExpected(), res);
    }

    @DataProvider
    public Object[] getInvestData() {
        return caseInfoList.toArray();
    }
}
