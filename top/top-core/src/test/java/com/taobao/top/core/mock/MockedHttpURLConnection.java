package com.taobao.top.core.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author haishi
 *
 */
public class MockedHttpURLConnection extends HttpURLConnection {

	private InputStream inputStream;
	private OutputStream outputStream;


	public MockedHttpURLConnection(URL url) {
		super(url);
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	boolean disconnected;
	@Override
	public void disconnect() {
		disconnected = true;
	}

	@Override
	public boolean usingProxy() {
		return false;
	}

	@Override
	public void connect() throws IOException {
		// TODO Auto-generated method stub
		
	}
	public InputStream getInputStream() throws IOException {
		return inputStream;
		
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return outputStream;
	}

	public boolean isDisconnected() {
		return disconnected;
	}


}
