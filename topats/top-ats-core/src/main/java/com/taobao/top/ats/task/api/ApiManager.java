package com.taobao.top.ats.task.api;

import java.util.Date;
import java.util.Map;

import com.taobao.top.ats.AtsException;
import com.taobao.top.ats.domain.AtsTaskDO;
import com.taobao.top.ats.domain.ResultSet;

public interface ApiManager {
	/**
	 * 初始化任务模板
	 */
	public void init() throws Exception;
	
	/**
	 * 判断一个请求是否符合入参模板定义
	 */
	public ResultSet isRequestRight(Map<String, String> request);
	
	/**
	 * 前置调用后端服务校验是否正确
	 */
	public ResultSet isPreCheckOK(Map<String, String> request);
	
	/**
	 * 判断一个task的子task是否是可忽略的错误
	 */
	public boolean isIgnoreError(String taskName, String errorCode) throws AtsException;
	
	/**
	 * 判断一个task的子task是否是需重试的错误
	 */
	public boolean isRetryError(String taskName, String errorCode) throws AtsException;
	
	/**
	 * 获取一个子任务最重返回给用户的请求串
	 */
	public String getRetrunRequest(String taskName, Map<String, String> subTaskRequest) throws AtsException;
	
	/**
	 * 获取一个忽略错误的情况下返回给用户的响应串
	 */
	public String getIngoreErrorResponse(String taskName, Map<String, String> subTaskRequest) throws AtsException;
	
	/**
	 * 获取调用错误的情况下返回给用户的响应串
	 */
	public String getErrorResponse(String errorCode, String errorMsg, Map<String, String> subTaskRequest);
	
	/**
	 * 根据入参和模板解析得到任务
	 */
	public AtsTaskDO getTask(Map<String, String> request) throws AtsException;
	
	/**
	 * 刷新api模板缓存
	 * @return
	 */
	public boolean refreshApiModels();
	
	/**
	 * 判断一个任务是否需要子任务再分页处理
	 * @param taskName
	 * @return
	 */
	public boolean isPageNeeded(String taskName) throws AtsException;
	
	/**
	 * 判断一个任务是否大任务api，根据api模板判断
	 * @param taskName
	 * @return
	 */
	public boolean isBigResult(String taskName) throws AtsException;
	
	/**
	 * 判断一个已存在的任务是否大任务api，根据attributes里面的标记判断（可能存在数据兼容性问题）
	 * @param attributes
	 * @return
	 */
	public boolean isBigResult(Map<String, String> attributes);

	/**
	 * 获取要翻页类型的子任务的下一次请求内容
	 * @param taskName
	 * @param subTaskRequest 这个需要是上一次翻页请求的内容。如果是第一次执行，直接将数据库中的如此传入即可
	 * @param totalResult
	 * @return 如果没有下一次翻页或不支持翻页，返回为null
	 */
	public Map<String, String> getPageDownRequest(String taskName, Map<String, String> subTaskRequest, Long totalResult) throws AtsException;
	
	/**
	 * 判断某个api在某个时间是否可以执行，只对优先级为2的api进行判断。优先级为0和1的永远为true。对于不存在的api永远是false
	 * @param taskName
	 * @param date
	 * @return
	 */
	public boolean isApiIntime(String taskName, Date date);
}
