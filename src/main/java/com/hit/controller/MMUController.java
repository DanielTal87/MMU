package com.hit.controller;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.hit.driver.CLI;
import com.hit.model.MMUModel;
import com.hit.model.Model;
import com.hit.view.LogEvent;
import com.hit.view.MMUView;
import com.hit.view.View;

/**
 * The controller of the MVC pattern
 * @author Daniel Altalat
 */
public class MMUController implements Controller, Observer
{
	private Model	model;
	private View		view;

	public MMUController(Model model, View view)
	{
		setModel(model);
		setView(view);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg)
	{
		if (o instanceof CLI)
		{
			((MMUModel)model).setConfiguration((List<String>)arg);
			((MMUModel)model).start();
		} else
			if (o instanceof MMUModel)
			{
				((MMUView)view).setConfiguration((LogEvent[])arg);
				((MMUView)view).start();
			}
	}

	/* ~~~~~~~~~~~~~~~ Setter & Getters ~~~~~~~~~~~~~~~ */

	/**
	 * @return the model
	 */
	public Model getModel()
	{
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(Model model)
	{
		this.model = model;
	}

	/**
	 * @return the view
	 */
	public View getView()
	{
		return view;
	}

	/**
	 * @param view
	 *            the view to set
	 */
	public void setView(View view)
	{
		this.view = view;
	}

}
