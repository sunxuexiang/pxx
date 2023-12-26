package com.wanmi.sbc.setting.systemfile.repository;

import com.wanmi.sbc.setting.systemfile.model.root.SystemFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 平台文件DAO
 * @author hudong
 * @date 2023-09-08 09:12:49
 */
@Repository
public interface SystemFileRepository extends JpaRepository<SystemFile, Long>,
        JpaSpecificationExecutor<SystemFile> {

    /**
     * 单个删除平台文件
     * @author hd
     */
    @Modifying
    @Query("update SystemFile set delFlag = 1 where id = ?1")
    void deleteById(Long id);

    /**
     * 批量删除平台文件
     * @author hd
     */
    @Modifying
    @Query("update SystemFile set delFlag = 1 ,updateTime = now() where id in ?1")
    int deleteByIdList(List<Long> idList);

    /**
     * 根据多个ID编号更新平台文件
     *
     * @param path   分类ID
     * @param ids 素材ID
     */
    @Modifying
    @Query("update SystemFile w set w.path = ?1 ,w.updateTime = now() where w.id in ?1")
    void updatePathByIds(String path, List<Long> ids);


    @Query("from SystemFile w where w.id in ?1 and w.delFlag = 0")
    List<SystemFile> findByIdList(List<Long> idList);

    /**
     * 根据文件Key 查询文件信息
     * @param fileKey
     * @return
     */
    SystemFile findByFileKey(String fileKey);

}
