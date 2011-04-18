package com.taobao.top.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Note:原代码有并发bug. modified by huaisu
 * 
 * 经常遇到这种情况：获取一个数据（资源），如果不存在则初始化后返回，否则直接返回
 * 这个类将这种情况的初始化中要考虑的多线程并发问题的解决方法抽取出来，作为模板。
 * 使用者只要实现初始化方法，不用再考虑初始化本身之外的线程安全和并发性能的问题。
 * 
 * @author linxuan
 * @author huaisu
 *
 * @param <K> 获取资源的Key类型，若资源只是单个对象，不是map结构，则现实方法可以不使用key
 * @param <V> 资源的类型
 * @param <E> 资源初始化时可能抛出的异常。若没有异常抛出，可以设置E为RuntimeException
 */
public class ConcurrentInitializer<K, V, E extends Throwable> {
    private ConcurrentHashMap<K, ReadWriteLock> keyLockMap;
    private ResourceHolder<K, V, E> resourceHolder;
    
    
    
    public ConcurrentInitializer(ResourceHolder<K, V, E> resourceHolder){
        this.resourceHolder = resourceHolder;
        this.keyLockMap = new ConcurrentHashMap<K, ReadWriteLock>();
    }

    public V getData(K key) throws E {
        V data = resourceHolder.currentData(key);
        if (data != null)
            return data;
        return initData(key);
    }

    private V initData(K key) throws E {
    	//get specified lock of the key, to prevent too many threads of 
    	//same key to initialize the cache at the same time, thus protect
    	//the backing system.
        ReadWriteLock keyLock = getLock(key);
        
        if(keyLock.writeLock().tryLock()) {
            try {
                V data = resourceHolder.currentData(key);
                if (data == null) {
                    data = resourceHolder.initializeDate(key);
                }
                return data;
            } finally {
            	keyLock.writeLock().unlock();
            	// to prevent the map grows
            	keyLockMap.remove(key);
            }        	
        } else {
        	keyLock.readLock().lock();
        	//when threads come here, means that the writelock has been unlocked, 
        	//which means data should have been initialized.
        	try {
            	return resourceHolder.currentData(key);        		
        	} finally {
        		keyLock.readLock().unlock();        		
        	}
        }    
    }

    /**
     * Get lock of the key.
     * map each key with same value to the same lock, 
     * @param key
     * @return
     */
    private ReadWriteLock getLock(K key) {
    	ReadWriteLock lock = keyLockMap.get(key);
        if(lock == null) {
            lock = new ReentrantReadWriteLock();
            ReadWriteLock oldLock = keyLockMap.putIfAbsent(key, lock); 
            if(oldLock != null) {
            	lock = oldLock;
            }
        }
        System.out.println(lock);
        return lock;
    }

}
