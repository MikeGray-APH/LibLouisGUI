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

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import java.io.UnsupportedEncodingException;

public class LibLouisAPH
{
	static final LibLouisAPHCallback libLouisAPHLog = new LibLouisAPHLog();
	static final int LOG_ALL = 0;
	static final int LOG_TRACE = 1;
	static final int LOG_DEBUG = 2;
	static final int LOG_INFO = 3;
	static final int LOG_WARNING = 4;
	static final int LOG_ERROR = 5;
	static final int LOG_FATAL = 6;
	
	public static void loadLibrary(String libraryPath)
	{
		NativeLibrary library = NativeLibrary.getInstance(libraryPath);
		Native.register(library);
		
		louis_set_log_callback(libLouisAPHLog);
	}

	public static native String louis_get_version();
	
	public static native void louis_set_log_callback(LibLouisAPHCallback libLouisAPHCallback);

	public static native void louis_set_path(String path);

	public static native int louis_translate_forward(char dots[], int dots_len, char chars[], int chars_len, String tables_name, String conversion_name, int chars_to_dots_map[], int dots_to_chars_map[]);

	public static native int louis_translate_backward(char chars[], int chars_len, char dots[], int dots_len, String tables_name, String conversion_name, int chars_to_dots_map[], int dots_to_chars_map[]);

	private static String translate(String charsString, String tables, String conversion, int charsToDotsMap[], int dotsToCharsMap[], boolean forward) throws UnsupportedEncodingException
	{
		int charsLength = charsString.length();
		char charsChars[] = new char[charsLength + 1];
		charsString.getChars(0, charsLength, charsChars, 0);
		
		int dotsLength = charsLength * 5;
		if(dotsLength < 0x100)
			dotsLength = 0x100;
		char dotsChars[] = new char[dotsLength + 1];
		
		int charsToDotsMapInts[] = null;
		if(charsToDotsMap != null)
			charsToDotsMapInts = new int[charsLength];
		
		int dotsToCharsMapInts[] = null;
		if(dotsToCharsMap != null)
			dotsToCharsMapInts = new int[dotsLength];
		
		int length = 0;
		if(forward)
			length = louis_translate_forward(dotsChars, dotsLength, charsChars, charsLength, tables, conversion, charsToDotsMapInts, dotsToCharsMapInts);
		else
			length = louis_translate_backward(dotsChars, dotsLength, charsChars, charsLength, tables, conversion, charsToDotsMapInts, dotsToCharsMapInts);
		
		if(length <= 0)
			return null;
		
		if(charsToDotsMapInts != null)
		for(int i = 0; i < charsLength && i < charsToDotsMap.length; i++)
			charsToDotsMap[i] = charsToDotsMapInts[i];
		
		if(dotsToCharsMapInts != null)
		for(int i = 0; i < length && i < dotsToCharsMap.length; i++)
			dotsToCharsMap[i] = dotsToCharsMapInts[i];
		
		return new String(dotsChars, 0, length);
	}

	public static String translateForward(String charsString, String tables, String conversion, int charsToDotsMap[], int dotsToCharsMap[]) throws UnsupportedEncodingException
	{
		return translate(charsString, tables, conversion, charsToDotsMap, dotsToCharsMap, true);
	}

	public static String translateBackward(String dotsString, String tables, String conversion, int dotsToCharsMap[], int charsToDotsMap[]) throws UnsupportedEncodingException
	{
		return translate(dotsString, tables, conversion, dotsToCharsMap, charsToDotsMap, false);
	}
}

interface LibLouisAPHCallback extends Callback
{
	void logMessage(int level, String message);
}

class LibLouisAPHLog implements LibLouisAPHCallback
{
	@Override
	public void logMessage(int level, String message)
	{
		Log.message(level, message, false);
	}
}
