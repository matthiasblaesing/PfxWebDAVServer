/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.ellipsis.webdav.server.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

/**
 * XMLWriter helper class.
 *
 * @author <a href="mailto:remm@apache.org">Remy Maucherat</a>
 */
public class XMLWriter {

	private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(XMLWriter.class);

	// -------------------------------------------------------------- Constants

	/**
	 * Opening tag.
	 */
	public static final int OPENING = 0;

	/**
	 * Closing tag.
	 */
	public static final int CLOSING = 1;

	/**
	 * Element with no content.
	 */
	public static final int NO_CONTENT = 2;

	// ----------------------------------------------------- Instance Variables

	/**
	 * Buffer.
	 */
	protected StringBuilder buffer = new StringBuilder();

	/**
	 * Writer.
	 */
	protected Writer writer = null;

	// ----------------------------------------------------------- Constructors

	/**
	 * Constructor.
	 */
	public XMLWriter() {
	}

	/**
	 * Constructor.
	 */
	public XMLWriter(Writer writer) {
		this.writer = writer;
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Retrieve generated XML.
	 *
	 * @return String containing the generated XML
	 */
	@Override
	public String toString() {
		return buffer.toString();
	}

	/**
	 * Write property to the XML.
	 *
	 * @param namespace
	 *           Namespace
	 * @param namespaceInfo
	 *           Namespace info
	 * @param name
	 *           Property name
	 * @param value
	 *           Property value
	 */
	public void writeProperty(String namespace, String namespaceInfo, String name, String value) {
		writeElement(namespace, namespaceInfo, name, OPENING);
		buffer.append(value);
		writeElement(namespace, namespaceInfo, name, CLOSING);
	}

	/**
	 * Write property to the XML.
	 *
	 * @param namespace
	 *           Namespace
	 * @param name
	 *           Property name
	 * @param value
	 *           Property value
	 */
	public void writeProperty(String namespace, String name, String value) {
		writeElement(namespace, name, OPENING);
		buffer.append(value);
		writeElement(namespace, name, CLOSING);
	}

	/**
	 * Write property to the XML.
	 *
	 * @param namespace
	 *           Namespace
	 * @param name
	 *           Property name
	 */
	public void writeProperty(String namespace, String name) {
		writeElement(namespace, name, NO_CONTENT);
	}

	/**
	 * Write an element.
	 *
	 * @param name
	 *           Element name
	 * @param namespace
	 *           Namespace abbreviation
	 * @param type
	 *           Element type
	 */
	public void writeElement(String namespace, String name, int type) {
		writeElement(namespace, null, name, type);
	}

	/**
	 * Write an element.
	 *
	 * @param namespace Namespace abbreviation
	 * @param namespaceInfo Namespace info
	 * @param name Element name
	 * @param type Element type
	 */
	public void writeElement(String namespace, String namespaceInfo, String name, int type) {
		if (!StringUtils.isEmpty(namespace)) {
			switch (type) {
			case OPENING:
				if (namespaceInfo != null) {
					buffer.append("<")
						.append(namespace)
						.append(CharsetUtil.COLON)
						.append(name).append(" xmlns:")
						.append(namespace)
						.append("=\"")
						.append(namespaceInfo).append("\">");
				} else {
					buffer.append("<")
						.append(namespace)
						.append(CharsetUtil.COLON)
						.append(name)
						.append(">");
				}
				break;
			case CLOSING:
				buffer.append("</")
					.append(namespace)
					.append(CharsetUtil.COLON)
					.append(name)
					.append(">\n");
				break;
			case NO_CONTENT:
			default:
				if (!StringUtils.isEmpty(namespaceInfo)) {
					buffer.append("<")
						.append(namespace)
						.append(CharsetUtil.COLON)
						.append(name).append(" xmlns:")
						.append(namespace).append("=\"")
						.append(namespaceInfo)
						.append("\"/>");
				} else {
					buffer.append("<")
						.append(namespace)
						.append(CharsetUtil.COLON)
						.append(name).append("/>");
				}
				break;
			}
		} else {
			switch (type) {
			case OPENING:
				buffer.append("<").append(name).append(">");
				break;
			case CLOSING:
				buffer.append("</").append(name).append(">\n");
				break;
			case NO_CONTENT:
			default:
				buffer.append("<").append(name).append("/>");
				break;
			}
		}
	}

	/**
	 * Write an element.
	 *
	 * @param namespace
	 *           Namespace abbreviation
	 * @param namespaceInfo
	 *           Namespace info
	 * @param name
	 *           Element name
	 * @param type
	 *           Element type
	 */
	public void writeNSElement(String namespace, Map<String, String> namespacePrefixMap, String name, int type) {
		if (!StringUtils.isEmpty(namespace)) {
			switch (type) {
			case OPENING:
				if (namespacePrefixMap != null && !namespacePrefixMap.isEmpty()) {
					buffer.append("<")
						.append(namespace)
						.append(":")
						.append(name);
					for (Entry<String, String> e : namespacePrefixMap.entrySet()) {
						buffer.append(" xmlns:")
							.append(e.getKey())
							.append("=\"")
							.append(e.getValue())
							.append("\"");
					}
					buffer.append(">");
				} else {
					buffer.append("<")
						.append(namespace)
						.append(CharsetUtil.COLON)
						.append(name)
						.append(">");
				}
				break;
			case CLOSING:
				buffer.append("</")
					.append(namespace)
					.append(CharsetUtil.COLON)
					.append(name)
					.append(">\n");
				break;
			case NO_CONTENT:
			default:
				if (namespacePrefixMap != null && !namespacePrefixMap.isEmpty()) {
					buffer.append("<")
						.append(namespace)
						.append(CharsetUtil.COLON)
						.append(name);
					for (Entry<String, String> e : namespacePrefixMap.entrySet()) {
						buffer.append(" xmlns:")
							.append(e.getKey())
							.append("=\"")
							.append(e.getValue())
							.append("\"");
					}
					buffer.append(">");
				} else {
					buffer.append("<")
						.append(namespace)
						.append(CharsetUtil.COLON)
						.append(name)
						.append(">");
				}
				break;
			}
		} else {
			switch (type) {
			case OPENING:
				buffer.append("<").append(name).append(">");
				break;
			case CLOSING:
				buffer.append("</").append(name).append(">\n");
				break;
			case NO_CONTENT:
			default:
				buffer.append("<").append(name).append("/>");
				break;
			}
		}
	}

	/**
	 * Write text.
	 *
	 * @param text
	 *           Text to append
	 */
	public void writeText(String text) {
		buffer.append(text);
	}

	/**
	 * Write data.
	 *
	 * @param data
	 *           Data to append
	 */
	public void writeData(String data) {
		buffer.append("<![CDATA[").append(data).append("]]>");
	}

	/**
	 * Write XML Header.
	 */
	public void writeXMLHeader() {
		buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
	}

	/**
	 * Send data and reinitializes buffer.
	 * @param logInfo 
	 */
	public void sendData(String logInfo) throws IOException {
		if (writer != null) {
			String content = buffer.toString();
			if(LOG.isDebugEnabled()) {
				LOG.debug((!StringUtils.isEmpty(logInfo) ? logInfo : "")+XMLHelper.format(content));
			}
			writer.write(content);
			buffer = new StringBuilder();
		}
	}

}
