package com.taobao.top.log.asyn;

/**
 * 异步输出接口
 * @author wenchu
 *
 */
public interface IAsynWriter <T>
{
	public void start();
	
	public void stop();
	
	public void restart();
	
	public void write(T content);
}
