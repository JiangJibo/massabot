/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.root.exception;

/**
 * @since 2017年2月21日 下午4:06:32
 * @version $Id$
 * @author JiangJibo
 *
 */
public class MassaBotException extends RuntimeException {

	private static final long serialVersionUID = -151403854634917402L;

	public MassaBotException(String message) {
		super(message);
	}

	public MassaBotException(Exception e) {
		super(e);
	}

	public MassaBotException(String message, Exception e) {
		super(message, e);
	}

}
