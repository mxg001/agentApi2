SELECT
    mi.id auto_id,
	mi.merchant_no,
	mi.merchant_name,
	mi.mobilephone,
	mi.agent_no,
	IFNULL(mi.team_id, '') team_id,
	IFNULL(mi.team_entry_id, '') team_entry_id,
	IFNULL(mi.province,'') province,
	IFNULL(mi.city, '') city,
	IFNULL(mi.district, '') district,
	mi.parent_node agent_node,
	mi.hlf_active,
	DATE_FORMAT(mi.create_time,'%Y-%m-%d %H:%i:%S') create_time,
	mi.last_update_time,
	IFNULL(mi.address, '') address,
	ifnull(mi.business_type, '') business_type,
	ifnull(mi.industry_type, '') industry_type,
    mi.status,
    mi.register_source,
    mi.recommended_source,
	mi.risk_status,
	mi.risk_settle,
	am.acq_merchant_no
FROM merchant_info mi
LEFT JOIN acq_merchant am ON am.merchant_no = mi.merchant_no
WHERE mi.last_update_time >= :sql_last_value
order by mi.last_update_time