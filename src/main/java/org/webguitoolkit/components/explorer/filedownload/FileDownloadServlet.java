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
/*
 * Created on Jul 21, 2005
 *
 */
package org.webguitoolkit.components.explorer.filedownload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.webguitoolkit.ui.base.WGTException;

/**
 * @author i102386
 * 
 */
public class FileDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 6592195008032345591L;

	/**
	 * Constructor of the object.
	 */
	public FileDownloadServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileName = request.getParameter("file");
		fileName = URLDecoder.decode(fileName, "UTF-8");

		try {
			response.setContentType("application/octetstream");
			response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "");
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			ServletOutputStream out = response.getOutputStream();

			// Write Header

			BufferedInputStream inStream = null;
			BufferedOutputStream outStream = null;

			try {
				inStream = new BufferedInputStream(new FileInputStream(new File(fileName)));
				outStream = new BufferedOutputStream(out);

				byte[] buffer = new byte[1024];
				int bytesRead;
				while (-1 != (bytesRead = inStream.read(buffer, 0, buffer.length))) {
					outStream.write(buffer, 0, bytesRead);
				}
			} catch (Exception e) {
				throw new WGTException( e );

			} finally {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			}

			out.flush();
		} catch (Exception e) {
			LogFactory.getLog(this.getClass()).error("Attachment download error:" + e);
		}

		// else {//file not found
		// response.sendRedirect("attachment/filenotfound.jsp");
		//	   	
		// }
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occure
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
