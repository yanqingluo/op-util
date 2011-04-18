package com.taobao.top.common.diagnostic.aop;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


public class CacheStatInterceptor implements MethodInterceptor {

	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object result = null;
		Method method = invocation.getMethod();
		Object[] args = invocation.getArguments();
		long start = System.currentTimeMillis();
		int code = 0;
		try {
			result = invocation.proceed();
		} catch (Exception e) {
			code = -1;
			throw e;
		} finally {
			// 有namespace为参数的方法
			if (args != null && args.length >= 2) {
				long duration = System.currentTimeMillis() - start;
				int namespace = (Integer) args[0];
			}

		}
		return result;
	}

}