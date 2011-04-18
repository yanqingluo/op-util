package com.taobao.top.common.cache.aop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.top.common.cache.ICache;

/**
 * 
 * @author zhudi
 * 
 */

public class ReplayAttackLocalCacheImpl implements ICache<String, Long> {

	private static final Log log = LogFactory
			.getLog(ReplayAttackLocalCacheImpl.class);
	/**
	 * the max size of this cache
	 */
	private int max_size = 1000;
	/**
	 * this is the cursor to refer to the map which will be clean next
	 */
	private AtomicInteger cleanCursor;
	/**
	 * this is the cursor to refer to the map which will be clean next
	 */
	private AtomicInteger cursor;
	/**
	 * the cache now size
	 */
	private AtomicInteger size;
	/**
	 * every time,when the size is going to surpass the max_size , the some
	 * content will be clean,and overflow times will increase
	 */
	private AtomicInteger overflowTimes;
	/**
	 * the cache object to save timestamp
	 */
	private InnerCache<String, Long>[] caches;
	/**
	 * lifecycle
	 */
	private long expiry = 30 * 60 * 1000L;
	/**
	 * ingore times 
	 */
	private AtomicLong ingoreTimes;
	
	private AtomicBoolean isOnClean = new AtomicBoolean(false);
	
	private int moduleSize = 10;
	
	
	private boolean isLogOn = false;

	/**
	 * clean job
	 */
	private ScheduledExecutorService scheduleService;

	public AtomicInteger getOverflowTimes() {
		return overflowTimes;
	}

	public void setOverflowTimes(AtomicInteger overflowTimes) {
		this.overflowTimes = overflowTimes;
	}

	public AtomicLong getIngoreTimes() {
		return ingoreTimes;
	}

	public void setIngoreTimes(AtomicLong ingoreTimes) {
		this.ingoreTimes = ingoreTimes;
	}

	public int getMax_size() {
		return max_size;
	}

	public void setMax_size(int maxSize) {
		max_size = maxSize;
	}

	public long getExpiry() {
		return expiry;
	}

	public void setExpiry(long expiry) {
		this.expiry = expiry;
	}

	public ReplayAttackLocalCacheImpl() {
		init();
	}

	public ReplayAttackLocalCacheImpl(int maxSize, long expiry) {
		this.max_size = maxSize;
		this.expiry = expiry;

		init();
	}
	public ReplayAttackLocalCacheImpl(int maxSize, long expiry,int moduleSize) {
		this.max_size = maxSize;
		this.expiry = expiry;
		this.moduleSize = moduleSize;
		
		init();
	}

	/**
	 * initialize the cache
	 */
	@SuppressWarnings("unchecked")
	private void init() {

		caches = new InnerCache[3];
		for (int i = 0; i < 3; i++) {
			caches[i] = new InnerCache<String, Long>(moduleSize);
		}
		/**
		 * clean job schedule begin to work
		 */
		scheduleService = Executors.newScheduledThreadPool(3);

		scheduleService.scheduleAtFixedRate(new InnerWork(), expiry, expiry,
				TimeUnit.MILLISECONDS);

		cleanCursor = new AtomicInteger(0);
		cursor = new AtomicInteger(0);
		size = new AtomicInteger(0);
		overflowTimes = new AtomicInteger(0);
		ingoreTimes = new AtomicLong(0);
	}

	
	@Override
	public boolean clear() {
		for (InnerCache<String, Long> cache : caches) {
			cache.clear();
		}
		return true;
	}

	@Override
	public boolean containsKey(String key) {
		for (InnerCache<String, Long> cache : caches) {
			if (cache.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy() {
		try {
			clear();

			if (scheduleService != null)
				scheduleService.shutdown();

			scheduleService = null;
		} catch (Exception ex) {
			log.error(ex);
		}
	}

	@Override
	public Long get(String key) {
		Long value = null;
		int num = cursor.get();
		int pre = getPre(num);
		value = caches[num].get(key);
		if (value == null) {
			value = caches[pre].get(key);
		}
		return value;
	}

	@Override
	public Set<String> keySet() {
		Set<String> keys = new HashSet<String>();
		for (InnerCache<String, Long> cache : caches) {
			keys.addAll(cache.keySet());
		}
		return keys;
	}

	@Override
	public Long put(String key, Long value) {
		int num = cursor.get();
		int nowSize = size.get();
		/**
		 * if the cache is going to be filled and there is not any clean job on working ,a real-time clean job will begin to work
		 */
		if ( nowSize>= (max_size * 0.95) && !isOnClean.get()) {
			isOnClean.set(true);
			InnerWork clean = new InnerWork();
			clean.setRealTime(true);
			scheduleService.submit(clean);
			overflowTimes.incrementAndGet();
			if (isLogOn) {
				log.warn("overflow happend ,clean the cache,count time:"
						+ overflowTimes.get());
			}
		}
		if(nowSize<=max_size){
			caches[num].put(key, value);
			size.incrementAndGet();
			return value;
		}else{
			ingoreTimes.incrementAndGet();
			if (isLogOn) {
				log.error("ingore happend ,count time:"
						+ ingoreTimes.get());
			}
			return null;
		}
		
	}

	@Override
	@Deprecated
	/**
	 * this parameter " expiry" should never come into force,
	 * It will use the default value  
	 */
	public Long put(String key, Long value, Date expiry) {
		put(key, value);
		return value;
	}

	@Override
	@Deprecated
	/**
	 * this parameter "TTL" should never come into force,
	 * It will use the default value  
	 */
	public Long put(String key, Long value, int TTL) {
		put(key, value);
		return value;
	}

	@Override
	public Long remove(String key) {
		Long value = null;
		for (InnerCache<String, Long> cache : caches) {
			value = cache.remove(key);
			if (value != null) {
				return value;
			}
		}
		return value;
	}

	@Override
	public int size() {
		return size.get();
	}

	@Override
	public Collection<Long> values() {
		List<Long> values = new ArrayList<Long>();
		for (InnerCache<String, Long> cache : caches) {
			values.addAll(cache.values());
		}
		return values;

	}

	private int getNext(int num) {
		num++;
		if (num == caches.length) {
			return 0;
		} else {
			return num;
		}
	}

	private int getPre(int num) {
		if (num == 0) {
			num = caches.length;
		}
		return --num;

	}

	/**
	 * clean job
	 * 
	 * @author zhudi
	 * 
	 */
	public class InnerWork implements Runnable {

		/**
		 * if the real_time is false,this job will do as a scheduled job and if
		 * the real_time is true ,this job will do the clean work preferentially
		 */
		private boolean realTime = false;

		public void setRealTime(boolean realTime) {
			this.realTime = realTime;
		}

		@Override
		public void run() {
			try {
				if (!realTime) {
					cursor.set(getNext(cursor.get()));
					if (log.isDebugEnabled()) {
						log.debug("schedule job begin at "
								+ System.currentTimeMillis());

					}
					if (getNext(cursor.get()) != cleanCursor.get()) {
						if (log.isDebugEnabled()) {
							log.debug("schedule job return");
						}
						return;
					}
				}
				isOnClean.set(true);
				if (log.isDebugEnabled()) {
					log.debug("job clean begin at "
							+ System.currentTimeMillis());
					log.debug("clean job cursor " + cleanCursor.get());
					log.debug("cursor " + cursor.get());
				}
				/**
				 * count the object size that this job will to clean
				 */
				int cleanSize = caches[cleanCursor.get()].size();
				size.addAndGet(-cleanSize);
				if (log.isDebugEnabled()) {
					log.debug("job clean size " + cleanSize);
					log.debug("cache size " + size.get());
				}
				caches[cleanCursor.get()].clear();
				/**
				 * the clean cursor go to next
				 */
				cleanCursor.set(getNext(cleanCursor.get()));
				isOnClean.set(false);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	/**
	 * inner cache
	 * 
	 * @author zhudi
	 * 
	 * @param <K>
	 * @param <V>
	 */
	public class InnerCache<K, V> {

		private Map<K, V>[] caches;
		private int moduleSize = 10;

		public InnerCache(int moduleSize) {
			this.moduleSize = moduleSize;
			init();
		}

		public InnerCache() {
			init();
		}

		@SuppressWarnings("unchecked")
		public void init() {
			caches = new ConcurrentHashMap[moduleSize];
			for (int i = 0; i < moduleSize; i++) {
				caches[i] = new ConcurrentHashMap<K, V>();
			}
		}

		public void put(K key, V value) {

			int moudleNum = getLocationCache(key);

			caches[moudleNum].put(key, value);

		}

		private int getLocationCache(K key) {
			long hashCode = (long) key.hashCode();

			if (hashCode < 0)
				hashCode = -hashCode;

			int moudleNum = (int) hashCode % moduleSize;
			return moudleNum;
		}

		public void clear() {
			for (int i = 0; i < moduleSize; i++) {
				caches[i].clear();
			}
		}

		public V get(K key) {
			int moudleNum = getLocationCache(key);
			return caches[moudleNum].get(key);
		}

		public boolean containsKey(K key) {
			int moudleNum = getLocationCache(key);
			return caches[moudleNum].containsKey(key);
		}

		public int size() {
			int size = 0;
			for (int i = 0; i < moduleSize; i++) {
				size += caches[i].size();
			}
			return size;
		}

		public Set<K> keySet() {
			Set<K> keys = new HashSet<K>();
			for (Map<K, V> cache : caches) {
				keys.addAll(cache.keySet());
			}
			return keys;
		}

		public Collection<V> values() {

			List<V> values = new ArrayList<V>();
			for (Map<K, V> cache : caches) {
				values.addAll(cache.values());
			}
			return values;

		}

		public V remove(K key) {
			V value = null;
			for (Map<K, V> cache : caches) {
				value = cache.remove(key);
				if (value != null) {
					return value;
				}
			}
			return value;
		}
	}

}
