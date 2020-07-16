package com.eeepay.modules.service.impl;

import com.eeepay.frame.annotation.DataSourceSwitch;
import com.eeepay.frame.utils.StringUtils;
import com.eeepay.modules.bean.UserInfoBean;
import com.eeepay.modules.dao.MachineManageDao;
import com.eeepay.modules.dao.TestDao;
import com.eeepay.modules.service.MachineManageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/16 14:51
 */
@Slf4j
@Service
public class MachineManageServiceImpl implements MachineManageService {
    @Resource
    private MachineManageDao machineManageDao;

    /**
     * 获取该用户下所有机具信息
     */
    public List<Map<String, Object>> getAllByCondition(UserInfoBean userInfoBean, Map<String, Object> params_map) {
        return machineManageDao.getAllByCondition(userInfoBean, params_map);
    }

    /**
     * 获取代理商信息
     */
    public Map<String, Object> getAgentInfoByAgentNo(String agent_no) {
        return machineManageDao.getAgentInfoByAgentNo(agent_no);
    }

    /**
     * 获取机具信息
     */
    public Map<String, Object> getTermInfoBySn(String sn) {
        return machineManageDao.getTermInfoBySn(sn);
    }

    /**
     * 下发机具
     */
    @DataSourceSwitch
    public int updateTerToSend(String sql_sn_array, String agent_no, String agent_node) {
        return machineManageDao.updateTerToSend(sql_sn_array, agent_no, agent_node);
    }

    /**
     * 回收机具
     */
    @DataSourceSwitch
    public int updateTerToBack(String sql_sn_array, String agent_no, String agent_node) {
        return machineManageDao.updateTerToBack(sql_sn_array, agent_no, agent_node);
    }

    /**
     * 查询代理商功能开关
     */
    public Map<String, Object> getFunctionManage(String function_number) {
        return machineManageDao.getFunctionManage(function_number);
    }

    /**
     * 查询活动信息
     */
    public String getIsTakeActivity(String merchant_no, String sn){
        return machineManageDao.getIsTakeActivity(merchant_no, sn);
    }

    /**
     * 查询活动信息
     */
    public String getIsSuperActivity(String agent_no, String sn){
        return machineManageDao.getIsSuperActivity(agent_no, sn);
    }

    /**
     * 解绑机具
     */
    public int terminalRelease(String sn){
        return machineManageDao.terminalRelease(sn);
    }

    /**
     * 插入流动记录
     */
    public int insTerminalOperate(String agent_no, String for_operater, int oper_num, String sn_array, String oper_detail_type, String oper_type){
        return machineManageDao.insTerminalOperate(agent_no, for_operater, oper_num, sn_array, oper_detail_type, oper_type);
    }

    /**
     * 插入流动记录时间
     */
    @Override
    public int insAgentTerminalOperate(String agent_no, String sn_array, String oper_detail_type, String oper_type, Date date) {
        if (StringUtils.isNotEmpty(sn_array)){
            String[] snArray = sn_array.split(",");
            for (String sn : snArray) {
                machineManageDao.insertTerminalOperateTime(agent_no,sn,oper_detail_type,oper_type,date);
            }

        }
        return 0;
    }

    /**
     * 机具流动记录查询
     */
    public List<Map<String, Object>> getSnSendAndRecInfo(Map<String, Object> params_map){
        return machineManageDao.getSnSendAndRecInfo(params_map);
    }

    /**
     * 机具流动详情查询
     */
    public String getSnSendAndRecDetail(String id){
        return machineManageDao.getSnSendAndRecDetail(id);
    }

    /**
     * 获取代理商权限控制信息
     */
    public String getAgentFunction(String agent_no, String function_number){
        return machineManageDao.getAgentFunction(agent_no, function_number);
    }


    public long countBlacklistNotContains(String agentNo){
        return machineManageDao.countBlacklistNotContains(agentNo);
    }

    public long countBlacklistContains(String agentNode){
        return machineManageDao.countBlacklistContains(agentNode);
    }

    /*
   查询当前代理商的一级代理商勾选的欢乐返子类型
   */
    public List<Map<String, Object>> getActivityTypes(String one_agent_no) {
        return machineManageDao.getActivityTypes(one_agent_no);
    }

}

