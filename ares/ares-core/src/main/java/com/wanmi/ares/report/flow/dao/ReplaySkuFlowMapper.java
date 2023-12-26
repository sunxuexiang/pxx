package com.wanmi.ares.report.flow.dao;


import com.wanmi.ares.report.flow.model.root.ReplaySkuFlow;

public interface ReplaySkuFlowMapper {

    int deleteByPrimaryKey(String id);

    int deleteByPrimary(ReplaySkuFlow record);

    int insert(ReplaySkuFlow record);

    int insertSelective(ReplaySkuFlow record);

    ReplaySkuFlow selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ReplaySkuFlow record);

    int updateByPrimaryKey(ReplaySkuFlow record);
}