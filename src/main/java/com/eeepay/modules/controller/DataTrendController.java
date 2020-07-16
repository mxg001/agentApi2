package com.eeepay.modules.controller;

import com.eeepay.frame.annotation.CurrentUser;
import com.eeepay.frame.annotation.SignValidate;
import com.eeepay.frame.annotation.SwaggerDeveloped;
import com.eeepay.frame.bean.ResponseBean;
import com.eeepay.frame.enums.EsNpospJoinType;
import com.eeepay.frame.enums.OrderTransStatus;
import com.eeepay.frame.enums.QueryScope;
import com.eeepay.frame.utils.StringUtils;
import com.eeepay.frame.utils.WebUtils;
import com.eeepay.frame.utils.swagger.OrderSwaggerNotes;
import com.eeepay.frame.utils.swagger.SwaggerNotes;
import com.eeepay.modules.bean.*;
import com.eeepay.modules.service.*;
import com.eeepay.modules.utils.EsSearchUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.eeepay.modules.bean.Tuple;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：数据趋势
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Slf4j
@RequestMapping("/data")
@Api(description = "数据模块")
@RestController
public class DataTrendController {

    @Resource
    private OrderEsService orderEsService;
    @Resource
    private OrderService orderService;
    @Resource
    private MerchantEsService merchantEsService;
    @Resource
    private AgentEsService agentEsService;
    @Resource
    private AccessService accessService;

    @ApiOperation(value = "数据-新增商户趋势、交易量趋势、新增代理商趋势", notes = OrderSwaggerNotes.VIEW_DATA_TREND)
    @PostMapping("/viewDataTrend")
    @SwaggerDeveloped
    public ResponseBean viewDataTrend(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                      @RequestBody(required = false) Map<String, String> bodyParas) {

        Map<String, Object> mapRes = new LinkedHashMap<>();

        String agentNode = userInfoBean.getAgentNode();
        try {
            String agentNo = bodyParas.get("agentNo");
            String queryScope = bodyParas.get("queryScope");
            //按代理商查询，如果没选择代理商，默认查当前登录的代理商
            if (StringUtils.isNotBlank(agentNo)) {
                if (!accessService.canAccessTheAgent(agentNode, agentNo)) {
                    return ResponseBean.error("操作不合法，请规范操作");
                }
                Map<String, Object> agentInfo = agentEsService.queryAgentInfoByAgentNodeOrAgentNo(agentNo);
                if (!CollectionUtils.isEmpty(agentInfo)) {
                    agentNode = String.valueOf(agentInfo.get("agent_node"));
                }
            } else {
                agentNo = userInfoBean.getAgentNo();
            }
            //获取交易量趋势
            EsSearchBean searchBean = EsSearchBean.builder().typeName(EsNpospJoinType.ORDER.getTypeName())
                    .queryScope(QueryScope.getByScopeCode(queryScope)).agentNode(agentNode).agentNo(agentNo)
                    .transStatus(OrderTransStatus.SUCCESS.getStatus()).build();
            Tuple<List<KeyValueBean>, List<KeyValueBean>> transOrderTrend = orderEsService.listSevenDayAndHalfYearDataTrend(searchBean);
            Map<String, Object> transOrderTrendMap = new HashMap<>();
            transOrderTrendMap.put("sevenDayTrend", transOrderTrend.v1());
            transOrderTrendMap.put("halfYearTrend", transOrderTrend.v2());
            mapRes.put("transOrderTrend", transOrderTrendMap);

            //获取新增商户趋势
            searchBean = EsSearchBean.builder().typeName(EsNpospJoinType.MERCHANT.getTypeName())
                    .queryScope(QueryScope.getByScopeCode(queryScope)).agentNode(agentNode).agentNo(agentNo).build();
            Tuple<List<KeyValueBean>, List<KeyValueBean>> newlyMerTrend = orderEsService.listSevenDayAndHalfYearDataTrend(searchBean);
            Map<String, Object> newlyMerTrendMap = new HashMap<>();
            newlyMerTrendMap.put("sevenDayTrend", newlyMerTrend.v1());
            newlyMerTrendMap.put("halfYearTrend", newlyMerTrend.v2());
            mapRes.put("newlyMerTrend", newlyMerTrendMap);

            //获取新增代理商趋势
            searchBean = EsSearchBean.builder().typeName(EsNpospJoinType.AGENT.getTypeName())
                    .queryScope(QueryScope.getByScopeCode(queryScope)).agentNode(agentNode).agentNo(agentNo).build();
            Tuple<List<KeyValueBean>, List<KeyValueBean>> newlyAgentTrend = orderEsService.listSevenDayAndHalfYearDataTrend(searchBean);
            Map<String, Object> newlyAgentTrendMap = new HashMap<>();
            newlyAgentTrendMap.put("sevenDayTrend", newlyAgentTrend.v1());
            newlyAgentTrendMap.put("halfYearTrend", newlyAgentTrend.v2());
            mapRes.put("newlyAgentTrend", newlyAgentTrendMap);

            return ResponseBean.success(mapRes);
        } catch (Exception e) {
            log.error("代理商{}数据->新增商户趋势、交易量趋势、新增代理商趋势异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("获取数据趋势失败，请稍候再试");
        }
    }

    @SignValidate(needSign = false)
    @SwaggerDeveloped
    @ApiOperation(value = "数据->查询预警信息详情", notes = SwaggerNotes.GET_MERCHANT_EARLY_WARNING_DETAILS)
    @PostMapping("/queryMerchantEarlyWarningDetails/{pageNo}/{pageSize}")
    public ResponseBean getMerchantEarlyWarningDetails(@PathVariable int pageNo,
                                                       @PathVariable int pageSize,
                                                       @RequestBody MerchantSearchBean searchBean,
                                                       @ApiIgnore @CurrentUser UserInfoBean userInfoBean){
        String agentNode = accessService.checkAndGetAgentNode(userInfoBean.getAgentNode(), searchBean.getAgentNo());
        MerchantWarningBean warningBean = merchantEsService.queryMerchantEarlyWaring(searchBean.getWarningId(), userInfoBean.getAgentNo());
        if (warningBean == null) {
            return ResponseBean.success();
        }
        pageNo = pageNo - 1 <= 0 ? 0 : pageNo - 1;
        pageSize = pageSize <= 1 ? 1 : pageSize;
        Tuple<List<MerchantEsResultBean>, Long> warningDetails =
                merchantEsService.getMerchantEarlyWarningDetails(searchBean.getQueryScope(), agentNode, warningBean, PageRequest.of(pageNo, pageSize));
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(warningDetails.v1())) {
            warningDetails.v1().forEach(item -> {
                if (StringUtils.isBlank(item.getMerchantName())) {
                    item.setMerchantName(item.getMerchantNo());
                }
                if (StringUtils.isBlank(item.getAgentName())) {
                    item.setAgentName(item.getAgentNo());
                }
                // 手机号打码显示
                item.setMobilePhone(StringUtils.mask4MobilePhone(item.getMobilePhone()));
                boolean isDirectMerchant = StringUtils.equalsIgnoreCase(item.getAgentNo(), userInfoBean.getAgentNo());
                item.setDirectMerchant(isDirectMerchant);
                // 非直营商户,不显示商户号
                if (!isDirectMerchant) {
                    item.setMerchantName("");
                }
            });
        }
        Map<String, Object> result = new HashMap<>();
        result.put("merchantWarning", warningBean);
        result.put("merchantList", warningDetails.v1());
        return ResponseBean.success(result, warningDetails.v2());
    }

    @SwaggerDeveloped
    @ApiOperation(value = "数据->查询预警商户汇总信息", notes = SwaggerNotes.QUERY_MERCHANT_EARLY_WARNING)
    @PostMapping("/queryMerchantEarlyWarning")
    public ResponseBean queryMerchantEarlyWarning(@RequestBody MerchantSearchBean searchBean,
                                                  @ApiIgnore @CurrentUser UserInfoBean userInfoBean){
        String agentNode = accessService.checkAndGetAgentNode(userInfoBean.getAgentNode(), searchBean.getAgentNo());
        List<MerchantWarningBean> warningBeanList = merchantEsService.queryMerchantEarlyWarning(searchBean.getQueryScope(), agentNode, userInfoBean.getAgentNo());
        return ResponseBean.success(warningBeanList);
    }

//    @ApiOperation(value = "数据->本月新增商户/代理商 累积商户/代理商", notes = SwaggerNotes.QUERY_MERCHANT_AND_AGENT_DATA)
//    @PostMapping("/queryMerchantAndAgentData")
//    public ResponseBean queryMerchantAndAgentData(@RequestBody MerchantSearchBean searchBean,
//                                                  @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
//        String agentNode = accessService.checkAndGetAgentNode(userInfoBean.getAgentNode(), searchBean.getAgentNo());
//        MerchantSumBean currentMonthMerchant = MerchantSearchUtils.queryCurrentMonthData(searchBean.getQueryScope(), agentNode, EsNpospJoinType.MERCHANT);
//        MerchantSumBean currentMonthAgent = MerchantSearchUtils.queryCurrentMonthData(searchBean.getQueryScope(), agentNode, EsNpospJoinType.AGENT);
//        MerchantSumBean allMerchant = MerchantSearchUtils.queryAllMerchant(searchBean.getQueryScope(), agentNode);
//        Long allAgentCount = MerchantSearchUtils.queryAllAgent(searchBean.getQueryScope(), agentNode);
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("currentMonthMerchant", currentMonthMerchant);
//        result.put("allMerchant", allMerchant);
//        result.put("currentMonthAgent", currentMonthAgent);
//        result.put("allAgentCount", allAgentCount);
//        return ResponseBean.success(result);
//    }
}
