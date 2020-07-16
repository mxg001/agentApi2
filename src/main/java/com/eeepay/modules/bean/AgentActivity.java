package com.eeepay.modules.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author tgh
 * @description 欢乐返活动
 * @date 2019/7/2
 */
@Data
public class AgentActivity {
    private Long id;
    private String activityTypeNo;// 欢乐返子类型编号
    private String activityTypeName;// 欢乐返子类型名称
    private String activityCode;// 欢乐返类型
    private BigDecimal transAmount;// 交易金额
    private String agentNo;
    private String agentNode;
    private BigDecimal cashBackAmount;// 返现金额
    private BigDecimal taxRate;// 税额百分比
    private Date createTime;// 创建时间
    private String remark;
    private Date currentTime;//返回当前时间
    private BigDecimal repeatRegisterAmount;
    private BigDecimal repeatRegisterRatio;
    private BigDecimal fullPrizeAmount;
    private BigDecimal notFullDeductAmount;
    private BigDecimal repeatFullPrizeAmount;
    private BigDecimal repeatNotFullDeductAmount;
    private boolean showFullPrizeAmount;
    private boolean showNotFullDeductAmount;
}
