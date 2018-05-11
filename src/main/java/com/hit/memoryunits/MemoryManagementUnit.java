package com.hit.memoryunits;

import com.hit.algorithm.IAlgoCache;
import com.hit.util.MMULogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

/**
 * This class represents the MMU - Hardware device that maps virtual to physical address by software
 * @author Daniel Altalat
 */
public class MemoryManagementUnit
{
	private IAlgoCache<Long,Long>	algorithm;
	private RAM						ram;

	/**
	 * @param ramCapacity
	 * @param algorithm
	 * @throws IOException
	 */
	public MemoryManagementUnit(int ramCapacity, IAlgoCache<Long,Long> algorithm) throws IOException
	{
		setAlgorithm(algorithm);
		setRam(new RAM(ramCapacity));
	}

	/**
	 * This method is the main method which returns array of pages that are requested from the user
	 * @param pageIds
	 *            array of page IDs
	 * @return returns array of pages that are requested from the user
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public synchronized Page<byte[]>[] getPages(Long[] pageIds) throws FileNotFoundException, ClassNotFoundException, IOException
	{
		Page<byte[]>[] result = new Page[pageIds.length];

		for (int i = 0; i < pageIds.length; i++)
		{
			if (algorithm.getElement(pageIds[i]) == null)
			{
				if (!ram.isFull()) // The RAM is not full, adding page by Page Fault
				{
					algorithm.putElement(pageIds[i], pageIds[i]); // adding the missing pageId to the RAM
					result[i] = HardDisk.getInstance().pageFault(pageIds[i]);	 // adding the missing page to the RAM
					MMULogger.getInstance().write("PF:" + pageIds[i], Level.INFO);
					ram.addPage(result[i]);
				} else // The RAM is full, adding page by Page Replacement
				{
					Long pageIdToHardDisk = algorithm.putElement(pageIds[i], pageIds[i]); // adding the missing pageId to the RAM algorithm
					                                                                      // saving the id of the removed page to save on the HardDisk
					Page<byte[]> pageToHd = ram.getPage(pageIdToHardDisk); // getting the page
					ram.removePage(pageToHd);
					result[i] = HardDisk.getInstance().pageReplacement(pageToHd, pageIds[i]);
					MMULogger.getInstance().write("PR:MTH " + pageIdToHardDisk + " " + "MTR " + pageIds[i], Level.INFO);
					ram.addPage(result[i]);
				}
			} else
			{
				result[i] = ram.getPage(algorithm.getElement(pageIds[i]));
			}
		}

		return result;
	}

	/**
	 * in the end of MMU put all page from ram to hard disk
	 * @throws Exception
	 */
	public void shutdown() throws Exception
	{
		HardDisk hardDisk = HardDisk.getInstance();
		Page<byte[]>[] pagesFromRam = ram.getPages(null);
		ram.removePages(pagesFromRam);
		for (Page<byte[]> page : pagesFromRam)
		{
			hardDisk.pageReplacement(page, null);
		}
	}

	/* ~~~~~~~~~~~~~~~ Setter & Getters ~~~~~~~~~~~~~~~ */

	public IAlgoCache<Long,Long> getAlgorithm()
	{
		return algorithm;
	}

	public void setAlgorithm(IAlgoCache<Long,Long> algorithm)
	{
		this.algorithm = algorithm;
	}

	public RAM getRam()
	{
		return ram;
	}

	public void setRam(RAM ram)
	{
		this.ram = ram;
	}

}
