package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.CcbClrgSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 建行分账汇总数据层
 * @author hudong
 * 2023-09-23 15:56
 */
@Repository
public interface CcbClrgSummaryRepository extends JpaRepository<CcbClrgSummary, Long>, JpaSpecificationExecutor<CcbClrgSummary> {


    @Query(value = "SELECT\n" +
            "\tt.id,\n" +
            "\tt.clrg_dt,\n" +
            "\tcount(1) AS trade_num,\n" +
            "  IFNULL(sum( t.clrg_amt ), 0) + IFNULL(sum( t.hdcg_amt ), 0) + IFNULL(t2.refundAmt, 0) AS trade_amt,\n" +
            "\tt2.refundAmt AS refund_amt,\n" +
            "\tsum( t.hdcg_amt ) AS hdcg_amt,\n" +
            "\tsum( t.clrg_amt ) AS clrg_amt,\n" +
            "\tt.rcvprt_mkt_mrch_id AS mkt_mrch_id,\n" +
            "\tt.py_ordr_no\n" +
            "FROM\n" +
            "\tccb_statement_det t\n" +
            "\tLEFT JOIN ( SELECT t.py_ordr_no, sum( t.tfr_amt ) AS refundAmt FROM ccb_statement_refund t GROUP BY t.py_ordr_no ) t2 ON t.py_ordr_no = t2.py_ordr_no \n" +
            "GROUP BY\n" +
            "\tt.clrg_dt,\n" +
            "\tt.rcvprt_mkt_mrch_id", nativeQuery = true)
    List<CcbClrgSummary> queryCcbClrgSummaryByList();

    /**
     * 根据分账日期统计数量
     * @param clrgDt
     * @return
     */
    Integer countCcbClrgSummaryByClrgDt(Date clrgDt);

}
