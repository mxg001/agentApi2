package com.eeepay.modules.controller;

import com.eeepay.frame.annotation.LoginValid;
import com.eeepay.frame.annotation.SignValidate;
import com.eeepay.frame.bean.ResponseBean;
import com.eeepay.frame.utils.WebUtils;
import com.eeepay.modules.bean.MerchantSumBean;
import com.eeepay.modules.bean.UserInfoBean;
import com.eeepay.modules.service.MerchantEsService;
import com.eeepay.modules.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-08 15:08
 */
@RestController
@Api(description = "测试模块")
@RequestMapping("/test")
public class TestController {

    @Resource
    private TestService testService;
    @Resource
    private MerchantEsService merchantEsService;

    @ApiOperation(value = "从写库查询代理商信息")
    @GetMapping("/getAgentByWrite/{agentNo}")
    public ResponseBean getAgentByWrite(@PathVariable String agentNo) {
        return ResponseBean.success(testService.getAgentByWrite(agentNo));
    }

    @ApiOperation(value = "从读库查询代理商信息")
    @GetMapping("/getAgentByRead/{agentNo}")
    public ResponseBean getAgentByRead(@PathVariable String agentNo) {
        return ResponseBean.success(testService.getAgentByRead(agentNo));
    }

    @ApiOperation(value = "从读和写库查询代理商信息")
    @GetMapping("/getAgent/{agentNo}")
    public ResponseBean getAgent(@PathVariable String agentNo) {
        return ResponseBean.success(Arrays.asList(
                testService.getAgentByRead(agentNo),
                testService.getAgentByWrite(agentNo)
        ));
    }

    @ApiOperation(value = "从读和写库查询代理商信息")
    @GetMapping("/getAgent2/{agentNo}")
    public ResponseBean getAgent2(@PathVariable String agentNo) {
        return ResponseBean.success(testService.getAgent(agentNo));
    }

    @RequestMapping("/getReqBodyInfo")
    @LoginValid(needLogin = false)
    @SignValidate
    public ResponseBean getReqBodyInfo(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        String sign = (String) params.get("sign");
        return ResponseBean.success(WebUtils.getReqBodyInfo(request));
    }

    @GetMapping("/getReqBodyInfo2")
    @LoginValid(needLogin = false)
    @SignValidate
    public ResponseBean getReqBodyInfo2(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        String sign = (String) params.get("sign");
        return ResponseBean.success(WebUtils.getReqBodyInfo(request));
    }


    @GetMapping("/getMerchantCount")
    public ResponseBean getMerchantCount(HttpServletRequest request) {
        UserInfoBean loginUser = WebUtils.getLoginUserInfoFromRedis(request);
        long all = merchantEsService.countMerchant(loginUser.getAgentNode(), false);
        long today = merchantEsService.countMerchant(loginUser.getAgentNode(), true);
        Map<String, Long> result = new HashMap<>();
        result.put("all", all);
        result.put("today", today);
        return ResponseBean.success(result);
    }


//    @LoginValid(needLogin = false)
//    @RequestMapping("/getMerchantCount/{teamId}")
//    public ResponseBean getMerchantCount(@PathVariable String teamId, HttpServletRequest request) {
//        UserInfoBean loginUser = WebUtils.getLoginUserInfoFromRedis(request);
//        MerchantSumBean merchantSumBean = merchantEsService.statisMerchantByTeamId(loginUser.getAgentNode(), "200010", false);
//        return ResponseBean.success(merchantEsService.statisMerchantByTeamId(loginUser.getAgentNode(), teamId, false));
//    }
}
