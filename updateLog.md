- 盛pos和超级盛pos区分
    - /merchantInfo/getAgentTeams/{agent_no} 组织增加返回子组织
        多返回了子组织 team_entry 
    - /merchantInfo/getMerchantInfoList      进件查询
        请求查询参数增加 team_entry_id
    - /merchant/merchantSummary          商户按组织汇总
        相应参数多返回字段teamEntry: 是否为子组织,true为子组织过滤查询传 teamEntryId, 否则传 teamId
        作用是在列表点击跳转查询商户接口时,需要根据teamEntry传对应的key
    - /merchant/merchantSummaryToday     今日新增商户商户按组织汇总
        同上
    - /merchant/listMerchantInfo/{pageNo}/{pageSize}
        请求查询参数增加 teamEntryId