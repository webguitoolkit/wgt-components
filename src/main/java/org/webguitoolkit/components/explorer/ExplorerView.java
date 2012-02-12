/*
Copyright 2008 Endress+Hauser Infoserve GmbH&Co KG
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing permissions
and limitations under the License.
*/
package org.webguitoolkit.components.explorer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webguitoolkit.components.preview.PreviewView;
import org.webguitoolkit.messagebox.MessageBox;
import org.webguitoolkit.tools.document.IDirectory;
import org.webguitoolkit.tools.document.IDocumentRepository;
import org.webguitoolkit.tools.document.IFile;
import org.webguitoolkit.tools.document.impl.DocumentRepositoryException;
import org.webguitoolkit.ui.base.DataBag;
import org.webguitoolkit.ui.base.IDataBag;
import org.webguitoolkit.ui.base.WGTException;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractView;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.contextmenu.BaseContextMenuListener;
import org.webguitoolkit.ui.controls.contextmenu.ContextMenu;
import org.webguitoolkit.ui.controls.dialog.DynamicDialog;
import org.webguitoolkit.ui.controls.event.ClientEvent;
import org.webguitoolkit.ui.controls.event.IActionListener;
import org.webguitoolkit.ui.controls.event.IServerEventListener;
import org.webguitoolkit.ui.controls.event.ServerEvent;
import org.webguitoolkit.ui.controls.form.Button;
import org.webguitoolkit.ui.controls.form.Compound;
import org.webguitoolkit.ui.controls.form.Label;
import org.webguitoolkit.ui.controls.form.Text;
import org.webguitoolkit.ui.controls.form.fileupload.AbstractUploadFileHandler;
import org.webguitoolkit.ui.controls.form.fileupload.FileUpload;
import org.webguitoolkit.ui.controls.layout.TableLayout;
import org.webguitoolkit.ui.controls.table.ITable;
import org.webguitoolkit.ui.controls.table.Table;
import org.webguitoolkit.ui.controls.table.TableColumn;
import org.webguitoolkit.ui.controls.table.renderer.ImageColumnRenderer;
import org.webguitoolkit.ui.controls.tree.AbstractTreeListener;
import org.webguitoolkit.ui.controls.tree.GenericTreeModel;
import org.webguitoolkit.ui.controls.tree.GenericTreeNode;
import org.webguitoolkit.ui.controls.tree.ITree;
import org.webguitoolkit.ui.controls.tree.Tree;
import org.webguitoolkit.ui.controls.tree.TreeNodeHandler;
import org.webguitoolkit.ui.controls.util.TextService;
import org.webguitoolkit.ui.controls.util.conversion.ConvertUtil;
import org.webguitoolkit.ui.controls.util.style.Style;

/**
 * The ExplorerView is intended to browse thru a DocumentStore (see DocumentStore API). It has a Windows-Explorer front-end with
 * the following functionality. <br>
 * Navigate in a tree within the directory structure of the DocumentStore depending on access rights.<br>
 * Show the files in the Directory.<br>
 * Enable to register (subscribe) for modification of a Directory<br>
 * Upload Files into a Directory<br>
 * Download Files from the DocumentStore<br>
 * Rename Directories and Files<br>
 * Delete Directories and Files<br>
 * Preview the File content (planned)<br>
 * To use the ExplorerVie in your WGT application you have to:<br>
 * Have a DocumentStore<br>
 * A MessageBox Implementation<br>
 * And to instantiate the Explorer as any other WGT-View via its constructor<br>
 * The following 2 lines are showing it exemplarily <code>
 IDocumentRepository docs = new FsDocumentRepository("D:\\test\\explorer", new TestAccessManager("peter"),
 new FileMessageBoxFactory("D:\\test\\messages"));

 ExplorerView explorerView = new ExplorerView(factory, viewConnector, docs);
 * </code> If you do so it will just show the Directory / File structure. No
 * context menus (beside the reload) are available. To get the context menus in the tree and the File list you have to register
 * them. There are a number of predefined context menus available in the ExplorerView class. All above mentioned functions are
 * implemented in those predefine context menus. You just have to register them. <br>
 * <code>

 explorerView.addTableContextMenu("app.view@View", explorerView.PREVIEW_FILE_LISTENER);
 explorerView.addTableContextMenu("app.view@Download", explorerView.DOWNLOAD_FILE_LISTENER);
 explorerView.addTableContextMenu("app.view@Delete", explorerView.DELETE_FILE_LISTENER);
 explorerView.addTableContextMenu("app.view@Rename", explorerView.RENAME_FILE_LISTENER);

 explorerView.addTreeContextMenu("app.view@Upload", explorerView.UPLOAD_FILE_LISTENER);
 explorerView.addTreeContextMenu("app.view@Delete", explorerView.DELETE_FOLDER_LISTENER);
 explorerView.addTreeContextMenu("app.view@Rename", explorerView.RENAME_FOLDER_LISTENER);
 explorerView.addTreeContextMenu("app.subscribe@Subscribe", explorerView.SUBSCRIBE_LISTENER);
 explorerView.addTreeContextMenu("app.unsubscribe@Unsubscribe", explorerView.UNSUBSCRIBE_LISTENER);
 explorerView.addTreeContextMenu("app.reload@Reload", explorerView.RELOAD_LISTENER);

 </code> Of cause you are able to develop your own listener and register it: <code>
 final IServerEventListener NULL_LISTENER = new IServerEventListener() {
 public void handle(ServerEvent event) {
 getPage().sendInfo("NULL_LISTENER");
 }
 };
 explorerView.addTreeContextMenu("my hand-made menu", NULL_LISTENER);
 </code> There are some options to control the
 * appearance of the ExplorerView in terms of size and so on. For details see the JavaDoc of this class.<br>
 * The following resource bundle keys are used to translate the UI into other languages (under dev.) <br>
 * view.explorer.name for name column <br>
 * view.explorer.name for name column <br>
 * view.explorer.size for file size column <br>
 * view.explorer.lastModified for timestamp column<br>
 * view.explorer.type file type
 * 
 * @author peter
 * 
 */
public class ExplorerView extends AbstractView {

	private static final String UTF_8 = "UTF-8";
	private static final int ONE_KB = 1024;
	private static final String PX = "px";
	public static final String EVENT_RESULT_PARAMETER = "result";
	protected static final String EVENT_RESULT_NODEID = "nodeid";
	private static final String TABLE_INITIALIZER = "extension,20px;name,250px;size,50px;modifiedAt,150px";
	private static final String AT_SIGN = "@";
	private static final String RESOURCE_PREFIX = "view.explorer.";

	// where to look for files in a directory
	protected static final String FILES_PROPERTY = "files";
	protected static final String CAPTION_PROPERTY = "name";
	protected static final String DIRECTORIES_PROPERTY = "directories";
	private static final String SIZE_PROPERTY = "size";
	private static final String LAST_MODIFIED_PROPERTY = "modifiedAt";
	private static final String EXTENSION_PROPERTY = "extension";

	protected static final String IMAGE_FOLDER_OPEN = "images/wgt/tree/folderOpen.gif";
	protected static final String IMAGE_FOLDER_CLOSED = "images/wgt/tree/folderClosed.gif";

	private static final String IMAGE_DEFAULT = "images/wgt/new.gif";
	private static final String IMAGE_MSDOC = "images/wgt/icons/msdoc.gif";
	private static final String IMAGE_XML = "images/wgt/icons/xml.gif";
	private static final String IMAGE_MSXLS = "images/wgt/icons/msxls.gif";
	private static final String IMAGE_PDF = "images/wgt/icons/pdf.gif";
	private static final String IMAGE_TXT = "images/wgt/new.gif";

	private Table table;
	private Tree tree;

	public int tableWidth = 450;
	public int treeWidth = 200;
	public int treeHeight = 445;
	private boolean showNodesInTree = false;

	private Map<String, String> imageMap = new HashMap<String, String>();
	private int tableRows = 20;

	private Map<String, Integer> tableContextMenuEvents = new LinkedHashMap<String, Integer>();
	private Map<String, Integer> treeContextMenuEvents = new LinkedHashMap<String, Integer>();

	private IDocumentRepository documentRepository;
	private MessageBox messageBox;

	private static Log log = LogFactory.getLog(ExplorerView.class);

	private String MESSAGE_LOC;
	private String TEST_DOCREP;

	/**
	 * @param factory WGT factory
	 * @param viewConnector where to plug in the view
	 * @param INode the file system URL to start browsing from
	 */
	public ExplorerView(WebGuiFactory factory, ICanvas viewConnector, IDocumentRepository documents) {
		super(factory, viewConnector);

		this.documentRepository = documents;
		imageMap.put("*", IMAGE_DEFAULT);
		imageMap.put(".doc", IMAGE_MSDOC);
		imageMap.put(".xls", IMAGE_MSXLS);
		imageMap.put(".xml", IMAGE_XML);
		imageMap.put(".txt", IMAGE_TXT);
		imageMap.put(".pdf", IMAGE_PDF);
	}

	// private TreeNodeHandler writeableFolderHandler = null;

	@Override
	protected void createControls(WebGuiFactory factory, ICanvas viewConnector) {
		try {

			TableLayout colLayout = factory.newTableLayout(viewConnector);

			tree = factory.newTree(colLayout);
			tree.getStyle().add(Style.WIDTH, treeWidth + PX);
			tree.getStyle().add(Style.HEIGHT, treeHeight + PX);
			colLayout.getCurrentCell().setStyle("vertical-align : top;width : 200px");

			// check this if round trip is needed on check
			tree.setFireOnCheckEvent(true);

			// create tree node handler for Node instances digging for "children"
			// and presenting itself with folder icon. Nodes can be dragged and
			// dropped

			TreeNodeHandler writeableFolderHandler = new TreeNodeHandler("writeable", Boolean.TRUE);
			TreeNodeHandler defaultFolderHandler = new TreeNodeHandler("writeable", Boolean.FALSE);

			if (showNodesInTree)
				writeableFolderHandler.setChildSelectors(new String[] { DIRECTORIES_PROPERTY, FILES_PROPERTY });
			else
				writeableFolderHandler.setChildSelectors(new String[] { DIRECTORIES_PROPERTY });

			writeableFolderHandler.setDisplayProperty(CAPTION_PROPERTY);
			writeableFolderHandler.setIconSrc(IMAGE_FOLDER_CLOSED, IMAGE_FOLDER_OPEN, IMAGE_FOLDER_CLOSED);
			writeableFolderHandler.setHasCheckboxes(false);
			// add node action listener
			writeableFolderHandler.setListener(new TreeListener());

			defaultFolderHandler.setDisplayProperty(CAPTION_PROPERTY);
			defaultFolderHandler.setIconSrc(IMAGE_FOLDER_CLOSED, IMAGE_FOLDER_OPEN, IMAGE_FOLDER_CLOSED);
			defaultFolderHandler.setHasCheckboxes(false);
			// add node action listener
			defaultFolderHandler.setListener(new TreeListener());

			TreeNodeHandler fileHandler = new TreeNodeHandler(IFile.class);
			fileHandler.setChildSelectors(new String[] { FILES_PROPERTY });
			fileHandler.setDisplayProperty(CAPTION_PROPERTY);
			fileHandler.setIconSrc(IMAGE_DEFAULT, IMAGE_DEFAULT, IMAGE_DEFAULT);
			fileHandler.setHasCheckboxes(false);
			// add node action listener
			fileHandler.setListener(new TreeListener());

			// create default node handler for all non.Nodes instances
			TreeNodeHandler defaultHandler = new TreeNodeHandler();
			defaultHandler.setDisplayProperty(CAPTION_PROPERTY);
			defaultHandler.setIconSrc(IMAGE_DEFAULT, IMAGE_DEFAULT, IMAGE_DEFAULT);
			defaultHandler.setHasCheckboxes(false);

			// add node action listener
			defaultHandler.setListener(new TreeListener());

			// create a generic tree model and apply to tree
			GenericTreeModel gtm = new GenericTreeModel(true, true, true, false);

			gtm.setDefaultTreeNodeHandler(defaultHandler);
			gtm.addTreeNodeHandler(fileHandler);
			gtm.addTreeNodeHandler(writeableFolderHandler);
			gtm.addTreeNodeHandler(defaultFolderHandler);

			gtm.setRoot(documentRepository.getRoot());

			tree.setModel(gtm);

			tree.load();

			// configure tree context menu
			if (!treeContextMenuEvents.isEmpty()) {
				ContextMenu folderContextMenu = getFactory().newContextMenu(tree);
				for (Iterator<String> iter = treeContextMenuEvents.keySet().iterator(); iter.hasNext();) {
					final String key = iter.next();
					factory.newContextMenuItem(folderContextMenu, TextService.getString(key), new BaseContextMenuListener() {
						@Override
						public void onAction(ClientEvent event, ITree tree, String nodeId) {
							ServerEvent serverEvent = new ServerEvent(tree, treeContextMenuEvents.get(key));
							GenericTreeNode treeNode = (GenericTreeNode)tree.getModel().getTreeNode(nodeId);
							serverEvent.putParameter(EVENT_RESULT_PARAMETER, treeNode.getDataObject().getDelegate());
							serverEvent.putParameter(EVENT_RESULT_NODEID, nodeId);
							fireServerEvent(serverEvent);
						}
					});
				}
				writeableFolderHandler.setContextMenu(folderContextMenu);

				ContextMenu defaultContextMenu = getFactory().newContextMenu(tree);
				registerListener(100, SUBSCRIBE_LISTENER);
				factory.newContextMenuItem(defaultContextMenu, TextService.getString("Subscribe"), new InternalTreeContextMenuListener(100));

				registerListener(101, UNSUBSCRIBE_LISTENER);
				factory.newContextMenuItem(defaultContextMenu, TextService.getString("Unsubscribe"), new InternalTreeContextMenuListener(
						101));

				registerListener(102, RELOAD_LISTENER);
				factory.newContextMenuItem(defaultContextMenu, TextService.getString("Reload"), new InternalTreeContextMenuListener(102));
				defaultFolderHandler.setContextMenu(defaultContextMenu);

				// fileHandler.setContextMenu(folderContextMenu);
				defaultHandler.setContextMenu(folderContextMenu);
			}

			table = factory.newTable(colLayout, null, tableRows);
			table.getStyle().add(Style.WIDTH, tableWidth + PX);
			colLayout.getCurrentCell().setStyle("vertical-align : top");

			TableColumn col = factory.newTableColumn(table, ""/*
																* getResourceKey(EXTENSION_PROPERTY
																* )
																*/, EXTENSION_PROPERTY, true);
			col.setRenderer(new ImageColumnRenderer());

			// configure table context menu
			if (!tableContextMenuEvents.isEmpty()) {
				ContextMenu cMenu = factory.newContextMenu(table);

				for (Iterator<String> iter = tableContextMenuEvents.keySet().iterator(); iter.hasNext();) {
					// create context menu entries
					String key = iter.next();
					int event = tableContextMenuEvents.get(key);
					String label = TextService.getString(key);
					factory.newContextMenuItem(cMenu, label, new InternalTableContextMenuListener(event));
				}
			}

			TableColumn tcol = factory.newTableColumn(table, getResourceKey(CAPTION_PROPERTY), CAPTION_PROPERTY, true);
			tcol = factory.newTableColumn(table, getResourceKey(SIZE_PROPERTY), SIZE_PROPERTY, true);
			tcol = factory.newTableColumn(table, getResourceKey(LAST_MODIFIED_PROPERTY), LAST_MODIFIED_PROPERTY, true);
			tcol.setConverter(ConvertUtil.DATE_TIME_CONVERTER);
			table.loadColumnConfig(TABLE_INITIALIZER);
		}
		catch (DocumentRepositoryException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to create a valid resource key
	 * 
	 * @param name
	 * @return
	 */
	private String getResourceKey(String name) {
		return RESOURCE_PREFIX + name + AT_SIGN + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private Integer eventNumber = new Integer(0);

	/**
	 * adds a context menu entry to the tree and registers the listener for this event.
	 * 
	 * @return the event number
	 */

	public int addTreeContextMenu(String titelKey, IServerEventListener listener) {
		int event;
		synchronized (eventNumber) {
			event = ++eventNumber;
			treeContextMenuEvents.put(titelKey, eventNumber);
			registerListener(eventNumber, listener);
		}
		return event;
	}

	/**
	 * adds a context menu entry to the table and registers the listener for this event.
	 * 
	 * @return the event number
	 */
	public int addTableContextMenu(String titelKey, IServerEventListener listener) {
		int event;
		synchronized (eventNumber) {
			event = ++eventNumber;
			tableContextMenuEvents.put(titelKey, eventNumber);
			registerListener(eventNumber, listener);
		}
		return event;
	}

	/**
	 * Set the height of the tree in the view
	 * 
	 * @param treeHeight
	 */
	public void setTreeHeight(int treeHeight) {
		this.treeHeight = treeHeight;
	}

	/**
	 * Set the initial number of rows for the table
	 * 
	 * @param treeRows
	 */
	public void setTableRows(int treeRows) {
		this.tableRows = treeRows;
	}

	/**
	 * Initialize the width of the table. This is evaluated during creation of the component.
	 * 
	 * @param tableWidth
	 */
	public void setTableWidth(int tableWidth) {
		this.tableWidth = tableWidth;
	}

	/**
	 * Initialize the width of the table. This is evaluated during creation of the component.
	 * 
	 * @param treeWidth
	 */
	public void setTreeWidth(int treeWidth) {
		this.treeWidth = treeWidth;
	}

	/***************************************************************************
	 *
	 **************************************************************************/

	public int getTreeWidth() {
		return treeWidth;
	}

	public int getTableWidth() {
		return tableWidth;
	}

	public int getTableRows() {
		return tableRows;
	}

	public Map getImageMap() {
		return imageMap;
	}

	public int getTreeHeight() {
		return treeHeight;
	}

	/**
	 * @return the currently selected tree node
	 */
	public String getSelectedTreeNodeId() {
		String result = getTree().getSelectedNodeId();
		return result;
	}

	/**
	 * @return the instance of the tree
	 */
	public Tree getTree() {
		return tree;
	}

	/**
	 * @return the currently selected tree node
	 */
	public IDataBag getSelectedTableRow() {
		IDataBag bag = getTable().getSelectedRow();
		return bag;
	}

	/**
	 * @return the instance of the tree
	 */
	public Table getTable() {
		return table;
	}

	/***************************************************************************
	 *
	 **************************************************************************/

	class TreeListener extends AbstractTreeListener {

		@Override
		public void onTreeNodeClicked(ITree tree, String nodeId) {
			try {
				GenericTreeNode treeNode = (GenericTreeNode)tree.getModel().getTreeNode(nodeId);
				IDirectory folder = (IDirectory)treeNode.getDataObject().getDelegate();
				List<DataBag> data = new ArrayList<DataBag>();
				List<IFile> files;
				files = folder.getFiles();

				for (Iterator<IFile> it = files.iterator(); it.hasNext();) {
					IFile docFile = it.next();
					DataBag bag = new DataBag(docFile);

					String name = docFile.getName();
					String ext = "";
					if (name.lastIndexOf('.') != -1) {
						ext = name.substring(name.lastIndexOf('.'));
					}
					String imageUrl = imageMap.get(ext);
					if (imageUrl == null)
						imageUrl = imageMap.get("*");
					bag.addProperty(EXTENSION_PROPERTY, imageUrl);
					// add tooltip
					bag.addProperty(EXTENSION_PROPERTY + ".title", ext);
					data.add(bag);
				}

				table.getDefaultModel().setTableData(data);
				String text = TextService.getString("view.explorer.title@Content of ");
				table.setTitle(text + folder.getName());
				table.reload();
			}
			catch (DocumentRepositoryException e) {
				throw new RuntimeException(e);
				// TODO-PZ handle error
			}
		}

	}

	/**
	 * A default listener that shows only a popup with the resource you have selected
	 */
	public IServerEventListener DEFAULT_LISTENER = new IServerEventListener() {
		public void handle(ServerEvent event) {
			getPage().sendInfo(
					"Event = " + event.getTypeAsInt() + ", Result = " + event.getParameter(ExplorerView.EVENT_RESULT_PARAMETER).toString()
							+ ", Type = " + event.getParameter(ExplorerView.EVENT_RESULT_PARAMETER).getClass().getSimpleName() + ".class");
		}
	};

	public IServerEventListener PREVIEW_FILE_LISTENER = new IServerEventListener() {
		public void handle(ServerEvent event) {
			try {
				IFile file = (IFile)event.getParameter(ExplorerView.EVENT_RESULT_PARAMETER);

				DynamicDialog dialog = new DynamicDialog(getPage());
				dialog.setWidth(500);
				dialog.setHeight(420);
				dialog.setWindowTitle(file.getRelativePath());

				PreviewView previewView = new PreviewView(getFactory(), dialog.getWindow(), file.getRelativePath(), null);
				previewView.show();
			}
			catch (DocumentRepositoryException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * This listener controls the renaming of folders in the tree. It also prompts for the new name and performs the renaming if
	 * no folder with the new name exists.
	 */
	public IServerEventListener RENAME_FOLDER_LISTENER = new IServerEventListener() {
		public void handle(ServerEvent event) {
			final IDirectory directory = (IDirectory)event.getParameter(ExplorerView.EVENT_RESULT_PARAMETER);
			final Tree tree = (Tree)event.getSource();
			String nodeid = (String)event.getParameter(ExplorerView.EVENT_RESULT_NODEID);
			final GenericTreeNode targetNode = (GenericTreeNode)tree.getModel().getTreeNode(nodeid);
			final DynamicDialog dd = new DynamicDialog(getPage());

			ICanvas canvas = dd.getWindow();

			Compound compound = getFactory().newCompound(canvas);
			compound.setBag(new DataBag(""));
			TableLayout layout = getFactory().newTableLayout(compound);

			getFactory().newLabel(layout, TextService.getString("prompt.rename.newname@Enter the new name"));
			layout.newLine();
			final Text input = getFactory().newText(layout, "input");
			layout.newLine();

			getFactory().newButton(layout, null, "Rename", "rename", new IActionListener() {
				public void onAction(ClientEvent event) {
					try {
						if (!directory.rename(input.getValue()))
							getPage().sendInfo("Rename not successful");
						else
							tree.updateNodeLabel(targetNode);
						dd.destroy();
					}
					catch (DocumentRepositoryException e) {
						throw new RuntimeException(e);
						// TODO-PZ handle error
					}
				}
			});
		}
	};

	/**
	 * This listener controls the deletion of folder in the tree. It checks if the folder is empty and prevents from deleting if
	 * not. It also propts for confirmation if a empty folder is going to be deleted.
	 */
	public IServerEventListener DELETE_FOLDER_LISTENER = new IServerEventListener() {
		public void handle(ServerEvent event) {
			try {
				final IDirectory directory = (IDirectory)event.getParameter(ExplorerView.EVENT_RESULT_PARAMETER);
				String nodeid = (String)event.getParameter(ExplorerView.EVENT_RESULT_NODEID);
				final Tree tree = (Tree)event.getSource();
				GenericTreeNode targetNode = (GenericTreeNode)tree.getModel().getTreeNode(nodeid);
				final GenericTreeNode parentNode = (GenericTreeNode)targetNode.getParent();

				final DynamicDialog dd = new DynamicDialog(getPage());

				ICanvas canvas = dd.getWindow();

				Compound compound = getFactory().newCompound(canvas);
				compound.setBag(new DataBag(""));
				TableLayout layout = getFactory().newTableLayout(compound);

				if (directory.getSize() != 0) {
					getFactory().newLabel(layout, TextService.getString("prompt.delete.notempty@The folder must be empty to delete it"));
					layout.newLine();

					getFactory().newButton(layout, null, "Close", "close this window", new IActionListener() {
						public void onAction(ClientEvent event) {
							dd.destroy();
						}
					});
				}
				else {
					getFactory().newLabel(layout, TextService.getString("prompt.delete.confirm@Confirm deletion of " + directory.getName()));
					layout.newLine();

					getFactory().newButton(layout, null, "Delete", "Folder will be deleted", new IActionListener() {
						public void onAction(ClientEvent event) {
							try {
								directory.remove(false);
								tree.reloadChildren(parentNode);
								dd.destroy();
							}
							catch (DocumentRepositoryException e) {
								throw new RuntimeException(e);
								// TODO-PZ handle error
							}
						}
					});
				}
			}
			catch (DocumentRepositoryException e) {
				throw new RuntimeException(e);
				// TODO-PZ handle error
			}

		}
	};

	/**
	 * This listener allows to upload files into a directory.
	 */
	public IServerEventListener UPLOAD_FILE_LISTENER = new IServerEventListener() {
		public void handle(ServerEvent event) {
			final IDirectory directory = (IDirectory)event.getParameter(ExplorerView.EVENT_RESULT_PARAMETER);
			String nodeid = (String)event.getParameter(ExplorerView.EVENT_RESULT_NODEID);
			final Tree tree = (Tree)event.getSource();
			final GenericTreeNode targetNode = (GenericTreeNode)tree.getModel().getTreeNode(nodeid);

			final DynamicDialog dd = new DynamicDialog(getPage());

			ICanvas canvas = dd.getWindow();

			Compound compound = getFactory().newCompound(canvas);
			compound.setBag(new DataBag(""));
			TableLayout layout = getFactory().newTableLayout(compound);

			// create popup coontet
			FileUpload fileUpload = getFactory().newFileUpload(layout);
			// TODO-MH : check why this is required
			fileUpload.setVisible(true);
			fileUpload.setProperty("filename");

			layout.newLine();

			final Label successMsg = getFactory().newLabel(layout, "explorer.upload.info@Select your file(s) and press Submit");

			layout.newLine();

			final Button okButton = getFactory().newButton(layout, null, "OK", "Closes this dialog", new IActionListener() {
				public void onAction(ClientEvent event) {
					tree.selectNode(targetNode, true);
					dd.destroy();
				}
			});
			okButton.setVisible(false); // OK button becomes visible when the
			// upload is finished

			fileUpload.setActionListener(new IActionListener() {

				public void onAction(ClientEvent event) {
					okButton.setVisible(true);
					successMsg.setTextKey("explorer.upload.success@Your file(s) were uploaded successfully");
				}
			});

			fileUpload.setFileHandler(new AbstractUploadFileHandler() {

				@Override
				public List getEventParameters() {
					return getFileNames();
				}

				@Override
				public void processUpload() throws Exception {
					List<FileItem> fileItems = getFileItems();
					for (Iterator<FileItem> it = fileItems.iterator(); it.hasNext();) {
						FileItem fi = it.next();

						// IFile file = directory.touch(fi.getName());
						IFile file = null;

						if (file != null) {
							try {
								// file.writeStream(fi.getInputStream());
								directory.createFile(fi.getName(), fi.getInputStream(), true);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
						else
							throw new RuntimeException("file " + file + " is NULL");
					}
				}

			});

		}
	};

	/**
	 * This listener allows to subscribe for changes on a folder a directory.
	 */
	public IServerEventListener SUBSCRIBE_LISTENER = new IServerEventListener() {

		public void handle(ServerEvent event) {
			IDirectory directory = (IDirectory)event.getParameter(ExplorerView.EVENT_RESULT_PARAMETER);
			String address = documentRepository.getAccessManager().getUserid();
			directory.subscribe(address);
		}
	};

	/**
	 * This listener allows to un-subscribe for changes on a folder a directory.
	 */
	public IServerEventListener UNSUBSCRIBE_LISTENER = new IServerEventListener() {
		public void handle(ServerEvent event) {
			IDirectory directory = (IDirectory)event.getParameter(ExplorerView.EVENT_RESULT_PARAMETER);
			String address = documentRepository.getAccessManager().getUserid();
			directory.unsubscribe(address);
		}
	};

	/**
	 * This listener allows to re-load the selected node in the tree.
	 */
	public IServerEventListener RELOAD_LISTENER = new IServerEventListener() {
		public void handle(ServerEvent event) {
			Tree tree = (Tree)event.getSource();
			String nodeid = (String)event.getParameter(ExplorerView.EVENT_RESULT_NODEID);
			GenericTreeNode targetNode = (GenericTreeNode)tree.getModel().getTreeNode(nodeid);
			tree.selectNode(targetNode, true);
		}
	};

	/**
	 * listener on the table to delete a file.
	 */
	public IServerEventListener DELETE_FILE_LISTENER = new IServerEventListener() {
		public void handle(ServerEvent event) {
			try {
				final IFile file = (IFile)event.getParameter(ExplorerView.EVENT_RESULT_PARAMETER);
				final DynamicDialog dd = new DynamicDialog(getPage());

				ICanvas canvas = dd.getWindow();
				Compound compound = getFactory().newCompound(canvas);
				compound.setBag(new DataBag(""));
				TableLayout layout = getFactory().newTableLayout(compound);

				boolean canWrite = true; // file.getDirectory().getWriteable();
				if (canWrite) {
					getFactory().newLabel(layout, TextService.getString("prompt.delete.confirm@Confirm deletion of " + file.getName()));
					layout.newLine();

					getFactory().newButton(layout, null, "Delete", "File will be deleted", new IActionListener() {
						public void onAction(ClientEvent event) {
							try {
								file.remove();
								GenericTreeNode targetNode = (GenericTreeNode)getTree().getModel().getTreeNode(getSelectedTreeNodeId());
								getTree().selectNode(targetNode, true);
								dd.destroy();
							}
							catch (DocumentRepositoryException e) {
								throw new RuntimeException(e);
								// TODO-PZ handle error
							}
						}
					});
				}
				else {
					getFactory().newLabel(layout,
							TextService.getString("prompt.delete.privilege@Missing privilege to delete <br>" + file.getName()));
				}
			}
			catch (DocumentRepositoryException e) {
				throw new RuntimeException(e);
				// TODO-PZ handle error
			}
		}
	};

	/**
	 * listener on the table to download a file.
	 */
	public IServerEventListener DOWNLOAD_FILE_LISTENER = new IServerEventListener() {
		public void handle(ServerEvent event) {
			IFile file = (IFile)event.getParameter(ExplorerView.EVENT_RESULT_PARAMETER);

			try {
				String fext = file.getExtension();
				String fname = file.getName();
				if (fext != null)
					fname = fname.substring(0, fname.length() - fext.length());
				System.out.println(fname + " " + fext);

				File temp = File.createTempFile(fname, fext);
				temp.deleteOnExit();

				InputStream in = file.getContent();
				OutputStream out = new FileOutputStream(temp);

				byte[] buf = new byte[ONE_KB];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();

				event.getSource().getPage().getContext().sendJavaScript("",
						"window.location.href='/WebGuiPatterns/FileDownload?file=" + URLEncoder.encode(temp.getPath(), UTF_8) + "'");

			}
			catch (Exception e) {
				throw new WGTException(e);
			}
		}
	};

	/**
	 * listener on the table to rename a file.
	 */
	public IServerEventListener RENAME_FILE_LISTENER = new IServerEventListener() {
		public void handle(ServerEvent event) {
			try {
				final IFile file = (IFile)event.getParameter(ExplorerView.EVENT_RESULT_PARAMETER);
				final DynamicDialog dd = new DynamicDialog(getPage());
				final Table table = (Table)event.getSource();

				ICanvas canvas = dd.getWindow();

				Compound compound = getFactory().newCompound(canvas);
				compound.setBag(new DataBag(""));
				TableLayout layout = getFactory().newTableLayout(compound);

				boolean canWrite = true; // file.getDirectory().getWriteable();
				if (canWrite) {
					getFactory().newLabel(layout, TextService.getString("prompt.rename.newname@Enter the new name"));
					layout.newLine();
					final Text input = getFactory().newText(layout, "input");
					layout.newLine();

					getFactory().newButton(layout, null, "Rename", "rename", new IActionListener() {
						public void onAction(ClientEvent event) {
							try {
								if (!file.rename(input.getValue()))
									getPage().sendInfo("Rename not successful");
								else {
									table.getDefaultModel();

									GenericTreeNode targetNode = (GenericTreeNode)getTree().getModel().getTreeNode(getSelectedTreeNodeId());
									getTree().selectNode(targetNode, true);
								}
								dd.destroy();
							}
							catch (DocumentRepositoryException e) {
								throw new RuntimeException(e);
								// TODO-PZ handle error
							}
						}
					});
				}
				else {
					getFactory().newLabel(layout,
							TextService.getString("prompt.delete.privilege@Missing privilege to rename <br>" + file.getName()));
				}

			}
			catch (DocumentRepositoryException e) {
				throw new RuntimeException(e);
				// TODO-PZ handle error
			}
		}
	};

	class InternalTableContextMenuListener extends BaseContextMenuListener {

		int eventNo;

		public InternalTableContextMenuListener(int eventNo) {
			super();
			this.eventNo = eventNo;
		}

		@Override
		public void onAction(ClientEvent event, ITable table, int row) {
			IDataBag bag = table.getRow(row);
			ServerEvent serverEvent = new ServerEvent(table, eventNo);
			serverEvent.putParameter(EVENT_RESULT_PARAMETER, bag.getDelegate());
			fireServerEvent(serverEvent);
		}
	}

	class InternalTreeContextMenuListener extends BaseContextMenuListener {

		int eventNo;

		public InternalTreeContextMenuListener(int eventNo) {
			super();
			this.eventNo = eventNo;
		}

		public void onAction(ClientEvent event, Tree tree, String nodeId) {
			ServerEvent serverEvent = new ServerEvent(tree, eventNo);
			GenericTreeNode treeNode = (GenericTreeNode)tree.getModel().getTreeNode(nodeId);
			serverEvent.putParameter(EVENT_RESULT_PARAMETER, treeNode.getDataObject().getDelegate());
			serverEvent.putParameter(EVENT_RESULT_NODEID, nodeId);
			fireServerEvent(serverEvent);
		}
	}

}
