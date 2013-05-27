package com.focused.projectf.global;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Threading {

	public static final int NumberOfThreads;	
	private static ExecutorService executorService;

	static {
		NumberOfThreads = Math.max(Runtime.getRuntime().availableProcessors() - 1, 1);		
		executorService =
				new ThreadPoolExecutor(
						NumberOfThreads, 
						NumberOfThreads, 
						1, 
						TimeUnit.MINUTES,
						new ArrayBlockingQueue<Runnable>(NumberOfThreads, true),
						new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public static void shutdown() {
		executorService.shutdown();
	}

	public static void pushOperation(Runnable runnable) {
		new Thread(runnable).start();
		
		//executorService.execute(runnable);
	}
	public static void pushOperation(final Runnable runnable, int i) {
		ArrayList<Callable<Object>> runs = new ArrayList<Callable<Object>>();
		runs.add(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				runnable.run();
				return null;
			}
		});
		
		try {
			executorService.invokeAll(runs, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean pause(float seconds) {
		try {
			Thread.sleep((int)(seconds * 1000f));
			return true;
		} catch(InterruptedException ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
