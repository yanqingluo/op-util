package com.taobao.top.core.mock;

import java.util.Hashtable;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class MockedNamedMap implements NamedNodeMap {

	Map<String, Node> map = new Hashtable<String, Node>();

	public int getLength() {
		// TODO Auto-generated method stub
		return map.size();
	}

	public Node getNamedItem(String name) {
		// TODO Auto-generated method stub
		return (Node) map.get(name);
	}

	public Node getNamedItemNS(String namespaceURI, String localName)
			throws DOMException {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node item(int index) {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node removeNamedItem(String name) throws DOMException {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node removeNamedItemNS(String namespaceURI, String localName)
			throws DOMException {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node setNamedItem(Node arg) throws DOMException {
		throw new RuntimeException("Not Implemented method.");
	}

	public Node setNamedItemNS(Node arg) throws DOMException {
		throw new RuntimeException("Not Implemented method.");
	}

	public Map<String, Node> getMap() {
		return map;
	}

	public void setMap(Map<String, Node> map) {
		this.map = map;
	}

}
