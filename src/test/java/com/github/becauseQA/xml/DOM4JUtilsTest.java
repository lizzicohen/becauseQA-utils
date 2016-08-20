/**
 * Project Name:commons
 * File Name:DOM4JUtilsTest.java
 * Package Name:com.github.becauseQA.xml
 * Date:Apr 16, 20169:57:06 PM
 * Copyright (c) 2016, alterhu2020@gmail.com All Rights Reserved.
 *
*/

package com.github.becauseQA.xml;

import java.util.List;

import org.dom4j.Node;
import org.junit.Before;
import org.junit.Test;

/**
 * ClassName:DOM4JUtilsTest  
 * Function: TODO ADD FUNCTION.  
 * Reason:	 TODO ADD REASON.  
 * Date:     Apr 16, 2016 9:57:06 PM 
 * @author   Administrator
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
public class DOM4JUtilsTest {

	

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testWriteSampleXml() {
		
		DOM4JUtils dom4jUtils=new DOM4JUtils("sample.xml");
		List<Node> nodes = dom4jUtils.getNodes("//sample/hello");
		for(Node node: nodes){
			String name = node.getName();
			System.out.println(name);
		}
	}

}
