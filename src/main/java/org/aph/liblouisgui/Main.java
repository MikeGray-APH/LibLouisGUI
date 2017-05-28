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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main
{
	private final Display display;
	private final Shell shell;

	public Main(String args[])
	{
		Display.setAppName("LibLouisGUI");

		display = Display.getDefault();

		shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));
		shell.setText("LibLouisGUI");

		Message.setShell(shell);

		//   load fonts
		loadFont("BrailleZephyr_6.otf");
		loadFont("BrailleZephyr_6b.otf");
		loadFont("BrailleZephyr_6s.otf");
		loadFont("BrailleZephyr_6sb.otf");
		loadFont("BrailleZephyr_8.otf");
		loadFont("BrailleZephyr_8b.otf");
		loadFont("BrailleZephyr_8s.otf");
		loadFont("BrailleZephyr_8sb.otf");
		loadFont("BrailleZephyr_8w.otf");
		loadFont("BrailleZephyr_8wb.otf");
		loadFont("BrailleZephyr_8ws.otf");
		loadFont("BrailleZephyr_8wsb.otf");

		Settings settings = new Settings(display, null);
		if(settings.readSettings())
		{
			if(settings.areLouLibraryFilesValid(false))
			{
				try
				{
					LibLouis.loadLibrary(settings.louLibraryFileName);
					LibLouis.lou_setDataPath(settings.louTablePath);
				}
				catch(UnsatisfiedLinkError error)
				{
					Message.messageError("Invalid liblouis library:  " + settings.louLibraryFileName, error, true);
				}
			}

			if(settings.areAPHLibraryFilesValid(false))
			{
				try
				{
					LibLouisAPH.loadLibrary(settings.aphLibraryFileName);
					LibLouisAPH.louis_set_path(settings.aphTablePath);
				}
				catch(UnsatisfiedLinkError error)
				{
					Message.messageError("Invalid liblouisAPH library:  " + settings.aphLibraryFileName, error, true);
				}
			}
		}

		TextTranslate textTranslate = new TextTranslate(shell);

		Label tableListLabel = new Label(shell, 0);

		new Actions(shell, settings, textTranslate, tableListLabel);

		//   need to set size after everthing has been added
		shell.setSize(320, 480);
		shell.open();
		while(!shell.isDisposed())
		if(!display.readAndDispatch())
			display.sleep();

		display.dispose();

		settings.writeSettings();
	}

	//TODO:  check if font is already on the system
	private void loadFont(String fontFileName)
	{
		try
		{
			InputStream fontInputStream = getClass().getResourceAsStream("/fonts/" + fontFileName);
			if(fontInputStream == null)
				return;
			File fontFile = new File(System.getProperty("java.io.tmpdir") + File.separator + fontFileName);
			FileOutputStream fontOutputStream = new FileOutputStream(fontFile);
			byte buffer[] = new byte[27720];
			int length;
			while((length = fontInputStream.read(buffer)) > 0)
				fontOutputStream.write(buffer, 0, length);
			fontInputStream.close();
			fontOutputStream.close();

			display.loadFont(fontFile.getPath());
		}
		catch(FileNotFoundException exception)
		{
			Message.messageError("ERROR:  Unable to open font file:  " + exception.getMessage());
		}
		catch(IOException exception)
		{
			Message.messageError("ERROR:  Unable to read font file:  " + exception.getMessage());
		}
	}

	public static void main(String args[])
	{
		new Main(args);
	}
}
