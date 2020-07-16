package com.eeepay.modules.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import com.eeepay.modules.bean.AgentSelectVo;
import com.eeepay.modules.bean.TeamSelect;
import com.eeepay.modules.bean.ThreeDataCollect;
import com.eeepay.modules.bean.ThreeDataTetailQo;

/**
 * 三方数据数据操作
 *
 * @author Qiu Jian
 *
 */
@Mapper
public interface ThreeDataDao {

	@Select({ "SELECT ", //
			"	agent_i_0.agent_no AS agent_no,  ", //
			"	agent_i_0.agent_name AS agent_name  ", //
			"FROM ", //
			"	agent_info agent_i_0 ", //
			"	JOIN agent_authorized_link agent_a_l_0 ON agent_a_l_0.agent_link = agent_i_0.agent_no  ", //
			"WHERE ", //
			"	agent_i_0.agent_level = 1  ", //
			"	AND agent_a_l_0.agent_authorized = #{agentNo,jdbcType=VARCHAR}  ", //
			"	AND ( agent_i_0.agent_name LIKE CONCAT( '%', #{keyword,jdbcType=VARCHAR}, '%' ) OR agent_i_0.agent_no = #{keyword,jdbcType=VARCHAR} OR agent_i_0.mobilephone = #{keyword,jdbcType=VARCHAR} )",//
			"	AND agent_a_l_0.record_status = 1  ", //
			"	AND agent_a_l_0.record_check = 1  ", //
			"	AND agent_a_l_0.is_look = 1  ", //
			"	AND agent_a_l_0.link_level <= 5"
	})
	@Results(value = { @Result(column = "agent_no", property = "agentNo", jdbcType = JdbcType.VARCHAR),
			@Result(column = "agent_name", property = "agentName", jdbcType = JdbcType.VARCHAR) })
	List<AgentSelectVo> selectChildrenAgentByAgentNoAndKeyword(@Param("agentNo") String agentNo,
			@Param("keyword") String keyword);

	@Select({ "SELECT ", //
			"	count( * )  ", //
			"FROM ", //
			"	agent_authorized_link  ", //
			"WHERE ", //
			"	agent_authorized =  #{agentNo,jdbcType=VARCHAR}",//
			"	AND record_status = 1  ", //
			"	AND record_check = 1  " //
		})
	int countAgentLinkByAgentNo(String agentNo);

	@Select({ "SELECT ", //
			"	team_entry_id AS team_id,  ", //
			"	team_entry_name AS team_name  ", //
			"FROM ", //
			"	team_info_entry  ", //
			"WHERE ", //
			"	team_id = #{teamId}" })
	@Results(value = { @Result(column = "team_id", property = "teamId", jdbcType = JdbcType.VARCHAR),
			@Result(column = "team_name", property = "teamName", jdbcType = JdbcType.VARCHAR) }, id = "teamSelectMap")
	List<TeamSelect> selectTeamInfoEntryByTeamId(String teamId);

	@Select({ "SELECT distinct ", //
			"	team_id,  ", //
			"	team_name  ", //
			"FROM ", //
			"	team_info WHERE team_id=#{teamId,jdbcType=VARCHAR}" })
	@ResultMap("teamSelectMap")
	TeamSelect selectTeamSelectByTeamId(String teamId);

	@Select({ "SELECT ", //
			"	agent_link  ", //
			"FROM ", //
			"	agent_authorized_link  ", //
			"WHERE ", //
			"	agent_authorized = #{agentNo,jdbcType=VARCHAR}  ", //
			"	AND record_status = 1  ", //
			"	AND record_check = 1  ", //
			"	AND is_look = 1  ", //
			"	AND link_level <= 5" })
	List<String> selectLookAgentNo(String agentNo);

	@Select({ "SELECT ", //
			"	agent_link  ", //
			"FROM ", //
			"	agent_authorized_link  ", //
			"WHERE ", //
			"	agent_authorized = #{currentAgentNo,jdbcType=VARCHAR}  ", //
			"	AND agent_link =  #{agentNo,jdbcType=VARCHAR}" })
	String selectAgentLinkByCurrentAgentNoAndAgentNo(@Param("currentAgentNo") String currentAgentNo,
			@Param("agentNo") String agentNo);

	ThreeDataCollect countThreeDataCollect(@Param("agentNoList") List<String> agentNoList,
			@Param("teamId") String teamId);

	@Select({ "SELECT ", //
			"	create_time  ", //
			"FROM ", //
			"	trade_sum_info  ", //
			"GROUP BY ", //
			"	create_time  ", //
			"ORDER BY ", //
			"	create_time DESC  ", //
			"	LIMIT 1" })
	Date selectLastUpdateTime();

	List<ThreeDataCollect> selectDetailByQo(ThreeDataTetailQo threeDataTetailQo);

	String countTerminalSumByAgentNoListAndTeamIdAndCreateTime(@Param("agentNoList") List<String> agentNoList,
			@Param("teamId") String teamId, @Param("date") Date date);

	ThreeDataCollect selectThreeDataCollectByQo(ThreeDataTetailQo threeDataTetailQo);

	ThreeDataCollect selectThreeDataCollectSectionByQo(ThreeDataTetailQo threeDataTetailQo);

}
