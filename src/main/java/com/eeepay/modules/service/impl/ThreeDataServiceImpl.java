package com.eeepay.modules.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eeepay.modules.bean.AgentSelectVo;
import com.eeepay.modules.bean.TeamSelect;
import com.eeepay.modules.bean.ThreeDataCollect;
import com.eeepay.modules.bean.ThreeDataTetailQo;
import com.eeepay.modules.dao.ThreeDataDao;
import com.eeepay.modules.service.SysDictService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;

import cn.hutool.core.date.DateUtil;

/**
 * 三方数据服务
 *
 * @author Qiu Jian
 *
 */
@Service
public class ThreeDataServiceImpl {

	public static final String THREE_INCOME_CALC_OEM = "THREE_INCOME_CALC_OEM";

	@Autowired
	private ThreeDataDao threeDataDao;
	
	@Autowired
	private SysDictService sysDictService;

	public Page<AgentSelectVo> getChildrenAgentByAgentNoAndKeyword(String agentNo, String keyword, String pageNo,
			String pageSize) {

		Page<AgentSelectVo> page = PageHelper.startPage(StringUtil.isEmpty(pageNo) ? 1 : Integer.valueOf(pageNo),
				StringUtil.isEmpty(pageSize) ? 10 : Integer.valueOf(pageSize), true);
		threeDataDao.selectChildrenAgentByAgentNoAndKeyword(agentNo, keyword);

		return page;
	}

	public Integer entrySwitch(String agentNo) {
		// 入口默认只有在三方关系链中存在且是非末级的代理商才显示
		int countAgentLinkByAgentNo = threeDataDao.countAgentLinkByAgentNo(agentNo);
		return countAgentLinkByAgentNo > 0 ? 1 : 0;
	}

	public ThreeDataCollect collectQuery(String currentAgentNo, String agentNo, String teamId) {
		List<String> agentNoList = new ArrayList<>();
		if (StringUtil.isEmpty(agentNo)) {// 查全部
			agentNoList.add(currentAgentNo);
			getAllLookAgentNo(agentNoList, currentAgentNo);
		} else {
			String selectAgentNo = threeDataDao.selectAgentLinkByCurrentAgentNoAndAgentNo(currentAgentNo, agentNo);
			agentNoList.add(selectAgentNo);
			getAllLookAgentNo(agentNoList, selectAgentNo);
		}

		ThreeDataCollect countThreeDataCollect = threeDataDao.countThreeDataCollect(agentNoList, teamId);
		if (countThreeDataCollect == null) {
			countThreeDataCollect = new ThreeDataCollect();
			countThreeDataCollect.setMerchantSum("0");
			countThreeDataCollect.setTransSum("0.00");
			countThreeDataCollect.setActivatedMerchantSum("0");
		}
		// 获取最新更新时间
		Date date = threeDataDao.selectLastUpdateTime();
		countThreeDataCollect.setLastUpdateTime(DateUtil.formatDateTime(date));

		// 获取最新库存
		String terminalSum = threeDataDao.countTerminalSumByAgentNoListAndTeamIdAndCreateTime(agentNoList, teamId,
				date);
		countThreeDataCollect.setTerminalSum(terminalSum == null ? "0" : terminalSum);
		return countThreeDataCollect;
	}

	public List<TeamSelect> getTeamSelectList() {

		String teamConfigStr = sysDictService.getSysDictValueByKey(THREE_INCOME_CALC_OEM);

		List<TeamSelect> teamSelectList = new ArrayList<>();

		String[] teamArray = teamConfigStr.split("-");
		for (int i = 0; i < teamArray.length; i++) {
			String teamId = teamArray[i];
			// 查询teamId下的子组织
			List<TeamSelect> teamInfoEntryList = threeDataDao.selectTeamInfoEntryByTeamId(teamId);
			if (teamInfoEntryList.size() > 0) {
				teamSelectList.addAll(teamInfoEntryList);
			} else {
				TeamSelect teamSelect = threeDataDao.selectTeamSelectByTeamId(teamId);
				teamSelectList.add(teamSelect);
			}
		}

		return teamSelectList;
	}

	private void getAllLookAgentNo(List<String> agentNoList, String agentNo) {
		List<String> selectLookAgentNo = threeDataDao.selectLookAgentNo(agentNo);
		for (String str : selectLookAgentNo) {
			if (agentNoList.contains(str)) {
				continue;
			}
			agentNoList.add(str);
			getAllLookAgentNo(agentNoList, str);
		}
	}

	public ThreeDataCollect getDetailByQo(ThreeDataTetailQo threeDataTetailQo) {
		String agentNo = threeDataTetailQo.getAgentNo();
		String currentAgentNo = threeDataTetailQo.getCurrentAgentNo();

		List<String> agentNoList = new ArrayList<>();
		if (StringUtil.isEmpty(agentNo)) {// 查全部
			agentNoList.add(currentAgentNo);
			getAllLookAgentNo(agentNoList, currentAgentNo);
		} else {
			String selectAgentNo = threeDataDao.selectAgentLinkByCurrentAgentNoAndAgentNo(currentAgentNo, agentNo);
			agentNoList.add(selectAgentNo);
			getAllLookAgentNo(agentNoList, selectAgentNo);
		}
		threeDataTetailQo.setAgentNoList(agentNoList);
		ThreeDataCollect threeDataCollect = threeDataDao.selectThreeDataCollectByQo(threeDataTetailQo);

		if (threeDataCollect == null) {
			threeDataCollect = new ThreeDataCollect();
			threeDataCollect.setMerchantSum("0");
			threeDataCollect.setTransSum("0.00");
			threeDataCollect.setActivatedMerchantSum("0");
		}

		List<ThreeDataCollect> details = threeDataDao.selectDetailByQo(threeDataTetailQo);
		threeDataCollect.setDetails(details);

		// 获取最新更新时间 最新库存
		ThreeDataCollect threeDataCollectSection = threeDataDao.selectThreeDataCollectSectionByQo(threeDataTetailQo);
		if (threeDataCollectSection == null) {
			threeDataCollect.setTerminalSum("0");
			String yearMonth = threeDataTetailQo.getYearMonth();
			yearMonth = yearMonth + "日";
			String lastUpdateTime = yearMonth.substring(0, 4).concat("年")
					.concat(yearMonth.substring(4, yearMonth.length()));
			threeDataCollect.setLastUpdateTime(lastUpdateTime);
		} else {
			threeDataCollect.setLastUpdateTime(threeDataCollectSection.getLastUpdateTime());
			threeDataCollect.setTerminalSum(threeDataCollectSection.getTerminalSum());
		}
		return threeDataCollect;
	}

}
