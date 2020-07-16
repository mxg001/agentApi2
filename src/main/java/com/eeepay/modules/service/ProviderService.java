package com.eeepay.modules.service;


import com.eeepay.frame.enums.RepayEnum;
import com.eeepay.modules.bean.AgentInfo;
import com.eeepay.modules.bean.ProviderBean;

import java.util.List;

/**
 * Created by 666666 on 2017/10/27.
 */
public interface ProviderService {
    /**
     * 开通oem服务
     *
     * @param agentNoList
     * @param loginAgent
     * @return
     */
    boolean openOemServiceCost(List<String> agentNoList, AgentInfo loginAgent, RepayEnum type);

    /**
     * 查询oemServiceCost信息
     *
     * @param agentNo
     * @param type
     * @return
     */
    ProviderBean queryOemServiceCost(String agentNo, String type);

    /**
     * 根据代理商编号及服务类型查询
     * @param agentNo
     * @param type
     * @return
     */
    ProviderBean queryServiceCost(String agentNo, String type);
}
