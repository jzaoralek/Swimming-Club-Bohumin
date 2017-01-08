package com.jzaoralek.scb.ui.monitoring.performance;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.PerformanceMeter;

public class PerformanceMonitor implements PerformanceMeter {

	private long timeStartAtClient;
	private long timeStartAtServer;
	private long timeCompleteAtServer;
	private long timeRecieveAtClient;
	private long timeCompleteAtClient;

	@Override
	public void requestStartAtClient(String arg0, Execution exec, long time) {
		timeStartAtClient = time;
	}

	@Override
	public void requestReceiveAtClient(String arg0, Execution exec, long time) {
		timeRecieveAtClient = time;
	}

	@Override
	public void requestCompleteAtClient(String arg0, Execution exec, long time) {
		timeCompleteAtClient = time;
		
		Long serverExe = timeCompleteAtServer - timeStartAtServer;
		Long clientExe = timeCompleteAtClient - timeRecieveAtClient;
		Long networkExe = (timeRecieveAtClient - timeCompleteAtServer); //+ (timeStartAtServer - timeStartAtClient);
		
		long totalExe = 0;
		totalExe += serverExe != null ? serverExe : 0;
		totalExe += clientExe != null ? clientExe : 0;
		totalExe += networkExe != null ? networkExe : 0;
		
		if (exec.getAttribute("command") != null) {
			System.out.println("Command name: " + exec.getAttribute("command"));
		}
//		System.out.println(
//				"Performance monitor : client:{"+clientExe+"} ms."
//						+ ", server:{"+serverExe+"} ms."
//								+ ", network:{"+networkExe+"} ms."
//										+ ", total:{"+totalExe+"} ms.");
	}

	@Override
	public void requestStartAtServer(String arg0, Execution exec, long time) {
		timeStartAtServer = time;
	}

	@Override
	public void requestCompleteAtServer(String arg0, Execution exec, long time) {
		timeCompleteAtServer = time;
	}
}
