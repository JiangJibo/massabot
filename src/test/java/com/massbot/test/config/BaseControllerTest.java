package com.massbot.test.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.massabot.config.mvc.AppMvcConfig;
import com.massabot.config.root.AppContextConfig;

/**
 * @since 2016年12月8日 下午4:45:26
 * @version $Id$
 * @author JiangJibo
 *
 */
@WebAppConfiguration
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppContextConfig.class, AppMvcConfig.class })
public abstract class BaseControllerTest {

	protected Gson gson;

	private MockMvc mockMvc;

	protected String userName;

	protected String password;

	protected boolean loginBefore = false;

	@Autowired
	protected WebApplicationContext webApplicationContext;

	@Before()
	public void setup() {
		gson = BeanUtils.instantiate(Gson.class);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); // 初始化MockMvc对象
		init();
	}

	/**
	 * @param urlTemplate
	 * @param urlVariables
	 * @return
	 */
	public String getRequest(String urlTemplate, Object... urlVariables) {
		return doRequest(RequestMethod.GET, null, null, urlTemplate, urlVariables);
	}

	/**
	 * @param contentType
	 * @param content
	 * @param urlTemplate
	 * @param urlVariables
	 * @return
	 */
	public String postRequest(MediaType contentType, String content, String urlTemplate, Object... urlVariables) {
		return doRequest(RequestMethod.POST, contentType, content, urlTemplate, urlVariables);
	}

	/**
	 * @param content
	 * @param urlTemplate
	 * @param urlVariables
	 * @return
	 */
	public String postRequest(String content, String urlTemplate, Object... urlVariables) {
		return doRequest(RequestMethod.POST, MediaType.APPLICATION_JSON, content, urlTemplate, urlVariables);
	}

	/**
	 * @param contentType
	 * @param content
	 * @param urlTemplate
	 * @param urlVariables
	 * @return
	 */
	public String putRequest(MediaType contentType, String content, String urlTemplate, Object... urlVariables) {
		return doRequest(RequestMethod.PUT, contentType, content, urlTemplate, urlVariables);
	}

	/**
	 * @param content
	 * @param urlTemplate
	 * @param urlVariables
	 * @return
	 */
	public String putRequest(String content, String urlTemplate, Object... urlVariables) {
		return doRequest(RequestMethod.PUT, MediaType.APPLICATION_JSON, content, urlTemplate, urlVariables);
	}

	/**
	 * @param urlTemplate
	 * @param urlVariables
	 * @return
	 */
	public String deleteRequest(String urlTemplate, Object... urlVariables) {
		return doRequest(RequestMethod.DELETE, null, null, urlTemplate, urlVariables);
	}

	/**
	 * @param filePath
	 * @return
	 */
	public String readJsonFile(String filePath) {
		BufferedReader bufferReader = null;
		try {
			File file = new File(filePath);
			long length = file.length();
			if (length > 5 * 1024 * 1024) {
				throw new IllegalArgumentException("试图读取的文件大小超过5M");
			}
			FileInputStream fileInputStream = new FileInputStream(file);
			return IOUtils.toString(fileInputStream, "UTF-8");
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(filePath, e);
		} finally {
			try {
				if (null != bufferReader) {
					bufferReader.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * @param method
	 * @param contentType
	 * @param content
	 * @param urlTemplate
	 * @param urlVariables
	 * @return
	 */
	protected String doRequest(RequestMethod method, MediaType contentType, String content, String urlTemplate, Object... urlVariables) {
		/*if (loginBefore) {
			login(userName, password);
		}*/
		MockHttpServletRequestBuilder builder;
		if (method == RequestMethod.GET) {
			builder = MockMvcRequestBuilders.get(urlTemplate, urlVariables);
		} else if (method == RequestMethod.POST) {
			builder = MockMvcRequestBuilders.post(urlTemplate, urlVariables);
		} else if (method == RequestMethod.PUT) {
			builder = MockMvcRequestBuilders.put(urlTemplate, urlVariables);
		} else if (method == RequestMethod.DELETE) {
			builder = MockMvcRequestBuilders.delete(urlTemplate, urlVariables);
		} else {
			throw new UnsupportedOperationException(method.name());
		}
		if (null != content) {
			builder.contentType(contentType).content(content);
		}
		try {
			return mockMvc.perform(builder).andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			return hanldeRequestException(e, method, contentType, content, urlTemplate, urlVariables);
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param file
	 *            文件域名称
	 * @param data
	 *            数据
	 * @param urlTemplate
	 * @param urlVariables
	 * @return
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	public String fileUpload(String file, String path, String urlTemplate, Object... urlVariables) {
		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(urlTemplate, urlVariables);
		try {
			builder.file(file, FileUtils.readFileToByteArray(new File(path)));
			return mockMvc.perform(builder).andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param e
	 * @param method
	 * @param contentType
	 * @param content
	 * @param urlTemplate
	 * @param urlVariables
	 * @return
	 */
	protected String hanldeRequestException(Exception e, RequestMethod method, MediaType contentType, String content, String urlTemplate,
			Object... urlVariables) {
		return null;
	}

	/**
	 * 登录
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	/*private boolean login(String userName, String password) {
		User user = new User(userName, password);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users/login");
		builder.contentType(MediaType.APPLICATION_JSON).content(gson.toJson(user));
		String result = null;
		try {
			result = mockMvc.perform(builder).andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new IllegalStateException("用户名[" + userName + "],密码[" + password + "]登录异常,请重新登录。");
		}
		boolean success = Boolean.parseBoolean(result);
		Assert.isTrue(success, "用户名[" + userName + "],密码[" + password + "]登录失败,请验证用户名密码。");
		this.loginBefore = false;
		return success;
	}*/

	protected abstract void init();

}
