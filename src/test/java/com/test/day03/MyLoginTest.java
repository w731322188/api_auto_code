package com.test.day03;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.day02.CaseInfo;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

/**
 * @author Mr吴
 * @date 2020/12/16  21:53
 */
public class MyLoginTest {

    public List<CaseInfo> caseInfoList;

    @BeforeTest
    public void setUp(){
        caseInfoList = getCaseInfoList(1);
    }

    @Test(dataProvider = "getExcelData")
    public void testLogin(CaseInfo caseInfo) throws JsonProcessingException {
        String inputParams = caseInfo.getInputParams();
        String requestHead = caseInfo.getRequestHead();
        String url = caseInfo.getUrl();
        //把Excel中的requestHeaders（Json字符串）转换成map
        ObjectMapper objectMapper = new ObjectMapper();
        Map headerMap = objectMapper.readValue(requestHead, Map.class);
        Response res =
                given().
                        headers(headerMap).
                        body(inputParams).
                when().
                        post("http://api.lemonban.com/futureloan" + url).
                then().
                        extract().response();
        //断言
        String expected = caseInfo.getExpected();
        //把数据转换为map
        Map expectedMap = objectMapper.readValue(expected, Map.class);
        Set<Map.Entry<String,Object>> set = expectedMap.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            String key = entry.getKey();
            Object expectedValue =  entry.getValue();
            Object actualValue = res.path(key);
            Assert.assertEquals(actualValue, expectedValue);
        }
        Integer memberId = res.path("data.id");
        String token = res.path("data.token_info.token");
        if (memberId != null){
            GlobalEnvironment.memberId = memberId;
            GlobalEnvironment.envData.put("member_id", memberId);
            GlobalEnvironment.envData.put("token", token);
        }
    }

    @DataProvider
    public Object[] getExcelData(){
        return caseInfoList.toArray();
    }

    public List<CaseInfo> getCaseInfoList(int index) {
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(index);
        importParams.setSheetNum(1);
        File file = new File("src/test/resources/api_testcases_futureloan.xls");
        return ExcelImportUtil.importExcel(file, CaseInfo.class, importParams);
    }


}
