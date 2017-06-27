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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Log
{
	static final int LOG_ALL = 0;
	static final int LOG_TRACE = 1;
	static final int LOG_DEBUG = 2;
	static final int LOG_INFO = 3;
	static final int LOG_WARNING = 4;
	static final int LOG_ERROR = 5;
	static final int LOG_FATAL = 6;
	
	static private Shell shell;
	static private final StringWriter logString = new StringWriter();
	static private final PrintWriter logWriter = new PrintWriter(logString);

	private Log(){}

	static String getMessageString()
	{
		return logString.toString();
	}

	static void setShell(Shell shell)
	{
		Log.shell = shell;
	}

	static void message(int level, String message, String info, boolean showMessage)
	{
		String string;
		
		switch(level)
		{
		case LibLouisAPH.LOG_ALL:      string = "ALL:  ";      break;
		case LibLouisAPH.LOG_TRACE:    string = "TRACE:  ";    break;
		case LibLouisAPH.LOG_DEBUG:    string = "DEBUG:  ";    break;
		case LibLouisAPH.LOG_INFO:     string = "INFO:  ";     break;
		case LibLouisAPH.LOG_WARNING:  string = "WARNING:  ";  break;
		case LibLouisAPH.LOG_ERROR:    string = "ERROR:  ";    break;
		case LibLouisAPH.LOG_FATAL:    string = "FATAL:  ";    break;
		default:                       string = "???:  ";      break;
		}

		string += message;
		if(info != null)
			string += info;
		System.err.println(string);
		System.err.flush();
		logWriter.println(string);
		logWriter.flush();

		if(showMessage && shell != null)
		{
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage(string);
			messageBox.open();
		}
	}

	static void message(int log, String message, Exception exception, boolean showMessage)
	{
		message(log, message, exception.getMessage(), showMessage);
	}

	static void message(int log, String message, Exception exception)
	{
		message(log, message, exception, true);
	}

	static void message(int log, String message, Error error, boolean showMessage)
	{
		message(log, message, error.getMessage(), showMessage);
	}

	static void message(int log, String message, Error error)
	{
		message(log, message, error, true);
	}

	static void message(int log, String message, String info)
	{
		message(log, message, info, true);
	}

	static void message(int log, String message, boolean showMessage)
	{
		message(log, message, (String)null, showMessage);
	}

	static void message(int log, String message)
	{
		message(log, message, true);
	}

}
