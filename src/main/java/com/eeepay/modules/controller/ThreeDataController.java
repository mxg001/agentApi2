package com.eeepay.modules.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eeepay.frame.annotation.CurrentUser;
import com.eeepay.frame.annotation.SwaggerDeveloped;
import com.eeepay.frame.bean.ResponseBean;
import com.eeepay.frame.utils.WebUtils;
import com.eeepay.frame.utils.swagger.SwaggerNotes;
import com.eeepay.modules.bean.AgentSelectVo;
import com.eeepay.modules.bean.TeamSelect;
import com.eeepay.modules.bean.ThreeDataCollect;
import com.eeepay.modules.bean.ThreeDataTetailQo;
import com.eeepay.modules.bean.UserInfoBean;
import com.eeepay.modules.service.impl.ThreeDataServiceImpl;
import com.github.pagehelper.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 三方数据控制器
 *
 * @author Qiu Jian
 *
 */
@Api(description = "三方数据模块")
@RestController
@Slf4j
public class ThreeDataController {

	@Autowired
	private ThreeDataServiceImpl threeDataService;

	@GetMapping("/threeData/getChildrenAgent")
	@SwaggerDeveloped
	@ApiOperation(value = "获取直属下级代理商名称", notes = SwaggerNotes.THREE_DATA_GET_CHILDREN_AGENT)
	public ResponseBean<List<AgentSelectVo>> getChildrenAgent(String keyword, String pageNo, String pageSize,
			@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {

		if (keyword == null) {
			keyword = "";
		}
		try {
			Page<AgentSelectVo> page = threeDataService.getChildrenAgentByAgentNoAndKeyword(userInfoBean.getAgentNo(),
					keyword, pageNo, pageSize);
			return new ResponseBean<>(200, "获取成功", page.getResult(), page.getTotal(), true);
		} catch (Exception e) {
			log.error("系统异常", e);
			return new ResponseBean<>(400, "获取失败", null, 0, false);
		}
	}

	@GetMapping("/threeData/teamSelectList")
	@SwaggerDeveloped
	@ApiOperation(value = "获取组织下拉列表", notes = SwaggerNotes.THREE_DATA_TEAM_SELECT_LIST)
	public ResponseBean<List<TeamSelect>> teamSelectList() {
		try {
			List<TeamSelect> list = threeDataService.getTeamSelectList();
			return new ResponseBean<>(200, "获取成功", list, 0, true);
		} catch (Exception e) {
			log.error("系统异常", e);
			return new ResponseBean<>(400, "获取失败", null, 0, false);
		}
	}

	@GetMapping("/threeData/collectQuery")
	@SwaggerDeveloped
	@ApiOperation(value = "获取三方数据汇总数据", notes = SwaggerNotes.THREE_DATA_COLLECT_QUERY)
	public ResponseBean<ThreeDataCollect> collectQuery(String agentNo, String teamId,
			@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
		try {
			ThreeDataCollect threeDataCollect = threeDataService.collectQuery(userInfoBean.getAgentNo(), agentNo,
					teamId);
			return new ResponseBean<>(200, "获取成功", threeDataCollect, 0, true);
		} catch (Exception e) {
			log.error("系统异常", e);
			return new ResponseBean<>(400, "获取失败", null, 0, false);
		}
	}

	@GetMapping("/threeData/detail")
	@SwaggerDeveloped
	@ApiOperation(value = "获取三方数据汇总数据明细", notes = SwaggerNotes.THREE_DATA_DETAIL)
	public ResponseBean<ThreeDataCollect> detail(ThreeDataTetailQo threeDataTetailQo,
			HttpServletRequest reuqest) {
		String loginAgentNo = WebUtils.getLoginAgentNo(reuqest);
		threeDataTetailQo.setCurrentAgentNo(loginAgentNo);
		try {
			ThreeDataCollect threeDataCollect =  threeDataService.getDetailByQo(threeDataTetailQo);
			return new ResponseBean<>(200, "获取成功", threeDataCollect, 0, true);
		} catch (Exception e) {
			log.error("系统异常", e);
			return new ResponseBean<>(400, "获取失败", null, 0, false);
		}
	}

}
