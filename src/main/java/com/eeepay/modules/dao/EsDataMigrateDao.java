package com.eeepay.modules.dao;

import com.eeepay.modules.bean.EsNpospDataBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-08-02 16:12
 */
@Mapper
public interface EsDataMigrateDao {
    EsNpospDataBean queryMerchantInfo(@Param("merchantNo") String merchantNo);

    List<EsNpospDataBean> listMbpInfo(@Param("merchantNo") String merchantNo);

    List<EsNpospDataBean> listMbpInfoByMerchantNoAndBpId(@Param("merchantNo") String merchantNo, @Param("bpId")String bpId);

    List<EsNpospDataBean> listOrderInfo(@Param("merchantNo") String merchantNo,
                                        @Param("pageSize") int pageSize,
                                        @Param("offset") int offset);
}
