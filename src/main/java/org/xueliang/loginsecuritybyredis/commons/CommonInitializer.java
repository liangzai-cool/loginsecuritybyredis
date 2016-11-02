package org.xueliang.loginsecuritybyredis.commons;

import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 基于注解的/WEB-INF/web.xml
 * 依赖 servlet 3.0
 * @author XueLiang
 * @date 2016年10月24日 下午5:58:45
 * @version 1.0
 */
public class CommonInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		
		// 基于注解配置的Web容器上下文
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(WebAppConfig.class);
		
		// 添加编码过滤器并进行映射
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter("UTF-8", true);
		FilterRegistration.Dynamic dynamicFilter = servletContext.addFilter("characterEncodingFilter", characterEncodingFilter);
		dynamicFilter.addMappingForUrlPatterns(null, true, "/*");
		
		// 添加静态资源映射
		ServletRegistration defaultServletRegistration = servletContext.getServletRegistration("default");
		defaultServletRegistration.addMapping("*.html");
		
		Servlet dispatcherServlet = new DispatcherServlet(context);
		ServletRegistration.Dynamic dynamicServlet = servletContext.addServlet("dispatcher", dispatcherServlet);
		dynamicServlet.addMapping("/");
	}
}