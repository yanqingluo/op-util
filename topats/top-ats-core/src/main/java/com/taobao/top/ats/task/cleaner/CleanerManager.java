package com.taobao.top.ats.task.cleaner;

/**
 * 临时文件清理器。
 * 
 * @author xuyingjie.pt
 */
public interface CleanerManager {
	/**
	 * 清除ATS服务器本地临时文件
	 */
	public void cleanLocalFile();

	/**
	 * 判断应用机器 是否可以启动守护进程（目的是尽量保证只有一台机器 运行清理任务）
	 */
	public boolean isCleanerAvailable();

	/**
	 * 清除DB,TFS和资源服务器上的历史任务数据，另外定时刷新API模板也在这里完成 为了保证只被一台ATS服务器调用，因此需要做是否已启动的校验
	 */
	public void cleanSharedData();

}
