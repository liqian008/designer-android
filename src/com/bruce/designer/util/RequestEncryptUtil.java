package com.bruce.designer.util;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.commons.httpclient.methods.multipart.PartSource;
/**
 * 
 * @author liqian
 *
 */
public class RequestEncryptUtil {

	/**
     * 根据反馈回来的信息，生成签名结果
     * @param Params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @return 生成的签名结果
	 * @throws NoSuchAlgorithmException 
     */
	public static String getSign(Map<String, String> requestMap, String secret) throws NoSuchAlgorithmException {
    	//过滤空值、sign与sign_type参数
    	Map<String, String> sParaNew = paramFilter(requestMap);
        //获取待签名字符串
        String preSignStr = createLinkString(sParaNew);
        preSignStr = preSignStr + secret;
        //获得签名验证结果
        String mySign = MD5.toMD5(preSignStr);
        return mySign;
    }
	
	
	/**
	 * 除去数组中的空值和签名参数
	 * @param paramMap 签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	private static Map<String, String> paramFilter(Map<String, String> paramMap) {

		Map<String, String> result = new HashMap<String, String>();
		if (paramMap == null || paramMap.size() <= 0) {
			return result;
		}

		for (String key : paramMap.keySet()) {
			String value = paramMap.get(key);
			if (value == null || value.equals("")
					|| key.equalsIgnoreCase("sign")) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * @param params 需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	private static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}

	
//	/**
//	 * 
//	 * @param content
//	 * @param charset
//	 * @return
//	 */
//    private static byte[] getContentBytes(String content, String charset) {
//        if (charset == null || "".equals(charset)) {
//            return content.getBytes();
//        }
//        try {
//            return content.getBytes(charset);
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
//        }
//    }
}
