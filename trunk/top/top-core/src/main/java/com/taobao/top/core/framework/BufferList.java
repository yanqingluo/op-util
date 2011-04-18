package com.taobao.top.core.framework;

import java.io.IOException;

/**
 * 特殊的ByteBuffer,内部是一个ByteBuffer列表
 * 线程不安全
 * @author fangweng(fangweng@taobao.com)
 * @email fangweng@taobao.com
 *
 */
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * 用于lazy load的缓冲对象
 * @author fangweng
 *
 */
public class BufferList 
{
	private byte[] buffers;
	private int limit = 0;
	private int position = 0;
	
	/**
	 * 获取BufferList内部字节总数
	 * @return
	 */
	public int getLength() {
		return limit - position;
	}

	/**
	 * 获取缓存总的内容
	 * @return
	 * @throws IOException
	 */
	public byte[] get() throws IOException
	{	
		return get(0,limit-position);
	}
	
	/**
	 * 获取从start开始的缓存内容，例如start=5，则返回从第六个位置开始
	 * @param start
	 * @return
	 * @throws IOException
	 */
	public byte[] get(int start) throws IOException
	{
		return get(start,limit - position - start);
	}
	
	/**
	 * 获取从start开始的缓存内容，例如start=5，则返回从第六个位置开始，长度为len
	 * @param start
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public byte[] get(int start,int len) throws IOException
	{
		if (buffers == null || limit - position == 0 
				||start < 0 || len <= 0)
			return null;
		
		byte[] buffer = new byte[len];
		System.arraycopy(buffers,start + position ,buffer,0,len);
		return buffer;
	}
	
	public String get(String encoding) throws UnsupportedEncodingException
	{
		return get(0,limit-position,encoding);
	}
	
	public String get(int start,int len,String encoding) throws UnsupportedEncodingException
	{
		if (buffers == null || limit - position == 0 
				||start < 0 || len <= 0)
			return null;
		
		String result = null;
		
		result = new String(buffers,start + position,len,encoding);
		
		return result;
	}
	
	
	public int add(InputStream in,int maxBufferSize) throws IOException
	{
		int count = 0;
		
		if (buffers == null || limit == position)
		{
			buffers = new byte[maxBufferSize];
			position = 0;
			
			count = limit = in.read(buffers);
			
		}
		else
		{
			byte[] newBuffer = new byte[limit - position + maxBufferSize];
			System.arraycopy(buffers,position,newBuffer,0,limit - position);
			count = in.read(newBuffer,limit - position,maxBufferSize);
			buffers = newBuffer;
			if(count == -1){
				limit = limit - position;
			}else{
				limit = limit - position + count;
			}
			position = 0;
			
		}
		
		return count;
	}
	
	/**
	 * 移除指定的缓存内容
	 * @param start
	 * @param length
	 */
	public void remove(int len)
	{
		//XXX 原来的
//		if (len > limit - position)
//			return;
		//XXX 新的
		if(len > limit - position)
		{
			position += (limit - position);
			return;
		}
		if (len == limit - position)
		{
			buffers = null;
			limit = 0;
			position = 0;
			return;
		}
		
		position += len;
	}
	
	/**
	 * 查找指定内容在缓存中的位置
	 * @param search
	 * @return
	 * @throws IOException
	 */
	public int indexOf(byte[] search) throws IOException
	{
		return indexOf(search,0);
	}
	
	/**
	 * 查找指定内容从start开始的缓存中的位置
	 * @param search
	 * @param start
	 * @return
	 * @throws IOException
	 */
	public int indexOf(byte[] search,int start) throws IOException
	{
		return searchFromBinaryBuffer(start,search);
	}
	
	/**
	 * 搜索位置
	 * @param buffer
	 * @param start
	 * @param search
	 * @return
	 * @throws IOException 
	 */
	private int searchFromBinaryBuffer(int start,byte[] search) throws IOException
	{
		int index = -1;
		
		if (buffers == null || limit - position <= 0 || search == null 
				|| (limit - position - start < search.length ))
			return index;
		
		int _len = limit;
		int _searchLen = search.length;
		
		for(int i = start + position ; i < _len; i++)
		{
			if (_len - i < _searchLen)
				break;
				
			if (buffers[i] != search[0])
				continue;
			
			boolean isFind = true;
			
			for(int j = 1 ; j < search.length; j++)
			{
				if (buffers[i+j] != search[j])
				{
					isFind = false;
					break;
				}
			}
			
			if (isFind)
			{
				index = i;
				break;
			}
		}
		
		return index - position;
	}	
	
	/**
	 * 清除缓存
	 */
	public void clean()
	{
		buffers = null;
		limit = 0;
		position = 0;
	}
}