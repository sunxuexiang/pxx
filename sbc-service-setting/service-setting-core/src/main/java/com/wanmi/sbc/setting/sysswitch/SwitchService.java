package com.wanmi.sbc.setting.sysswitch;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.sysswitch.SwitchEnum;
import com.wanmi.sbc.setting.api.request.sysswitch.SwitchRequest;
import com.wanmi.sbc.setting.api.response.SwitchGetByIdResponse;
import com.wanmi.sbc.setting.redis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by yuanlinling on 2017/4/26.
 */
@Service
@Transactional(readOnly = true, timeout = 10)
public class SwitchService {

    @Autowired
    SwitchRepository switchRepository;

    @Autowired
    private RedisService redisService;

    /**
     * 根据id查询开关
     *
     * @param id
     * @return
     */
    public SwitchGetByIdResponse findSwitchById(String id){
        SwitchGetByIdResponse response = new SwitchGetByIdResponse();

        Optional<Switch> optional = switchRepository.findById(id);

        if (optional.isPresent()) {
            BeanUtils.copyProperties(optional.get(), response);
        }

        return response;
    }

    /**
     * 开关开启关闭
     *
     * @param id
     * @param status
     * @return
     */
    @Transactional
    public int updateSwitch(String id,Integer status){
        if(StringUtils.isEmpty(id) || status == null ){
            throw new SbcRuntimeException("K-000009");
        }
        return switchRepository.updateSwitch(id,status);
    }

    @Transactional
    public void save(SwitchRequest request){
        Switch entity = findSwitchByCode(request.getSwitchCode());
        if(entity.getId()==null){
            KsBeanUtil.copyPropertiesThird(request,entity);
            entity.setDelFlag(DeleteFlag.NO);
        }else{
            if(StringUtils.isNotBlank(request.getSwitchName())){
                entity.setSwitchName(request.getSwitchName());
            }
            if(null!=request.getStatus()){
                entity.setStatus(request.getStatus());
            }
        }
        if(entity.getStatus()==null){
            entity.setStatus(0);
        }
        if(StringUtils.isBlank(entity.getSwitchName())){
            entity.setSwitchName(entity.getSwitchCode());
        }
        entity.setSwitchCode(getRedisKey(entity.getSwitchCode()));
        SwitchEnum.checkSwtichCode(entity.getSwitchCode());
        switchRepository.save(entity);
        putRedis(entity.getSwitchCode(),entity.getStatus());
    }

    private Switch findSwitchByCode(String swithCode){
        List<Switch> switchList = switchRepository.findSwitchByCode(swithCode);
        if(CollectionUtils.isNotEmpty(switchList)){
            return switchList.get(0);
        }
        return new Switch();
    }

    private void deleteRedis(String swithCode) {
        redisService.hdelete(SWITCH_KEY,swithCode.trim());
    }

    private void putRedis(String swithCode,Integer status) {
        deleteRedis(swithCode);
        redisService.hset(SWITCH_KEY,swithCode , Objects.toString(status));
    }

    public Integer getStatus(String switchCode){
        String key =getRedisKey(switchCode);
        String statu = redisService.hget(SWITCH_KEY,key);
        if(statu ==null){
          List<Integer> list =  switchRepository.findBySwtichCode(key);
          Integer s=0;
          if(CollectionUtils.isNotEmpty(list)){
              s = list.get(0);
          }
          putRedis(key,s);
          return s;
        }
        return Integer.parseInt(statu);
    }

    public Boolean isOpen(String switchCode){
        Integer s= getStatus(switchCode);
        return s==1;
    }
    private static final String SWITCH_KEY = "switch";
    public String getRedisKey(String switchCode){
        if(StringUtils.isBlank(switchCode)){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"switchRedisKey错误");
        }
        switchCode=switchCode.trim();
        return switchCode;
        //return String.format(SWITCH_KEY,switchCode);
    }
}
