package com.wanmi.sbc.advertising.util;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.HibernateException;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.property.access.spi.SetterMethodImpl;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XyyResultTransformer extends AliasedTupleSubsetResultTransformer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Class resultClass;
	private boolean isInitialized;
	private String[] aliases;
	private Setter[] setters;

	public XyyResultTransformer(Class resultClass) {
		if (resultClass == null) {
			throw new IllegalArgumentException("resultClass cannot be null");
		}
		isInitialized = false;
		this.resultClass = resultClass;
	}

	@Override
	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
		return false;
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		Object result;

		try {
			if (!isInitialized) {
				initialize(aliases);
			} else {
				check(aliases);
			}

			result = resultClass.newInstance();

			for (int i = 0; i < aliases.length; i++) {
				if (setters[i] != null) {
					// 转换特定类型
					Object castTuple = castTuple((SetterMethodImpl) setters[i], tuple[i]);
					setters[i].set(result, castTuple, null);
				}
			}
		} catch (InstantiationException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		} catch (IllegalAccessException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		}

		return result;
	}

	private Object castTuple(SetterMethodImpl setterMethodImpl, Object obj) {
		if (obj == null) {
			return obj;
		}
		// BigInteger转成long
		if (obj instanceof BigInteger) {
			return ((BigInteger) obj).longValue();
		}
		
		// 枚举值转换
		Class<?>[] parameterTypes = setterMethodImpl.getMethod().getParameterTypes();
		// 只取第一个参数
		if (ArrayUtils.isNotEmpty(parameterTypes) && parameterTypes[0].isEnum()) {
			Method[] methods = parameterTypes[0].getMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(JsonCreator.class)) {
					try {
						Object res = method.invoke(null, obj);
						return res;
					} catch (Exception e) {
						log.error("方法[{}]枚举值[{}]赋值时报错,返回null值", method, obj);
						return null;
					} 
				}
			}
		}
		
		return obj;
	}

	private void initialize(String[] aliases) {
		PropertyAccessStrategyChainedImpl propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(
				PropertyAccessStrategyBasicImpl.INSTANCE, PropertyAccessStrategyFieldImpl.INSTANCE,
				PropertyAccessStrategyMapImpl.INSTANCE);
		this.aliases = new String[aliases.length];

		setters = new Setter[aliases.length];

		for (int i = 0; i < aliases.length; i++) {

			String alias = aliases[i];

			if (alias != null) {
				this.aliases[i] = alias;
				alias = underlineToCamel(alias);
				setters[i] = propertyAccessStrategy.buildPropertyAccess(resultClass, alias).getSetter();
			}
		}
		isInitialized = true;
	}

	private void check(String[] aliases) {
		if (!Arrays.equals(aliases, this.aliases)) {
			throw new IllegalStateException("aliases are different from what is cached; aliases="
					+ Arrays.asList(aliases) + " cached=" + Arrays.asList(this.aliases));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		XyyResultTransformer that = (XyyResultTransformer) o;

		if (!resultClass.equals(that.resultClass)) {
			return false;
		}
		if (!Arrays.equals(aliases, that.aliases)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = resultClass.hashCode();
		result = 31 * result + (aliases != null ? Arrays.hashCode(aliases) : 0);
		return result;
	}

	/**
	 * 下划线转驼峰
	 * 
	 * @param param
	 * @return
	 */
	public static String underlineToCamel(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (c == '_') {
				if (++i < len) {
					sb.append(Character.toUpperCase(param.charAt(i)));
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
