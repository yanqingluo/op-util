package mock;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.taobao.common.tfs.TfsManager;

public class TfsManagerMock implements TfsManager {

	public boolean fetchFile(String tfsFileName, String tfsPrefix,
			String localFileName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean fetchFile(String tfsFileName, String tfsPrefix,
			OutputStream output) {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
		try{
			for(int i = 0; i < 10; i++){
				writer.write("request" + i);
				writer.newLine();
				writer.write("response" + i);
				writer.newLine();
			}
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public String getMasterIP() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hideFile(String fileName, String tfsPrefix, int option) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	public String newTfsFileName(String prefix) {
		return prefix;
	}

	public String saveFile(String localFileName, String tfsFileName,
			String tfsPrefix) {
		return localFileName;
	}

	public String saveUniqueFile(String localFileName, String tfsFileName,
			String tfsPrefix) {
		// TODO Auto-generated method stub
		return null;
	}

	public int setMasterIP(String ipaddr) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean unlinkFile(String tfsFileName, String tfsPrefix) {
		// TODO Auto-generated method stub
		return false;
	}

	public int unlinkUniqueFile(String tfsFileName, String tfsPrefix) {
		// TODO Auto-generated method stub
		return 0;
	}

}
