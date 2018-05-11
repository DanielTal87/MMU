package com.hit.memoryunits;

import java.util.HashMap;
import java.util.Map;

/**
 * Random Access Memory
 * @author Daniel Altalat
 */
public class RAM
{
	private int						capacity;
	private Map<Long,Page<byte[]>>	pagesMap;

	public RAM(int initialCapacity)
	{
		this.setCapacity(initialCapacity);
		pagesMap = new HashMap<Long,Page<byte[]>>(initialCapacity);
	}

	public Map<Long,Page<byte[]>> getPages()
	{
		return pagesMap;
	}

	public void addPage(Page<byte[]> addPage)
	{
		this.pagesMap.put(addPage.getPageId(), addPage);
	}

	public void addPages(Page<byte[]>[] addPages)
	{
		for (int i = 0; i < addPages.length; i++)
			this.pagesMap.put(addPages[i].getPageId(), addPages[i]);
	}

	public void removePage(Page<byte[]> removePage)
	{
		this.pagesMap.remove(removePage.getPageId());
	}

	public void removePages(Page<byte[]>[] removePages)
	{
		for (int i = 0; i < removePages.length; i++)
		{
			this.pagesMap.remove(removePages[i].getPageId());
		}
	}

	public Page<byte[]> getPage(Long pageId)
	{
		return this.pagesMap.get(pageId);
	}

	public Page<byte[]>[] getPages(Long[] pageIds)
	{
		@SuppressWarnings("unchecked")
		Page<byte[]>[] returnPages = new Page[pageIds.length];
		for (int i = 0; i < pageIds.length; i++)
		{
			returnPages[i] = getPage(pageIds[i]);
		}
		return returnPages;
	}

	public void setPages(Map<Long,Page<byte[]>> pages)
	{
		pagesMap = pages;
	}

	public boolean isFull()
	{
		return pagesMap.size() >= this.getCapacity();
	}

	public int size()
	{
		return this.pagesMap.size();
	}

	/* ~~~~~~~~~~~~~~~ Setter & Getters ~~~~~~~~~~~~~~~ */

	public int getCapacity()
	{
		return capacity;
	}

	public void setCapacity(int initialCapacity)
	{
		this.capacity = initialCapacity;
	}

}
