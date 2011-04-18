package com.taobao.top.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.taobao.hsf.app.spring.util.HSFSpringConsumerBean;
import com.taobao.hsf.app.spring.util.SuperHSFSpringConsumerBeanTop;

public class ServiceUtil {
	public static void waitServiceReady(Object... serviceList) {
		int timePerSleep = 5;
		int totalTimeout = 2000;
		int sleepTime = 0;
		try {
			for (Object service : serviceList) {
				if (sleepTime > totalTimeout) {
					System.out
							.println("wait configServer pushing address timeout: "
									+ totalTimeout + "ms");
					break;
				}

				boolean ready = isServiceAddressReadyFromHSFSpringConsumerBean(service);
				while (!ready) {
					Thread.sleep(timePerSleep);
					sleepTime += timePerSleep;
					ready = isServiceAddressReadyFromHSFSpringConsumerBean(service);
					if (sleepTime > totalTimeout) {
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isServiceAddressReadyForTop(
			SuperHSFSpringConsumerBeanTop service) throws Exception {
		ClassLoader loader = SuperHSFSpringConsumerBeanTop.class
				.getClassLoader();
		Class serviceHolderClazz = Class.forName(
				"com.taobao.hsf.app.spring.util.HSFServiceHolderComponent",
				true, loader);
		Object metaService = invokeStaticMethodWithoutParam(serviceHolderClazz,
				"getMetadataService");
		Object addrService = invokeReadField(metaService, "addressService");
		Object metadata = invokeReadField(service, "metadata");
		String serviceUniqueName = (String)invokeMethodWithoutParam(metadata, "getUniqueName");
		Object routeResultCache = invokeMethodWithParam(addrService,
				"getRouteResultCache", serviceUniqueName);
		Object addrList = invokeMethodWithoutParam(routeResultCache,
				"getInterfaceAddresses");
		int size = (Integer) invokeMethodWithoutParam(addrList, "size");
		return size > 0;
	}

	public static boolean isServiceAddressReadyFromHSFSpringConsumerBean(
			Object service) throws Exception {
		ClassLoader loader = HSFSpringConsumerBean.class.getClassLoader();
		// Class serviceHolderClazz =
		// loader.loadClass("com.taobao.hsf.app.spring.util.HSFServiceHolderComponent");
		Class serviceHolderClazz = Class.forName(
				"com.taobao.hsf.app.spring.util.HSFServiceHolderComponent",
				true, loader);
		Object metaService = invokeStaticMethodWithoutParam(serviceHolderClazz,
				"getMetadataService");
		Object addrService = invokeReadField(metaService, "addressService");
		Object routeResultCache = invokeMethodWithParam(addrService,
				"getRouteResultCache",
				getServiceUniqueNameFromServiceProxy(service));
		Object addrList = invokeMethodWithoutParam(routeResultCache,
				"getInterfaceAddresses");
		int size = (Integer) invokeMethodWithoutParam(addrList, "size");
		return size > 0;
	}

	private static String getServiceUniqueNameFromServiceProxy(
			Object serviceProxy) throws Exception {
		Object hsfProxy = Proxy.getInvocationHandler(serviceProxy);
		Object metadata = invokeReadField(hsfProxy, "serviceConsumerMetadata");
		String uniqueName = (String) invokeMethodWithoutParam(metadata,
				"getUniqueName");
		return uniqueName;
	}

	private static Object invokeMethodWithoutParam(Object instance,
			String methodName) throws Exception {
		Method method = instance.getClass().getDeclaredMethod(methodName);
		method.setAccessible(true);
		return method.invoke(instance, null);
	}

	private static Object invokeMethodWithParam(Object instance,
			String methodName, Object... values) throws Exception {
		Class[] paramTypes = new Class[values.length];
		for (int i = 0; i < values.length; i++) {
			paramTypes[i] = values[i].getClass();
		}
		Method method = instance.getClass().getDeclaredMethod(methodName,
				paramTypes);
		method.setAccessible(true);
		Class returnType = method.getReturnType();
		if (returnType == null) {// returnType instanceof Void
			method.invoke(instance, values);
		}
		return method.invoke(instance, values);
	}

	private static Object invokeStaticMethodWithoutParam(Class clazz,
			String methodName) throws Exception {
		Method method = clazz.getDeclaredMethod(methodName);
		method.setAccessible(true);
		return method.invoke(null, null);
	}

	private static Object invokeReadField(Object instance, String fieldName)
			throws Exception {
		Class clazz = instance.getClass();
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(instance);
	}

	private static Object invokeGetStaticField(Class clazz, String fieldName)
			throws Exception {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(null);
	}


}
