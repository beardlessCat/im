package com.bigyj.server.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具, 用户获取bean或者HttpServletRequest
 */
@Component
public class SpringContextUtil implements ApplicationContextAware
{

	/**
	 * 上下文对象实例
	 */
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		SpringContextUtil.applicationContext = applicationContext;
	}

	public static void setContext(ApplicationContext applicationContext)
	{
		SpringContextUtil.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	/**
	 * 通过name获取 Bean.
	 */
	public static <T> T getBean(String name)
	{
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 通过class获取Bean.
	 */
	public static <T> T getBean(Class<T> clazz)
	{
		if (null == applicationContext)
		{
			return null;
		}
		return applicationContext.getBean(clazz);
	}

	/**
	 * 通过name,以及Clazz返回指定的Bean
	 */
	public static <T> T getBean(String name, Class<T> clazz)
	{
		return applicationContext.getBean(name, clazz);
	}


}
