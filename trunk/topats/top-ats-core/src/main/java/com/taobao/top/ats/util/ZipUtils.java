package com.taobao.top.ats.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 压缩工具类。
 * 
 * @author carver.gu
 * @since 1.0, Nov 23, 2010
 */
public abstract class ZipUtils {

	public static void zipDir(String zipName, File srcDir, File destDir) throws IOException {
		OutputStream os = null;
		ZipOutputStream zos = null;
		try {
			os = new FileOutputStream(new File(destDir, zipName));
			zos = new ZipOutputStream(os);
			zipFile(zos, srcDir, null);
		} finally {
			IOUtils.closeQuietly(zos);
			IOUtils.closeQuietly(os);
		}
	}

	private static void zipFile(ZipOutputStream out, File file, String parent) throws IOException {
		if (file.isDirectory()) {
			File[] fs = file.listFiles();
			for (File f : fs) {
				if (StringUtils.isBlank(parent)) {
					zipFile(out, f, f.getName());
				} else {
					zipFile(out, f, parent + "/" + f.getName());
				}
			}
		} else {
			out.putNextEntry(new ZipEntry(parent));
			InputStream in = null;
			try {
				in = new FileInputStream(file);
				IOUtils.copy(in, out);
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		String zipName = "gfs.zip";
		File srcDir = new File("E:/Working/test");
		File destDir = new File("E:/Working/");
		zipDir(zipName, srcDir, destDir);
	}

}
