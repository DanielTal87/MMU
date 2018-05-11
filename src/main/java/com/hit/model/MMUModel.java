package com.hit.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import com.hit.algorithm.*;
import com.hit.driver.MMUDriver;
import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.processes.Process;
import com.hit.processes.ProcessCycles;
import com.hit.processes.RunConfiguration;
import com.hit.util.MMULogger;
import com.hit.view.LogEvent;

public class MMUModel extends Observable implements Model
{
	public int				numProcesses;
	public int				ramCapacity;
	private List<String>		configuration;

	public MMUModel()
	{}

	@Override
	public void start()
	{
		// Initialize the ram capacity
		int capacity = Integer.parseInt(getConfiguration().get(1));
		MMULogger.getInstance().write("RC:" + capacity, Level.INFO);

		// Initialize the algorithm type
		IAlgoCache<Long,Long> algorithm = algorithmCheck(getConfiguration().get(0), capacity);
		System.out.println("Processing...");

		MemoryManagementUnit mmu = null;
		try
		{
			mmu = new MemoryManagementUnit(capacity, algorithm);
		} catch (IOException e)
		{
			MMULogger.getInstance().write(e.getMessage(), Level.SEVERE);
			e.printStackTrace();
		}
		RunConfiguration runConfig = MMUDriver.readConfigurationFile();
		List<ProcessCycles> processCyclesList = runConfig.getProcessCycles();
		List<Process> processes = MMUDriver.createProcesses(processCyclesList, mmu);
		MMULogger.getInstance().write("PN:" + processes.size(), Level.INFO);
		try
		{
			MMUDriver.runProcesses(processes);
		} catch (InterruptedException | ExecutionException e)
		{
			MMULogger.getInstance().write(e.getMessage(), Level.SEVERE);
			e.printStackTrace();
		}
		System.out.println("Thank You");

		// Wait one second for MMULogger finishes writing to log file
		try
		{
			Thread.sleep(1000);
		} catch (Exception e)
		{
			MMULogger.getInstance().write(e.getMessage(), Level.SEVERE);
			System.out.println(e.getMessage());
		}

		// Read the log file
		ArrayList<String> logLines = new ArrayList<>();
		try
		{
			Scanner logScanner = new Scanner(new BufferedReader(new FileReader(MMULogger.LOG_FILE_NAME)));
			while (logScanner.hasNext())
			{
				logLines.add(logScanner.nextLine());
			}
			logScanner.close();
		} catch (FileNotFoundException e)
		{
			System.err.println("Log file don't found");
			MMULogger.getInstance().write(e.getMessage(), Level.SEVERE);
			logLines = null;
		}

		LogEvent[] logEvents = new LogEvent[logLines.size()];

		logEvents[0] = new LogEvent();
		logEvents[0].setLogEvent(LogEvent.LOG_EVENT.RAM_CAPACITY);
		logEvents[0].setRamCapacity(Integer.parseInt(logLines.get(0).substring(3)));

		logEvents[1] = new LogEvent();
		logEvents[1].setLogEvent(LogEvent.LOG_EVENT.PROCESSES_NUMBER);
		logEvents[1].setProcessesNumber(Integer.parseInt(logLines.get(1).substring(3)));

		for (int i = 2; i < logLines.size(); i++)
		{
			logEvents[i] = analyzeLogLine(logLines.get(i));
		}

		setChanged();
		notifyObservers(logEvents);
		MMULogger.getInstance().close();
	}

	public LogEvent analyzeLogLine(String logLine)
	{
		LogEvent logEvent = new LogEvent();

		if (logLine.contains("PF"))
		{
			logEvent.setLogEvent(LogEvent.LOG_EVENT.PAGE_FAULT);
			logEvent.setPageId(Long.parseLong(logLine.substring(3)));
			return logEvent;
		}

		else
			if (logLine.contains("PR"))
			{
				logEvent.setLogEvent(LogEvent.LOG_EVENT.PAGE_REPLACEMENT);
				logEvent.setMth(Long.parseLong(logLine.substring(7, logLine.indexOf(" ", 7))));
				logEvent.setMtr(Long.parseLong(logLine.substring(logLine.indexOf("MTR ") + "MTR ".length())));
				return logEvent;
			}

			else
				if (logLine.contains("GP"))
				{
					String[] splitedLogLine = logLine.split(" ");
					logEvent.setLogEvent(LogEvent.LOG_EVENT.GET_PAGES);

					int currProcessId = Integer.parseInt(splitedLogLine[0].substring(4));
					logEvent.setProcessId(currProcessId);

					Long currPageId = Long.parseLong(splitedLogLine[1]);
					logEvent.setPageId(currPageId);

					splitedLogLine[2] = splitedLogLine[2].replace("[", ""); // first byte
					splitedLogLine[2] = splitedLogLine[2].replace(",", "");

					for (int i = 3; i < splitedLogLine.length - 1; i++)
						splitedLogLine[i] = splitedLogLine[i].replace(",", "");

					splitedLogLine[splitedLogLine.length - 1] = splitedLogLine[splitedLogLine.length - 1].replace("]", ""); // last byte

					logEvent.setData(splitedLogLine);

					return logEvent;
				}

		return logEvent;
	}

	/**
	 * @param algorithmType
	 * @param capacity
	 * @return algorithm The algorithm that was selected in the given capacity
	 */
	private static IAlgoCache<Long,Long> algorithmCheck(String algorithmType, Integer capacity)
	{
		IAlgoCache<Long,Long> algorithm = null;

		switch (algorithmType)
		{
			case "lru":
				algorithm = new LRUAlgoCacheImpl<Long,Long>(capacity);
				break;
			case "nfu":
				algorithm = new NFUAlgoCacheImpl<Long,Long>(capacity);
				break;
			case "random":
				algorithm = new RandomAlgoCacheImpl<Long,Long>(capacity);
		}

		return algorithm;
	}

	/* ~~~~~~~~~~~~~~~ Setter & Getters ~~~~~~~~~~~~~~~ */

	/**
	 * @return the configuration
	 */
	public List<String> getConfiguration()
	{
		return this.configuration;
	}

	/**
	 * @param configuration
	 *            the configuration to set
	 */
	public void setConfiguration(List<String> configuration)
	{
		this.configuration = configuration;
	}
}
