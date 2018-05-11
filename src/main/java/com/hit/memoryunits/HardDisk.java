package com.hit.memoryunits;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.hit.util.MMULogger;

import java.util.Set;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class represents the Hard Disk component, by software
 * @author Daniel Altalat
 */
public class HardDisk
{

	public static final String			DEFAULT_FILE_NAME	= "src/main/resources/com/hit/HardDisk/HardDisk_file";			 // TODO add a file
	private static int					_SIZE				= 1000;
	public HashMap<Long,Page<byte[]>>	fileData;
	private static final HardDisk		instance				= new HardDisk();	 // Singleton with static factory

	private HardDisk()
	{
		fileData = new HashMap<>();
		for (Long i = 0L; i < _SIZE; i++)
		{
			fileData.put(i, new Page<byte[]>(i, i.toString().getBytes()));
		}
		try
		{
			writeToHardDisk();
		} catch (IOException e)
		{
			MMULogger.getInstance().write(e.getMessage(), Level.SEVERE);
			e.printStackTrace();
		}
	}

	public static HardDisk getInstance() throws IOException
	{
		return instance;
	}

	/**
	 * This method is called when a page is not in fast memory (RAM)
	 * @param pageId
	 *            given pageId
	 * @return the page with the given pageId
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Page<byte[]> pageFault(Long pageId) throws FileNotFoundException, IOException
	{
		readFromHardDisk();
		Page<byte[]> returnPage = fileData.get(pageId);

		return returnPage;
	}

	/**
	 * This method is called when a page is not in the RAM and the RAM is full
	 * @param moveToHdPage
	 *            page which should be moved to HD
	 * @param moveToRamId
	 *            page id of the pages which should be moved to RAM
	 * @return the page with the given pageId
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Page<byte[]> pageReplacement(Page<byte[]> moveToHdPage, Long moveToRamId) throws FileNotFoundException, IOException
	{
		readFromHardDisk();
		fileData.put(moveToHdPage.getPageId(), moveToHdPage);
		Page<byte[]> currentPage = fileData.get(moveToRamId);
		writeToHardDisk();

		return currentPage;
	}

	@SuppressWarnings("unchecked")
	public void readFromHardDisk() throws FileNotFoundException, IOException
	{
		FileInputStream fileInput = new FileInputStream(DEFAULT_FILE_NAME);
		ObjectInputStream hardDiskInputFile = new ObjectInputStream(fileInput);
		try
		{
			fileData = (HashMap<Long,Page<byte[]>>)hardDiskInputFile.readObject();
		} catch (ClassNotFoundException e)
		{
			MMULogger.getInstance().write(e.getMessage(), Level.SEVERE);
		} finally
		{
			fileInput.close();
			hardDiskInputFile.close();
		}
	}

	public void writeToHardDisk() throws FileNotFoundException, IOException
	{
		FileOutputStream hardDiskFile = null;
		ObjectOutputStream writeData = null;
		try
		{
			hardDiskFile = new FileOutputStream(DEFAULT_FILE_NAME);
			writeData = new ObjectOutputStream(hardDiskFile);
			writeData.writeObject(fileData);
			writeData.flush();
		} catch (FileNotFoundException e)
		{
			MMULogger.getInstance().write(e.getMessage(), Level.SEVERE);
		} finally
		{
			writeData.close();
			hardDiskFile.close();
		}
	}

	/**
	 * The method saves all the pages from the given list to the Hard Disk
	 * @param toSave
	 *            all the pages that we need to save
	 * @throws IOException
	 */
	public void saveAll(Map<Long,Page<byte[]>> toSave) throws IOException
	{
		Set<Entry<Long,Page<byte[]>>> entries = toSave.entrySet();
		for (Entry<Long,Page<byte[]>> entry : entries)
		{
			fileData.remove(entry.getKey());
			fileData.put(entry.getKey(), entry.getValue()); // update the page content
		}
		writeToHardDisk(); // update the Hard Disk file
	}

}
