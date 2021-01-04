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
import java.util.Objects;

import static io.restassured.RestAssured.given;

/**
 * @author Mr吴
 * @date 2020/12/26  21:10
 */
public class AddLoanCase extends BaseCase {

    public List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp() {
        caseInfoList = getCaseInfoList(4);
        //参数化excel替换
        caseInfoList = paramReplace(caseInfoList);
    }

    @Test(dataProvider = "getAddLoanData")
    public void testAddLoan(CaseInfo caseInfo) {
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(), caseInfo.getCaseId());
        Map<String, Object> headerMap = parseJsonToMap(caseInfo.getRequestHeader());
        Response res =
                given().
                        log().all().
                        //设置rest-assured返回的json数据中的小数设置为BigDecimal类型
                        headers(headerMap).
                        body(caseInfo.getInputParams()).
                when().
                        post(caseInfo.getUrl()).
                then().
                        log().all().
                        extract().response();
        addLogToAllure(logFilePath);

        Object loanId = res.path("data.id");
//        if (loanId != null){
//            GlobalEnvironment.envData.put("loan_id", loanId);
//        }
        if (Objects.equals(caseInfo.getCaseId(),1)) {
            GlobalEnvironment.envData.put("loan_id1", loanId + "");
        }else if (Objects.equals(caseInfo.getCaseId(),2)) {
            GlobalEnvironment.envData.put("loan_id2", loanId + "");
        }else if (Objects.equals(caseInfo.getCaseId(),3)) {

            GlobalEnvironment.envData.put("loan_id3", loanId + "");
        }
        //断言
        assertExpected(caseInfo.getExpected(), res);
    }


    @DataProvider
    public Object[] getAddLoanData() {
        return caseInfoList.toArray();
    }
}
