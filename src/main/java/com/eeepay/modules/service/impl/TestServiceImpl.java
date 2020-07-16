package com.eeepay.modules.service.impl;

import com.eeepay.frame.annotation.DataSourceSwitch;
import com.eeepay.frame.db.DataSourceType;
import com.eeepay.frame.exception.AppException;
import com.eeepay.frame.utils.StringUtils;
import com.eeepay.modules.dao.TestDao;
import com.eeepay.modules.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-08 15:10
 */
@Service
@Slf4j
public class TestServiceImpl implements TestService {
    @Resource
    private TestDao testDao;
    @Resource
    private TestService testService;

    @Override
    @DataSourceSwitch(DataSourceType.BILL)
    public Map<String, Object> getAgentByWrite(String agentNo) {
        log.info("代理商编号: {}", agentNo);
        if (StringUtils.equals(agentNo, "error")) {
            throw new AppException(400, "异常");
        }
        return testDao.getAgentByAgentNo(agentNo);
    }

    @Override
    public Map<String, Object> getAgentByRead(String agentNo) {
        log.info("代理商编号: {}", agentNo);
        return testDao.getAgentByAgentNo(agentNo);
    }

    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public List<Map<String, Object>> getAgent(String agentNo) {
        Map<String, Object> agentByRead = testService.getAgentByRead(agentNo);
        Map<String, Object> agentByWrite = testService.getAgentByWrite(agentNo);
        return Arrays.asList(agentByWrite, agentByRead, testDao.getAgentByAgentNo(agentNo));
    }

    @Override
    @DataSourceSwitch
    public List<Map<String, Object>> getAgent2(String agentNo) {
        Map<String, Object> agentByRead = getAgentByRead(agentNo);
        Map<String, Object> agentByWrite = getAgentByWrite(agentNo);
        return Arrays.asList(agentByWrite, agentByRead, testDao.getAgentByAgentNo(agentNo));
    }
}
