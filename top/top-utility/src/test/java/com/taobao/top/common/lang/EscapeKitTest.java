package com.taobao.top.common.lang;

import static org.junit.Assert.*;

import org.junit.Test;


public class EscapeKitTest {

	@Test
	public void testEscapeAfterTrimJson() {

		assertEquals("NoNeedToEscape", EscapeKit.escapeAfterTrimJson("NoNeedToEscape"));
		//System.out.println(export.escapeAfterTrimJson(" Escape \\\""));
		assertEquals("Escape \\\\\\\"", EscapeKit.escapeAfterTrimJson(" Escape \\\""));
	}
	
//	@Test
//	public void testEscapeAfterTrimXml() {
//		assertEquals("NoNeedToEscape", EscapeKit.escapeAfterTrimXml("NoNeedToEscape"));
//		
//		assertEquals("Escape &lt;&gt;&amp;&quot;&apos;", EscapeKit.escapeAfterTrimXml(" Escape <>&\"'"));
//	}

	@Test
	public void testEscapeHtml() {
		assertEquals("NoNeedToEscape", EscapeKit.escapeHtml("NoNeedToEscape"));
	
		assertEquals("Escape &lt;&gt;&amp;&quot;&apos;", EscapeKit.escapeHtml(" Escape <>&\"'"));
	}

}
