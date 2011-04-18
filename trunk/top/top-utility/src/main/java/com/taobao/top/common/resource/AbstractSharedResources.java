package com.taobao.top.common.resource;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 描述：共享资源管理类，统一控制资源的并发初始化、资源的有效性等
 * 
 * @author <a href="mailto:jiuren@taobao.com">jiuren</a>
 */
public abstract class AbstractSharedResources<V> implements SharedResources<V> {

	static private final String SINGLE_RSC_KEY = "single_resource";

	private ConcurrentHashMap<String, FutureTask<V>> initRightSelect = new ConcurrentHashMap<String, FutureTask<V>>();

	private AtomicBoolean testOnGet = new AtomicBoolean(true);

	/**
	 * 指定取资源时是否检验资源的有效性(Validation)
	 */
	public void testOnGet(boolean b) {
		testOnGet.set(b);
	}

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
			InterruptedException {

		if (!isSingletonResource()) {
			throw new RuntimeException("多实例资源管理器, 不能调用无参数的gerResource()方法.");
		}

		int retry = 2;
		V resource = null;
		FutureTask<V> initTask;

		for (int i = 0; (i < retry) && (resource == null); ++i) {
			initTask = initRightSelect.get(SINGLE_RSC_KEY);
			if (initTask == null) {
				initTask = new FutureTask<V>(new Callable<V>() {
					public V call() throws Exception {
						return initResource();
					}
				});
				initTask = initRightSelect
						.putIfAbsent(SINGLE_RSC_KEY, initTask);
				if (initTask == null) {
					initTask = initRightSelect.get(SINGLE_RSC_KEY);
					initTask.run();
				}
			}

			try {
				resource = initTask.get();
				if (testOnGet.get()) {
					validate(resource);
				}
			} catch (ExecutionException e) {
				initRightSelect.remove(SINGLE_RSC_KEY);
				throw e;
			} catch (ResourceInvalidException e) {
				initRightSelect.remove(SINGLE_RSC_KEY);
				if (i + 1 < retry) {
					resource = null;
				} else {
					throw e;
				}
			}
		}
		return resource;
	}

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
			ResourceInvalidException, InterruptedException {

		if (isSingletonResource()) {
			throw new RuntimeException("单实例资源管理器, 不能调用带参数的gerResource(key)方法.");
		}

		int retry = 2;
		V resource = null;
		FutureTask<V> initTask;

		for (int i = 0; (i < retry) && (resource == null); ++i) {
			initTask = initRightSelect.get(key);
			if (initTask == null) {
				initTask = new FutureTask<V>(new Callable<V>() {
					public V call() throws Exception {
						return initResource(key);
					}
				});
				initTask = initRightSelect.putIfAbsent(key, initTask);
				if (initTask == null) {
					initTask = initRightSelect.get(key);
					initTask.run();
				}
			}

			try {
				resource = initTask.get();
				if (testOnGet.get()) {
					validate(resource);
				}
			} catch (ExecutionException e) {
				initRightSelect.remove(key);
				throw e;
			} catch (ResourceInvalidException e) {
				initRightSelect.remove(key);
				if (i + 1 < retry) {
					resource = null;
				} else {
					throw e;
				}
			}
		}
		return resource;
	}

	/**
	 * 返回true当此管理器只管理单个资源实例。 当管理多个资源实例时，资源之间以key区分.
	 * 比如：古代的貂蝉、西施、小乔等，她们同属美女，但每个人有不同的名字、相貌、技艺。
	 * 
	 * @return
	 */
	protected boolean isSingletonResource() {
		return false;
	}

	/**
	 * 初始化资源实例。 注意：该方法只能被getResource()调用，不能通过调用该方法获取资源实例。
	 * 
	 * 如果子类管理单实例资源，则只实现initResource() 如果子类管理多实例资源，则只实现keyedInitResource()
	 */
	protected V initResource() throws Exception {
		return null;
	}

	/**
	 * 初始化资源实例。 注意：该方法只能被getResource()调用，不能通过调用该方法获取资源实例。
	 * 
	 * 如果子类管理单实例资源，则只实现initResource() 如果子类管理多实例资源，则只实现keyedInitResource()
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	abstract protected V initResource(String key) throws Exception;

	/**
	 * 检验资源是否合格.如不合格则抛出ResourceInvalidException异常.
	 */
	protected void validate(V resource) throws ResourceInvalidException {
		return;
	}

}
