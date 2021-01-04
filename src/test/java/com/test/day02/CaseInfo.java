package com.test.day02;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Mr吴
 * @date 2020/12/16  21:54
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CaseInfo {

    @Excel(name = "序号(caseId)")
    private Integer caseId;

    @Excel(name = "接口模块(interface)")
    private String interfaceName;

    @Excel(name = "用例标题(title)")
    private String title;

    @Excel(name = "请求头(request_head)")
    private String requestHead;

    @Excel(name = "请求方式(method)")
    private String method;

    @Excel(name = "接口地址(url)")
    private String url;

    @Excel(name = "参数输入(inputParams)")
    private String inputParams;

    @Excel(name = "期望返回结果(expected)")
    private String expected;

}
