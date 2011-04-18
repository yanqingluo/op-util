package com.taobao.top.privilege;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Default whiteList manager fixedList implementation.
 * @author moling
 * @since 1.0, 2009-9-2
 */
public class WhiteListManagerImpl implements WhiteListManager {
	private List<String> whiteAppList;
	private List<String> whiteIpList;
	
	//切割ip后的白名单
	private List<String[]> whiteIps;
	
	public void setWhiteAppList(List<String> whiteAppList) {
		this.whiteAppList = whiteAppList;
	}

	public void setWhiteIpList(List<String> whiteIpList) {
		this.whiteIpList = whiteIpList;
		
		//在注入时就将白名单切割开
		if (whiteIpList != null && whiteIpList.size() > 0) {
			whiteIps = new ArrayList<String[]>();
			
			for (String ips : whiteIpList) {
				//把多个ip拆开加入ip列表中
				if (StringUtils.isNotEmpty(ips)) {
					String[] whites = StringUtils.split(ips, ",");
					if (null != whites && whites.length > 0) {
						for (int i = 0; i < whites.length; ++i) {
							//把每个ip按“.”拆开，得到一个四位的数组
							String[] ipParse = StringUtils.split(whites[i], ".");
							whiteIps.add(ipParse);
						}
					}
				}
				
			}
		}
	}

	public boolean isAppWhite(String appKey) {
		//如果白名单为空或app为空，必定此app不在白名单内
		if (null == whiteAppList || whiteAppList.size() == 0 || StringUtils.isEmpty(appKey)) {
			return false;
		}
		
		for (String app : whiteAppList) {
			if (appKey.equals(app)) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isIpWhite(String ip) {
		//如果白名单为空或ip为空，必定此ip不在白名单内
		if (null == whiteIps || whiteIps.size() == 0 || StringUtils.isEmpty(ip)) {
			return false;
		}
		
		//将ip以“.”进行拆分
		String[] requestParse = StringUtils.split(ip, ".");
		
		for (String[] whiteIp : whiteIps) {
			if (ipsEqual(requestParse, whiteIp)) {
				return true;
			}
		}
		
		return false;
	}
	
	//判断两个ip是否相等
	private boolean ipsEqual(String[] requestIp, String[] whiteIp) {
		boolean equal = false;
		
		//判断白名单ip是否在列表中必须要两个ip都不为空进行比较
		if (requestIp != null && whiteIp != null && requestIp.length == whiteIp.length) {			
			if (requestIp[0].equals(whiteIp[0])
					&& requestIp[1].equals(whiteIp[1])
					&& ("*".equals(whiteIp[2]) || requestIp[2]
							.equals(whiteIp[2]))
					&& ("*".equals(whiteIp[3]) || requestIp[3]
							.equals(whiteIp[3]))) {
				equal = true;
			}
		}
		
		return equal;
	}
	
}
