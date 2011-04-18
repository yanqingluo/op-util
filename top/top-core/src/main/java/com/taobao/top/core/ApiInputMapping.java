package com.taobao.top.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.top.core.ApiApplicationParameter.Type;
import com.taobao.top.core.framework.TopPipeInput;
import com.taobao.top.traffic.mapping.MemberMapping;
import com.taobao.util.CollectionUtil;

/**
 * 传入ApiInput和ParameterMapping，得到该参数的传输对象
 * 
 * @version 2009-3-9
 * @author <a href="mailto:xalinx@gmail.com">alin</a>
 * 
 */
public class ApiInputMapping {
	public static Object mapping(TopPipeInput pipeInput, MemberMapping<?> param) {
		Object value = null;
		if (param.getName() == null) {
			Collection<MemberMapping<?>> members = param.getMapping()
					.getMemberMappings().values();
			Map<String, Object> apiMap = new HashMap<String, Object>(members
					.size());
			for (MemberMapping<?> mm : members) {
				apiMap.put(mm.getName(), singleMapping(pipeInput, mm.getName(), mm
						.getMappingType()));
			}
			value = apiMap;
		}
		// 存在和api参数对应的
		else {
			value = singleMapping(pipeInput, param.getName(), param
					.getMappingType());
		}
		return value;
	}

	private static Object singleMapping(TopPipeInput input, String apiParamName,
			String remoteType) {
		Object value = null;
		// XXX session_nick和session_id,app_key,api_key需要特殊处理 zixue
		if (ProtocolConstants.P_SESSION_NICK.equals(apiParamName)) {
			value = input.getSessionNick();
		} else if (ProtocolConstants.P_SESSION_UID.equals(apiParamName)) {
			value = input.getSessionUid();
		} else if (ProtocolConstants.P_API_KEY.equals(apiParamName)
				|| ProtocolConstants.P_APP_KEY.equals(apiParamName)) {
			value = input.getAppKey();
		} else {
			// TODO byte[]数组目前表示图片，一个api只有一张图片，先写死 zixue
			if (byte[].class.getName().equals(remoteType)) {
				com.taobao.top.core.framework.FileItem fileItem = input.getFileData();
				if(fileItem != null){
					value = fileItem.getBout().toByteArray();
				}
			} else {
				value = input.getString(apiParamName, false); // to 1.0 api, parameters do not trim
			}
		}
		return value;
	}
	
	private static Object getValue(TopPipeInput input, ApiApplicationParameter parameter) {
		if(parameter.getType() == Type.BYTE_ARRAY) {
			byte[] fileData = null;
			com.taobao.top.core.framework.FileItem fileItem = input.getFileData();
			if(fileItem != null){
				fileData = fileItem.getBout().toByteArray();
			}
			return fileData;
		} else {
			return input.getString(parameter.getName(), parameter.isNeedTrim());
		}
	}
	public static Map<String, Object> getHsfMappingParam(TopPipeInput input, Api api) {
		List<ApiApplicationParameter> applicationMustParams = api
				.getApplicationMustParams();
		List<ApiApplicationParameter> applicationOptionalParams = api
				.getApplicationOptionalParams();
		List<ApiApplicationParameter> combineMustParams = api
				.getApplicationCombineParams();
		// 发送到服务方的数据
		HashMap<String, Object> map = new HashMap<String, Object>();
		// put optional parameters
		if (CollectionUtil.isNotEmpty(applicationOptionalParams)) {
			for (ApiApplicationParameter optionalParam : applicationOptionalParams) {
				String key = optionalParam.getName();
				Object value = getValue(input, optionalParam);
				map.put(key, value);
			}
		}
		// put combine must parameters
		if (CollectionUtil.isNotEmpty(combineMustParams)) {
			for (ApiApplicationParameter combineParam : combineMustParams) {
				String key = combineParam.getName();
				Object value = getValue(input, combineParam);
				map.put(key, value);
			}
		}
		// put must parameters
		if (CollectionUtil.isNotEmpty(applicationMustParams)) {
			for (ApiApplicationParameter mustParam : applicationMustParams) {
				String key = mustParam.getName();
				Object value = getValue(input, mustParam);
				map.put(key, value);
			}
		}
		// put system optional
		map.put(ProtocolConstants.P_FORMAT, input.getFormat());
		map.put(ProtocolConstants.P_STYLE, input.getString(ProtocolConstants.P_STYLE, true));
		
		// put system inner parameters
		map.put(ProtocolConstants.P_SESSION_UID, input.getSessionUid());
		map.put(ProtocolConstants.P_SESSION_NICK, input.getSessionNick());
		map.put(ProtocolConstants.P_APP_IP, input.getAppIp());
		map.put(ProtocolConstants.P_ENDUSER_IP, input.getEndUserIp());
		
		map.put(ProtocolConstants.P_TOP_TAG, input.getTag());
		map.put(ProtocolConstants.P_TOP_BIND_NICK, input.getBindNick());
		map.put(ProtocolConstants.P_TOP_ISV_ID, input.getIsvId());
		
		// put system default must
		map.put(ProtocolConstants.P_METHOD, input.getApiName());
		map.put(ProtocolConstants.P_VERSION, input.getVersion());
		map.put(ProtocolConstants.P_APP_KEY, input.getAppKey());
		map.put(ProtocolConstants.P_TIMESTAMP, input.getTimeStamp());

		return map;
	}
}
