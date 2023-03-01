/**
 *
 */
package com.contiautomotive.architecture.tool.automation;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.contiautomotive.architecture.tool.handlers.CidlCollectorHandler;
import com.contiautomotive.cidl.cidl.Software;
import com.contiautomotive.cidl.cidl.TypeCollection;
import com.contiautomotive.cidl.cidl.impl.ModelImpl;
import com.contiautomotive.read.cidl.data.CollectCidlData;
import com.continental.plm.cli.AbstractToolchainCommand;
import com.continental.plm.flavors.model.IFlavor;
import com.continental.plm.flavors.util.FlavorUtil;

/**
 * @author uidu1787
 *
 */
public class AutomationTool extends AbstractToolchainCommand {
	private static final Logger logger = LogManager.getLogger(AutomationTool.class);
	public AutomationTool(String name, String description) {
		super(name, description);
		
	}
/**
 * Instantiate the cli application
 */
public AutomationTool()
{
	super("AutomationToolCmd","Automation Tool for new model");
	logger.info("Plugin loaded....");
}
/**
 *
 */
@Override
public Integer run(String[] args) {
  CidlCollectorHandler cidlInstance = CidlCollectorHandler.getInstance();
  this.clp.parse(args);
  String filePath = "";
  for(int i =0;i<=args.length-1;i++){
  if(args[i].toString().contains("-c=")){
  logger.info(args[i]);
  filePath =  args[i].replace("-c=", "");
   }
  }
  HashMap<String, String> confiData = readXMLConfig(filePath);
  String projectName = confiData.get("projectName");
  String rhapsodyInstallationPath = confiData.get("rhapsodyInstallationPath");
 logger.info("project name " + projectName);
  IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
  if(project != null) {
   try {
		IFlavor flavor = FlavorUtil.getViewFlavor(project);
		logger.info(" flavor name " + flavor.getName());
	    CollectCidlData pluginXtend = new CollectCidlData(project);
		if (pluginXtend.getResource() != null) {
			cidlInstance.openProject(confiData.get("rhapsodyFilePath"),true,rhapsodyInstallationPath);
			Software sw = null;
			HashMap<String, EList<com.contiautomotive.cidl.cidl.Plugin>> pluginMap = new HashMap<>();
			HashMap<String, EList<TypeCollection>> typeMap = new HashMap<>();
			// search for first software resource
			for (Resource res : pluginXtend.getResource()) {
				EList<EObject> contents = res.getContents();
				for (EObject obj : contents) {
					if (obj instanceof ModelImpl && ((ModelImpl) obj).getSoftware() != null) {
						sw = ((ModelImpl) obj).getSoftware();

					} else if (obj instanceof ModelImpl && ((ModelImpl) obj).getPlugins() != null
							&& !((ModelImpl) obj).getPlugins().isEmpty()) {
						EList<com.contiautomotive.cidl.cidl.Plugin> plugin = ((ModelImpl) obj).getPlugins();
						plugin.forEach(pluginName -> {
							String componentName = pluginName.getComponent().getName();
							pluginMap.put(componentName, plugin);
						});

					} else if (obj instanceof ModelImpl && ((ModelImpl) obj).getTypeCollection() != null
							&& !((ModelImpl) obj).getTypeCollection().isEmpty()) {
						EList<TypeCollection> type = ((ModelImpl) obj).getTypeCollection();
						type.forEach(typeName -> {
							String componentName = typeName.getComponent().getName();
							typeMap.put(componentName, type);
						});
					}
				}
			}
			if (sw != null) {
				cidlInstance.getCIDLDataToRhapsody(sw, pluginMap, typeMap);
				cidlInstance.setTypesandFlows(sw, pluginMap);
				cidlInstance.saveAndClose();
			} else {
				logger.info("No software resource found");
			}
		}
	 } catch (Exception e) {
		logger.info("Exception caught in incremental building of the project ");
		
	}finally {
		cidlInstance.saveAndClose();
	}
   }
   return super.run(args);
  }
/**
 *
 * @param filePath
 * @return
 */
private HashMap<String, String> readXMLConfig(String filePath) {
    HashMap<String, String> configData = new HashMap<>();
    try{
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    Document doc = docBuilder.parse(filePath);
    XPath xPath =  XPathFactory.newInstance().newXPath();
    String expression = "//CATEGORY/argument";
    NodeList childNodes = (NodeList) xPath.evaluate(expression, doc, XPathConstants.NODESET);
   // load the xml file into properties format
   if(childNodes.getLength() >= 0){
    for(int i=0;i<childNodes.getLength();i++)
    {
	  Node c = childNodes.item(i);
	  if (c.getNodeType() == Node.ELEMENT_NODE) {
	  childNodes.item(i).getAttributes();
	  int attlength = c.getAttributes().getLength();
	  for(int j=0;attlength>j;j++)
	   {
		String keyNodeValue = c.getAttributes().item(j).getNodeValue();
		String attValue = c.getTextContent();
		configData.put(keyNodeValue,attValue);
	  }
     }
    }
   }
  } catch (Exception e1) {
	  logger.error("ERROR while reading config xml file...");
}
return configData;

}
}
