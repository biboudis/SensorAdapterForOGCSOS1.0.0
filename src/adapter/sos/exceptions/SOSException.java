/*******************************************************************************
 * Copyright (c) 2012 Aggelos Biboudis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Aggelos Biboudis - initial API and implementation
 ******************************************************************************/
package adapter.sos.exceptions;

public class SOSException extends Exception {
	public SOSException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String exceptionCode;
	
	
	public SOSException(String message, String exceptionCode) {
		super(message);
		this.setExceptionCode(exceptionCode);
	}

	public SOSException(String message) {
		super(message);
	}

	public String getExceptionCode() {
		return exceptionCode;
	}
	public void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

}
