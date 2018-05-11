package com.hit.memoryunits;

import com.hit.algorithm.IAlgoCache;
import org.junit.Assert;
import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.hit.algorithm.LRUAlgoCacheImpl;

public class MemoryManagementUnitTest
{
	@Test
	public void testMMU() throws FileNotFoundException, IOException, ClassNotFoundException
	{
		System.out.println("Test the MMU part");

		writeToFile();

		Long[] pagesToGet = new Long[1];
		pagesToGet[0] = (long)1;
		IAlgoCache<Long,Long> lru = new LRUAlgoCacheImpl<>(1);
		MemoryManagementUnit mmu = new MemoryManagementUnit(1, lru);
		Page<byte[]>[] returnedPages = mmu.getPages(pagesToGet);
		Assert.assertEquals((long)1, (long)returnedPages[0].getPageId());

		pagesToGet[0] = (long)2;
		returnedPages = mmu.getPages(pagesToGet);
		Assert.assertEquals((long)2, (long)returnedPages[0].getPageId());

	}

	public Boolean writeToFile() throws FileNotFoundException, IOException
	{
		HashMap<Long,Page<byte[]>> pagesInDisk = new HashMap<>();

		byte[] b1 = "Content example1".getBytes();
		Page<byte[]> p1 = new Page<byte[]>((long)1, b1);
		pagesInDisk.put(p1.getPageId(), p1);

		byte[] b2 = "Content example2".getBytes();
		Page<byte[]> p2 = new Page<byte[]>((long)2, b2);
		pagesInDisk.put(p2.getPageId(), p2);

		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("src/main/resources/com/hit/HardDisk/HardDisk_file"));

		try
		{
			out.writeObject(pagesInDisk);
			out.flush();
		} catch (Exception e)
		{
			return false;
		} finally
		{
			out.close();
		}
		return true;
	}

}
