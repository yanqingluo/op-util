package com.taobao.top.ats.service.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.taobao.common.tfs.TfsManager;

public class TfsManagerMock implements TfsManager {

	public boolean fetchFile(String tfsFileName, String tfsPrefix, String localFileName) {
		return false;
	}

	public boolean fetchFile(String tfsFileName, String tfsPrefix, OutputStream output) {
		InputStream input = TfsManagerMock.class.getResourceAsStream("/TaskResult.txt");
		try {
			IOUtils.copy(input, output);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public String getMasterIP() {
		return null;
	}

	public boolean hideFile(String fileName, String tfsPrefix, int option) {
		return false;
	}

	public boolean isEnable() {
		return false;
	}

	public String newTfsFileName(String prefix) {
		return null;
	}

	public String saveFile(String localFileName, String tfsFileName, String tfsPrefix) {
		return null;
	}

	public String saveUniqueFile(String localFileName, String tfsFileName, String tfsPrefix) {
		return null;
	}

	public int setMasterIP(String ipaddr) {
		return 0;
	}

	public boolean unlinkFile(String tfsFileName, String tfsPrefix) {
		return false;
	}

	public int unlinkUniqueFile(String tfsFileName, String tfsPrefix) {
		return 0;
	}

}
