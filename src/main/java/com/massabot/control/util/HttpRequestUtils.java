/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 2017年3月12日 下午7:46:04
 * @version $Id$
 * @author JiangJibo
 *
 */
public class HttpRequestUtils {

	final static Logger LOGGER = LoggerFactory.getLogger(HttpRequestUtils.class);

	public static final String URL_PREFIX = "http://";

	public static final String URL_SUFFIX = ":8080";

	public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	private static final Map<String, String> HEADER_CACHE = new HashMap<String, String>(16);

	/**
	 * 根据IP地址组装成完整的请求路径
	 * 
	 * @param hostAdress
	 * @return
	 */
	public static String createRequestUrl(String hostAdress, String webRoot, String controllerPath) {
		return HttpRequestUtils.URL_PREFIX + hostAdress + HttpRequestUtils.URL_SUFFIX + webRoot + controllerPath;
	}

	public static String getHostAdress(String requestUrl) {
		return requestUrl.subSequence(requestUrl.indexOf("//") + 2, requestUrl.lastIndexOf(":")).toString();
	}

	/**
	 * 向指定URL以指定方式发送Http请求
	 * 
	 * @param precondition
	 *            是否发送请求的前置校验
	 * @param url
	 *            请求路径
	 * @param method
	 *            请求方式
	 * @param timeout
	 *            超时时间,单位为毫秒
	 * @return
	 * @throws Exception
	 */
	public static String doRequestWithThread(final String url, final RequestMethod method, int timeout) throws Exception {
		Callable<String> task = new Callable<String>() {

			public String call() throws Exception {
				// 用HttpClient发送请求，分为五步
				// 第一步：创建HttpClient对象
				HttpClient httpClient = HttpClientBuilder.create().build();
				// 第二步：创建代表请求的对象,参数是访问的服务器地址
				HttpUriRequest request = getRequestByMethod(method, url);
				addHeaderForRequest(request);
				try {
					// 第三步：执行请求，获取服务器发还的相应对象
					HttpResponse httpResponse = httpClient.execute(request);
					// 第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// 第五步：从相应对象当中取出数据，放到entity当中
						HttpEntity entity = httpResponse.getEntity();
						return EntityUtils.toString(entity, "utf-8");// 将entity当中的数据转换为字符串
					}
				} catch (Exception e) {
					LOGGER.error("", e);
				}
				return null;
			}
		};
		return EXECUTOR.submit(task).get(timeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * 不另起线程地调用HttpClient
	 * 
	 * @param precondition
	 * @param url
	 * @param method
	 * @param timeout
	 * @return
	 */
	public static String doRequest(String url, RequestMethod method, int timeout) {
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
		String response = null;
		try {
			HttpUriRequest request = HttpRequestUtils.getRequestByMethod(method, url);
			HttpResponse httpResponse = httpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = httpResponse.getEntity();
				response = EntityUtils.toString(entity, "utf-8");
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return response;
	}

	/**
	 * 添加header数据
	 * 
	 * @param request
	 */
	private static void addHeaderForRequest(HttpUriRequest request) {
		for (Entry<String, String> entry : HEADER_CACHE.entrySet()) {
			request.addHeader(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 指定Http请求的方式
	 * 
	 * @param method
	 * @param url
	 * @return
	 */
	public static HttpUriRequest getRequestByMethod(RequestMethod method, String url) {
		switch (method) {
		case GET:
			return new HttpGet(url);
		case POST:
			return new HttpPost(url);
		case PUT:
			return new HttpPut(url);
		case DELETE:
			return new HttpDelete(url);
		default:
			throw new IllegalStateException("非法请求方式");
		}
	}

	/**
	 * 給Request的Header添加数据
	 * 
	 * @param name
	 * @param value
	 * @return false:内部原有的header被覆盖; true:新增的header
	 */
	public static boolean cacheHeader(String name, String value) {
		return HEADER_CACHE.put(name, value) != null;
	}

	/**
	 * 是否存在指定的header属性
	 * 
	 * @param name
	 * @return
	 */
	public static boolean conttainHeaderAttribute(String name) {
		return HEADER_CACHE.containsKey(name);
	}

	/**
	 * 删除Header数据
	 * 
	 * @param name
	 * @return false:不存在指定的header;true:删除成功
	 */
	public static boolean removeHeader(String name) {
		return HEADER_CACHE.remove(name) != null;
	}

	/**
	 * 清空header缓存
	 */
	public static void clearHeaderCache() {
		HEADER_CACHE.clear();
	}

	/**
	 * Http请求枚举
	 * 
	 * @since 2017年3月9日 上午9:48:48
	 * @version $Id$
	 * @author JiangJibo
	 *
	 */
	public enum RequestMethod {

		GET, POST, PUT, DELETE;

	}

}
