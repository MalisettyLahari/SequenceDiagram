package com.contiautomotive.architecture.tool.handlers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.contiautomotive.common.GlobalVariables;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

public class CompareJson {
	private static final Logger logger = LogManager.getLogger(CompareJson.class);
	IRPApplication rpy = null;
	JSONParser parser = new JSONParser();
	HashSet<String> software = new HashSet<String>();
	HashSet<String> partitions = new HashSet<String>();
	HashSet<String> clusters = new HashSet<String>();
	HashSet<String> components = new HashSet<String>();
	HashSet<String> Intfblks = new HashSet<String>();
	HashSet<String> model_partition_elements = new HashSet<>();
	HashSet<String> model_cluster_elements = new HashSet<String>();
	HashSet<String> model_component_elements = new HashSet<String>();
	HashSet<String> model_intf_elements = new HashSet<String>();
	HashSet<String> model_software_elements = new HashSet<String>();
	Map<String, List<String>> commonJsonElements_map = new HashMap<String, List<String>>();
	Map<String, List<String>> modelElements_map = new HashMap<String, List<String>>();
	Map<String, ArrayList<String>> elementMissingInModelJson_Map = new HashMap<String, ArrayList<String>>();
	Map<String, ArrayList<String>> elementMissingInGeneralJson_Map = new HashMap<String, ArrayList<String>>();
	static int i;

	public ArrayList<String> getSoftware(JSONObject path) {
		ArrayList<String> sw =(ArrayList<String>) path.get(GlobalVariables.SOFTWARE_METACLASS);
		return sw;
	}

	public ArrayList<String> getPartitions(JSONObject path) {
		ArrayList<String> partitionList = (ArrayList<String>) path.get(GlobalVariables.PARTITION_METACLASS);
		return partitionList;
	}

	public ArrayList<String> getClusters(JSONObject path) {
		ArrayList<String> clusterList = (ArrayList<String>) path.get(GlobalVariables.CLUSTER_METACLASS);

		return clusterList;
	}

	public ArrayList<String> getComponents(JSONObject path) {
		ArrayList<String> componentList = (ArrayList<String>) path.get(GlobalVariables.COMPONENT_METACLASS);
		return componentList;
	}

	public ArrayList<String> getIntfBlks(JSONObject path) {
		ArrayList<String> intfblks = (ArrayList<String>) path.get(GlobalVariables.INTERFACE_BLOCK_METACLASS);
		return intfblks;
	}

	public void readAllJsonFiles(File[] filesList) {
		try {
			JSONObject flavour = (JSONObject) parser.parse(new FileReader(filesList[i]));
			software.addAll(getSoftware(flavour));
			partitions.addAll(getPartitions(flavour));
			clusters.addAll(getClusters(flavour));
			components.addAll(getComponents(flavour));
			Intfblks.addAll(getIntfBlks(flavour));
		} catch (Exception e) {
		   logger.info("Exception while geting Json file\n" + e.toString());
		}
	}

	@SuppressWarnings("unchecked")
	public void createCommonJson() {
		FileWriter file = null;
		try {
			String path=System.getProperty("user.home")+"\\JsonFileComparison" ;
			File f = new File(path);
			if(!f.exists()) {
			f.mkdirs();
			}
			f = new File(path+"\\General.json");
			file = new FileWriter(f.getAbsoluteFile());
			if(f.exists()){
			JSONArray partitionArray = new JSONArray();
			JSONArray clusterArray = new JSONArray();
			JSONArray SoftwareArray = new JSONArray();
			JSONArray componentArray = new JSONArray();
			JSONArray interfaceBlockArray = new JSONArray();
			partitionArray.addAll(partitions);
			clusterArray.addAll(clusters);
			SoftwareArray.addAll(software);
			componentArray.addAll(components);
			interfaceBlockArray.addAll(Intfblks);
			JSONObject commonJson = new JSONObject();
			commonJson.put(GlobalVariables.SOFTWARE_METACLASS, SoftwareArray);
			commonJson.put(GlobalVariables.PARTITION_METACLASS, partitionArray);
			commonJson.put(GlobalVariables.CLUSTER_METACLASS, clusterArray);
			commonJson.put(GlobalVariables.COMPONENT_METACLASS, componentArray);
			commonJson.put(GlobalVariables.INTERFACE_BLOCK_METACLASS, interfaceBlockArray);
			file.write(commonJson.toJSONString());
			file.close();
			}
		} catch (IOException e) {
			logger.info("Exception while creating Json file\n" + e.toString());
		}
	}

	public void readCommonJsonMap() {
		try {
			String path=System.getProperty("user.home") ;
			Object object = parser.parse(new FileReader(path+"\\JsonFileComparison\\General.json"));
			if(object instanceof org.json.simple.JSONObject){
			    org.json.simple.JSONObject obj = (JSONObject)object;
			commonJsonElements_map.put(GlobalVariables.SOFTWARE_METACLASS,
					(List<String>) obj.get(GlobalVariables.SOFTWARE_METACLASS));
			commonJsonElements_map.put(GlobalVariables.PARTITION_METACLASS,
					(List<String>) obj.get(GlobalVariables.PARTITION_METACLASS));
			commonJsonElements_map.put(GlobalVariables.CLUSTER_METACLASS,
					(List<String>) obj.get(GlobalVariables.CLUSTER_METACLASS));
			commonJsonElements_map.put(GlobalVariables.INTERFACE_BLOCK_METACLASS,
					(List<String>) obj.get(GlobalVariables.INTERFACE_BLOCK_METACLASS));
			commonJsonElements_map.put(GlobalVariables.COMPONENT_METACLASS,
					(List<String>) obj.get(GlobalVariables.COMPONENT_METACLASS));
		}
		} catch (Exception e) {
			logger.info("Exception while reading common Json file\n" + e.toString());
		}
	}

	public void readModelElements() {
		rpy = RhapsodyAppServer.getActiveRhapsodyApplication();
		IRPPackage pack = (IRPPackage) rpy.activeProject().findNestedElementRecursive("Model",
				GlobalVariables.PACKAGE_METACLASS);
		IRPCollection software_list = pack.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 0);
		int softwareCount = software_list.getCount();
		IRPModelElement sotware_elem;
		for (int l = 1; l < softwareCount + 1; l++) {
			sotware_elem = (IRPModelElement) software_list.getItem(l);
			model_software_elements.add(sotware_elem.getName());
		}
		IRPCollection partitionList = pack.getNestedElementsByMetaClass(GlobalVariables.PACKAGE_METACLASS, 0);
		int partitionCount = partitionList.getCount();
		IRPModelElement partition_ele;
		IRPModelElement cluster_ele;
		IRPModelElement component_ele = null;
		IRPModelElement intf_elem;
		IRPCollection clusterList = null;
		IRPCollection componentList;
		IRPCollection intf_list;

		for (int m = 1; m < partitionCount + 1; m++) {
			partition_ele = (IRPModelElement) partitionList.getItem(m);
			model_partition_elements.add(partition_ele.getName());
			clusterList = partition_ele.getNestedElementsByMetaClass(GlobalVariables.PACKAGE_METACLASS, 0);
			int clustersCount = clusterList.getCount();

			for (int j = 1; j < clustersCount + 1; j++) {
				cluster_ele = (IRPModelElement) clusterList.getItem(j);
				if (cluster_ele.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.PACKAGE_METACLASS)) {
					model_cluster_elements.add(cluster_ele.getName());
					componentList = cluster_ele.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
					int componentCount = componentList.getCount();
					for (int k = 1; k < componentCount + 1; k++) {
						component_ele = (IRPModelElement) componentList.getItem(k);
						if (component_ele.getUserDefinedMetaClass()
								.equalsIgnoreCase(GlobalVariables.COMPONENT_METACLASS)) {
							model_component_elements.add(component_ele.getName() + ":" + cluster_ele.getName());
						}
						intf_list = component_ele.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 0);
						int interfaceBlockCount = intf_list.getCount();
						for (int l = 1; l < interfaceBlockCount + 1; l++) {
							intf_elem = (IRPModelElement) intf_list.getItem(l);
							if (intf_elem.getUserDefinedMetaClass()
									.equalsIgnoreCase(GlobalVariables.INTERFACE_BLOCK_METACLASS)
									|| intf_elem.getUserDefinedMetaClass()
											.equalsIgnoreCase(GlobalVariables.DELEGATEINTERFACE_METACLASS)) {
								model_intf_elements.add(intf_elem.getName() + ":" + component_ele.getName());

							}
						}
					}
				}
			}
		}
	}

	// @SuppressWarnings("unchecked")
	@SuppressWarnings("unchecked")
	public void createModelJson() throws IOException {
		String path=System.getProperty("user.home") ;
		FileWriter file1 = new FileWriter(path+"\\JsonFileComparison\\Modelelements.json");
		JSONObject modelObjectJson = new JSONObject();
		JSONArray partitionArray = new JSONArray();
		JSONArray clusterArray = new JSONArray();
		JSONArray SoftwareArray = new JSONArray();
		JSONArray componentArray = new JSONArray();
		JSONArray interfaceBlockArray = new JSONArray();
		partitionArray.addAll(model_partition_elements);
		clusterArray.addAll(model_cluster_elements);
		SoftwareArray.addAll(model_software_elements);
		componentArray.addAll(model_component_elements);
		interfaceBlockArray.addAll(model_intf_elements);
		modelObjectJson.put(GlobalVariables.PARTITION_METACLASS, partitionArray);
		modelObjectJson.put(GlobalVariables.CLUSTER_METACLASS, clusterArray);
		modelObjectJson.put(GlobalVariables.COMPONENT_METACLASS, componentArray);
		modelObjectJson.put(GlobalVariables.INTERFACE_BLOCK_METACLASS, interfaceBlockArray);
		modelObjectJson.put(GlobalVariables.SOFTWARE_METACLASS, SoftwareArray);
		file1.write(modelObjectJson.toJSONString());
		file1.close();
	}

	@SuppressWarnings("unchecked")
	public void readModelJsonMap() {
		try {
			String path=System.getProperty("user.home") ;
			JSONObject obj = (JSONObject) parser.parse(new FileReader(path+"\\JsonFileComparison\\Modelelements.json"));
			modelElements_map.put(GlobalVariables.PARTITION_METACLASS,
					(List<String>) obj.get(GlobalVariables.PARTITION_METACLASS));
			modelElements_map.put(GlobalVariables.CLUSTER_METACLASS,
					(List<String>) obj.get(GlobalVariables.CLUSTER_METACLASS));
			modelElements_map.put(GlobalVariables.COMPONENT_METACLASS,
					(List<String>) obj.get(GlobalVariables.COMPONENT_METACLASS));
			modelElements_map.put(GlobalVariables.INTERFACE_BLOCK_METACLASS,
					(List<String>) obj.get(GlobalVariables.INTERFACE_BLOCK_METACLASS));
			modelElements_map.put(GlobalVariables.SOFTWARE_METACLASS,
					(List<String>) obj.get(GlobalVariables.SOFTWARE_METACLASS));
		} catch (Exception e) {
			logger.info("Exception while reading Model Json file\n" + e.toString());
		}
	}

	public void compareElements(String element) throws IOException {
		ArrayList<String> commonJsonMap = (ArrayList<String>) commonJsonElements_map.get(element);
		ArrayList<String> modelJsonMap = (ArrayList<String>) modelElements_map.get(element);
		if(commonJsonMap != null && modelJsonMap != null)
		{
		int size = commonJsonMap.size();
		for (int j = size - 1; j >= 0; j--) {
			String e1 = commonJsonMap.get(j);
			if (modelJsonMap.contains(e1)) {
				commonJsonMap.remove(e1); // commonJson contains elements not present in modelJson
				modelJsonMap.remove(e1); // modelJson contains elements not present in commonJson
			}
		}
		elementMissingInModelJson_Map.put(element, commonJsonMap);
		elementMissingInGeneralJson_Map.put(element, modelJsonMap);

		// store compared elements in JSON file
		JSONObject obj = new JSONObject();
		obj.put("ElementsMissingInModelJson", elementMissingInModelJson_Map);
		obj.put("ElementMissingInGeneralJson", elementMissingInGeneralJson_Map);
		String path=System.getProperty("user.home") ;
		FileWriter file1 = new FileWriter(path+"\\JsonFileComparison\\Compare.json");
		file1.write(obj.toJSONString());
		file1.close();
	  }
	}

	@SuppressWarnings("unchecked")
	public static void performJsonCheck() throws IOException {
		CompareJson sampleJson = new CompareJson();
		try {
			String path=System.getProperty("user.home") ;
			File folder = new File(path+"\\JsonFiles");

			if (!folder.exists())
            CidlCollectorHandler.getInstance().createDirectory(folder);
			FilenameFilter textFilefilter =  (File dir, String name) -> {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".json")) {
						return true;
					} else {
						return false;
					}
				
			};
			// List of all the text files
			File[] filesList = folder.listFiles(textFilefilter);
			for (i = 0; i < filesList.length; i++) {
				sampleJson.readAllJsonFiles(filesList);
				logger.info("File name: " + filesList[i]);
			}
		} catch (Exception e) {
			logger.info(e.toString());
		}
		sampleJson.createCommonJson();
		sampleJson.readCommonJsonMap();
		sampleJson.readModelElements();
		sampleJson.createModelJson();
		sampleJson.readModelJsonMap();
		sampleJson.compareElements(GlobalVariables.SOFTWARE_METACLASS);
		sampleJson.compareElements(GlobalVariables.PARTITION_METACLASS);
		sampleJson.compareElements(GlobalVariables.CLUSTER_METACLASS);
		sampleJson.compareElements(GlobalVariables.COMPONENT_METACLASS);
		sampleJson.compareElements(GlobalVariables.INTERFACE_BLOCK_METACLASS);
	}
}

