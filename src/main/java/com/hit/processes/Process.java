package com.hit.processes;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.memoryunits.Page;
import com.hit.util.MMULogger;

/**
 * @author Daniel Altalat
 */
public class Process implements Callable<Boolean>

{
	private int						id;
	private MemoryManagementUnit		mmu;
	private ProcessCycles			processCycles;

	/**
	 * This constructor represents a process constructor, which gets 3 configure parameters to simulate real process
	 * @param id
	 *            of the process
	 * @param mmu
	 *            reference to the MMU object
	 * @param processCycles
	 *            process cycles configuration
	 */
	public Process(int id, MemoryManagementUnit mmu, ProcessCycles processCycles)
	{
		this.setId(id);
		this.setMmu(mmu);
		this.setProcessCycles(processCycles);
	}

	/**
	 * The process business logic method
	 * The method makes each of the process cycle in 'processCycles' to request its pages, update them and then
	 * go to sleep.
	 * their common source is the MMU, which sync the RAM and the HardDisk.
	 * The page request and receive and the sleeping time defined as one cycle
	 */
	@Override
	public Boolean call() throws Exception
	{
		try
		{
			for (ProcessCycle cycle : this.processCycles.getProcessCycles())
			{
				Object pagesObject[] = cycle.getPages().toArray();
				Long[] pagesIds = Arrays.copyOf(pagesObject, pagesObject.length, Long[].class);
				Page<byte[]>[] pages = this.mmu.getPages(pagesIds);
				for (int i = 0; i < pages.length; i++)
				{
					pages[i].setContent(cycle.getData().get(i));
					MMULogger.getInstance().write("GP:P" + this.getId() + " " + pages[i] + " " + Arrays.toString(pages[i].getContent()), Level.INFO);
				}
				Thread.sleep(cycle.getSleepMs());
			}
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

	/* ~~~~~~~~~~~~~~~ Setter & Getters ~~~~~~~~~~~~~~~ */

	/**
	 * @return the id of the process
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            the id of the process
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * @return the mmu
	 */
	public MemoryManagementUnit getMmu()
	{
		return mmu;
	}

	/**
	 * @param mmu
	 *            the mmu to set
	 */
	public void setMmu(MemoryManagementUnit mmu)
	{
		this.mmu = mmu;
	}

	/**
	 * @return the processCycles
	 */
	public ProcessCycles getProcessCycles()
	{
		return processCycles;
	}

	/**
	 * @param processCycles
	 *            the processCycles to set
	 */
	public void setProcessCycles(ProcessCycles processCycles)
	{
		this.processCycles = processCycles;
	}

}
