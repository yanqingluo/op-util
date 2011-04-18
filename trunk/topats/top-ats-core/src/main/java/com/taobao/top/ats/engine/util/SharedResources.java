package com.taobao.top.ats.engine.util;

import java.util.concurrent.ExecutionException;

/**
 * 共享资源接口。
 * 
 * @author carver.gu
 * @since 1.0, Aug 23, 2010
 */
public interface SharedResources<V> {

	/**
	 * 取单实例共享资源。 如果资源初始化过程中抛出异常，则删除该资源，以便下一个线程可以再次初始化资源。
	 * 
	 * @throws ExecutionException 如果资源初始化过程中抛出异常
	 * @throws ResourceInvalidException 如果资源初始化正常，但后续检验结果为不合格
	 * @throws InterruptedException 如果线程在等待资源初始化的时候被中断
	 */
	public V getResource() throws ExecutionException, ResourceInvalidException, InterruptedException;

	/**
	 * 取多实例共享资源。 如果资源初始化过程中抛出异常，则删除该资源，以便下一个线程可以再次初始化资源。
	 * 
	 * @param key 资源KEY
	 * @throws ExecutionException 如果资源初始化过程中抛出异常
	 * @throws ResourceInvalidException 如果资源初始化正常，但后续检验结果为不合格
	 * @throws InterruptedException 如果线程在等待资源初始化的时候被中断
	 */
	public V getResource(final String key) throws ExecutionException, ResourceInvalidException, InterruptedException;

}
