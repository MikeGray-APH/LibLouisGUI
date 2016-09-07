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

public class LibLouis
{
	private static String libraryPath, dataPath;

	public static String getLibraryPath()
	{
		return libraryPath;
	}

	public static void loadLibrary(String libraryPath)
	{
		LibLouis.libraryPath = libraryPath;

		NativeLibrary library = NativeLibrary.getInstance(libraryPath);
		Native.register(library);
	}

	public static String getDataPath()
	{
		return dataPath;
	}

	public static void setDataPath(String dataPath)
	{
		LibLouis.dataPath = dataPath;
	}

	public static native String lou_version();

	public static native void lou_setDataPath(String path);

	public static native int lou_translateString(String tables, Memory inbuf, IntByReference inlen, Memory outbuf, IntByReference outlen, short typeforms[], byte spacing[], int mode);
}
