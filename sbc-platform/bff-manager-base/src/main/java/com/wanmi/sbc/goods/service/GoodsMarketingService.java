package com.wanmi.sbc.goods.service;

import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.marketing.bean.vo.GoodsMarketingExportVo;
import com.wanmi.sbc.order.bean.vo.TradeDetailedExportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

/**
 * 商品EXCEL处理服务
 * Created by dyt on 2017/8/17.
 */
@Slf4j
@Service
public class GoodsMarketingService {


    /**
     * @param goodsMarketingExportVoList
     */
    public void export(List<GoodsMarketingExportVo> goodsMarketingExportVoList,OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("ERP编号", new SpelColumnRender<GoodsMarketingExportVo>("erpGoodsInfoNo")),
                new Column("SKU编号",new SpelColumnRender<GoodsMarketingExportVo>("goodsInfoNo")),
                new Column("商品名称", new SpelColumnRender<GoodsMarketingExportVo>("goodsInfoName")),
                new Column("商品分类", new SpelColumnRender<GoodsMarketingExportVo>("cateName")),
                new Column("品牌", new SpelColumnRender<GoodsMarketingExportVo>("brandName")),
                new Column("活动类型", new SpelColumnRender<GoodsMarketingExportVo>("subType")),
                new Column("活动名称", new SpelColumnRender<GoodsMarketingExportVo>("marketingName")),
                new Column("活动时间", new SpelColumnRender<GoodsMarketingExportVo>("beginAndEndTime")),
                new Column("活动力度", new SpelColumnRender<GoodsMarketingExportVo>("suitCouponDesc")),
                new Column("适用区域", new SpelColumnRender<GoodsMarketingExportVo>("wareName")),
                new Column("是否叠加", new SpelColumnRender<GoodsMarketingExportVo>("isOverlap")),
                new Column("是否终止", new SpelColumnRender<GoodsMarketingExportVo>("terminationFlag")),
                new Column("最后操作人", new SpelColumnRender<GoodsMarketingExportVo>("updatePerson")),
                new Column("最后操作时间", new SpelColumnRender<GoodsMarketingExportVo>("updateTime")),
        };
        excelHelper
                .addSheet(
                        "商品活动信息导出",
                        columns,
                        goodsMarketingExportVoList
                );
        excelHelper.write(outputStream);
    }
}