package com.eeepay.frame.utils.swagger;

/**
 * @Title：agentApi2
 * @Description：交易接口相关的SwaggerNote
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
public final class OrderSwaggerNotes {

    public static final String GROUP_SUMMARY_TRANS_ORDER = "交易分组统计\n" +
            "- 请求参数\n" +
            "    - queryScope: 查询范围，ALL：全部交易，OFFICAL：直属交易，CHILDREN：下级交易，非必传，默认按全部交易汇总，位于请求body中\n\n" +
            "- 返回参数\n" +
            "    - productGroup: 按业务产品汇总的结果，agentGroup：按代理商汇总的结果\n" +
            "       - groupList: 按业务产品或代理商汇总的集合列表\n" +
            "          - countOrder: 该分组下的订单数量\n" +
            "          - sumOrderAmount: 该分组下的总交易金额\n" +
            "          - key: 业务产品汇总对应业务产品id，代理商分组对应代理商编号\n" +
            "          - name: 业务产品汇总对应业务产品名称，代理商分组对应代理商名称\n" +
            "          - entryTeam: 子组织Id，点击后将此参数值直接传到/queryTransOrderForPage/{pageNo}/{pageSize}接口\n" +
            "       - totalCountOrder: 按业务产品汇总或代理商汇总对应的总订单数量\n" +
            "       - totalSumOrderAmount: 按业务产品汇总或代理商汇总对应的总订单交易金额\n\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"productGroup\": {\n" +
            "            \"groupList\": [\n" +
            "                {\n" +
            "                    \"countOrder\": 16625,\n" +
            "                    \"name\": \"盛钱包\",\n" +
            "                    \"entryTeam\": \"\",\n" +
            "                    \"sumOrderAmount\": \"2004889330.04\",\n" +
            "                    \"key\": \"200010\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"\",\n" +
            "                    \"entryTeam\": \"\",\n" +
            "                    \"name\": \"其它产品\",\n" +
            "                    \"countOrder\": 325,\n" +
            "                    \"sumOrderAmount\": 263034.68\n" +
            "                },\n" +
            "                {\n" +
            "                    \"entryTeam\": \"100070-001\",\n" +
            "                    \"countOrder\": 623,\n" +
            "                    \"name\": \"盛POS21\",\n" +
            "                    \"sumOrderAmount\": \"183403.87\",\n" +
            "                    \"key\": \"100070\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"entryTeam\": \"100070-002\",\n" +
            "                    \"countOrder\": 191,\n" +
            "                    \"name\": \"超级盛POS\",\n" +
            "                    \"sumOrderAmount\": \"16706.95\",\n" +
            "                    \"key\": \"100070\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"totalCountOrder\": 17765,\n" +
            "            \"totalSumOrderAmount\": 2005352554.53\n" +
            "        },\n" +
            "        \"agentGroup\": {\n" +
            "            \"groupList\": [\n" +
            "                {\n" +
            "                    \"countOrder\": 0,\n" +
            "                    \"name\": \"su二代1\",\n" +
            "                    \"sumOrderAmount\": 0.00,\n" +
            "                    \"key\": \"1561\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"countOrder\": 112,\n" +
            "                    \"name\": \"cy啊抓狂\",\n" +
            "                    \"sumOrderAmount\": 88992.19,\n" +
            "                    \"key\": \"1868\"\n" +
            "                },\n" +
            "            ],\n" +
            "            \"totalCountOrder\": 118,\n" +
            "            \"totalSumOrderAmount\": 93458.19\n" +
            "        }\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String QUERY_TRANS_ORDER_FOR_PAGE = "交易明细\n" +
            "- 请求参数\n" +
            "    - pageNo: 当前页，大于等于0，必传，位于请求接口地址中\n" +
            "    - pageSize: 每页显示条数，大于等于10，必传，位于请求接口地址中\n" +
            "    - queryScope: 查询范围，ALL：全部交易，OFFICAL：直属交易，CHILDREN：下级交易，非必传，默认按全部交易汇总，位于请求body中\n\n" +
            "    - agentNo: 代理商编号，非必传，默认为当前登录代理商，位于请求body中\n" +
            "    - startCreateTime: 开始创建时间，非必传，位于请求body中\n" +
            "    - endCreateTime: 结束创建时间，非必传，位于请求body中\n" +
            "    - minTransAmount: 最小交易金额，非必传，位于请求body中\n" +
            "    - maxTransAmount: 最大交易金额，非必传，位于请求body中\n" +
            "    - teamId: 所属组织，非必传，位于请求body中\n" +
            "    - entryTeam: 所属子组织，非必传，位于请求body中\n" +
            "    - bpId: 业务产品，非必传，位于请求body中\n" +
            "    - cardType: 交易卡片种类，非必传，位于请求body中\n" +
            "    - accountNo: 交易卡号，非必传，位于请求body中\n" +
            "    - transStatus: 交易状态，非必传，位于请求body中\n" +
            "    - mobileNo: 手机号，非必传，位于请求body中\n" +
            "    - merchantKey: 商户名称/编号，非必传，位于请求body中\n" +
            "    - payMethod: 交易方式，非必传，位于请求body中\n" +
            "    - orderNo: 订单号，非必传，位于请求body中\n" +
            "    - settleStatus: 结算状态，非必传，位于请求body中\n" +
            "    - deviceSn: 机具SN号，非必传，位于请求body中\n" +
            "- 返回参数\n" +
            "    - pageNo: 当前页\n" +
            "    - pageSize: 每页显示条数\n" +
            "    - totalCount: 总记录数\n" +
            "    - pageCount: 总页数\n" +
            "    - pageContent: 当前页的数据集合\n\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"pageNo\": 1,\n" +
            "        \"pageSize\": 10,\n" +
            "        \"totalCount\": 122,\n" +
            "        \"pageContent\": [\n" +
            "            {\n" +
            "                \"hp_type_name\": \"盛钱包商户版(非接)\",\n" +
            "                \"service_name\": \"生意宝POS刷卡\",\n" +
            "                \"trans_status_zh\": \"失败\",\n" +
            "                \"merchant_name\": \"CY注册刷卡1\",\n" +
            "                \"trans_time\": \"2017-08-31 16:05:37\",\n" +
            "                \"trans_amount\": 44,\n" +
            "                \"trans_status\": \"FAILED\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"hp_type_name\": \"盛钱包商户版(非接)\",\n" +
            "                \"service_name\": \"生意宝POS刷卡\",\n" +
            "                \"trans_status_zh\": \"成功\",\n" +
            "                \"merchant_name\": \"CY注册刷卡1\",\n" +
            "                \"trans_time\": \"2017-08-31 16:06:03\",\n" +
            "                \"trans_amount\": 45,\n" +
            "                \"trans_status\": \"SUCCESS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"hp_type_name\": \"盛钱包D(不带非接)\",\n" +
            "                \"service_name\": \"超级推POS刷卡测试\",\n" +
            "                \"trans_status_zh\": \"成功\",\n" +
            "                \"merchant_name\": \"二级\",\n" +
            "                \"trans_time\": \"2017-06-28 15:13:24\",\n" +
            "                \"trans_amount\": 44,\n" +
            "                \"trans_status\": \"SUCCESS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"hp_type_name\": \"盛钱包D(不带非接)\",\n" +
            "                \"service_name\": \"超级推POS刷卡测试\",\n" +
            "                \"trans_status_zh\": \"成功\",\n" +
            "                \"merchant_name\": \"二级\",\n" +
            "                \"trans_time\": \"2017-06-28 15:22:23\",\n" +
            "                \"trans_amount\": 13,\n" +
            "                \"trans_status\": \"SUCCESS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"hp_type_name\": \"盛钱包D(不带非接)\",\n" +
            "                \"service_name\": \"超级推POS刷卡测试\",\n" +
            "                \"trans_status_zh\": \"成功\",\n" +
            "                \"merchant_name\": \"二级\",\n" +
            "                \"trans_time\": \"2017-06-28 15:48:31\",\n" +
            "                \"trans_amount\": 16,\n" +
            "                \"trans_status\": \"SUCCESS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"hp_type_name\": \"盛钱包D(不带非接)\",\n" +
            "                \"service_name\": \"超级推POS刷卡测试\",\n" +
            "                \"trans_status_zh\": \"成功\",\n" +
            "                \"merchant_name\": \"二级\",\n" +
            "                \"trans_time\": \"2017-06-28 16:46:19\",\n" +
            "                \"trans_amount\": 12,\n" +
            "                \"trans_status\": \"SUCCESS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"hp_type_name\": \"盛钱包D(不带非接)\",\n" +
            "                \"service_name\": \"超级推POS刷卡测试\",\n" +
            "                \"trans_status_zh\": \"成功\",\n" +
            "                \"merchant_name\": \"二级\",\n" +
            "                \"trans_time\": \"2017-06-28 16:50:30\",\n" +
            "                \"trans_amount\": 15,\n" +
            "                \"trans_status\": \"SUCCESS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"hp_type_name\": \"盛钱包D(不带非接)\",\n" +
            "                \"service_name\": \"超级推POS刷卡测试\",\n" +
            "                \"trans_status_zh\": \"成功\",\n" +
            "                \"merchant_name\": \"二级\",\n" +
            "                \"trans_time\": \"2017-06-28 17:20:21\",\n" +
            "                \"trans_amount\": 12,\n" +
            "                \"trans_status\": \"SUCCESS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"hp_type_name\": \"盛钱包D(不带非接)\",\n" +
            "                \"service_name\": \"超级快POS刷卡\",\n" +
            "                \"trans_status_zh\": \"成功\",\n" +
            "                \"merchant_name\": \"皮春平的小店\",\n" +
            "                \"trans_time\": \"2017-07-10 11:01:08\",\n" +
            "                \"trans_amount\": 301,\n" +
            "                \"trans_status\": \"SUCCESS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"hp_type_name\": \"盛钱包商户版(非接)\",\n" +
            "                \"service_name\": \"超级快POS刷卡\",\n" +
            "                \"trans_status_zh\": \"失败\",\n" +
            "                \"merchant_name\": \"cy超级快设备进件1\",\n" +
            "                \"trans_time\": \"2017-08-31 09:37:39\",\n" +
            "                \"trans_amount\": 11,\n" +
            "                \"trans_status\": \"FAILED\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"pageCount\": 13\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String TRANS_ORDER_DATA_DETAIL = "数据-明细(按月/按日统计下发数据)\n" +
            "- 请求参数\n" +
            "    - agentNo: 按代理商查询，非必传，默认当前登录代理商，位于请求body中\n" +
            "    - queryDate: 查询月份，yyyy-MM，非必传，默认上个月，位于请求body中\n" +
            "    - queryScope: 查询范围，ALL：全部交易，OFFICAL：直属交易，CHILDREN：下级交易，非必传，默认按全部交易汇总，位于请求body中\n\n" +
            "- 返回参数\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"dailyData\": [\n" +
            "            {\n" +
            "                \"key\": \"05-01\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-02\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-03\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-04\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-05\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-06\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-07\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-08\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-09\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-10\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-11\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-12\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-13\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-14\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-15\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-16\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-17\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-18\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-19\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-20\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-21\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-22\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-23\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-24\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-25\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-26\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-27\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-28\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-29\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-30\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\": \"05-31\",\n" +
            "                \"value\": {\n" +
            "                    \"teamGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalCountOrder\": 0,\n" +
            "                        \"totalSumOrderAmount\": 0\n" +
            "                    },\n" +
            "                    \"agentAddCount\": 0,\n" +
            "                    \"merGroup\": {\n" +
            "                        \"groupList\": [],\n" +
            "                        \"totalActiveMerCount\": 0,\n" +
            "                        \"totalMerCount\": 0\n" +
            "                    }\n" +
            "                },\n" +
            "                \"description\": null\n" +
            "            }\n" +
            "        ],\n" +
            "        \"monthData\": {\n" +
            "            \"teamGroup\": {\n" +
            "                \"groupList\": [],\n" +
            "                \"totalCountOrder\": 0,\n" +
            "                \"totalSumOrderAmount\": 0\n" +
            "            },\n" +
            "            \"agentAddCount\": 0,\n" +
            "            \"merGroup\": {\n" +
            "                \"groupList\": [],\n" +
            "                \"totalActiveMerCount\": 0,\n" +
            "                \"totalMerCount\": 0\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String VIEW_DATA_TREND = "数据-新增商户趋势、交易量趋势、新增代理商趋势\n" +
            "- 请求参数\n" +
            "    - agentNo: 按代理商查询，非必传，默认当前登录代理商，位于请求body中\n" +
            "    - queryScope: 查询范围，ALL：全部，OFFICAL：直属，CHILDREN：下级，非必传，默认按全部汇总，位于请求body中\n\n" +
            "- 返回参数\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"transOrderTrend\": {\n" +
            "            \"sevenDayTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"05-22\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-23\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-24\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-25\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-26\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-27\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-28\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"halfYearTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"2018-12\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-01\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-02\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-03\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-04\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-05\",\n" +
            "                    \"value\": \"3816.70\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"newlyMerTrend\": {\n" +
            "            \"sevenDayTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"05-22\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-23\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-24\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-25\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-26\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-27\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-28\",\n" +
            "                    \"value\": \"6\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"halfYearTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"2018-12\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-01\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-02\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-03\",\n" +
            "                    \"value\": \"1\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-04\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-05\",\n" +
            "                    \"value\": \"6\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"newlyAgentTrend\": {\n" +
            "            \"sevenDayTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"05-22\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-23\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-24\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-25\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-26\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-27\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-28\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"halfYearTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"2018-12\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-01\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-02\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-03\",\n" +
            "                    \"value\": \"1\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-04\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-05\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

}
