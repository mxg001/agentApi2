package com.eeepay.modules.controller;

import com.eeepay.frame.annotation.CurrentUser;
import com.eeepay.frame.annotation.LoginValid;
import com.eeepay.frame.annotation.SignValidate;
import com.eeepay.frame.annotation.SwaggerDeveloped;
import com.eeepay.frame.bean.ResponseBean;
import com.eeepay.frame.utils.ALiYunOssUtil;
import com.eeepay.frame.utils.StringUtils;
import com.eeepay.frame.utils.WebUtils;
import com.eeepay.frame.utils.swagger.SwaggerNoteLmc;
import com.eeepay.modules.bean.UserInfoBean;
import com.eeepay.modules.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/29 11:20
 */
@Api(description = "banner查询")
@RestController
@RequestMapping("/banner")
public class BannerController {
    @Resource
    private BannerService bannerService;

    @SwaggerDeveloped
    @ApiOperation(value = "banner查询", notes = SwaggerNoteLmc.FIND_BANNER)
    @PostMapping("/findBanner")
    public ResponseBean getHomeMsg(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, HttpServletRequest req) {
        List<Map<String, Object>> list = bannerService.findBanner(WebUtils.getAppDeviceInfo(req).getAppNo());
        for(Map<String,Object> map : list){
            String banner_attachment = StringUtils.filterNull(map.get("banner_attachment"));
            banner_attachment = ALiYunOssUtil.genUrl("agent-attch", banner_attachment, new Date(new Date().getTime()+100000));
            map.put("banner_attachment", banner_attachment);
        }
        return ResponseBean.success(list);
    }

}
