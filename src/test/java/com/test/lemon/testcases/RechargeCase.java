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
 * @date 2020/12/25  21:09
 */
public class RechargeCase extends BaseCase {


    public List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp() {
        caseInfoList = getCaseInfoList(3);
        //参数化excel替换
        caseInfoList = paramReplace(caseInfoList);
    }

    @Test(dataProvider = "getRechargeData")
    public void testRecharge(CaseInfo caseInfo) {
        //System.out.println(caseInfo);
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(), caseInfo.getCaseId());
        Map<String, Object> headerMap = parseJsonToMap(caseInfo.getRequestHeader());
        Response res =
                given().
                        log().all().
                        //设置rest-assured返回的json数据中的小数设置为BigDecimal类型
//                        config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
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
    public Object[] getRechargeData() {
        return caseInfoList.toArray();
    }
}
