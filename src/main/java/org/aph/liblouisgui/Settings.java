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

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Settings
{
	String version;
	String louLibraryFileName, louTablePath, louTableList;
	String aphLibraryFileName, aphTablePath, aphTableList, aphConversionFileName;
	boolean usingAPH;
	Font textFont, brailleFont;

	private final Display display;
	private final File file;

	public Settings(Display display, String fileName)
	{
		this.display = display;

		if(fileName == null)
			fileName = System.getProperty("user.home") + File.separator + ".liblouisgui.conf";
		file = new File(fileName);
	}

	private boolean readLine(String line)
	{
		if(line.length() == 0)
			return true;

		int offset = line.indexOf(' ');
		if(offset < 0)
			return false;

		String value = line.substring(offset + 1);
		if(value.length() < 1)
			return false;

		String tokens[];
		switch(line.substring(0, offset))
		{
		case "version":  version = value;  break;

		case "lou.library.fileName":  louLibraryFileName = value;  break;
		case "lou.table.path":        louTablePath = value;        break;
		case "lou.table.list":        louTableList = value;        break;

		case "aph.library.fileName":     aphLibraryFileName = value;     break;
		case "aph.table.path":           aphTablePath = value;           break;
		case "aph.table.list":           aphTableList = value;           break;
		case "aph.conversion.fileName":  aphConversionFileName = value;  break;

		case "using.library":

			if(value.equals("LibLouis"))
				usingAPH = false;
			else if(value.equals("LibLouisAPH"))
				usingAPH = true;
			else
				return false;
			break;

		case "text.font":

			//   find offset for fileName
			offset = value.indexOf(' ') + 1;
			if(offset < 1 || offset == value.length())
				return false;
			offset = value.indexOf(' ', offset) + 1;
			if(offset < 1 || offset == value.length())
				return false;
			tokens = value.split(" ");
			if(tokens.length < 3)
				return false;
			textFont = new Font(display, value.substring(offset), Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
			break;

		case "braille.font":

			//   find offset for fileName
			offset = value.indexOf(' ') + 1;
			if(offset < 1 || offset == value.length())
				return false;
			offset = value.indexOf(' ', offset) + 1;
			if(offset < 1 || offset == value.length())
				return false;
			tokens = value.split(" ");
			if(tokens.length < 3)
				return false;
			brailleFont = new Font(display, value.substring(offset), Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
			break;

		default:  return false;
		}

		return true;
	}

	boolean readSettings()
	{
		if(!file.exists())
		{
			Log.message(Log.LOG_WARNING, "Settings file not found:  " + file.getPath(), false);
			return false;
		}

		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String line;
			int lineNumber = 1;
			while((line = reader.readLine()) != null)
			{
				try
				{
					if(!readLine(line))
						Log.message(Log.LOG_ERROR, "Unknown setting, line #" + lineNumber, line + " -- " + file.getPath(), false);
				}
				catch(NumberFormatException ignored)
				{
					Log.message(Log.LOG_ERROR, "Bad setting value, line #" + lineNumber, line + " -- " + file.getPath(), false);
				}
				finally
				{
					lineNumber++;
				}
			}
		}
		catch(FileNotFoundException exception)
		{
			Log.message(Log.LOG_ERROR, "Unable to open settings file for reading", exception);
		}
		catch(IOException exception)
		{
			Log.message(Log.LOG_ERROR, "Unable to read settings file", exception);
		}
		finally
		{
			try
			{
				if(reader != null)
					reader.close();
			}
			catch(IOException exception)
			{
				Log.message(Log.LOG_ERROR, "Unable to close settings file", exception);
			}
		}

		return true;
	}

	private void writeLines(PrintWriter writer)
	{
		String version = System.getProperty("liblouisgui.version");
		if(version != null)
			writer.println("version " + version);

		if(louLibraryFileName != null)
			writer.println("lou.library.fileName " + louLibraryFileName);
		if(louTablePath != null)
		writer.println("lou.table.path " + louTablePath);
		if(louTableList != null)
			writer.println("lou.table.list " + louTableList);

		if(aphLibraryFileName != null)
			writer.println("aph.library.fileName " + aphLibraryFileName);
		if(aphTablePath != null)
			writer.println("aph.table.path " + aphTablePath);
		if(aphTableList != null)
			writer.println("aph.table.list " + aphTableList);
		if(aphConversionFileName != null)
			writer.println("aph.conversion.fileName " + aphConversionFileName);

		if(usingAPH)
			writer.println("using.library LibLouisAPH");
		else
			writer.println("using.library LibLouis");

		if(textFont != null)
		{
			FontData fontData = textFont.getFontData()[0];
			writer.println("text.font "
			               + fontData.getHeight() + ' '
			               + fontData.getStyle() + ' '
			               + fontData.getName());
		}

		if(brailleFont != null)
		{
			FontData fontData = brailleFont.getFontData()[0];
			writer.println("braille.font "
			               + fontData.getHeight() + ' '
			               + fontData.getStyle() + ' '
			               + fontData.getName());
		}

		writer.println();
	}

	boolean writeSettings()
	{
		try
		{
			if(!file.exists())
				file.createNewFile();
		}
		catch(IOException exception)
		{
			Log.message(Log.LOG_ERROR, "Unable to create settings file", exception);
			return false;
		}

		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(file);
			writeLines(writer);
		}
		catch(FileNotFoundException exception)
		{
			Log.message(Log.LOG_ERROR, "Unable to open settings file for writing", exception);
			return false;
		}
		finally
		{
			if(writer != null)
				writer.close();
		}

		return true;
	}

	boolean areLouLibraryFilesValid(boolean outMessage)
	{
		if(louLibraryFileName == null)
		{
			if(outMessage)
				Log.message(Log.LOG_WARNING, "LibLouis library not set", true);
			return false;
		}

		if(!new File(louLibraryFileName).exists())
		{
			if(outMessage)
				Log.message(Log.LOG_WARNING, "LibLouis library does not exist:  " + louLibraryFileName, true);
			return false;
		}

		//TODO:  some libraries don't need it
//		if(louTablePath == null)
//		{
//			if(outMessage)
//				Log.message("Table path not set", true);
//			return false;
//		}

		if(louTableList == null)
		{
			if(outMessage)
				Log.message(Log.LOG_ERROR, "Tables not set", true);
			return false;
		}

		return true;
	}

	boolean areAPHLibraryFilesValid(boolean outMessage)
	{
		if(aphLibraryFileName == null)
		{
			if(outMessage)
				Log.message(Log.LOG_WARNING, "LibLouisAPH library not set", true);
			return false;
		}

		if(!new File(aphLibraryFileName).exists())
		{
			if(outMessage)
				Log.message(Log.LOG_WARNING, "LibLouisAPH library does not exist:  " + aphLibraryFileName, true);
			return false;
		}

		//TODO:  some libraries don't need it
//		if(aphTablePath == null)
//		{
//			if(outMessage)
//				Log.message("Table path not set", true);
//			return false;
//		}

		if(aphTableList == null)
		{
			if(outMessage)
				Log.message(Log.LOG_WARNING, "Tables not set", true);
			return false;
		}

		return true;
	}
}
