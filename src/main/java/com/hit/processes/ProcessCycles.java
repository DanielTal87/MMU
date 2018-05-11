package com.hit.processes;

import java.util.List;

/**
 * This class represents container of Process Cycles, which associated to each thread
 * @author Daniel Altalat
 */
public class ProcessCycles
{
	private List<ProcessCycle> processCycles;

	public ProcessCycles(List<ProcessCycle> processCycles)
	{
		this.processCycles = processCycles;
	}

	public List<ProcessCycle> getProcessCycles()
	{
		return processCycles;
	}

	public void setProcessCycles(List<ProcessCycle> processCycles)
	{
		this.processCycles = processCycles;
	}

	@Override
	public String toString()
	{
		return processCycles.toArray().toString();
	}
}
