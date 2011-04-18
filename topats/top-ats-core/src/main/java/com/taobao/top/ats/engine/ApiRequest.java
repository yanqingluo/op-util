package com.taobao.top.ats.engine;

import java.util.Map;

public class ApiRequest {

	private String interfaceName;
	private String interfaceMethod;
	private String interfaceVersion;
	private Map<String, String> parameters;

	public String getInterfaceName() {
		return this.interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getInterfaceMethod() {
		return this.interfaceMethod;
	}
	public void setInterfaceMethod(String interfaceMethod) {
		this.interfaceMethod = interfaceMethod;
	}
	public String getInterfaceVersion() {
		return this.interfaceVersion;
	}
	public void setInterfaceVersion(String interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
	}
	public Map<String, String> getParameters() {
		return this.parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

}
