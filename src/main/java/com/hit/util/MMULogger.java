package com.hit.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Daniel Altalat
 */
public class MMULogger
{
	public final static String	LOG_FILE_NAME	= "logs/log.txt";
	private FileHandler			handler;
	private static MMULogger		instance			= null;

	private MMULogger()
	{
		try
		{
			handler = new FileHandler(LOG_FILE_NAME);
			handler.setFormatter(new OnlyMessageFormatter());
		} catch (IOException | SecurityException e)
		{
			System.err.println("Log file Error");
			e.printStackTrace();
		}
	}

	/**
	 * Taking care for singleton pattern
	 * @return instance
	 */
	public static MMULogger getInstance()
	{
		if (instance == null)
			instance = new MMULogger();
		return instance;
	}

	public synchronized void write(String command, Level level)
	{
		LogRecord record = new LogRecord(level, command);
		handler.publish(record);
	}

	class OnlyMessageFormatter extends Formatter
	{
		public OnlyMessageFormatter()
		{
			super();
		}

		@Override
		public String format(LogRecord record)
		{
			return record.getMessage() + "\r\n";
		}
	}

	public void close()
	{
		handler.close();
	}
}
