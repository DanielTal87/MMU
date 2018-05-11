package com.hit.processes;

import java.util.List;

/**
 * This class represents the configuration of all threads, it contains list of ProcessCycles that's associated with each thread
 * @author Daniel Altalat
 */
public class RunConfiguration
{
	private List<ProcessCycles> processesCycles;

	public RunConfiguration(List<ProcessCycles> processesCycles)
	{
		super();
		this.processesCycles = processesCycles;
	}

	@Override
	public String toString()
	{
		return processesCycles.toArray().toString();
	}

	/* ~~~~~~~~~~~~~~~ Setter & Getters ~~~~~~~~~~~~~~~ */

	/**
	 * @return the processesCycles
	 */
	public List<ProcessCycles> getProcessCycles()
	{
		return processesCycles;
	}

	/**
	 * @param processesCycles
	 *            the processesCycles to set
	 */
	public void setProcessCycles(List<ProcessCycles> processesCycles)
	{
		this.processesCycles = processesCycles;
	}

}
