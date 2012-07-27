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
package adapter.davis;

public class DavisReturnCodes {
	// If 1 then it may be ACK(0x06), NACK(0x21), CANCEL(0x18)
	public static final byte ACK = 0x06;
	public static final byte NACK = 0x21;
	public static final byte CANCEL = 0x18;
}
