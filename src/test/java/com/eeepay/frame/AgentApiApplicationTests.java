package com.eeepay.frame;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.eeepay.frame.bean.AppDeviceInfo;
import com.eeepay.frame.config.SpringHolder;
import com.eeepay.frame.enums.OrderTransStatus;
import com.eeepay.frame.enums.QueryScope;
import com.eeepay.frame.utils.ALiYunOssUtil;
import com.eeepay.frame.utils.Constants;
import com.eeepay.frame.utils.WebUtils;
import com.eeepay.frame.utils.external.AccountApiEnum;
import com.eeepay.frame.utils.external.CoreApiEnum;
import com.eeepay.frame.utils.external.ExternalApiUtils;
import com.eeepay.frame.utils.external.FlowmoneyApiEnum;
import com.eeepay.frame.utils.redis.RedisUtils;
import com.eeepay.modules.bean.AgentInfo;
import com.eeepay.modules.bean.EsSearchBean;
import com.eeepay.modules.bean.KeyValueBean;
import com.eeepay.modules.dao.MerchantDao;
import com.eeepay.modules.dao.SmsDao;
import com.eeepay.modules.dao.TransOrderDao;
import com.eeepay.modules.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;

/**
 * 系统测试类
 */
@RunWith(SpringRunner.class)
//@SpringBootTest(classes = AgentApiApplication.class)
@SpringBootTest(properties = {"spring.profiles.active=test"})
public class AgentApiApplicationTests {

    @Test
    public void redisTest() {
        AppDeviceInfo deviceInfo = new AppDeviceInfo();
        deviceInfo.setName("abc");
        deviceInfo.setAppName("asdsadsad");
        RedisUtils.set("deviceInfo", deviceInfo);
        AppDeviceInfo dbInfo = RedisUtils.get("deviceInfo");
        dbInfo.setName("123456");
        dbInfo.setAppName("0987654321");
        AppDeviceInfo dbInfo2 = RedisUtils.get("deviceInfo");
        System.out.println(JSONUtil.toJsonStr(dbInfo2));
        RedisUtils.del("dbInfo2");
    }

    @Test
    public void sysConfigTest() {
        System.out.println(WebUtils.getSysConfigValueByKey("PURSE_PAY_OUT_ACC_NO_NEWEPTOK"));
    }


    @Test
    public void signTest() {
        Map<String, String> bodyParams = new HashMap<>();
        String signSrc = WebUtils.buildSignSrc(false, bodyParams, "sign");
        System.out.println(signSrc);
    }


    @Test
    public void queryAgentInfoByAgentNodeOrAgentNoTest() {
        AgentEsService agentEsService = SpringHolder.getBean(AgentEsService.class);
        Map<String, Object> agentInfo = agentEsService.queryAgentInfoByAgentNodeOrAgentNo("0-1572-2113-");
        System.out.println(JSONUtil.toJsonStr(agentInfo));
    }

    @Test
    public void orderTransStatusTest() {
        System.out.println(OrderTransStatus.SUCCESS.getStatus());
        System.out.println(OrderTransStatus.getZhByStatus(OrderTransStatus.SUCCESS.getStatus()));
    }

    @Test
    public void dateUtilTest() {
        Date now = DateUtil.parse("2019-02-01", "yyyy-MM-dd");
        System.out.println(DateUtil.format(DateUtil.beginOfMonth(now), "yyyy-MM-dd"));
        System.out.println(DateUtil.format(DateUtil.endOfMonth(now), "yyyy-MM-dd"));
    }

    @Test
    public void transOrderDaoTest() {
        TransOrderDao transOrderDao = SpringHolder.getBean(TransOrderDao.class);
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", "SK756779786131036468");
        List<Map<String, Object>> maps = transOrderDao.queryTransOrderByParams(params);
        System.out.println(maps.toString());

        Map<String, BigDecimal> profitMap = transOrderDao.queryAgentProfitByOrderNo("SK756779786131036468", new String[]{"profits_1", "profits_2", "profits_3", "profits_4"});
        System.out.println(profitMap);
    }

    @Test
    public void groupSummaryMerchantInfoTest() {
        OrderEsService orderEsService = SpringHolder.getBean(OrderEsService.class);
        EsSearchBean searchBean = new EsSearchBean();
        searchBean.setQueryScope(QueryScope.ALL);
        searchBean.setAgentNode("0-1446-");
        System.out.println(orderEsService.groupMerchantByTeamAndHlfActive(searchBean, 1));

    }

    @Test
    public void listSevenDayAndHalfYearDataTrendTest() {
//        EsSearchBean searchBean = new EsSearchBean();
//        searchBean.setTypeName(EsNpospJoinType.ORDER.getTypeName());
//        searchBean.setAgentNode("0-1446-");
//        searchBean.setAgentNo("1446");
//        searchBean.setQueryScope(QueryScope.getByScopeCode("ALL"));
//        searchBean.setTransStatus(OrderTransStatus.SUCCESS.getStatus());
//
//        Tuple<List<KeyValueBean>, List<KeyValueBean>> orderTrendRes = EsSearchUtils.listSevenDayAndHalfYearDataTrend(searchBean);
//        System.out.println(JSONUtil.toJsonStr(orderTrendRes));
    }

    @Test
    public void smsDaoTest() {
        SmsDao smsDao = SpringHolder.getBean(SmsDao.class);
        System.out.println(smsDao.getLatest5MinuteSmsCode("18998718665", "200010"));
        int count = smsDao.insertSmsCode("18603049008", "123456", "200010");
        System.out.println(count);
    }

    @Test
    public void java8MapTest() {
        MerchantDao merchantDao = SpringHolder.getBean(MerchantDao.class);
        Map<String, String> resultMap = new HashMap<>();
        List<Map<String, Object>> result = merchantDao.listTeamNameByAgentNo("1446");
        Optional.ofNullable(result)
                .orElse(new ArrayList<>())
                .forEach(item -> {
                    resultMap.put(Objects.toString(item.get("team_id")), Objects.toString(item.get("team_name")));
                });
        List<KeyValueBean> teamInfos = new ArrayList<>();
        Optional.ofNullable(resultMap)
                .orElse(new HashMap<>())
                .forEach((k, v) ->
                        teamInfos.add(new KeyValueBean(k, v))
                );
        System.out.println(teamInfos);
    }

    @Test
    public void testApi() {
        Arrays.stream(AccountApiEnum.values()).forEach(item -> System.out.println(ExternalApiUtils.getAccountPath(item)));
        Arrays.stream(CoreApiEnum.values()).forEach(item -> System.out.println(ExternalApiUtils.getCorePath(item)));
        Arrays.stream(FlowmoneyApiEnum.values()).forEach(item -> System.out.println(ExternalApiUtils.getFlowmoneyPath(item)));
    }

    @Test
    public void getAgentTeamsTest() {
        MerchantInfoService merchantInfoService = SpringHolder.getBean(MerchantInfoService.class);
        List<Map<String, Object>> teamMapList = merchantInfoService.getAgentTeams("1446", false);
        System.out.println(teamMapList.toString());
    }

    @Test
    public void updateMbpAndOrderEntryTeamByMerTest() throws Exception {
        EsDataMigrateService esDataMigrateService = SpringHolder.getBean(EsDataMigrateService.class);
        esDataMigrateService.updateMbpAndOrderEntryTeamByMer("258121000031112", "100070-002");
        Thread.sleep(1000 * 500);
    }

    @Test
    public void aliYunGenUrlTest() {
        String imgUrl = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, "abcd_1584926408363_84085.jpg");
        System.out.println("海报来了：" + imgUrl);
    }

    @Test
    public void aliYunGenUrlTest2() {
        String agentNo = "1114669";
        AgentInfoService agentInfoService = SpringHolder.getBean(AgentInfoService.class);
        AgentInfo search = agentInfoService.queryAgentInfo(agentNo);
        System.out.println(search.getAgentNo() + "-------" + search.getAgentNode());
    }
    @Test
    public void tuomingTest() throws InterruptedException {
        String idCardNo = "420922199208083455";
        String bankNo = "6227002661020329884";
        idCardNo = idCardNo.replaceAll("(?<=\\w{2})\\w(?=\\w{2})", "*");
        bankNo = bankNo.replaceAll("(?<=\\w{6})\\w(?=\\w{4})", "*");
        System.out.println(idCardNo);
        System.out.println(bankNo);
    }

}
