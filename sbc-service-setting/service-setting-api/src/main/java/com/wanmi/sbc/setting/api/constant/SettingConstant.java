package com.wanmi.sbc.setting.api.constant;

/**
 * Setting常量接口
 * @author hudong
 * @date 2023-07-07
 */
public interface SettingConstant {
    /**
     * 目录类枚举
     */
    enum MenuInfoEnum{
        /**
         * 入驻开店
         */
        SETTLE_IN_STORE("fc9e99b73fe311e9828800163e0fc468","入驻开店",2,"fc9e99b73fe311e9828800163e0fc468");
        private String key;
        private String desc;
        private Integer grade;
        private String parentKey;

        MenuInfoEnum(String key, String desc, Integer grade, String parentKey) {
            this.key = key;
            this.desc = desc;
            this.grade = grade;
            this.parentKey = parentKey;
        }

        public String getKey() {
            return key;
        }
        public Integer getGrade() {
            return grade;
        }

        public String getDesc() {
            return desc;
        }

        public String getParentKey() {
            return parentKey;
        }

    }

}
