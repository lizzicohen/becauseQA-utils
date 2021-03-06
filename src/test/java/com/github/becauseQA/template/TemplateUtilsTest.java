package com.github.becauseQA.template;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.TemplateException;

public class TemplateUtilsTest {

	private FileTemplateLoader fileTemplateLoader;
	private File location;
	private StringTemplateLoader stringTemplateLoader;
	@Before
	public void setup() throws IOException {
		// Class<? extends Class> protectionDomain =
		// TemplateUtilsTest.class.getClass().g
		String location2 = this.getClass().getResource("").getPath();
		// this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
		location = new File(location2);
		fileTemplateLoader = new FileTemplateLoader(location);

		stringTemplateLoader = new StringTemplateLoader();
		new TemplateUtils();
	}

	@Test
	public void testStringTemplate(){
		String templateName="dataTemplate";
		String parseContent="this is a template content ${sex} used to get it ${name}";
		stringTemplateLoader.putTemplate(templateName, parseContent);
		Map<String,Object> dataModel=new HashMap<>();
		dataModel.put("name", "alter .hu");
		dataModel.put("sex", true);
		String renderContent = TemplateUtils.renderContent(stringTemplateLoader, templateName, null, dataModel);
		System.out.println(renderContent);
		
	}
	
	@Ignore
	@Test
	public void testRenderResourceContent() {
		// fail("Not yet implemented");
		String locationPath = location.getPath() + File.separator + "email.html";

		DataModel dataModel = new DataModel();

		// dataModel.setAge(35);
		dataModel.setfemale(true);
		// dataModel.setPassword("testingpasswd");
		dataModel.setSalary(5000);
		dataModel.setUsername("altertesting");
	
		TemplateUtils.renderContent(fileTemplateLoader, "sample", locationPath, dataModel);

	}


	@Test
	public void testRenderList() throws TemplateException, IOException {
		// fail("Not yet implemented");
		String locationPath = location.getPath() + File.separator + "email.html";

		Map<String, Object> mapObject = new HashMap<>();
		
		
		DataModel dataModel = new DataModel();
		// dataModel.setAge(35);
		dataModel.setfemale(true);
		// dataModel.setPassword("testingpasswd");
		dataModel.setSalary(5000);
		dataModel.setUsername("altermapdata1");
		mapObject.put("user", dataModel);
		
		
		DataModel dataModel2 = new DataModel();
		// dataModel.setAge(35);
		dataModel2.setfemale(true);
		// dataModel.setPassword("testingpasswd");
		dataModel2.setSalary(5000);
		//dataModel2.setUsername("altermapdata2");
	
		//list
		List<DataModel> dataModels=new ArrayList<>();
		dataModels.add(dataModel);
		dataModels.add(dataModel2);
		
		mapObject.put("users", dataModels);
		
		// map
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("mytestuserkey", "zipuser");
		hashMap.put("mytestpasswordkey", "testpasword");
		mapObject.put("mapkey", hashMap);
		
		TemplateUtils.rendMap(fileTemplateLoader, "sample2", locationPath, mapObject);

	}

	@Test
	public void testRendMap() {
		// fail("Not yet implemented");
	}

}
