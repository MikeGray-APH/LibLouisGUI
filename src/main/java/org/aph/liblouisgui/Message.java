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

public class Message
{
	static private Shell shell;
	static private final StringWriter logString = new StringWriter();
	static private final PrintWriter logWriter = new PrintWriter(logString);

	private Message(){}

	static String getMessageString()
	{
		return logString.toString();
	}

	static void setShell(Shell shell)
	{
		Message.shell = shell;
	}

	static void messageError(String message, String info, boolean showMessage)
	{
		String string;

		if(info == null)
			string = "ERROR:  " + message;
		else
			string = "ERROR:  " + message + ":  " + info;
		System.err.println(string);
		System.err.flush();
		logWriter.println(string);
		logWriter.flush();

		if(showMessage && shell != null)
		{
			if(info == null)
				string = "ERROR:  " + message;
			else
				string = "ERROR:  " + message + ":\n" + info;
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage(string);
			messageBox.open();
		}
	}

	static void messageError(String message, Exception exception, boolean showMessage)
	{
		messageError(message, exception.getMessage(), showMessage);
	}

	static void messageError(String message, Exception exception)
	{
		messageError(message, exception, true);
	}

	static void messageError(String message, Error error, boolean showMessage)
	{
		messageError(message, error.getMessage(), showMessage);
	}

	static void messageError(String message, Error error)
	{
		messageError(message, error, true);
	}

	static void messageError(String message, String info)
	{
		messageError(message, info, true);
	}

	static void messageError(String message, boolean showMessage)
	{
		messageError(message, (String)null, showMessage);
	}

	static void messageError(String message)
	{
		messageError(message, true);
	}

}
