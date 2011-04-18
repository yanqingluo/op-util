package com.taobao.top.common.resource;

import java.util.concurrent.ExecutionException;

/**
 * 描述：共享资源管理类，统一控制资源的并发初始化、资源的有效性等
 * 
 * @author <a href="mailto:jiuren@taobao.com">jiuren</a>
 */
public interface SharedResources<V> {

	/**
	 * 取单实例共享资源。 如果资源初始化过程中抛出异常，则删除该资源，以便下一个线程可以再次初始化资源。最后抛出ExecutionException。
	 * 
	 * @return
	 * @throws ExecutionException
	 *             如果资源初始化过程中抛出异常
	 * @throws ResourceInvalidException
	 *             如果资源初始化正常，但后续检验结果为不合格
	 * @throws InterruptedException
	 *             如果线程在等待资源初始化的时候被中断
	 */
	public V getResource() throws ExecutionException, ResourceInvalidException,
			InterruptedException;

	/**
	 * 取多实例共享资源。 如果资源初始化过程中抛出异常，则删除该资源，以便下一个线程可以再次初始化资源。最后抛出ExecutionException。
	 * 
	 * @param resourceKey
	 *            资源key
	 * @return
	 * @throws ExecutionException
	 *             如果资源初始化过程中抛出异常
	 * @throws ResourceInvalidException
	 *             如果资源初始化正常，但后续检验结果为不合格
	 * @throws InterruptedException
	 *             如果线程在等待资源初始化的时候被中断
	 */
	public V getResource(final String key) throws ExecutionException,
			ResourceInvalidException, InterruptedException;

}
