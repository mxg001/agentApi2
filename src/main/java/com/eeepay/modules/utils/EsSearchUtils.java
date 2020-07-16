package com.eeepay.modules.utils;

import com.eeepay.frame.config.SpringHolder;
import com.eeepay.frame.enums.QueryScope;
import com.eeepay.frame.utils.Constants;
import com.eeepay.frame.utils.StringUtils;
import com.eeepay.frame.utils.es.EsLog;
import com.eeepay.modules.bean.EsSearchBean;
import com.eeepay.modules.bean.KeyValueBean;
import com.eeepay.modules.bean.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.eeepay.frame.enums.EsNpospField.*;
import static com.eeepay.frame.enums.EsNpospJoinType.*;
import static com.eeepay.frame.enums.QueryScope.ALL;

/**
 * @Title：agentApi2
 * @Description：订单ES搜索辅助类
 * @Author：zhangly
 * @Date：2019/5/24 15:30
 * @Version：1.0
 */
@Slf4j
public class EsSearchUtils {

    private static final ElasticsearchTemplate elasticsearchTemplate = SpringHolder.getBean(ElasticsearchTemplate.class);

    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter YYYY_MM = DateTimeFormatter.ofPattern("yyyy-MM");
    public static final DateTimeFormatter YYYY_MM_01_00_00_00 = DateTimeFormatter.ofPattern("yyyy-MM-01 00:00:00");
    public static final DateTimeFormatter YYYY_MM_DD_00_00_00 = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
    public static final DateTimeFormatter MM_DD = DateTimeFormatter.ofPattern("MM-dd");
    public static final DateTimeFormatter YYYY_MM_DD_23_59_59 = DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59");

    //七日
    private static final String SEVEN_DAY_AGO = "seven_day_ago";
    //半年
    private static final String HALF_YEAR_AGO = "half_year_ago";

    private static final String SUM_OF_TRANS_AMOUNT = "sumTransAmount";

    /**
     * 根据 EsSearchBean 获取SearchQuery
     *
     * @param searchBean
     * @return
     */
    public static NativeSearchQueryBuilder getEsSearchQueryBuilder(EsSearchBean searchBean) {

        String typeName = searchBean.getTypeName();
        String agentNode = searchBean.getAgentNode();
        String agentNo = searchBean.getAgentNo();
        String startCreateTime = searchBean.getStartCreateTime();
        String endCreateTime = searchBean.getEndCreateTime();
        String startTransTime = searchBean.getStartTransTime();
        String endTransTime = searchBean.getEndTransTime();
        String minTransAmount = searchBean.getMinTransAmount();
        String maxTransAmount = searchBean.getMaxTransAmount();
        String teamId = searchBean.getTeamId();
        String teamEntryId = searchBean.getTeamEntryId();
        String bpId = searchBean.getBpId();
        String cardType = searchBean.getCardType();
        String accountNo = searchBean.getAccountNo();
        String transStatus = searchBean.getTransStatus();
        String mobileNo = searchBean.getMobileNo();
        String merchantNo = searchBean.getMerchantNo();
        String merchantKey = searchBean.getMerchantKey();
        String payMethod = searchBean.getPayMethod();
        String orderNo = searchBean.getOrderNo();
        String settleStatus = searchBean.getSettleStatus();
        String deviceSn = searchBean.getDeviceSn();

        PageRequest pageRequest = searchBean.getPageRequest();
        String[] includeFields = searchBean.getIncludeFields();
        String[] groupFields = searchBean.getGroupFields();
        Map<String, String> sumFields = searchBean.getSumFields();
        Map<String, SortOrder> sortFields = searchBean.getSortFields();
        Map<String, List<Object>> notFields = searchBean.getNotFields();

        //默认全部
        QueryScope queryScope = searchBean.getQueryScope();
        queryScope = (null == queryScope ? ALL : queryScope);

        BoolQueryBuilder builder = QueryBuilders.boolQuery()
                //查询类型
                .filter(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), typeName));

        //过滤开始创建时间，结束创建时间
        RangeQueryBuilder createTimeRange = null;
        if (StringUtils.isNotBlank(startCreateTime)) {
            createTimeRange = QueryBuilders.rangeQuery(CREATE_TIME.getFieldName()).gte(startCreateTime);
        }
        if (StringUtils.isNotBlank(endCreateTime)) {
            createTimeRange = (null == createTimeRange ? QueryBuilders.rangeQuery(CREATE_TIME.getFieldName()).lte(endCreateTime) : createTimeRange.lte(endCreateTime));
        }
        if (null != createTimeRange) {
            builder.filter(createTimeRange);
        }
        //过滤开始交易时间，结束交易时间
        RangeQueryBuilder transTimeRange = null;
        if (StringUtils.isNotBlank(startTransTime)) {
            transTimeRange = QueryBuilders.rangeQuery(TRANS_TIME.getFieldName()).gte(startTransTime);
        }
        if (StringUtils.isNotBlank(endTransTime)) {
            transTimeRange = (null == transTimeRange ? QueryBuilders.rangeQuery(TRANS_TIME.getFieldName()).lte(endTransTime) : transTimeRange.lte(endTransTime));
        }
        if (null != transTimeRange) {
            builder.filter(transTimeRange);
        }
        //过滤最小金额，最大金额
        RangeQueryBuilder transAmountRange = null;
        if (StringUtils.isNotBlank(minTransAmount)) {
            transAmountRange = QueryBuilders.rangeQuery(TRANS_AMOUNT.getFieldName()).gte(minTransAmount);
        }
        if (StringUtils.isNotBlank(maxTransAmount)) {
            transAmountRange = (null == transAmountRange ? QueryBuilders.rangeQuery(TRANS_AMOUNT.getFieldName()).lte(maxTransAmount) : transAmountRange.lte(maxTransAmount));
        }
        if (null != transAmountRange) {
            builder.filter(transAmountRange);
        }
        //过滤所属组织
        if (StringUtils.isNotBlank(teamId)) {
            builder.filter(QueryBuilders.termQuery(TEAM_ID.getFieldName(), teamId));
        }
        //过滤子组织
        if (StringUtils.isNotBlank(teamEntryId)) {
            builder.filter(QueryBuilders.termQuery(TEAM_ENTRY_ID.getFieldName() + ".keyword", teamEntryId));
        }
        //过滤业务产品
        if (StringUtils.isNotBlank(bpId)) {
            builder.filter(QueryBuilders.termQuery(BP_ID.getFieldName(), bpId));
        }
        //过滤卡片种类
        if (StringUtils.isNotBlank(cardType)) {
            builder.filter(QueryBuilders.termQuery(CARD_TYPE.getFieldName(), cardType));
        }
        //过滤交易卡号
        if (StringUtils.isNotBlank(accountNo)) {
            builder.filter(QueryBuilders.termQuery(ACCOUNT_NO.getFieldName(), accountNo));
        }
        //过滤交易状态
        if (StringUtils.isNotBlank(transStatus)) {
            builder.filter(QueryBuilders.termQuery(TRANS_STATUS.getFieldName(), transStatus));
        }
        //过滤手机号
        if (StringUtils.isNotBlank(mobileNo)) {
            builder.filter(QueryBuilders.termQuery(MOBILE_PHONE.getFieldName(), mobileNo));
        }
        //过滤商户编号
        if (StringUtils.isNotBlank(merchantNo)) {
            builder.filter(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), merchantNo));
        }
        //过滤商户编号
        if (StringUtils.isNotBlank(merchantKey)) {
            builder.filter(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), merchantKey));
        }
        //过滤交易方式
        if (StringUtils.isNotBlank(payMethod)) {
            builder.filter(QueryBuilders.termQuery(PAY_METHOD.getFieldName(), payMethod));
        }
        //过滤订单号
        if (StringUtils.isNotBlank(orderNo)) {
            builder.filter(QueryBuilders.termQuery(ORDER_NO.getFieldName(), orderNo));
        }
        //过滤结算状态
        if (StringUtils.isNotBlank(settleStatus)) {
            builder.filter(QueryBuilders.termQuery(SETTLE_STATUS.getFieldName(), settleStatus));
        }
        //过滤机具SN号
        if (StringUtils.isNotBlank(deviceSn)) {
            builder.filter(QueryBuilders.termQuery(DEVICE_SN.getFieldName(), deviceSn));
        }
        //not字段
        if (!CollectionUtils.isEmpty(notFields)) {
            for (Map.Entry<String, List<Object>> notField : notFields.entrySet()) {
                List<Object> values = notField.getValue();
                for (Object value : values) {
                    builder.mustNot(QueryBuilders.termQuery(notField.getKey(), value));
                }
            }
        }
        //查询范围
        switch (queryScope) {
            case ALL: {
                //全部
                if (typeName.equals(ORDER.getTypeName()) || typeName.equals(MERCHANT.getTypeName())) {
                    builder.filter(QueryBuilders.wildcardQuery(AGENT_NODE.getFieldName() + ".key", String.format("%s*", agentNode)));
                }
                if (typeName.equals(AGENT.getTypeName())) {
                    builder.filter(QueryBuilders.wildcardQuery(AGENT_NODE.getFieldName() + ".key", String.format("%s*", agentNode)));
                    builder.mustNot(QueryBuilders.termQuery(AGENT_NODE.getFieldName() + ".key", agentNode));
                }
                break;
            }
            case OFFICAL: {
                //直营
                if (typeName.equals(ORDER.getTypeName()) || typeName.equals(MERCHANT.getTypeName())) {
                    builder.filter(QueryBuilders.termQuery(AGENT_NODE.getFieldName() + ".key", agentNode));
                }
                if (typeName.equals(AGENT.getTypeName())) {
                    builder.filter(QueryBuilders.termQuery(PARENT_ID.getFieldName(), agentNo));
                }
                break;
            }
            case CHILDREN: {
                //下级
                if (typeName.equals(ORDER.getTypeName()) || typeName.equals(MERCHANT.getTypeName())) {
                    builder.filter(QueryBuilders.wildcardQuery(AGENT_NODE.getFieldName() + ".key", String.format("%s*", agentNode)));
                    builder.mustNot(QueryBuilders.termQuery(AGENT_NODE.getFieldName() + ".key", agentNode));
                }
                if (typeName.equals(AGENT.getTypeName())) {
                    builder.filter(QueryBuilders.wildcardQuery(AGENT_NODE.getFieldName() + ".key", String.format("%s*", agentNode)));
                    builder.mustNot(QueryBuilders.termQuery(AGENT_NODE.getFieldName() + ".key", agentNode));
                    builder.mustNot(QueryBuilders.termQuery(PARENT_ID.getFieldName(), agentNo));
                }
                break;
            }
            default: {
                //全部
                if (typeName.equals(ORDER.getTypeName())) {
                    builder.filter(QueryBuilders.wildcardQuery(AGENT_NODE.getFieldName() + ".key", String.format("%s*", agentNode)));
                }
                if (typeName.equals(AGENT.getTypeName())) {
                    builder.filter(QueryBuilders.wildcardQuery(AGENT_NODE.getFieldName() + ".key", String.format("%s*", agentNode)));
                    builder.mustNot(QueryBuilders.termQuery(AGENT_NODE.getFieldName() + ".key", agentNode));
                }
                break;
            }
        }
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withIndices(Constants.NPOSP_ES_INDEX)
                .withQuery(builder);

        AbstractAggregationBuilder termsAggregations = null;
        //排序
        if (!CollectionUtils.isEmpty(sortFields)) {
            //按创建时间倒序排序
            SortBuilder sortBuilder = null;
            for (Map.Entry<String, SortOrder> sortField : sortFields.entrySet()) {
                sortBuilder = SortBuilders.fieldSort(sortField.getKey()).order(sortField.getValue());
                searchQueryBuilder.withSort(sortBuilder);
            }
        }
        //分页
        if (null != pageRequest) {
            searchQueryBuilder.withPageable(pageRequest);
        }
        //过滤字段
        if (null != includeFields && includeFields.length > 0) {
            //两个参数分别是要显示的和不显示的
            FetchSourceFilter fetchSourceFilter = new FetchSourceFilter(includeFields, null);
            searchQueryBuilder.withSourceFilter(fetchSourceFilter);
        }
        return searchQueryBuilder;
    }

    /**
     * 查询7日和半年趋势
     * 交易量/新增商户/新增代理商
     * <p>
     * 交易量按交易时间统计
     *
     * @return v1 7日数据
     * v2 半年数据
     */
    public static Tuple<List<KeyValueBean>, List<KeyValueBean>> listSevenDayAndHalfYearDataTrend(EsSearchBean searchBean) {

        String typeName = searchBean.getTypeName();

        LocalDateTime now = LocalDateTime.now();
        DateRangeAggregationBuilder seventDays = AggregationBuilders.dateRange(SEVEN_DAY_AGO).field(CREATE_TIME.getFieldName());
        if (ORDER.getTypeName().equals(typeName)) {
            seventDays = AggregationBuilders.dateRange(SEVEN_DAY_AGO).field(TRANS_TIME.getFieldName());
        }
        for (int i = 0; i < 7; i++) {
            LocalDateTime day = now.minusDays(i + 1L);
            seventDays.addRange(day.format(MM_DD), day.format(YYYY_MM_DD_00_00_00), day.format(YYYY_MM_DD_23_59_59));
        }
        if (ORDER.getTypeName().equals(typeName)) {
            seventDays.subAggregation(AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName()));
        }
        DateRangeAggregationBuilder halfYear = AggregationBuilders.dateRange(HALF_YEAR_AGO).field(CREATE_TIME.getFieldName());
        if (ORDER.getTypeName().equals(typeName)) {
            halfYear = AggregationBuilders.dateRange(HALF_YEAR_AGO).field(TRANS_TIME.getFieldName());
        }
        for (int i = 0; i < 6; i++) {
            LocalDateTime localDateTime = now.minusMonths(i + 1L);
            String start = localDateTime.with(TemporalAdjusters.firstDayOfMonth()).format(YYYY_MM_DD_00_00_00);
            String end = localDateTime.with(TemporalAdjusters.lastDayOfMonth()).format(YYYY_MM_DD_23_59_59);
            String key = localDateTime.format(YYYY_MM);
            halfYear.addRange(key, start, end);
        }
        if (ORDER.getTypeName().equals(typeName)) {
            halfYear.subAggregation(AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName()));
        }

        SearchQuery searchQuery = getEsSearchQueryBuilder(searchBean)
                .addAggregation(seventDays)
                .addAggregation(halfYear)
                .build();
        EsLog.info("交易量/新增商户/新增代理商 趋势", searchQuery);

        return elasticsearchTemplate.query(searchQuery, response -> {
            Map<String, Aggregation> aggregationMap = response.getAggregations().asMap();
            InternalDateRange seventDateRange = (InternalDateRange) aggregationMap.get(SEVEN_DAY_AGO);
            List<KeyValueBean> seventDayList = seventDateRange.getBuckets()
                    .stream()
                    .map(item -> new KeyValueBean(item.getKey(), getItemValueByType(item, typeName), ""))
                    .collect(Collectors.toList());

            InternalDateRange halfYearRange = (InternalDateRange) aggregationMap.get(HALF_YEAR_AGO);
            List<KeyValueBean> halfYearList = halfYearRange.getBuckets()
                    .stream()
                    .map(item -> new KeyValueBean(item.getKey(), getItemValueByType(item, typeName), ""))
                    .collect(Collectors.toList());
            return new Tuple<>(seventDayList, halfYearList);
        });
    }

    private static String getItemValueByType(Range.Bucket item, String typeName) {
        if (ORDER.getTypeName().equals(typeName)) {
            InternalSum sumTransAmountAggreRes = (InternalSum) item.getAggregations().asMap().get(SUM_OF_TRANS_AMOUNT);
            return sumTransAmountAggreRes == null ? "0" : new BigDecimal(sumTransAmountAggreRes.getValue()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        }
        return item.getDocCount() + "";
    }
}