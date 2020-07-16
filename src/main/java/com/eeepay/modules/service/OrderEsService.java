package com.eeepay.modules.service;

import com.eeepay.frame.bean.PageBean;
import com.eeepay.modules.bean.EsSearchBean;
import com.eeepay.modules.bean.KeyValueBean;
import com.eeepay.modules.bean.Tuple;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：订单业务层(ES)
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
public interface OrderEsService {

    /**
     * 汇总交易订单金额
     *
     * @param searchBean ES查询条件
     * @return 汇总金额
     */
    Tuple<Long, BigDecimal> sumTransAmountByTerms(EsSearchBean searchBean);

    /**
     * 交易分组统计
     * 单个字段分组
     *
     * @param searchBean ES查询条件
     * @return 分组统计结果：订单数量、订单金额
     */
    List<Map<String, Object>> groupSummaryTransOrder(EsSearchBean searchBean);

    /**
     * 交易查询
     *
     * @param searchBean ES查询条件
     * @return
     */
    PageBean queryTransOrderForPage(EsSearchBean searchBean);

    /**
     * 商户新增分组统计
     * 根据组织机构和欢乐返激活状态分组统计
     *
     * @param searchBean ES查询条件
     * @param teamType   组织类型，1：主组织，2：子组织
     * @return 分组统计结果：新增总商户数量，新增激活商户数量
     */
    List<Map<String, Object>> groupMerchantByTeamAndHlfActive(EsSearchBean searchBean, int teamType);

    /**
     * 查询近七日和半年的交易数据
     *
     * @param searchBean ES查询条件
     * @return v1 7日数据
     * v2 半年数据
     */
    Tuple<List<KeyValueBean>, List<KeyValueBean>> listSevenDayAndHalfYearDataTrend(EsSearchBean searchBean);
}
