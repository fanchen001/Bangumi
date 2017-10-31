package com.fanchen.imovie.thread;

import android.os.Process;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;


/**
 * 描述：线程工厂.
 *
 * @author fanchen
 */
public class AsyThreadFactory {
	
	/** 任务执行器. */
	public static Executor mExecutorService = null;
	
	/** 保存线程数量 . */
	private static final int CORE_POOL_SIZE = 2;
	
	/** 最大线程数量 . */
    private static final int MAXIMUM_POOL_SIZE = 8;
    
    /** 活动线程数量 . */
    private static final int KEEP_ALIVE = 2;

    /** 线程工厂 . */
    private static final ThreadFactory mThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "BangumiThread #" + mCount.getAndIncrement());
        }
    };

    /** 队列. */
    private static final BlockingQueue<Runnable> mPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(5);
    
    /**
     * 获取执行器.
     *
     * @return the executor service
     */
    public static Executor getExecutorService() { 
        if (mExecutorService == null) { 
        	int numCores = getNumCores();
        	mExecutorService
	         = new ThreadPoolExecutor(numCores * CORE_POOL_SIZE,numCores * MAXIMUM_POOL_SIZE,numCores * KEEP_ALIVE,
                    TimeUnit.SECONDS, mPoolWorkQueue, mThreadFactory);
        }
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        return mExecutorService;
    } 
	
    /** 
	 * 获取cpu核心数
	 * filesystem at "/sys/devices/system/cpu" 
	 * @return The number of cores, or 1 if failed to get result 
	 */ 
	public static int getNumCores() { 
		try { 
			File dir = new File("/sys/devices/system/cpu/"); 
			File[] files = dir.listFiles(new FileFilter(){

				@Override
				public boolean accept(File pathname) {
					if(Pattern.matches("cpu[0-9]", pathname.getName())) { 
					   return true; 
				    } 
				    return false; 
				}
				
			}); 
			return files.length; 
		} catch(Exception e) { 
			e.printStackTrace();
			return 1; 
		} 
	} 
}
