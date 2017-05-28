/* Copyright (C) 2017 American Printing House for the Blind Inc.
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

public class LibLouisAPH
{

	public static void loadLibrary(String libraryPath)
	{
		NativeLibrary library = NativeLibrary.getInstance(libraryPath);
		Native.register(library);
	}

	public static native String louis_get_version();

	public static native void louis_set_path(String path);

	public static native int louis_translate_forward(Memory dots, int dots_len, Memory chars, int chars_len, String tables_name, String conversion_name, int chars_to_dots_map[], int dots_to_chars_map[]);

	public static native int louis_translate_backward(Memory dots, int dots_len, Memory chars, int chars_len, String tables_name, String conversion_name, int chars_to_dots_map[], int dots_to_chars_map[]);

	public static String louisTranslateString(String charsString, String tables, String conversion, int charsToDotsMap[], int dotsToCharsMap[]) throws UnsupportedEncodingException
	{
		byte charsBytes[] = charsString.getBytes("UTF-16LE");
		Memory charsBuffer = new Memory(charsBytes.length);
		charsBuffer.write(0, charsBytes, 0, charsBytes.length);
		int charsLength = charsString.length();

		int dotsLength = charsString.length() * 5;
		if(dotsLength < 0x100)
			dotsLength = 0x100;
		Memory dotsBuffer = new Memory(dotsLength);

		int length = louis_translate_forward(dotsBuffer, dotsLength, charsBuffer, charsLength, tables, conversion, null, null);

		if(length == 0)
			return null;

		byte dotsBytes[] = dotsBuffer.getByteArray(0, length * 2);
		return new String(dotsBytes, "UTF-16LE");
	}

	public static String louisBackTranslateString(String dotsString, String tables, String conversion, int charsToDotsMap[], int dotsToCharsMap[]) throws UnsupportedEncodingException
	{
		byte dotsBytes[] = dotsString.getBytes("UTF-16LE");
		Memory dotsBuffer = new Memory(dotsBytes.length);
		dotsBuffer.write(0, dotsBytes, 0, dotsBytes.length);
		int dotsLength = dotsString.length();

		int charsLength = dotsString.length() * 7;
		if(charsLength < 0x100)
			charsLength = 0x100;
		Memory charsBuffer = new Memory(charsLength * 2);

		int length = louis_translate_backward(charsBuffer, charsLength, dotsBuffer, dotsLength, tables, conversion, null, null);

		if(length == 0)
			return null;

		byte charsBytes[] = charsBuffer.getByteArray(0, length * 2);
		return new String(charsBytes, "UTF-16LE");
	}
}

