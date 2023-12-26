package com.wanmi.sbc.setting.gatherboxset.service;

import com.wanmi.sbc.setting.gatherboxset.model.root.GatherBoxSet;
import com.wanmi.sbc.setting.gatherboxset.repository.GatherBoxSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("GatherBoxSetService")
public class GatherBoxSetService {

    @Autowired
    private GatherBoxSetRepository gatherBoxSetRepository;
    /**
     * 修改修改凑箱配置
     * @author lq
     */
    @Transactional
    public GatherBoxSet modify(GatherBoxSet entity) {
        gatherBoxSetRepository.save(entity);
        return entity;
    }

    /**
     * 获取凑箱设置
     * @return
     */
    public  GatherBoxSet getGatherBoxSet(){
        List<GatherBoxSet> gatherBoxSetList=gatherBoxSetRepository.findAll();
        if(gatherBoxSetList.size()>0){
            return gatherBoxSetList.get(0);
        }
        return null;
    }

}
