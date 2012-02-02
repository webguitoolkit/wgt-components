package org.webguitoolkit.components.asynchronous;

public class ProcessExample extends AbstractAsynchronousProcess {
	@Override
	public void run() {
		this.setTotal(75);
		int i = 0;
		while (i < 75) {
			System.out.println("count " + i++);
			try {
				sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
				super.setFinished(true);
			}
			super.countUp();
		}
		System.out.println("final count " + i++);
		super.setFinished(true);
	}
}
