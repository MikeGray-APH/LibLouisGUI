/* Copyright (C) 2016 American Printing House for the Blind Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aph.liblouisgui;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.ptr.IntByReference;

import java.io.UnsupportedEncodingException;

public class LibLouis
{

	public static void loadLibrary(String libraryPath)
	{
		NativeLibrary library = NativeLibrary.getInstance(libraryPath);
		Native.register(library);
	}

	public static native String lou_version();

	public static native void lou_setDataPath(String path);

	public static native int lou_translateString(String tables, Memory inbuf, IntByReference inlen, Memory outbuf, IntByReference outlen, short typeforms[], byte spacing[], int mode);

	public static native int lou_backTranslateString(String tables, Memory inbuf, IntByReference inlen, Memory outbuf, IntByReference outlen, short typeforms[], byte spacing[], int mode);

	public static String translateString(String tables, String inputString, int outputMax, short typeforms[], byte spacing[], int mode) throws UnsupportedEncodingException
	{
		byte inputBytes[] = inputString.getBytes("UTF-16LE");

		Memory inbuf = new Memory(inputBytes.length);
		inbuf.write(0, inputBytes, 0, inputBytes.length);
		IntByReference inlen = new IntByReference(inputString.length());

		Memory outbuf = new Memory(outputMax * 2);
		IntByReference outlen = new IntByReference(outputMax * 2);

		int result = lou_translateString(tables, inbuf, inlen, outbuf, outlen, null, null, 0);

		if(result == 0)
			return null;

		int length = outlen.getValue();
		byte outputBytes[] = outbuf.getByteArray(0, length * 2);
		String outputString = new String(outputBytes, "UTF-16LE");

		return outputString;
	}

	public static String backTranslateString(String tables, String inputString, int outputMax, short typeforms[], byte spacing[], int mode) throws UnsupportedEncodingException
	{
		byte inputBytes[] = inputString.getBytes("UTF-16LE");

		Memory inbuf = new Memory(inputBytes.length);
		inbuf.write(0, inputBytes, 0, inputBytes.length);
		IntByReference inlen = new IntByReference(inputString.length());

		Memory outbuf = new Memory(outputMax * 2);
		IntByReference outlen = new IntByReference(outputMax * 2);

		int result = lou_backTranslateString(tables, inbuf, inlen, outbuf, outlen, null, null, 0);

		int length = outlen.getValue();
		byte outputBytes[] = outbuf.getByteArray(0, length * 2);
		String outputString = new String(outputBytes, "UTF-16LE");

		return outputString;
	}
}
