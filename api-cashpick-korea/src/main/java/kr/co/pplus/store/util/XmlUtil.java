package kr.co.pplus.store.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtil {
	static Logger logger = LoggerFactory.getLogger(XmlUtil.class);
	static XPathFactory factory = XPathFactory.newInstance();

	public static Document parseXml(InputSource src)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = null;
		DocumentBuilder builder = factory.newDocumentBuilder();

		doc = builder.parse(src);
		return doc;
	}

	public static String getSafeNodeValue(Node node) {
		return node != null ? node.getNodeValue() : null;
	}

	public static String getSafeNodeTextContent(Node node, String chNodeName) {
		if (node == null)
			return null;
		return getSafeNodeTextContent(selectOne(node, chNodeName));

	}

	public static String getSafeNodeTextContent(Node node) {
		return node != null ? node.getTextContent() : null;
	}

	public static Document parseXml(Reader r)
			throws ParserConfigurationException, SAXException, IOException {

		InputSource in = new InputSource(r);
		return parseXml(in);
	}

	public static Document parseXml(InputStream is)
			throws ParserConfigurationException, SAXException, IOException {

		InputSource in = new InputSource(is);
		return parseXml(in);
	}

	public static Document parseXml(String str)
			throws ParserConfigurationException, SAXException, IOException {

		InputSource in = new InputSource(new StringReader(str));
		return parseXml(in);
	}

	public static Document getXmlFromUrl(String urlString,
			int connecTimeoutSec, int readTimeoutSec) {
		URL url = null;
		try {
			logger.debug("url:" + urlString);
			url = new URL(urlString);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(connecTimeoutSec * 1000);
			con.setReadTimeout(readTimeoutSec * 1000);
			con.connect();
			InputStream is = con.getInputStream();
			Document doc = XmlUtil.parseXml(is);
			return doc;
		} catch (Exception e) {
			logger.error("getxmlFromUrl fail", e);

		}
		return null;
	}

	public static Document getXmlFromUrlForLocal(String pathString) {
		try {
			logger.debug("url:" + pathString);
			File f = new File(pathString);
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = null;
			StringBuffer buf = new StringBuffer();
			while ((line = br.readLine()) != null) {
				if (line.length() > 0)
					buf.append(line);
			}
			Document doc = XmlUtil.parseXml(buf.toString());
			try {
				br.close();
			} catch (Exception ex) {

			}
			return doc;
		} catch (Exception e) {
			logger.error("getxmlFromUrl fail", e);

		}
		return null;
	}

	public static Document parseXml(File f)
			throws ParserConfigurationException, SAXException, IOException {
		if (f == null || !f.exists()) {
			throw new IllegalArgumentException(
					"File is null or File not exists.");
		}
		InputSource in = new InputSource(f.toURI().toASCIIString());
		return parseXml(in);
	}

	/**
	 * 
	 * <pre>
	 * xpath로 NodeList를 가져온다.
	 * </pre>
	 * 
	 * @param node
	 * @param expression
	 * @return NodeList
	 */
	public static NodeList select(Node node, String expression) {
		NodeList result = null;
		try {

			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(expression);
			result = (NodeList) expr.evaluate(node, XPathConstants.NODESET);
			// result = XPathAPI.selectNodeList(node, expression);
		} catch (XPathExpressionException e) {

			logger.error("select fail", e);
		}
		return result;
	}

	/**
	 * 
	 * <pre>
	 * xpath로 Node를 가져온다 (결과가 여러개일 경우 첫번째 노드만을 가져온다.)
	 * </pre>
	 * 
	 * @param node
	 * @param expression
	 * @return Node
	 */
	public static Node selectOne(Node node, String expression) {
		Node result = null;
		try {
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(expression);
			result = (Node) expr.evaluate(node, XPathConstants.NODE);
			// NodeList resultList = XPathAPI.selectNodeList(node, expression);
			// if (resultList != null) {
			// result = resultList.item(0);
			// }
		} catch (XPathExpressionException e) {
			logger.error("selectOne Fail", e);

		}
		return result;
	}

	/**
	 * 
	 * <pre>
	 * 주어진 노드의 속성을 Node형태로 가져온다.
	 * </pre>
	 * 
	 * @param node
	 * @param attrName
	 * @return Node
	 */
	public static Node getAttributeNode(Node node, String attrName) {
		Attr attr = ((Element) node).getAttributeNode(attrName);
		return attr;
	}
}
