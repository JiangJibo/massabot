/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.mvc.event;

/**
 * 函数式事件接口
 * 
 * @since 2017年3月28日 下午2:52:55
 * @version $Id$
 * @author JiangJibo
 *
 */
@FunctionalInterface
public interface EventPostProcessor {

	/**
	 * 在ApplicationEvent执行完之后执行此方法
	 * 
	 * @throws Exception
	 */
	public void doAfterEvent() throws Exception;

}
