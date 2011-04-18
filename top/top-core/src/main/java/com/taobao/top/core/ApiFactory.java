/**
 * taobao.com 2008 copyright
 */
package com.taobao.top.core;

/**
 * @version 2008-2-29
 * @author <a href="mailto:zixue@taobao.com">zixue</a>
 * 
 */
public interface ApiFactory {

	Api getApi(String method) throws ApiConfigException;
}
