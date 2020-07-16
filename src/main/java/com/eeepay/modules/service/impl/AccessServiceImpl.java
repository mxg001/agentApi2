package com.eeepay.modules.service.impl;

import com.eeepay.frame.exception.AppException;
import com.eeepay.frame.utils.StringUtils;
import com.eeepay.modules.bean.AgentInfo;
import com.eeepay.modules.dao.AccessDao;
import com.eeepay.modules.dao.AgentInfoDao;
import com.eeepay.modules.service.AccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-27 09:53
 */
@Slf4j
@Service
public class AccessServiceImpl implements AccessService {
    @Resource
    private AccessDao accessDao;
    @Resource
    private AgentInfoDao agentInfoDao;

    @Override
    public boolean canAccessTheMerchant(String loginAgentNode, String merchantNo, boolean isOwn) {
        return accessDao.canAccessTheMerchant(loginAgentNode, merchantNo, isOwn) > 0;
    }

    @Override
    public boolean canAccessTheMerchantWithKey(String loginAgentNode, String merchantKey, boolean isOwn) {
        return accessDao.canAccessTheMerchantWithKey(loginAgentNode, merchantKey, isOwn) > 0;
    }

    @Override
    public boolean canAccessTheAgent(String loginAgentNode, String agentNo) {
        return accessDao.canAccessTheAgent(loginAgentNode, agentNo) > 0;
    }

    @Override
    public String checkAndGetAgentNode(String loginAgentNo, String agentNo) {
        if (StringUtils.isBlank(agentNo)) {
            return loginAgentNo;
        }
        AgentInfo agentInfo = agentInfoDao.selectByAgentNo(agentNo);
        if (!canAccessTheAgent(loginAgentNo, agentNo) || agentInfo == null) {
            throw new AppException("无权操作");
        }
        return agentInfo.getAgentNode();
    }

    /**
     * 根据V2商户号/商户名模糊查询超级还商户号
     * @param v2MerKey
     * @param currAgentNode
     * @param isOwn
     * @return
     */
    @Override
    public List<String> getRepayMerNoByV2MerKey(String v2MerKey, String currAgentNode, boolean isOwn) {
        return accessDao.getRepayMerNoByV2MerKey(v2MerKey, currAgentNode, isOwn);
    }
}
