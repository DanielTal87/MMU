package com.hit.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;

import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import com.hit.util.MMULogger;
import com.hit.controller.MMUController;

import javax.swing.*;

public class MMUView extends Observable implements View, MouseListener, ActionListener
{

	public static final int				BYTES_IN_PAGE			= 5;
	private Integer						currentLine				= 0;
	private Integer						pageSize					= BYTES_IN_PAGE;
	private Integer						processesNumber			= 0;
	private int							ramCapacity				= 0;
	private Integer						pageFaultCounter			= 0;
	private Integer						pageReplacmentCounter	= 0;
	private JFrame						frame					= new JFrame();
	private JTable						table;
	private JButton						play, playAll, reset;
	private DefaultTableModel			tableModel;
	private JTextField					pageFaultAmountTextField;
	private JTextField					pageReplacmentAmountTextField;
	private JTextArea					processesTextArea;
	private Map<String,String>			processesSelected		= new HashMap<>();
	private Map<String,List<String>>		pagesInRam				= new HashMap<>();
	private LogEvent[]					logEvents;
	private JTextArea					dataTextArea;
	MMUController						controller;

	public MMUView()
	{}

	public void createAndShowGUI()
	{
		// Ask for window decorations provided by the look and feel.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("MMU Simulator");

		// Set the border layout
		frame.setLayout(new BorderLayout(10, 10));

		// Setting the frame size
		frame.setPreferredSize(new Dimension(700, 500));

		// Set the start position
		frame.pack();
		frame.setLocationRelativeTo(null);

		// Set the background color
		frame.getContentPane().setBackground(new Color(238, 255, 255));

		// Set the frame icon to an image loaded from a file.
		frame.setIconImage(new ImageIcon("docs/daniel_icon.jpg").getImage());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Disable resize
		frame.setResizable(false);

		currentLine = 0;
		pageFaultCounter = 0;
		pageReplacmentCounter = 0;
		JPanel northPanel = InitialTable();
		JPanel westPanel = InitialTextArea();
		JPanel eastPanel = InitialTextFields();
		JPanel southPanel = InitialButtons();
		JPanel centerPanel = InitialScrolPane();

		frame.add(northPanel, BorderLayout.NORTH);
		frame.add(southPanel, BorderLayout.SOUTH);
		frame.add(westPanel, BorderLayout.WEST);
		frame.add(eastPanel, BorderLayout.EAST);
		frame.add(centerPanel, BorderLayout.CENTER);

		// Display the window.
		frame.setVisible(true);

	}

	public void setConfiguration(LogEvent[] arg)
	{
		this.logEvents = arg;
		setRamCapacity(arg);
		setProcessesNumber(arg);
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public int getPageSize()
	{
		return BYTES_IN_PAGE;
	}

	public int getRamCapacity()
	{
		return ramCapacity;
	}

	public void setRamCapacity(LogEvent[] arg)
	{
		this.ramCapacity = arg[0].getRamCapacity();
		currentLine++;
	}

	public int getProcessesNumber()
	{
		return processesNumber;
	}

	public void setProcessesNumber(LogEvent[] arg)
	{
		this.processesNumber = arg[1].getProcessesNumber();
		currentLine++;
	}

	private JPanel InitialTextArea()
	{
		processesTextArea = new JTextArea(2, 7);
		processesTextArea.setBackground(new Color(238, 255, 255));
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new BorderLayout());
		frame.add(westPanel);
		westPanel.setBackground(null);
		JLabel lb3 = new JLabel("Processes", SwingConstants.CENTER);
		lb3.setFont(new Font("Avenir next", Font.BOLD, 16));
		String newLine = System.getProperty("line.separator");
		StringBuilder processStr = new StringBuilder();
		for (int i = 1; i < processesNumber + 1; i++)
		{
			processStr.append("     process " + i + newLine);
		}

		processesTextArea.setLineWrap(true);
		processesTextArea.setText(processStr.toString());
		processesTextArea.setFont(new Font("Helvetica Neue", Font.LAYOUT_LEFT_TO_RIGHT, 14));
		processesTextArea.addMouseListener(this);
		westPanel.setLayout(new BorderLayout(5, 5));
		westPanel.add(lb3, BorderLayout.NORTH);
		westPanel.add(processesTextArea, BorderLayout.WEST);

		return westPanel;
	}

	private JPanel InitialTextFields()
	{
		JPanel eastPanel = new JPanel();
		JPanel insidePanel = new JPanel();
		JPanel insidePanel2 = new JPanel();
		frame.add(eastPanel);
		frame.add(insidePanel);
		frame.add(insidePanel2);
		eastPanel.setBackground(null);
		insidePanel.setBackground(null);
		insidePanel2.setBackground(null);
		JLabel lb1 = new JLabel("Page Fault Amount");
		lb1.setFont(new Font("Avenir next", Font.LAYOUT_LEFT_TO_RIGHT, 15));
		// lb1.setForeground(Color.black);
		pageFaultAmountTextField = new JTextField();
		pageFaultAmountTextField.setForeground(Color.black);
		pageFaultAmountTextField.setText(pageFaultCounter.toString() + "   ");
		JLabel lb2 = new JLabel("Page Replacment Amount");
		lb2.setFont(new Font("Avenir next", Font.LAYOUT_LEFT_TO_RIGHT, 15));
		pageReplacmentAmountTextField = new JTextField();
		pageReplacmentAmountTextField.setForeground(Color.black);
		pageReplacmentAmountTextField.setText(pageReplacmentCounter.toString() + "   ");
		insidePanel.add(lb1);
		insidePanel.add(pageFaultAmountTextField);
		insidePanel2.add(lb2);
		insidePanel2.add(pageReplacmentAmountTextField);
		eastPanel.setLayout(new BorderLayout());
		eastPanel.add(insidePanel, BorderLayout.NORTH);
		eastPanel.add(insidePanel2, BorderLayout.EAST);
		return eastPanel;
	}

	private JPanel InitialTable()
	{
		JPanel northPanel = new JPanel();
		frame.add(northPanel);
		JLabel tableLabel = new JLabel("MMU RAM Analyzer", SwingConstants.CENTER);
		JPanel tabelPanel = new JPanel();
		frame.add(tabelPanel);

		tabelPanel.setBackground(new Color(238, 255, 255));// .setBackground(null);
		tableLabel.setForeground(Color.black);
		tableLabel.setFont(new Font("Avenir next", Font.BOLD, 40));
		tabelPanel.setLayout(new BorderLayout());
		tabelPanel.add(tableLabel, BorderLayout.NORTH);
		northPanel.setBackground(null);
		tableModel = new DefaultTableModel(pageSize + 1, ramCapacity);
		table = new JTable(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setTableHeader(null);

		for (int i = 0; i < ramCapacity; i++)
		{
			TableColumnModel colModel = table.getColumnModel();
			TableColumn col = colModel.getColumn(i);
			col.setPreferredWidth(70);
		}

		for (int j = 0; j < ramCapacity; j++)
		{
			table.setValueAt("Page: ", 0, j);
		}

		for (int i = 1; i < pageSize + 1; i++)
		{
			for (int j = 0; j < ramCapacity; j++)
			{
				table.setValueAt(0, i, j);
			}
		}

		Border border = BorderFactory.createLineBorder(Color.GRAY);
		table.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8, 8, 8, 8)));
		table.setFont(new Font("Helvetica Neue", Font.LAYOUT_LEFT_TO_RIGHT, 12));
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(70 * 7, 115));
		tabelPanel.add(sp, BorderLayout.CENTER);
		northPanel.add(tabelPanel);

		return northPanel;
	}

	private JPanel InitialButtons()
	{
		JPanel southPanel = new JPanel();
		frame.add(southPanel);
		southPanel.setBackground(null);

		play = new JButton("Play");
		playAll = new JButton("Play All");
		reset = new JButton("Reset");

		play.addActionListener(this);
		playAll.addActionListener(this);
		reset.addActionListener(this);

		play.addMouseListener(this);
		playAll.addMouseListener(this);
		reset.addMouseListener(this);

		southPanel.add(play);
		southPanel.add(playAll);
		southPanel.add(reset);

		return southPanel;
	}

	private JPanel InitialScrolPane()
	{
		JPanel centerPanel = new JPanel();
		frame.add(centerPanel);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.setBackground(Color.LIGHT_GRAY);
		dataTextArea = new JTextArea();
		String listString = new String();
		String newLine = System.getProperty("line.separator");
		ArrayList<String> logLines = new ArrayList<>();
		Scanner logScanner = null;
		try
		{
			logScanner = new Scanner(new BufferedReader(new FileReader(MMULogger.LOG_FILE_NAME)));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		while (logScanner.hasNext())
		{
			logLines.add(logScanner.nextLine());
		}
		logScanner.close();
		for (String s : logLines)
		{
			listString += s + newLine;
		}

		dataTextArea.setText(listString);
		dataTextArea.setFont(new Font("Avenir next", Font.LAYOUT_LEFT_TO_RIGHT, 14));
		JScrollPane scrolPane = new JScrollPane(dataTextArea);
		centerPanel.add(scrolPane);
		return centerPanel;
	}

	private boolean CheckSelctionProcesses()
	{
		boolean goodSelection = true;
		String newLine = System.getProperty("line.separator");
		String[] parts = processesTextArea.getSelectedText().split(newLine);
		if (!processesTextArea.getSelectedText().contains("process"))// .contains("process"))// .startsWith(" process"))
		{
			goodSelection = false;
		} else
		{
			for (String part : parts)
			{
				if (part.length() <= 7)
				{
					goodSelection = false;
				}
			}
		}

		return goodSelection;
	}

	private void WrongSelectionProcessMsg()
	{
		processesSelected.clear();
		processesTextArea.getHighlighter().removeAllHighlights();
		JOptionPane.showMessageDialog(frame, "Wrong selection of processes", "MMU-PROCESS-ERROR", JOptionPane.ERROR_MESSAGE);
	}

	public void ErrorMsg(String msg)
	{
		JOptionPane.showMessageDialog(frame, msg, "ERROR", JOptionPane.ERROR_MESSAGE);
	}

	private void InitialSelectedProcesses()
	{
		if (processesTextArea.getSelectedText() != null && CheckSelctionProcesses())
		{
			String newLine = System.getProperty("line.separator");
			String[] parts = processesTextArea.getSelectedText().split(newLine);
			try
			{
				int start = processesTextArea.getText().indexOf(processesTextArea.getSelectedText());
				int end = start + processesTextArea.getSelectedText().length();
				DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(null);
				processesTextArea.getHighlighter().removeAllHighlights();
				processesTextArea.getHighlighter().addHighlight(start, end, painter);
				processesSelected.clear();
				for (String part : parts)
				{
					processesSelected.put(part.substring(13).replace("\r", ""), part.substring(13));
				}
			} catch (Exception e)
			{
				WrongSelectionProcessMsg();
			}
		} else
			if (processesTextArea.getSelectedText() != null)
			{
				WrongSelectionProcessMsg();
			}
	}

	synchronized void SynchronizeViewWithProcessesSelected(String ProcessID, String PageID, List<String> contantOfPage)
	{
		for (int i = 0; i < ramCapacity; i++)
		{
			if (!pagesInRam.containsKey(((String)table.getValueAt(0, i)).substring(6)))
			{
				table.setValueAt("Page: ", 0, i);
				for (int j = 1; j < pageSize + 1; j++)
				{
					table.setValueAt(0, j, i);
				}
			}
		}

		if (processesSelected.containsKey(ProcessID))
		{
			boolean done = false;

			for (int i = 0; i < ramCapacity && !done; i++)
			{
				if (((String)table.getValueAt(0, i)).substring(6).equals(PageID))
				{
					int j = 1;
					done = true;
					for (String value : contantOfPage)
					{
						table.setValueAt(value, j, i);
						j++;
					}
				}
			}

			for (int i = 0; i < ramCapacity && !done; i++)
			{
				if (!pagesInRam.containsKey(((String)table.getValueAt(0, i)).substring(6)))
				{
					table.setValueAt("Page: " + PageID, 0, i);
					int j = 1;
					done = true;
					for (String value : contantOfPage)
					{
						table.setValueAt(value, j, i);
						j++;
					}
				}
			}
		}
	}

	private Boolean CheckIfPageFaultAndExcecuteAction()
	{
		Boolean isPageFault = false;
		if (logEvents[currentLine].getLogEvent().equals(LogEvent.LOG_EVENT.PAGE_FAULT))
		{
			isPageFault = true;
			pageFaultCounter++;
			pageFaultAmountTextField.setText("  " + pageFaultCounter.toString());
			pagesInRam.put(logEvents[currentLine].getPageId().toString(), null);
		}

		return isPageFault;
	}

	private Boolean CheckIfPageReplacmentAndExcecuteAction()
	{
		Boolean isPageReplacment = false;
		if (logEvents[currentLine].getLogEvent().equals(LogEvent.LOG_EVENT.PAGE_REPLACEMENT))
		{
			isPageReplacment = true;
			pageReplacmentCounter++;
			pageReplacmentAmountTextField.setText("  " + pageReplacmentCounter.toString());
			pagesInRam.remove(logEvents[currentLine].getMth().toString());
			pagesInRam.put(logEvents[currentLine].getMtr().toString(), null);
		}

		return isPageReplacment;
	}

	private Boolean CheckIfGetPagesAndExcecuteAction()
	{
		Boolean isGetPages = false;
		if (logEvents[currentLine].getLogEvent().equals(LogEvent.LOG_EVENT.GET_PAGES))
		{
			isGetPages = true;

			String pageID = logEvents[currentLine].getData()[1];
			String processID = logEvents[currentLine].getData()[0].substring(4);
			List<String> contantOfPage = new ArrayList<String>(pageSize);

			for (int i = 2; i < 7; i++)
			{
				contantOfPage.add(logEvents[currentLine].getData()[i].toString());
			}

			SynchronizeViewWithProcessesSelected(processID, pageID, contantOfPage);
		}

		return isGetPages;
	}

	private void MarkCurrentLine()
	{
		int start = 0;
		int end = 0;
		try
		{
			start = dataTextArea.getLineStartOffset(currentLine);
			end = dataTextArea.getLineEndOffset(currentLine);

		} catch (BadLocationException e1)
		{
			ErrorMsg(e1.getMessage());
		}
		DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);
		try
		{
			dataTextArea.getHighlighter().removeAllHighlights();
			dataTextArea.getHighlighter().addHighlight(start, end, painter);
			dataTextArea.moveCaretPosition(start);
		} catch (BadLocationException e)
		{
			ErrorMsg(e.getMessage());
		}
	}

	private void InitialSystem()
	{
		currentLine = 0;
		pageFaultCounter = 0;
		pageFaultAmountTextField.setText("  " + pageFaultCounter.toString());
		pageReplacmentCounter = 0;
		pageReplacmentAmountTextField.setText("  " + pageReplacmentCounter.toString());
		pagesInRam.clear();
		dataTextArea.getHighlighter().removeAllHighlights();
		processesSelected.clear();
		processesTextArea.getHighlighter().removeAllHighlights();
		StringBuilder processStr = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		for (int i = 1; i < processesNumber + 1; i++)
		{
			processStr.append("     process " + i + newLine);
		}
		processesTextArea.setText(processStr.toString());

		for (int j = 0; j < ramCapacity; j++)
		{
			table.setValueAt("Page: ", 0, j);
		}

		for (int i = 1; i < pageSize + 1; i++)
		{
			for (int j = 0; j < ramCapacity; j++)
			{
				table.setValueAt(0, i, j);
			}
		}

		play.setEnabled(true);
		playAll.setEnabled(true);
		reset.setEnabled(true);
		ReadNextLine();
	}

	public boolean ReadNextLine()
	{
		boolean readNextLine = true;
		if (currentLine != logEvents.length)
		{
			readNextLine = true;
			if (CheckIfPageFaultAndExcecuteAction())
			{} else
				if (CheckIfPageReplacmentAndExcecuteAction())
				{} else
					if (CheckIfGetPagesAndExcecuteAction())
					{}
			MarkCurrentLine();
			currentLine++;
		} else
			if (currentLine == logEvents.length)
			{
				readNextLine = false;
				int dialogButton = JOptionPane.YES_NO_OPTION;
				int dialogResult = JOptionPane.showConfirmDialog(null, "Would you like to try again?", "The simulation is over!!!", dialogButton);
				if (dialogResult == 0)
				{
					InitialSystem();
				} else
				{
					play.setEnabled(false);
					playAll.setEnabled(false);
				}
			}

		return readNextLine;
	}

	@Override
	public void actionPerformed(ActionEvent act)
	{
		if (act.getSource() == play)
		{
			ReadNextLine();
		} else
			if (act.getSource() == playAll)
			{
				while (ReadNextLine());
			} else
				if (act.getSource() == reset)
				{
					InitialSystem();
				}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{}

	@Override
	public void mouseEntered(MouseEvent e)
	{}

	@Override
	public void mouseExited(MouseEvent e)
	{}

	@Override
	public void mousePressed(MouseEvent e)
	{}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		InitialSelectedProcesses();
	}

	@Override
	public void start()
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				createAndShowGUI();

			}
		});

	}
}
