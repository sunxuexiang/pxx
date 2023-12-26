package com.wanmi.sbc.setting.videomanagement.repository;

import com.wanmi.sbc.setting.videomanagement.model.root.VideoSearchRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoSearchRecordRepository extends JpaRepository<VideoSearchRecord, Long>,
        JpaSpecificationExecutor<VideoSearchRecord> {
    List<VideoSearchRecord> findByCustomerIdAndContent(String customerId, String videoName);

    @Query(value = "select * from video_search_record where customer_id = ?1 order by search_time desc",
            countQuery = "select count(1) from video_search_record where customer_id = ?1", nativeQuery = true)
    Page<VideoSearchRecord> findByCustomerId(String customerId, Pageable pageable);
}
