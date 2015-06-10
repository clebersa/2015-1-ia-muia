package application;

import common.Configuration;
import common.Logger;

public class MUIAChecker implements Runnable {
	private static final Long TIME_TO_WAIT_BEFORE_START = (long) 10000;
	private MUIA checkable;
	
	public MUIAChecker( MUIA checkable ) {
		this.checkable = checkable;
	}

	@Override
	public void run() {
		Integer waitSeconds = Integer.valueOf(Long.toString((TIME_TO_WAIT_BEFORE_START / 1000)));
		Logger.debug("Waiting " + waitSeconds + " seconds to start MUIA Checker of MUIA copy {" + checkable + "}");
		try {
			Thread.sleep( TIME_TO_WAIT_BEFORE_START );
		} catch (InterruptedException e) {
			Logger.error("MUIA Checker of MUIA copy {" + checkable + "} interrupted");
		}
		Logger.debug("MUIA Checker of MUIA copy {" + checkable + "} started!");
		
		while( true ) {
			checkable.keepAlive();
			
			Long periodicity = Long.parseLong(Configuration.get(Configuration.MUIA_CHECKER_PERIODICITY)) * 1000;
			try {
				Thread.sleep(periodicity);
			} catch (InterruptedException e) {
				Logger.error("MUIA Checker of MUIA copy {" + checkable + "} interrupted");
			}
		}
	}

}
