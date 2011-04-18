package mock;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.taobao.common.tair.DataEntry;
import com.taobao.common.tair.Result;
import com.taobao.common.tair.ResultCode;
import com.taobao.common.tair.TairManager;
import com.taobao.top.ats.util.KeyConstants;

public class TairManagerMock implements TairManager {

	public Result<Integer> decr(int namespace, Object key, int value, int defaultValue) {
		return null;
	}

	public ResultCode delete(int namespace, Object key) {
		return null;
	}

	public Result<DataEntry> get(int namespace, Object key) {
		InetAddress inetAddress;
		Result<DataEntry> result = null;
		DataEntry dataEntry = null;

		if (KeyConstants.CLEANER_TAIR_FLAG_KEY.equals(key)) {
			try {

				inetAddress = InetAddress.getLocalHost();
				String localIp = inetAddress.getHostAddress();
				dataEntry = new DataEntry(localIp);
				ResultCode resultCode = new ResultCode(ResultCode.SUCCESS.getCode());
				result = new Result<DataEntry>(resultCode, dataEntry);

			} catch (UnknownHostException e) {

				e.printStackTrace();
			}
		} else {
			ResultCode resultCode = new ResultCode(ResultCode.DATANOTEXSITS.getCode());
			result = new Result<DataEntry>(resultCode, dataEntry);
		}
		return result;
	}

	public String getVersion() {
		return null;
	}

	public Result<Integer> incr(int namespace, Object key, int value, int defaultValue) {
		return null;
	}

	public ResultCode invalid(int namespace, Object key) {
		return null;
	}

	public ResultCode mdelete(int namespace, List<Object> keys) {
		return null;
	}

	public Result<List<DataEntry>> mget(int namespace, List<Object> keys) {
		return null;
	}

	public ResultCode minvalid(int namespace, List<? extends Object> keys) {
		return null;
	}

	public ResultCode put(int namespace, Object key, Serializable value) {
		return null;
	}

	public ResultCode put(int namespace, Object key, Serializable value, int version) {

		ResultCode resultCode = new ResultCode(ResultCode.SUCCESS.getCode());
		return resultCode;

	}

	public ResultCode put(int namespace, Object key, Serializable value, int version, int expireTime) {
		return null;
	}

}
