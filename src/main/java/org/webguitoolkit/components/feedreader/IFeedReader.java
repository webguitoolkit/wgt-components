package org.webguitoolkit.components.feedreader;

import java.util.List;

import org.webguitoolkit.components.feedreader.FeedReader.FeedAndConnection;

public interface IFeedReader {

	// constants
	public static final int FEED_VIEW = 0;
	public static final int ITEM_VIEW = 1;

	/**
	 * 
	 */
	public abstract void refreshFeedItems(FeedAndConnection feed);

	/**
	 * @param startView the startView to set
	 */
	public abstract void setStartView(int startView);

	/**
	 * @return the startView
	 */
	public abstract int getStartView();

	/**
	 * 
	 * @return
	 */
	public abstract int getWidth();

	/**
	 * 
	 * @param width
	 */
	public abstract void setWidth(int width);

	/**
	 * 
	 * @return
	 */
	public abstract int getHeight();

	/**
	 * 
	 * @param height
	 */
	public abstract void setHeight(int height);

	/**
	 * 
	 * @return
	 */
	public abstract List<String> getFeedConnections();

	/**
	 * overwrites all formerly given feed connections
	 * 
	 * @param feedConnections
	 */
	public abstract void setFeedConnections(List<String> feedConnections);

	/**
	 * 
	 * @return
	 */
	public abstract String getFeedConnection();

	/**
	 * overwrites all formerly given feed connections
	 * 
	 * @param feedConnection
	 */
	public abstract void setFeedConnection(String feedConnection);

	/**
	 * @param title the title to set
	 */
	public abstract void setTitle(String title);

	/**
	 * @return the title
	 */
	public abstract String getTitle();

	/**
	 * adds feed connections to the existing list of feed connections
	 * 
	 * @param feedConnections
	 */
	public abstract void addFeedConnections(List<String> feedConnections);

	/**
	 * adds one feed connection to the existing list of feed connections
	 * 
	 * @param feedConnections
	 */
	public abstract void addFeedConnection(String feedConnection);

}
