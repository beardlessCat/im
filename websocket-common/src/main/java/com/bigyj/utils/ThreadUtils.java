package com.bigyj.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {
	private static int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	private static int IO_MAX = Math.max(2, CPU_COUNT * 2);
	private static int KEEP_ALIVE_SECONDS = 30;
	private static int QUEUE_SIZE = 10000 ;

	public static Executor getExecutor(){
		ThreadPoolExecutor executor = new ThreadPoolExecutor(
				IO_MAX,
				IO_MAX,
				KEEP_ALIVE_SECONDS,
				TimeUnit.SECONDS,
				new LinkedBlockingQueue(QUEUE_SIZE));
		return executor;
	}

}
