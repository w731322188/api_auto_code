package com.test.lemon.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.lemon.base.BaseCase;
import com.test.lemon.data.GlobalEnvironment;
import com.test.lemon.entity.CaseInfo;
import com.test.lemon.util.PhoneRandomUtil;
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
 * @date 2020/12/21  22:17
 */
public class RegisterCase extends BaseCase {


    public List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp() {
        caseInfoList = getCaseInfoList(0);
    }

    @Test(dataProvider = "getExcelData")
    public void testRegister(CaseInfo caseInfo) throws JsonProcessingException {

        Integer caseId = caseInfo.getCaseId();
        if (Objects.equals(caseId, 1)) {
            //随机生成没有被注册的手机号码
            String phone = PhoneRandomUtil.getNoRegisterPhone();
            //放到环境变量中
            GlobalEnvironment.envData.put("mobile_phone1", phone);
        } else if (Objects.equals(caseId, 2)) {
            //随机生成没有被注册的手机号码
            String phone = PhoneRandomUtil.getNoRegisterPhone();
            //放到环境变量中
            GlobalEnvironment.envData.put("mobile_phone2", phone);
        } else if (Objects.equals(caseId, 3)) {
            //随机生成没有被注册的手机号码
            String phone = PhoneRandomUtil.getNoRegisterPhone();
            //放到环境变量中
            GlobalEnvironment.envData.put("mobile_phone3", phone);
        }
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(), caseId);
        //参数化替换case
        caseInfo = paramReplace(caseInfo);
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
        //Allure添加日志文件
        addLogToAllure(logFilePath);
        Map inputParamMap = parseJsonToMap(caseInfo.getInputParams());
        Object pwd = inputParamMap.get("pwd");
        if (Objects.equals(caseId, 1)) {
            GlobalEnvironment.envData.put("mobile_phone1", res.path("data.mobile_phone") + "");
            GlobalEnvironment.envData.put("member_id1", res.path("data.id") + "");
            GlobalEnvironment.envData.put("pwd1", pwd + "");
        } else if (Objects.equals(caseId, 2)) {
            GlobalEnvironment.envData.put("mobile_phone2", res.path("data.mobile_phone") + "");
            GlobalEnvironment.envData.put("member_id2", res.path("data.id") + "");
            GlobalEnvironment.envData.put("pwd2", pwd + "");
        } else if (Objects.equals(caseId, 3)) {
            GlobalEnvironment.envData.put("mobile_phone3", res.path("data.mobile_phone") + "");
            GlobalEnvironment.envData.put("member_id3", res.path("data.id") + "");
            GlobalEnvironment.envData.put("pwd3", pwd + "");
        }

        //接口响应断言
        assertExpected(caseInfo.getExpected(), res);

        //数据库断言
        assertSql(caseInfo);
//        Integer memberId = res.path("data.id");
//        if (memberId != null) {
//            Object mobilePhone = res.path("data.mobile_phone");
//            GlobalEnvironment.envData.put("mobile_phone", mobilePhone + "");
//            Map inputParamMap = objectMapper.readValue(caseInfo.getInputParams(), Map.class);
//            Object pwd = inputParamMap.get("pwd");
//            GlobalEnvironment.envData.put("pwd", pwd + "");
//        }
    }


    @DataProvider
    public Object[] getExcelData() {
        return caseInfoList.toArray();
    }
}
