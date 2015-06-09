package application;

import common.Configuration;

public class MUIAChecker implements Runnable {
	private MUIA checkable;
	
	public MUIAChecker( MUIA checkable ) {
		this.checkable = checkable;
	}

	@Override
	public void run() {
		while( true ) {
			checkable.keepAlive();
			
			Long periodicity = Long.parseLong(Configuration.get(Configuration.MUIA_CHECKER_PERIODICITY)) * 1000;
			try {
				Thread.sleep(periodicity);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
