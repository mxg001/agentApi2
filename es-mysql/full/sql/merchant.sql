SELECT
    mi.id auto_id,
	mi.merchant_no,
	mi.merchant_name,
	mi.mobilephone,
	mi.agent_no,
	mi.IFNULL(team_id, '') team_id,
	mi.IFNULL(team_entry_id, '') team_entry_id,
	mi.IFNULL(province,'') province,
	mi.IFNULL(city, '') city,
	mi.IFNULL(district, '') district,
	mi.parent_node agent_node,
	mi.hlf_active,
	mi.DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S') create_time,
	mi.last_update_time,
	mi.IFNULL(address, '') address,
	mi.ifnull(business_type, '') business_type,
	mi.ifnull(industry_type, '') industry_type,
    mi.status,
    mi.register_source,
    mi.recommended_source,
	mi.risk_status,
	mi.risk_settle,
	am.acq_merchant_no
FROM merchant_info mi
LEFT JOIN acq_merchant am ON am.merchant_no = mi.merchant_no
WHERE mi.id >= :sql_last_value
order by mi.id asc
limit 50000