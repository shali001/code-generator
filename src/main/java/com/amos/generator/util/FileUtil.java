package com.amos.generator.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	public static List<File> getAllFilePath(String rootPath,FileFilter fileFilter) {

		File file = new File(rootPath);
		File[] files = file.listFiles(fileFilter);

		List<File> fileList = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				getAllFilePath(files[i].getPath(),fileFilter);
			} else {
				fileList.add(files[i]);
			}
		}
		return fileList;
	}

	public static byte[] file2byte(File file) {
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}
}
