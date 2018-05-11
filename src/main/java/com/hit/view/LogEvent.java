package com.hit.view;

/**
 * this class is a data structure for all the needed information for each event of the MMU run log file.
 * @author Daniel Altalat
 */
public class LogEvent
{

	public enum LOG_EVENT
	{
		RAM_CAPACITY, PROCESSES_NUMBER, PAGE_FAULT, PAGE_REPLACEMENT, GET_PAGES, ERROR
	};

	private LOG_EVENT	logEvent;

	/* RAM_CAPACITY type variables */
	private int			ramCapacity;

	/* PROCESSES_NUMBER type variables */
	private int			processesNumber;

	/* PAGE_FAULT/GET_PAGES type variables */
	private Long		pageId;

	/* PAGE_REPLACEMENT type variables */
	private Long		mth;
	private Long		mtr;

	/* GET_PAGES type variables */
	private int			processId;
	private String[]		data;

	/* ~~~~~~~~~~~~~~~ Setter & Getters ~~~~~~~~~~~~~~~ */

	/**
	 * @return the logEvent
	 */
	public LOG_EVENT getLogEvent()
	{
		return logEvent;
	}

	/**
	 * @param logEvent
	 *            the logEvent to set
	 */
	public void setLogEvent(LOG_EVENT logEvent)
	{
		this.logEvent = logEvent;
	}

	/**
	 * @return the mth
	 */
	public Long getMth()
	{
		return mth;
	}

	/**
	 * @param mth
	 *            the mth to set
	 */
	public void setMth(Long mth)
	{
		this.mth = mth;
	}

	/**
	 * @return the mtr
	 */
	public Long getMtr()
	{
		return mtr;
	}

	/**
	 * @param mtr
	 *            the mtr to set
	 */
	public void setMtr(Long mtr)
	{
		this.mtr = mtr;
	}

	/**
	 * @return the data
	 */
	public String[] getData()
	{
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(String[] data)
	{
		this.data = data;
	}

	/**
	 * @return the pageId
	 */
	public Long getPageId()
	{
		return pageId;
	}

	/**
	 * @param pageId
	 *            the pageId to set
	 */
	public void setPageId(Long pageId)
	{
		this.pageId = pageId;
	}

	/**
	 * @return the processId
	 */
	public int getProcessId()
	{
		return processId;
	}

	/**
	 * @param processId
	 *            the processId to set
	 */
	public void setProcessId(int processId)
	{
		this.processId = processId;
	}

	public int getRamCapacity()
	{
		return ramCapacity;
	}

	public void setRamCapacity(int ramCapacity)
	{
		this.ramCapacity = ramCapacity;
	}

	public int getProcessesNumber()
	{
		return processesNumber;
	}

	public void setProcessesNumber(int processesNumber)
	{
		this.processesNumber = processesNumber;
	}

}
