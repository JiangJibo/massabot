package com.massabot.config.mvc;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import com.massabot.config.mvc.interceptor.ResetStateInterceptor;
import com.massabot.config.root.exception.MassaBotExceptionResolver;

/**
 * @since 2016年12月5日 下午4:20:35
 * @version $Id$
 * @author JiangJibo
 *
 */
@EnableAsync
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = { "com.massabot.control", "com.massabot.config.mvc" })
public class AppMvcConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

	private static ApplicationContext appMvcContext;

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver commonsMultipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(10 * 1024 * 1024);
		return multipartResolver;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#configureMessageConverters(java.util.List)
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		converters.add(new MappingJackson2HttpMessageConverter());
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#configureContentNegotiation(org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer)
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(true).useJaf(false).favorParameter(true).parameterName("mediaType").ignoreAcceptHeader(true);
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#configureDefaultServletHandling(org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer)
	 */
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#configureHandlerExceptionResolvers(java.util.List)
	 */
	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add(new MassaBotExceptionResolver());
		exceptionResolvers.add(new DefaultHandlerExceptionResolver());
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		/*ConnectedInterceptor conBOInt = new ConnectedInterceptor();
		registry.addInterceptor(new MappedInterceptor(conBOInt.getIncludePatterns(), conBOInt.getExcludePatterns(), conBOInt));
		RequestTimeInterceptor timeInt = new RequestTimeInterceptor();
		registry.addInterceptor(new MappedInterceptor(timeInt.getIncludePatterns(), timeInt.getExcludePatterns(), timeInt));*/
		ResetStateInterceptor staInt = new ResetStateInterceptor();
		registry.addInterceptor(new MappedInterceptor(staInt.getIncludePatterns(), staInt.getExcludePatterns(), staInt));
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		appMvcContext = applicationContext;
	}

	public static ApplicationContext getAppContext() {
		return appMvcContext;
	}

}
