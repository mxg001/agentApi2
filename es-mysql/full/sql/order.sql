SELECT
  cto.id auto_id,
	cto.order_no,
	cto.merchant_no,
	cto.trans_amount,
	cto.pay_method,
	cto.trans_status,
	cto.trans_type,
	cto.mobile_no mobilephone,
	cto.card_type,
	cto.account_no,
	DATE_FORMAT(cto.create_time, '%Y-%m-%d %H:%i:%S') create_time,
	DATE_FORMAT(cto.trans_time, '%Y-%m-%d %H:%i:%S') trans_time,
	cto.last_update_time,
  cto.business_product_id bp_id,
	IFNULL(mi.team_id, '') team_id,
	IFNULL(mi.team_entry_id, '') team_entry_id,
	mi.agent_no,
	mi.parent_node agent_node,
	IFNULL(cto.service_id, '') service_id,
	cto.settlement_method,
	cto.settle_type,
	IFNULL(cto.settle_status, '0') settle_status,
	cto.device_sn
FROM
	collective_trans_order cto
JOIN merchant_info mi ON mi.merchant_no = cto.merchant_no
LEFT JOIN business_product_define bpd ON bpd.bp_id = cto.business_product_id
WHERE cto.id >= :sql_last_value
ORDER BY cto.id ASC
limit 50000