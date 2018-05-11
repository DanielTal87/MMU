package com.hit.processes;

import java.util.List;

/**
 * This class represents ProcessCycle which is the life cycle of the process
 * @author Daniel Altalat
 */
public class ProcessCycle
{
	private List<Long>		pages;
	private int				sleepMs;
	private List<byte[]>		data;

	/**
	 * This constructor represents a Process Cycle constructor, which gets the relevant configuration to the process life cycle
	 * @param pages
	 *            to read/write to them
	 * @param sleepMs
	 *            MS to sleep
	 * @param data
	 *            data to write
	 */
	public ProcessCycle(List<Long> pages, int sleepMs, List<byte[]> data)
	{
		super();
		setPages(pages);
		setSleepMs(sleepMs);
		setData(data);
	}

	@Override
	public String toString()
	{
		StringBuilder message = new StringBuilder();
		message.append(getPages().toString());

		return message.toString();
	}

	/* ~~~~~~~~~~~~~~~ Setter & Getters ~~~~~~~~~~~~~~~ */

	/**
	 * @return the pages
	 */
	public List<Long> getPages()
	{
		return pages;
	}

	/**
	 * @param pages
	 *            the pages to set
	 */
	public void setPages(List<Long> pages)
	{
		this.pages = pages;
	}

	/**
	 * @return the sleepMs
	 */
	public int getSleepMs()
	{
		return sleepMs;
	}

	/**
	 * @param sleepMs
	 *            the sleepMs to set
	 */
	public void setSleepMs(int sleepMs)
	{
		this.sleepMs = sleepMs;
	}

	/**
	 * @return the data
	 */
	public List<byte[]> getData()
	{
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(List<byte[]> data)
	{
		this.data = data;
	}

}
