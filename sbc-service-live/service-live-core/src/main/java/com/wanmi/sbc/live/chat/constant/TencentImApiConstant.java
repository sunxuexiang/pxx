package com.wanmi.sbc.live.chat.constant;

public class TencentImApiConstant {
    /**
     * 账号管理
     */
    public static class AccountManage {
        /**导入单个帐号*/
        public static final String ACCOUNT_IMPORT = "v4/im_open_login_svc/account_import";
        /**导入多个帐号*/
        public static final String MULTI_ACCOUNT_IMPORT = "v4/im_open_login_svc/multiaccount_import";
        /**删除帐号*/
        public static final String ACCOUNT_DELETE = "v4/im_open_login_svc/account_delete";
        /**查询帐号*/
        public static final String ACCOUNT_CHECK = "v4/im_open_login_svc/account_check";
        /**失效帐号登录状态*/
        public static final String ACCOUNT_KICK = "v4/im_open_login_svc/kick";
        /**查询账号在线状态*/
        public static final String ACCOUNT_QUERY_STATE = "v4/openim/query_online_status";
    }

    /**
     * 群组管理
     */
    public static class GroupManage {
        /**App 管理员可以通过该接口获取 App 中所有群组的 ID。*/
        public static final String GET_ALL_GROUP_ID = "v4/group_open_http_svc/get_appid_group_list";
        /**创建群组*/
        public static final String CREATE_GROUP = "v4/group_open_http_svc/create_group";
        /**获取群详细资料*/
        public static final String GET_GROUP_INFO = "v4/group_open_http_svc/get_group_info";
        /**获取群成员详细资料*/
        public static final String GET_GROUP_MEMBER_INFO = "v4/group_open_http_svc/get_group_member_info";
        /**修改群基础资料*/
        public static final String MODIFY_GROUP_BASE_INFO = "v4/group_open_http_svc/modify_group_base_info";
        /**增加群成员*/
        public static final String ADD_GROUP_MEMBER = "v4/group_open_http_svc/add_group_member";
        /**删除群成员*/
        public static final String DELETE_GROUP_MEMBER = "v4/group_open_http_svc/delete_group_member";
        /**修改群成员资料*/
        public static final String MODIFY_GROUP_MEMBER_INFO = "v4/group_open_http_svc/modify_group_member_info";
        /**解散群组*/
        public static final String DESTROY_GROUP = "v4/group_open_http_svc/destroy_group";
        /**获取用户所加入的群组*/
        public static final String GET_JOINED_GROUP_LIST = "v4/group_open_http_svc/get_joined_group_list";
        /**查询用户在群组中的身份*/
        public static final String GET_ROLE_IN_GROUP = "v4/group_open_http_svc/get_role_in_group";
        /**批量禁言和取消禁言*/
        public static final String FORBID_SEND_MSG = "v4/group_open_http_svc/forbid_send_msg";
        /**获取被禁言群成员列表*/
        public static final String GET_GROUP_SHUT_UIN = "v4/group_open_http_svc/get_group_shutted_uin";
        /**在群组中发送普通消息*/
        public static final String SEND_GROUP_MSG = "v4/group_open_http_svc/send_group_msg";
        /**在群组中发送系统通知*/
        public static final String SEND_GROUP_SYSTEM_NOTIFICATION = "v4/group_open_http_svc/send_group_system_notification";
        /**撤回群消息*/
        public static final String GROUP_MSG_RECALL = "v4/group_open_http_svc/group_msg_recall";
        /**转让群主*/
        public static final String CHANGE_GROUP_OWNER = "v4/group_open_http_svc/change_group_owner";
        /**导入群基础资料*/
        public static final String IMPORT_GROUP = "v4/group_open_http_svc/import_group";
        /**导入群消息*/
        public static final String IMPORT_GROUP_MSG = "v4/group_open_http_svc/import_group_msg";
        /**导入群成员*/
        public static final String IMPORT_GROUP_MEMBER = "v4/group_open_http_svc/import_group_member";
        /**设置成员未读消息计数*/
        public static final String SET_UNREAD_MSG_NUM = "v4/group_open_http_svc/set_unread_msg_num";
        /**删除指定用户发送的消息*/
        public static final String DELETE_GROUP_MSG_BY_SENDER = "v4/group_open_http_svc/delete_group_msg_by_sender";
        /**拉取群历史消息*/
        public static final String GROUP_MSG_GET_SIMPLE = "v4/group_open_http_svc/group_msg_get_simple";
        /**获取直播群在线人数*/
        public static final String GET_ONLINE_MEMBER_NUM = "v4/group_open_http_svc/get_online_member_num";
    }

    /**
     * 全局禁言管理
     */
    public static class AllSinentManage {
        /**设置全局禁言*/
        public static final String SET_NO_SPEAKING = "v4/openconfigsvr/setnospeaking";
        /**查询全局禁言*/
        public static final String GET_NO_SPEAKING = "v4/openconfigsvr/getnospeaking";
    }

}
