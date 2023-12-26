package com.wanmi.sbc.live.host.service;

import com.wanmi.sbc.common.util.UUIDUtil;
import com.wanmi.sbc.live.api.request.host.*;
import com.wanmi.sbc.live.bean.vo.LiveHostVO;
import com.wanmi.sbc.live.host.dao.LiveHostMapper;
import com.wanmi.sbc.live.host.model.root.LiveHost;
import com.wanmi.sbc.live.hostAccount.dao.LiveHostAccountMapper;
import com.wanmi.sbc.live.hostAccount.model.root.LiveHostAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("LiveHostService")
public class LiveHostService {

    @Resource
    private LiveHostMapper liveHostMapper;

    @Resource
    private LiveHostAccountMapper liveHostAccountMapper;


    /**
     * 获取主播分页列表
     * @param request
     * @return
     */
    public Page<LiveHostVO> getPage(LiveHostPageRequest request){
        List<LiveHost> pageList=liveHostMapper.getPage(request);
        int total=liveHostMapper.getPageCount(request);
        List<LiveHostVO> liveHostVOList=new ArrayList<>();
        pageList.forEach(liveHost -> {
            LiveHostVO vo=new LiveHostVO();
            BeanUtils.copyProperties(liveHost, vo);
            liveHostVOList.add(vo);
        });
        request.setPageNum(request.getPageNum() / request.getPageSize());
        return new PageImpl<LiveHostVO>(liveHostVOList, request.getPageable(), total);
    }

    /**
     * 获取主播信息
     * @param hostId
     * @return
     */
    public LiveHost getInfo(int hostId){
        return liveHostMapper.getInfo(hostId);
    }

    public LiveHost getInfo(LiveHostInfoRequest request){
        LiveHostAccount liveHostAccount = liveHostAccountMapper.getInfo(request);
        if(liveHostAccount==null){
            return null;
        }
        return liveHostMapper.getInfo(liveHostAccount.getHostId());
    }

    /**
     * 获取所有已启用的直播账户
     * @return
     */
    public List<String> getEnableCustomerAccountList(){
        return liveHostAccountMapper.getEnableCustomerAccountList();
    }

    @Transactional
    public int add(LiveHostAddRequest request){
        List<LiveHostCustomerAccount> accounts = request.getAccounts();
        List<LiveHostCustomerAccount> insertAccounts=new ArrayList<>();
        for (int i = 0; accounts!=null && i < accounts.size(); i++) {
            LiveHostCustomerAccount account = accounts.get(i);
            LiveHostAccount info = liveHostAccountMapper.getInfo(LiveHostInfoRequest.request(account.getCustomerId()));
            if(info==null){
                insertAccounts.add(account);
            }else{
                log.warn("{}账户已绑定其他主播",account.getCustomerAccount());
            }
        }
        request.setAccounts(insertAccounts);

        LocalDateTime currentDate= LocalDateTime.now();
        LiveHost liveHost=new LiveHost(request);
        liveHost.setDelFlag(0L);//未删除
        liveHost.setCreateTime(currentDate);
        liveHost.setUpdateTime(currentDate);
        int count = liveHostMapper.add(liveHost);

        for (int i = 0; i < insertAccounts.size(); i++) {
            LiveHostCustomerAccount account = accounts.get(i);
            LiveHostAccount liveHostAccount=new LiveHostAccount();
            liveHostAccount.setHostId(liveHost.getHostId());
            liveHostAccount.setCustomerId(account.getCustomerId());
            liveHostAccount.setCustomerAccount(account.getCustomerAccount());
            liveHostAccount.setDelFlag(0);
            liveHostAccount.setDataSn(account.getCustomerId());//数据唯一ID，如果删除则设置为UUID
            liveHostAccount.setCreateTime(currentDate);
            liveHostAccount.setUpdateTime(currentDate);
            liveHostAccountMapper.add(liveHostAccount);
        }
        return count;
    }

    @Transactional
    public int modify(LiveHostModifyRequest request){
        LiveHost info = liveHostMapper.getInfo(request.getHostId());
        if(info==null){
            throw new RuntimeException("主播信息不存在");
        }

        List<LiveHostAccount> allAccounts=liveHostAccountMapper.getListByHostId(request.getHostId());
        List<LiveHostCustomerAccount> accounts = request.getAccounts();
        List<LiveHostCustomerAccount> insertAccounts=new ArrayList<>();
        for (int i = 0; accounts!=null && i < accounts.size(); i++) {
            LiveHostCustomerAccount account = accounts.get(i);
            insertAccounts.add(account);
            /*LiveHostAccount liveHostAccount = liveHostAccountMapper.getInfo(LiveHostInfoRequest.request(account.getCustomerId()));
            if(liveHostAccount==null){

            }else{
                log.warn("{}账户已绑定其他主播",account.getCustomerAccount());
            }*/
        }
        request.setAccounts(insertAccounts);


        LocalDateTime currentDate= LocalDateTime.now();
        if(insertAccounts.size()==0){
            allAccounts.stream().forEach(item ->{
                item.setDelFlag(1);
            });
        }else{
           /* allAccounts.stream().forEach(item ->{
                item.setDelFlag(1);//设置为已删除
                item.setDataSn(UUIDUtil.getUUID());//随机数据唯一ID
            });*/
            for (LiveHostCustomerAccount customerAccount:insertAccounts) {
                boolean exist=false;
                b:for (LiveHostAccount account:allAccounts){
                    if(account.getCustomerId().equals(customerAccount.getCustomerId())){
                        account.setDelFlag(0);//未删除
                        account.setDataSn(account.getCustomerId());//数据唯一ID
                        exist=true;
                        break b;
                    }
                }
                if(!exist){//新增
                    LiveHostAccount liveHostAccount=new LiveHostAccount();
                    liveHostAccount.setCustomerId(customerAccount.getCustomerId());
                    liveHostAccount.setCustomerAccount(customerAccount.getCustomerAccount());
                    liveHostAccount.setDelFlag(0);
                    liveHostAccount.setDataSn(customerAccount.getCustomerId());//数据唯一ID
                    liveHostAccount.setCreateTime(currentDate);
                    liveHostAccount.setUpdateTime(currentDate);
                    allAccounts.add(liveHostAccount);
                }
            }
        }


        LiveHost modify=new LiveHost(request);
        modify.setUpdateTime(currentDate);
        int count = liveHostMapper.modify(modify);

        for (int i = 0;i < allAccounts.size(); i++) {
            LiveHostAccount liveHostAccount = allAccounts.get(i);
            if(liveHostAccount.getHostAccountId()==null){
                liveHostAccount.setHostId(request.getHostId());
                liveHostAccount.setCreateTime(currentDate);
                liveHostAccount.setUpdateTime(currentDate);
                liveHostAccountMapper.add(liveHostAccount);
            }else{
                liveHostAccount.setUpdateTime(currentDate);
                liveHostAccountMapper.modify(liveHostAccount);
            }
        }
        return count;
    }


    @Transactional
    public int delete(LiveHostDeleteRequest request){
        LiveHost info = liveHostMapper.getInfo(request.getHostId());
        if(info==null){
            throw new RuntimeException("主播信息不存在");
        }
        int count=liveHostAccountMapper.getEnableCountByHostId(request.getHostId());
        if(count>0){
            return 11;
        }

        LocalDateTime currentDate= LocalDateTime.now();
        LiveHost modify=new LiveHost();
        modify.setHostId(info.getHostId());
        modify.setUpdateTime(currentDate);
        modify.setDelFlag(1L);//删除
        liveHostMapper.modify(modify);

        return count;
    }

    /**
     * 离职
     * @param request
     * @return
     */
    @Transactional
    public int leave(LiveHostLeaveRequest request){
        LiveHost info = liveHostMapper.getInfo(request.getHostId());
        if(info==null){
            throw new RuntimeException("主播信息不存在");
        }
        int count=liveHostAccountMapper.getEnableCountByHostId(request.getHostId());
        if(count>0){
            throw new RuntimeException("请先取消关联的直播账户");
        }

        LocalDateTime currentDate= LocalDateTime.now();
        LiveHost modify=new LiveHost();
        modify.setHostId(info.getHostId());
        modify.setUpdateTime(currentDate);
        modify.setWorkingState(0L);//离职
        count = liveHostMapper.modify(modify);

        return count;
    }

    /**
     * 重新启用
     * @param request
     * @return
     */
    @Transactional
    public int enable(LiveHostEnableRequest request){
        LiveHost info = liveHostMapper.getInfo(request.getHostId());
        if(info==null){
            throw new RuntimeException("主播信息不存在");
        }

        LocalDateTime currentDate= LocalDateTime.now();
        LiveHost modify=new LiveHost();
        modify.setHostId(info.getHostId());
        modify.setUpdateTime(currentDate);
        modify.setWorkingState(1L);//在职
        int count = liveHostMapper.modify(modify);

        return count;
    }
}