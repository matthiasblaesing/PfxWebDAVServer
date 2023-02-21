/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.ellipsis.webdav.server.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.ellipsis.webdav.HttpHeaders;

import nl.ellipsis.webdav.server.IMimeTyper;
import nl.ellipsis.webdav.server.ITransaction;
import nl.ellipsis.webdav.server.IWebDAVStore;
import nl.ellipsis.webdav.server.StoredObject;
import nl.ellipsis.webdav.server.locking.ResourceLocks;
import nl.ellipsis.webdav.server.util.CharsetUtil;
import nl.ellipsis.webdav.server.util.URLUtil;

public class DoGet extends DoHead {

	private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DoGet.class);
	private static Pattern RANGE_PATTERN = Pattern.compile("\\s*bytes\\s*=\\s*(\\d*)\\s*-\\s*(\\d*)\\s*");

	public DoGet(IWebDAVStore store, String dftIndexFile, String insteadOf404, ResourceLocks resourceLocks,
			IMimeTyper mimeTyper, int contentLengthHeader) {
		super(store, dftIndexFile, insteadOf404, resourceLocks, mimeTyper, contentLengthHeader);
	}

	@Override
	protected void doBody(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp, String path) {
		try {
			Long start = null;
			Long end = null;

			String rangeHeader = req.getHeader(HttpHeaders.RANGE);
			if(rangeHeader != null) {
				Matcher rangeMatcher = RANGE_PATTERN.matcher(rangeHeader);
				if(! rangeMatcher.matches()) {
					resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid range");
					return;
				}
				String startString = rangeMatcher.group(1).trim();
				String endString = rangeMatcher.group(2).trim();
				if(! startString.isEmpty()) {
					start = Long.valueOf(startString);
				}
				if(! endString.isEmpty()) {
					end = Long.valueOf(endString);
				}
			}

			if(start != null && end != null && start > end) {
				resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE, "Invalid range");
				return;
			}

			StoredObject so = _store.getStoredObject(transaction, path);
			if (so.isNullResource()) {
				String methodsAllowed = DeterminableMethod.determineMethodsAllowed(so);
				resp.addHeader(HttpHeaders.ALLOW, methodsAllowed);
				resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				return;
			}

			if(start != null && (start < 0 || start > so.getResourceLength())) {
				resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
				return;
			}

			if(start != null || end != null) {
				resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
				long startEv = start != null ? start : 0;
				long maxEv = so.getResourceLength();
				long endEv = end != null ? Math.min(maxEv, end) : maxEv;
				resp.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(endEv - startEv));
				resp.addHeader(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d",
					startEv,
					endEv,
					so.getResourceLength()));
			}

			try (OutputStream out = resp.getOutputStream(); InputStream in = _store.getResourceContent(transaction, path);) {
				if(start != null) {
					in.skip(start);
				}

				int read = -1;
				byte[] copyBuffer = new byte[BUF_SIZE];

				long pos = start != null ? start : 0;
				while ((read = in.read(copyBuffer, 0, copyBuffer.length)) != -1) {
					int toWrite = read;
					if(end != null && (pos + read) > end) {
						toWrite = (int) (end - pos);
					}
					pos += toWrite;
					if(toWrite == 0) {
						break;
					}
					out.write(copyBuffer, 0, toWrite);
				}
			}
		} catch (Exception e) {
			LOG.error(e.toString(), e);
		}
	}

	@Override
	protected void folderBody(ITransaction transaction, String path, HttpServletResponse resp, HttpServletRequest req)
			throws IOException {

		StoredObject so = _store.getStoredObject(transaction, path);
		if (so == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, req.getRequestURI());
		} else {

			if (so.isNullResource()) {
				String methodsAllowed = DeterminableMethod.determineMethodsAllowed(so);
				resp.addHeader(HttpHeaders.ALLOW, methodsAllowed);
				resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				return;
			}

			if (so.isFolder()) {
				// TODO some folder response (for browsers, DAV tools
				// use propfind) in html?
				// Locale locale = req.getLocale();

				DateFormat shortDF = getDateTimeFormat(req.getLocale());
				resp.setContentType("text/html");
				resp.setCharacterEncoding("UTF8");
				String[] children = _store.getChildrenNames(transaction, path);
				// Make sure it's not null
				children = (children == null ? new String[] {} : children);
				// Sort by name
				Arrays.sort(children);

				String css = getCSS();

				String href = URLUtil.getCleanPath(req.getContextPath(),req.getServletPath());

                                OutputStream out = resp.getOutputStream();

                                StringBuilder sbFolderBody = new StringBuilder();
                                sbFolderBody.append("<html><head><title>Content of folder ");
                                sbFolderBody.append(URLUtil.getCleanPath(href,path));
                                sbFolderBody.append("</title><style type=\"text/css\">");
                                sbFolderBody.append(css);
                                sbFolderBody.append("</style></head>");
                                sbFolderBody.append("<body>");
                                sbFolderBody.append(getHeader(transaction, URLUtil.getCleanPath(href,path), resp, req));
                                sbFolderBody.append("<table>");
                                sbFolderBody.append("<tr><th>Name</th><th>Size</th><th>Created</th><th>Modified</th></tr>");
                                sbFolderBody.append("<tr>");
                                if(!path.equals(CharsetUtil.FORWARD_SLASH)) {
                                        sbFolderBody.append("<td colspan=\"4\"><a href=\"../\">Parent</a></td></tr>");
                                }
                                boolean isEven = false;
                                for (String child : children) {
                                        isEven = !isEven;
                                        StoredObject obj = _store.getStoredObject(transaction, URLUtil.getCleanPath(path,child));
                                        appendTableRow(transaction,sbFolderBody,URLUtil.getCleanPath(href,path),child,obj,isEven,shortDF);
                                }
                                sbFolderBody.append("</table>");
                                sbFolderBody.append(getFooter(transaction, path, resp, req));
                                sbFolderBody.append("</body></html>");
                                out.write(sbFolderBody.toString().getBytes("UTF-8"));
			}
		}
	}

	/**
	 * Return the header to be displayed in front of the folder content
	 * 
	 * @param transaction
	 * @param path
	 * @param resp
	 * @param req
	 * @return
	 */
	private void appendTableRow(ITransaction transaction, StringBuilder sb, String resourcePath, String resourceName, StoredObject obj, boolean isEven, DateFormat df) {
		sb.append("<tr class=\"");
		sb.append(isEven ? "even" : "odd");
		sb.append("\">");
		sb.append("<td>");
		sb.append("<a href=\"");
		sb.append(URLUtil.getCleanPath(resourcePath,resourceName));
		if (obj == null) {
			LOG.error("Should not return null for " + URLUtil.getCleanPath(resourcePath,resourceName));
		}
		if (obj != null && obj.isFolder()) {
			sb.append("/");
		}
		sb.append("\">");
		sb.append(resourceName);
		sb.append("</a></td>");
		if (obj != null && obj.isFolder()) {
			sb.append("<td>Folder</td>");
		} else {
			sb.append("<td>");
			if (obj != null) {
				sb.append(obj.getResourceLength());
			} else {
				sb.append("Unknown");
			}
			sb.append(" Bytes</td>");
		}
		if (obj != null && obj.getCreationDate() != null) {
			sb.append("<td>");
			sb.append(df.format(obj.getCreationDate()));
			sb.append("</td>");
		} else {
			sb.append("<td></td>");
		}
		if (obj != null && obj.getLastModified() != null) {
			sb.append("<td>");
			sb.append(df.format(obj.getLastModified()));
			sb.append("</td>");
		} else {
			sb.append("<td></td>");
		}
		sb.append("</tr>");
	}

	/**
	 * Return the CSS styles used to display the HTML representation of the webdav
	 * content.
	 * 
	 * @return
	 */
	private String getCSS() {
		// The default styles to use
		String retVal = "";
		try {
			// Try loading one via class loader and use that one instead
			ClassLoader cl = getClass().getClassLoader();
			InputStream iStream = cl.getResourceAsStream("webdav.css");
			if (iStream != null) {
				// Found css via class loader, use that one
				StringBuilder out = new StringBuilder();
				byte[] b = new byte[4096];
				for (int n; (n = iStream.read(b)) != -1;) {
					out.append(new String(b, 0, n));
				}
				retVal = out.toString();
			}
		} catch (IOException | RuntimeException ex) {
			LOG.error("Error in reading webdav.css", ex);
		}

		return retVal;
	}

	/**
	 * Return the header to be displayed in front of the folder content
	 * 
	 * @param transaction
	 * @param path
	 * @param resp
	 * @param req
	 * @return
	 */
	private String getHeader(ITransaction transaction, String path, HttpServletResponse resp, HttpServletRequest req) {
		return "<h1>Content of folder " + path + "</h1>";
	}

	/**
	 * Return the footer to be displayed after the folder content
	 * 
	 * @param transaction
	 * @param path
	 * @param resp
	 * @param req
	 * @return
	 */
	private String getFooter(ITransaction transaction, String path, HttpServletResponse resp, HttpServletRequest req) {
		return "";
	}
	
	/**
	 * Return this as the Date/Time format for displaying Creation + Modification
	 * dates
	 *
	 * @param browserLocale
	 * @return DateFormat used to display creation and modification dates
	 */
	private DateFormat getDateTimeFormat(Locale browserLocale) {
		return SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.MEDIUM, browserLocale);
	}
}
