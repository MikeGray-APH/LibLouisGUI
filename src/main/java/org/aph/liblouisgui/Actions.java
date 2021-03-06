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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Actions
{
	private final Shell parentShell;
	private final Settings settings;
	private final TextTranslate textTranslate;
	private final Label tableListLabel;
	
	private final EditConversionFileNameAction editConversionFileNameAction;

	public Actions(Shell parentShell, Settings settings, TextTranslate textTranslate, Label tableListLabel)
	{
		this.parentShell = parentShell;
		this.settings = settings;
		this.textTranslate = textTranslate;
		this.tableListLabel = tableListLabel;

		if(settings.usingAPH)
		{
			if(settings.aphTableList != null)
				tableListLabel.setText("Tables:  " + settings.aphTableList);
			else
				tableListLabel.setText("Tables:");
		}
		else
		{
			if(settings.louTableList != null)
				tableListLabel.setText("Tables:  " + settings.louTableList);
			else
				tableListLabel.setText("Tables:");
		}
		setTableListLabelToolTip();

		if(settings.textFont != null)
			textTranslate.setTextFont(settings.textFont);
		if(settings.brailleFont != null)
			textTranslate.setBrailleFont(settings.brailleFont);

		Menu menuBar = new Menu(parentShell, SWT.BAR);
		parentShell.setMenuBar(menuBar);

		ToolBar toolBar = new ToolBar(parentShell, SWT.HORIZONTAL | SWT.FLAT);
		toolBar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		toolBar.moveAbove(null);

		Menu menu;
		MenuItem item;

		String mod1KeyName = "Ctrl+";
		String mod2KeyName = "Shift+";
		if(System.getProperty("os.name").toLowerCase().startsWith("mac"))
		{
			mod1KeyName = "⌘";
			mod2KeyName = "⇧";
		}

		//   LibLouis menu
		menu = new Menu(menuBar);
		item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("&Settings");
		item.setMenu(menu);

		editConversionFileNameAction = new EditConversionFileNameAction();
		new SwitchLibraryHandler().addToMenu(menu, "use", 0, true);
		new MenuItem(menu, SWT.SEPARATOR);
		new SetLibraryPathDialogAction().addToMenu(menu, "Set Library Path", 0, true);
		new SetTablePathDialogAction().addToMenu(menu, "Set Table Path", 0, true);
		new EditTablePathAction().addToMenu(menu, "Edit Table Path", 0, true);
		new EditTableListAction().addToMenu(menu, "Edit Tables", 0, true);
		editConversionFileNameAction.addToMenu(menu, "Edit Conversion", 0, true);
		editConversionFileNameAction.menuItem.setEnabled(settings.usingAPH);
		new MenuItem(menu, SWT.SEPARATOR);
		new TextFontHandler().addToMenu(menu, "Text Font", 0, true);
		new BrailleFontHandler().addToMenu(menu, "Braille Font", 0, true);

		//   edit menu
		menu = new Menu(menuBar);
		item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("&Edit");
		item.setMenu(menu);

		//   cut, copy, and paste accelerators are handled by StyledText.
		new CutAction().addToMenu(menu, "Cut\t" + mod1KeyName + 'X', 0, true);
		new CopyAction().addToMenu(menu, "Copy\t" + mod1KeyName + 'C', 0, true);
		new PasteAction().addToMenu(menu, "Paste\t" + mod1KeyName + 'V', 0, true);

		//   translate menu
		menu = new Menu(menuBar);
		item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("&Translate");
		item.setMenu(menu);

		new TranslateTextAction().addToMenuAndToolBar(menu, toolBar, "translate", 0, "forward-32x32.png", true);
		new TranslateBrailleAction().addToMenuAndToolBar(menu, toolBar, "back-translate", 0, "backward-32x32.png", true);
		new ConvertToUnicode().addToMenuAndToolBar(menu, toolBar, "unicode", 0, "unicode-32x32.png", true);
		new ConvertToAscii().addToMenuAndToolBar(menu, toolBar, "ascii", 0, "ascii-32x32.png", true);

		//   about menu
		menu = new Menu(menuBar);
		item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("Help");
		item.setMenu(menu);

		new AboutHandler().addToMenu(menu, "About", 0, true);
		new ShowLog().addToMenu(menu, "Show Log", 0, true);
	}

	private void setTableListLabelToolTip()
	{
		StringBuilder toolTipString = new StringBuilder(100);
		toolTipString.append("Library:");
		if(settings.usingAPH)
		{
			if(settings.aphLibraryFileName != null)
				toolTipString.append("  ").append(settings.aphLibraryFileName);
		}
		else
		{
			if(settings.louLibraryFileName != null)
				toolTipString.append("  ").append(settings.louLibraryFileName);
		}
		toolTipString.append(System.getProperty("line.separator")).append("Tables Path:");
		if(settings.usingAPH)
		{
			if(settings.aphTablePath != null)
				toolTipString.append("  ").append(settings.aphTablePath);
		}
		else
		{
			if(settings.louTablePath != null)
				toolTipString.append("  ").append(settings.louTablePath);
		}
		if(settings.usingAPH)
		{
			toolTipString.append(System.getProperty("line.separator")).append("Conversion File:");
			if(settings.aphConversionFileName != null)
				toolTipString.append("  ").append(settings.aphConversionFileName);
		}
		tableListLabel.setToolTipText(toolTipString.toString());
	}

	private final class SwitchLibraryHandler extends BaseAction
	{
		@Override
		void addToMenu(Menu menu, String tag, int accelerator, boolean enabled)
		{
			super.addToMenu(menu, tag, accelerator, enabled);
			
			if(settings.usingAPH)
				menuItem.setText("use LibLouis");
			else
				menuItem.setText("use LibLouisAPH");
		}

		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			settings.usingAPH = !settings.usingAPH;
			if(settings.usingAPH)
			{
				menuItem.setText("use LibLouis");
				if(settings.aphTableList != null)
					tableListLabel.setText("Tables:  " + settings.aphTableList);
				else
					tableListLabel.setText("Tables:");

				editConversionFileNameAction.setEnabled(true);
				parentShell.setText("LibLouisAPH");
			}
			else
			{
				menuItem.setText("use LibLouisAPH");
				if(settings.louTableList != null)
					tableListLabel.setText("Tables:  " + settings.louTableList);
				else
					tableListLabel.setText("Tables:");

				editConversionFileNameAction.setEnabled(false);
				parentShell.setText("LibLouis");
			}
			setTableListLabelToolTip();
		}
	}

	private final class SetLibraryPathDialogAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			FileDialog fileDialog = new FileDialog(parentShell, SWT.OPEN);
			if(settings.usingAPH)
				fileDialog.setFileName(settings.aphLibraryFileName);
			else
				fileDialog.setFileName(settings.louLibraryFileName);
			String fileName = fileDialog.open();
			if(fileName == null)
				return;

			if(!new File(fileName).exists())
			{
				if(settings.usingAPH)
					Log.message(Log.LOG_ERROR, "LibLouisAPH library does not exist:  " + fileName, true);
				else
					Log.message(Log.LOG_ERROR, "LibLouis library does not exist:  " + fileName, true);
				return;
			}

			try
			{
				if(settings.usingAPH)
				{
					LibLouisAPH.loadLibrary(fileName);
					settings.aphLibraryFileName = fileName;
					setTableListLabelToolTip();
				}
				else
				{
					LibLouis.loadLibrary(fileName);
					settings.louLibraryFileName = fileName;
					setTableListLabelToolTip();
				}
			}
			catch(UnsatisfiedLinkError error)
			{
				if(settings.usingAPH)
					Log.message(Log.LOG_ERROR, "Invalid LibLouisAPH library:  " + fileName, error, true);
				else
					Log.message(Log.LOG_ERROR, "Invalid LibLouis library:  " + fileName, error, true);
				return;
			}

			if(settings.usingAPH)
				settings.aphLibraryFileName = fileName;
			else
				settings.louLibraryFileName = fileName;
		}
	}

	private final class SetTablePathDialogAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			DirectoryDialog directoryDialog = new DirectoryDialog(parentShell, SWT.OPEN);
			String directoryName = directoryDialog.open();
			if(directoryName == null)
				return;

			try
			{
				if(settings.usingAPH)
				{
					LibLouisAPH.louis_set_path(directoryName);
					settings.aphTablePath = directoryName;
				}
				else
				{
					LibLouis.lou_setDataPath(directoryName);
					settings.louTablePath = directoryName;
				}
				setTableListLabelToolTip();
			}
			catch(UnsatisfiedLinkError error)
			{
				if(settings.usingAPH)
					Log.message(Log.LOG_ERROR, "Invalid liblouisAPH library:  " + settings.aphLibraryFileName, error, true);
				else
					Log.message(Log.LOG_ERROR, "Invalid liblouis library:  " + settings.louLibraryFileName, error, true);
			}
		}
	}

	private final class EditTablePathAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			new EditTablePathDialog();
		}
	}

	private final class EditTablePathDialog implements SelectionListener, KeyListener
	{
		private final Shell shell;
		private final Text text;
		private final Button okButton;
		private final Button cancelButton;

		private EditTablePathDialog()
		{
			shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
			shell.setText("tables");
			shell.setLayout(new GridLayout(1, true));

			text = new Text(shell, SWT.SINGLE | SWT.LEFT);
			if(settings.usingAPH)
				text.setText(settings.aphTablePath);
			else
				text.setText(settings.louTablePath);
			text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
			text.addKeyListener(this);

			Composite composite = new Composite(shell, 0);
			composite.setLayout(new GridLayout(2, true));
			composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));

			okButton = new Button(composite, SWT.PUSH);
			okButton.setText("OK");
			okButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			okButton.addSelectionListener(this);

			cancelButton = new Button(composite, SWT.PUSH);
			cancelButton.setText("Cancel");
			cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			cancelButton.addSelectionListener(this);

			shell.pack();
			shell.open();
		}

		private void setTablePath(String tablePath)
		{
			try
			{
				if(settings.usingAPH)
				{
					//LibLouis.lou_setDataPath(tablePath);
					settings.aphTablePath = tablePath;
				}
				else
				{
					LibLouis.lou_setDataPath(tablePath);
					settings.louTablePath = tablePath;
				}
				setTableListLabelToolTip();
			}
			catch(UnsatisfiedLinkError error)
			{
				if(settings.usingAPH)
					Log.message(Log.LOG_ERROR, "Invalid liblouisAPH library:  " + settings.aphLibraryFileName, error, true);
				else
					Log.message(Log.LOG_ERROR, "Invalid liblouis library:  " + settings.louLibraryFileName, error, true);
			}
		}

		@Override
		public void widgetSelected(SelectionEvent event)
		{
			if(event.widget == okButton)
				setTablePath(text.getText());
			shell.dispose();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent ignored){}

		@Override
		public void keyPressed(KeyEvent event)
		{
			if(event.keyCode == '\r' || event.keyCode == '\n')
			{
				setTablePath(text.getText());
				shell.dispose();
			}
		}

		@Override
		public void keyReleased(KeyEvent ignored){}
	}

	private final class EditTableListAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			new EditTableListDialog();
		}
	}

	private final class EditTableListDialog implements SelectionListener, KeyListener
	{
		private final Shell shell;
		private final Text text;
		private final Button okButton;
		private final Button cancelButton;

		private EditTableListDialog()
		{
			shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
			shell.setText("tables");
			shell.setLayout(new GridLayout(1, true));

			text = new Text(shell, SWT.SINGLE | SWT.LEFT);
			if(settings.usingAPH)
			{
				if(settings.aphTableList != null)
					text.setText(settings.aphTableList);
			}
			else
			{
				if(settings.louTableList != null)
					text.setText(settings.louTableList);
			}
			text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
			text.addKeyListener(this);

			Composite composite = new Composite(shell, 0);
			composite.setLayout(new GridLayout(2, true));
			composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));

			okButton = new Button(composite, SWT.PUSH);
			okButton.setText("OK");
			okButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			okButton.addSelectionListener(this);

			cancelButton = new Button(composite, SWT.PUSH);
			cancelButton.setText("Cancel");
			cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			cancelButton.addSelectionListener(this);

			shell.pack();
			shell.open();
		}

		@Override
		public void widgetSelected(SelectionEvent event)
		{
			if(event.widget == okButton)
			{
				if(settings.usingAPH)
				{
					settings.aphTableList = text.getText();
					tableListLabel.setText("Tables:  " + settings.aphTableList);
				}
				else
				{
					settings.louTableList = text.getText();
					tableListLabel.setText("Tables:  " + settings.louTableList);
				}
				parentShell.layout();
			}
			shell.dispose();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent ignored){}

		@Override
		public void keyPressed(KeyEvent event)
		{
			if(event.keyCode == '\r' || event.keyCode == '\n')
			{
				if(settings.usingAPH)
				{
					settings.aphTableList = text.getText();
					tableListLabel.setText("Tables:  " + settings.aphTableList);
				}
				else
				{
					settings.louTableList = text.getText();
					tableListLabel.setText("Tables:  " + settings.louTableList);
				}
				parentShell.layout();
				shell.dispose();
			}
		}

		@Override
		public void keyReleased(KeyEvent ignored){}
	}

	private final class EditConversionFileNameAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			new EditConversionFileNameDialog();
		}
	}

	private final class EditConversionFileNameDialog implements SelectionListener, KeyListener
	{
		private final Shell shell;
		private final Text text;
		private final Button okButton;
		private final Button cancelButton;

		private EditConversionFileNameDialog()
		{
			shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
			shell.setText("tables");
			shell.setLayout(new GridLayout(1, true));

			text = new Text(shell, SWT.SINGLE | SWT.LEFT);
			if(settings.aphConversionFileName != null)
				text.setText(settings.aphConversionFileName);
			text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
			text.addKeyListener(this);

			Composite composite = new Composite(shell, 0);
			composite.setLayout(new GridLayout(2, true));
			composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));

			okButton = new Button(composite, SWT.PUSH);
			okButton.setText("OK");
			okButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			okButton.addSelectionListener(this);

			cancelButton = new Button(composite, SWT.PUSH);
			cancelButton.setText("Cancel");
			cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			cancelButton.addSelectionListener(this);

			shell.pack();
			shell.open();
		}

		@Override
		public void widgetSelected(SelectionEvent event)
		{
			if(event.widget == okButton)
			{
				settings.aphConversionFileName = text.getText();
				if(settings.aphConversionFileName.length() == 0)
					settings.aphConversionFileName = null;
				setTableListLabelToolTip();
			}
			shell.dispose();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent ignored){}

		@Override
		public void keyPressed(KeyEvent event)
		{
			if(event.keyCode == '\r' || event.keyCode == '\n')
			{
				settings.aphConversionFileName = text.getText();
				if(settings.aphConversionFileName.length() == 0)
					settings.aphConversionFileName = null;
				setTableListLabelToolTip();
				shell.dispose();
			}
		}

		@Override
		public void keyReleased(KeyEvent ignored){}
	}

	private class TextFontHandler extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			FontDialog fontDialog = new FontDialog(parentShell, SWT.OPEN);
			fontDialog.setFontList(textTranslate.getTextFont().getFontData());
			FontData fontData = fontDialog.open();
			if(fontData == null)
				return;
			settings.textFont = new Font(parentShell.getDisplay(), fontData);
			textTranslate.setTextFont(settings.textFont);
		}
	}

	private class BrailleFontHandler extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			FontDialog fontDialog = new FontDialog(parentShell, SWT.OPEN);
			fontDialog.setFontList(textTranslate.getBrailleFont().getFontData());
			FontData fontData = fontDialog.open();
			if(fontData == null)
				return;
			settings.brailleFont = new Font(parentShell.getDisplay(), fontData);
			textTranslate.setBrailleFont(settings.brailleFont);
		}
	}

	private class CutAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			textTranslate.cut();
		}
	}

	private class CopyAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			textTranslate.copy();
		}
	}

	private class PasteAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			textTranslate.paste();
		}
	}

	private final class TranslateTextAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			if(settings.usingAPH)
			{
				if(!settings.areAPHLibraryFilesValid(true))
					return;
			}
			else
			{
				if(!settings.areLouLibraryFilesValid(true))
					return;
			}

			String inputLines[] = textTranslate.getTextLines();
			if(inputLines.length == 0)
				return;

			ArrayList<String> outputLines = new ArrayList<>(inputLines.length);

			for(String inputLine : inputLines)
			{
				if(inputLine.length() == 0)
				{
					outputLines.add("");
					continue;
				}

				String outputLine = "";
				try
				{
					if(settings.usingAPH)
						outputLine = LibLouisAPH.translateForward(inputLine, settings.aphTableList, settings.aphConversionFileName, null, null);
					else
						outputLine = LibLouis.translateString(settings.louTableList, inputLine, inputLine.length() * 3, null, null, 0);
				}
				catch(UnsupportedEncodingException exception)
				{
					Log.message(Log.LOG_ERROR, "UnsupportedEncodingException", exception, true);
					return;
				}
				catch(UnsatisfiedLinkError error)
				{
					if(settings.usingAPH)
						Log.message(Log.LOG_ERROR, "Invalid liblouisAPH library:  " + settings.aphLibraryFileName, error, true);
					else
						Log.message(Log.LOG_ERROR, "Invalid liblouis library:  " + settings.louLibraryFileName, error, true);
					return;
				}
				catch(Exception exception)
				{
					Log.message(Log.LOG_ERROR, "Exception", exception, true);
				}
				catch(Error error)
				{
					Log.message(Log.LOG_ERROR, "Error", error, true);
				}
				if(outputLine == null)
				{
					Log.message(Log.LOG_ERROR, "Translation error for line:  " + inputLine, false);
					outputLines.add("");
				}
				else
					outputLines.add(outputLine);
			}

			textTranslate.setBrailleLines(outputLines.toArray(new String[outputLines.size()]));
		}
	}

	private final class TranslateBrailleAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			if(settings.usingAPH)
			{
				if(!settings.areAPHLibraryFilesValid(true))
					return;
			}
			else
			{
				if(!settings.areLouLibraryFilesValid(true))
					return;
			}

			String inputLines[] = textTranslate.getBrailleLines();
			if(inputLines.length == 0)
				return;
			

			ArrayList<String> outputLines = new ArrayList<>(inputLines.length);

			for(String inputLine : inputLines)
			{
				if(inputLine.length() == 0)
				{
					outputLines.add("");
					continue;
				}

				String outputLine = "";
				try
				{
					if(settings.usingAPH)
						outputLine = LibLouisAPH.translateBackward(inputLine, settings.aphTableList, settings.aphConversionFileName, null, null);
					else
						outputLine = LibLouis.backTranslateString(settings.louTableList, inputLine, inputLine.length() * 3, null, null, 0);
				}
				catch(UnsupportedEncodingException exception)
				{
					Log.message(Log.LOG_ERROR, "UnsupportedEncodingException", exception, true);
					return;
				}
				catch(UnsatisfiedLinkError error)
				{
					if(settings.usingAPH)
						Log.message(Log.LOG_ERROR, "Invalid liblouisAPH library:  " + settings.aphLibraryFileName, error, true);
					else
						Log.message(Log.LOG_ERROR, "Invalid liblouis library:  " + settings.louLibraryFileName, error, true);
					return;
				}
				catch(Exception exception)
				{
					Log.message(Log.LOG_ERROR, "Translation failed:  Exception", exception, false);
					textTranslate.setTextLines(null);
				}
				catch(Error error)
				{
					Log.message(Log.LOG_ERROR, "Error", error, true);
				}
				if(outputLine == null)
				{
					Log.message(Log.LOG_ERROR, "Translation error for line:  " + inputLine, false);
					outputLines.add("");
				}
				else
					outputLines.add(outputLine);
			}

			textTranslate.setTextLines(outputLines.toArray(new String[outputLines.size()]));
		}
	}

	private static final String asciiString =   " A1B'K2L@CIF/MSP\"E3H9O6R^DJG>NTQ,*5<-U8V.%[$+X!&;:4\\0Z7(_?W]#Y)=";

	private final class ConvertToUnicode extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			char chars[] = textTranslate.getBraille().toUpperCase().toCharArray();

			for(int i = 0; i < chars.length; i++)
			if(chars[i] >= 0x20 && chars[i] <= 0x5f)
				chars[i] = ((char)(0x2800 + asciiString.indexOf(chars[i])));

			textTranslate.setBraille(new String(chars));
		}
	}

	private final class ConvertToAscii extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			char chars[] = textTranslate.getBraille().toUpperCase().toCharArray();

			for(int i = 0; i < chars.length; i++)
			if(chars[i] >= 0x2800 && chars[i] <= 0x283f)
				chars[i] = asciiString.charAt(chars[i] - 0x2800);

			textTranslate.setBraille(new String(chars));
		}
	}

	private final class AboutHandler extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			new AboutDialog(parentShell);
		}
	}

	private final class AboutDialog
	{
		private AboutDialog(Shell parentShell)
		{
			Shell dialog = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
			dialog.setLayout(new GridLayout(1, true));
			dialog.setText("About BrailleZephyr");

			String versionString = settings.version;
			if(versionString == null)
				versionString = "dev";

			Label label;

			Image image = new Image(parentShell.getDisplay(), getClass().getResourceAsStream("/images/LibLouisGUI-logo-250x65.png"));
			label = new Label(dialog, SWT.CENTER);
			label.setLayoutData(new GridData(GridData.FILL_BOTH));
			label.setImage(image);

			new Label(dialog, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_BOTH));

			label = new Label(dialog, SWT.CENTER);
			label.setLayoutData(new GridData(GridData.FILL_BOTH));
			label.setFont(new Font(parentShell.getDisplay(), "Sans", 14, SWT.BOLD));
			label.setText("LibLouisGUI " + versionString);

			label = new Label(dialog, SWT.CENTER);
			label.setLayoutData(new GridData(GridData.FILL_BOTH));
			label.setFont(new Font(parentShell.getDisplay(), "Sans", 10, SWT.NORMAL));
			label.setText("Copyright © 2016 American Printing House for the Blind Inc.");

			//TODO:  LibLouis copyright?
//			label = new Label(dialog, SWT.CENTER);
//			label.setLayoutData(new GridData(GridData.FILL_BOTH));
//			label.setFont(new Font(parentShell.getDisplay(), "Sans", 10, SWT.NORMAL));
//			label.setText("Copyright © 2016 American Printing House for the Blind Inc.");

			dialog.pack();
			dialog.open();
			while(!dialog.isDisposed())
				if(!dialog.getDisplay().readAndDispatch())
					dialog.getDisplay().sleep();
		}
	}
	
	private final class ShowLog extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			textTranslate.setText(Log.getMessageString());
		}
	}

	private static class BaseAction implements SelectionListener
	{
		protected MenuItem menuItem;
		protected ToolItem toolItem;
		protected boolean enabled;

		void addToMenuAndToolBar(Menu menu, ToolBar toolBar, String tag, int accelerator, String iconFileName, boolean enabled)
		{
			this.enabled = enabled;

			menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(tag);
			if(accelerator != 0)
				menuItem.setAccelerator(accelerator);
			menuItem.addSelectionListener(this);
			menuItem.setEnabled(enabled);

			toolItem = new ToolItem(toolBar, SWT.PUSH);
			toolItem.setImage(new Image(toolBar.getParent().getShell().getDisplay(), getClass().getResourceAsStream("/images/" + iconFileName)));
			toolItem.setToolTipText(tag);
			toolItem.addSelectionListener(this);
			toolItem.setEnabled(enabled);
		}

		void addToMenu(Menu menu, String tag, int accelerator, boolean enabled)
		{
			this.enabled = enabled;

			menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(tag);
			if(accelerator != 0)
				menuItem.setAccelerator(accelerator);
			menuItem.addSelectionListener(this);
			menuItem.setEnabled(enabled);
		}

		void setEnabled(boolean enabled)
		{
			this.enabled = enabled;
			if(menuItem != null)
				menuItem.setEnabled(enabled);
			if(toolItem != null)
				toolItem.setEnabled(enabled);
		}

		boolean isEnabled()
		{
			return enabled;
		}

		@Override
		public void widgetSelected(SelectionEvent ignored){}

		@Override
		public void widgetDefaultSelected(SelectionEvent ignored){}
	}
}
