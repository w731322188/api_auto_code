package com.test.lemon.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.lemon.base.BaseCase;
import com.test.lemon.data.GlobalEnvironment;
import com.test.lemon.entity.CaseInfo;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;

/**
 * @author Mr吴
 * @date 2020/12/16  21:53
 */
public class MyLoginCase extends BaseCase {

    public List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp() {
        caseInfoList = getCaseInfoList(1);
        //参数化用例
        caseInfoList = paramReplace(caseInfoList);
    }

    @Test(dataProvider = "getExcelData")
    public void testLogin(CaseInfo caseInfo) throws JsonProcessingException {
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

        String token = res.path("data.token_info.token");
        if (Objects.equals(caseInfo.getCaseId(), 1)) {
            GlobalEnvironment.envData.put("token1", token);
        } else if (Objects.equals(caseInfo.getCaseId(), 2)) {
            GlobalEnvironment.envData.put("token2", token);
        } else if (Objects.equals(caseInfo.getCaseId(), 3)) {
            GlobalEnvironment.envData.put("token3", token);
        }else if (Objects.equals(caseInfo.getCaseId(), 4)) {
            GlobalEnvironment.envData.put("token4", token);
        }

        //断言
        assertExpected(caseInfo.getExpected(), res);

//        Integer memberId = res.path("data.id");
//        if (memberId != null){
//            GlobalEnvironment.envData.put("member_id", memberId);
//            GlobalEnvironment.envData.put("token", token);
//        }
    }

    @DataProvider
    public Object[] getExcelData() {
        return caseInfoList.toArray();
    }


}
