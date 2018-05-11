package com.hit.driver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;
import java.util.logging.Level;

import com.hit.util.MMULogger;
import com.hit.view.View;

/**
 * @author Daniel Altalat
 */
public class CLI extends Observable implements Runnable, View
{

	Scanner		input;
	PrintWriter	output;

	public CLI(InputStream in, OutputStream out)
	{
		this.input = new Scanner(in);
		this.output = new PrintWriter(out);
	}

	@Override
	public void run()
	{
		start();
	}

	@Override
	public void start()
	{
		Boolean isValidInput = true;
		String startStopCommand = null;
		String capacity = null;
		String algorithm = null;
		String userInput = null;

		do
		{
			capacity = "";
			algorithm = "";
			write("What would you like to do? (stop/start)");
			startStopCommand = input.nextLine().toLowerCase();

			if (startStopCommand.equals("start"))
			{
				isValidInput = true;
				write("Please enter required algorithm and RAM capacity");
				userInput = input.nextLine().toLowerCase();
				String[] algorithmAndCapacity = userInput.split(" ");
				algorithm = algorithmAndCapacity[0];
				if (!algorithm.equals("lru") && !algorithm.equals("nfu") && !algorithm.equals("random"))
				{
					write("Not a valid command!");
					MMULogger.getInstance().write("Algorithm: Not a valid command!", Level.SEVERE);
					isValidInput = false;
					continue;
				}
				try
				{
					Integer.valueOf(algorithmAndCapacity[1]);
				} catch (Exception e)
				{
					write("Not a valid command!");
					MMULogger.getInstance().write("Capacity: Not a valid command!", Level.SEVERE);
					isValidInput = false;
					continue;
				}
				capacity = algorithmAndCapacity[1].toString();

				isValidInput = true;

				if (isValidInput)
				{
					ArrayList<String> finalCommand = new ArrayList<String>();
					finalCommand.add(algorithm);
					finalCommand.add(capacity);
					setChanged();
					notifyObservers(finalCommand);
					isValidInput = false;
					continue;
				}
			} else
				if (startStopCommand.equals("stop"))
				{
					write("Thank you!");
					MMULogger.getInstance().write("Thank you!", Level.SEVERE);
					input.close();

					break;
				} else
				{
					MMULogger.getInstance().write("Not a valid command!", Level.SEVERE);
					write("Not a valid command");
					isValidInput = false;
					continue;
				}
		} while (!isValidInput);
	}

	public void write(String string)
	{
		output.println(string);
		output.flush();
	}

}
