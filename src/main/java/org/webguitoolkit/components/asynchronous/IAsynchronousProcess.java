package org.webguitoolkit.components.asynchronous;

public interface IAsynchronousProcess{
	void start();
	int getCounter();
	int getTotal();
	boolean hasFinished();
	void setTotal(int total);
	int getPercentage();

	public boolean isShowProcessItemCount();

	public boolean isShowProcessPercentage();

	public boolean isShowProcessBar();

	public void setShowProcessItemCount(boolean showProcessItemCount);

	public void setShowProcessPercentage(boolean showProcessPercentage);

	public void setShowProcessBar(boolean showProcessBar);
}
