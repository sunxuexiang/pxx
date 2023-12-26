package com.wanmi.sbc.customer.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author baijianzhong
 * @ClassName CustomUUIDGenerator
 * @Date 2019-10-21 19:31
 * @Description TODO
 **/
public class CustomerUUIDGenerator extends UUIDGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Object id =  getFieldValueByName( object);

        if (id != null) {
            return (Serializable) id;
        }
        String uuid = (String) super.generate(session, object);
        return uuid.replaceAll("-","");
    }

    private Object getFieldValueByName(Object o) {
        Object value = null;
        try {
            Field[] fields = o.getClass().getDeclaredFields();
            for(int i=0;i<fields.length;i++) {
                Field field = fields[i];
                if(field.getAnnotation(Id.class)!=null) {
                    String firstLetter = field.getName().substring(0, 1).toUpperCase();
                    String getter = "get" + firstLetter + field.getName().substring(1);
                    Method method = o.getClass().getMethod(getter, new Class[] {});
                    value = method.invoke(o, new Object[] {});
                }
            }
            return value;
        } catch (Exception e) {
            return null;
        }
    }
}
