package com.eeepay.frame.utils;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-13 09:44
 */
public class Constants {
    /**
     * 登陆密码解密时需要的密钥
     */
    public static final String LOGIN_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIn2zWqU7K/2qm5pOpq5bp9R+3MTnStWTfJU9nC/Vo7UKH9dITPvrELCTK+qlqpx5Fes+l0GY7n6u4n4jyiw4ejsvkZYQ5ww477yLOn2FcoEGuZEwPgSCmfTST0OFUgQqn+/J11k9L92jEHyieE3qmhMkMt0UsVUSJwx/nZxo30ZAgMBAAECgYBD3YHigeuEC4R+14iaf8jo2j0kuGtB3Cxvnlez0otTqw1YyYkBsU49cLKkXvfKVEgM0Ow/QltgKvSBxCE31PrrDka5TygVMqqA/IM7NrDvjUcGLjyoeNmLA8660fWcDxUTlAGN5kxIvUATayVwKVflpWPWu0FPKsWrZustnEo+4QJBAMCmYsWqAKWYMVRXFP3/XGRfio8DV793TOckyBSN9eh8UhgoZyT3u7oeHmDJEwm4aNMHlg1Pcdc6tNsvi1FRCiUCQQC3VNzfF4xOtUgX7vWPL8YVljLuXmy12iVYmg6ofu9l31nwM9FLQ1TRFglvF5LWrIXTQb07PgGd5DJMAQWGsqLlAkAPE7Z9M73TN+L8b8hDzJ1leZi1cpSGdoa9PEKwYR/SrxAZtefEm+LEQSEtf+8OfrEtetWCeyo0pvKKiOEFXytFAkEAgynL/DC0yXsZYUYtmYvshHU5ayFTVagFICbYZeSrEo+BoUDxdI9vl0fU6A5NmBlGhaZ65G+waG5jLc1tTrlvoQJAXBEoPcBNAosiZHQfYBwHqU6mJ9/ZacJh3MtJzGGebfEwJgtln5b154iANqNWXpySBLvkK+Boq7FYRiD83pqmUg==";
    /**
     * 用于存放loginToken的参数名
     */
    public static final String LOGIN_TOKEN = "LOGIN_TOKEN";
    /**
     * redis key 的前缀
     */
    public static final String REDIS_KEY_PREFIX = "agentApi2";

    public static final String CAPTCHA_REDIS_KEY_PREFIX = "CAPTCHA_REDIS_KEY_";

    public static final String TEAM_ID_999 = "999";

    /*阿里云存储boss附件bucket*/
    public static final String ALIYUN_OSS_ATTCH_TUCKET = "agent-attch";

    /*阿里云存储boss附件临时bucket*/
    public static final String ALIYUN_OSS_TEMP_TUCKET = "boss-temp";

    public static final String ACCOUNT_API_SECURITY = "zouruijin";

    public static final String USER_NO_SEQ = "user_no_seq";

    public static final String USER_VALUE = "1000000000000000000";
    /**
     * 用户登陆相关的redis key
     * agentApi2:userInfo:loginToken = 用户信息
     */
    public static final String REDIS_LOGIN_TOKEN_KEY = REDIS_KEY_PREFIX + ":loginToken:%s";

    /**
     * es 索引
     */
    public static final String NPOSP_ES_INDEX = "nposp_es";
    public static final String NPOSP_ES_TYPE= "_doc";

    /**
     * 登陆loginToken的存活时间
     */
    public static final String SYS_CONFIG_LOGIN_TOKEN_TTL = "agentApi2_login_token_ttl";
    /**
     * 优质商户 本月交易金额>=x元
     * 默认值: 本月交易金额≥50000元
     */
    public static final String SYS_CONFIG_QUALITY_SEARCH_CUR_MONTH_TRANS_MONEY = "agentApi2_merchant_quality_search_cur_month_trans_money";
    /**
     * 活跃商户 近x天交易笔数>=x笔,且交易金额>=x元
     * 默认值: 近30天交易笔数≥2笔并交易金额≥10元
     */
    public static final String SYS_CONFIG_ACTIVE_SEARCH_TRANS_DAY = "agentApi2_merchant_active_search_trans_day";
    public static final String SYS_CONFIG_ACTIVE_SEARCH_TRANS_ORDER_NUM = "agentApi2_merchant_active_search_trans_order_num";
    public static final String SYS_CONFIG_ACTIVE_SEARCH_TRANS_MONEY = "agentApi2_merchant_active_search_trans_money";
    /**
     * 休眠商户 入网≥X天,连续无交易大于X天
     * 默认值:入网≥60天,连续无交易大于60天
     */
    public static final String SYS_CONFIG_SLEEP_SEARCH_MERCHANT_CREATE = "agentApi2_merchant_sleep_search_merchant_create";
    public static final String SYS_CONFIG_SLEEP_SEARCH_TRANS_TIME = "agentApi2_merchant_sleep_search_trans_time";
    /**
     * 商户交易下滑汇总商户的最大数量
     */
    public static final String SYS_CONFIG_MAX_TRANS_SLIDE = "agentApi2_merchant_max_trans_slide";

}
