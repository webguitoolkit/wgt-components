package org.webguitoolkit.components.asynchronous;

public abstract class AbstractAsynchronousProcess extends Thread implements IAsynchronousProcess {
	protected int counter = 0;
	private int total = -1;
	private boolean finished = false;
	
	private boolean showProcessItemCount = true;
	private boolean showProcessPercentage = true;
	private boolean showProcessBar = true;

	protected void countUp() {
		counter++;
	}

	/**
	 * @return the counter
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	public boolean hasFinished() {
		return finished;
	}
	protected void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	public int getPercentage(){
		int p = 0;
		float f = ((float) counter/(float) total)*100;
		p = (int)f;
		return p;
	}

	public boolean isShowProcessItemCount() {
		return showProcessItemCount;
	}

	public void setShowProcessItemCount(boolean showProcessItemCount) {
		this.showProcessItemCount = showProcessItemCount;
	}

	public boolean isShowProcessPercentage() {
		return showProcessPercentage;
	}

	public void setShowProcessPercentage(boolean showProcessPercentage) {
		this.showProcessPercentage = showProcessPercentage;
	}

	public boolean isShowProcessBar() {
		return showProcessBar;
	}

	public void setShowProcessBar(boolean showProcessBar) {
		this.showProcessBar = showProcessBar;
	}

}
