package com.eeepay.modules.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.eeepay.frame.annotation.CurrentUser;
import com.eeepay.frame.annotation.SwaggerDeveloped;
import com.eeepay.frame.bean.ResponseBean;
import com.eeepay.frame.utils.ClientInterface;
import com.eeepay.frame.utils.GsonUtils;
import com.eeepay.frame.utils.StringUtils;
import com.eeepay.frame.utils.swagger.SwaggerNoteLmc;
import com.eeepay.modules.bean.MerchantInfo;
import com.eeepay.modules.bean.SnReceiveInfo;
import com.eeepay.modules.bean.UserInfoBean;
import com.eeepay.modules.service.MachineManageService;
import com.eeepay.modules.service.MerchantInfoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author lmc
 * @date 2019/5/16 14:51
 */
@Api(description = "机具管理")
@RestController
@RequestMapping("/machinemanage")
public class MachineManageController {
    @Resource
    private MachineManageService machineManageService;

    @Resource
    private MerchantInfoService merchantInfoService ;

    @SwaggerDeveloped
    @ApiOperation(value = "机具筛选查询信息", notes = SwaggerNoteLmc.QUERY_MACHINE_INFO)
    @PostMapping("/getAllByCondition")
    public ResponseBean getAllByCondition(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        //筛选字段
        //select_type    1-下发  2-全部
        //title_type    1-全部机具  2-我的机具
        //is_all    0-未勾中全选  1-勾中全选
        // sn
        //terminal_id
        //psam_no
        //open_status     分配状态
        //mername_no
        //agentname_no
        //sn_min  (select_type=1下选填，但是sn_min和sn_max必须同时填或者不填)
        //sn_max  (select_type=1下选填，但是sn_min和sn_max必须同时填或者不填)
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        int pageNo = Integer.parseInt(StringUtils.filterNull(params_map.get("pageNo")));
        int pageSize = Integer.parseInt(StringUtils.filterNull(params_map.get("pageSize")));
        String is_all = StringUtils.filterNull(params_map.get("is_all"));
        //设置分页信息，分别是当前页数和每页显示的总记录数
        long count = 0;
        List<Map<String, Object>> list = null;
        //全选
        if ("1".equals(is_all)) {
            list = machineManageService.getAllByCondition(userInfoBean, params_map);
            return ResponseBean.success(list, list.size());
        }

        //第一页查询总页数，其它页不查询
        if (pageNo == 1) {
            Page page = PageHelper.startPage(pageNo, pageSize);
            list = machineManageService.getAllByCondition(userInfoBean, params_map);
            count = page.getTotal();
        } else {
            PageHelper.startPage(pageNo, pageSize, false);
            list = machineManageService.getAllByCondition(userInfoBean, params_map);
        }

        return ResponseBean.success(list, count);
    }

    @SwaggerDeveloped
    @ApiOperation(value = "机具管理", notes = SwaggerNoteLmc.MANAGE_TERMINAL)
    @PostMapping("/manageTerminal")
    public ResponseBean manageTerminal(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        //处理字段
        //receive_agent_no  (select_type=1 必填)
        //receive_agent_node  (select_type=1 必填)
        //select_type    1-下发  2-回收
        //sn_array_str        123456,123457,1234587
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        String select_type = StringUtils.filterNull(params_map.get("select_type"));
        String receive_agent_no = StringUtils.filterNull(params_map.get("receive_agent_no"));
        String receive_agent_node = StringUtils.filterNull(params_map.get("receive_agent_node"));
        String sn_array_str = StringUtils.filterNull(params_map.get("sn_array_str"));
        String agent_no = userInfoBean.getAgentNo();
        String agent_node = userInfoBean.getAgentNode();


        if ("".equals(sn_array_str)) {
            return ResponseBean.error("下发机具sn号不能为空");
        }

        //下发操作
        if ("1".equals(select_type)) {
            return distributeTerminal(agent_no, userInfoBean.getAgentName(), sn_array_str, receive_agent_no,  receive_agent_node);
        }

        //回收操作
        if ("2".equals(select_type)) {
            return takeBackTerminal(agent_no, agent_node, userInfoBean.getAgentName(), sn_array_str);
        }
        return ResponseBean.success(null, "");
    }

    @SwaggerDeveloped
    @ApiOperation(value = "机具流动记录列表查询", notes = SwaggerNoteLmc.SN_SEND_AND_REC_INFO)
    @PostMapping("/getSnSendAndRecInfo")
    public ResponseBean getSnSendAndRecInfo(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        //筛选字段
        //oper_type  必填，筛选栏类型 1-入库  2-出库
        //date_start  格式YYYY-MM-DD 00:00:00,选填, 但是date_start和date_end 必须同时填或者不填
        //date_end  格式YYYY-MM-DD 23:59:59,选填，但是date_start和date_end 必须同时填或者不填
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        params_map.put("agent_no", userInfoBean.getAgentNo());
        int pageNo = Integer.parseInt(StringUtils.filterNull(params_map.get("pageNo")));
        int pageSize = Integer.parseInt(StringUtils.filterNull(params_map.get("pageSize")));
        if (pageNo == 1) {
            List<Map<String, Object>> list = machineManageService.getSnSendAndRecInfo(params_map);
            int count = 0;
            for (Map<String, Object> map : list) {
                count += Integer.parseInt(map.get("oper_num").toString());
            }
            return ResponseBean.success(list, count);
        } else {
            //设置分页信息，分别是当前页数和每页显示的总记录数
            PageHelper.startPage(pageNo, pageSize, false);
            List<Map<String, Object>> list = machineManageService.getSnSendAndRecInfo(params_map);
            return ResponseBean.success(list);
        }
    }

    @SwaggerDeveloped
    @ApiOperation(value = "机具流动记录详情查询", notes = SwaggerNoteLmc.SN_SEND_AND_REC_DETAIL)
    @PostMapping("/getSnSendAndRecDetail")
    public ResponseBean getSnSendAndRecDetail(@RequestBody String params) {
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        String id = StringUtils.filterNull(params_map.get("id"));
        int pageNo = Integer.parseInt(StringUtils.filterNull(params_map.get("pageNo")));
        int pageSize = Integer.parseInt(StringUtils.filterNull(params_map.get("pageSize")));
        String sn_str = machineManageService.getSnSendAndRecDetail(id);
        String[] sn_array = sn_str.split(",");
        List<String> list = new ArrayList<>();
        int currIdx = (pageNo > 1 ? (pageNo - 1) * pageSize : 0);
        for (int i = 0; i < pageSize && i < sn_array.length - currIdx; i++) {
            String sn = sn_array[currIdx + i];
            list.add(sn);
        }
        return ResponseBean.success(list, sn_array.length);
    }

    @SwaggerDeveloped
    @ApiOperation(value = "机具解绑", notes = SwaggerNoteLmc.TERMINAL_RELEASE)
    @PostMapping("/terminalRelease")
    public ResponseBean terminalRelease(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        String sn = StringUtils.filterNull(params_map.get("sn"));

        if (1 != userInfoBean.getAgentLevel()) {
            return ResponseBean.error("只有一级代理商才可以解绑操作");
        }

        //获取代理商解绑操作配置
        Map<String, Object> map = machineManageService.getFunctionManage("030");

        if(!CollectionUtils.isEmpty(map)){
            if ("0".equals(StringUtils.filterNull(map.get("function_switch")))){
                return ResponseBean.error("该代理商没有解绑功能权限");
            }else {
                //开启代理商就只能对应的代理商才能进行解绑操作
                if ("1".equals(StringUtils.filterNull(map.get("agent_control")))) {

                    if ("".equals(StringUtils.filterNull(machineManageService.getAgentFunction(userInfoBean.getAgentNo(), "030")))) {
                        return ResponseBean.error("该代理商没有解绑功能权限");
                    }
                }
            }
            Map<String, Object> term_info_map = machineManageService.getTermInfoBySn(sn);
            //校验该sn是否是该代理商链条下所有的
            String agent_node = StringUtils.filterNull(term_info_map.get("agent_node"));
            if(!agent_node.startsWith(userInfoBean.getAgentNode())) {
                return ResponseBean.error("该机具不属于该代理商，暂不可解绑");
            }
            String merchant_no = StringUtils.filterNull(term_info_map.get("merchant_no"));
            if("".equals(merchant_no)){
                return ResponseBean.error("该机具未绑定商户，暂不可解绑");
            }

            //判断是否参与满奖活动
            if(!"".equals(merchant_no) && !"".equals(StringUtils.filterNull(machineManageService.getIsTakeActivity(merchant_no, sn)))){
                return ResponseBean.error("该机具已经参加过活动，暂不可解绑");
            }

            //判断是否参与超级推活动
            if(!"".equals(StringUtils.filterNull(machineManageService.getIsSuperActivity(StringUtils.filterNull(term_info_map.get("agent_no")), sn)))){
                return ResponseBean.error("机具硬件类型为超级推机具，不允许该操作");
            }

            int num = machineManageService.terminalRelease(sn);
            if (num > 0) {
                /**
                 * 解绑成功后判断是智能盛POS组织的才需要清除长token,app_no   31 team_id   100060
                 * 接口地址：/mer/user/clearLongToken
                 * 请求格式：form表单键值对post
                 * 请求参数：merchantNo
                 */
                //判断组织,是不是判断商户的组织
                MerchantInfo merchantInfo = merchantInfoService.selectByMerchantNo(merchant_no);
                if(merchantInfo != null && "100060".equals(merchantInfo.getTeamId())){
                    ClientInterface.cleanLongToken(merchant_no);
                }
                return ResponseBean.success(null, "解绑成功");
            }else {
                return ResponseBean.error("解绑失败");
            }

        }
        return ResponseBean.success();
    }

    /*
    根据成功执行的sn字符串数组获取以代理商编号分组代理商信息
     */
    public Map<String, SnReceiveInfo> groupByAgentNo(String sql_success_str, Map<String, String> sn_agent_map){
        String[] sn_array = sql_success_str.split(",");
        ArrayList<String> agent_no_array = new ArrayList<>();
        HashMap<String, SnReceiveInfo> map = new HashMap<>();
        for(String sn : sn_array){
            String agent_no = sn_agent_map.get(sn);
            if(!agent_no_array.contains(agent_no)){
                agent_no_array.add(agent_no);
                SnReceiveInfo snReceiveInfo = new SnReceiveInfo();
                snReceiveInfo.setAgentNo(agent_no);
                snReceiveInfo.setSnStr(sn);
                snReceiveInfo.setSuccessCount(1);
                map.put(agent_no, snReceiveInfo);
            }else{
                SnReceiveInfo snReceiveInfo = map.get(agent_no);
                snReceiveInfo.setSnStr(snReceiveInfo.getSnStr() + "," + sn);
                snReceiveInfo.setSuccessCount(snReceiveInfo.getSuccessCount() + 1);
                map.put(agent_no, snReceiveInfo);
            }
        }
        return map;
    }

    /*
    机具下发
     */
    public ResponseBean distributeTerminal(String agent_no,String current_agent_name, String sn_array_str,String receive_agent_no, String receive_agent_node) {
        String parent_agent_no = StringUtils.filterNull(machineManageService.getAgentInfoByAgentNo(receive_agent_no).get("parent_id"));
        //下发的代理商必须是直属的
        if (!parent_agent_no.equals(agent_no)) {
            return ResponseBean.error("跨级代理商，仅允许下发直属代理商");
        }

        String[] sn_array = sn_array_str.split(",");
        int length = sn_array.length;
        //需要执行的机具更新sn字符串
        String can_exe_sql_str = "";
        //分片sql的list
        ArrayList<String> sn_sql = new ArrayList<>();
        //流动记录统计成功的sql
        String sql_success_str = "";
        //遍历去除超级推机具并封装成小于500分片的机具sql
        for (int i = 0; i < length; i++) {
            String sn = sn_array[i];
            //是否参与了超级推活动。参与则不可以下发（下发已经去除了）
//            if ("".equals(StringUtils.filterNull(machineManageService.getIsSuperActivity(StringUtils.filterNull(machineManageService.getTermInfoBySn(sn).get("agent_no")), sn)))) {
//                can_exe_sql_str += "'"+ sn + "',";
//            }

            //sn加引号
            can_exe_sql_str += "'"+ sn + "',";
            //大于500需要进行分批执行
            if(i != 0 && i % 500 == 0 && !"".equals(can_exe_sql_str)) {
                //去除最后一个逗号
                can_exe_sql_str = can_exe_sql_str.substring(0, can_exe_sql_str.length() - 1);
                sn_sql.add(can_exe_sql_str);
                //重置需要执行的机具sql
                can_exe_sql_str = "";
                continue;
            }
            //收尾工作
            if(i == length - 1) {
                if(!"".equals(can_exe_sql_str)) {
                    can_exe_sql_str = can_exe_sql_str.substring(0, can_exe_sql_str.length() - 1);
                    sn_sql.add(can_exe_sql_str);
                }
            }
        }

        for (String sql : sn_sql) {
            int num = machineManageService.updateTerToSend(sql, receive_agent_no, receive_agent_node);
            if (num > 0) {
                sql_success_str += sql + ",";
            }
        }

        //流动记录
        if(!"".equals(sql_success_str)){
            //去除sql的引号
            sql_success_str = sql_success_str.replace("'", "");
            int success_num = sql_success_str.split(",").length;
            String agent_name = StringUtils.filterNull(machineManageService.getAgentInfoByAgentNo(receive_agent_no).get("agent_name"));
            //出库
            machineManageService.insTerminalOperate(agent_no, agent_name.equals("")?receive_agent_no : agent_name, success_num, sql_success_str, "1", "2");
            //保存出库时间
            Date date = new Date();
            machineManageService.insAgentTerminalOperate(agent_no,sql_success_str,"1","2",date);
            //入库
            machineManageService.insTerminalOperate(receive_agent_no, current_agent_name.equals("") ? agent_no : current_agent_name, success_num, sql_success_str, "1", "1");
            //保存入库时间
            machineManageService.insAgentTerminalOperate(receive_agent_no,sql_success_str,"1","1",date);

            if(success_num == length){
                return ResponseBean.success(null, "下发成功");
            }
        }
        return ResponseBean.success(null, "下发成功,有部分机具参与活动不支持下发操作");
    }

    /*
   机具回收
   */
    public ResponseBean takeBackTerminal(String agent_no,String agent_node, String current_agent_name, String sn_array_str) {
        String[] sn_temp_array = sn_array_str.split(",");
        JSONArray jsonArray = new JSONArray();
        int fail_count = 0;
        int count = sn_temp_array.length;
        //分片sql的list
        ArrayList<String> sn_sql = new ArrayList<>();
        //分片sql
        String sql_sn_array_str = "";
        //记录原始的sn号对应的agent_no
        Map<String, String> sn_agent_map = new HashMap<>();
        for (int i = 0; i < count; i++) {
            String sn_temp = sn_temp_array[i];
            Map<String, Object> map = machineManageService.getTermInfoBySn(sn_temp);
            String sn_agent_no = StringUtils.filterNull(map.get("agent_no"));
            sn_agent_map.put(sn_temp, sn_agent_no);
            String parent_id = StringUtils.filterNull(machineManageService.getAgentInfoByAgentNo(sn_agent_no).get("parent_id"));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sn", sn_temp);
            //回收机具仅允许回收直属下级代理商且未绑定商户的机具
            if (sn_agent_no.equals(agent_no)) {
                fail_count++;
                jsonObject.put("fail_result", "自己的机具无需回收");
                jsonArray.add(jsonObject);
                continue;
            }

            if (!parent_id.equals(agent_no)) {
                fail_count++;
                jsonObject.put("fail_result", "仅允许回收直属下级机具");
                jsonArray.add(jsonObject);
                continue;
            }

            if (!"".equals(StringUtils.filterNull(machineManageService.getIsSuperActivity(sn_agent_no, sn_temp)))) {
                fail_count++;
                jsonObject.put("fail_result", "机具硬件类型为超级推机具，不允许该操作");
                jsonArray.add(jsonObject);
                continue;
            }

            if ("2".equals(map.get("open_status"))) {
                fail_count++;
                jsonObject.put("fail_result", "已绑定商户机具");
                jsonArray.add(jsonObject);
                continue;
            }

            //拼接需要执行的sn的sql
            sql_sn_array_str += "'"+sn_temp + "',";

            //大于500需要进行分批执行
            if(i != 0 && i % 500 == 0 && !"".equals(sql_sn_array_str)) {
                //去除最后一个逗号
                sql_sn_array_str = sql_sn_array_str.substring(0, sql_sn_array_str.length() - 1);
                sn_sql.add(sql_sn_array_str);
                //重置需要执行的机具sql
                sql_sn_array_str = "";
                continue;
            }
        }

        //收尾工作
        if(!"".equals(sql_sn_array_str)) {
            sql_sn_array_str = sql_sn_array_str.substring(0, sql_sn_array_str.length() - 1);
            sn_sql.add(sql_sn_array_str);
        }

        //执行成功的sql
        String sql_success_str = "";
        for (String sql : sn_sql) {
            //回收操作
            int num = machineManageService.updateTerToBack(sql, agent_no, agent_node);
            if (num <= 0) {
                //只记录回收失败的
                String[] fail_sn_array = sql.split(",");
                for (String sn_temp : fail_sn_array) {
                    JSONObject fail_json = new JSONObject();
                    fail_json.put("sn", sn_temp);
                    fail_json.put("fail_result", "回收失败");
                    jsonArray.add(fail_json);
                }
                fail_count = fail_count + fail_sn_array.length;
            } else {
                sql_success_str += sql + ",";
            }
        }

        //插入流动记录
        if (!"".equals(sql_success_str)) {
            //去除sql的引号
            Map<String, SnReceiveInfo> sn_receive_info_map = groupByAgentNo(sql_success_str.replace("'", ""), sn_agent_map);
            for (SnReceiveInfo snReceiveInfo : sn_receive_info_map.values()) {
                String agent_name = StringUtils.filterNull(machineManageService.getAgentInfoByAgentNo(snReceiveInfo.getAgentNo()).get("agent_name"));
                //入库
                machineManageService.insTerminalOperate(agent_no, agent_name.equals("") ? snReceiveInfo.getAgentNo() : agent_name, snReceiveInfo.getSuccessCount(), snReceiveInfo.getSnStr(), "2", "1");
                //出库
                machineManageService.insTerminalOperate(snReceiveInfo.getAgentNo(), current_agent_name.equals("") ? agent_no : current_agent_name, snReceiveInfo.getSuccessCount(), snReceiveInfo.getSnStr(), "2", "2");
            }
        }


        //回收有失败需要返回失败信息
        if (fail_count > 0) {
            JSONObject data_json = new JSONObject();
            data_json.put("success_count", count - fail_count);
            data_json.put("fail_count", fail_count);
            data_json.put("sn_array", jsonArray);
            return ResponseBean.success(data_json);
        }
        return ResponseBean.success(null, "全部回收成功");
    }

    /**
     * 查询当前代理商的一级代理商勾选的欢乐返子类型
     */
    @SwaggerDeveloped
    @ApiOperation(value = "查询当前代理商的一级代理商勾选的欢乐返子类型", notes = SwaggerNoteLmc.QUERY_ACTIVITY_TYPES)
    @PostMapping("/getActivityTypes")
    public ResponseBean getAgentActivity(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) throws Exception {
        List<Map<String, Object>> list = machineManageService.getActivityTypes(userInfoBean.getOneAgentNo());
        Map<String, Object> map = new HashMap<>();
        map.put("activity_type_no", "");
        map.put("activity_type_name", "全部");
        list.add(0, map);
        return ResponseBean.success(list);
    }


}
