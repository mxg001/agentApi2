package com.eeepay.modules.controller;

import com.eeepay.frame.annotation.LoginValid;
import com.eeepay.frame.annotation.SwaggerDeveloped;
import com.eeepay.frame.bean.ResponseBean;
import com.eeepay.modules.service.EsDataMigrateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-08-05 09:21
 */
//@SignValidate(needSign = false)
@LoginValid(needLogin = false)
@Slf4j
@RequestMapping("/esDataMigrate")
@Api(description = "es数据迁移合并")
@RestController
public class EsDataMigrateController {

    @Resource
    private EsDataMigrateService esDataMigrateService;

    @ApiOperation(value = "代理商迁移后需要修改es数据")
    @GetMapping("/agentMigrate/{migrateAgentNode}/{newParentId}")
    @SwaggerDeveloped
    public ResponseBean agentMigrate(@PathVariable String migrateAgentNode,
                                     @PathVariable String newParentId) {
        esDataMigrateService.agentMigrate(migrateAgentNode, newParentId);
        return ResponseBean.success();
    }

    @ApiOperation(value = "商户迁移后需要修改es数据,boss系统调用")
    @GetMapping("/merchantMigrate/{merchantNo}/{oldAgentNo}")
    @SwaggerDeveloped
    public ResponseBean merchantMigrate(@PathVariable String merchantNo,
                                        @PathVariable String oldAgentNo) {
        esDataMigrateService.merchantMigrate(merchantNo, oldAgentNo);
        return ResponseBean.success();
    }

    @ApiOperation(value = "变更业务产品,盛钱包系统调用")
    @GetMapping("/changeMerchantProducts/{merchantNo}")
    @SwaggerDeveloped
    public ResponseBean changeMerchantProducts(@PathVariable String merchantNo) {
        esDataMigrateService.changeMerchantProducts(merchantNo);
        return ResponseBean.success();
    }

    @ApiOperation(value = "变更商户子组织，同步更新ES进件子组织")
    @GetMapping("/changeMerchantEntryTeamId/{merchantNo}/{newEntryTeamId}")
    @SwaggerDeveloped
    public ResponseBean changeMerchantEntryTeamId(@PathVariable String merchantNo, @PathVariable String newEntryTeamId) {
        esDataMigrateService.updateMbpAndOrderEntryTeamByMer(merchantNo, newEntryTeamId);
        return ResponseBean.success();
    }

    @ApiOperation(value = "商户欢乐返激活时，商户进件同步激活")
    @GetMapping("/activeMerchantBusinessProduct/{merchantNo}")
    @SwaggerDeveloped
    public ResponseBean activeMerchantBusinessProduct(@PathVariable String merchantNo) {
        esDataMigrateService.activeMerchantBusinessProduct(merchantNo);
        return ResponseBean.success();
    }

    @ApiOperation(value = "普通商户与特约商户的绑定，同步更新ES商户数据")
    @PostMapping("/changeAcqMerchantNoToEs")
    @SwaggerDeveloped
    public ResponseBean bindAcqMerchantNoToEs(@RequestBody(required = false) Map<String, String> bodyParams) {
        String merchantNo = bodyParams.get("merchantNo");
        String acqMerchantNo = bodyParams.get("acqMerchantNo");
        esDataMigrateService.bindAcqMerchantNoToEs(merchantNo, acqMerchantNo);
        return ResponseBean.success();
    }
}
