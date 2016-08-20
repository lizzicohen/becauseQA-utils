package com.github.becauseQA.cucumber.selenium;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.github.becauseQA.apache.commons.FileUtils;
import com.github.becauseQA.apache.commons.StringUtils;
import com.github.becauseQA.json.JSONUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PageRecorder {

	private static final Logger log = Logger.getLogger(PageRecorder.class);

	private static String javaScript_GetCommand = "return document.selenium_data === undefined ? '' : document.selenium_data;";

	private static String lastCommandid = null;
	private static String elementName;

	public static void InjectAlertifyFile() {
		String alertifyJS = PageRecorder.class.getResource("/js/toastr.min.js").getFile();
		try {
			String alertifyJSContent = FileUtils.readFileToString(new File(alertifyJS));
			// String alertifyFound = "return typeof $.notify=='function'; ";
			String alertifyFound = "return typeof toastr=='object'; ";
			boolean alertSupport = (boolean) BaseSteps.runJS(alertifyFound);
			if (!alertSupport) {
				BaseSteps.runJS(alertifyJSContent);
			} else {
				log.info("Current browser had support toastr object,it's modern browser");
			}

			String toastrCSS = PageRecorder.class.getResource("/js/toastr.min.css").getFile();
			String toastrJSContent = FileUtils.readFileToString(new File(toastrCSS), "UTF-8");
			BaseSteps.addCssStyle(toastrJSContent);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void InjectJSON2ObjectForWierdBrowsers() {
		String jsonFilePath = PageRecorder.class.getResource("/js/json2.js").getFile();
		try {
			String jsonContent = FileUtils.readFileToString(new File(jsonFilePath));
			String IsJson2ObjectExists = "return typeof JSON === 'object';";
			boolean jsonSupport = (boolean) BaseSteps.runJS(IsJson2ObjectExists);
			if (!jsonSupport) {
				BaseSteps.runJS(jsonContent);
			} else {
				log.info("Current browser had support JSON object,it's modern browser");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void InjectJQuery() {
		String jqueryFilePath = PageRecorder.class.getResource("/js/jquery-3.1.0.min.js").getFile();
		try {
			String jqueryContent = FileUtils.readFileToString(new File(jqueryFilePath));
			String IsJqueryObjectExists = "return typeof jQuery  === 'undefined';";
			boolean jqueryNotSupport = (boolean) BaseSteps.runJS(IsJqueryObjectExists);
			if (jqueryNotSupport) {
				BaseSteps.runJS(jqueryContent);
			} else {
				log.info("Current browser had support JQuery object,it's modern browser");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean IsVisualSearchScriptInjected() {
		String jsCheckScript = "return document.PageRecorder === undefined ? false : true;";
		boolean IsVisualSearchScriptInjected = (boolean) BaseSteps.runJS(jsCheckScript);
		return IsVisualSearchScriptInjected;
	}

	public static void InjectPageRecorder() {

		InjectJQuery();
		InjectJSON2ObjectForWierdBrowsers();
		InjectAlertifyFile();
		String jsonFilePath = PageRecorder.class.getResource("/js/PageRecorder.js").getFile();
		try {
			boolean isVisualSearchScriptInjected = IsVisualSearchScriptInjected();
			if (!isVisualSearchScriptInjected) {
				String jsonContent = FileUtils.readFileToString(new File(jsonFilePath));
				BaseSteps.runJS(jsonContent);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void destoryPageRecorder() {
		String[] jsObjectArray = { "document.PageRecorder", "document.pr_prevActiveElement", "document.selenium_data" };
		StringBuilder deathBuilder = new StringBuilder();

		for (String sentencedToDeath : jsObjectArray) {

			String formatStr = MessageFormat.format(
					" try {{ delete {0}; }} catch (e) {{ if (console) {{ console.log('ERROR: |{0}| --> ' + e.message)}} }} ",
					sentencedToDeath);
			deathBuilder.append(formatStr);
		}

		if (IsVisualSearchScriptInjected()) {
			log.info("DestroyVisualSearch: Scripts have been injected previously. Kill'em all!");
			BaseSteps.runJS(deathBuilder.toString());
		}

	}

	public static JsonObject getSeleniumCommand() {
		String seleniumDataStr = "";
		JsonObject seleniumData = null;
		try {
			seleniumDataStr = (String) BaseSteps.runJS(javaScript_GetCommand);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (StringUtils.isNotEmpty(seleniumDataStr)) {
			JsonElement jsonElement = JSONUtils.toJsonElement(seleniumDataStr);
			String commandId = jsonElement.getAsJsonObject().get("CommandId").getAsString();
			String commandName = jsonElement.getAsJsonObject().get("Command").getAsString();
			if (lastCommandid == commandId)
				return null;
			if (commandName.equals("rightClickHandler")) {
				seleniumData = jsonElement.getAsJsonObject();
			} else if (commandName.equals("AddElement")) {
				seleniumData = jsonElement.getAsJsonObject();
			}
			lastCommandid = commandId;

		}
		return seleniumData;
	}

	public static void startRecord(WebDriver driver) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					PageRecorder.InjectPageRecorder();
					JsonObject seleniumCommand = PageRecorder.getSeleniumCommand();
					if (seleniumCommand != null) {
						String commandName = seleniumCommand.get("Command").getAsString();
						if (commandName.equals("rightClickHandler")) {
							// when you right click the element
						} else if (commandName.equals("AddElement")) {
							// add the command to any place you want
							if (!elementName.equals(seleniumCommand.get("ElementName").getAsString())) {
								String elementId = seleniumCommand.get("ElementId").getAsString();
								String cssPath = seleniumCommand.get("CSS").getAsString();
								String XPath = seleniumCommand.get("XPath").getAsString();
								log.info("Add Element: " + elementName + "," + elementId + "," + cssPath + "," + XPath);
								elementName = seleniumCommand.get("ElementName").getAsString();
							}
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					log.error(e);
					cancel();
				}

			}
		}, 0, 100 * 1000);
	}

	/**
	 * Show a non-blocking messagebox to indicate the step execution
	 * 
	 * @param message
	 *            the message need to show in the page
	 * @param type
	 *            type should be success,info,warning,error
	 */
	public static void showMessage(String message, String type,String title) {
		String msgType = "info"; //
		if (StringUtils.isNotEmpty(type)) {
			msgType = type;
		}
		String notifyJS = "toastr.options.closeButton= true;toastr.options.debug= false;"
				+ "toastr.options.newestOnTop=true;toastr.options.progressBar=false;"
				+ "toastr.options.positionClass='toast-top-left';toastr.options.preventDuplicates= false;"
				+ "toastr.options.showDuration=300;toastr.options.hideDuration=3000;"
				+ "toastr.options.timeOut=8000;toastr.options.extendedTimeOut=1000;"
				+ "toastr.options.showEasing='swing';toastr.options.hideEasing='linear';"
				+ "toastr.options.showMethod='fadeIn';toastr.options.hideMethod='slideUp';" + "toastr['" + msgType
				+ "'](\"" + message + "\", '"+title+"').css('width','350px');";
		try

		{
			BaseSteps.runJS(notifyJS);
		} catch (WebDriverException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	

	public static void main(String[] args) {
		showMessage("test", null,"");
	}

}