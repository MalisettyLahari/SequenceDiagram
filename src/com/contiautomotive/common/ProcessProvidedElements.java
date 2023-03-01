package com.contiautomotive.common;

import java.io.FileWriter;
import java.util.HashMap;

import org.apache.logging.log4j.*;
import org.eclipse.emf.common.util.EList;
import org.json.simple.JSONArray;

import com.contiautomotive.architecture.tool.handlers.CidlCollectorHandler;
import com.contiautomotive.cidl.cidl.AccessibleInterface;
import com.contiautomotive.cidl.cidl.Component;
import com.contiautomotive.cidl.cidl.DelegateInterface;
import com.contiautomotive.cidl.cidl.Parameter;
import com.contiautomotive.cidl.cidl.Plugin;
import com.contiautomotive.cidl.cidl.PluginTemplate;
import com.contiautomotive.cidl.cidl.ProvidedConstant;
import com.contiautomotive.cidl.cidl.ProvidedFunction;
import com.contiautomotive.cidl.cidl.ProvidedInterface;
import com.contiautomotive.cidl.cidl.ProvidedPort;
import com.contiautomotive.cidl.cidl.ProvidedVariable;
import com.contiautomotive.cidl.cidl.SubComponent;
import com.contiautomotive.cidl.cidl.Type;
import com.google.gson.JsonArray;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPFlow;
import com.telelogic.rhapsody.core.IRPInstance;
import com.telelogic.rhapsody.core.IRPLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPort;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPTag;
import com.telelogic.rhapsody.core.IRPType;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

public class ProcessProvidedElements {

	private static final Logger logger = LogManager.getLogger(ProcessProvidedElements.class);
	// Variables declaration - do not modify
	javax.swing.JInternalFrame jInternalFrame = new javax.swing.JInternalFrame();
	javax.swing.JPanel jPanel_Main = new javax.swing.JPanel();
	javax.swing.JButton cancelButton = new javax.swing.JButton();
	javax.swing.JPanel jPanel_Sub = new javax.swing.JPanel();
	javax.swing.JLabel jLabel = new javax.swing.JLabel();
	javax.swing.JTextField textField = new javax.swing.JTextField();

	static IRPModelElement element_toAdd = null;
	static IRPModelElement existing_elementinModel = null;
	IRPModelElement userdefinedtypes = null;
	static IRPApplication app = RhapsodyAppServer.getActiveRhapsodyApplication();
	static IRPClassifier datatype = null;
	static IRPClass funcClass = null;
	IRPModelElement typesComponent = null;
	IRPUnit partition = null;
	static IRPUnit plugin = null;
	IRPInstance partition_part = null;
	IRPInstance subplugin_part = null;
	IRPInstance plugin_part = null;
	IRPInstance software_part = null;
	IRPInstance sub_part = null;
	IRPInstance cluster_part = null;
	static IRPUnit plugin_template = null;
	IRPUnit subplugin_template = null;
	IRPUnit subpartition = null;
	IRPUnit software = null;
	static IRPUnit swcomponent = null;
	static IRPUnit subcomponent = null;
	IRPUnit subsubcomponent = null;
	static IRPClass pluginintfblck = null;
	static IRPClass intfblck = null;
	static IRPClass intfblckplugin = null;
	IRPUnit subcluster = null;
	static IRPOperation operation = null;
	static IRPAttribute operationprop = null;
	IRPUnit cluster = null;
	static IRPPort port = null;
	boolean match = false;
	boolean portandConnectorExists = false;
	IRPPort reqport = null;
	IRPLink reqconnector = null;
	static IRPAttribute flowpropertyport = null;
	static IRPPort pluginport = null;
	static IRPAttribute flowprop = null;
	IRPFlow flow = null;
	IRPModelElement existing_element = null;
	static IRPClass subintfblck = null;
	IRPStereotype composite_stereotype = null;
	IRPStereotype thirdparty_stereotype = null;
	static String flowpropfullname = null;
	static String variablefullname = null;

	JsonArray jobjintfblcks = new JsonArray();
	static String constantfullname = null;
	static String operationfullname = null;
	String operationfullpath = null;
	String constantfullpath = null;
	String variablefullpath = null;
	String portfullpath = null;
	static HashMap<String, String> requiredoperationmap = new HashMap<String, String>();
	static HashMap<String, String> requiredvariablemap = new HashMap<String, String>();
	static HashMap<String, String> requiredportmap = new HashMap<String, String>();
	static HashMap<String, String> requiredconstantmap = new HashMap<String, String>();
	FileWriter file = null;
	JSONArray listofinterfaceblocks = new JSONArray();

	public static void processCompProvidedInterfaceBlockAndPort(IRPUnit pkg, AccessibleInterface ai,
			IRPUnit swcomponent, boolean elementPresent, Component component) {
		
		try {
		if(!elementPresent) {
		if (ai instanceof DelegateInterface) {
			intfblck = (IRPClass) swcomponent.findNestedElement("d_" + ((AccessibleInterface) ai).getName(),

					GlobalVariables.DELEGATEINTERFACE_METACLASS);
			if (intfblck == null) {
				intfblck = (IRPClass) swcomponent.addNewAggr(GlobalVariables.DELEGATEINTERFACE_METACLASS,
						"d_" + ((AccessibleInterface) ai).getName());
			} else {
				logger.info("Interface Block already Exists");
			}
			port = (IRPPort) swcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (port == null) {
				port = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
						((AccessibleInterface) ai).getName());
			} else {
				logger.info("Port already Exists");
			}
			funcClass = (IRPClass) getPortContractforDelegate(pkg, intfblck);
			if(funcClass!=null) {
			port.setContract(funcClass);
			}
		} else if (ai instanceof ProvidedInterface) {
			
			intfblck = (IRPClass) swcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
					GlobalVariables.INTERFACE_BLOCK_METACLASS);
			if (intfblck == null) {
				intfblck = (IRPClass) swcomponent.addNewAggr(GlobalVariables.INTERFACE_BLOCK_METACLASS,
						((AccessibleInterface) ai).getName());
			} else {
				logger.info("Interface Block already Exists");
			}
			port = (IRPPort) swcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (port == null) {
				port = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
						((AccessibleInterface) ai).getName());
			} else {
				logger.info("Port already Exists");
			}
			funcClass = (IRPClass) getPortContract(pkg, intfblck);
			if(funcClass!=null) {
			port.setContract(funcClass);
			}
		}
		else {
		logger.info("Codeg Interface");
		}
		}
		} catch (Exception e) {
			logger.info("Exception while processing Provided Elements of Component" + component.getName() + "\n" + e.toString());
		}
	}

	public static void processSubCompProvidedInterfaceBlockAndPort(IRPUnit pkg,AccessibleInterface ai,
			IRPUnit subcomponent,boolean elementPresent) {
		
		try {
			if(!elementPresent) {
		if (ai instanceof DelegateInterface) {
			subintfblck = (IRPClass) subcomponent.findNestedElement("_" + ((AccessibleInterface) ai).getName(),
					GlobalVariables.DELEGATEINTERFACE_METACLASS);
			if (subintfblck == null) {
				subintfblck = (IRPClass) subcomponent.addNewAggr(GlobalVariables.DELEGATEINTERFACE_METACLASS,
						"d_" + ((AccessibleInterface) ai).getName());
			} else {
				logger.info("Interface Block already Exists");
			}
			port = (IRPPort) subcomponent.getOwner().findNestedElement(((AccessibleInterface) ai).getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (port == null) {
				port = (IRPPort) subcomponent.getOwner().addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
						((AccessibleInterface) ai).getName());
			} else {
				logger.info("Port already Exists");
			}
			funcClass = (IRPClass) getPortContractforDelegate(pkg, subintfblck);
			if (funcClass != null) {
				port.setContract(funcClass);
			}
		} else {
			subintfblck = (IRPClass) subcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
					GlobalVariables.INTERFACE_BLOCK_METACLASS);
			if (subintfblck == null) {
				subintfblck = (IRPClass) subcomponent.addNewAggr(GlobalVariables.INTERFACE_BLOCK_METACLASS,
						((AccessibleInterface) ai).getName());
			} else {
				logger.info("Interface Block already Exists");
			}
		
			port = (IRPPort) subcomponent.getOwner().findNestedElement(((AccessibleInterface) ai).getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (port == null) {
				port = (IRPPort) subcomponent.getOwner().addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
						((AccessibleInterface) ai).getName());
			} else {
				logger.info("Port already Exists");
			}
			funcClass = (IRPClass) getPortContract(pkg, subintfblck);
			if (funcClass != null) {
				port.setContract(funcClass);
			}
		}
		}
		}
		catch (Exception e) {
			
			logger.info("Exception while adding provided interface");
		}
	}

	/**
	 * function to check if element @param name already exists
	 * 
	 * @param element_in_model
	 * @param metaclass
	 */
	private static IRPModelElement checkifElementExists(IRPUnit element_in_model, String name, String metaclass) {
		
		existing_elementinModel = element_in_model.findNestedElement(name, metaclass);
		return existing_elementinModel;
	}

	/**
	 * function to add new element @param element_name to model
	 * 
	 * @param model_element
	 * @param metaClass
	 */
	private static IRPModelElement addElementtoModel(IRPUnit model_element, String metaClass, String element_name) {
		try {
			element_toAdd = model_element.addNewAggr(metaClass, element_name);
		} catch (Exception e) {
			logger.info("exception caught while adding element to model ");
		}
		return element_toAdd;
	}

	public static void processPluginBlockAndPort(IRPUnit pkg, AccessibleInterface ai, IRPUnit plugin, IRPUnit swcomponent,
			Plugin pluginComponent,boolean elementPresent) {
		
		try {
		if(!elementPresent) {
		plugin = (IRPClass) checkifElementExists(swcomponent, ((Plugin) pluginComponent).getName(), "Plugin");
		if (ai instanceof DelegateInterface) {
			intfblckplugin = (IRPClass) checkifElementExists(plugin, ((AccessibleInterface) ai).getName(),
					GlobalVariables.DELEGATEINTERFACE_METACLASS);
			if (intfblckplugin == null) {
				intfblckplugin = (IRPClass) addElementtoModel(plugin, GlobalVariables.DELEGATEINTERFACE_METACLASS,
						"_" + ((AccessibleInterface) ai).getName());
			} else {
				logger.info("Interface Block already Exists");
			}
			port = (IRPPort) checkifElementExists(plugin, ((AccessibleInterface) ai).getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (port == null) {
				port = (IRPPort) addElementtoModel(plugin, GlobalVariables.PROXY_PORT_METACLASS,
						((AccessibleInterface) ai).getName());
			} else {
				logger.info("Interface Block already Exists");
			}
			funcClass = (IRPClass) getPortContractforDelegate(pkg, intfblckplugin);
			if (funcClass != null) {
				port.setContract(funcClass);
			}
		} else {
			intfblckplugin = (IRPClass) checkifElementExists(plugin, ((AccessibleInterface) ai).getName(),
					GlobalVariables.INTERFACE_BLOCK_METACLASS);
			if (intfblckplugin == null) {
				intfblckplugin = (IRPClass) addElementtoModel(plugin, GlobalVariables.INTERFACE_BLOCK_METACLASS,
						((AccessibleInterface) ai).getName());
			} else {
				logger.info("Interface Block already Exists");
			}
			port = (IRPPort) checkifElementExists(plugin, ((AccessibleInterface) ai).getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (port == null) {
				port = (IRPPort) addElementtoModel(plugin, GlobalVariables.PROXY_PORT_METACLASS,
						((AccessibleInterface) ai).getName());
			} else {
				logger.info("Port already Exists");
			}
			funcClass = (IRPClass) getPortContract(pkg, intfblck);
			if (funcClass != null) {
				port.setContract(funcClass);
			}
		}
		}
		}
		catch (Exception e) {
		
			logger.info("Element already present");
		}
	}

	/**
	 * Function to process Provided Elements from cidl Data
	 * 
	 * @param interface_element is the interface element(Operation, ProxyPort or
	 *                          FlowProperty to be added to the Component @param
	 *                          swcomponent
	 * @author uic38326
	 */
	public static void processCompInterface(IRPUnit prj,IRPUnit pkg,IRPUnit swcomponent,Object interface_element) {
		// is it provided
		if (interface_element instanceof ProvidedFunction) { // a function
			ProvidedFunction pf = (ProvidedFunction) interface_element;
			IRPClassifier genericType = getTypeFromProfile(prj);
			operation = (IRPOperation) intfblck.findNestedElement(((ProvidedFunction) pf).getName(),
					GlobalVariables.OPERATION_METACLASS);
			if (operation == null) {
				operation = (IRPOperation) intfblck.addNewAggr(GlobalVariables.OPERATION_METACLASS,
						((ProvidedFunction) pf).getName());
				operation.setReturns(genericType);
			}

			operationprop = (IRPAttribute) intfblck.findNestedElement(operation.getName(),
					GlobalVariables.OPERATIONPROPERTY_METACLASS);
			if (operationprop == null) {
				operationprop = (IRPAttribute) intfblck.addNewAggr(GlobalVariables.OPERATIONPROPERTY_METACLASS,
						operation.getName());
			}

			operationprop.setType(genericType);
			operationfullname = operationprop.getFullPathName();
			requiredoperationmap.put(pf.getName(), operationfullname);
			if (operationprop.getDependencies().getCount() <= 0) {
				IRPDependency dependency = operationprop.addDependencyBetween(operationprop, operation);
				dependency.changeTo(GlobalVariables.ALLOCATION_TAG);
			}
			EList<Parameter> p = pf.getParameters();
			IRPTag directiontag = operationprop.getTag(GlobalVariables.DIRECTION_TAG);
			if (pf.getReturns() != null) {
				if ((pf.getReturns().getType() != null) && (!(isVoidType(pf.getReturns().getType().getName())))) {
						operationprop.setTagValue(directiontag, "Out");
				}
			} else if (p.size() != 0) {
				operationprop.setTagValue(directiontag, "In");
			} else if (pf.getReturns() != null && p.size() != 0) {
				operationprop.setTagValue(directiontag, "Bidirectinal");
			} else {
				operationprop.setTagValue(directiontag, "Unspecified");
			}

		} else if (interface_element instanceof ProvidedPort) { // a port
			IRPModelElement typeof = null;
			ProvidedPort pp = (ProvidedPort) interface_element;
			flowpropertyport = (IRPAttribute) intfblck.findNestedElement(((ProvidedPort) pp).getName(),
					GlobalVariables.FLOW_PROPERTY_METACLASS);
			if (flowpropertyport == null) {
				flowpropertyport = (IRPAttribute) intfblck.addNewAggr(GlobalVariables.FLOW_PROPERTY_METACLASS,
						((ProvidedPort) pp).getName());
			}

			flowpropfullname = flowpropertyport.getFullPathName();

			requiredportmap.put(pp.getName(), flowpropfullname);

			IRPTag rl = (IRPTag) flowpropertyport.getTag(GlobalVariables.DIRECTION_TAG);
			if (rl == null) {
				rl = (IRPTag) flowpropertyport.addNewAggr(GlobalVariables.TAG_METACLASS, GlobalVariables.DIRECTION_TAG);

			}
			flowpropertyport.setTagValue(rl, "Bidirectional");
			Type type = pp.getType();
			if (type != null) {
				typeof = (IRPModelElement) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (typeof == null) {
					typeof = addTypeToModel(pkg, type);
				}
				flowpropertyport.setType((IRPClassifier) typeof);
			}

		} else if (interface_element instanceof ProvidedVariable) { // a variable
			IRPModelElement typeof = null;
			ProvidedVariable pv = (ProvidedVariable) interface_element;
			IRPAttribute flowprop = processFlowProperty(pv, intfblck);
			variablefullname = flowprop.getFullPathName();
			requiredvariablemap.put(pv.getName(), variablefullname);
			IRPTag directiontag = flowprop.getTag(GlobalVariables.DIRECTION_TAG);
			flowprop.setTagValue(directiontag, "Bidirectional");
		
			Type type = pv.getType();
			if (type != null) {
				typeof = (IRPModelElement) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (typeof == null) {
					typeof = addTypeToModel(pkg,type);

				}
				flowprop.setType((IRPClassifier) typeof);
			}

		} else if (interface_element instanceof ProvidedConstant) { // a constant
			IRPModelElement typeof = null;
			ProvidedConstant pc = (ProvidedConstant) interface_element;
			IRPAttribute flowprop = processFlowProperty(pc, intfblck);
			constantfullname = flowprop.getFullPathName();
			requiredconstantmap.put(pc.getName(), constantfullname);

			Type type = pc.getType();
			if (type != null) {
				typeof = (IRPModelElement) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (typeof == null) {
					typeof = addTypeToModel(pkg,type);

				}
				flowprop.setType((IRPClassifier) typeof);
			}

			IRPTag directiontag = flowprop.getTag(GlobalVariables.DIRECTION_TAG);
			flowprop.setTagValue(directiontag, "Out");

		}
	}
	public static boolean isVoidType(String type_name) {
        if(type_name!=null) {
		return (type_name.equalsIgnoreCase("void"));
        }
        return false;
	}
	
	private static IRPClass getPortContract(IRPModelElement owner, IRPClass intfblck) {
		funcClass = (IRPClass) owner.findNestedElementRecursive(intfblck.getName(),
				GlobalVariables.INTERFACE_BLOCK_METACLASS);
		return funcClass;
	}

	private static IRPClass getPortContractforDelegate(IRPModelElement owner, IRPClass intfblck) {
		funcClass = (IRPClass) owner.findNestedElementRecursive(intfblck.getName(),
				GlobalVariables.DELEGATEINTERFACE_METACLASS);
		return funcClass;
	}

	/**
	 * Function to check if the datatype is present in model
	 * 
	 * @param type of the element
	 * @author uic38326
	 */
	public static IRPClassifier addTypeToModel(IRPUnit pkg,Type type) {
		IRPModelElement typesComponent = pkg.findNestedElementRecursive(GlobalVariables.DATA_TYPE_CLASS,
				GlobalVariables.COMPONENT_METACLASS);
		if (typesComponent != null) {
			datatype = (IRPType) typesComponent.findNestedElement(type.getName(), GlobalVariables.DATA_TYPE_METACLASS);
			if (datatype == null) {
				datatype = (IRPType) typesComponent.addNewAggr(GlobalVariables.DATA_TYPE_METACLASS, type.getName());
			}
		}
		return datatype;
	}

	public static void processPluginInterface(IRPUnit prj, IRPUnit pkg, IRPUnit swcomponent,
			 Object interface_element) {
		// is it provided
		if (interface_element instanceof ProvidedFunction) { // a function
			ProvidedFunction pf = (ProvidedFunction) interface_element;
			IRPClassifier genericType =getTypeFromProfile(prj);

			operation = (IRPOperation) intfblckplugin.findNestedElement(((ProvidedFunction) pf).getName(),
					GlobalVariables.OPERATION_METACLASS);
			if (operation == null) {
				operation = (IRPOperation) intfblckplugin.addNewAggr(GlobalVariables.OPERATION_METACLASS,
						((ProvidedFunction) pf).getName());
				operation.setReturns(genericType);
			}

			operationprop = (IRPAttribute) intfblckplugin.findNestedElement(operation.getName(),
					GlobalVariables.OPERATIONPROPERTY_METACLASS);
			if (operationprop == null) {
				operationprop = (IRPAttribute) intfblckplugin.addNewAggr(GlobalVariables.OPERATIONPROPERTY_METACLASS,
						operation.getName());
			}
			operationprop.setType(genericType);

			operationfullname = operationprop.getFullPathName();

			requiredoperationmap.put(pf.getName(), operationfullname);

			if (operationprop.getDependencies().getCount() <= 0) {
				IRPDependency dependency = operationprop.addDependencyBetween(operationprop, operation);
				dependency.changeTo(GlobalVariables.ALLOCATION_TAG);
			}
			EList<Parameter> param = pf.getParameters();
			IRPTag directionTag = operationprop.getTag(GlobalVariables.DIRECTION_TAG);
			if (pf.getReturns() != null) {
				if ((pf.getReturns().getType() != null) && (!(isVoidType(pf.getReturns().getType().getName())))) {					
						operationprop.setTagValue(directionTag, "Out");
				}
			} else if (param.size() != 0) {
				operationprop.setTagValue(directionTag, "In");
			} else if (pf.getReturns() != null && param.size() != 0) {
				operationprop.setTagValue(directionTag, "Bidirectinal");
			} else {
				operationprop.setTagValue(directionTag, "Unspecified");
			}
		}

		else if (interface_element instanceof ProvidedPort) { // a port
			IRPClassifier typeof = null;
			ProvidedPort pp = (ProvidedPort) interface_element;
			flowpropertyport = (IRPAttribute) intfblckplugin.findNestedElement(((ProvidedPort) pp).getName(),
					GlobalVariables.FLOW_PROPERTY_METACLASS);
			if (flowpropertyport == null) {
				flowpropertyport = (IRPAttribute) intfblckplugin.addNewAggr(GlobalVariables.FLOW_PROPERTY_METACLASS,
						((ProvidedPort) pp).getName());
			}

			flowpropfullname = flowpropertyport.getFullPathName();

			requiredportmap.put(pp.getName(), flowpropfullname);
			IRPTag rl = (IRPTag) flowpropertyport.getTag(GlobalVariables.DIRECTION_TAG);
			if (rl == null) {
				rl = (IRPTag) flowpropertyport.addNewAggr(GlobalVariables.TAG_METACLASS, GlobalVariables.DIRECTION_TAG);

			}
			flowpropertyport.setTagValue(rl, "Bidirectional");
			Type type = pp.getType();
			if (type != null) {
				typeof = (IRPClassifier) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (typeof == null) {
					typeof = ProcessProvidedElements.addTypeToModel(pkg,type);
				}
				flowpropertyport.setType(typeof);
			}


		} else if (interface_element instanceof ProvidedVariable) { // a variable
			IRPClassifier typeof = null;
			ProvidedVariable pv = (ProvidedVariable) interface_element;
			IRPAttribute flowprop = ProcessProvidedElements.processFlowProperty(pv, intfblckplugin);

			variablefullname = flowprop.getFullPathName();

			requiredvariablemap.put(pv.getName(), variablefullname);
			Type type = pv.getType();
			if (type != null) {
				typeof = (IRPClassifier) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (typeof == null) {
					typeof = ProcessProvidedElements.addTypeToModel(pkg,type);
				}
				flowprop.setType(typeof);
			}
			IRPTag rl = (IRPTag) flowprop.getTag(GlobalVariables.DIRECTION_TAG);
			if (rl == null) {
				rl = (IRPTag) flowprop.addNewAggr(GlobalVariables.TAG_METACLASS, GlobalVariables.DIRECTION_TAG);

			}
			flowprop.setTagValue(rl, "Bidirectinal");


		} else if (interface_element instanceof ProvidedConstant) { // a constant
			ProvidedConstant pc = (ProvidedConstant) interface_element;
			IRPAttribute flowprop = ProcessProvidedElements.processFlowProperty(pc, intfblckplugin);

			constantfullname = flowprop.getFullPathName();

			requiredconstantmap.put(pc.getName(), constantfullname);
			Type type = pc.getType();
			if (type != null) {
				IRPClassifier typeof = (IRPClassifier) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (typeof == null) {
					typeof = ProcessProvidedElements.addTypeToModel(pkg,type);
				}
				flowprop.setType(typeof);
			}
			IRPTag rl = (IRPTag) flowprop.getTag(GlobalVariables.DIRECTION_TAG);
			if (rl == null) {
				rl = (IRPTag) flowprop.addNewAggr(GlobalVariables.TAG_METACLASS, GlobalVariables.DIRECTION_TAG);

			}
			flowprop.setTagValue(rl, "Out");
		}
	}

	/**
	 * Function to process Provided Elements from cidl Data
	 * 
	 * @param interface_element is the interface element(Operation, ProxyPort or
	 *                          FlowProperty to be added to the SubComponent @param
	 *                          subcomponent
	 * @author uic38326
	 */
	public static void processSubCompInterface(IRPUnit prj, IRPUnit pkg, IRPUnit swcomponent, 
			Object interface_element) {
		// is it provided
		try {
		if (interface_element instanceof ProvidedFunction) { // a function
			ProvidedFunction pf = (ProvidedFunction) interface_element;
			IRPClassifier genericType = getTypeFromProfile(prj);
			operation = (IRPOperation) subintfblck.findNestedElement(((ProvidedFunction) pf).getName(),
					GlobalVariables.OPERATION_METACLASS);
			if (operation == null) {
				operation = (IRPOperation) subintfblck.addNewAggr(GlobalVariables.OPERATION_METACLASS,
						((ProvidedFunction) pf).getName());
				operation.setReturns(genericType);
			}

			operationprop = (IRPAttribute) subintfblck.findNestedElement(operation.getName(),
					GlobalVariables.OPERATIONPROPERTY_METACLASS);
			if (operationprop == null) {
				operationprop = (IRPAttribute) subintfblck.addNewAggr(GlobalVariables.OPERATIONPROPERTY_METACLASS,
						operation.getName());
			}
			operationprop.setType(genericType);

			operationfullname = operationprop.getFullPathName();

			requiredoperationmap.put(pf.getName(), operationfullname);

			if (operationprop.getDependencies().getCount() <= 0) {
				IRPDependency opDependency = operationprop.addDependencyBetween(operationprop, operation);
				opDependency.changeTo(GlobalVariables.ALLOCATION_TAG);
			}
			EList<Parameter> p = pf.getParameters();
			IRPTag direcTag = operationprop.getTag(GlobalVariables.DIRECTION_TAG);
			if (pf.getReturns() != null) {
				if ((pf.getReturns().getType() != null) &&  (!(isVoidType(pf.getReturns().getType().getName())))) {
						operationprop.setTagValue(direcTag, "Out");
				}
			} else if (p.size() != 0) {
				operationprop.setTagValue(direcTag, "In");
			} else if (pf.getReturns() != null && p.size() != 0) {
				operationprop.setTagValue(direcTag, "Bidirectinal");
			} else {
				operationprop.setTagValue(direcTag, "Unspecified");
			}

		} else if (interface_element instanceof ProvidedPort) { // a port
			IRPClassifier typeof = null;
			ProvidedPort pp = (ProvidedPort) interface_element;
			flowpropertyport = (IRPAttribute) subintfblck.findNestedElement(((ProvidedPort) pp).getName(),
					GlobalVariables.FLOW_PROPERTY_METACLASS);
			if (flowpropertyport == null) {
				flowpropertyport = (IRPAttribute) subintfblck.addNewAggr(GlobalVariables.FLOW_PROPERTY_METACLASS,
						((ProvidedPort) pp).getName());
			}
			Type type = pp.getType();
			if (type != null) {
				typeof = (IRPClassifier) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (typeof == null) {
					typeof = addTypeToModel(pkg,type);
				}
			}
			flowpropertyport.setType(typeof);
			flowpropfullname = flowpropertyport.getFullPathName();
			requiredportmap.put(pp.getName(), flowpropfullname);
			IRPTag rl = (IRPTag) flowpropertyport.getTag(GlobalVariables.DIRECTION_TAG);
			if (rl == null) {
				rl = (IRPTag) flowpropertyport.addNewAggr(GlobalVariables.TAG_METACLASS, GlobalVariables.DIRECTION_TAG);

			}
			flowpropertyport.setTagValue(rl, "Bidirectional");
		
		} else if (interface_element instanceof ProvidedVariable) { // a variable
			ProvidedVariable pv = (ProvidedVariable) interface_element;
			IRPAttribute flowprop = processFlowProperty(pv, subintfblck);
			variablefullname = flowprop.getFullPathName();
			requiredvariablemap.put(pv.getName(), variablefullname);
			IRPTag directiontag = flowprop.getTag(GlobalVariables.DIRECTION_TAG);
			if(directiontag != null) {
			flowprop.setTagValue(directiontag, "Bidirectional");}
			Type type = pv.getType();
			if (type != null) {
				IRPClassifier datatype = (IRPClassifier) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (datatype == null) {
					datatype = addTypeToModel(pkg,type);
				}
				flowprop.setType(datatype);
			}

		} else if (interface_element instanceof ProvidedConstant) { // a constant
			ProvidedConstant pc = (ProvidedConstant) interface_element;
			IRPAttribute flowprop = processFlowProperty(pc, subintfblck);
			constantfullname = flowprop.getFullPathName();
			requiredconstantmap.put(pc.getName(), constantfullname);
			IRPTag directiontag = flowprop.getTag(GlobalVariables.DIRECTION_TAG);
			flowprop.setTagValue(directiontag, "Out");
			Type type = pc.getType();
			if (type != null) {
				IRPClassifier datatype = (IRPClassifier) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (datatype == null) {
					datatype = addTypeToModel(pkg,type);
				}
				flowprop.setType(datatype);

			}
		}
		}
		catch (Exception e) {
		logger.info("Exception while adding ports");
		
		}
	}

	public static IRPAttribute processFlowProperty(ProvidedConstant pc, IRPModelElement intfblck) {

		flowprop = (IRPAttribute) intfblck.findNestedElement(((ProvidedConstant) pc).getName(),
				GlobalVariables.FLOW_PROPERTY_METACLASS);
		if (flowprop == null) {
			flowprop = (IRPAttribute) intfblck.addNewAggr(GlobalVariables.FLOW_PROPERTY_METACLASS,
					((ProvidedConstant) pc).getName());
		}
		IRPTag rl = (IRPTag) flowprop.findNestedElement(GlobalVariables.DIRECTION_TAG, GlobalVariables.TAG_METACLASS);
		if (rl == null) {
			rl = (IRPTag) flowprop.addNewAggr(GlobalVariables.TAG_METACLASS, GlobalVariables.DIRECTION_TAG);
			flowprop.setTagValue(rl, "Out");
		}
		return flowprop;
	}

	public static IRPAttribute processFlowProperty(ProvidedVariable pv, IRPModelElement intfblck) {

		flowprop = (IRPAttribute) intfblck.findNestedElement(((ProvidedVariable) pv).getName(),
				GlobalVariables.FLOW_PROPERTY_METACLASS);
		if (flowprop == null) {
			flowprop = (IRPAttribute) intfblck.addNewAggr(GlobalVariables.FLOW_PROPERTY_METACLASS,
					((ProvidedVariable) pv).getName());
		}
		IRPTag rl = (IRPTag) flowprop.findNestedElement(GlobalVariables.DIRECTION_TAG, GlobalVariables.TAG_METACLASS);
		if (rl == null) {
			rl = (IRPTag) flowprop.addNewAggr(GlobalVariables.TAG_METACLASS, GlobalVariables.DIRECTION_TAG);
			flowprop.setTagValue(rl, "Out");
		}
		return flowprop;
	}

	/**
	 * function to process Interface Blocks and ProxyPorts of Plugin Templates
	 * @param pkg 
	 */
	public static void processPluginInterfaceBlockAndPort(IRPUnit pkg, AccessibleInterface ai, IRPUnit plugin_template,IRPUnit swcomponent,
			 boolean elementPresent) {
		try {
			if(!elementPresent) {
			pluginintfblck = (IRPClass) plugin_template.findNestedElement(((AccessibleInterface) ai).getName(),
					GlobalVariables.INTERFACE_BLOCK_METACLASS);
			if (pluginintfblck == null) {
				pluginintfblck = (IRPClass) plugin_template.addNewAggr(GlobalVariables.INTERFACE_BLOCK_METACLASS,
						((AccessibleInterface) ai).getName());
			} else {
				logger.info("Interface Block already Exists");
			}
			pluginport = (IRPPort) swcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (pluginport == null) {
				pluginport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
						((AccessibleInterface) ai).getName());
			} else {
				logger.info("Port already Exists");
			}
			funcClass = (IRPClass) getPortContract(pkg, pluginintfblck);
			if (funcClass != null) {
				pluginport.setContract(funcClass);
			}
			}
		} catch (Exception e) {
			logger.info("Error while adding Provided Interfaces of Plugin Templates\n");
		}
	}

	/**
	 * Function to get the generic type from profile
	 * 
	 * @author uic38326
	 * @param prj 
	 */

	public static IRPClassifier getTypeFromProfile(IRPUnit prj) {
		IRPClassifier partition_eleme = (IRPClassifier) prj.findNestedElementRecursive(GlobalVariables.GENERIC_TYPE,
				GlobalVariables.DATA_TYPE_METACLASS);
		return partition_eleme;
	}

	/**
	 * function to process Provided Elements of Plugins
	 * @param pkg 
	 * @param pkg 
	 */
	public static void processPluginTemplateInterface(IRPUnit prj, IRPUnit pkg, IRPUnit swcomponent,
			Object interface_element) {
        try {
		// is it provided
		if (interface_element instanceof ProvidedFunction) { // a function
			ProvidedFunction pf = (ProvidedFunction) interface_element;
			IRPClassifier genericType = getTypeFromProfile(prj);
			operation = (IRPOperation) pluginintfblck.findNestedElement(((ProvidedFunction) pf).getName(),
					GlobalVariables.OPERATION_METACLASS);
			if (operation == null) {
				operation = (IRPOperation) pluginintfblck.addNewAggr(GlobalVariables.OPERATION_METACLASS,
						((ProvidedFunction) pf).getName());
				operation.setReturns(genericType);
			}
			operationprop = (IRPAttribute) pluginintfblck.findNestedElement(operation.getName(),
					GlobalVariables.OPERATIONPROPERTY_METACLASS);
			if (operationprop == null) {
				operationprop = (IRPAttribute) pluginintfblck.addNewAggr(GlobalVariables.OPERATIONPROPERTY_METACLASS,
						operation.getName());
			}
			operationprop.setType(genericType);
			operationfullname = operationprop.getFullPathName();

			requiredoperationmap.put(pf.getName(), operationfullname);
			if (operationprop.getDependencies().getCount() <= 0) {
				IRPDependency dependency = operationprop.addDependencyBetween(operationprop, operation);
				dependency.changeTo(GlobalVariables.ALLOCATION_TAG);
			}
			EList<Parameter> p = pf.getParameters();
			IRPTag dirTag = operationprop.getTag(GlobalVariables.DIRECTION_TAG);
			if (pf.getReturns() != null) {
				if ((pf.getReturns().getType() != null)  &&  (!(isVoidType(pf.getReturns().getType().getName())))) {
						operationprop.setTagValue(dirTag, "Out");
				}
			} else if (p.size() != 0) {
				operationprop.setTagValue(dirTag, "In");
			} else if (pf.getReturns() != null && p.size() != 0) {
				operationprop.setTagValue(dirTag, "Bidirectinal");
			} else {
				operationprop.setTagValue(dirTag, "Unspecified");
			}
		} else if (interface_element instanceof ProvidedPort) { // a port
			ProvidedPort pp = (ProvidedPort) interface_element;
			IRPClassifier typeof = null;

			flowpropertyport = (IRPAttribute) pluginintfblck.findNestedElement(((ProvidedPort) pp).getName(),
					GlobalVariables.FLOW_PROPERTY_METACLASS);
			if (flowpropertyport == null) {
				flowpropertyport = (IRPAttribute) pluginintfblck.addNewAggr(GlobalVariables.FLOW_PROPERTY_METACLASS,
						((ProvidedPort) pp).getName());
			}

			flowpropfullname = flowpropertyport.getFullPathName();

			requiredportmap.put(pp.getName(), flowpropfullname);

			Type type = pp.getType();
			if (type != null) {
				typeof = (IRPClassifier) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (typeof == null) {
					typeof = ProcessProvidedElements.addTypeToModel(pkg,type);
				}

			}
			flowpropertyport.setType(typeof);
			IRPTag rl = (IRPTag) flowpropertyport.findNestedElement(GlobalVariables.DIRECTION_TAG,
					GlobalVariables.TAG_METACLASS);
			if (rl == null) {
				rl = (IRPTag) flowpropertyport.addNewAggr(GlobalVariables.TAG_METACLASS, GlobalVariables.DIRECTION_TAG);
				flowpropertyport.setTagValue(rl, "Bidirectional");
			}


		} else if (interface_element instanceof ProvidedVariable) { // a variable
			ProvidedVariable pv = (ProvidedVariable) interface_element;
			IRPAttribute flowprop = ProcessProvidedElements.processFlowProperty(pv, pluginintfblck);
			variablefullname = flowprop.getFullPathName();
			requiredvariablemap.put(pv.getName(), variablefullname);

			Type type = pv.getType();
			if (type != null) {
				IRPClassifier datatype = (IRPClassifier) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (datatype == null) {
					datatype = ProcessProvidedElements.addTypeToModel(pkg,type);
				}
				flowprop.setType(datatype);
			}
			IRPTag rl = (IRPTag) flowprop.getTag(GlobalVariables.DIRECTION_TAG);
			if (rl == null) {
				rl = (IRPTag) flowprop.addNewAggr(GlobalVariables.TAG_METACLASS, GlobalVariables.DIRECTION_TAG);

			}
			flowprop.setTagValue(rl, "Bidirectional");


		} else if (interface_element instanceof ProvidedConstant) { // a constant
			ProvidedConstant pc = (ProvidedConstant) interface_element;
			IRPAttribute flowprop = ProcessProvidedElements.processFlowProperty(pc, pluginintfblck);
			
			constantfullname = flowprop.getFullPathName();
			requiredconstantmap.put(pc.getName(), constantfullname);
			Type type = pc.getType();
			if (type != null) {
				IRPClassifier datatype = (IRPClassifier) swcomponent.findNestedElementRecursive(type.getName(),
						GlobalVariables.DATA_TYPE_METACLASS);
				if (datatype == null) {
					datatype = ProcessProvidedElements.addTypeToModel(pkg,type);
				}
				flowprop.setType(datatype);
			}
			IRPTag rl = (IRPTag) flowprop.getTag(GlobalVariables.DIRECTION_TAG);
			if (rl == null) {
				rl = (IRPTag) flowprop.addNewAggr(GlobalVariables.TAG_METACLASS, GlobalVariables.DIRECTION_TAG);

			}
			flowprop.setTagValue(rl, "Out");

		}
	} catch(Exception e) {
		logger.info("Exception caought while adding elements of plugin template interface");
	}
	}
        
	
}
