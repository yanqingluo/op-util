package com.taobao.top.impl.core.export;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.taobao.top.json.JsonConfig;
import com.taobao.top.json.processors.JsonValueProcessor;

/**
 * 
 * 对json DATE类型特别处理
 * 
 * @author liupo <liupo@taobao.com>
 * @version 1.0
 *
 */
public class JsonValueProcessorImpl implements JsonValueProcessor{
	 private String format="yyyy-MM-dd HH:mm:ss";
	 public JsonValueProcessorImpl(){
	  
	 }
	 public JsonValueProcessorImpl(String format){
	  this.format=format;
	 }
	 public Object processArrayValue(Object value, JsonConfig jsonConfig) {
	  String[] obj={};
	  if(value instanceof Date[] && value!=null){
	   SimpleDateFormat sf=new SimpleDateFormat(format);
	   Date[] dates=(Date[])value;
	   obj =new String[dates.length];
	   for (int i = 0; i < dates.length; i++) {
	    obj[i]=sf.format(dates[i]);
	   }
	  }
	  return obj;
	 }

	 public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
	  if(value instanceof Date && value!=null){
	   String str=new SimpleDateFormat(format).format((Date)value);
	   return str;
	  }
	  return null;
	 }

	 public String getFormat() {
	  return format;
	 }

	 public void setFormat(String format) {
	  this.format = format;
	 }
	 
	} 



