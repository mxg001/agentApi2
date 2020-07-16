package com.eeepay.modules.service.impl;

import cn.hutool.json.JSONUtil;
import com.eeepay.frame.annotation.CacheData;
import com.eeepay.frame.bean.PageBean;
import com.eeepay.frame.enums.MerHlfActive;
import com.eeepay.frame.enums.OrderTransStatus;
import com.eeepay.frame.utils.StringUtils;
import com.eeepay.frame.utils.es.EsLog;
import com.eeepay.modules.bean.EsSearchBean;
import com.eeepay.modules.bean.KeyValueBean;
import com.eeepay.modules.bean.Tuple;
import com.eeepay.modules.service.OrderEsService;
import com.eeepay.modules.utils.EsSearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.eeepay.frame.enums.EsNpospField.*;
import static com.eeepay.frame.enums.EsNpospJoinType.MERCHANT;
import static com.eeepay.frame.enums.EsNpospJoinType.ORDER;

/**
 * @Title：agentApi2
 * @Description：订单业务层实现(ES)
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@Slf4j
@Service
public class OrderEsServiceImpl implements OrderEsService {

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    private static final String SUM_OF_TRANS_AMOUNT = "sumTransAmount";

    /**
     * 汇总交易订单金额
     *
     * @param searchBean ES查询条件
     * @return 汇总金额
     */
    @CacheData
    @Override
    public Tuple<Long, BigDecimal> sumTransAmountByTerms(EsSearchBean searchBean) {

        searchBean.setTypeName(ORDER.getTypeName());
        log.info("交易金额汇总【参数：{}】开始", JSONUtil.toJsonStr(searchBean));
        String agentNode = searchBean.getAgentNode();

        if (StringUtils.isBlank(agentNode)) {
            return new Tuple<>(0L, BigDecimal.ZERO);
        }
        //按交易金额进行汇总
        SumAggregationBuilder sumTransAmountBuilder = AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName());
        SearchQuery searchQuery = EsSearchUtils.getEsSearchQueryBuilder(searchBean)
                .addAggregation(sumTransAmountBuilder).build();
        EsLog.info("首页-交易金额汇总", searchQuery);

        return elasticsearchTemplate.query(searchQuery, response -> {
            try {
                BigDecimal sumOfTransAmount = BigDecimal.ZERO;
                Long countOfOrder = 0L;

                countOfOrder = response.getHits().getTotalHits();
                InternalSum aggreRes = (InternalSum) response.getAggregations().asMap().get(SUM_OF_TRANS_AMOUNT);
                log.info("交易金额汇总【参数：{}】，ES分组统计结果{}", JSONUtil.toJsonStr(searchBean), aggreRes);
                sumOfTransAmount = aggreRes == null ? BigDecimal.ZERO : new BigDecimal(aggreRes.getValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
                return new Tuple<>(countOfOrder, sumOfTransAmount);

            } catch (Exception e) {
                log.info("交易金额汇总【参数：{}】，获取ES分组统计结果异常{}", JSONUtil.toJsonStr(searchBean), e);
                return new Tuple<>(0L, BigDecimal.ZERO);
            }
        });
    }

    /**
     * 交易分组统计
     * 单个字段分组
     *
     * @param searchBean ES查询条件
     * @return 分组统计结果：订单数量、订单金额
     */
    @CacheData
    @Override
    public List<Map<String, Object>> groupSummaryTransOrder(EsSearchBean searchBean) {

        searchBean.setTypeName(ORDER.getTypeName());
        log.info("交易分组统计【参数：{}】开始", JSONUtil.toJsonStr(searchBean));
        List<Map<String, Object>> resList = new ArrayList<>();
        String agentNode = searchBean.getAgentNode();
        String[] groupFields = searchBean.getGroupFields();
        //代理商节点和分组字段不能为空
        if (StringUtils.isBlank(agentNode) || (null == groupFields || groupFields.length == 0)) {
            return resList;
        }
        //分组
        TermsAggregationBuilder groupBuilders = AggregationBuilders.terms(groupFields[0]).field(groupFields[0]);
        //统计交易金额
        SumAggregationBuilder sumTransAmountBuilder = AggregationBuilders.sum(SUM_OF_TRANS_AMOUNT).field(TRANS_AMOUNT.getFieldName());
        groupBuilders.subAggregation(sumTransAmountBuilder);

        NativeSearchQuery searchQuery = EsSearchUtils.getEsSearchQueryBuilder(searchBean)
                .addAggregation(groupBuilders).build();
        EsLog.info("交易分组汇总统计", searchQuery);
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        Terms summaryTerm = aggregations.get(groupFields[0]);
        Map<String, Object> subMap = new HashMap<>();
        for (Terms.Bucket bk : summaryTerm.getBuckets()) {
            //相当于当前分组对应的订单数量
            long countOrder = bk.getDocCount();
            String key = bk.getKeyAsString();
            if (StringUtils.isNotBlank(key)) {
                //得到所有子聚合
                Map subaggMap = bk.getAggregations().asMap();
                //获取汇总的订单金额，四舍五入保留2位小数
                String sumOrderAmountStr = ((InternalSum) subaggMap.get(SUM_OF_TRANS_AMOUNT)).getValueAsString();
                BigDecimal sumOrderAmount = new BigDecimal(sumOrderAmountStr).setScale(2, BigDecimal.ROUND_HALF_UP);

                subMap = new HashMap<>();
                subMap.put("key", key);
                subMap.put("countOrder", countOrder);
                subMap.put("sumOrderAmount", sumOrderAmount.toString());
                resList.add(subMap);
            }
        }
        return resList;
    }

    /**
     * 交易查询
     *
     * @param searchBean ES查询条件
     * @return
     */
    @CacheData
    @Override
    public PageBean queryTransOrderForPage(EsSearchBean searchBean) {

        searchBean.setTypeName(ORDER.getTypeName());
        log.info("交易明细查询【参数：{}】开始", JSONUtil.toJsonStr(searchBean));

        String agentNode = searchBean.getAgentNode();
        if (StringUtils.isBlank(agentNode)) {
            return null;
        }
        NativeSearchQuery searchQuery = EsSearchUtils.getEsSearchQueryBuilder(searchBean).build();
        EsLog.info("交易明细查询", searchQuery);

        return elasticsearchTemplate.query(searchQuery, response -> {
            List<Map<String, Object>> pageContent = Arrays.stream(response.getHits().getHits()).map(item -> {
                Map<String, Object> sourceAsMap = item.getSourceAsMap();
                sourceAsMap.put("trans_status_zh", OrderTransStatus.getZhByStatus(String.valueOf(sourceAsMap.get("trans_status"))));
                return sourceAsMap;
            }).collect(Collectors.toList());
            Long totalCount = response.getHits().getTotalHits();
            PageRequest pageRequest = searchBean.getPageRequest();
            pageRequest = (null == pageRequest ? PageRequest.of(0, 10) : pageRequest);
            PageBean page = new PageBean(pageRequest.getPageNumber(), pageRequest.getPageSize(), totalCount, pageContent);
            return page;
        });
    }

    /**
     * 商户新增分组统计
     *
     * @param searchBean ES查询条件
     * @param teamType   组织类型，1：主组织，2：子组织
     * @return 分组统计结果：新增总商户数量，新增激活商户数量
     */
    @CacheData
    @Override
    public List<Map<String, Object>> groupMerchantByTeamAndHlfActive(EsSearchBean searchBean, int teamType) {

        searchBean.setTypeName(MERCHANT.getTypeName());
        log.info("商户新增分组统计【参数：{}】开始", JSONUtil.toJsonStr(searchBean));
        List<Map<String, Object>> resList = new ArrayList<>();
        String agentNode = searchBean.getAgentNode();
        //代理商节点和分组字段不能为空
        if (StringUtils.isBlank(agentNode)) {
            return resList;
        }
        //添加分组字段
        TermsAggregationBuilder teamGroupBuilders = null;
        if (teamType == 1) {
            //主组织分组
            teamGroupBuilders = AggregationBuilders.terms(TEAM_ID.getFieldName()).field(TEAM_ID.getFieldName());
        } else {
            //子组织分组
            teamGroupBuilders = AggregationBuilders.terms(TEAM_ENTRY_ID.getFieldName()).field(TEAM_ENTRY_ID.getFieldName() + ".keyword");
        }
        TermsAggregationBuilder hlfActiveGroupBuilders = AggregationBuilders.terms(HLF_ACTIVE.getFieldName()).field(HLF_ACTIVE.getFieldName());
        teamGroupBuilders.subAggregation(hlfActiveGroupBuilders);

        NativeSearchQuery searchQuery = EsSearchUtils.getEsSearchQueryBuilder(searchBean)
                .addAggregation(teamGroupBuilders).build();
        EsLog.info("商户新增分组统计", searchQuery);
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        Terms summaryTerm = aggregations.get(teamType == 1 ? TEAM_ID.getFieldName() : TEAM_ENTRY_ID.getFieldName());
        Map<String, Object> subMap = new HashMap<>();
        for (Terms.Bucket tb : summaryTerm.getBuckets()) {
            //当前组织机构下的所有新增商户
            long countMer = tb.getDocCount();
            String key = tb.getKeyAsString();
            if (StringUtils.isNotBlank(key)) {
                subMap = new HashMap<>();
                subMap.put("key", key);
                subMap.put("countMer", countMer);
                long activeCountMer = 0;
                //获取激活状态分组结果
                Terms hlfActiveTerms = tb.getAggregations().get(HLF_ACTIVE.getFieldName());
                for (Terms.Bucket subTb : hlfActiveTerms.getBuckets()) {
                    long subCountMer = subTb.getDocCount();
                    String subKey = subTb.getKeyAsString();
                    if (MerHlfActive.ACTIVE.getStatus().equals(subKey)) {
                        activeCountMer += subCountMer;
                    }
                }
                subMap.put("activeCountMer", activeCountMer);
                resList.add(subMap);
            }
        }
        return resList;
    }

    @CacheData(type = CacheData.CacheType.ALL_DAY)
    @Override
    public Tuple<List<KeyValueBean>, List<KeyValueBean>> listSevenDayAndHalfYearDataTrend(EsSearchBean searchBean) {
        return EsSearchUtils.listSevenDayAndHalfYearDataTrend(searchBean);
    }
}
