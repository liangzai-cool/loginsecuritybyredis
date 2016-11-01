package org.xueliang.loginsecuritybyredis.commons;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.xueliang.loginsecuritybyredis")
public class WebAppConfig  {
	
	@Bean
	public ContentNegotiationManagerFactoryBean contentNegotiationManagerFactoryBean() {
	    ContentNegotiationManagerFactoryBean contentNegotiationManagerFactoryBean = new ContentNegotiationManagerFactoryBean();
	    contentNegotiationManagerFactoryBean.setFavorParameter(true);
	    contentNegotiationManagerFactoryBean.setIgnoreAcceptHeader(true);
	    contentNegotiationManagerFactoryBean.setDefaultContentType(MediaType.APPLICATION_JSON);
	    Properties mediaTypesProperties = new Properties();
	    mediaTypesProperties.setProperty("json", MediaType.APPLICATION_JSON_VALUE);
	    contentNegotiationManagerFactoryBean.setMediaTypes(mediaTypesProperties);
		return contentNegotiationManagerFactoryBean;
	}
	
	@Bean
	public ContentNegotiatingViewResolver contentNegotiatingViewResolver(@Autowired ContentNegotiationManagerFactoryBean contentNegotiationManagerFactoryBean) {
		ContentNegotiatingViewResolver contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
		contentNegotiatingViewResolver.setOrder(1);
		contentNegotiatingViewResolver.setContentNegotiationManager(contentNegotiationManagerFactoryBean.getObject());
		return contentNegotiatingViewResolver;
	}
	
	@Bean
	public StringHttpMessageConverter stringHttpMessageConverter() {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
		return stringHttpMessageConverter;
	}
	
	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter(@Autowired StringHttpMessageConverter stringHttpMessageConverter) {
		RequestMappingHandlerAdapter requestMappingHandlerAdapter = new RequestMappingHandlerAdapter();
		requestMappingHandlerAdapter.setMessageConverters(Collections.singletonList(stringHttpMessageConverter));
		return requestMappingHandlerAdapter;
	}
	
//	@Override
//	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
//		configurer.favorPathExtension(false).
//        favorParameter(true).
//        parameterName("mediaType").
//        ignoreAcceptHeader(true).
//        useJaf(false).
//        defaultContentType(MediaType.APPLICATION_JSON).
//        mediaType("xml", MediaType.APPLICATION_XML).
//        mediaType("json", MediaType.APPLICATION_JSON);
//	}
}