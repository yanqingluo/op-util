package com.taobao.top.core.mock;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
public class MockedNode implements Node {

	private String value;

	public MockedNode(String value) {
		super();
		this.value = value;
	}

	public Node appendChild(Node newChild) throws DOMException {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node cloneNode(boolean deep) {
		throw new RuntimeException("Not Implemented method.");
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		throw new RuntimeException("Not Implemented method.");
	}

	public NamedNodeMap getAttributes() {
		throw new RuntimeException("Not Implemented method.");
	}

	public String getBaseURI() {
		throw new RuntimeException("Not Implemented method.");
	}

	public NodeList getChildNodes() {
		throw new RuntimeException("Not Implemented method.");
	}

	public Object getFeature(String feature, String version) {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node getFirstChild() {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node getLastChild() {
		throw new RuntimeException("Not Implemented method.");
	}

	public String getLocalName() {
		throw new RuntimeException("Not Implemented method.");
	}

	public String getNamespaceURI() {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node getNextSibling() {
		throw new RuntimeException("Not Implemented method.");
	}

	public String getNodeName() {
		throw new RuntimeException("Not Implemented method.");
	}

	public short getNodeType() {
		throw new RuntimeException("Not Implemented method.");
	}

	public String getNodeValue() throws DOMException {
		return value;
	}

	public Document getOwnerDocument() {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node getParentNode() {
		throw new RuntimeException("Not Implemented method.");
	}

	public String getPrefix() {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node getPreviousSibling() {
		throw new RuntimeException("Not Implemented method.");
	}

	public String getTextContent() throws DOMException {
		throw new RuntimeException("Not Implemented method.");
	}

	public Object getUserData(String key) {
		throw new RuntimeException("Not Implemented method.");
	}

	public boolean hasAttributes() {
		throw new RuntimeException("Not Implemented method.");
	}

	public boolean hasChildNodes() {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		throw new RuntimeException("Not Implemented method.");
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		throw new RuntimeException("Not Implemented method.");
	}

	public boolean isEqualNode(Node arg) {
		throw new RuntimeException("Not Implemented method.");
	}

	public boolean isSameNode(Node other) {
		throw new RuntimeException("Not Implemented method.");
	}

	public boolean isSupported(String feature, String version) {
		throw new RuntimeException("Not Implemented method.");
	}

	public String lookupNamespaceURI(String prefix) {
		throw new RuntimeException("Not Implemented method.");
	}

	public String lookupPrefix(String namespaceURI) {
		throw new RuntimeException("Not Implemented method.");
	}

	public void normalize() {
		throw new RuntimeException("Not Implemented method.");
		
	}

	public Node removeChild(Node oldChild) throws DOMException {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		throw new RuntimeException("Not Implemented method.");
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		this.value = nodeValue;
		
	}

	public void setPrefix(String prefix) throws DOMException {
		throw new RuntimeException("Not Implemented method.");
		
	}

	public void setTextContent(String textContent) throws DOMException {
		throw new RuntimeException("Not Implemented method.");
		
	}

	public Object setUserData(String key, Object data, UserDataHandler handler) {
		throw new RuntimeException("Not Implemented method.");
	}

}
