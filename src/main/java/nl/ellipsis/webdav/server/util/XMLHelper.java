/*
 * Copyright 2018 Ellipsis BV, The Netherlands
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
package nl.ellipsis.webdav.server.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import nl.ellipsis.webdav.server.LocalFileSystemStore;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLHelper {
	private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalFileSystemStore.class);

	private static DocumentBuilder documentBuilder;

	/**
	 * Return JAXP document builder instance.
	 * @throws ParserConfigurationException 
	 */
	public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		if(documentBuilder == null) {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);

			Collection<String> XML_FEATURES_TO_DISABLE = Collections.unmodifiableList(Arrays.asList(
				// Features from https://xerces.apache.org/xerces-j/features.html
				"http://xml.org/sax/features/external-general-entities",
				"http://xml.org/sax/features/external-parameter-entities",
				"http://apache.org/xml/features/validation/schema",
				"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
				"http://apache.org/xml/features/nonvalidating/load-external-dtd",

				// Features from https://xerces.apache.org/xerces2-j/features.html
				"http://apache.org/xml/features/xinclude/fixup-base-uris"
			));

			documentBuilderFactory.setExpandEntityReferences(false);

			// Set the validating off because it can be mis-used to pull a validation document
			// that is malicious or from the local machine
			documentBuilderFactory.setValidating(false);

			documentBuilderFactory.setXIncludeAware(false);

			documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			// https://owasp.trendmicro.com/main#!/codeBlocks/disableXmlExternalEntities has these
			// though set to false, but I think they're only possible to set if we have some additional
			// xerces and jaxp-api stuff on the classpath for them to be available/relevant
			// As it is, they all throw `ParserConfigurationException: Feature 'X' is not recognized.`
			//
			// https://stackoverflow.com/a/58522022/797 is the closest I found to discussion of it.
			//
			// documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			// documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			// documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

			for (String feature : XML_FEATURES_TO_DISABLE) {
				documentBuilderFactory.setFeature(feature, false);
			}

			DocumentBuilder result = documentBuilderFactory.newDocumentBuilder();

			result.setEntityResolver(new NoOpEntityResolver());

			documentBuilder = result;
		}
		return documentBuilder;
	}

	private static class NoOpEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) {
			return new InputSource(new ByteArrayInputStream(new byte[]{}));
		}
	}

	public static Node findSubElement(Node parent, String localName) {
		if (parent == null) {
			return null;
		}
		Node child = parent.getFirstChild();
		while (child != null) {
			if ((child.getNodeType() == Node.ELEMENT_NODE) && (child.getLocalName().equals(localName))) {
				return child;
			}
			child = child.getNextSibling();
		}
		return null;
	}


	public static String format(String xml) {
		String retval = xml;

		Document document = null;
		try (StringReader sr = new StringReader(xml)) {
			InputSource inputSource = new InputSource(sr);
			document = getDocumentBuilder().parse(inputSource);
			retval = format(document);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			LOG.warn("Failed to format XML", e);
		}
		return retval;
	}


	public static String format(Document document) {
		String retval = null;
		if (document != null) {
			TransformerFactory transfac = TransformerFactory.newInstance();

			try(StringWriter sw = new StringWriter()) {
				Transformer transformer = transfac.newTransformer();

				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");

				//create string from xml tree
				StreamResult result = new StreamResult(sw);

				DOMSource source = new DOMSource(document);

				transformer.transform(source, result);

				retval = sw.toString();
			} catch (IOException | TransformerException e) {
				LOG.warn("Failed to format XML", e);
			}
		}
		return retval;
	}


	public static List<String> getPropertiesFromXML(Node propNode) {
		List<String> properties = new ArrayList<>();
		NodeList childList = propNode.getChildNodes();
	
		for (int i = 0; i < childList.getLength(); i++) {
			Node currentNode = childList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = currentNode.getLocalName();
				String namespace = currentNode.getNamespaceURI();
				String propertyName;
				if (nodeName.indexOf(':') != -1) {
					propertyName = nodeName.substring(nodeName.indexOf(':') + 1);
				} else {
					propertyName = nodeName;
				}
				// href is a live property which is handled differently
				properties.add(propertyName);
			}
		}
		return properties;
	}


}
