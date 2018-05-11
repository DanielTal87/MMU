package com.hit.driver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.hit.controller.MMUController;
import com.hit.memoryunits.HardDisk;
import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.model.MMUModel;
import com.hit.processes.Process;
import com.hit.processes.ProcessCycles;
import com.hit.processes.RunConfiguration;
import com.hit.util.MMULogger;
import com.hit.view.MMUView;

/**
 * @author Daniel Altalat
 */
public class MMUDriver
{
	private static final String CONFIG_FILE_NAME = "src/main/resources/com/hit/config/Configuration.json";

	public static void main(String[] args) throws InterruptedException, InvocationTargetException
	{
		// Build MVC model to demonstrate MMU system actions
		CLI cli = new CLI(System.in, System.out);
		MMUModel model = new MMUModel();
		MMUView view = new MMUView();
		MMUController controller = new MMUController(model, view);
		model.addObserver(controller);
		cli.addObserver(controller);
		view.addObserver(controller);
		new Thread(cli).start();
	}

	/**
	 * The method starts each process
	 * when all the process are done, it returns to the main, which will operate a shutdown(maximum after 10 minutes) and opens the GUI.
	 * @param applications
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static void runProcesses(List<Process> applications) throws InterruptedException, ExecutionException
	{
		ExecutorService executor = Executors.newCachedThreadPool();
		@SuppressWarnings("unchecked")
		Future<Boolean> futures[] = new Future[applications.size()];
		for (int i = 0; i < futures.length; i++)
		{
			futures[i] = executor.submit(applications.get(i));
		}
		executor.shutdown();
		try
		{
			executor.awaitTermination(2, TimeUnit.MINUTES);
		} catch (InterruptedException e)
		{
			MMULogger.getInstance().write(e.getMessage(), Level.SEVERE);
		}
	}

	public static RunConfiguration readConfigurationFile()
	{
		RunConfiguration runConfiguration = null;
		try
		{
			Gson gson = new Gson();
			runConfiguration = gson.fromJson(new JsonReader(new FileReader(CONFIG_FILE_NAME)), RunConfiguration.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e)
		{
			MMULogger.getInstance().write(e.toString(), Level.SEVERE);
		}

		return runConfiguration;
	}

	/**
	 * The method build a list of processes that need to execute
	 * @param appliocationsScenarios
	 * @param mmu
	 * @return list of the processes that needs to be run
	 */
	public static List<Process> createProcesses(List<ProcessCycles> appliocationsScenarios, MemoryManagementUnit mmu)
	{
		List<Process> processList = new ArrayList<Process>();

		for (int id = 0; id < appliocationsScenarios.size(); id++)
		{
			processList.add(new Process(id + 1, mmu, appliocationsScenarios.get(id)));
		}

		return processList;
	}

	/**
	 * The method makes sure that after the shutdown all the RAM will be updated in the HardDisk changes were saved.
	 * @param mmu
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static void shutDown(MemoryManagementUnit mmu)
	{
		try
		{
			HardDisk.getInstance().saveAll(mmu.getRam().getPages());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
