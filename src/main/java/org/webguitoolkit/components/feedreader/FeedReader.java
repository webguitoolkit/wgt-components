/**
 * (c) 2009, Endress&Hauser InfoServe GmbH & Co KG
 * Created on 05.06.2009
 */
package org.webguitoolkit.components.feedreader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.webguitoolkit.ui.base.DataBag;
import org.webguitoolkit.ui.base.IDataBag;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractView;
import org.webguitoolkit.ui.controls.IBaseControl;
import org.webguitoolkit.ui.controls.Page;
import org.webguitoolkit.ui.controls.container.Canvas;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.contextmenu.IContextMenu;
import org.webguitoolkit.ui.controls.contextmenu.IContextMenuItem;
import org.webguitoolkit.ui.controls.contextmenu.IContextMenuListener;
import org.webguitoolkit.ui.controls.event.ClientEvent;
import org.webguitoolkit.ui.controls.event.IActionListener;
import org.webguitoolkit.ui.controls.form.IButton;
import org.webguitoolkit.ui.controls.form.ICompound;
import org.webguitoolkit.ui.controls.form.ILabel;
import org.webguitoolkit.ui.controls.form.Label;
import org.webguitoolkit.ui.controls.layout.ITableLayout;
import org.webguitoolkit.ui.controls.table.ITable;
import org.webguitoolkit.ui.controls.tree.ITree;
import org.webguitoolkit.ui.controls.util.TextService;
import org.webguitoolkit.ui.controls.util.Tooltip;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

/**
 * Feed Reader Control for showing news feeds in a HTML-based widget that can be integrated into any WGT-project
 * 
 * The control provides three views: 1. an overview over several determined feeds 2. an overview over the feed entries of a
 * certain feed 3. a detailed view of the chosen feed entry
 * 
 * To use the feed reader properly you have to copy the feed-reader-images from common-include folder and check out the
 * application resources from TranslationTool! See WebGuiPatterns-project for details.
 * 
 * @author Alexander Sattler
 */
public class FeedReader extends AbstractView implements IFeedReader {

	// constants
	private static final int ITEM_DETAIL_VIEW = 2;

	private static final int DEFAULT_HEIGHT = 320;
	private static final int DEFAULT_WIDTH = 250;
	private static final int DEFAULT_OFFSET = 3; // number of feed entries that are simultaneously displayed
	private static final String COOKIENAME_PREFIX_READ = "ElementRead";
	private static final String COOKIENAME_PREFIX_HIDDEN = "ElementHide";

	private static final String FEEDUSER_PWD = "feEd123";
	private static final String FEEDUSER_NAME = "321dEef"; // FIXME in Konfigurationsdatei als Property auslagern?

	private static final int ITEM_HEIGHT = 80;
	private static final String BUTTON_THEME = "black";

	private static final String IMAGE_BASE_PATH = "./images/components/feedreader/";

	// variables
	private int startView;

	private int width, height;
	private String title, feedConnection;
	private List<String> feedConnections;
	private boolean isShowTitle;

	private ITableLayout layout;

	private List<FeedAndConnection> allFeeds;
	private FeedAndConnection inputFeed;
	private String feedTitle = "";

	private IButton btn_back, btn_home, btn_refresh, btn_link;

	private int scrollbarFrom = 0;
	private int scrollbarIterator = 1;
	private int numberOfViewableItems = DEFAULT_OFFSET;
	private int scrollbarLimit = 0;

	private int boxHeight = 0;

	/**
	 * 
	 * @param factory
	 * @param viewConnector
	 * @param feedConnection
	 * @param startView
	 */
	public FeedReader(WebGuiFactory factory, Canvas viewConnector, String feedConnection, int startView) {
		this(factory, viewConnector, Arrays.asList(feedConnection), startView);
	}

	/**
	 * 
	 * @param factory
	 * @param viewConnector
	 * @param feedConnections
	 * @param startView
	 */
	public FeedReader(WebGuiFactory factory, Canvas viewConnector, List<String> feedConnections, int startView) {
		super(factory, viewConnector);
		this.setStartView(startView);

		// default values
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		this.title = "";
		this.isShowTitle = false;

		if (feedConnections.isEmpty()) {
			this.feedConnection = "";
		}
		else {
			this.feedConnection = feedConnections.get(0);
		}

		this.feedConnections = new ArrayList<String>();
		this.feedConnections.addAll(feedConnections);

		layout = factory.createTableLayout(viewConnector);
		try {
			allFeeds = new ArrayList<FeedAndConnection>();
			this.loadFeeds(feedConnections);
		}
		catch (Exception e) {
			Logger.getLogger(FeedReader.class).error("", e);
		}
	}

	/**
	 * 
	 * @param theCan
	 */
	private void startFeedContainer(final ICanvas theCan) {
		boxHeight = 66 + (ITEM_HEIGHT * this.numberOfViewableItems);
		String startHtml = "<table cellspacing=\"0\" cellpadding=\"0\"" + " style=\"width: " + this.width
				+ "px;\" class=\"wgtTabPane wgtTabBox wgtFeedReaderContainer\">"
				+ // FIXME inline style!
				"<tr><td colspan=\"3\" class=\"wgtTabContentBorder\">" + "<div class=\"wgtFeedReaderTabContent\" style=\"height: "
				+ boxHeight + "px;\">";
		ILabel lblContainer = getFactory().createLabel(theCan, startHtml);
	}

	/**
	 * 
	 * @param theCan
	 */
	private void endFeedContainer(final ICanvas theCan) {
		String endHtml = "</div></td></tr><tr class=\"wgtFeedReaderContainerFooter\"><td class=\"wgtLBFooter\"> </td>"
				+ "<td class=\"wgtCFooter\" style=\"width: " + (this.width - 8)
				+ "px;\">&nbsp;</td><td class=\"wgtRBFooter\"> </td></tr></table>";
		ILabel lblContainer = getFactory().createLabel(theCan, endHtml);
	}

	/**
	 * 
	 * @param theCan
	 * @param whichView
	 */
	private void showFeedTitle(ICanvas theCan, int whichView) {
		ITableLayout titleLayout = getFactory().createTableLayout(theCan);
		titleLayout.getEcsTable().setCellPadding(0);
		titleLayout.getEcsTable().setCellSpacing(0);
		titleLayout.getEcsTable().setWidth(this.width - 4);

		ILabel lblTitle;
		switch (whichView) {
			case FEED_VIEW:
				lblTitle = getFactory().createLabel(titleLayout, this.getTitle());
				break;
			default:
				lblTitle = getFactory().createLabel(titleLayout, this.getFeedTitle());
		}
		lblTitle.addCssClass("wgtFeedReaderTitle");

		titleLayout.getCurrentCell().setHeight(16);
		titleLayout.getCurrentCell().setClass("wgtFeedReaderTitleBar");
	}

	/**
	 * Called from feed view
	 * 
	 * @param theICan
	 * @param isShowBackHomeRefreshLink
	 * @param backFrom
	 */
	private void showFeedNavigation(final ICanvas theICan, final boolean[] isShowBackHomeRefreshLink, final int backFrom) {
		showFeedNavigation(theICan, isShowBackHomeRefreshLink, backFrom, null, null, null);
	}

	/**
	 * Called from item view
	 * 
	 * @param theICan
	 * @param isShowBackHomeRefreshLink
	 * @param backFrom
	 * @param db_toFeed
	 * @param db_fromFeed
	 */
	private void showFeedNavigation(final ICanvas theICan, final boolean[] isShowBackHomeRefreshLink, final int backFrom,
			final IDataBag db_toFeed, final IDataBag db_fromFeed) {
		showFeedNavigation(theICan, isShowBackHomeRefreshLink, backFrom, db_toFeed, db_fromFeed, null);
	}

	/**
	 * Called from item detail view
	 * 
	 * @param theICan
	 * @param isShowBackHomeRefreshLink
	 * @param backFrom
	 * @param db_toFeed
	 * @param entry
	 */
	private void showFeedNavigation(final ICanvas theICan, final boolean[] isShowBackHomeRefreshLink, final int backFrom,
			final IDataBag db_toFeed, final SyndEntry entry) {
		showFeedNavigation(theICan, isShowBackHomeRefreshLink, backFrom, db_toFeed, null, entry);
	}

	/**
	 * 
	 * @param theCan
	 * @param isShowBack
	 * @param isShowHome
	 * @param backFrom
	 * @param db_toFeed
	 */
	private void showFeedNavigation(final ICanvas theICan, final boolean[] isShowBackHomeRefreshLink, final int backFrom,
			final IDataBag db_toFeed, final IDataBag db_fromFeed, final SyndEntry entry) {
		// TODO remove the following block when ICanvas gets the missing methods
		final Canvas theCan;
		if (theICan instanceof Canvas) {
			theCan = (Canvas)theICan;
		}
		else
			return;

		boolean isShowBack = isShowBackHomeRefreshLink[0];
		boolean isShowHome = isShowBackHomeRefreshLink[1];
		boolean isShowRefresh = isShowBackHomeRefreshLink[2];
		boolean isShowLink = isShowBackHomeRefreshLink[3];

		ITableLayout navLayout = getFactory().createTableLayout(theCan);
		navLayout.getEcsTable().setClass("wgtFeedReaderHeaderNavigation");
		navLayout.getEcsTable().setWidth(this.getWidth() - 4);
		navLayout.addCssClass("wgtFeedReaderIconBar");

		// generate source links to button icons (because of theming)
		String srcBackButton = IMAGE_BASE_PATH + "btn_footer_back";
		if (StringUtils.isNotBlank(BUTTON_THEME)) {
			srcBackButton += "_" + BUTTON_THEME;
		}
		srcBackButton += ".gif";

		String srcHomeButton = IMAGE_BASE_PATH + "btn_footer_home";
		if (StringUtils.isNotBlank(BUTTON_THEME)) {
			srcHomeButton += "_" + BUTTON_THEME;
		}
		srcHomeButton += ".gif";

		String srcRefreshButton = IMAGE_BASE_PATH + "btn_footer_refresh";
		if (StringUtils.isNotBlank(BUTTON_THEME)) {
			srcRefreshButton += "_" + BUTTON_THEME;
		}
		srcRefreshButton += ".gif";

		String srcLinkButton = IMAGE_BASE_PATH + "btn_footer_link";
		if (StringUtils.isNotBlank(BUTTON_THEME)) {
			srcLinkButton += "_" + BUTTON_THEME;
		}
		srcLinkButton += ".gif";

		// back button
		btn_back = getFactory().createLinkButton(navLayout, srcBackButton, "", "feedreader.back", new IActionListener() {

			public void onAction(ClientEvent event) {
				if (event.getSource() != null && event.getSource() == btn_back && btn_back.isDisabled() == false) {

					theCan.removeAllChildren();
					switch (backFrom) {
						case ITEM_VIEW:
							goToFeedView(theCan);
							break;
						case ITEM_DETAIL_VIEW:
							goToItemView(theCan, db_toFeed);
							break;
						default:
							goToFeedView(theCan);
					}
					theCan.redraw();
				}
			}

		}, "btnback");
		btn_back.addCssClass("wgtFeedReaderIcon");

		if (!isShowBack) {
			btn_back.setDisabled(true);
			btn_back.setTooltip("");
		}

		// home button
		btn_home = getFactory().createLinkButton(navLayout, srcHomeButton, null, "feedreader.home", new IActionListener() {

			public void onAction(ClientEvent event) {
				if (event.getSource() != null && event.getSource() == btn_home && btn_home.isDisabled() == false) {

					theCan.removeAllChildren();
					goToFeedView(theCan);
					theCan.redraw();
				}
			}

		}, "btnhome");
		btn_home.addCssClass("wgtFeedReaderIconHome");

		if (!isShowHome) {
			btn_home.setDisabled(true);
			btn_home.setTooltip("");
		}

		// refresh button
		btn_refresh = getFactory().createLinkButton(navLayout, srcRefreshButton, "", "feedreader.refresh", new IActionListener() {

			public void onAction(ClientEvent event) {
				if (event.getSource() != null && event.getSource() == btn_refresh && btn_refresh.isDisabled() == false) {

					theCan.removeAllChildren();
					switch (backFrom) {
						case ITEM_VIEW:
							goToItemView(theCan, db_fromFeed);
							break;
						case ITEM_DETAIL_VIEW:
							// goToItemDetailView(theCan);
							break;
						default:
							goToFeedView(theCan);
					}
					theCan.redraw();
				}
			}

		}, "btnrefresh");
		btn_refresh.addCssClass("wgtFeedReaderIcon");

		if (!isShowRefresh) {
			btn_refresh.setDisabled(true);
			btn_refresh.setTooltip("");
		}

		// link button
		String linkTo = "";
		if (entry != null) {
			linkTo = entry.getLink();
		}
		btn_link = getFactory().createLink(navLayout, srcLinkButton, "", "feedreader.link", linkTo, "_blank");
		btn_link.addCssClass("wgtFeedReaderIcon");
		// set empty action listener to avoid error message when clicking the button in disabled mode
		btn_link.setActionListener(new IActionListener() {
			public void onAction(ClientEvent event) {
			}
		});

		if (!isShowLink) {
			btn_link.setDisabled(true);
			btn_link.setTarget("");
			btn_link.setTooltip("");
		}
	}

	/*** BEGIN SCROLLBAR NAVIGATION ***/
	private void showFooterNavigation(final ICanvas theICan, final IDataBag db_feed) {
		// TODO remove the following block when ICanvas gets the missing methods
		final Canvas theCan;
		if (theICan instanceof Canvas) {
			theCan = (Canvas)theICan;
		}
		else
			return;
		ITableLayout canLayout = getFactory().createTableLayout(theCan);
		canLayout.getEcsTable().setClass("wgtFeedReaderFooterNavigation");
		canLayout.getEcsTable().setWidth(this.getWidth() - 4);

		// generate source links to button icons (because of theming)
		String srcFirstButton = IMAGE_BASE_PATH + "btn_nav_page-first";
		if (StringUtils.isNotBlank(BUTTON_THEME)) {
			srcFirstButton += "_" + BUTTON_THEME;
		}
		srcFirstButton += ".gif";

		String srcPageDownButton = IMAGE_BASE_PATH + "btn_nav_page-down";
		if (StringUtils.isNotBlank(BUTTON_THEME)) {
			srcPageDownButton += "_" + BUTTON_THEME;
		}
		srcPageDownButton += ".gif";

		String srcStepDownButton = IMAGE_BASE_PATH + "btn_nav_step-down";
		if (StringUtils.isNotBlank(BUTTON_THEME)) {
			srcStepDownButton += "_" + BUTTON_THEME;
		}
		srcStepDownButton += ".gif";

		String srcStepUpButton = IMAGE_BASE_PATH + "btn_nav_step-up";
		if (StringUtils.isNotBlank(BUTTON_THEME)) {
			srcStepUpButton += "_" + BUTTON_THEME;
		}
		srcStepUpButton += ".gif";

		String srcPageUpButton = IMAGE_BASE_PATH + "btn_nav_page-up";
		if (StringUtils.isNotBlank(BUTTON_THEME)) {
			srcPageUpButton += "_" + BUTTON_THEME;
		}
		srcPageUpButton += ".gif";

		String srcLastButton = IMAGE_BASE_PATH + "btn_nav_page-last";
		if (StringUtils.isNotBlank(BUTTON_THEME)) {
			srcLastButton += "_" + BUTTON_THEME;
		}
		srcLastButton += ".gif";

		IButton btnFirst = getFactory().createLinkButton(canLayout, srcFirstButton, null, "feedreader.navigation.up.first",
				new IActionListener() {

					public void onAction(ClientEvent event) {
						scrollbarFrom = 0;
						theCan.removeAllChildren();
						goToItemView(theCan, db_feed);
						theCan.redraw();
					}

				});

		IButton btnPageDown = getFactory().createLinkButton(canLayout, srcPageDownButton, null, "feedreader.navigation.up.page",
				new IActionListener() {

					public void onAction(ClientEvent event) {
						decreaseFrom(numberOfViewableItems);
						theCan.removeAllChildren();
						goToItemView(theCan, db_feed);
						theCan.redraw();
					}

				});

		IButton btnStepDown = getFactory().createLinkButton(canLayout, srcStepDownButton, null, "feedreader.navigation.up.step",
				new IActionListener() {

					public void onAction(ClientEvent event) {
						decreaseFrom(scrollbarIterator);
						theCan.removeAllChildren();
						goToItemView(theCan, db_feed);
						theCan.redraw();
					}

				});

		IButton btnStepUp = getFactory().createLinkButton(canLayout, srcStepUpButton, null, "feedreader.navigation.down.step",
				new IActionListener() {

					public void onAction(ClientEvent event) {
						increaseFrom(scrollbarIterator);
						theCan.removeAllChildren();
						goToItemView(theCan, db_feed);
						theCan.redraw();
					}

				});

		IButton btnPageUp = getFactory().createLinkButton(canLayout, srcPageUpButton, null, "feedreader.navigation.down.page",
				new IActionListener() {

					public void onAction(ClientEvent event) {
						increaseFrom(numberOfViewableItems);
						theCan.removeAllChildren();
						goToItemView(theCan, db_feed);
						theCan.redraw();
					}

				});

		IButton btnLast = getFactory().createLinkButton(canLayout, srcLastButton, null, "feedreader.navigation.up.last",
				new IActionListener() {

					public void onAction(ClientEvent event) {
						scrollbarFrom = scrollbarLimit - numberOfViewableItems;
						theCan.removeAllChildren();
						goToItemView(theCan, db_feed);
						theCan.redraw();
					}

				});
	}

	private void increaseFrom(int value) {
		if (scrollbarFrom + value + numberOfViewableItems <= scrollbarLimit) {
			scrollbarFrom += value;
		}
		else {
			scrollbarFrom = scrollbarLimit - numberOfViewableItems;
		}
	}

	/**
	 * 
	 * @param value the value to decrease
	 * @return true, if scrollbarFrom - value > 0
	 */
	private boolean decreaseFrom(int value) {
		if (scrollbarFrom - value > 0) {
			scrollbarFrom -= value;
			return true;
		}
		else {
			scrollbarFrom = 0;
			return false;
		}
	}

	/*** END SCROLLBAR NAVIGATION ***/

	/**
	 * 
	 * @param theCan
	 */
	private void goToFeedView(final ICanvas theICan) {
		// TODO remove the following block when ICanvas gets the missing methods
		final Canvas theCan;
		if (theICan instanceof Canvas) {
			theCan = (Canvas)theICan;
		}
		else
			return;

		this.startFeedContainer(theCan);
		if (this.isShowTitle()) {
			this.showFeedTitle(theCan, FEED_VIEW);
		}
		this.showFeedNavigation(theCan, new boolean[] { false, false, false, false }, FEED_VIEW);

		ITableLayout canLayout = getFactory().createTableLayout(theCan);
		canLayout.getEcsTable().setCellPadding(0);
		canLayout.getEcsTable().setCellSpacing(0);
		canLayout.addCssClass("wgtFeedReaderFeedView");
		// canLayout.getEcsTable().setHeight(this.boxHeight - 44);

		if (this.getAllFeeds().size() > 0) {
			int iterations = 0;
			for (final FeedAndConnection feed : this.getAllFeeds()) {
				iterations++;

				final ICompound comp = getFactory().createCompound(canLayout);
				DataBag db_feed = new DataBag(feed);
				comp.setBag(db_feed);

				ITableLayout compLayout = getFactory().createTableLayout(comp);

				String theActualFeedTitle = feed.getFeed().getTitle();

				// get formerly manipulated feed entries from cookies
				List<String> cookieNames = new ArrayList<String>();

				int countOfNonReadItems = feed.getFeed().getEntries().size();
				// if there are cookies set count the read entries
				// else (no cookie is set) all the entries are new or there are none
				if (Page.getServletRequest().getCookies() != null) {
					for (Cookie ck : Page.getServletRequest().getCookies()) {
						cookieNames.add(ck.getName());
					}

					int countOfReadItems = 0;
					for (String item : cookieNames) {
						if (item.split("\\|")[0].equals(COOKIENAME_PREFIX_READ + md5Hash(theActualFeedTitle))) {
							countOfReadItems++;
						}
					}

					countOfNonReadItems = countOfNonReadItems - countOfReadItems;
				}

				if (countOfNonReadItems > 1) {
					theActualFeedTitle += " (" + countOfNonReadItems + " " + TextService.getString("feedreader.feedview.newentries") + ")";
				}
				else if (countOfNonReadItems == 1) {
					theActualFeedTitle += " (" + countOfNonReadItems + " " + TextService.getString("feedreader.feedview.newentry") + ")";
				}
				else {
					theActualFeedTitle += " (" + TextService.getString("feedreader.feedview.nonewentries") + ")";
				}

				IButton btn_feedtitle = getFactory().createLinkButton(compLayout, null, theActualFeedTitle, feed.getFeed().getTitle(),
						new IActionListener() {

							public void onAction(ClientEvent event) {
								theCan.removeAllChildren();
								setFeedTitle(feed.getFeed().getTitle());
								goToItemView(theCan, comp.getBag());
								theCan.redraw(); // FIXME: overwrites the previously set, empty click listener
							}

						});
				btn_feedtitle.addCssClass("wgtFeedReaderFeedViewButton");

				compLayout.getEcsTable().setOnMouseOver("doListMouseOver(1, this);");
				if (iterations % 2 == 0) { // row even
					compLayout.getEcsTable().setClass("wgtFeedReaderFeedViewContainer");
					compLayout.getEcsTable().setOnMouseOut("doListMouseOut(1, this);");
				}
				else { // row odd
					compLayout.getEcsTable().setClass("wgtFeedReaderFeedViewContainerOdd");
					compLayout.getEcsTable().setOnMouseOut("doListMouseOut(1, this);");
				}

				canLayout.newRow();
			}
		}
		else {
			ILabel lblNoFeeds = getFactory().createLabel(canLayout, "feedreader.feedview.empty");
			lblNoFeeds.addCssClass("wgtFeedReaderTextNotification");
			canLayout.addCssClass("wgtFeedReaderFeedViewContainer");
		}

		this.endFeedContainer(theCan);
	}

	/**
	 * 
	 * @param theCan
	 * @param dataBag
	 */
	private void goToItemView(final ICanvas theICan, final IDataBag dataBag) {
		// TODO remove the following block when ICanvas gets the missing methods
		final Canvas theCan;
		if (theICan instanceof Canvas) {
			theCan = (Canvas)theICan;
		}
		else
			return;
		// theCan.setCssClass("wgtFeedReaderItemView");
		this.startFeedContainer(theCan);
		this.showFeedTitle(theCan, ITEM_VIEW);
		if (this.getStartView() == ITEM_VIEW) {
			this.showFeedNavigation(theCan, new boolean[] { false, false, true, false }, ITEM_VIEW, null, dataBag);
		}
		else {
			this.showFeedNavigation(theCan, new boolean[] { true, false, true, false }, ITEM_VIEW, null, dataBag);
		}

		ITableLayout canLayout = getFactory().createTableLayout(theCan);
		canLayout.getEcsTable().setCellPadding(0);
		canLayout.getEcsTable().setCellSpacing(0);

		FeedAndConnection feed = (FeedAndConnection)dataBag.getDelegate();
		this.refreshFeedItems(feed);

		// load the refreshed feed so that the view will be refreshed on the first invoking
		FeedAndConnection refreshedFeed = null;
		for (FeedAndConnection item : this.getAllFeeds()) {
			if (feed.getConnection().equals(item.getConnection())) {
				refreshedFeed = item;
				break;
			}
		}

		boolean skipFooterNavigation = false; // flag to indicate whether there is no need to show the footer navigation bar
		if (refreshedFeed != null) {
			// set the scrollbar limit
			scrollbarLimit = refreshedFeed.getFeed().getEntries().size();

			final FeedAndConnection finalRefreshedFeed = refreshedFeed;

			if (finalRefreshedFeed.getFeed().getEntries().size() > 0) {
				int iterations = 0;
				for (Object obj : finalRefreshedFeed.getFeed().getEntries()) {
					try {
						iterations++;
						if ((iterations > scrollbarFrom) && (iterations <= scrollbarFrom + numberOfViewableItems)) {
							SyndEntry entry = (SyndEntry)obj;
							entry.setSource(finalRefreshedFeed.getFeed());
							final ICompound comp = getFactory().createCompound(canLayout);
							DataBag db_entry = new DataBag(entry);
							comp.setBag(db_entry);

							final ITableLayout compLayout = getFactory().createTableLayout(comp);

							String entryContent = entry.getDescription().getValue();
							String entryShortContent = (entryContent.length() < 80) ? entryContent : entryContent.substring(0, 80) + "...";

							Date theDate = null;
							String dateString = "";
							if (entry.getUpdatedDate() != null) {
								theDate = entry.getUpdatedDate();
							}
							else {
								theDate = entry.getPublishedDate();
							}

							Locale lang;
							if (StringUtils.isNotBlank(finalRefreshedFeed.getFeed().getLanguage())) {
								lang = new Locale(finalRefreshedFeed.getFeed().getLanguage());
							}
							else {
								lang = TextService.getLocale();
							}

							DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, lang);
							if (theDate != null) {
								dateString = df.format(theDate);
							}

							String uniqueFeedEntryId = entry.getSource().getTitle() + entry.getTitle() + entry.getUpdatedDate();
							// set entry as a button with link to the detail view
							final IButton btn_title = getFactory().createLinkButton(compLayout, null,
									"<strong>" + entry.getTitle() + " - " + dateString + "</strong><br />" + entryShortContent, "",
									new IActionListener() {

										public void onAction(ClientEvent event) {
											// send javascript that creates a cookie on client side
											String js = "markElement('" + md5Hash(finalRefreshedFeed.getFeed().getTitle()) + "|"
													+ ((IButton)event.getSource()).getId() + "');";
											getPage().getContext().sendJavaScript("clickElement", js); // TODO not in button click
											// event --> scroll event?

											// load item detail view
											theCan.removeAllChildren();
											goToItemDetailView(theCan, comp.getBag(), finalRefreshedFeed.getConnection());
											theCan.redraw();
										}

									}, md5Hash(uniqueFeedEntryId));

							IContextMenu ctxmen = getFactory().createContextMenu(btn_title);
							IContextMenuItem ctxmen_item1 = getFactory().createContextMenuItem(ctxmen, "feedreader.contextmenu.markread",
									new IContextMenuListener() { // TODO wording

										public void onAction(ClientEvent event, IBaseControl control) {
											// getPage().sendInfo("Mark as read - You clicked on " + control.getId());
											String js = "markElement('" + md5Hash(finalRefreshedFeed.getFeed().getTitle()) + "|"
													+ control.getId() + "');";
											getPage().getContext().sendJavaScript("clickElement", js);
											theCan.removeAllChildren(); // FIXME works only on second call - cookie might not be
											// set yet on first call
											goToItemView(theCan, dataBag);
											theCan.redraw();
										}

										public void onAction(ClientEvent event, ITable table, int row) {
										}

										public void onAction(ClientEvent event, ITree tree, String nodeId) {
										}

									});
							ctxmen_item1.setTooltip("feedreader.contextmenu.markread.tooltip");

							IContextMenuItem ctxmen_item2 = getFactory().createContextMenuItem(ctxmen, "feedreader.contextmenu.hide",
									new IContextMenuListener() { // TODO wording

										public void onAction(ClientEvent event, IBaseControl control) {
											getPage().sendInfo("Hide - You clicked on " + control.getId());
										}

										public void onAction(ClientEvent event, ITable table, int row) {
										}

										public void onAction(ClientEvent event, ITree tree, String nodeId) {
										}

									});
							ctxmen_item2.setTooltip("feedreader.contextmenu.hide.tooltip");

							Tooltip tip = new Tooltip("");
							tip.setLongHTMLText(entryContent);
							tip.setDelayTooltip(1000);

							btn_title.setTooltip(tip);
							btn_title.addCssClass("wgtFeedReaderItemViewButton");

							String srcDirectLinkButton = IMAGE_BASE_PATH + "btn_footer_link";
							if (StringUtils.isNotBlank(BUTTON_THEME)) {
								srcDirectLinkButton += "_" + BUTTON_THEME;
							}
							srcDirectLinkButton += ".gif";

							IButton btnDirectLink = getFactory().createLink(compLayout, srcDirectLinkButton, "",
									"feedreader.link.instruction", entry.getLink(), "_blank");
							btnDirectLink.setActionListener(new IActionListener() {

								public void onAction(ClientEvent event) {
									// send javascript that creates a cookie on client side
									String js = "markElement('" + btn_title.getId() + "');";
									getPage().getContext().sendJavaScript("clickElement", js); // TODO not in button click event
									// --> scroll event?

									// style instructions
									// if (iterations % 2 == 0) { // row even
									// set special style for formerly read feed entries
									compLayout.getEcsTable().setClass("wgtFeedReaderItemViewContainer");
									compLayout.getEcsTable().setHeight(ITEM_HEIGHT);
									// } else { // row odd
									// compLayout.getEcsTable().setClass("wgtFeedReaderItemViewContainerOdd");
									// compLayout.getEcsTable().setHeight(ITEM_HEIGHT);
									// }
									// theCan.removeAllChildren();
									// goToItemView(theCan, dataBag);
									// theCan.redraw();

									// FIXME wirkungslos
									// goToItemView(theCan, dataBag); //FIXME fuehrt zu Fehlermeldung
								}

							});

							// IButton btnHideLink = getFactory().createLinkButton(compLayout, IMAGE_BASE_PATH +
							// "btn_footer_link.gif", "",
							// "hide", new IActionListener() {
							// public void onAction(ClientEvent event) {
							// // send javascript that creates a cookie on client side
							// String js = "hideElement('" + btn_title.getId() + "');";
							// getPage().getContext().sendJavaScript("hideElement", js);
							// }
							// });

							// get formerly manipulated feed entries from cookies
							List<String> cookieNames = new ArrayList<String>();
							for (Cookie ck : Page.getServletRequest().getCookies()) {
								cookieNames.add(ck.getName());
							}

							// only show non-hidden elements
							if (cookieNames.contains(COOKIENAME_PREFIX_HIDDEN + btn_title.getId())) {
								// TODO do anything (hide entry e.g.)
							}

							final String cookieEntryStr = COOKIENAME_PREFIX_READ + md5Hash(finalRefreshedFeed.getFeed().getTitle()) + "|"
									+ btn_title.getId();
							// style instructions
							if (iterations % 2 == 0) { // row even
								if (cookieNames.contains(cookieEntryStr)) {
									// set special style for formerly read feed entries
									compLayout.getEcsTable().setClass("wgtFeedReaderItemViewContainer");
								}
								else {
									compLayout.getEcsTable().setClass("wgtFeedReaderItemViewContainerNew");
								}
							}
							else { // row odd
								if (cookieNames.contains(cookieEntryStr)) {
									compLayout.getEcsTable().setClass("wgtFeedReaderItemViewContainerOdd");
								}
								else {
									compLayout.getEcsTable().setClass("wgtFeedReaderItemViewContainerOddNew");
								}
							}
							compLayout.getEcsTable().setHeight(ITEM_HEIGHT);
							compLayout.getEcsTable().setStyle("width: " + (this.getWidth() - 4) + "px");

							// highlighting
							if (cookieNames.contains(cookieEntryStr)) {
								compLayout.getEcsTable().setOnMouseOver("doListMouseOver(2, this);");
							}
							else {
								compLayout.getEcsTable().setOnMouseOver("doListMouseOver(3, this);");
							}
							if (iterations % 2 == 0) { // row even
								if (cookieNames.contains(cookieEntryStr)) {
									compLayout.getEcsTable().setOnMouseOut("doListMouseOut(2, this);");
								}
								else {
									compLayout.getEcsTable().setOnMouseOut("doListMouseOut(3, this);");
								}
							}
							else { // row odd
								if (cookieNames.contains(cookieEntryStr)) {
									compLayout.getEcsTable().setOnMouseOut("doListMouseOut(2, this);");
								}
								else {
									compLayout.getEcsTable().setOnMouseOut("doListMouseOut(3, this);");
								}
							}

							canLayout.newRow();
						}
					}
					catch (ClassCastException e) {
					}
				}
			}
			else {
				ILabel lblNoItems = getFactory().createLabel(canLayout, "feedreader.itemview.empty");
				lblNoItems.addCssClass("wgtFeedReaderTextNotification");
				canLayout.addCssClass("wgtFeedReaderFeedViewContainer");
			}

			if (finalRefreshedFeed.getFeed().getEntries().size() <= this.getNumberOfViewableItems()) {
				skipFooterNavigation = true;
			}
		}

		if (!skipFooterNavigation) {
			this.showFooterNavigation(theCan, dataBag);
		}
		this.endFeedContainer(theCan);
	}

	/**
	 * 
	 * @param theCan
	 * @param dataBag
	 */
	private void goToItemDetailView(final ICanvas theCan, IDataBag dataBag, String connectionString) {
		SyndEntry entry = (SyndEntry)dataBag.getDelegate();
		SyndFeed feed = entry.getSource();
		FeedAndConnection fdCon = new FeedAndConnection(feed, connectionString);
		final IDataBag db_feed = new DataBag(fdCon);

		this.startFeedContainer(theCan);
		this.showFeedTitle(theCan, ITEM_DETAIL_VIEW);
		if (this.getStartView() == ITEM_VIEW) {
			this.showFeedNavigation(theCan, new boolean[] { true, false, false, true }, ITEM_DETAIL_VIEW, db_feed, entry);
		}
		else {
			this.showFeedNavigation(theCan, new boolean[] { true, true, false, true }, ITEM_DETAIL_VIEW, db_feed, entry);
		}

		ITableLayout canLayout = getFactory().createTableLayout(theCan);

		IButton btn_title = getFactory().createLink(canLayout, null, entry.getTitle(), null, entry.getLink(), "_blank");
		btn_title.addCssClass("wgtFeedReaderItemDetailViewButton");
		canLayout.newRow();

		Date theDate = null;
		String dateString = "";
		if (entry.getUpdatedDate() != null) {
			theDate = entry.getUpdatedDate();
		}
		else {
			theDate = entry.getPublishedDate();
		}

		Locale lang;
		if (StringUtils.isNotBlank(feed.getLanguage())) {
			lang = new Locale(feed.getLanguage());
		}
		else {
			lang = TextService.getLocale();
		}

		DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, lang);
		if (theDate != null) {
			dateString = df.format(theDate);
		}

		Label lbl_desc = (Label)getFactory().createLabel(canLayout,
				"<em style='display:block; height:5px'>" + dateString + "</em><br />" + entry.getDescription().getValue());
		lbl_desc.setCssClass("wgtFeedReaderItemDetailViewDescription");
		lbl_desc.getStyle().add("height", (this.boxHeight - 61) + "px");
		canLayout.newRow();

		this.endFeedContainer(theCan);
	}

	/**
	 * 
	 * @param feedConnections
	 * @throws Exception
	 */
	private void loadFeeds(List<String> feedConnections) throws Exception {
		FeedAndConnection inputFeed = null;
		this.getAllFeeds().clear();
		for (String feedConnectionString : feedConnections) {
			Authenticator.setDefault(new FeedReaderAuthenticator(FEEDUSER_NAME, FEEDUSER_PWD));

			URL feedUrl = new URL(feedConnectionString);
			BufferedReader in = new BufferedReader(new InputStreamReader(feedUrl.openStream()));

			SyndFeedInput sfi = new SyndFeedInput(true);

			inputFeed = new FeedAndConnection(sfi.build(in), feedConnectionString);
			this.getAllFeeds().add(inputFeed);

			in.close();
		}
		this.setInputFeed(this.getAllFeeds().get(0)); // TODO set to certain feed?
	}

	/**
	 * 
	 */
	public void refreshFeedItems(FeedAndConnection feed) {
		String connect = feed.getConnection();

		int existingIndex = -1, i = 0;
		for (FeedAndConnection fac : this.getAllFeeds()) {
			if (fac.getConnection().equals(connect)) {
				existingIndex = i;
				break;
			}
			i++;
		}

		if (existingIndex >= 0) {
			this.getFeedConnections().set(existingIndex, connect);
		}
		else { // element not existing - should never be called
			this.getFeedConnections().add(connect);
		}

		try {
			this.loadFeeds(this.getFeedConnections());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param startView the startView to set
	 */
	public void setStartView(int startView) {
		this.startView = startView;
	}

	/**
	 * @return the startView
	 */
	public int getStartView() {
		return startView;
	}

	/**
	 * 
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 
	 * @param height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getFeedConnections() {
		return feedConnections;
	}

	/**
	 * overwrites all formerly given feed connections
	 * 
	 * @param feedConnections
	 */
	public void setFeedConnections(List<String> feedConnections) {
		this.feedConnections = feedConnections;
		this.feedConnection = feedConnections.get(0);
		try {
			allFeeds = new ArrayList<FeedAndConnection>();
			this.loadFeeds(feedConnections);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getFeedConnection() {
		return feedConnection;
	}

	/**
	 * overwrites all formerly given feed connections
	 * 
	 * @param feedConnection
	 */
	public void setFeedConnection(String feedConnection) {
		this.feedConnection = feedConnection;
		this.setFeedConnections(Arrays.asList(feedConnection));
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
		this.setShowTitle(true);
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	public int getNumberOfViewableItems() {
		return numberOfViewableItems;
	}

	/**
	 * 
	 * @param numberOfViewableItems - must not be smaller than 1
	 */
	public void setNumberOfViewableItems(int numberOfViewableItems) {
		if (numberOfViewableItems > 0) {
			this.numberOfViewableItems = numberOfViewableItems;
		}
		else {
			this.numberOfViewableItems = DEFAULT_OFFSET;
		}
	}

	/**
	 * @param isShowTitle the isShowTitle to set
	 */
	private void setShowTitle(boolean isShowTitle) {
		this.isShowTitle = isShowTitle;
	}

	/**
	 * @return the isShowTitle
	 */
	private boolean isShowTitle() {
		return isShowTitle;
	}

	/**
	 * 
	 * @return
	 */
	private FeedAndConnection getInputFeed() {
		return inputFeed;
	}

	/**
	 * 
	 * @param inputFeed
	 */
	private void setInputFeed(FeedAndConnection inputFeed) {
		this.inputFeed = inputFeed;
	}

	/**
	 * adds feed connections to the existing list of feed connections
	 * 
	 * @param feedConnections
	 */
	public void addFeedConnections(List<String> feedConnections) {
		this.getFeedConnections().addAll(feedConnections);
		try {
			this.loadFeeds(feedConnections);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * adds one feed connection to the existing list of feed connections
	 * 
	 * @param feedConnections
	 */
	public void addFeedConnection(String feedConnection) {
		this.addFeedConnections(Arrays.asList(feedConnection));
	}

	/************************************/
	static class FeedAndConnection {
		SyndFeed feed;
		String connection;

		FeedAndConnection(SyndFeed feed, String connection) {
			this.feed = feed;
			this.connection = connection;
		}

		public SyndFeed getFeed() {
			return feed;
		}

		public void setFeed(SyndFeed feed) {
			this.feed = feed;
		}

		public String getConnection() {
			return connection;
		}

		public void setConnection(String connection) {
			this.connection = connection;
		}
	}

	/** 
	 * 
	 */
	private void setAllFeeds(List<SyndFeed> allFeeds, List<String> connectionStrings) {
		List<FeedAndConnection> allFeedsWithConnectionString = new ArrayList<FeedAndConnection>();
		for (int i = 0; i < allFeeds.size(); i++) {
			FeedAndConnection feedAndConnectionObject = new FeedAndConnection(allFeeds.get(i), connectionStrings.get(i));
			allFeedsWithConnectionString.add(feedAndConnectionObject);
		}
		this.allFeeds = allFeedsWithConnectionString;
	}

	/**
	 * 
	 * @return
	 */
	private List<FeedAndConnection> getAllFeeds() {
		return allFeeds;
	}

	/**
	 * @param feedTitle the feedTitle to set
	 */
	private void setFeedTitle(String feedTitle) {
		this.feedTitle = feedTitle;
	}

	/**
	 * @return the feedTitle
	 */
	private String getFeedTitle() {
		return feedTitle;
	}

	/**
	 * 
	 */
	@Override
	protected void createControls(WebGuiFactory factory, ICanvas viewConnector) {
		viewConnector.getStyle().addWidth(this.width + "px");
		viewConnector.getStyle().addHeight(this.height + "px");
		viewConnector.setScrollable(false);

		switch (this.getStartView()) {
			case FEED_VIEW:
				this.goToFeedView(viewConnector);
				break;
			case ITEM_VIEW:
				FeedAndConnection feed = this.getInputFeed();
				IDataBag db = new DataBag(feed);
				this.setFeedTitle(feed.getFeed().getTitle());
				this.goToItemView(viewConnector, db);
				break;
			default:
				this.goToFeedView(viewConnector);
		}

		String jsCookieSet = "var today = new Date();\n"
				+ "var expiringDate = new Date();\n"
				+ "var oneMonth = today.getTime() + (30 * 24 * 60 * 60 * 1000);\n"
				+ "expiringDate.setTime(oneMonth);  \n"
				+ "function markElement(clickedElement) {\n"
				+ "	if (document.cookie) {\n"
				+ "		document.cookie = \""
				+ COOKIENAME_PREFIX_READ
				+ "\" + clickedElement + \"=\" + clickedElement + \"|\" + today.toString() + \"; expires=\" + expiringDate.toGMTString() + \";\";\n"
				+ "	}\n"
				+ "}\n"
				+ "function hideElement(clickedElement) {\n"
				+ "	if (document.cookie) {\n"
				+ "		document.cookie = \""
				+ COOKIENAME_PREFIX_HIDDEN
				+ "\" + clickedElement + \"=\" + clickedElement + \"|\" + today.toString() + \"; expires=\" + expiringDate.toGMTString() + \";\";\n"
				+ "	}\n" + "}";
		ILabel lbl_js = getFactory().createLabel(layout, "<script type='text/javascript'>" + jsCookieSet + "</script>");
	}

	/**
	 * Converts a string into an md5-hash-expression. Is used to avoid problems with entities, blanks and quotes in text fragments
	 * that are used for identifying HTML objects.
	 * 
	 * @param hashStr
	 * @return
	 */
	public static String md5Hash(String hashStr) {
		StringBuffer stringBuffer = new StringBuffer(hashStr.length());
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(hashStr.getBytes());
			Formatter f = new Formatter(stringBuffer);
			for (byte b : md5.digest()) {
				f.format("%02x", b);
			}
		}
		catch (NoSuchAlgorithmException e) {
			return "";
		}
		return stringBuffer.toString();
	}
}
