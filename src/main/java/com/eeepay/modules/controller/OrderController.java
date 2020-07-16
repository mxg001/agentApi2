package com.eeepay.modules.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.eeepay.frame.annotation.CurrentUser;
import com.eeepay.frame.annotation.SwaggerDeveloped;
import com.eeepay.frame.bean.PageBean;
import com.eeepay.frame.bean.ResponseBean;
import com.eeepay.frame.enums.*;
import com.eeepay.frame.utils.StringUtils;
import com.eeepay.frame.utils.swagger.OrderSwaggerNotes;
import com.eeepay.modules.bean.*;
import com.eeepay.modules.service.*;
import com.eeepay.modules.utils.MerchantSearchUtils;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Title：agentApi2
 * @Description：
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Slf4j
@RequestMapping("/order")
@Api(description = "交易模块")
@RestController
public class OrderController {

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
    @Resource
    private AgentInfoService agentInfoService;
    @Resource
    private MerchantInfoService merchantInfoService;


    @ApiOperation(value = "首页->数据下发（今日交易量、全部商户、今日新增商户）")
    @GetMapping("/loadIndexData")
    @SwaggerDeveloped
    public ResponseBean loadIndexData(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            //封装查询条件
            EsSearchBean searchBean = EsSearchBean.builder().agentNode(userInfoBean.getAgentNode())
                    .transStatus(OrderTransStatus.SUCCESS.getStatus()).startCreateTime(DateUtil.format(new Date(), "yyyy-MM-dd"))
                    .build();

            Map<String, Object> result = new HashMap<>();
            result.put("daySumTransAmount", orderEsService.sumTransAmountByTerms(searchBean).v2());
            result.put("allMerchant", merchantEsService.countMerchant(userInfoBean.getAgentNode(), false));
            result.put("todayNewlyMerchant", merchantEsService.countMerchant(userInfoBean.getAgentNode(), true));
            return ResponseBean.success(result);

        } catch (Exception e) {
            log.error("当前登录代理商{}首页->数据下发异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("获取首页数据失败，请稍候再试");
        }
    }

    @ApiOperation(value = "首页->交易查询->交易汇总", notes = OrderSwaggerNotes.GROUP_SUMMARY_TRANS_ORDER)
    @PostMapping("/groupSummaryTransOrder")
    @SwaggerDeveloped
    public ResponseBean groupSummaryTransOrder(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                               @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        Map<String, Object> result = new HashMap<>();
        String loginAgentNode = userInfoBean.getAgentNode();
        String loginAgentNo = userInfoBean.getAgentNo();
        long loginAgentLevel = userInfoBean.getAgentLevel();
        String loginAgentOem = userInfoBean.getAgentOem();
        try {
            String queryScope = bodyParams.get("queryScope");

            String agentNo, agentName, teamId, teamName;
            long totalCountOrder = 0, otherCountOrder = 0, agentLevel = 0;
            BigDecimal totalSumOrderAmount = BigDecimal.ZERO, otherSumOrderAmount = BigDecimal.ZERO;

            //获取当前登录代理商业务产品的所属组织
            List<Map<String, Object>> teamMapList = merchantInfoService.getAgentTeams(loginAgentNo, false);
            Set<String> showTeamIds = new HashSet<>();
            for (Map<String, Object> teamMap : teamMapList) {
                String team_id = StringUtils.filterNull(teamMap.get("team_id"));
                if ("100070".equals(team_id)) {
                    showTeamIds.add("100070");
                }
                if ("200010".equals(team_id)) {
                    showTeamIds.add("200010");
                }
                if (!"100070".equals(team_id) && !"200010".equals(team_id)) {
                    showTeamIds.add("other");
                }
            }
            //封装查询条件
            //按业务产品汇总
            EsSearchBean searchBean = EsSearchBean.builder().queryScope(QueryScope.getByScopeCode(queryScope))
                    .agentNode(loginAgentNode).transStatus(OrderTransStatus.SUCCESS.getStatus())
                    .groupFields(new String[]{"team_id"}).build();

            List<Map<String, Object>> productSummary = orderEsService.groupSummaryTransOrder(searchBean);
            List<Map<String, Object>> showTeamSummary = new ArrayList<>();
            boolean orderContains200010 = false;
            boolean orderContains100070 = false;
            for (Map<String, Object> map : productSummary) {
                teamId = String.valueOf(map.get("key"));
                //获取组织机构名称
                teamName = orderService.getTeamNameByTeamId(teamId);
                //获取组织机构名称
                map.put("name", teamName);
                map.put("entryTeam", "");
                if (showTeamIds.contains(teamId)) {
                    if ("200010".equals(teamId)) {
                        orderContains200010 = true;
                        showTeamSummary.add(map);
                    }
                    if ("100070".equals(teamId)) {
                        //下面分子组织再汇总，暂不添加
                        orderContains100070 = true;
                    }
                } else {
                    otherCountOrder += (long) map.get("countOrder");
                    otherSumOrderAmount = otherSumOrderAmount.add(new BigDecimal(map.get("sumOrderAmount").toString()));
                }
                //数量和金额累加
                totalCountOrder += (long) map.get("countOrder");
                totalSumOrderAmount = totalSumOrderAmount.add(new BigDecimal(map.get("sumOrderAmount").toString()));
            }
            if (!orderContains200010 && showTeamIds.contains("200010")) {
                showTeamSummary.add(ImmutableMap.of("key", "200010", "entryTeam", "", "name", orderService.getTeamNameByTeamId("200010"), "countOrder", 0, "sumOrderAmount", 0));
            }
            if (!orderContains100070 && showTeamIds.contains("100070")) {
                showTeamSummary.add(ImmutableMap.of("key", "100070", "entryTeam", "100070-001", "name", orderService.getEntryTeamNameByEntryTeamId("100070-001"), "countOrder", 0, "sumOrderAmount", 0));
                showTeamSummary.add(ImmutableMap.of("key", "100070", "entryTeam", "100070-002", "name", orderService.getEntryTeamNameByEntryTeamId("100070-002"), "countOrder", 0, "sumOrderAmount", 0));
            }

            //100070按子组织再汇总交易
            if (orderContains100070) {
                //获取100070所有自组织
                List<Map<String, Object>> entryTeamList = orderService.getEntryTeamByTeamId("100070");
                Set<String> hasEntryTeamId = new HashSet<>();
                EsSearchBean entryGroupSearchBean = EsSearchBean.builder().queryScope(QueryScope.getByScopeCode(queryScope))
                        .agentNode(loginAgentNode).transStatus(OrderTransStatus.SUCCESS.getStatus()).teamId("100070")
                        .groupFields(new String[]{"team_entry_id.keyword"}).build();
                List<Map<String, Object>> entryGroupSummary = orderEsService.groupSummaryTransOrder(entryGroupSearchBean);
                String entryTeamId, entryTeamName;
                for (Map<String, Object> map : entryGroupSummary) {
                    entryTeamId = String.valueOf(map.get("key"));
                    entryTeamName = orderService.getEntryTeamNameByEntryTeamId(entryTeamId);
                    //获取组织机构名称
                    map.put("key", "100070");
                    map.put("name", entryTeamName);
                    map.put("entryTeam", entryTeamId);
                    showTeamSummary.add(map);
                    hasEntryTeamId.add(entryTeamId);
                }
                for (Map<String, Object> entryTeamMap : entryTeamList) {
                    String subEntryTeamId = String.valueOf(entryTeamMap.get("team_entry_id"));
                    String subEntryTeamName = String.valueOf(entryTeamMap.get("team_entry_name"));
                    if (!hasEntryTeamId.contains(subEntryTeamId)) {
                        showTeamSummary.add(ImmutableMap.of("key", "100070", "entryTeam", subEntryTeamId, "name", subEntryTeamName, "countOrder", 0, "sumOrderAmount", 0));
                    }
                }
            }

            if (showTeamIds.contains("other")) {
                //构造其它
                showTeamSummary.add(ImmutableMap.of("key", "other", "entryTeam", "", "name", "其它产品", "countOrder", otherCountOrder, "sumOrderAmount", otherSumOrderAmount));
            }

            Map<String, Object> productGroup = new HashMap<>();
            productGroup.put("groupList", showTeamSummary);
            productGroup.put("totalCountOrder", totalCountOrder);
            productGroup.put("totalSumOrderAmount", totalSumOrderAmount);
            result.put("productGroup", productGroup);

            //按代理商汇总
            totalCountOrder = 0L;
            totalSumOrderAmount = BigDecimal.ZERO;

            List<Map<String, Object>> showAgentSummary = new ArrayList<>();
            Map<String, Object> showMap = new HashMap<>();
            Tuple<Long, BigDecimal> tuple = null;
            //获取所有直接下级代理商
            List<AgentInfo> allDirectChildren = agentInfoService.getAllDirectChildren(loginAgentNo);
            for (AgentInfo children : allDirectChildren) {

                searchBean = EsSearchBean.builder().queryScope(QueryScope.getByScopeCode(queryScope))
                        .agentNode(children.getAgentNode()).transStatus(OrderTransStatus.SUCCESS.getStatus())
                        .build();
                tuple = orderEsService.sumTransAmountByTerms(searchBean);

                showMap = new HashMap<>();
                showMap.put("key", children.getAgentNo());
                showMap.put("name", children.getAgentName());
                showMap.put("countOrder", tuple.v1().longValue());
                showMap.put("sumOrderAmount", tuple.v2());

                showAgentSummary.add(showMap);

                //数量和金额累加
                totalCountOrder += tuple.v1().longValue();
                totalSumOrderAmount = totalSumOrderAmount.add(tuple.v2());
            }
            Map<String, Object> agentGroup = new HashMap<>();
            agentGroup.put("groupList", showAgentSummary);
            agentGroup.put("totalCountOrder", totalCountOrder);
            agentGroup.put("totalSumOrderAmount", totalSumOrderAmount);
            result.put("agentGroup", agentGroup);

            return ResponseBean.success(result);
        } catch (Exception e) {
            log.error("当前登录代理商{}分组汇总交易【参数：{}】异常{}", userInfoBean.getAgentNode(), bodyParams.toString(), e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }

    @ApiOperation(value = "首页->交易查询->交易明细", notes = OrderSwaggerNotes.QUERY_TRANS_ORDER_FOR_PAGE)
    @PostMapping("/queryTransOrderForPage/{pageNo}/{pageSize}")
    @SwaggerDeveloped
    public ResponseBean queryTransOrderForPage(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                               @PathVariable(required = false) int pageNo,
                                               @PathVariable(required = false) int pageSize,
                                               @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        Map<String, Object> result = new HashMap<>();

        String agentNode = userInfoBean.getAgentNode();
        pageNo = pageNo < 1 ? 1 : pageNo;
        pageNo = pageNo - 1;
        pageSize = pageSize < 10 ? 10 : pageSize;
        EsSearchBean searchBean = new EsSearchBean();
        try {
            String agentNo = bodyParams.get("agentNo");

            //按代理商查询，如果没选择代理商，默认查当前登录的代理商交易
            if (StringUtils.isNotBlank(agentNo)) {
                if (!accessService.canAccessTheAgent(agentNode, agentNo)) {
                    return ResponseBean.error("无权操作");
                }
                Map<String, Object> agentInfo = agentEsService.queryAgentInfoByAgentNodeOrAgentNo(agentNo);
                if (!CollectionUtils.isEmpty(agentInfo)) {
                    agentNode = String.valueOf(agentInfo.get("agent_node"));
                }
            }
            String transStatus = bodyParams.get("transStatus");
            if (null == transStatus) {
                transStatus = OrderTransStatus.SUCCESS.getStatus();
                bodyParams.put("transStatus", transStatus);
            }
            Map<String, List<Object>> notFields = new HashMap<>();
            //封装查询条件
            searchBean = BeanUtil.mapToBean(bodyParams, EsSearchBean.class, true);
            String teamId = bodyParams.get("teamId");
            String entryTeam = bodyParams.get("entryTeam");
            searchBean.setTeamId(teamId);
            searchBean.setTeamEntryId(entryTeam);
            if ("other".equalsIgnoreCase(teamId)) {
                searchBean.setTeamId("");
                searchBean.setTeamEntryId("");
                //“其它”，排除100070和200010的组织
                List<Object> values = new ArrayList<>();
                values.add("200010");
                values.add("100070");
                notFields.put("team_id", values);
            }
            //排除失败初始化交易
            List<Object> values = new ArrayList<>();
            values.add("FAILED");
            values.add("INIT");
            notFields.put("trans_status", values);
            searchBean.setNotFields(notFields);

            searchBean.setAgentNode(agentNode);
            //汇总总的交易金额
            BigDecimal totalTransAmount = orderEsService.sumTransAmountByTerms(searchBean).v2();
            //封装分页对象
            PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
            //封装要查询的字段名称
            String[] includeFields = new String[]{"order_no", "merchant_no", "merchant_name", "trans_amount", "trans_type", "create_time", "trans_time",
                    "bp_id", "bp_name", "service_id", "service_name", "hp_type_name", "trans_status"};
            searchBean.setIncludeFields(includeFields);
            searchBean.setPageRequest(pageRequest);
            //按创建时间倒序
            searchBean.setSortFields(ImmutableMap.of("create_time", SortOrder.DESC));
            //执行分页查询
            PageBean pageRes = orderEsService.queryTransOrderForPage(searchBean);
            //封装页面需要字段
            List<Map<String, Object>> pageContent = pageRes.getPageContent();

            if (!CollectionUtils.isEmpty(pageContent)) {
                Map<String, Object> orderMap = new HashMap<>();
                Map<String, Object> pageRecord = new HashMap<>();
                String orderNo = "";
                ListIterator<Map<String, Object>> iterator = pageContent.listIterator();
                while (iterator.hasNext()) {
                    pageRecord = iterator.next();
                    orderNo = StringUtils.filterNull(pageRecord.get("order_no"));
                    orderMap = orderService.queryOrderDetailByOrderNo(orderNo);
                    if (!CollectionUtils.isEmpty(orderMap)) {
                        pageRecord.put("merchant_name", StringUtils.filterNull(orderMap.get("merchant_name")));
                        pageRecord.put("trans_type_zh", OrderTransType.getZhByType(StringUtils.filterNull(orderMap.get("trans_type"))));
                        pageRecord.put("bp_name", StringUtils.filterNull(orderMap.get("bp_name")));
                        pageRecord.put("service_id", StringUtils.filterNull(orderMap.get("service_id")));
                        pageRecord.put("service_name", StringUtils.filterNull(orderMap.get("service_name")));
                        pageRecord.put("hp_type_name", StringUtils.filterNull(orderMap.get("hp_type_name")));
                        String dbTeamId = StringUtils.filterNull(orderMap.get("team_id"));
                    }
                }
            }
            result.put("totalTransAmount", totalTransAmount);
            result.put("pageBean", pageContent);
            return ResponseBean.success(result);
        } catch (Exception e) {
            log.error("交易明细查询【参数：{}】异常{}", JSONUtil.toJsonStr(searchBean), e);
            return ResponseBean.error("交易明细查询失败，请稍候再试");
        }
    }

    @ApiOperation(value = "首页->交易查询->交易详情")
    @GetMapping("/showOrderDetail/{orderNo}")
    @SwaggerDeveloped
    public ResponseBean showOrderDetail(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                        @PathVariable String orderNo) {
        try {
            if (StringUtils.isBlank(orderNo)) {
                return ResponseBean.error("订单号不能为空");
            }
            Map<String, Object> orderMap = orderService.queryOrderDetailByOrderNo(orderNo);
            if (CollectionUtils.isEmpty(orderMap)) {
                return ResponseBean.error("此订单不存在");
            }
            //只能查询当前登录代理商的所属订单
            String agentNode = String.valueOf(orderMap.get("agent_node"));
            if (!agentNode.startsWith(userInfoBean.getAgentNode())) {
                return ResponseBean.error("非法操作");
            }
            //交易类型翻译
            orderMap.put("trans_type_zh", OrderTransType.getZhByType(String.valueOf(orderMap.get("trans_type"))));
            //所属代理商分润
            String agentLevel = String.valueOf(orderMap.get("agent_level"));
            String profitField = "profits_" + agentLevel;
            Map<String, BigDecimal> agentProfitMap = orderService.queryAgentProfitByOrderNo(orderNo, new String[]{profitField});
            BigDecimal agentProfit = CollectionUtil.isEmpty(agentProfitMap) ? BigDecimal.ZERO : agentProfitMap.get(profitField);
            orderMap.put("offical_agent_profit", agentProfit);
            //卡种翻译
            orderMap.put("card_type_zh", CardType.getZhByType(String.valueOf(orderMap.get("card_type"))));
            //卡类型和发卡行
            String card_kind = "", crad_belong = "";
            Map<String, Object> cardInfo = orderService.queryCardInfoByAccountNo(String.valueOf(orderMap.get("account_no")));
            if (!CollectionUtils.isEmpty(cardInfo)) {
                card_kind = String.valueOf(cardInfo.get("card_name"));
                crad_belong = String.valueOf(cardInfo.get("bank_desc"));
            }
            orderMap.put("card_kind", card_kind);
            orderMap.put("crad_belong", crad_belong);
            return ResponseBean.success(orderMap);

        } catch (Exception e) {
            log.error("当前登录代理商{}首页->交易查询->交易{}详情异常{}", userInfoBean.getAgentNode(), orderNo, e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }

    @ApiOperation(value = "数据-近6月商户交易量")
    @PostMapping("/monthSumTransAmount")
    @SwaggerDeveloped
    public ResponseBean monthSumTransAmount(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                            @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        String agentNode = userInfoBean.getAgentNode();
        try {
            String agentNo = bodyParams.get("agentNo");
            String queryScope = bodyParams.get("queryScope");
            //按代理商查询，如果没选择代理商，默认查当前登录的代理商交易
            if (StringUtils.isNotBlank(agentNo)) {
                if (!accessService.canAccessTheAgent(agentNode, agentNo)) {
                    return ResponseBean.error("无权操作");
                }
                Map<String, Object> agentInfo = agentEsService.queryAgentInfoByAgentNodeOrAgentNo(agentNo);
                if (!CollectionUtils.isEmpty(agentInfo)) {
                    agentNode = String.valueOf(agentInfo.get("agent_node"));
                }
            }
            //封装查询条件
            EsSearchBean searchBean = new EsSearchBean();
            searchBean.setAgentNode(agentNode);
            searchBean.setTransStatus(OrderTransStatus.SUCCESS.getStatus());
            searchBean.setQueryScope(QueryScope.getByScopeCode(queryScope));
            //封装返回信息
            Map<String, Object> result = new HashMap<>();
            Date now = new Date();
            //先统计近6月累计交易量
            Date sixMonthBefore = DateUtils.addMonths(now, -6);
            String startTransTime = DateUtil.format(sixMonthBefore, "yyyy-MM") + "-01 00:00:00";
            String endTransTime = DateUtil.format(now, "yyyy-MM") + "-01 00:00:00";
            searchBean.setStartTransTime(startTransTime);
            searchBean.setEndTransTime(endTransTime);
            result.put("sixMonthSumTransAmount", orderEsService.sumTransAmountByTerms(searchBean).v2());
            //再统计本月交易量
            searchBean.setStartTransTime(endTransTime);
            searchBean.setEndTransTime(null);
            result.put("currMonthSumTransAmount", orderEsService.sumTransAmountByTerms(searchBean).v2());

            MerchantSumBean currentMonthMerchant = MerchantSearchUtils.queryCurrentMonthData(searchBean.getQueryScope(),
                    StringUtils.isNotBlank(agentNo) ? agentNo : userInfoBean.getAgentNo(), agentNode, EsNpospJoinType.MERCHANT);
            MerchantSumBean currentMonthAgent = MerchantSearchUtils.queryCurrentMonthData(searchBean.getQueryScope(),
                    StringUtils.isNotBlank(agentNo) ? agentNo : userInfoBean.getAgentNo(), agentNode, EsNpospJoinType.AGENT);
            MerchantSumBean allMerchant = MerchantSearchUtils.queryAllMerchant(searchBean.getQueryScope(), agentNode);
            Long allAgentCount = MerchantSearchUtils.queryAllAgent(searchBean.getQueryScope(),
                    StringUtils.isNotBlank(agentNo) ? agentNo : userInfoBean.getAgentNo(), agentNode);

            result.put("currentMonthMerchant", currentMonthMerchant);
            result.put("allMerchant", allMerchant);
            result.put("currentMonthAgent", currentMonthAgent);
            result.put("allAgentCount", allAgentCount);

            return ResponseBean.success(result);
        } catch (Exception e) {
            log.error("当前登录代理商{}数据->近6月商户交易量异常{}", agentNode, e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }

    @ApiOperation(value = "数据-明细", notes = OrderSwaggerNotes.TRANS_ORDER_DATA_DETAIL)
    @PostMapping("/transOrderDataDetail")
    @SwaggerDeveloped
    public ResponseBean transOrderDataDetail(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                             @RequestBody(required = false) Map<String, String> bodyParams) {

        Map<String, Object> res = new HashMap<>();
        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        List<KeyValueBean> dailyData = new ArrayList<>();// 当月每日数据汇总
        KeyValueBean monthData = new KeyValueBean();// 当月数据总数据汇总

        String agentNode = userInfoBean.getAgentNode();
        String agentOem = userInfoBean.getAgentOem();
        try {
            String agentNo = bodyParams.get("agentNo");
            String queryScope = bodyParams.get("queryScope");
            //查询数据月份，yyyy-MM
            String queryDateStr = bodyParams.get("queryDate");
            //按代理商查询，如果没选择代理商，默认查当前登录的代理商
            if (StringUtils.isNotBlank(agentNo)) {
                if (!accessService.canAccessTheAgent(agentNode, agentNo)) {
                    return ResponseBean.error("无权操作");
                }
                Map<String, Object> agentInfo = agentEsService.queryAgentInfoByAgentNodeOrAgentNo(agentNo);
                if (!CollectionUtils.isEmpty(agentInfo)) {
                    agentNode = String.valueOf(agentInfo.get("agent_node"));
                }
            } else {
                agentNo = userInfoBean.getAgentNo();
            }
            //默认查看上个月数据
            Date now = new Date();
            Date queryDate = DateUtils.addMonths(now, -1);
            if (StringUtils.isNotBlank(queryDateStr)) {
                try {
                    queryDate = DateUtil.parse(queryDateStr, "yyyy-MM");
                } catch (Exception e) {
                    queryDate = DateUtils.addMonths(now, -1);
                }
            }
            String startCreateTime, endCreateTime;

            EsSearchBean searchBean = new EsSearchBean();
            searchBean.setAgentNode(agentNode);
            searchBean.setAgentNo(agentNo);
            searchBean.setAgentOem(agentOem);
            searchBean.setQueryScope(QueryScope.getByScopeCode(queryScope));
            searchBean.setTransStatus(OrderTransStatus.SUCCESS.getStatus());
            searchBean.setGroupFields(new String[]{"team_id"});
            //获取日数据
            Date beginOfMonth = DateUtil.beginOfMonth(queryDate);
            Date endOfMonth = DateUtil.endOfMonth(queryDate);
            Date currentDate = DateUtil.parse(DateUtil.formatDate(new Date()));
            //如果查本月数据，截至当日
            while (!beginOfMonth.after(endOfMonth) && !beginOfMonth.after(currentDate)) {
                Date nextOfBegin = DateUtils.addDays(beginOfMonth, 1);
                startCreateTime = DateUtil.format(beginOfMonth, "yyyy-MM-dd");
                endCreateTime = DateUtil.format(nextOfBegin, "yyyy-MM-dd");
                searchBean.setStartTransTime(startCreateTime + " 00:00:00");
                searchBean.setEndTransTime(endCreateTime + " 00:00:00");

                dailyData.add(new KeyValueBean(DateUtil.format(beginOfMonth, "MM-dd"), getTransOrderDataDetailData(searchBean, "day")));
                beginOfMonth = nextOfBegin;
            }
            Collections.sort(dailyData, new Comparator<KeyValueBean>() {
                @Override
                public int compare(KeyValueBean o1, KeyValueBean o2) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
                    try {
                        Date o1Date = sdf.parse(o1.getKey());
                        Date o2Date = sdf.parse(o2.getKey());
                        return o1Date.after(o2Date) ? -1 : 1;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
            res.put("dailyData", dailyData);

            //获取月数据
            startCreateTime = DateUtil.format(DateUtil.beginOfMonth(queryDate), "yyyy-MM-dd");
            endCreateTime = DateUtil.format(DateUtil.beginOfMonth(DateUtils.addMonths(queryDate, 1)), "yyyy-MM-dd");
            searchBean.setStartTransTime(startCreateTime + " 00:00:00");
            searchBean.setEndTransTime(endCreateTime + " 00:00:00");
            res.put("monthData", getTransOrderDataDetailData(searchBean, "month"));
            return ResponseBean.success(res);

        } catch (Exception e) {
            log.error("当前登录代理商{}数据->明细获取数据异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }

    /**
     * 获取订单明细数据，transOrderDataDetail接口辅助方法
     *
     * @param searchBean
     * @return
     */
    private Map<String, Object> getTransOrderDataDetailData(EsSearchBean searchBean, String type) {

        Map<String, Object> result = new HashMap<>();
        String agentOem = searchBean.getAgentOem();

        String teamId, teamName;
        long totalCountOrder = 0, otherCountOrder = 0, totalMerCount = 0, totalActiveMerCount = 0, otherMerCount = 0, otherActiveMerCount = 0;
        BigDecimal totalSumOrderAmount = BigDecimal.ZERO, otherSumOrderAmount = BigDecimal.ZERO;
        //获取当前登录代理商业务产品的所属组织
        List<Map<String, Object>> teamMapList = merchantInfoService.getAgentTeams(searchBean.getAgentNo(), false);
        Set<String> showTeamIds = new HashSet<>();
        for (Map<String, Object> teamMap : teamMapList) {
            String team_id = StringUtils.filterNull(teamMap.get("team_id"));
            if ("100070".equals(team_id)) {
                showTeamIds.add("100070");
            }
            if ("200010".equals(team_id)) {
                showTeamIds.add("200010");
            }
            if (!"100070".equals(team_id) && !"200010".equals(team_id)) {
                showTeamIds.add("other");
            }
        }

        //交易数据
        List<Map<String, Object>> teamSummary = orderEsService.groupSummaryTransOrder(searchBean);
        List<Map<String, Object>> showTeamSummary = new ArrayList<>();
        boolean orderContains200010 = false;
        boolean orderContains100070 = false;

        if (!CollectionUtils.isEmpty(teamSummary)) {
            for (Map<String, Object> map : teamSummary) {
                teamId = String.valueOf(map.get("key"));
                //获取组织机构名称
                teamName = orderService.getTeamNameByTeamId(teamId);
                //获取组织机构名称
                map.put("name", teamName);
                map.put("entryTeam", "");
                if (showTeamIds.contains(teamId)) {
                    if ("200010".equals(teamId)) {
                        orderContains200010 = true;
                        showTeamSummary.add(map);
                    }
                    if ("100070".equals(teamId)) {
                        //下面分子组织再汇总，暂不添加
                        orderContains100070 = true;
                    }
                } else {
                    otherCountOrder += (long) map.get("countOrder");
                    otherSumOrderAmount = otherSumOrderAmount.add(new BigDecimal(map.get("sumOrderAmount").toString()));
                }
                //数量和金额累加
                totalCountOrder += (long) map.get("countOrder");
                totalSumOrderAmount = totalSumOrderAmount.add(new BigDecimal(map.get("sumOrderAmount").toString()));
            }
        }
        //如果是月数据，盛钱包和盛POS，为0也要下发
        if ("month".equalsIgnoreCase(type)) {
            if (!orderContains200010 && showTeamIds.contains("200010")) {
                showTeamSummary.add(ImmutableMap.of("key", "200010", "entryTeam", "", "name", orderService.getTeamNameByTeamId("200010"), "countOrder", 0, "sumOrderAmount", 0));
            }
            if (!orderContains100070 && showTeamIds.contains("100070")) {
                showTeamSummary.add(ImmutableMap.of("key", "100070", "entryTeam", "100070-001", "name", orderService.getEntryTeamNameByEntryTeamId("100070-001"), "countOrder", 0, "sumOrderAmount", 0));
                showTeamSummary.add(ImmutableMap.of("key", "100070", "entryTeam", "100070-002", "name", orderService.getEntryTeamNameByEntryTeamId("100070-002"), "countOrder", 0, "sumOrderAmount", 0));
            }
        }
        //构造“其它产品”显示，有才下发，为0不下发
        if ((otherCountOrder > 0L || "month".equalsIgnoreCase(type)) && (showTeamIds.contains("other"))) {
            showTeamSummary.add(ImmutableMap.of("key", "other", "entryTeam", "", "name", "其它产品", "countOrder", otherCountOrder, "sumOrderAmount", otherSumOrderAmount));
        }
        //100070按子组织再汇总交易
        if (orderContains100070) {
            //获取100070所有自组织
            List<Map<String, Object>> entryTeamList = orderService.getEntryTeamByTeamId("100070");
            Set<String> hasEntryTeamId = new HashSet<>();
            EsSearchBean entryGroupSearchBean = new EsSearchBean();
            BeanUtil.copyProperties(searchBean, entryGroupSearchBean);
            entryGroupSearchBean.setTeamId("100070");
            entryGroupSearchBean.setGroupFields(new String[]{"team_entry_id.keyword"});
            List<Map<String, Object>> entryGroupSummary = orderEsService.groupSummaryTransOrder(entryGroupSearchBean);
            String entryTeamId, entryTeamName;
            for (Map<String, Object> map : entryGroupSummary) {
                entryTeamId = String.valueOf(map.get("key"));
                entryTeamName = orderService.getEntryTeamNameByEntryTeamId(entryTeamId);
                //获取组织机构名称
                map.put("key", "100070");
                map.put("name", entryTeamName);
                map.put("entryTeam", entryTeamId);
                showTeamSummary.add(map);
                hasEntryTeamId.add(entryTeamId);
            }
            for (Map<String, Object> entryTeamMap : entryTeamList) {
                String subEntryTeamId = String.valueOf(entryTeamMap.get("team_entry_id"));
                String subEntryTeamName = String.valueOf(entryTeamMap.get("team_entry_name"));
                if (!hasEntryTeamId.contains(subEntryTeamId)) {
                    showTeamSummary.add(ImmutableMap.of("key", "100070", "entryTeam", subEntryTeamId, "name", subEntryTeamName, "countOrder", 0, "sumOrderAmount", 0));
                }
            }
        }

        Map<String, Object> teamGroup = new HashMap<>();
        teamGroup.put("groupList", showTeamSummary);
        teamGroup.put("totalCountOrder", totalCountOrder);
        teamGroup.put("totalSumOrderAmount", totalSumOrderAmount);
        result.put("teamGroup", teamGroup);

        //由于订单的由创建时间改为交易时间，所以这里取transTime

        //获取新增代理商数据
        EsSearchBean agentSearchBean = new EsSearchBean();
        agentSearchBean.setQueryScope(searchBean.getQueryScope());
        agentSearchBean.setAgentNode(searchBean.getAgentNode());
        agentSearchBean.setAgentNo(searchBean.getAgentNo());
        agentSearchBean.setStartCreateTime(searchBean.getStartTransTime());
        agentSearchBean.setEndCreateTime(searchBean.getEndTransTime());
        agentSearchBean.setPageRequest(PageRequest.of(0, 10));
        PageBean agentPage = agentEsService.queryAgentInfoForPage(agentSearchBean);
        result.put("agentAddCount", null == agentPage ? 0 : agentPage.getTotalCount());

        //获取新增商户数据
        EsSearchBean merSearchBean = new EsSearchBean();
        merSearchBean.setQueryScope(searchBean.getQueryScope());
        merSearchBean.setAgentNode(searchBean.getAgentNode());
        merSearchBean.setStartCreateTime(searchBean.getStartTransTime());
        merSearchBean.setEndCreateTime(searchBean.getEndTransTime());

        //新增商户数据
        List<Map<String, Object>> merSummary = orderEsService.groupMerchantByTeamAndHlfActive(merSearchBean, 1);
        List<Map<String, Object>> showMerSummary = new ArrayList<>();
        orderContains200010 = false;
        orderContains100070 = false;

        if (!CollectionUtils.isEmpty(merSummary)) {
            for (Map<String, Object> map : merSummary) {
                teamId = String.valueOf(map.get("key"));
                teamName = orderService.getTeamNameByTeamId(teamId);
                //获取组织机构名称
                map.put("name", teamName);
                map.put("entryTeam", "");
                if (showTeamIds.contains(teamId)) {
                    if ("200010".equals(teamId)) {
                        orderContains200010 = true;
                        showMerSummary.add(map);
                    }
                    if ("100070".equals(teamId)) {
                        //下面分子组织再汇总，暂不添加
                        orderContains100070 = true;
                    }
                } else {
                    otherMerCount += (long) map.get("countMer");
                    otherActiveMerCount += (long) map.get("activeCountMer");
                }
                //总商户和总激活商户累加
                totalMerCount += (long) map.get("countMer");
                totalActiveMerCount += (long) map.get("activeCountMer");
            }
        }
        //如果是月数据，盛钱包和盛POS，为0也要下发
        if ("month".equalsIgnoreCase(type)) {
            if (!orderContains200010 && showTeamIds.contains("200010")) {
                showMerSummary.add(ImmutableMap.of("key", "200010", "entryTeam", "", "name", orderService.getTeamNameByTeamId("200010"), "countMer", 0, "activeCountMer", 0));
            }
            if (!orderContains100070 && showTeamIds.contains("100070")) {
                showMerSummary.add(ImmutableMap.of("key", "100070", "entryTeam", "100070-001", "name", orderService.getEntryTeamNameByEntryTeamId("100070-001"), "countMer", 0, "activeCountMer", 0));
                showMerSummary.add(ImmutableMap.of("key", "100070", "entryTeam", "100070-002", "name", orderService.getEntryTeamNameByEntryTeamId("100070-002"), "countMer", 0, "activeCountMer", 0));
            }
        }
        //构造“其它产品”显示，有才下发，为0不下发
        if ((otherMerCount > 0L || "month".equalsIgnoreCase(type) && showTeamIds.contains("other"))) {
            showMerSummary.add(ImmutableMap.of("key", "other", "entryTeam", "", "name", "其它产品", "countMer", otherMerCount, "activeCountMer", otherActiveMerCount));
        }
        //100070按子组织再汇总交易
        if (orderContains100070) {
            //获取100070所有自组织
            List<Map<String, Object>> entryTeamList = orderService.getEntryTeamByTeamId("100070");
            Set<String> hasEntryTeamId = new HashSet<>();
            EsSearchBean entryGroupSearchBean = new EsSearchBean();
            BeanUtil.copyProperties(merSearchBean, entryGroupSearchBean);
            entryGroupSearchBean.setTeamId("100070");
            List<Map<String, Object>> entryGroupSummary = orderEsService.groupMerchantByTeamAndHlfActive(merSearchBean, 2);
            String entryTeamId, entryTeamName;
            for (Map<String, Object> map : entryGroupSummary) {
                entryTeamId = String.valueOf(map.get("key"));
                entryTeamName = orderService.getEntryTeamNameByEntryTeamId(entryTeamId);
                //获取组织机构名称
                map.put("key", "100070");
                map.put("name", entryTeamName);
                map.put("entryTeam", entryTeamId);
                showMerSummary.add(map);
                hasEntryTeamId.add(entryTeamId);
            }
            for (Map<String, Object> entryTeamMap : entryTeamList) {
                String subEntryTeamId = String.valueOf(entryTeamMap.get("team_entry_id"));
                String subEntryTeamName = String.valueOf(entryTeamMap.get("team_entry_name"));
                if (!hasEntryTeamId.contains(subEntryTeamId)) {
                    showMerSummary.add(ImmutableMap.of("key", "100070", "entryTeam", subEntryTeamId, "name", subEntryTeamName, "countMer", 0, "activeCountMer", 0));
                }
            }
        }

        Map<String, Object> merGroup = new HashMap<>();
        merGroup.put("groupList", showMerSummary);
        merGroup.put("totalMerCount", totalMerCount);
        merGroup.put("totalActiveMerCount", totalActiveMerCount);
        result.put("merGroup", merGroup);

        return result;
    }
}