package com.wanmi.sbc.setting.presetsearch.repositoy;

import com.wanmi.sbc.setting.presetsearch.model.PresetSearchTerms;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresetSearchTermsRepositoy  extends JpaRepository<PresetSearchTerms,Integer>,
        JpaSpecificationExecutor<PresetSearchTerms> {

    /**
     * 根据ID获取数据
     *
     * @param id
     * @return
     */
    PresetSearchTerms findById(Long id);

    /**
     * 根据ID删除数据
     * @param id
     */
    void deleteById(Long id);

    /**
     * 查询比指定排序大的数据
     *
     * @param sort
     * @return
     */
    List<PresetSearchTerms> findBySortGreaterThan(Integer sort);

    /**
     * 编辑排序
     *
     * @param id
     * @param sort
     */
    @Modifying
    @Query(value = "update PresetSearchTerms pst set pst.sort = ?2 where pst.id = ?1")
    void updateSort(Long id, Integer sort);
}
