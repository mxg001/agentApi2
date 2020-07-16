package com.eeepay.modules.service;

import com.eeepay.modules.bean.UserInfoBean;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/16 14:51
 */
public interface MachineManageService {
    /**
     * 获取该用户下所有机具信息
     */
    List<Map<String, Object>> getAllByCondition(UserInfoBean userInfoBean, Map<String, Object> params_map);

    /**
     * 获取代理商信息
     */
    Map<String, Object> getAgentInfoByAgentNo(String agent_no);

    /**
     * 获取机具信息
     */
    Map<String, Object> getTermInfoBySn(String sn);

    /**
     * 下发机具
     */
    int updateTerToSend(String sql_sn_array, String agent_no, String agent_node);

    /**
     * 回收机具
     */
    int updateTerToBack(String sql_sn_array, String agent_no, String agent_node);

    /**
     * 查询代理商功能开关
     */
    Map<String, Object> getFunctionManage(String function_number);

    /**
     * 查询活动信息
     */
    String getIsTakeActivity(String merchant_no, String sn);

    /**
     * 查询活动信息
     */
    String getIsSuperActivity(String agent_no, String sn);


    /**
     * 解绑机具
     */
    int terminalRelease(String sn);

    /**
     * 流动记录
     */
    int insTerminalOperate(String agent_no, String for_operater, int oper_num, String sn_array, String oper_detail_type, String oper_type);
    /**
     * 流动时间
     */
    int insAgentTerminalOperate(String agent_no, String sn_array, String oper_detail_type, String oper_type, Date date);

    /**
     * 机具流动记录查询
     */
    List<Map<String, Object>> getSnSendAndRecInfo(Map<String, Object> params_map);

    /**
     * 机具流动详情查询
     */
    String getSnSendAndRecDetail(String id);

    /**
     * 获取代理商权限控制信息
     */
    String getAgentFunction(String agent_no, String function_number);

    /**
     * 是否黑名单 不包含下级
     */
    long countBlacklistNotContains(String agentNo);

    /**
     * 是否黑名单 包含下级
     */
    long countBlacklistContains(String agentNode);

    /*
    查询当前代理商的一级代理商勾选的欢乐返子类型
     */
    List<Map<String, Object>> getActivityTypes(String one_agent_no);
}
