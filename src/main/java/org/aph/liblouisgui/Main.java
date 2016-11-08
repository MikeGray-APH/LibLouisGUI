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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main
{
	private final Shell shell;

	public Main(String args[])
	{
		Display.setAppName("LibLouisGUI");

		Display display = Display.getDefault();

		shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));
		shell.setText("LibLouisGUI");

		Message.setShell(shell);

		Settings settings = new Settings(null);
		if(settings.readSettings() && settings.libraryFileName != null && settings.tablePath != null)
		{
			try
			{
				LibLouis.loadLibrary(settings.libraryFileName);
				LibLouis.lou_setDataPath(settings.tablePath);
			}
			catch(UnsatisfiedLinkError error)
			{
				Message.messageError("Invalid liblouis library:  " + settings.libraryFileName, error, true);
			}
		}

		TextTranslate textTranslate = new TextTranslate(shell);
		new Actions(shell, settings, textTranslate);

		//   need to set size after everthing has been added
		shell.setSize(320, 480);
		shell.open();
		while(!shell.isDisposed())
		if(!display.readAndDispatch())
			display.sleep();

		display.dispose();

		settings.writeSettings();
	}

	public static void main(String args[])
	{
		new Main(args);
	}
}
