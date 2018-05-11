package com.hit.memoryunits;

import java.io.Serializable;

/**
 * @author Daniel Altalat
 * @param <T>
 */
public class Page <T> implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private T					content;
	private Long					pageId;

	public Page(Long id, T content)
	{
		this.setContent(content);
		this.setPageId(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{
		boolean isEquals = false;

		if (this == obj || pageId.equals(((Page<T>)obj).getPageId()))
		{
			isEquals = true;
		}

		return isEquals;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((pageId == null) ? 0 : pageId.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return pageId.toString();
	}

	/* ~~~~~~~~~~~~~~~ Setter & Getters ~~~~~~~~~~~~~~~ */

	public Long getPageId()
	{
		return pageId;
	}

	public void setPageId(Long pageId)
	{
		this.pageId = pageId;
	}

	public T getContent()
	{
		return content;
	}

	public void setContent(T content)
	{
		this.content = content;
	}

}
