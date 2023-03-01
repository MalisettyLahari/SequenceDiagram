package com.contiautomotive.architecture.tool.handlers;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.*;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.contiautomotive.cidl.cidl.AbstractComponent;
import com.contiautomotive.cidl.cidl.AccessibleInterface;
import com.contiautomotive.cidl.cidl.Cluster;
import com.contiautomotive.cidl.cidl.CodegComponent;
import com.contiautomotive.cidl.cidl.Component;
import com.contiautomotive.cidl.cidl.CompositeComponent;
import com.contiautomotive.cidl.cidl.DelegateEntity;
import com.contiautomotive.cidl.cidl.DelegateInterface;
import com.contiautomotive.cidl.cidl.Partition;
import com.contiautomotive.cidl.cidl.Plugin;
import com.contiautomotive.cidl.cidl.PluginTemplate;
import com.contiautomotive.cidl.cidl.ProvidedElement;
import com.contiautomotive.cidl.cidl.ProvidedFunction;
import com.contiautomotive.cidl.cidl.ProvidedInterface;
import com.contiautomotive.cidl.cidl.ProvidedInterfaceElement;
import com.contiautomotive.cidl.cidl.ReqDocIdSpecification;
import com.contiautomotive.cidl.cidl.RequiredConstant;
import com.contiautomotive.cidl.cidl.RequiredFunction;
import com.contiautomotive.cidl.cidl.RequiredInterface;
import com.contiautomotive.cidl.cidl.RequiredInterfaceEntity;
import com.contiautomotive.cidl.cidl.RequiredPort;
import com.contiautomotive.cidl.cidl.RequiredVariable;
import com.contiautomotive.cidl.cidl.SchedulingBlock;
import com.contiautomotive.cidl.cidl.Software;
import com.contiautomotive.cidl.cidl.SubComponent;
import com.contiautomotive.cidl.cidl.Type;
import com.contiautomotive.cidl.cidl.TypeCollection;
import com.contiautomotive.cidl.cidl.impl.BaseTypeImpl;
import com.contiautomotive.cidl.cidl.impl.ModelImpl;
import com.contiautomotive.cidl.cidl.impl.NumericTypeImpl;
import com.contiautomotive.cidl.cidl.impl.PointerTypeImpl;
import com.contiautomotive.cidl.cidl.impl.StructTypeImpl;
import com.contiautomotive.common.GlobalVariables;
import com.contiautomotive.common.ProcessProvidedElements;
import com.contiautomotive.read.cidl.data.CollectCidlData;
import com.continental.plm.flavors.model.IFlavor;
import com.continental.plm.flavors.util.FlavorUtil;
import com.google.gson.JsonArray;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPFlow;
import com.telelogic.rhapsody.core.IRPGeneralization;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPInstance;
import com.telelogic.rhapsody.core.IRPLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPPort;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPSequenceDiagram;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPTag;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RhapsodyAppServer;
import com.contiautomotive.architecture.tool.diagrams.GenerateSequenceDiagram;
import com.contiautomotive.architecture.tool.handlers.CompareJson;
/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CidlCollectorHandler extends AbstractHandler {
	javax.swing.JFrame jFrame = new javax.swing.JFrame();
	private static final Logger logger = LogManager.getLogger(CidlCollectorHandler.class);
	private static CidlCollectorHandler cidlCollectorInstance;
	// Variables declaration - do not modify
	javax.swing.JInternalFrame jInternalFrame = new javax.swing.JInternalFrame();
	javax.swing.JPanel jPanelMain = new javax.swing.JPanel();
	javax.swing.JButton cancelButton = new javax.swing.JButton();
	javax.swing.JPanel jPanelSub = new javax.swing.JPanel();
	javax.swing.JLabel jLabel = new javax.swing.JLabel();
	javax.swing.JTextField textField = new javax.swing.JTextField();
	IRPUnit pkg = null;
	IRPModelElement elementToAdd = null;
	IRPModelElement existingElementinModel = null;
	IRPModelElement userdefinedtypes = null;
	IRPApplication app = null;
	IRPProject prj = null;
	IRPClass funcClass = null;
	IRPModelElement typesComponent = null;
	IRPUnit partition = null;
	IRPUnit plugin = null;
	IRPInstance partitionPart = null;
	IRPInstance subpluginPart = null;
	IRPInstance pluginPart = null;
	IRPInstance softwarePart = null;
	IRPInstance subPart = null;
	IRPInstance clusterPart = null;
	IRPUnit pluginTemplate = null;
	IRPUnit subpartition = null;
	IRPUnit software = null;
	IRPUnit swcomponent = null;
	IRPUnit subcomponent = null;
	IRPUnit subsubcomponent = null;
	IRPUnit nestedsubcomponent = null;
	IRPClass pluginintfblck = null;
	IRPClass intfblck = null;
	IRPClass intfblckplugin = null;
	IRPUnit subcluster = null;
	IRPOperation operation = null;
	IRPAttribute operationprop = null;
	IRPUnit cluster = null;
	IRPPort port = null;
	boolean match = false;
	boolean flag = false;
	boolean isPresent = false;
	boolean portPresent = false;
	boolean portandConnectorExists = false;
	IRPPort reqport = null;
	IRPLink reqconnector = null;
	IRPAttribute flowpropertyport = null;
	IRPPort pluginport = null;
	IRPAttribute flowprop = null;
	IRPFlow flow = null;
	IRPClass subintfblck = null;
	IRPStereotype compositeStereotype = null;
	IRPStereotype thirdpartyStereotype = null;
	String flowpropfullname = null;
	String variablefullname = null;
	HashMap<String, List<String>> exists = new HashMap<>();
	boolean elementPresent = false;
	JsonArray jobjintfblcks = new JsonArray();
	String constantfullname = null;
	String operationfullname = null;
	String operationfullpath = null;
	String constantfullpath = null;
	String variablefullpath = null;
	String portfullpath = null;
	HashMap<String, String> requiredoperationmap = new HashMap<>();
	HashMap<String, String> requiredvariablemap = new HashMap<>();
	HashMap<String, String> requiredportmap = new HashMap<>();
	HashMap<String, String> requiredconstantmap = new HashMap<>();
	HashMap<AbstractComponent, HashMap<String, HashMap<String, String>>> providedInterfaces = new HashMap<>();
	HashMap<Plugin, HashMap<String, HashMap<String, String>>> providedpluginInterfaces = new HashMap<>();
	HashMap<PluginTemplate, HashMap<String, HashMap<String, String>>> pluginTemplateInterfaces = new HashMap<>();
	HashMap<String, List<String>> existingelements = new HashMap<>();
	HashMap<String, List<String>> existingPorts = new HashMap<>();
	HashMap<String, List<String>> existingComponents = new HashMap<>();
	HashMap<String, HashMap<String, String>> existingConnectors = new HashMap<>();
	HashMap<String, List<String>> subcomponenthashmap = new HashMap<>();
	HashMap<String, List<String>> plugintemplateshashmap = new HashMap<>();
	HashMap<String, List<String>> subplugintemplates = new HashMap<>();
	FileWriter file = null;
	AbstractComponent providedComponent = null;
	public static final JSONArray listofinterfaceblocks = new JSONArray();
	HashMap<String, String> fullPathMap = new HashMap<>();
	String fullPathName = null;
	private Process rhpProces = null;
	 Software swTest = null;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {

			logger.info("Architecture tool is started...");
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL resource = classLoader.getResource("icons/Rhapsody.gif");
			ImageIcon icon = new ImageIcon(resource);
			jFrame.setIconImage(icon.getImage());
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("CNRTest");
//			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("IntegrationStream");


			if (project != null) {
				IFlavor flavor = FlavorUtil.getViewFlavor(project);
				logger.info(" flavor name " + flavor.getName());
				
					project.build(IncrementalProjectBuilder.AUTO_BUILD, "org.eclipse.xtext.ui.shared.xtextBuilder",
							null, new NullProgressMonitor());
					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					CollectCidlData pluginXtend = new CollectCidlData(project);
					if (pluginXtend.getResource() != null) {
						Software sw = null;

						HashMap<String, EList<Plugin>> pluginMap = new HashMap<>();
						HashMap<String, EList<TypeCollection>> typeMap = new HashMap<>();
						// search for first software resource
						for (Resource res : pluginXtend.getResource()) {
							EList<EObject> contents = res.getContents();
							for (EObject obj : contents) {
								if (obj instanceof ModelImpl && ((ModelImpl) obj).getSoftware() != null) {
									sw = ((ModelImpl) obj).getSoftware();
									swTest=sw;
								} else if (obj instanceof ModelImpl && ((ModelImpl) obj).getPlugins() != null
										&& !((ModelImpl) obj).getPlugins().isEmpty()) {
									EList<Plugin> pluginList = ((ModelImpl) obj).getPlugins();
									pluginList.forEach(pluginName -> {
										String componentName = pluginName.getComponent().getName();
										pluginMap.put(componentName, pluginList);
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
							initComponents(sw, pluginMap, typeMap);
						} else {
							logger.info("No software resource found");
						}
					}
				
			}
		} catch (Exception e) {
			logger.info("exception caught in incremental building of the project or\n"+ e.toString());
			
		}
		return null;
	}

	/**
	 * @param sw
	 * @param pluginList
	 * @param typeMap
	 *
	 */
	private void initComponents(Software sw, HashMap<String, EList<Plugin>> pluginMap,
			HashMap<String, EList<TypeCollection>> typeMap) {
		javax.swing.JButton browseButton = new javax.swing.JButton();
		javax.swing.JButton okButton = new javax.swing.JButton();
		javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame.getContentPane());
		jFrame.getContentPane().setLayout(jFrame1Layout);
		jFrame1Layout.setHorizontalGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 400, Short.MAX_VALUE));
		jFrame1Layout.setVerticalGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 300, Short.MAX_VALUE));

		jInternalFrame.setVisible(true);

		javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame.getContentPane());
		jInternalFrame.getContentPane().setLayout(jInternalFrame1Layout);
		jInternalFrame1Layout.setHorizontalGroup(jInternalFrame1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
		jInternalFrame1Layout.setVerticalGroup(jInternalFrame1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

		jFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		jFrame.setTitle("Architecture Tool");
		Font font = new Font("Tahoma", Font.BOLD, 11);
		jFrame.setFont(font);
		jFrame.setBounds(new java.awt.Rectangle(20, 20, 20, 20));
		jFrame.setForeground(new java.awt.Color(255, 102, 102));
		jFrame.setResizable(false);
		jFrame.setVisible(true);
		jFrame.setLocation(450, 300);
		jFrame.setAlwaysOnTop(true);

		jPanelMain.setBackground(new java.awt.Color(204, 204, 204));
		jPanelMain.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 4));
		jPanelMain.setForeground(new java.awt.Color(204, 204, 204));

		okButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		okButton.setText("Start");
		okButton.setEnabled(false);
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				String fillCheck = textField.getText();
				writeToFile(fillCheck);
				if (fillCheck.endsWith(".rpyx")) {
					try {

						jFrame.dispose();
						logger.info(textField.getText());
						logger.info("Start exporting cidl data to Rhapsody Model\n");
						IRPStereotype prjStereotype = (IRPStereotype) prj.findNestedElementRecursive("EBSArchitecture",
								GlobalVariables.STEREOTYPE_METACLASS);
						prj.setStereotype(prjStereotype);
						long starttime = System.currentTimeMillis();
						createJsonFile();
						// provided
//						getCIDLDataToRhapsody(sw, pluginMap, typeMap);
						processSequenceDiagram(sw);
						// required
//					    setTypesandFlows(sw, pluginMap);

						long endtime = System.currentTimeMillis();
						logger.info("Time for run" + (starttime - endtime));
						logger.info("gg");
						logger.info("End exporting cidl data to Rhapsody\n");
						textField.setText("");
						logger.info("Exported cidl Data to Rhapsody");
						logger.info("Architecture tool ended...............");
						app.writeToOutputWindow("Exported Cidl Data", "Exported Cidl Data\n");
						app.writeToOutputWindow("Rhapsody Model Generated", "Rhapsody Model Generated");
					} catch (Exception e) {
						logger.info(e.toString());
					}
				} else {
					logger.info("Please select rpyx file\n");
				}
			}

			private void createJsonFile() {
				try {
					String path=System.getProperty("user.home") ;
					File directory = new File(path+"\\JsonFiles");
					boolean dirSucess = false;
					if (!directory.exists())
						dirSucess   = createDirectory(directory);
					File fileName = new File(path+"\\JsonFiles\\TE_ST1.json");
					boolean success = false;
					if(!fileName.exists() && dirSucess)						
						success  = createFile(fileName);
					if(success)
					file = new FileWriter(path+"\\JsonFiles\\TE_ST1.json");
				} catch (IOException e) {
					logger.info(e.toString());
				}
			}
		});

		cancelButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		cancelButton.setText("Cancel");
		cancelButton.setMaximumSize(new java.awt.Dimension(60, 20));
		cancelButton.setMinimumSize(new java.awt.Dimension(60, 20));
		cancelButton.setPreferredSize(new java.awt.Dimension(60, 20));
		cancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!textField.getText().isEmpty())
					writeToFile(textField.getText());
				jFrame.dispose();
			}
		});

		jPanelSub.setBackground(new java.awt.Color(204, 204, 204));
		Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		jPanelSub.setBorder(javax.swing.BorderFactory.createTitledBorder(raisedetched, "Select Rhapsody rpyx File",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Tahoma", 1, 12))); // NOI18N

		jLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		jLabel.setText("File Path");
		textField.addActionListener(e-> {
			
		});

		browseButton.setBackground(new java.awt.Color(204, 204, 204));
		browseButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		browseButton.setText("...");
		browseButton.setMaximumSize(new java.awt.Dimension(45, 27));
		browseButton.setMinimumSize(new java.awt.Dimension(45, 27));
		browseButton.setPreferredSize(new java.awt.Dimension(45, 27));
		browseButton.addActionListener(e-> {

				okButton.setEnabled(true);
				try {
					browseButtonActionPerformed(okButton);
				} catch (IOException exp) {
					logger.info("Unable to select model path\n" + exp.toString());
					

			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanelSub);
		jPanelSub.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addGap(4, 4, 4)
						.addComponent(jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 53,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, 501,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(browseButton, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
						.addGap(4, 4, 4)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(jPanel2Layout.createSequentialGroup().addGap(3, 3, 3)
								.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(browseButton, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addGap(3, 3, 3)));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanelMain);
		jPanelMain.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
						.addComponent(jPanelSub, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(jPanelSub, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jFrame.getContentPane());
		jFrame.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanelMain,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanelMain,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		jFrame.pack();

	}

	/**
	 *
	 * @param fillCheck
	 */
	private void writeToFile(String fillCheck) {
		Path filePath = getUserDir();
		try {
			filePath = createFile(filePath);
			Files.write(Paths.get(filePath.toUri()), fillCheck.getBytes());
		} catch (IOException e) {
			logger.info("Unable to write file\n");
		}

	}

	/**
	 *
	 * @return
	 */
	private Path getUserDir() {
		String userDir = System.getProperty("user.home");
		Path filePath = Paths.get(userDir, "filePathInfo.txt");
		return filePath;
	}

	/**
	 *
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private Path createFile(Path filePath) throws IOException {
		if (!new File(filePath.toString()).exists())
			filePath = Files.createFile(filePath);
		return filePath;

	}

	private void browseButtonActionPerformed(JButton okButton) throws IOException {
		String textFieldValue = textField.getText();
		JFileChooser fileChooser;
		textField.setEditable(false);
		okButton.setEnabled(true);
		String filePath = readFilePath();
		if (textFieldValue.isEmpty()) {
			fileChooser = new JFileChooser(filePath);
		} else {
			fileChooser = new JFileChooser(textFieldValue);
		}

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Rpyx", "rpyx", "rpy"));

		fileChooser.setAcceptAllFileFilterUsed(false);
		String dllfilePath = FileLocator
				.toFileURL(Platform.getBundle("com.contiautomotive.architecture.tool").getEntry("/")).getFile();
		File fileName = new File(dllfilePath + "\\rhapsody.dll");
		System.load(fileName.toString());
		int result = fileChooser.showOpenDialog(jFrame);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			String selectedFilePath = selectedFile.toString();
			openProject(selectedFilePath,false,"");
			textField.setText(selectedFile.toString());
			textField.setToolTipText(textField.getText());
		}
	}

	private String readFilePath() {
		StringBuilder fileContenet = new StringBuilder("");
		try {
			Path filePath = getUserDir();
			filePath = createFile(filePath);

			@SuppressWarnings("resource")
			Stream<String> lines = Files.lines(filePath);
				List<String> filteredLines = lines.collect(Collectors.toList());
				filteredLines.forEach(name -> {
					if (!name.isEmpty())
						fileContenet.append(name);
				});
			
		} catch (IOException e1) {

			logger.info("Unable to read excel file path or Unable to create text file path\n");
		}
		
		return fileContenet.toString();
	}

	/**
	 *
	 * function to process cidl data and add the elements to model
	 *
	 * @param sw
	 * @param pluginList
	 * @param typeMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getCIDLDataToRhapsody(Software sw, HashMap<String, EList<Plugin>> pluginMap,
			HashMap<String, EList<TypeCollection>> typeMap) throws FileNotFoundException {
		String sct = "";
		FileWriter fileWriter = null;
		try {
			String path=System.getProperty("user.home") ;
			fileWriter = new FileWriter(path+"\\JsonFiles\\TE_ST1.json");
		} catch (IOException e) {
		  logger.info("Exception in getting file path\n" + e.toString());	
		}
		JSONObject obj = new JSONObject();
		JSONArray componentarray = new JSONArray();
		JSONArray clusterarray = new JSONArray();
		JSONArray partitionarray = new JSONArray();
		JSONArray softwareArray = new JSONArray();

		try {

			processSoftware(sw);
			softwareArray.add(sw.getName());
			for (Partition p : sw.getPartitions()) {
				logger.info("Partition Name: " + p.getName());
				processPartition(p, sw);
				partitionarray.add(p.getName());
				for (Cluster cl : p.getCluster()) {
					if (cl.getName() != null) {
						logger.info("Cluster Name: " + cl.getName());
						processCluster(cl, p);
						clusterarray.add(cl.getName());
						for (Component c : cl.getComponents()) {
							List<String> subcomps = new ArrayList<>();
							List<String> templates = new ArrayList<>();
							List<String> subtemplates = new ArrayList<>();
							if (c.getName() != null ) {
								logger.info("Component Name:" + c.getName());
								
								processComponent(c, pluginMap, typeMap);
//								processComponentScheduledDiagram(c);
								componentarray.add(c.getName()+":"+cluster.getName());
							}
							for (PluginTemplate template : c.getTemplates()) {
								if (template.getName() != null) {
									logger.info("Template Names: " + template.getName());
									processTemplate(template, c);
									templates.add(template.getName());
								}
							}
							for (SubComponent sc : c.getSubcomponents()) {
								if (sc.getName() != null) {
									logger.info("SubComponent Name : " + sc.getName());
									processSubComponent(sc, typeMap);
//									processSubComponentScheduledDiagram(sc);
									componentarray.add(sc.getName()+":"+cluster.getName());
									subcomps.add(sc.getName());

								}
								for (PluginTemplate subtemplate : sc.getTemplates()) {
									if (subtemplate.getName() != null) {
										logger.info("Template Names:" + subtemplate.getName());
										processTemplate(subtemplate);
										templates.add(subtemplate.getName());
									}
								}
								for (SubComponent subsub : sc.getSubcomponents()) {
									if (subsub.getName() != null) {
										logger.info("Sub Sub Component Name: " + subsub.getName());
										processSubSubComponent(subsub, typeMap);
//										processSubComponentScheduledDiagram(sc);
										componentarray.add(subsub.getName()+":"+cluster.getName());
									}
									for (PluginTemplate subtemplate : subsub.getTemplates()) {
										if (subtemplate.getName() != null) {
											logger.info("Template Names: " + subtemplate.getName());
											processSubTemplate(subtemplate);
											templates.add(subtemplate.getName());
										}
									}
									for (SubComponent subcomp : subsub.getSubcomponents()) {
										if (subcomp.getName() != null) {
											logger.info("SubComponent Name: " + subcomp.getName());
											processSubSubSubComponent(subcomp, typeMap);
//											processSubComponentScheduledDiagram(sc);
											componentarray.add(subcomp.getName()+":"+cluster.getName());
										}
										for (PluginTemplate subtemplate : subcomp.getTemplates()) {
											if (subtemplate.getName() != null) {
												logger.info("Template Names: " + subtemplate.getName());
												processTemplate(subtemplate);
												templates.add(subtemplate.getName());
											}
										}
									}
								}
							subplugintemplates.put(sc.getName(),subtemplates);
							}
							subcomponenthashmap.put(c.getName(), subcomps);
							plugintemplateshashmap.put(c.getName(), templates);
					  }
					}
				}
				prj.save();
				}
				
				obj.put(GlobalVariables.SOFTWARE_METACLASS, software);
				obj.put(GlobalVariables.PARTITION_METACLASS, partitionarray);
				obj.put(GlobalVariables.CLUSTER_METACLASS, clusterarray);
				obj.put(GlobalVariables.COMPONENT_METACLASS, componentarray);
				obj.put(GlobalVariables.INTERFACE_BLOCK_METACLASS, listofinterfaceblocks);
				fileWriter.write(obj.toJSONString());

				int compCount=componentarray.size();
				int intfCount=listofinterfaceblocks.size();
				int clusterCount=clusterarray.size();
			logger.info("compCount :"+compCount + " intfCount :"+intfCount +" clusterCount :"+clusterCount);
			
			fileWriter.close();

		} catch (Exception e) {
			logger.info("Error in adding elements to Json File Or Error in adding elements to Rhapsody Model");
		}
		return sct;
	}
	
	public void processSequenceDiagram(Software sw) {
		try {
			processSoftware(sw);			
			for (Partition p : sw.getPartitions()) {				
				processPartition(p, sw);
				for (Cluster cl : p.getCluster()) {
					logger.info("Cluster Name: " + cl.getName());
					processCluster(cl, p);
					for (Component c : cl.getComponents()) {
						if (c.getName() != null) {
						  logger.info("Component Name :" + c.getName());
						  swcomponent = (IRPClass) checkifElementExists(subcluster, ((Component) c).getName(),
									GlobalVariables.CLASS_METACLASS);
						  if(swcomponent != null)
						      processComponentScheduledDiagram(c);
						}
						for (SubComponent sc : c.getSubcomponents()) {
							if (sc.getName() != null) {
								logger.info("SubComponent Name:" + sc.getName());
								subcomponent = (IRPClass) checkifElementExists(swcomponent, ((SubComponent) sc).getName(),
										GlobalVariables.CLASS_METACLASS);
								if(subcomponent != null)
								    processSubComponentScheduledDiagram(sc);
							}
							for (SubComponent subsub : sc.getSubcomponents()) {
								if (subsub.getName() != null) {
									logger.info("SubComponent Name:" + subsub.getName());
									subsubcomponent = (IRPClass) checkifElementExists(subcomponent, ((SubComponent) subsub).getName(),
											GlobalVariables.CLASS_METACLASS);
									if(subsubcomponent != null)
									    processSubSubComponentScheduledDiagram(subsub);
								}
								for (SubComponent subcomp : subsub.getSubcomponents()) {
									if (subcomp.getName() != null) {
										logger.info("SubComponent Name:" + subcomp.getName());
										nestedsubcomponent = (IRPClass) checkifElementExists(subsubcomponent,
												((SubComponent) subcomp).getName(), GlobalVariables.CLASS_METACLASS);
										if(nestedsubcomponent != null)
										    processNestedComponentScheduledDiagram(subcomp);
									}
								}
							}
						}
					}
				}
			
			}
		}
		catch(Exception e)
		{
			logger.info("Error while processing Sequence diagram");
		}
	}
	

	private void processComponentScheduledDiagram(Component c) {
		String componentFileName = c.eResource().getURI().lastSegment();
		List<String> cIDLDiagramElements = new LinkedList<>();
		HashMap<String, String> whenConditionMap = new HashMap<>();
//		required interface
		HashMap<String, String> requiredIntfs = new HashMap<>();
		for (RequiredInterface ri : c.getRequiredInterfaces()) {
			if (ri.getInterface() != null) { // provided interface must be available
				EObject intfObj = ri.getInterface();
				if (intfObj instanceof ProvidedInterface) {
					for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
						if (rie instanceof RequiredFunction) { // function, variable or content
							RequiredFunction rf = (RequiredFunction) rie;
							requiredIntfs.put(rf.getFunction().getName(), ri.getComponent().getName());
						}
					}
				}
			}
		}
		// Provided Interfaces
		for (AccessibleInterface ai : c.getProvidedInterfaces()) {
			if (ai instanceof ProvidedInterface) {
				ProvidedInterface pi = (ProvidedInterface) ai;
				for (ProvidedInterfaceElement pe : pi.getProvidedEntities()) {
					if (pe instanceof ProvidedFunction) { // get all provided functions
						ProvidedFunction pf = (ProvidedFunction) pe;
						SchedulingBlock schedule = pf.getScheduling();
						if (schedule != null) {
							logger.info("check ScheduleView Diagram for : " + pf.getName() + " for the component  => "
									+ swcomponent.getFullPathName());
							cIDLDiagramElements.clear();
							whenConditionMap.clear();
							cIDLDiagramElements.add(c.getName());													
							GenerateSequenceDiagram.getInstance().checkScheduleDiagram(swcomponent, pf, prj, requiredIntfs,
									cIDLDiagramElements, whenConditionMap, componentFileName);
						}
					}
				}
			}
		}
	}

	private void processSubComponentScheduledDiagram(SubComponent sc) {
		String componentFileName = sc.eResource().getURI().lastSegment();
		List<String> cIDLDiagramElements = new LinkedList<>();
		HashMap<String, String> whenConditionMap = new HashMap<>();
		// required interface
		HashMap<String, String> requiredIntfs = new HashMap<>();
		for (RequiredInterface ri : sc.getRequiredInterfaces()) {
			if (ri.getInterface() != null) { // provided interface must be available
				EObject intfObj = ri.getInterface();
				if (intfObj instanceof ProvidedInterface) {
					for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
						if (rie instanceof RequiredFunction) { // function, variable or content
							RequiredFunction rf = (RequiredFunction) rie;
							requiredIntfs.put(rf.getFunction().getName(), ri.getComponent().getName());
						}
					}
				}
			}
		}
		// Provided Interfaces
		for (AccessibleInterface ai : sc.getProvidedInterfaces()) {
			if (ai instanceof ProvidedInterface) {
				ProvidedInterface pi = (ProvidedInterface) ai;
				for (ProvidedInterfaceElement pe : pi.getProvidedEntities()) {
					if (pe instanceof ProvidedFunction) { // get all provided functions
						ProvidedFunction pf = (ProvidedFunction) pe;
						SchedulingBlock schedule = pf.getScheduling();
						if (schedule != null) {
							logger.info("check ScheduleView Diagram for : " + pf.getName() + " for the component  => "
									+ subcomponent.getFullPathName());
							cIDLDiagramElements.clear();
							whenConditionMap.clear();
							cIDLDiagramElements.add(sc.getName());							
							GenerateSequenceDiagram.getInstance().checkScheduleDiagram(subcomponent, pf, prj, requiredIntfs,
									cIDLDiagramElements, whenConditionMap, componentFileName);
						}
					}
				}
			}
		}
	}
	
	private void processSubSubComponentScheduledDiagram(SubComponent sc) {
		String componentFileName = sc.eResource().getURI().lastSegment();
		List<String> cIDLDiagramElements = new LinkedList<>();
		HashMap<String, String> whenConditionMap = new HashMap<>();
		// required interface
		HashMap<String, String> requiredIntfs = new HashMap<>();
		for (RequiredInterface ri : sc.getRequiredInterfaces()) {
			if (ri.getInterface() != null) { // provided interface must be available
				EObject intfObj = ri.getInterface();
				if (intfObj instanceof ProvidedInterface) {
					for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
						if (rie instanceof RequiredFunction) { // function, variable or content
							RequiredFunction rf = (RequiredFunction) rie;
							requiredIntfs.put(rf.getFunction().getName(), ri.getComponent().getName());
						}
					}
				}
			}
		}
		// Provided Interfaces
		for (AccessibleInterface ai : sc.getProvidedInterfaces()) {
			if (ai instanceof ProvidedInterface) {
				ProvidedInterface pi = (ProvidedInterface) ai;
				for (ProvidedInterfaceElement pe : pi.getProvidedEntities()) {
					if (pe instanceof ProvidedFunction) { // get all provided functions
						ProvidedFunction pf = (ProvidedFunction) pe;
						SchedulingBlock schedule = pf.getScheduling();
						if (schedule != null) {
							logger.info("check ScheduleView Diagram for : " + pf.getName() + " for the component  => "
									+ subsubcomponent.getFullPathName());
							cIDLDiagramElements.clear();
							whenConditionMap.clear();
							cIDLDiagramElements.add(sc.getName());							
							GenerateSequenceDiagram.getInstance().checkScheduleDiagram(subsubcomponent, pf, prj, requiredIntfs,
									cIDLDiagramElements, whenConditionMap, componentFileName);
						}
					}
				}
			}
		}
	}
	
	private void processNestedComponentScheduledDiagram(SubComponent sc) {
		String componentFileName = sc.eResource().getURI().lastSegment();
		List<String> cIDLDiagramElements = new LinkedList<>();
		HashMap<String, String> whenConditionMap = new HashMap<>();
		// required interface
		HashMap<String, String> requiredIntfs = new HashMap<>();
		for (RequiredInterface ri : sc.getRequiredInterfaces()) {
			if (ri.getInterface() != null) { // provided interface must be available
				EObject intfObj = ri.getInterface();
				if (intfObj instanceof ProvidedInterface) {
					for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
						if (rie instanceof RequiredFunction) { // function, variable or content
							RequiredFunction rf = (RequiredFunction) rie;
							requiredIntfs.put(rf.getFunction().getName(), ri.getComponent().getName());
						}
					}
				}
			}
		}
		// Provided Interfaces
		for (AccessibleInterface ai : sc.getProvidedInterfaces()) {
			if (ai instanceof ProvidedInterface) {
				ProvidedInterface pi = (ProvidedInterface) ai;
				for (ProvidedInterfaceElement pe : pi.getProvidedEntities()) {
					if (pe instanceof ProvidedFunction) { // get all provided functions
						ProvidedFunction pf = (ProvidedFunction) pe;
						SchedulingBlock schedule = pf.getScheduling();
						if (schedule != null) {
							logger.info("check ScheduleView Diagram for : " + pf.getName() + " for the component  => "
									+ nestedsubcomponent.getFullPathName());
							cIDLDiagramElements.clear();
							whenConditionMap.clear();
							cIDLDiagramElements.add(sc.getName());							
							GenerateSequenceDiagram.getInstance().checkScheduleDiagram(nestedsubcomponent, pf, prj, requiredIntfs,
									cIDLDiagramElements, whenConditionMap, componentFileName);
						}
					}
				}
			}
		}
	}
	
	
	
	private HashMap<String, List<String>> checkElementsinModel(IRPUnit cluster, String component) {

		existingelements.clear();
		IRPModelElement componentInPkg = cluster.findNestedElement(component, GlobalVariables.CLASS_METACLASS);
		IRPCollection components = componentInPkg.getNestedElementsByMetaClass(GlobalVariables.PORT_METACLASS, 0);
		@SuppressWarnings("unchecked")
		List<String> elementnames = (List<String>) components.toList().stream().filter(obj ->((IRPModelElement)obj).getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.PROXY_PORT_METACLASS)).map(obj ->((IRPModelElement)obj).getName()).collect(Collectors.toList()); 
		existingelements.put(component, elementnames);
		return existingelements;
	}

	/**
	 * function to process required interfaces from cidl
	 *
	 * @param sw
	 * @param pluginMap
	 * @return
	 */
	public String setTypesandFlows(Software sw, HashMap<String, EList<Plugin>> pluginMap) {
		String types = "";
		try {
			processSoftware(sw);
			
				for (Partition p : sw.getPartitions()) {
					processPartition(p, sw);
					for (Cluster cl : p.getCluster()) {
						if (cl.getName() != null) {
							logger.info("Cluster Name: " + cl.getName());
							processCluster(cl, p);
							
								for (Component c : cl.getComponents()) {
									if (c.getName() != null && !c.getName().equalsIgnoreCase("ComAL")) {
										logger.info("Component Name :" + c.getName());
									
										processComponentFlowsAndTypes(c, cl, pluginMap);
										
								
									for (PluginTemplate template : c.getTemplates()) {
										if (template.getName() != null) {
											logger.info("Template Name:" + template.getName());
											processTemplateFlowsAndTypes(template);
										}
									}
									for (SubComponent sc : c.getSubcomponents()) {
										if (sc.getName() != null) {
											logger.info("SubComponent Name:" + sc.getName());
											
											processSubComponentFlowsAndTypes(sc, c);
										}
										for (PluginTemplate template : sc.getTemplates()) {
											if (template.getName() != null) {
												logger.info("Template Name: " + template.getName());
												processSubTemplateFlowsAndTypes(template);
											}
										}
										for (SubComponent subsub : sc.getSubcomponents()) {
											if (subsub.getName() != null) {
												logger.info("SubComponent Name:" + subsub.getName());
												processSubSubComponentFlowsAndTypes(subsub, sc);
											}
											for (PluginTemplate template : subsub.getTemplates()) {
												if (template.getName() != null) {
													logger.info("Template Name: " + template.getName());
													processSubTemplateFlowsAndTypes(template);
												}
											}
											for (SubComponent subcomp : subsub.getSubcomponents()) {
												if (subcomp.getName() != null) {
													logger.info("SubComponent Name:" + subcomp.getName());
													processSubSubComponentFlowsAndTypes(subcomp, subsub);
												}
												for (PluginTemplate template : subcomp.getTemplates()) {
													if (template.getName() != null) {
														logger.info("Template Name:" + template.getName());
														processSubTemplateFlowsAndTypes(template);
													}
												}
											}
										}
									}
								}
							}
							

						}
					}
					prj.save();
				}
			
		} catch (Exception e) {
			logger.info("Error while adding elements to the model \n" + e.toString());
			
		}
		return types;
	}

	/**
	 * function to process Plugin templates of sub components
	 *
	 * @param template
	 * @param sc
	 */

	private void processSubTemplate(PluginTemplate template) {
		
		try {
			pluginTemplate = (IRPUnit) checkifElementExists(subsubcomponent, template.getName(),
					GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
			if (pluginTemplate == null) {
				pluginTemplate = (IRPUnit) addElementtoModel(subsubcomponent,
						GlobalVariables.PLUGIN_TEMPLATE_METACLASS, template.getName());
				String description = template.getDesc();
				pluginTemplate.setDescription(description);

				subpluginPart = (IRPInstance) checkifElementExists(subcomponent,
						GlobalVariables.PART_KEYWORD + template.getName(), GlobalVariables.PART_USER_METACLASS);
				if (subpluginPart == null) {
					subpluginPart = (IRPInstance) addElementtoModel(subcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + template.getName());
					subpluginPart.setOtherClass((IRPClassifier) pluginTemplate);
				}
				IRPTag tag= (IRPTag) checkifElementExists(pluginTemplate, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (tag == null) {
					  addElementtoModel(pluginTemplate, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
					
				}
				IRPTag docId = (IRPTag) checkifElementExists(pluginTemplate, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (docId == null) {
					 addElementtoModel(pluginTemplate, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
				}
			} else {
				logger.info("Plugin Template : " + template.getName() + " already exists");
			}
			// Provided Interface
			existingelements = checkElementsinModel(subsubcomponent, template.getName());

			

				HashMap<String, HashMap<String, String>> interfaces = new HashMap<>();

				HashMap<String, String> providedElementsMap = new HashMap<>();
				elementPresent = false;

				for (AccessibleInterface ai : template.getProvidedInterfaces()) {

					elementPresent = false;

					for (List<String> value : existingelements.values()) {
						if (value.contains(ai.getName())) {
							elementPresent = true;
							break;
						}
					}
					if(!elementPresent) {
					ProcessProvidedElements.processPluginInterfaceBlockAndPort(pkg, ai, pluginTemplate, subcomponent,
							 elementPresent);
					}
					if (ai instanceof ProvidedInterface) {
						ProvidedInterface pi = (ProvidedInterface) ai;
						
							for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
								providedElementsMap.put(pie.getName(), pie.getName());
								if (!elementPresent) {

									ProcessProvidedElements.processPluginTemplateInterface(prj, pkg,  subcomponent, 
											pie);
								}
							}
						
					}

					interfaces.putIfAbsent(ai.getName(), providedElementsMap);
				}

				pluginTemplateInterfaces.put(template, interfaces);

			
		} catch (Exception e) {
			logger.info("Exception while adding Plugin Template Or Plugin Template Interfaces\n");
		}
	}

	/**
	 * function to process Plugin templates of sub components
	 *
	 * @param template
	 * @param sc
	 */

	@SuppressWarnings("unchecked")
	private void processTemplate(PluginTemplate template) {
		
		try {
			pluginTemplate = (IRPUnit) checkifElementExists(subcomponent, template.getName(),
					GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
			if (pluginTemplate == null) {
				pluginTemplate = (IRPUnit) addElementtoModel(subcomponent, GlobalVariables.PLUGIN_TEMPLATE_METACLASS,
						template.getName());
				String description = template.getDesc();
				pluginTemplate.setDescription(description);
				fullPathName = pluginTemplate.getFullPathName();
				fullPathMap.putIfAbsent(pluginTemplate.getName(), fullPathName);
				subpluginPart = (IRPInstance) checkifElementExists(subcomponent,
						GlobalVariables.PART_KEYWORD + template.getName(), GlobalVariables.PART_USER_METACLASS);
				if (subpluginPart == null) {
					subpluginPart = (IRPInstance) addElementtoModel(subcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + template.getName());
					subpluginPart.setOtherClass((IRPClassifier) pluginTemplate);
				}
				IRPTag archiTag = (IRPTag) checkifElementExists(pluginTemplate, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (archiTag == null) {
					addElementtoModel(pluginTemplate, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
				}
				IRPTag reqId = (IRPTag) checkifElementExists(pluginTemplate, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqId == null) {
					 addElementtoModel(pluginTemplate, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
				}
			} else {
				logger.info("Plugin Template : " + template.getName() + " already exists");
			}
			// Provided Interface
			existingelements = checkElementsinModel(subcomponent, template.getName());

			

				HashMap<String, HashMap<String, String>> interfaces = new HashMap<>();

				HashMap<String, String> providedElements = new HashMap<>();
				elementPresent = false;

				for (AccessibleInterface intfe : template.getProvidedInterfaces()) {

					elementPresent = false;

					for (List<String> value : existingelements.values()) {
						if (value.contains(intfe.getName())) {
							elementPresent = true;
							break;
						}
					}
					if(!elementPresent) {
					ProcessProvidedElements.processPluginInterfaceBlockAndPort(pkg, intfe, pluginTemplate, swcomponent,
							 elementPresent);
					}
					if (intfe instanceof ProvidedInterface) {
						listofinterfaceblocks.add(intfe.getName()+":"+template.getName());
						ProvidedInterface pi = (ProvidedInterface) intfe;
						
							for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
								providedElements.put(pie.getName(), pie.getName());
								if (!elementPresent) {

									ProcessProvidedElements.processPluginTemplateInterface(prj, pkg, swcomponent, 
											pie);
								}
							}
						
					}

					interfaces.putIfAbsent(intfe.getName(), providedElements);
				}

				pluginTemplateInterfaces.put(template, interfaces);

			
		} catch (Exception e) {
			logger.info("Exception while adding Plugin Template Or Plugin Template Interfaces\n");
		}
	}

	/**
	 * function to check if element @param name already exists
	 *
	 * @param element_in_model
	 * @param metaclass
	 */
	private IRPModelElement checkifElementExists(IRPUnit elementInModel, String name, String metaclass) {
	
		existingElementinModel = elementInModel.findNestedElement(name, metaclass);
		return existingElementinModel;
	}

	/**
	 * function to add new element @param element_name to model
	 *
	 * @param model_element
	 * @param metaClass
	 */
	private IRPModelElement addElementtoModel(IRPUnit modelElement, String metaClass, String elementName) {
		try {
			elementToAdd = modelElement.addNewAggr(metaClass, elementName);
		} catch (Exception e) {
			logger.info("exception caught while adding element to model\n" + e.toString());
			
		}
		return elementToAdd;
	}

	/**
	 * function to process Plugin Templates of Components
	 */

	@SuppressWarnings("unchecked")
	private void processTemplate(PluginTemplate template, Component c) {
		try {
			pluginTemplate = (IRPClass) checkifElementExists(swcomponent, template.getName(),
					GlobalVariables.CLASS_METACLASS);
			if (pluginTemplate == null) {
				pluginTemplate = (IRPUnit) addElementtoModel(swcomponent, GlobalVariables.PLUGIN_TEMPLATE_METACLASS,
						template.getName());
				String description = template.getDesc();
				pluginTemplate.setDescription(description);
				fullPathName = pluginTemplate.getFullPathName();
				fullPathMap.putIfAbsent(pluginTemplate.getName(), fullPathName);
				pluginPart = (IRPInstance) checkifElementExists(swcomponent,
						GlobalVariables.PART_KEYWORD + template.getName(), GlobalVariables.PART_USER_METACLASS);
				if (pluginPart == null) {
					pluginPart = (IRPInstance) addElementtoModel(swcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + template.getName());
					pluginPart.setOtherClass((IRPClassifier) pluginTemplate);
				} else {
					logger.info("Element already exists\n");
				}
				IRPTag rl = (IRPTag) checkifElementExists(pluginTemplate, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					addElementtoModel(pluginTemplate, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
				}
				IRPTag reqid = (IRPTag) checkifElementExists(pluginTemplate, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					 addElementtoModel(pluginTemplate, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
				}

			} else {
				logger.info("Plugin Template : " + template.getName() + " already exists");
			}

			// Provided Interface

			existingelements = checkElementsinModel(subcluster, c.getName());

			

				HashMap<String, HashMap<String, String>> interfaceMap = new HashMap<>();

				HashMap<String, String> providedelements = new HashMap<>();
				elementPresent = false;
				for (AccessibleInterface intf : template.getProvidedInterfaces()) {
					elementPresent = false;
					for (List<String> value : existingelements.values()) {
						if (value.contains(intf.getName())) {
							elementPresent = true;
							break;
						}
					}
					if(!elementPresent) {
					ProcessProvidedElements.processPluginInterfaceBlockAndPort(pkg, intf, pluginTemplate, swcomponent,
							 elementPresent);
					}
					if (intf instanceof ProvidedInterface) {
						listofinterfaceblocks.add(intf.getName()+":"+template.getName());
						ProvidedInterface pi = (ProvidedInterface) intf;
						
							for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
								providedelements.put(pie.getName(), pie.getName());
								if (!elementPresent) {
									ProcessProvidedElements.processPluginTemplateInterface(prj, pkg, swcomponent, 
											pie);

								}
							}
						
					}

					interfaceMap.putIfAbsent(intf.getName(), providedelements);
				}

				pluginTemplateInterfaces.put(template, interfaceMap);

			
		} catch (Exception e) {
			
			logger.info("Exception caught while adding plugin template");
		}

	}

	/**
	 * function to process required interfaces of Plugin Templates of Components
	 */
	private void processSubTemplateFlowsAndTypes(PluginTemplate template) {
		
		pluginTemplate = (IRPClass) subcomponent.findNestedElement(template.getName(),
				GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
		// Required Interfaces
		existingConnectors = checkforConnectors(subcomponent, template.getName());
		portPresent = false;
		try {
			for (RequiredInterface ri : template.getRequiredInterfaces()) {
				portPresent = false;
				existingConnectors = checkforConnectors(subcomponent, template.getName());
				for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
					ProvidedElement conjPort = ri.getInterface();
					AbstractComponent compName = ri.getComponent();
					for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
						HashMap<String, String> connector = entry.getValue();
						String connector_entry = connector.get(compName.getName() + "__" + conjPort.getName());
						if (connector_entry != null) {
							portPresent = true;
							break;
						} else {
							portPresent = false;
						}
					}

				}
			}
		} catch (Exception e) {
			logger.info("Error while processing Required Interfaces\n");
		}
	}



	/**
	 * function to process required interfaces of Plugin Templates of Components
	 */
	private void processTemplateFlowsAndTypes(PluginTemplate template) {
		
		pluginTemplate = (IRPClass) swcomponent.findNestedElement(template.getName(),
				GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
		// Required Interfaces
		existingConnectors = checkforConnectors(swcomponent, template.getName());
		portPresent = false;
		try {
			for (RequiredInterface ri : template.getRequiredInterfaces()) {
				portPresent = false;
				existingConnectors = checkforConnectors(swcomponent, template.getName());
				for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
					ProvidedElement conj_port = ri.getInterface();
					AbstractComponent compname = ri.getComponent();
					for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
						HashMap<String, String> connector = entry.getValue();
						String connector_entry = connector.get(compname.getName() + "__" + conj_port.getName());

						if (connector_entry != null) {
							portPresent = true;
							break;
						} else {
							portPresent = false;
							break;
						}
					}

					if (!portPresent) {
						processRequiredInterfaces(pluginTemplate, ri, rie);
						
					}
				}
			}
		} catch (Exception e) {
			logger.info("Error while processing Required Interfaces\n");
		}
	}

	/**
	 * function to process software
	 */
	private void processSoftware(Software sw) {
		
		try {
			software = (IRPClass) checkifElementExists(pkg, ((Software) sw).getName(),
					GlobalVariables.SOFTWARE_METACLASS);
			if (software == null) {
				software = (IRPClass) addElementtoModel(pkg, GlobalVariables.SOFTWARE_METACLASS,
						((Software) sw).getName());
				String description = sw.getDesc();
				software.setDescription(description);
			}
		} catch (Exception e) {
			logger.info("Exception while adding Software to Model\n" + e.toString());
			
		}

	}

	/**
	 *
	 * function to process Nested Subcomponents
	 *
	 * @param subComponent
	 * @param typeMap
	 */
	private void processSubSubSubComponent(SubComponent subComponent,
			HashMap<String, EList<TypeCollection>> typeMap) {
		try {
			nestedsubcomponent = (IRPClass) checkifElementExists(subsubcomponent,
					((SubComponent) subComponent).getName(), GlobalVariables.CLASS_METACLASS);
			if (nestedsubcomponent == null) {
				nestedsubcomponent = (IRPUnit) addElementtoModel(subsubcomponent, GlobalVariables.COMPONENT_METACLASS,
						((SubComponent) subComponent).getName());
				String description = subComponent.getDesc();
				String label = subComponent.getLongName();
				nestedsubcomponent.setDescription(description);
				nestedsubcomponent.setDisplayName(label);

				nestedsubcomponent.setSeparateSaveUnit(1);
				if (typeMap.containsKey(nestedsubcomponent.getName())) {
					EList<TypeCollection> type = typeMap.get(nestedsubcomponent.getName());
					for (TypeCollection types : type) {
						EList<Type> abscomp = types.getTypes();
						abscomp.forEach(datatype -> {
							if ((!datatype.getName().equalsIgnoreCase("void")) && (datatype instanceof NumericTypeImpl || datatype instanceof StructTypeImpl
									|| datatype instanceof PointerTypeImpl || datatype instanceof BaseTypeImpl)) {								
									IRPClassifier data = (IRPClassifier) nestedsubcomponent
											.findNestedElement(datatype.getName(), GlobalVariables.DATA_TYPE_METACLASS);
									if (data == null) {
										data = (IRPClassifier) addElementtoModel(nestedsubcomponent,
												GlobalVariables.DATA_TYPE_METACLASS, datatype.getName());
									}
							   }
						});

					}
				}
				String thirdParty = subComponent.getThirdParty();
				if (thirdParty != null && thirdpartyStereotype != null) {
						nestedsubcomponent.addSpecificStereotype(thirdpartyStereotype);					
				}
				if ((subComponent instanceof CompositeComponent) && (compositeStereotype != null)) {
						nestedsubcomponent.addSpecificStereotype(compositeStereotype);
				}
				String archi = subComponent.getArchitect();
				IRPTag rl = (IRPTag) checkifElementExists(nestedsubcomponent, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					rl = (IRPTag) addElementtoModel(nestedsubcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
					if (archi != null) {
						nestedsubcomponent.setTagValue(rl, archi);
					}
				}
				IRPTag reqid = (IRPTag) checkifElementExists(nestedsubcomponent, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					reqid = (IRPTag) addElementtoModel(nestedsubcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
					ReqDocIdSpecification req = subComponent.getReq();
					for (Integer id : req.getReqDocIds()) {
						nestedsubcomponent.setTagValue(reqid, id.toString());
					}
				}
				subPart = (IRPInstance) checkifElementExists(subsubcomponent,
						GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName(),
						GlobalVariables.PART_USER_METACLASS);
				if (subPart == null) {
					subPart = (IRPInstance) addElementtoModel(subsubcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName());
					subPart.setOtherClass((IRPClassifier) nestedsubcomponent);
				}
			} else {
				logger.info("SubComponent : " + subComponent.getName() + " already exists");
			}
			// Provided Interfaces
			existingelements = checkElementsinModel(subsubcomponent, subComponent.getName());
		
				HashMap<String, HashMap<String, String>> interfaces = new HashMap<>();
				HashMap<String, String> providedelements = new HashMap<>();
				elementPresent = false;
				for (AccessibleInterface ai : subComponent.getProvidedInterfaces()) {
					elementPresent = false;
					
					if(!elementPresent) {
					processNestedSubProvidedInterfaceBlockAndPort(ai, elementPresent,subComponent);
					}
					if (ai instanceof ProvidedInterface) {
						ProvidedInterface pi = (ProvidedInterface) ai;
						for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
							providedelements.put(ai.getName(), pie.getName());
							if (!elementPresent) {
								ProcessProvidedElements.processSubCompInterface(prj, pkg,subsubcomponent,
										 pie);
							}
						}
					}

				}

				providedInterfaces.put(subComponent, interfaces);			
		} catch (Exception e) {
			logger.info("Exception caught while adding Sub Components\n");
		}
	}

	private void processNestedSubProvidedInterfaceBlockAndPort(AccessibleInterface ai,
			boolean elementPresent, SubComponent subComponent) {
		try {
		if (!elementPresent) {
			if (ai instanceof DelegateInterface) {
				subintfblck = (IRPClass) nestedsubcomponent.findNestedElement(
						"_" + ((AccessibleInterface) ai).getName(), GlobalVariables.DELEGATEINTERFACE_METACLASS);
				if (subintfblck == null) {
					subintfblck = (IRPClass) nestedsubcomponent.addNewAggr(GlobalVariables.DELEGATEINTERFACE_METACLASS,
							"_" + ((AccessibleInterface) ai).getName());
				}
				port = (IRPPort) nestedsubcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (port == null) {
					port = (IRPPort) nestedsubcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							((AccessibleInterface) ai).getName());
				}
				funcClass = (IRPClass) getPortContract(pkg, port, ai, prj);
				port.setContract(funcClass);
			} else {
				subintfblck = (IRPClass) nestedsubcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
				if (subintfblck == null) {
					subintfblck = (IRPClass) nestedsubcomponent.addNewAggr(GlobalVariables.INTERFACE_BLOCK_METACLASS,
							((AccessibleInterface) ai).getName());
				}
				
				port = (IRPPort) nestedsubcomponent.getOwner().getOwner().getOwner().findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (port == null) {
					port = (IRPPort) nestedsubcomponent.getOwner().getOwner().getOwner().addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							((AccessibleInterface) ai).getName());
				}
				funcClass = (IRPClass) getIntfBlockFunctionClass(pkg, port, (IRPProject) prj);
				port.setContract(funcClass);
			}
		}
		}catch(Exception e) {
			logger.info("Exception while processing Provided Elements of Component" + subComponent.getName());
		}
	}

	/**
	 *
	 * function to process Nested Subcomponents
	 *
	 * @param subComponent
	 * @param typeMap
	 */
	@SuppressWarnings("unchecked")
	private void processSubSubComponent(SubComponent subComponent,
			HashMap<String, EList<TypeCollection>> typeMap) {
		try {
			subsubcomponent = (IRPClass) checkifElementExists(subcomponent, ((SubComponent) subComponent).getName(),
					GlobalVariables.CLASS_METACLASS);
			if (subsubcomponent == null) {
				subsubcomponent = (IRPUnit) addElementtoModel(subcomponent, GlobalVariables.COMPONENT_METACLASS,
						((SubComponent) subComponent).getName());
				String description = subComponent.getDesc();
				String label = subComponent.getLongName();
				subsubcomponent.setDescription(description);
				subsubcomponent.setDisplayName(label);

				subsubcomponent.setSeparateSaveUnit(1);
				if (typeMap.containsKey(subsubcomponent.getName())) {
					EList<TypeCollection> type = typeMap.get(subsubcomponent.getName());
					for (TypeCollection types : type) {
						EList<Type> abscomp = types.getTypes();
						abscomp.forEach(datatype -> {
							if ((!datatype.getName().equalsIgnoreCase("void")) && (datatype instanceof NumericTypeImpl || datatype instanceof StructTypeImpl
									|| datatype instanceof PointerTypeImpl || datatype instanceof BaseTypeImpl)) {
									IRPClassifier data = (IRPClassifier) subsubcomponent
											.findNestedElement(datatype.getName(), GlobalVariables.DATA_TYPE_METACLASS);
									if (data == null) {
										data = (IRPClassifier) addElementtoModel(subsubcomponent,
												GlobalVariables.DATA_TYPE_METACLASS, datatype.getName());
									}
							   }
						});

					}
				}
				String thirdParty = subComponent.getThirdParty();
				if (thirdParty != null && thirdpartyStereotype != null) {					
						subsubcomponent.addSpecificStereotype(thirdpartyStereotype);
				}
				if ((subComponent instanceof CompositeComponent) && (compositeStereotype != null)) {
						subsubcomponent.addSpecificStereotype(compositeStereotype);
				}
				String archi = subComponent.getArchitect();
				IRPTag rl = (IRPTag) checkifElementExists(subsubcomponent, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					rl = (IRPTag) addElementtoModel(subsubcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
					if (archi != null) {
						subsubcomponent.setTagValue(rl, archi);
					}
				}
				IRPTag reqid = (IRPTag) checkifElementExists(subsubcomponent, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					reqid = (IRPTag) addElementtoModel(subsubcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
					ReqDocIdSpecification req = subComponent.getReq();
					for (Integer id : req.getReqDocIds()) {
						subsubcomponent.setTagValue(reqid, id.toString());
					}
				}
				subPart = (IRPInstance) checkifElementExists(subcomponent,
						GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName(),
						GlobalVariables.PART_USER_METACLASS);
				if (subPart == null) {
					subPart = (IRPInstance) addElementtoModel(subcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName());
					subPart.setOtherClass((IRPClassifier) subsubcomponent);
				}
			} else {
				logger.info("SubComponent : " + subComponent.getName() + " already exists");
			}
			// Provided Interfaces
			existingelements = checkElementsinModel(subcomponent, subComponent.getName());
			
				HashMap<String, HashMap<String, String>> interfaces = new HashMap<>();
				HashMap<String, String> providedelements = new HashMap<>();
				elementPresent = false;
				for (AccessibleInterface ai : subComponent.getProvidedInterfaces()) {
					elementPresent = false;
					
					processSubProvidedInterfaceBlockAndPort(ai, elementPresent, subComponent);
					if (ai instanceof ProvidedInterface) {
						listofinterfaceblocks.add(ai.getName()+":"+subComponent.getName());
						ProvidedInterface pi = (ProvidedInterface) ai;
						for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
							providedelements.put(ai.getName(), pie.getName());
							if (!elementPresent) {
								ProcessProvidedElements.processSubCompInterface(prj, pkg, subcomponent, pie);
							}
						}
					}

				}

				providedInterfaces.put(subComponent, interfaces);

			
		} catch (Exception e) {
			logger.info("Exception caught while adding Sub Components\n");
		}
	}

	/**
	 * function to process Nested Subcomponents
	 *
	 * @param subComponent
	 * @param typeMap
	 */

	@SuppressWarnings("unchecked")
	private void processSubComponent(SubComponent subComponent,	HashMap<String, EList<TypeCollection>> typeMap) {

		try {
			subcomponent = (IRPClass) checkifElementExists(swcomponent, ((SubComponent) subComponent).getName(),
					GlobalVariables.CLASS_METACLASS);
			if (subcomponent == null) {
				subcomponent = (IRPUnit) addElementtoModel(swcomponent, GlobalVariables.COMPONENT_METACLASS,
						((SubComponent) subComponent).getName());
				String description = subComponent.getDesc();
				String label = subComponent.getLongName();
				subcomponent.setDescription(description);
				subcomponent.setDisplayName(label);
				fullPathName = subcomponent.getFullPathName();
				fullPathMap.putIfAbsent(subcomponent.getName(), fullPathName);
				subcomponent.setSeparateSaveUnit(1);
				if (typeMap.containsKey(subcomponent.getName())) {
					EList<TypeCollection> type = typeMap.get(subcomponent.getName());
					for (TypeCollection types : type) {
						EList<Type> abscomp = types.getTypes();
						abscomp.forEach(datatype -> {
							if ((!datatype.getName().equalsIgnoreCase("void")) && (datatype instanceof NumericTypeImpl || datatype instanceof StructTypeImpl
									|| datatype instanceof PointerTypeImpl || datatype instanceof BaseTypeImpl)) {								
									IRPClassifier data = (IRPClassifier) subcomponent
											.findNestedElement(datatype.getName(), GlobalVariables.DATA_TYPE_METACLASS);
									if (data == null) {
										data = (IRPClassifier) subcomponent
												.addNewAggr(GlobalVariables.DATA_TYPE_METACLASS, datatype.getName());

									}
							   }
						});

					}
				}
				String thirdParty = subComponent.getThirdParty();
				if (thirdParty != null && thirdpartyStereotype != null) {
						subcomponent.addSpecificStereotype(thirdpartyStereotype);
				}
				if ((subComponent instanceof CompositeComponent) && (compositeStereotype != null)) {
						subcomponent.addSpecificStereotype(compositeStereotype);
				}
				String archi = subComponent.getArchitect();
				IRPTag rl = (IRPTag) checkifElementExists(subcomponent, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					rl = (IRPTag) addElementtoModel(subcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
					if (archi != null) {
						subcomponent.setTagValue(rl, archi);
					}
				}
				IRPTag reqid = (IRPTag) checkifElementExists(subcomponent, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					reqid = (IRPTag) addElementtoModel(subcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
					ReqDocIdSpecification req = subComponent.getReq();
					for (Integer id : req.getReqDocIds()) {
						subcomponent.setTagValue(reqid, id.toString());
					}
				}
				subPart = (IRPInstance) checkifElementExists(swcomponent,
						GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName(),
						GlobalVariables.PART_USER_METACLASS);
				if (subPart == null) {
					subPart = (IRPInstance) addElementtoModel(swcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName());
					subPart.setOtherClass((IRPClassifier) subcomponent);
				}
			} else {
				fullPathName = subcomponent.getFullPathName();
				fullPathMap.putIfAbsent(subcomponent.getName(), fullPathName);
				logger.info("SubComponent : " + subComponent.getName() + " already exists");
			}

			// Provided Interfaces
			existingelements = checkElementsinModel(swcomponent, subComponent.getName());
			

				HashMap<String, HashMap<String, String>> interfaces = new HashMap<>();

				HashMap<String, String> providedelements = new HashMap<>();
				elementPresent = false;
				for (AccessibleInterface ai : subComponent.getProvidedInterfaces()) {

					for (List<String> value : existingelements.values()) {
						if (value.contains(ai.getName())) {
							elementPresent = true;
							break;
						}
					}
					if(!elementPresent) {
					ProcessProvidedElements.processSubCompProvidedInterfaceBlockAndPort(pkg, ai, subcomponent,
							elementPresent);
					}
					if (ai instanceof ProvidedInterface) {
						listofinterfaceblocks.add(ai.getName()+":"+subComponent.getName());
						ProvidedInterface pi = (ProvidedInterface) ai;
						for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
							providedelements.put(pie.getName(), pie.getName());
							if (!elementPresent) {
								ProcessProvidedElements.processSubCompInterface(prj, pkg,  swcomponent, pie);
							}
						}
					}

					interfaces.put(ai.getName(), providedelements);
				}

				providedInterfaces.put(subComponent, interfaces);

			
		} catch (Exception e) {
			
			logger.info("Exception while processing SubComponent : " + subComponent.getName());
		}
	}

	/**
	 * function to process required Interfaces of Nested Subcomponents
	 *
	 * @author uic38326
	 */
	@SuppressWarnings("unlikely-arg-type")
	private void processSubSubComponentFlowsAndTypes(SubComponent subComponent, SubComponent c) {
		
		try {
			IRPModelElement swComponent = subcluster.findNestedElementRecursive(((SubComponent) c).getName(),
					GlobalVariables.COMPONENT_METACLASS);
			subcomponent = (IRPClass) swComponent.findNestedElement(((SubComponent) subComponent).getName(),
					GlobalVariables.COMPONENT_METACLASS);

			for (AccessibleInterface ai : subComponent.getProvidedInterfaces()) {
				if (ai instanceof DelegateInterface) {
					for (DelegateEntity rie : ((DelegateInterface) ai).getDelegateEntities()) {
						AbstractComponent dcomp = rie.getDelegateComponent();
						ProvidedInterface pintf = rie.getDelegateInterface();
						IRPModelElement baseClass = pkg.findNestedElementRecursive(dcomp.getName(),
								GlobalVariables.CLASS_METACLASS);
						if (baseClass != null) {
							IRPModelElement mo = baseClass.findNestedElementRecursive(pintf.getName(),
									GlobalVariables.INTERFACE_BLOCK_METACLASS);
							intfblck = (IRPClass) pkg.findNestedElementRecursive(ai.getName(),
									GlobalVariables.CLASS_METACLASS);

							if (mo != null && intfblck != null) {
								IRPGeneralization myGen = intfblck.findGeneralization(mo.getName());
								if (myGen != null) {
									IRPClassifier baseclass = myGen.getBaseClass();
									if (!baseclass.equals(dcomp)) {
										intfblck.addGeneralization((IRPClassifier) mo);
										IRPGeneralization myNewGen = intfblck.findGeneralization(mo.getName());
										if (!baseclass.getName().equals(baseClass.getName())) {
											myNewGen.changeTo(GlobalVariables.REALIZATION_TAG);
										}
									}
								} else {
									intfblck.addGeneralization((IRPClassifier) mo);
									myGen = intfblck.findGeneralization(mo.getName());
									myGen.changeTo(GlobalVariables.REALIZATION_TAG);
								}
							}
							IRPCollection myGeneralizations = intfblck.getGeneralizations();
							for (Object generalizationelement : myGeneralizations.toList()) {
								IRPModelElement generalizationmodelelement = (IRPModelElement) generalizationelement;
								IRPGeneralization general = (IRPGeneralization) generalizationmodelelement;
								general.changeTo(GlobalVariables.REALIZATION_TAG);
							}
						}
					}
				}
			}

			// Required Interfaces
			existingConnectors = checkforConnectors(swComponent, subComponent.getName());
			portPresent = false;
			for (RequiredInterface ri : subComponent.getRequiredInterfaces()) {
				portPresent = false;
				existingConnectors = checkforConnectors(swComponent, subComponent.getName());
				for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
					ProvidedElement conj_port = ri.getInterface();
					AbstractComponent compname = ri.getComponent();
					if (conj_port instanceof ProvidedInterface || conj_port instanceof DelegateInterface) {
						IRPPort reqportForComp = (IRPPort) subcomponent.getOwner().getOwner().findNestedElement(compname.getName() + "__" + conj_port.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if(reqportForComp == null) {
						reqportForComp = (IRPPort) swComponent.getOwner().getOwner().addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
								compname.getName() + "__" + conj_port.getName());
						IRPClass compClass = prj.findClass(compname.getName());
						IRPClass funcClass_ = getRequiredPortContract(compClass, conj_port.getName());
						if (funcClass_ != null) {
							reqportForComp.setContract(funcClass_);
						}
					  }
					}
						for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
							HashMap<String, String> connector = entry.getValue();
							String connectorentry = connector.get(compname.getName() + "__" + conj_port.getName());
							if (connectorentry != null) {
								portPresent = true;
								break;
							} else {
								portPresent = false;
								break;
							}
						}

						if (!portPresent) {
							processRequiredSubSubInterfaces(subcomponent, swComponent, ri, rie);
						}

				}
			}
		} catch (Exception e) {
			logger.info("Error while processing Required Interface of SubComponent : " + subComponent.getName() + "\n" + e.toString());
			
		}
	}

	/**
	 * function to process required Interfaces of Subcomponents
	 *
	 * @author uic38326
	 */
	@SuppressWarnings("unlikely-arg-type")
	private void processSubComponentFlowsAndTypes(SubComponent subComponent, Component c) {
		
		try {

			subcomponent = (IRPClass) swcomponent.findNestedElementRecursive(((SubComponent) subComponent).getName(),
					GlobalVariables.COMPONENT_METACLASS);
			if (subcomponent == null) {
				processSubComponent(subComponent, null);
			}
			for (AccessibleInterface ai : subComponent.getProvidedInterfaces()) {
				if (ai instanceof DelegateInterface) {
					match = false;
					intfblck = (IRPClass) subcomponent.findNestedElement("d_" + ai.getName(),
							GlobalVariables.DELEGATEINTERFACE_METACLASS);
					IRPCollection myGeneralizations = intfblck.getGeneralizations();
					for (DelegateEntity rie : ((DelegateInterface) ai).getDelegateEntities()) {
						AbstractComponent dcomp = rie.getDelegateComponent();
						ProvidedInterface pintf = rie.getDelegateInterface();
						for (Object generalizationelement : myGeneralizations.toList()) {
							IRPGeneralization generalizationmodelelement = (IRPGeneralization) generalizationelement;
							if (generalizationmodelelement.getBaseClass().getName().equalsIgnoreCase(dcomp.getName())) {
								match = true;
								break;
							}
						}
						if (!match) {
							IRPModelElement baseClass = findSourceandDestination(dcomp.getName());
							IRPPort reqPort = (IRPPort) subcomponent.findNestedElement(
									dcomp.getName() + "__" + ai.getName(), GlobalVariables.PROXY_PORT_METACLASS);
							IRPLink reqConnector = (IRPLink) subcomponent
									.findNestedElement(dcomp.getName() + "__" + ai.getName(), GlobalVariables.LINK_TAG);

							if (reqPort == null) {
								reqPort = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
										dcomp.getName() + "__" + ai.getName());
							}

							reqPort.setIsReversed(1);
							if (reqConnector == null) {
								IRPModelElement fpartowner = findSourceandDestination(dcomp.getName());
								IRPInstance fpart = findPart(fpartowner);
								IRPPort fromport = (IRPPort) fpartowner.findNestedElementRecursive(pintf.getName(),
										GlobalVariables.PROXY_PORT_METACLASS);
								IRPModelElement tpartowner = pkg.findNestedElementRecursive(subcomponent.getName(),
										GlobalVariables.COMPONENT_METACLASS);
								IRPInstance tpart = findPart(tpartowner);
								IRPPort toport = (IRPPort) subcomponent.findNestedElementRecursive(ai.getName(),
										GlobalVariables.PROXY_PORT_METACLASS);
								if (reqConnector == null) {
									addConnector (subcomponent, fpart, tpart, fromport, toport,
											dcomp.getName(), ai.getName());
								}
							}

							if (baseClass != null) {
								IRPModelElement mo = baseClass.findNestedElementRecursive(pintf.getName(),
										GlobalVariables.INTERFACE_BLOCK_METACLASS);
								intfblck = (IRPClass) pkg.findNestedElementRecursive("d_" + ai.getName(),
										GlobalVariables.DELEGATEINTERFACE_METACLASS);
								if (mo != null && intfblck != null) {
									IRPGeneralization generalisation = intfblck.findGeneralization(mo.getName());
									if (generalisation != null) {
										IRPClassifier baseclass = generalisation.getBaseClass();
										if (!baseclass.equals(dcomp)) {
											intfblck.addGeneralization((IRPClassifier) mo);
											IRPGeneralization myNewGen = intfblck.findGeneralization(mo.getName());
											if (!baseclass.getName().equals(baseClass.getName())) {
												myNewGen.changeTo(GlobalVariables.REALIZATION_TAG);
											}
										}
									} else {
										intfblck.addGeneralization((IRPClassifier) mo);
										generalisation = intfblck.findGeneralization(mo.getName());
										generalisation.changeTo(GlobalVariables.REALIZATION_TAG);
									}
								}
								IRPCollection myGeneralization = intfblck.getGeneralizations();
								for (Object generalizationelement : myGeneralization.toList()) {
									IRPModelElement generalizationmodelelement = (IRPModelElement) generalizationelement;
									IRPGeneralization general = (IRPGeneralization) generalizationmodelelement;
									general.changeTo(GlobalVariables.REALIZATION_TAG);
								}
							} else {
								logger.info("Delegate interface cannot be resolved (REALIZATION)");
							}
							funcClass = (IRPClass) getPortContractforDelegate(pkg, intfblck);
							if (funcClass != null) {
								reqPort.setContract(funcClass);
							}
						}
					}
				}
			}
			// Required Interfaces
			existingConnectors.clear();
			portPresent = false;
			
				
				HashMap<RequiredInterface, HashMap<AbstractComponent, ProvidedElement>> reqInterface = new HashMap<>();
				HashMap<AbstractComponent, ProvidedElement> reqInterfaceAndComp = null;
				AbstractComponent absComp = null;
				ProvidedElement provEle = null;
				for (RequiredInterface ri : subComponent.getRequiredInterfaces()) {
					reqInterfaceAndComp = new HashMap<>();
					if(ri.getComponent() instanceof AbstractComponent)
						absComp = ri.getComponent();
					if(ri.getInterface() instanceof ProvidedElement)
						provEle = ri.getInterface();
					if(absComp != null && provEle != null) {
					reqInterfaceAndComp.put(absComp, provEle);
					reqInterface.put(ri, reqInterfaceAndComp);
					}
				}
				for (Entry<RequiredInterface, HashMap<AbstractComponent, ProvidedElement>> ri : reqInterface.entrySet()) {
					portPresent = false;
					if(existingConnectors.isEmpty()) {
					existingConnectors = checkforConnectors(swcomponent, subComponent.getName());}
					HashMap<AbstractComponent, ProvidedElement> reqInterfaceAndCompName = ri.getValue();
					ProvidedElement conj_port = null;
					AbstractComponent compname = null;
					for(Entry<AbstractComponent, ProvidedElement> entries : reqInterfaceAndCompName.entrySet()) {
					if(entries.getValue() instanceof ProvidedElement) {
					conj_port = (ProvidedElement) entries.getValue();}
					if(entries.getKey() instanceof AbstractComponent) {
					compname = (AbstractComponent) entries.getKey();}
					}
					if (conj_port instanceof ProvidedInterface || conj_port instanceof DelegateInterface) {
						IRPPort reqportForComp = (IRPPort) subcomponent.getOwner().findNestedElement(compname.getName() + "__" + conj_port.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if(reqportForComp == null) {
						reqportForComp = (IRPPort) subcomponent.getOwner().addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
								compname.getName() + "__" + conj_port.getName());
						IRPClass compClass = prj.findClass(compname.getName());
						IRPClass funcClass_ = getRequiredPortContract(compClass, conj_port.getName());
						if (funcClass_ != null) {
							reqportForComp.setContract(funcClass_);
						}
					  }
					}
					for (RequiredInterfaceEntity rie : ri.getKey().getRequiredEntities()) {
						List<SubComponent> subcomps = compname.getSubcomponents();
						List<PluginTemplate> templates = compname.getTemplates();
						
						if (conj_port instanceof ProvidedInterface) {
							IRPPort reqportForComp = (IRPPort) subcomponent.getOwner().findNestedElement(compname.getName() + "__" + conj_port.getName(),
									GlobalVariables.PROXY_PORT_METACLASS);
							if(reqportForComp == null) {
							reqportForComp = (IRPPort) subcomponent.getOwner().addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									compname.getName() + "__" + conj_port.getName());
							IRPClass compClass = prj.findClass(compname.getName());
							IRPClass funcClass_ = getRequiredPortContract(compClass, conj_port.getName());
							if (funcClass_ != null) {
								reqportForComp.setContract(funcClass_);
							}
						  }
						}
						
						for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
							HashMap<String, String> connector = entry.getValue();
							String connectorEntry = connector.get(compname.getName() + "__" + conj_port.getName());
							if (connectorEntry != null) {
								portPresent = true;
								break;
							} else {
								portPresent = false;
								break;
							}
						}
						if (!portPresent) {
							for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {

								HashMap<String, String> connector = entry.getValue();
								for (SubComponent subcompname : subcomps) {
									String connector_entry = connector
											.get(subcompname.getName() + "__" + conj_port.getName());
									if (connector_entry != null) {
										portPresent = true;
										break;
									} else {
										portPresent = false;
										break;
									}
								}
								for (PluginTemplate subcompname : templates) {
									Collection<String> value = connector.values();
									for (String name : value) {
										if (name.contains(subcompname.getName())) {
											portPresent = true;
											break;
										} else {
											portPresent = false;
											break;
										}
									}
								}

							}
								if ((!portPresent) && (rie instanceof RequiredConstant || rie instanceof RequiredFunction
										|| rie instanceof RequiredVariable || rie instanceof RequiredPort)) {
									processRequiredSubInterfaces(subcomponent, ri.getKey(), rie);
							}
						}
					}

				}
			
		} catch (Exception e) {
			
			logger.info("Exception while processing SubComponent Or its Required Interface: " + subComponent.getName());
		}
	}

	private void processTopLevelDelegations(AbstractComponent abscomp, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent) {
		

		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null) {
			try
			{
			reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, abscomp.getName(),
					provelem.getName());
			}
			catch(Exception e) {
				logger.info(e.getMessage());
			}
		}

		IRPModelElement mainElement = findSourceandDestination(maincomp.getName());
		IRPInstance mainpart = findPart(mainElement);
		IRPPort mainport = (IRPPort) mainElement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainElement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}

		}
		IRPLink mainconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainconnector == null) {
			try {
			addConnector(swcomponent, tpart, mainpart, toport, mainport, abscomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}

		if (abscomp instanceof SubComponent) {
			IRPUnit delegateelement = (IRPUnit) pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.COMPONENT_METACLASS);
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				try {
				addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						abscomp.getName(), provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
	}

	/**
	 *
	 * @param component
	 * @param cl
	 * @param pluginList
	 * @param typeMap
	 */

	@SuppressWarnings("unchecked")
	private void processComponent(Component component, HashMap<String, EList<Plugin>> pluginMap,
			HashMap<String, EList<TypeCollection>> typeMap) {

		try {
			swcomponent = (IRPClass) checkifElementExists(subcluster, ((Component) component).getName(),
					GlobalVariables.CLASS_METACLASS);
			if (swcomponent == null) {
				swcomponent = (IRPUnit) addElementtoModel(subcluster, GlobalVariables.COMPONENT_METACLASS,
						((Component) component).getName());
				String description = component.getDesc();
				String label = component.getLongName();
				swcomponent.setDescription(description);
				swcomponent.setDisplayName(label);
				fullPathName = swcomponent.getFullPathName();
				fullPathMap.putIfAbsent(swcomponent.getName(), fullPathName);
				swcomponent.setSeparateSaveUnit(1);

				if (typeMap.containsKey(swcomponent.getName())) {
					EList<TypeCollection> type = typeMap.get(swcomponent.getName());
					for (TypeCollection types : type) {
						EList<Type> abscomp = types.getTypes();
						abscomp.forEach(datatype -> {
							if (datatype instanceof NumericTypeImpl || datatype instanceof StructTypeImpl
									|| datatype instanceof PointerTypeImpl || datatype instanceof BaseTypeImpl) {
								IRPClassifier data = (IRPClassifier) checkifElementExists(swcomponent,
										datatype.getName(), GlobalVariables.DATA_TYPE_METACLASS);
								if ((!datatype.getName().equalsIgnoreCase("void")) && (data == null)) {									
										data = (IRPClassifier) addElementtoModel(swcomponent,
												GlobalVariables.DATA_TYPE_METACLASS, datatype.getName());									
								}
							}
						});

					}
				}
				String thirdParty = component.getThirdParty();
				if (thirdParty != null  && thirdpartyStereotype != null) {
						swcomponent.addSpecificStereotype(thirdpartyStereotype);
				}
				if ((component instanceof CompositeComponent) && (compositeStereotype != null)) {
						swcomponent.addSpecificStereotype(compositeStereotype);					
				}
				if (pluginMap.containsKey(swcomponent.getName())) {
					EList<Plugin> pluginList = pluginMap.get(swcomponent.getName());
					if (pluginList != null) {
						for (Plugin pluginname : pluginList) {
							logger.info("Plugin:" + pluginname.getName());
							pluginList.forEach(pluginComponent -> {
								processPlugin(pluginComponent);

							});
						}
					}
				}
				IRPTag rl = (IRPTag) checkifElementExists(swcomponent, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					rl = (IRPTag) addElementtoModel(swcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
					String archi = component.getArchitect();
					if (archi != null) {
						swcomponent.setTagValue(rl, archi);
					}
				}
				IRPTag reqid = (IRPTag) checkifElementExists(swcomponent, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					reqid = (IRPTag) addElementtoModel(swcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
					ReqDocIdSpecification req = component.getReq();
					for (Integer id : req.getReqDocIds()) {
						swcomponent.setTagValue(reqid, id.toString());
					}
				}
				clusterPart = (IRPInstance) subcluster.findNestedElementRecursive(
						GlobalVariables.PART_KEYWORD + ((Component) component).getName(),
						GlobalVariables.PART_USER_METACLASS);
				if (clusterPart == null) {
					clusterPart = (IRPInstance) addElementtoModel(subcluster, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + ((Component) component).getName());
					clusterPart.setOtherClass((IRPClassifier) swcomponent);
				}
				String genericlink= "http://frcvshare001.conti.de/artifacts/CV/CT_VC1_develop/lastFullFlight/DetailedDesign/html/";
                IRPHyperLink link=(IRPHyperLink) swcomponent.addNewAggr("HyperLink", swcomponent.getName()+".html");
                String particularlink= partition.getName()+"/"+ cluster.getName()+"/"+ swcomponent.getName()+"/"+swcomponent.getName()+ ".html";
                link.setURL(genericlink+particularlink);
			} else {

				if (pluginMap.containsKey(swcomponent.getName())) {
					EList<Plugin> pluginList = pluginMap.get(swcomponent.getName());
					if (pluginList != null) {
						for (Plugin pluginname : pluginList) {
							logger.info("Plugin:" + pluginname.getName());
							pluginList.forEach(pluginComponent -> {
								processPlugin(pluginComponent);

							});
						}
					}
				}
				fullPathName = swcomponent.getFullPathName();
				fullPathMap.putIfAbsent(swcomponent.getName(), fullPathName);
				logger.info("Component : " + component.getName() + " already exists");
			}
			// Provided Interfaces
			existingelements = checkElementsinModel(subcluster, component.getName());
			
				HashMap<String, HashMap<String, String>> interfaces = new HashMap<>();
				HashMap<String, String> providedelements = new HashMap<>();
				elementPresent = false;
				for (AccessibleInterface ai : component.getProvidedInterfaces()) {

					elementPresent = existingelements.values().stream().anyMatch(value -> value.contains(ai.getName()));
					if(!elementPresent) {
					ProcessProvidedElements.processCompProvidedInterfaceBlockAndPort(pkg, ai, swcomponent,
							elementPresent, component);
					}
					if (ai instanceof ProvidedInterface) {
						listofinterfaceblocks.add(ai.getName()+":"+component.getName());
						ProvidedInterface pi = (ProvidedInterface) ai;
						for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
							providedelements.put(pie.getName(), pie.getName());
							if (!elementPresent) {
								ProcessProvidedElements.processCompInterface(prj, pkg, swcomponent,  pie);
							}
						}
					}
					interfaces.putIfAbsent(ai.getName(), providedelements);
				}

				providedInterfaces.put(component, interfaces);

			
		} catch (Exception e) {
			
			logger.info("Exception while processing Component : " + component.getName());
		}
	}

	@SuppressWarnings("unchecked")
	private void processPlugin(Plugin pluginComponent) {
		try {
			plugin = (IRPClass) checkifElementExists(swcomponent, ((Plugin) pluginComponent).getName(),
					GlobalVariables.CLASS_METACLASS);
			if (plugin == null) {
				plugin = (IRPUnit) addElementtoModel(swcomponent, GlobalVariables.PLUGIN_METACLASS,
						((Plugin) pluginComponent).getName());
				String description = pluginComponent.getDesc();
				plugin.setDescription(description);
			} else {
				logger.info("Plugin" + pluginComponent.getName() + " already exists");
			}
			clusterPart = (IRPInstance) checkifElementExists(swcomponent,
					GlobalVariables.PART_KEYWORD + ((Plugin) pluginComponent).getName(),
					GlobalVariables.PART_USER_METACLASS);
			if (clusterPart == null) {
				clusterPart = (IRPInstance) addElementtoModel(swcomponent, GlobalVariables.PART_USER_METACLASS,
						GlobalVariables.PART_KEYWORD + ((Plugin) pluginComponent).getName());
				clusterPart.setOtherClass((IRPClassifier) plugin);
			}
			existingelements = checkElementsinModel(swcomponent, pluginComponent.getName());

			HashMap<String, HashMap<String, String>> interfaces = new HashMap<>();

			HashMap<String, String> providedelements = new HashMap<>();
			elementPresent = false;

			for (AccessibleInterface ai : pluginComponent.getProvidedInterfaces()) {
				elementPresent = false;
				for (List<String> value : existingelements.values()) {
					if (value.contains(ai.getName())) {
						elementPresent = true;
						break;
					}
				}

				ProcessProvidedElements.processPluginBlockAndPort(pkg, ai, plugin, swcomponent, pluginComponent,
						elementPresent);
				if (ai instanceof ProvidedInterface) {
					 listofinterfaceblocks.add(ai.getName()+":"+pluginComponent.getName());
					ProvidedInterface pi = (ProvidedInterface) ai;
					for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
						providedelements.put(pie.getName(), pie.getName());
						if (!elementPresent) {
							ProcessProvidedElements.processPluginInterface(prj, pkg, swcomponent,
									pie);

						}
					}
				}

				interfaces.putIfAbsent(ai.getName(), providedelements);
			}

			providedpluginInterfaces.put(pluginComponent, interfaces);
		} catch (Exception e) {
			
			logger.info("Error in processing Plugin" + pluginComponent.getName());
		}
	}

	@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
	private void processComponentFlowsAndTypes(Component component, Cluster cl,
			HashMap<String, EList<Plugin>> pluginMap) {

		try {
			swcomponent = (IRPClass) subcluster.findNestedElementRecursive(((Component) component).getName(),
					GlobalVariables.COMPONENT_METACLASS);
			if (swcomponent != null && (pluginMap.containsKey(swcomponent.getName()))) {
					EList<Plugin> pluginlist = pluginMap.get(swcomponent.getName());
					if (pluginlist != null) {
						for (Plugin pluginname : pluginlist) {
							plugin = (IRPClass) checkifElementExists(swcomponent, ((Plugin) pluginname).getName(),
									GlobalVariables.PLUGIN_METACLASS);
							if (plugin == null) {
								processPlugin(pluginname);
							}

							// Required Plugin Interfaces
							for (RequiredInterface ri : pluginname.getRequiredInterfaces()) {
								portPresent = false;
								existingConnectors = checkforConnectors(swcomponent, plugin.getName());
								for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
									ProvidedElement conj_port = ri.getInterface();
									AbstractComponent compname = ri.getComponent();
										List<SubComponent> subcomps = compname.getSubcomponents();
										for (Entry<String, HashMap<String, String>> entry : existingConnectors
												.entrySet()) {
											HashMap<String, String> connectorMap = entry.getValue();
											String connector_entry = connectorMap
													.get(compname.getName() + "__" + conj_port.getName());
											if (connector_entry != null) {
												portPresent = true;
												break;
											} else {
												portPresent = false;
												break;
											}
										}
										if (!portPresent) {
											for (Entry<String, HashMap<String, String>> entrySet : existingConnectors
													.entrySet()) {
												HashMap<String, String> connector = entrySet.getValue();
												for (SubComponent subCompname : subcomps) {
													String connector_entry = connector
															.get(subCompname.getName() + "__" + conj_port.getName());
													if (connector_entry != null) {
														portPresent = true;
														break;
													} else {
														portPresent = false;
													}
												}
												if (true) {
													break;
												}
											}

											if (!portPresent) {
												processRequiredInterfaces(plugin, ri, rie);
											}
										}

								}
							}
						}
					}
			}
			// Delegate interfaces
			HashMap<AccessibleInterface, HashMap<AbstractComponent, ProvidedInterface>> delegateInterface = new HashMap<>();
			HashMap<AbstractComponent, ProvidedInterface> delegatComp_ProvInt= new HashMap<>();
			for (AccessibleInterface ai : component.getProvidedInterfaces()) {
				if (ai instanceof DelegateInterface) {
				for (DelegateEntity rie : ((DelegateInterface) ai).getDelegateEntities()) {
					AbstractComponent dcomp = rie.getDelegateComponent();
					ProvidedInterface pintf = rie.getDelegateInterface();
					delegatComp_ProvInt.put(dcomp, pintf);
				}
				delegateInterface.put(ai, delegatComp_ProvInt);
			  }
			}
			for (Entry<AccessibleInterface, HashMap<AbstractComponent, ProvidedInterface>> ai : delegateInterface.entrySet()) {
				
					if (ai.getKey() instanceof DelegateInterface) {
						HashMap<AbstractComponent, ProvidedInterface> delegateIntAndProvidedIntf = ai.getValue();
						match = false;
						intfblck = (IRPClass) swcomponent.findNestedElement("d_" + ai.getKey().getName(),
								GlobalVariables.DELEGATEINTERFACE_METACLASS);
						if (intfblck == null) {
							ProcessProvidedElements.processCompProvidedInterfaceBlockAndPort(pkg, ai.getKey(),
									swcomponent, elementPresent, component );
							intfblck = (IRPClass) swcomponent.findNestedElement("d_" + ai.getKey().getName(),
									GlobalVariables.DELEGATEINTERFACE_METACLASS);
						}
						IRPCollection myGeneralizations = intfblck.getGeneralizations();
						for (Object generalizationelement : myGeneralizations.toList()) {
							IRPModelElement generalizationmodelelement = (IRPModelElement) generalizationelement;
							IRPGeneralization general = (IRPGeneralization) generalizationmodelelement;
							general.changeTo(GlobalVariables.REALIZATION_TAG);
						}
						for (Entry<AbstractComponent, ProvidedInterface> rie : delegateIntAndProvidedIntf.entrySet()) {
							AbstractComponent dcomp = rie.getKey();
							ProvidedInterface pintf = rie.getValue();
							match = myGeneralizations.toList().stream().anyMatch( generalizationelement -> ((IRPGeneralization) generalizationelement).getBaseClass().getName().equalsIgnoreCase(pintf.getName()));
							if (!match) {
								IRPModelElement baseClass = findSourceandDestination(dcomp.getName());
								IRPPort reqPort = (IRPPort) swcomponent.findNestedElement(ai.getKey().getName(),
										GlobalVariables.PROXY_PORT_METACLASS);
								IRPLink reqConnector = (IRPLink) swcomponent.findNestedElement(
										dcomp.getName() + "__" + ai.getKey().getName(), GlobalVariables.LINK_TAG);
								if (reqPort == null) {
									reqPort = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
											ai.getKey().getName());
								}
								reqPort.setIsReversed(1);
								if (reqConnector == null) {
									IRPModelElement fpartowner = findSourceandDestination(dcomp.getName());
									if (fpartowner == null) {
										fpartowner = pkg.findNestedElementRecursive(dcomp.getName(),
												GlobalVariables.CLASS_METACLASS);
									}

									IRPInstance fpart = findPart(fpartowner);
									IRPPort fromport = (IRPPort) fpartowner.findNestedElementRecursive(pintf.getName(),
											GlobalVariables.PROXY_PORT_METACLASS);
									IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
									IRPInstance tpart = findPart(tpartowner);
									IRPPort toport = (IRPPort) swcomponent.findNestedElementRecursive(ai.getKey().getName(),
											GlobalVariables.PROXY_PORT_METACLASS);
									if (reqConnector == null) {
										addConnector(swcomponent, fpart, tpart, fromport, toport,
												dcomp.getName(), ai.getKey().getName());
									}
								}

								if (baseClass != null) {
									IRPModelElement mo = baseClass.findNestedElementRecursive(pintf.getName(),
											GlobalVariables.INTERFACE_BLOCK_METACLASS);

									if (mo != null && intfblck != null) {
										IRPGeneralization myGen = intfblck.findGeneralization(mo.getName());
										if (myGen != null) {
											IRPClassifier basecls = myGen.getBaseClass();
											if (!basecls.equals(dcomp)) {
												intfblck.addGeneralization((IRPClassifier) mo);
												IRPGeneralization myNewGen = intfblck.findGeneralization(mo.getName());
												if (!basecls.getName().equals(baseClass.getName())) {
													myNewGen.changeTo(GlobalVariables.REALIZATION_TAG);
												}
											}
										} else {
											intfblck.addGeneralization((IRPClassifier) mo);
											myGen = intfblck.findGeneralization(mo.getName());
											myGen.changeTo(GlobalVariables.REALIZATION_TAG);
										}

									}
								} else {
									logger.info("Delegate interface cannot be resolved (REALIZATION)");
								}
								funcClass = (IRPClass) getPortContractforDelegate(pkg, intfblck);
								if (funcClass != null) {
									reqPort.setContract(funcClass);
								}
							}
						}
					}
				
			}
			// Required Interfaces

			existingConnectors = checkforConnectors(subcluster, component.getName());

			portPresent = false;
			HashMap<RequiredInterface, HashMap<AbstractComponent, ProvidedElement>> reqInterface = new HashMap<>();
			HashMap<AbstractComponent, ProvidedElement> reqInterfaceAndComp = null;
			AbstractComponent absComp = null;
			ProvidedElement provEle = null;
			for (RequiredInterface ri : component.getRequiredInterfaces()) {
				reqInterfaceAndComp = new HashMap<>();
				if(ri.getComponent() instanceof AbstractComponent)
					absComp = ri.getComponent();
				if(ri.getInterface() instanceof ProvidedElement)
					provEle = ri.getInterface();
				if(absComp != null && provEle != null) {
				reqInterfaceAndComp.put(absComp, provEle);
				reqInterface.put(ri, reqInterfaceAndComp);
				}
			}
			
				existingConnectors.clear();
				for (Entry<RequiredInterface, HashMap<AbstractComponent, ProvidedElement>> ri : reqInterface.entrySet()) {
					portPresent = false;
					if(existingConnectors.isEmpty())
					      existingConnectors = checkforConnectors(subcluster, component.getName());
					HashMap<AbstractComponent, ProvidedElement> reqInterfaceAndCompName = ri.getValue();
					ProvidedElement conj_port = null;
					AbstractComponent compname = null;
					for(Entry<AbstractComponent, ProvidedElement> entries : reqInterfaceAndCompName.entrySet()) {
					if(entries.getValue() instanceof ProvidedElement)
						conj_port = (ProvidedElement) entries.getValue();					
					if(entries.getKey() instanceof AbstractComponent)
						compname = (AbstractComponent) entries.getKey();
					}
					if (conj_port instanceof ProvidedInterface  || conj_port instanceof DelegateInterface) {
						IRPPort reqportForComp = (IRPPort) swcomponent.findNestedElement(compname.getName() + "__" + conj_port.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if(reqportForComp == null) {
						reqportForComp = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
								compname.getName() + "__" + conj_port.getName());
						IRPClass compClass = prj.findClass(compname.getName());
						IRPClass funcClass_ = getRequiredPortContract(compClass, conj_port.getName());
						if (funcClass_ != null) {
							reqportForComp.setContract(funcClass_);
						}
					  }
					}
					for (RequiredInterfaceEntity rie : ri.getKey().getRequiredEntities()) {

						List<SubComponent> subcomps = compname.getSubcomponents();
						List<PluginTemplate> templates = compname.getTemplates();
						for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
							HashMap<String, String> connector = entry.getValue();
							String connectorMap_entry = connector.get(compname.getName() + "__" + conj_port.getName());
							if (connectorMap_entry != null) {
								portPresent = true;
								break;
							} else {
								portPresent = false;
								break;
							}
						}
						if (!portPresent) {
							for (Entry<String, HashMap<String, String>> entryset : existingConnectors.entrySet()) {
								HashMap<String, String> connector = entryset.getValue();
								for (SubComponent subcompName : subcomps) {
									String connector_entry = connector
											.get(subcompName.getName() + "__" + conj_port.getName());
									if (connector_entry != null) {
										portPresent = true;
										break;
									} else {
										portPresent = false;
										break;
									}
								}
								for (PluginTemplate subcompName : templates) {
									Collection<String> value = connector.values();
									for (String name : value) {
										if (name.contains(subcompName.getName())) {
											portPresent = true;
											break;
										} else {
											portPresent = false;
											break;
										}
									}
								}
							}
						
								if ((!portPresent) && (rie instanceof RequiredFunction || rie instanceof RequiredConstant
										|| rie instanceof RequiredPort || rie instanceof RequiredVariable)) {
									processRequiredInterfaces(swcomponent, ri.getKey(), rie);
							}
						}
						
					}
				}
			
		} catch (Exception e) {
			logger.info("Exception while processing Component OR its  Required Interface Element: " + component.getName()+"\n" + e.toString());
		}

	}

	private HashMap<String, HashMap<String, String>> checkforConnectors(IRPModelElement pkg, String component) {

		try {
				IRPModelElement component_in_pkg = pkg.findNestedElementRecursive(component,
						GlobalVariables.CLASS_METACLASS);
				IRPCollection components = component_in_pkg.getNestedElementsByMetaClass("Link", 0);
				
				@SuppressWarnings("unchecked")
				HashMap<String, String> connectorsinModel =  (HashMap<String, String>) components.toList().stream().filter(obj ->((IRPModelElement)obj).getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.CONNECTOR_METACLASS)).collect(Collectors.toMap(map -> ((IRPModelElement)map).getName(), map -> ((IRPModelElement)map).getName())); 
				
				existingConnectors.put(component, connectorsinModel);
		} catch (Exception e) {
			logger.info("Exception while checking existing connectors\n" + e.toString());
			

		}
		return existingConnectors;
	}

	private void processRequiredInterfaces(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		
		AbstractComponent abscomp = ri.getComponent();
		ProvidedElement provelem = ri.getInterface();

		if (rie instanceof RequiredConstant) {
			processRequiredConstant(swcomponent, ri, rie);
		} else if (rie instanceof RequiredPort) {
			processRequiredPort(swcomponent, ri, rie);
		} else if (rie instanceof RequiredFunction) {
			processRequiredFunction(swcomponent, ri, rie);
		} else if (rie instanceof RequiredVariable) {
			processRequiredVariable(swcomponent, ri, rie);
		} else if (rie instanceof CodegComponent) {
			logger.info("A codeg Element ");
		}
		processPlugin(abscomp, provelem);
	}

	private void processRequiredSubSubInterfaces(IRPUnit swcomponent, IRPModelElement swswcomp, RequiredInterface ri,
			Object rie) {
		
		if (rie instanceof RequiredConstant) {
			processSubSubRequiredConstant(swcomponent, swswcomp, ri, rie);
		} else if (rie instanceof RequiredPort) {
			processSubSubRequiredPort(swcomponent, swswcomp, ri, rie);
		} else if (rie instanceof RequiredFunction) {
			processSubSubRequiredFunction(swcomponent, swswcomp, ri, rie);
		} else if (rie instanceof RequiredVariable) {
			processSubSubRequiredVariable(swcomponent, swswcomp, ri, rie);
		} else if (rie instanceof CodegComponent) {
			logger.info("A codeg Element ");
		}
		
	}

	private void processSubSubRequiredConstant(IRPUnit swcomponent, IRPModelElement swswcomp, RequiredInterface ri,
			Object rie) {
		

		try {
			flag = false;
			boolean isflag = flag;
			providedComponent = null;
			isPresent=false;
			RequiredConstant rc = (RequiredConstant) rie;
			String requiredconstant = rc.getConstant().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provIntfelem = ri.getInterface();
			AbstractComponent dcomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String providedElement = provIntfelem.getName();
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provIntfelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provIntfelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provIntfelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						String dCompName = dcomp.getName();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						
						Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
						          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
						
						for (Entry<Object, Object> entry : filtermap
								.entrySet()) {
							if (!isflag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
								if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) 
									{
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredconstant)) 
										{
											providedComponent = ((AbstractComponent)entry.getKey());
											provIntfelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provIntfelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												isflag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (isflag) {
											break;}

									}
								}
							}
							if (isflag || isPresent) {
								break;
								}
						}

					}
				}
				
				Map<Object, Object> providedFilterMap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
				
				for (Entry<Object, Object> entry : providedFilterMap
						.entrySet()) {
					if (!isflag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
					
								if(interfacename.get(provIntfelem.getName()) != null) {
								HashMap<String, String> intfelems = interfacename.get(provIntfelem.getName());
								if (intfelems.containsValue(requiredconstant)) {

									providedComponent = (AbstractComponent) entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provIntfelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) 
									{
										isflag = true;} 
									else 
									{
										isPresent = true;
										break;
									}
								}
							}
							if (isflag) {
								break;}
					
					}
					if (isflag || isPresent) {
						break;
					}
				}
				
				if (!isflag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!isflag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(provIntfelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provIntfelem.getName());
									if (intfelems.containsValue(requiredconstant)) {

									
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provIntfelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										IRPLink reqConnector = reqconnector;
										if (reqConnector == null) 
										{
											isflag = true;
										} else {
											isPresent = true;
											break;
										}
										}
									}
								if (isflag)
								{
									break;
									}
						
						}
						if (isflag || isPresent) {
							break;
						}
					}
				}
				
				
				if (!isflag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!isflag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(provIntfelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provIntfelem.getName());
									if (intfelems.containsValue(requiredconstant)) {
										
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												providedComponent.getName() + "__" + provIntfelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											isflag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (isflag) {
									break;
								}
						
						}
						if (isflag || isPresent) {
							break;
						}
					}
				}
					if ((isflag) && (providedComponent != null)) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + provIntfelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubSubComponents(provIntfelem, dcomp, providedComponent, maincomp,
									rc.getConstant().getName(), swswcomp);
						}
					}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provIntfelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provIntfelem, pluginComponent, maincomp);
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provIntfelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provIntfelem, pluginTemplateComponent, maincomp);
				}
			}
		} catch (Exception e) {
			
			logger.info("Error in creating connectors");
		}

	}

	private void processSubSubRequiredFunction(IRPUnit swcomponent, IRPModelElement swswcomp, RequiredInterface ri,
			Object rie) {
		

		try {
			flag = false;
			Boolean isFlag = flag;
			providedComponent = null;
			isPresent=false;
			RequiredFunction rc = (RequiredFunction) rie;
			String reqfunc = rc.getFunction().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provele = ri.getInterface();
			AbstractComponent dcomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			
			String providedElement = provele.getName();
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provele.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provele instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provele;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						String dCompName = dcomp.getName();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						
						Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
						          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
						
						for (Entry<Object, Object> entry : filtermap
								.entrySet()) {
							if (!isFlag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfaceName = (HashMap<String, HashMap<String, String>>) entry.getValue();
								if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfaceName.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(reqfunc)) {
											providedComponent = ((AbstractComponent)entry.getKey());
											provele = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provele.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												isFlag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (isFlag) {
											break;
										}
									}
								}
							}
							if (isFlag || isPresent) {
								break;
							}
						}

					}
					}				
				Map<Object, Object> providedfiltermap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
				
				for (Entry<Object, Object> entry : providedfiltermap
						.entrySet()) {
					if (!isFlag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
					
								if(interfacename.get(provele.getName()) != null) {
								HashMap<String, String> intfelems = interfacename.get(provele.getName());
								if (intfelems.containsValue(reqfunc)) {

								
									providedComponent = (AbstractComponent) entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provele.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										isFlag = true;
									} else {
										isPresent = true;
										break;
									}
								}
							}
							if (isFlag || isPresent) {
								break;
							}
					
					}
					if (isFlag || isPresent) {
						break;
					}
				}
				
				if (!isFlag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!isFlag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(provele.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provele.getName());
									if (intfelems.containsValue(reqfunc)) {

										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provele.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											isFlag = true;
										} else 
										{
											isPresent = true;
											break;
										}
									}
								}
								if (isFlag) {
									break;
								}
						
						}
						if (isFlag || isPresent)
						{
							break;
						}
					}
				}
				if (!isFlag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!isFlag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(provele.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provele.getName());
									if (intfelems.containsValue(reqfunc)) {
										
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												providedComponent.getName() + "__" + provele.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											isFlag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (isFlag || isPresent) {
									break;
								}
						
						}
						if (isFlag || isPresent) {
							break;
						}
					}
				}
				if ((isFlag) && (providedComponent != null)) {
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provele.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubSubComponents(provele, dcomp, providedComponent, maincomp,
									rc.getFunction().getName(), swswcomp);
						}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provele.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provele, pluginComponent, maincomp);
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provele.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provele, pluginTemplateComponent, maincomp);
				}
			}
		} catch (Exception e) {
		
			logger.info("Error in creating connectors");
		}

	}

	private void processSubSubRequiredVariable(IRPUnit swcomponent, IRPModelElement swswcomp, RequiredInterface ri,
			Object rie) {
		
		try {
			flag = false;
			Boolean isflag =flag;
		isPresent=false;
		Boolean ispresent =isPresent;
			providedComponent = null;
			RequiredVariable rc = (RequiredVariable) rie;
			String reqvar = rc.getVariable().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelem = ri.getInterface();
			AbstractComponent dComp = null;
			Plugin plugincomp = null;
			PluginTemplate pluginTemplateComponent = null;
			String providedElement = provelem.getName();
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dComp = die.getDelegateComponent();
						String dCompName = dComp.getName();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						
						Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
						          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
						
						for (Entry<Object, Object> entry : filtermap
								.entrySet()) {
							if (!isflag && !ispresent) {
								HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
								if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(dComp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(reqvar)) {
											providedComponent = ((AbstractComponent)entry.getKey());
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dComp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												isflag = true;
											} else {
												ispresent = true;
												break;
											}
										}
										if (isflag) {
											break;
										}

									}
								}}
							if (isflag || ispresent) {
								break;
							}
						}

					}
				}
				
				Map<Object, Object> proviFilterMap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
				
				for (Entry<Object, Object> entry : proviFilterMap
						.entrySet()) {
					if (!isflag && !ispresent) {
						HashMap<String, HashMap<String, String>> interfaceName = (HashMap<String, HashMap<String, String>>) entry.getValue();
					
								if(interfaceName.get(provelem.getName()) != null) {
								HashMap<String, String> intfelems = interfaceName.get(provelem.getName());
								if (intfelems.containsValue(reqvar)) {

									
									providedComponent = (AbstractComponent) entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									IRPLink reqConnector = reqconnector;
									if (reqConnector == null) {
										
										isflag = true;
									} else {
										ispresent = true;
										break;
									}
								}
							}
							if (isflag || ispresent) {
								break;
							}
					
					}
					if (isflag || ispresent) {
						break;
					}
				}
				if (!isflag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!isflag && !ispresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(provelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provelem.getName());
									if (intfelems.containsValue(reqvar)) {

										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											isflag = true;
										} else {
											ispresent = true;
											break;
										}
									}
								}
								if (isflag || ispresent) {
									break;
								}
						
						}
						if (isflag || ispresent) {
							break;
						}
					}
				}
				if (!isflag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!isflag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(provelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provelem.getName());
									if (intfelems.containsValue(reqvar)) {
									
										plugincomp = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											isflag = true;
										} else {
											ispresent = true;
											break;
										}
									}
								}
								if (isflag || ispresent) {
									break;
								}
						
						}
						if (isflag || ispresent) {
							break;
						}
					}
				}
				if ((isflag) && (providedComponent != null)) {
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubSubComponents(provelem, dComp, providedComponent, maincomp,
									rc.getVariable().getName(), swswcomp);
						}
				}
			}
			if (plugincomp != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						plugincomp.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provelem, plugincomp, maincomp);
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provelem, pluginTemplateComponent, maincomp);
				}
			}
			flag = isflag;
			isPresent = ispresent;
		} catch (Exception e) {
			
			logger.info("Error in creating connectors");
		}

	}

	private void processSubSubRequiredPort(IRPUnit swcomponent, IRPModelElement swswcomp, RequiredInterface ri,
			Object rie) {
		

		try {
			flag = false;
			isPresent=false;
			Boolean present = isPresent;
			providedComponent = null;
			RequiredPort rc = (RequiredPort) rie;
			String reqPort = rc.getPort().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provElem = ri.getInterface();
			AbstractComponent dcompname = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			String providedElement = provElem.getName();
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provElem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provElem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provElem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcompname = die.getDelegateComponent();
						String dCompName = dcompname.getName();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						
						Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
						          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
						
						for (Entry<Object, Object> entry : filtermap
								.entrySet()) {
							if (!flag && !present) {
								HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
								if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(dcompname.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(reqPort)) {
											providedComponent = ((AbstractComponent)entry.getKey());
											provElem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcompname.getName() + "__" + provElem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												present = true;
												break;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag) {
								break;
							}
						}

					}
				}
				
				Map<Object, Object> providedFilterMap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
				
				for (Entry<Object, Object> entry : providedFilterMap
						.entrySet()) {
					if (!flag && !present) {
						HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
					
								if(interfacename.get(provElem.getName()) != null) {
								HashMap<String, String> intfelems = interfacename.get(provElem.getName());
								if (intfelems.containsValue(reqPort)) {

									providedComponent = (AbstractComponent) entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provElem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										present = true;
										break;
									}
								}
							}
							if (flag) {
								break;
							}
					
					}
					if (flag) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !present) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(provElem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provElem.getName());
									if (intfelems.containsValue(reqPort)) {

										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provElem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										IRPLink connector = reqconnector;
										if (connector == null) {
											flag = true;
										} else {
											present = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
						
						}
						if (flag) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
					
									if(interfacename.get(provElem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provElem.getName());
									if (intfelems.containsValue(reqPort)) {
										
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												providedComponent.getName() + "__" + provElem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											present = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
						
						}
						if (flag) {
							break;
						}
					}
				}
				if ((flag) && (providedComponent != null)) {
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provElem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubSubComponents(provElem, dcompname, providedComponent, maincomp,
									rc.getPort().getName(), swswcomp);
						}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provElem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provElem, pluginComponent, maincomp);
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provElem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provElem, pluginTemplateComponent, maincomp);
				}
			}
			
			isPresent = present;
			
		} catch (Exception e) {
		
			logger.info("Error in creating connectors");
		}

	}

	private void processRequiredSubInterfaces(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
	
		if (rie instanceof RequiredConstant) {
			processSubRequiredConstant(swcomponent, ri, rie);
		} else if (rie instanceof RequiredPort) {
			processSubRequiredPort(swcomponent, ri, rie);
		} else if (rie instanceof RequiredFunction) {
			processSubRequiredFunction(swcomponent, ri, rie);
		} else if (rie instanceof RequiredVariable) {
			processSubRequiredVariable(swcomponent, ri, rie);
		} else if (rie instanceof CodegComponent) {
			logger.info("A codeg Element ");
		}
		
	}

	private void processSubRequiredVariable(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		
		try {
			flag = false;
			match = false;
			isPresent=false;
			providedComponent = null;
			RequiredVariable rc = (RequiredVariable) rie;
			String reqvar = rc.getVariable().getName();
			AbstractComponent deleComp = null;
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelem = ri.getInterface();
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			String providedElement = provelem.getName();
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						deleComp = die.getDelegateComponent();
						String dCompName = deleComp.getName();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						
						Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
						          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
						
						for (Entry<Object, Object> entry : filtermap
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfaceName = (HashMap<String, HashMap<String, String>>) entry.getValue();
								if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(deleComp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfaceName.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(reqvar)) {
											providedComponent = ((AbstractComponent)entry.getKey());
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													deleComp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}

					}
				}
				
				Map<Object, Object> providedIntfFilterMap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
				
				for (Entry<Object, Object> entry : providedIntfFilterMap
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> intfname = (HashMap<String, HashMap<String, String>>) entry.getValue();
					
								if(intfname.get(provelem.getName()) != null) {
								HashMap<String, String> intfElems = intfname.get(provelem.getName());
								if (intfElems.containsValue(reqvar)) {
									
									providedComponent = (AbstractComponent) entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										isPresent = true;
									}
								}
							}
							if (flag) {
								break;
							}
					
					}
					if (flag || isPresent) {
						break;
					}
				}

				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
					
									if(interfacename.get(provelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provelem.getName());
									if (intfelems.containsValue(reqvar)) {

										
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
										}
									}
								}
								if (flag) {
									break;
								}
						
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
					
									if(interfacename.get(provelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provelem.getName());
									if (intfelems.containsValue(reqvar)) {

										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
										}
									}
								}
								if (flag) {
									break;
								}
						
						}
						if (flag || isPresent) {
							break;
						}
					}
				}

				if (providedComponent != null) {
					reqconnector = (IRPLink) swcomponent.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
					if (reqconnector == null) {

						createportsforSubComponents(provelem, deleComp, providedComponent, maincomp,
								rc.getVariable().getName());
					}
				}

			}
			if (pluginComponent != null)
			{
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provelem, pluginComponent, maincomp);
				}
			}
			if (pluginTemplateComponent != null)
			{
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provelem, pluginTemplateComponent, maincomp);
				}
			}

		} catch (Exception e) {
			
			logger.info("Error in creating connectors");
		}
	}

	private void processSubComponentDelegations(AbstractComponent providedComponent, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent) {
		
		reqconnector = (IRPLink) subcomponent.findNestedElement(providedComponent.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		IRPModelElement frompartowner = null;
		IRPInstance fpart = null;
		IRPInstance mainpart = null;
		IRPPort mainport = null;
		frompartowner = findSourceandDestination(subcomponent.getName());
		if (frompartowner != null) {
			fpart = findPart(frompartowner);
		}
		if (fpart == null) {
			frompartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (frompartowner != null) {
				fpart = findPart(frompartowner);
			}
		}
		IRPPort fromport = (IRPPort) frompartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance topart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null) {
			reqconnector = addConnector(subcomponent, fpart, topart, fromport, toport, providedComponent.getName(),
					provelem.getName());
		}
		if (providedComponent instanceof SubComponent) {

			String maincomponent = null;
			for (Entry<String, List<String>> entry : subcomponenthashmap.entrySet()) {
				List<String> subcomponentname = entry.getValue();
				if (subcomponentname.contains(providedComponent.getName())) {
					maincomponent = entry.getKey();
					break;
				}
			}
			if (!maincomp.getName().equalsIgnoreCase(maincomponent)) {
				if (maincomponent.equalsIgnoreCase(swcomponent.getName())) {
					IRPUnit delegateElement = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegateElement);
					IRPPort delegateport = (IRPPort) delegateElement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateconnector = (IRPLink) delegateElement.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateconnector == null) {
						try {
							addConnector(delegateElement, topart, delegatepart, toport, delegateport,
								providedComponent.getName(), provelem.getName());
					}catch(Exception e) {
						logger.info(e.getMessage());
					}
					}
				} else {
					IRPModelElement mainelement = findSourceandDestination(maincomponent);
					mainpart = findPart(mainelement);
					mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					if (mainport == null) {
						mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
								provelem.getName());
						funcClass = getRequiredPortContract(pkg, provelem.getName());
						if (funcClass != null) {
							mainport.setContract(funcClass);
						}

					}
					IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (mainConnector == null) {
						try {
						addConnector(swcomponent, topart, mainpart, toport, mainport,
								providedComponent.getName(), provelem.getName());
						}catch(Exception e) {
							logger.info(e.getMessage());
						}
					
					}
					IRPUnit delegateEle = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegateEle);
					IRPPort delegateport = (IRPPort) delegateEle.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateconnector = (IRPLink) delegateEle.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateconnector == null) {
						try {
						addConnector(delegateEle, mainpart, delegatepart, mainport,
								delegateport, providedComponent.getName(), provelem.getName());
						}catch(Exception e) {
							logger.info(e.getMessage()	);
						}
						}
				}
			} else {
				IRPModelElement mainElement = findSourceandDestination(maincomp.getName());
				mainpart = findPart(mainElement);
				mainport = (IRPPort) mainElement.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (mainport == null) {
					mainport = (IRPPort) mainElement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						mainport.setContract(funcClass);
					}

				}
				IRPLink mainconnector = (IRPLink) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
				if (mainconnector == null) {
					try {
					addConnector(swcomponent, topart, mainpart, toport, mainport,
							providedComponent.getName(), provelem.getName());
					}catch(Exception e) {
						logger.info(e.getMessage());
					}
				}
			}
		}

		if (!maincomp.getName().equalsIgnoreCase(providedComponent.getName())) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				try {
				addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						providedComponent.getName(), provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}

	}

	private void processSubRequiredFunction(IRPUnit subcomponent, RequiredInterface ri, Object rie) {
		try {
			flag = false;
			isPresent=false;
			providedComponent = null;
			RequiredFunction rc = (RequiredFunction) rie;
			String requiredfunc = rc.getFunction().getName();
			AbstractComponent maincomp = ri.getComponent();
			AbstractComponent dcomp = null;
			ProvidedElement provelem = ri.getInterface();
			Plugin pluginComp = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			String providedElement = provelem.getName();
			reqconnector = (IRPLink) subcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						String dCompName = dcomp.getName();
						Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
				          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));  
						
						for (Entry<Object, Object> entry : filtermap
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
								if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredfunc)) {
											providedComponent = ((AbstractComponent)entry.getKey());
											provelem = di;
											reqconnector = (IRPLink) subcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
											}
										}
										if (flag || isPresent) {
											break;
										}

									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}
						if (flag || isPresent) {
							break;
						}
					}

				}
				
				Map<Object, Object> providedFilterMap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
				
				for (Entry<Object, Object> entry : providedFilterMap
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();

							if(interfacename.get(provelem.getName()) != null) {
						
								HashMap<String, String> intfelems = interfacename.get(provelem.getName());
								if (intfelems.containsValue(requiredfunc)) {

									providedComponent = (AbstractComponent) entry.getKey();
									reqconnector = (IRPLink) subcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;

									}
								}
							}
							if (flag) {
								break;
							}
					}
					if (flag || isPresent) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entryset : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entryset.getValue();
					
									if(interfacename.get(provelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provelem.getName());
									if (intfelems.containsValue(requiredfunc)) {

										pluginTemplateComponent = entryset.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;

										}
									}
								}
								if (flag) {
									break;
								}
					
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
					
								if(interfacename.get(provelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provelem.getName());
									if (intfelems.containsValue(requiredfunc)) {

										pluginComp = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												pluginComp.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;

										}
									}
								}
								if (flag) {
									break;
								}
						
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if ((flag || isPresent) && (providedComponent != null)) {
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubComponents(provelem, dcomp, providedComponent, maincomp,
									rc.getFunction().getName());
						}
				}
			}
			if (pluginComp != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginComp.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provelem, pluginComp, maincomp);
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provelem, pluginTemplateComponent, maincomp);
				}
			}
		} catch (

		Exception e) {
			
			logger.info("Error in creating connectors");
		}
	}

	private void processSubRequiredPort(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		
		try {
			flag = false;
			isPresent=false;
			providedComponent = null;
			RequiredPort rc = (RequiredPort) rie;
			String requiredport = rc.getPort().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provEle = ri.getInterface();
			AbstractComponent dcomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			String providedElement = provEle.getName();
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provEle.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provEle instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provEle;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						String dCompName = dcomp.getName();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						
						Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
						          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
						
						for (Entry<Object, Object> entry : filtermap
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> intfName = (HashMap<String, HashMap<String, String>>) entry.getValue();
								if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : intfName.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredport)) {
											providedComponent = ((AbstractComponent)entry.getKey());
											provEle = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provEle.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if(reqconnector==null) {
												flag = true;
												}
												else {
												isPresent=true;
												break;
												}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag) {
								break;
							}
						}

					}
				}
				
				Map<Object, Object> providedFilterMap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
				
				for (Entry<Object, Object> entry : providedFilterMap
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
				
								if(interfacename.get(provEle.getName()) != null) {
								HashMap<String, String> intfelems = interfacename.get(provEle.getName());
								if (intfelems.containsValue(requiredport)) {

								
									providedComponent = (AbstractComponent) entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provEle.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if(reqconnector==null) {
										flag = true;
										}
										else {
										isPresent=true;
										break;
										}
								}
							}
							if (flag) {
								break;
							}
						
					}
					if (flag) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(provEle.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provEle.getName());
									if (intfelems.containsValue(requiredport)) {

										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												providedComponent.getName() + "__" + provEle.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										IRPLink requiredconnector = reqconnector;
										if(requiredconnector ==null) {
											flag = true;
											}
											else {
											isPresent=true;
											break;
											}
									}
								}
								if (flag) {
									break;
								}
							
						}
						if (flag) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
					
									if(interfacename.get(provEle.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provEle.getName());
									if (intfelems.containsValue(requiredport)) {

										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												providedComponent.getName() + "__" + provEle.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										IRPLink reqConnector = reqconnector;
										if(reqConnector == null) {
											flag = true;
											}
											else {
											isPresent=true;
											break;
											}
									}
								}
								if (flag) {
									break;
								}
						}
						if (flag) {
							break;
						}
					}
				}
				if (flag && (providedComponent != null)) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + provEle.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubComponents(provEle, dcomp, providedComponent, maincomp,
									rc.getPort().getName());
						}
				}

				if (pluginComponent != null) {
					reqconnector = (IRPLink) swcomponent.findNestedElement(
							pluginComponent.getName() + "__" + provEle.getName(), GlobalVariables.LINK_TAG);
					if (reqconnector == null) {
						createportsforSubPlugins(provEle, pluginComponent, maincomp);
					}
				}
				if (pluginTemplateComponent != null) {
					reqconnector = (IRPLink) swcomponent.findNestedElement(
							pluginTemplateComponent.getName() + "__" + provEle.getName(), GlobalVariables.LINK_TAG);
					if (reqconnector == null) {
						createportsforSubTemplates(provEle, pluginTemplateComponent, maincomp);
					}
				}
			}
		} catch (Exception e) {
		
			logger.info("Error in creating connectors");
		}

	}

	private void processSubRequiredConstant(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
	

		try {
			flag = false;
			isPresent=false;
			providedComponent = null;
			RequiredConstant rc = (RequiredConstant) rie;
			String requiredconstant = rc.getConstant().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement providedEle = ri.getInterface();
			AbstractComponent delegatecomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			String providedElement = providedEle.getName();
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + providedEle.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (providedEle instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) providedEle;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						delegatecomp = die.getDelegateComponent();
						String dCompName = delegatecomp.getName();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						
						Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
						          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
						
						for (Entry<Object, Object> entry : filtermap
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
								if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(delegatecomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredconstant)) {
											providedComponent = ((AbstractComponent)entry.getKey());
											providedEle = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													delegatecomp.getName() + "__" + providedEle.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if(reqconnector==null) {
												flag = true;
												}
												else {
												isPresent=true;
												break;
												}
										}
										if (flag) {
											break;}
									}
								}
							}
							if (flag || isPresent) {
								break;}
						}

					}
				}
				
				Map<Object, Object> providedIntfFilterMap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
				
				for (Entry<Object, Object> entry : providedIntfFilterMap
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> intfname = (HashMap<String, HashMap<String, String>>) entry.getValue();
					
								if(intfname.get(providedEle.getName()) != null) {
								HashMap<String, String> intfelems = intfname.get(providedEle.getName());
								if (intfelems.containsValue(requiredconstant)) {

									providedComponent = (AbstractComponent) entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + providedEle.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if(reqconnector==null) {
										flag = true;
										}
										else {
										isPresent=true;
										break;
										}
								}
							}
							if (flag || isPresent) {
								break;
							}
					
					}
					if (flag) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(providedEle.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(providedEle.getName());
									if (intfelems.containsValue(requiredconstant)) {

								
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + providedEle.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if(reqconnector==null) {
											flag = true;
											}
											else {
											isPresent=true;
											break;
											}
									}
								}
								if (flag || isPresent) {
									break;
								}
						
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(providedEle.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(providedEle.getName());
									if (intfelems.containsValue(requiredconstant)) {
									
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												providedComponent.getName() + "__" + providedEle.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if(reqconnector==null) {
											flag = true;
											}
											else {
											isPresent=true;
											break;
											}
									}
								}
								if (flag) {
									break;
								}
						
						}
						if (flag) {
							break;
						}
					}
				}
				if (flag && (providedComponent != null)) {					
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + providedEle.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubComponents(providedEle, delegatecomp, providedComponent, maincomp,
									rc.getConstant().getName());
						}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + providedEle.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(providedEle, pluginComponent, maincomp);
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + providedEle.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(providedEle, pluginTemplateComponent, maincomp);
				}
			}
		} catch (Exception e) {
			
			logger.info("Error in creating connectors");
		}

	}

	private void createportsforSubTemplates(ProvidedElement provelem, PluginTemplate providedComponent,
			AbstractComponent maincomp) {
		
		if (provelem instanceof ProvidedInterface) {
			reqport = (IRPPort) subcomponent.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) subcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processSubComponentDelegations(providedComponent, maincomp, provelem, subcomponent);
				} else {
					processTopLevelDelegations(providedComponent, maincomp, provelem, subcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}
		} else if (provelem instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provelem).getDelegateEntities()) {
				AbstractComponent dcomp = die.getDelegateComponent();
				ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
				reqport = (IRPPort) subcomponent.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
						GlobalVariables.LINK_TAG);
				if (di.getName().equalsIgnoreCase(provelem.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									provelem.getName());
							funcClass = getRequiredPortContract(pkg, provelem.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}

						reqport.setIsReversed(1);
						IRPModelElement fpartowner = null;
						IRPInstance frompart = null;
						fpartowner = findSourceandDestination(providedComponent.getName());
						if (fpartowner != null) {
							frompart = findPart(fpartowner);
						}
						if (frompart == null) {
							fpartowner = pkg.findNestedElementRecursive(provelem.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fpartowner != null) {
								frompart = findPart(fpartowner);
							}
						}
						if(fpartowner!=null) {
						IRPPort fromport = (IRPPort) fpartowner.findNestedElementRecursive(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(subcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) subcomponent.findNestedElementRecursive(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if (reqconnector == null) {
							try {
							reqconnector = addConnector(subcomponent, frompart, tpart, fromport, toport, dcomp.getName(),
									provelem.getName() + "__" + provelem.getName());
							}catch(Exception e) {
								logger.info(e.getMessage());
							}
						}
						}

					} else {
						logger.info("Connector already exists");
					}
				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

	}

	private void processTopLevelDelegations(PluginTemplate abscomp, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent) {
		
		IRPModelElement fpartOwner = null;
		IRPInstance fpart = null;
		fpartOwner = findSourceandDestination(subcomponent.getName());
		if (fpartOwner != null) {
			fpart = findPart(fpartOwner);
		}
		if (fpart == null) {
			fpartOwner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartOwner != null) {
				fpart = findPart(fpartOwner);
			}
		}
		IRPPort fromport = (IRPPort) fpartOwner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartOwner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartOwner);
		IRPPort toPort = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toPort == null) {
			toPort = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toPort.setContract(funcClass);
			}

		}
		toPort.setIsReversed(1);
		if (reqconnector == null) {
			try {
			reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toPort, abscomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
		IRPModelElement mainele = findSourceandDestination(maincomp.getName());
		IRPInstance mainpart = findPart(mainele);
		IRPPort mainPort = (IRPPort) mainele.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainPort == null) {
			mainPort = (IRPPort) mainele.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainPort.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			try {
			addConnector(swcomponent, tpart, mainpart, toPort, mainPort, abscomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}

		if (abscomp instanceof SubComponent) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(abscomp.getName());
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				try {
				addConnector(delegateelement, mainpart, delegatepart, mainPort, delegateport,
						abscomp.getName(), provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}

	}

	private void processSubComponentDelegations(PluginTemplate providedComponent, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent) {
		
		reqconnector = (IRPLink) subcomponent.findNestedElement(providedComponent.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		IRPModelElement fpartowner = null;
		IRPInstance frompart = null;
		IRPInstance mainpart = null;
		IRPPort mainport = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			frompart = findPart(fpartowner);
		}
		if (frompart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				frompart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toPort = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toPort == null) {
			toPort = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toPort.setContract(funcClass);
			}

		}
		toPort.setIsReversed(1);
		if (reqconnector == null) {
			reqconnector = addConnector(subcomponent, frompart, tpart, fromport, toPort, providedComponent.getName(),
					provelem.getName());
		}
		if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {

			String maincomponent = null;
			for (Entry<String, List<String>> entry : plugintemplateshashmap.entrySet()) {
				List<String> subcomponentname = entry.getValue();
				if (subcomponentname.contains(providedComponent.getName())) {
					maincomponent = entry.getKey();
					break;
				}
			}
			if (!maincomp.getName().equalsIgnoreCase(maincomponent)) {
				if (maincomponent.equalsIgnoreCase(swcomponent.getName())) {
					IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegateelement);
					IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateConnector = (IRPLink) delegateelement.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateConnector == null) {
						try {
						addConnector(delegateelement, tpart, delegatepart, toPort, delegateport,
								providedComponent.getName(), provelem.getName());
						}catch(Exception e) {
							logger.info(e.getMessage());
						}
					}
				} else {
					IRPModelElement mainele = findSourceandDestination(maincomponent);
					mainpart = findPart(mainele);
					mainport = (IRPPort) mainele.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					if (mainport == null) {
						mainport = (IRPPort) mainele.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
								provelem.getName());
						funcClass = getRequiredPortContract(pkg, provelem.getName());
						if (funcClass != null) {
							mainport.setContract(funcClass);
						}

					}
					IRPLink main_Connector = (IRPLink) swcomponent.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (main_Connector == null) {
						try {
						addConnector(swcomponent, tpart, mainpart, toPort, mainport,
								providedComponent.getName(), provelem.getName());
						}catch(Exception e) {
							logger.info(e.getMessage());
						}
					}
					IRPUnit delegateElem = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegateElem);
					IRPPort delegateport = (IRPPort) delegateElem.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateconnector = (IRPLink) delegateElem.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateconnector == null) {
						try {
						addConnector(delegateElem, mainpart, delegatepart, mainport,
								delegateport, providedComponent.getName(), provelem.getName());
						}catch(Exception e) {
							logger.info(e.getMessage());
						}
						}
				}
			} else {
				IRPModelElement mainEle = findSourceandDestination(maincomp.getName());
				mainpart = findPart(mainEle);
				mainport = (IRPPort) mainEle.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (mainport == null) {
					mainport = (IRPPort) mainEle.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						mainport.setContract(funcClass);
					}

				}
				IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
				if (mainConnector == null) {
					try {
					addConnector(swcomponent, tpart, mainpart, toPort, mainport,
							providedComponent.getName(), provelem.getName());
					}catch(Exception e) {
						logger.info(e.getMessage());
					}
				}
			}
		}

		if (!maincomp.getName().equalsIgnoreCase(providedComponent.getName())) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
			IRPInstance delegatePart = findPart(delegateelement);
			IRPPort delegatePort = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				try {
				addConnector(delegateelement, mainpart, delegatePart, mainport, delegatePort,
						providedComponent.getName(), provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}

	}

	private void createportsforSubPlugins(ProvidedElement provEle, Plugin providedComponent,
			AbstractComponent maincomp) {
		
		if (provEle instanceof ProvidedInterface) {
			ProvidedInterface pi = (ProvidedInterface) provEle;
			reqport = (IRPPort) subcomponent.findNestedElement(pi.getName(), GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) subcomponent.findNestedElement(providedComponent.getName() + "__" + pi.getName(),
					GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provEle.getName());
					funcClass = getRequiredPortContract(pkg, provEle.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processSubComponentDelegations(providedComponent, maincomp, provEle, subcomponent);
				} else {
					processTopLevelDelegations(providedComponent, maincomp, provEle, subcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}

		} else if (provEle instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provEle).getDelegateEntities()) {
				AbstractComponent dcomp = die.getDelegateComponent();
				ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
				reqport = (IRPPort) subcomponent.findNestedElement(provEle.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provEle.getName() + "__" + provEle.getName(),
						GlobalVariables.LINK_TAG);
				if (di.getName().equalsIgnoreCase(provEle.getName())
						&& dcomp.getName().equalsIgnoreCase(providedComponent.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									provEle.getName());
							funcClass = getRequiredPortContract(pkg, provEle.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}

						reqport.setIsReversed(1);
						IRPModelElement fpartowner = null;
						IRPInstance fpart = null;
						fpartowner = findSourceandDestination(providedComponent.getName());
						if (fpartowner != null) {
							fpart = findPart(fpartowner);
						}
						if (fpart == null) {
							fpartowner = pkg.findNestedElementRecursive(provEle.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fpartowner != null) {
								fpart = findPart(fpartowner);
							}
						}
						if(fpartowner!=null) {
						IRPPort fromport = (IRPPort) fpartowner.findNestedElementRecursive(provEle.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(subcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) subcomponent.findNestedElement(provEle.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						   if (reqconnector == null && fpart != null && fromport != null) {
							reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, dcomp.getName(),
									provEle.getName() + "__" + provEle.getName());
						   }
					    }
					} else {
						logger.info("Connector already exists");
					}
				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

	}

	private void processTopLevelDelegations(Plugin abscomp, AbstractComponent maincomp, ProvidedElement provelem,
			IRPUnit subcomponent) {
		
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromPort = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tPart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null) {
			try {
			reqconnector = addConnector(subcomponent, fpart, tPart, fromPort, toport, abscomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
		IRPModelElement mainEle = findSourceandDestination(maincomp.getName());
		IRPInstance mainpart = findPart(mainEle);
		IRPPort mainport = (IRPPort) mainEle.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainEle.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			try {
			addConnector(swcomponent, tPart, mainpart, toport, mainport, abscomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}

		if (abscomp instanceof SubComponent) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(abscomp.getName());
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				try {
				addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						abscomp.getName(), provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}

	}

	private void processSubComponentDelegations(Plugin providedComponent, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent) {
		
		IRPModelElement fromPartowner = null;
		IRPInstance fpart = null;
		fromPartowner = findSourceandDestination(subcomponent.getName());
		if (fromPartowner != null) {
			fpart = findPart(fromPartowner);
		}
		if (fpart == null) {
			fromPartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fromPartowner != null) {
				fpart = findPart(fromPartowner);
			}
		}
		IRPPort fromport = (IRPPort) fromPartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance topart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null && fromport != null && fpart != null) {
			reqconnector = addConnector(subcomponent, fpart, topart, fromport, toport, providedComponent.getName(),
					provelem.getName());
		}
		IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(
				providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			try {
			addConnector(mainelement, topart, mainpart, toport, mainport, providedComponent.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
		if (!maincomp.getName().equalsIgnoreCase(providedComponent.getName())) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
			IRPInstance delegatePart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateConnector = (IRPLink) delegateelement.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateConnector == null) {
				try {
				addConnector(delegateelement, mainpart, delegatePart, mainport, delegateport,
						providedComponent.getName(), provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
	}

	private void createportsforSubSubComponents(ProvidedElement provele, AbstractComponent dcomp,
			AbstractComponent providedComponent, AbstractComponent maincomp, String requiredelement,
			IRPModelElement swswcomp) {
		
		if (provele instanceof ProvidedInterface) {
			
			reqport = (IRPPort) subcomponent.findNestedElement(provele.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPPort reqPort = reqport;
			reqconnector = (IRPLink) subcomponent.findNestedElement(
					providedComponent.getName() + "__" + provele.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqPort == null) {
					reqPort = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provele.getName());
					funcClass = getRequiredPortContract(pkg, provele.getName());
					if (funcClass != null) {
						reqPort.setContract(funcClass);
					}
				}
				reqPort.setIsReversed(1);

				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processSubSubComponentDelegations(providedComponent, maincomp, provele, subcomponent, swswcomp);
				} else {
					processSubTopLevelDelegations(providedComponent, maincomp, provele, subcomponent, swswcomp);
				}
			} else {
				logger.info("Connector already exists");
			}
			reqport = reqPort;
		} else if (provele instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provele).getDelegateEntities()) {
				dcomp = die.getDelegateComponent();
				reqport = (IRPPort) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provele.getName(), GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provele.getName() + "__" + provele.getName(),
						GlobalVariables.LINK_TAG);
				if (dcomp.getName().equalsIgnoreCase(providedComponent.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									providedComponent.getName() + "__" + provele.getName());
							funcClass = getRequiredPortContract(pkg, provele.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}
						reqport.setIsReversed(1);
						IRPModelElement fpartowner = null;
						IRPInstance fpart = null;
						fpartowner = findSourceandDestination(providedComponent.getName());
						if (fpartowner != null) {
							fpart = findPart(fpartowner);
						}
						if (fpart == null) {
							fpartowner = pkg.findNestedElementRecursive(provele.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fpartowner != null) {
								fpart = findPart(fpartowner);
							}
						}
					    if(fpartowner!=null) {
						IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provele.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(subcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provele.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if (reqconnector == null) {
							try {
							reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, dcomp.getName(),
									provele.getName() + "__" + provele.getName());
							}catch(Exception e) {
								logger.info(e.getMessage());
							}
						}
					    }
					} else {
						logger.info("Connector already exists");
					}
				} 
				else if ((provele instanceof DelegateInterface) && (!match)) {
						reqport = (IRPPort) subcomponent.findNestedElement(provele.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provele.getName(),
								GlobalVariables.CONNECTOR_METACLASS);

						if (reqconnector == null) {
							if (reqport == null) {
								reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
										provele.getName());
								funcClass = getRequiredPortContract(pkg, provele.getName());
								if (funcClass != null) {
									reqport.setContract(funcClass);
								}
							}
							reqport.setIsReversed(1);
							processDelegationConnectors(providedComponent, provele, dcomp, subcomponent);
						} else {
							logger.info("Connnector exists already");
						}

				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

		logger.info("Required Element :" + requiredelement);

	}

	private void processSubTopLevelDelegations(AbstractComponent abscomp, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent, IRPModelElement swswcomp) {
		
		IRPModelElement fpartowner = null;
		IRPInstance frompart = null;
		IRPInstance mainpart = null;
		IRPPort mainPort = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			frompart = findPart(fpartowner);
		}
		if (frompart == null) {
			fpartowner = pkg.findNestedElementRecursive(subcomponent.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				frompart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement subOwner = findSourceandDestination(swswcomp.getName());
		IRPInstance spart = findPart(subOwner);
		IRPPort sport = (IRPPort) subOwner.findNestedElement(provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
		if (sport == null) {
			sport = (IRPPort) swswcomp.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				sport.setContract(funcClass);
			}

		}
		sport.setIsReversed(1);
		if (reqconnector == null) {
			try {
			reqconnector = addConnector(subcomponent, frompart, spart, fromport, sport, providedComponent.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}

		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		
		try {
		 addConnector(swswcomp, spart, tpart, sport, toport, abscomp.getName(),
				provelem.getName());
		}catch(Exception e) {
			logger.info(e.getMessage());
		}
		IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
		mainpart = findPart(mainelement);
		mainPort = (IRPPort) mainelement.findNestedElement(provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
		if (mainPort == null) {
			mainPort = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainPort.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			try {
			addConnector(swcomponent, tpart, mainpart, toport, mainPort, abscomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}

		if (abscomp instanceof SubComponent) {
			IRPUnit delegateelement = (IRPUnit) pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.COMPONENT_METACLASS);
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				try {
				addConnector(delegateelement, mainpart, delegatepart, mainPort, delegateport,
						abscomp.getName(), provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
	}

	private void processSubSubComponentDelegations(AbstractComponent providedComponent, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent, IRPModelElement swswcomp) {
		
		reqconnector = (IRPLink) subcomponent.findNestedElement(providedComponent.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);

		IRPModelElement fromPartOwner = null;
		IRPInstance fpart = null;
		IRPInstance mainPart = null;
		IRPPort mainport = null;
		fromPartOwner = findSourceandDestination(subcomponent.getName());
		if (fromPartOwner != null) {
			fpart = findPart(fromPartOwner);
		}
		if (fpart == null) {
			fromPartOwner = pkg.findNestedElementRecursive(subcomponent.getName(), GlobalVariables.CLASS_METACLASS);
			if (fromPartOwner != null) {
				fpart = findPart(fromPartOwner);
			}
		}
		IRPPort fromport = (IRPPort) fromPartOwner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement subOwner = findSourceandDestination(swswcomp.getName());
		IRPInstance spart = findPart(subOwner);
		IRPPort sPort = (IRPPort) subOwner.findNestedElement(provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
		if (sPort == null) {
			sPort = (IRPPort) swswcomp.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				sPort.setContract(funcClass);
			}

		}
		sPort.setIsReversed(1);
		if (reqconnector == null) {
			reqconnector = addConnector(subcomponent, fpart, spart, fromport, sPort, providedComponent.getName(),
					provelem.getName());
		}

		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toPort = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toPort == null) {
			toPort = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toPort.setContract(funcClass);
			}

		}
		toPort.setIsReversed(1);

		@SuppressWarnings("unused")
		IRPLink subreqconnector = addConnector(swswcomp, spart, tpart, sPort, toPort, providedComponent.getName(),
				provelem.getName());

		if (providedComponent instanceof SubComponent) {

			String mainComp = null;
			for (Entry<String, List<String>> entry : subcomponenthashmap.entrySet()) {
				List<String> subcomponentname = entry.getValue();
				if (subcomponentname.contains(providedComponent.getName())) {
					mainComp = entry.getKey();
					break;
				}
			}
			if (!maincomp.getName().equalsIgnoreCase(mainComp)) {
				if (mainComp.equalsIgnoreCase(swcomponent.getName())) {
					IRPUnit delegateEle = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegateEle);
					IRPPort delegateport = (IRPPort) delegateEle.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateconnector = (IRPLink) delegateEle.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateconnector == null) {
						try {
						addConnector(delegateEle, tpart, delegatepart, toPort, delegateport,
								providedComponent.getName(), provelem.getName());
						}catch(Exception e) {
							logger.info(e.getMessage());
						}
					}
				} else {
					IRPModelElement mainelem = findSourceandDestination(mainComp);
					mainPart = findPart(mainelem);
					mainport = (IRPPort) mainelem.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					if (mainport == null) {
						mainport = (IRPPort) mainelem.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
								provelem.getName());
						funcClass = getRequiredPortContract(pkg, provelem.getName());
						if (funcClass != null) {
							mainport.setContract(funcClass);
						}

					}
					IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (mainConnector == null) {
						try {
						addConnector(swcomponent, tpart, mainPart, toPort, mainport,
								providedComponent.getName(), provelem.getName());
						}catch(Exception e) {
							logger.info(e.getMessage());
						}
					}
					IRPUnit delegatelem = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegatelem);
					IRPPort delegateport = (IRPPort) delegatelem.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateconnector = (IRPLink) delegatelem.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateconnector == null) {
						try {
						addConnector(delegatelem, mainPart, delegatepart, mainport,
								delegateport, providedComponent.getName(), provelem.getName());
						}catch(Exception e) {
							logger.info(e.getMessage());
						}
					}
				}
			} else {
				IRPModelElement mainElem = findSourceandDestination(maincomp.getName());
				mainPart = findPart(mainElem);
				mainport = (IRPPort) mainElem.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (mainport == null) {
					mainport = (IRPPort) mainElem.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						mainport.setContract(funcClass);
					}

				}
				IRPLink mainconnector = (IRPLink) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
				if (mainconnector == null) {
					try {
					addConnector(swcomponent, tpart, mainPart, toPort, mainport,
							providedComponent.getName(), provelem.getName());
					}catch(Exception e) {
						logger.info(e.getMessage());
					}
				}
			}
		}

		if (!maincomp.getName().equalsIgnoreCase(providedComponent.getName())) {
			IRPUnit delegateElement = (IRPUnit) findSourceandDestination(providedComponent.getName());
			IRPInstance delegatePart = findPart(delegateElement);
			IRPPort delegateport = (IRPPort) delegateElement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateConnector = (IRPLink) delegateElement.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateConnector == null) {
				try {
				addConnector(delegateElement, mainPart, delegatePart, mainport, delegateport,
						providedComponent.getName(), provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}

	}

	private void createportsforSubComponents(ProvidedElement provelement, AbstractComponent dcomp,
			AbstractComponent providedComponent, AbstractComponent maincomp, String requiredelement) {
        try
        {
		if (provelement instanceof ProvidedInterface) {
			reqport = (IRPPort) subcomponent.findNestedElement(provelement.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) subcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelement.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelement.getName());
					funcClass = getRequiredPortContract(pkg, provelement.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);

				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processSubComponentDelegations(providedComponent, maincomp, provelement, subcomponent);
				} else {
					processTopLevelDelegations(providedComponent, maincomp, provelement, subcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}

		} else if (provelement instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provelement).getDelegateEntities()) {
				dcomp = die.getDelegateComponent();
				reqport = (IRPPort) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelement.getName(), GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelement.getName() + "__" + provelement.getName(),
						GlobalVariables.LINK_TAG);
				if (dcomp.getName().equalsIgnoreCase(providedComponent.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									providedComponent.getName() + "__" + provelement.getName());
							funcClass = getRequiredPortContract(pkg, provelement.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}
						reqport.setIsReversed(1);
						IRPModelElement fpartowner = null;
						IRPInstance fpart = null;
						fpartowner = findSourceandDestination(providedComponent.getName());
						if (fpartowner != null) {
							fpart = findPart(fpartowner);
						}
						if (fpart == null) {
							fpartowner = pkg.findNestedElementRecursive(provelement.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fpartowner != null) {
								fpart = findPart(fpartowner);
							}
						}
						if(fpartowner!=null) {
						IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelement.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(subcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelement.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if (reqconnector == null) {
							reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, dcomp.getName(),
									provelement.getName() + "__" + provelement.getName());
						}
						}
					} else {
						logger.info("Connector already exists");
					}
				} 
				else if ((provelement instanceof DelegateInterface) && (!match)) {
						reqport = (IRPPort) subcomponent.findNestedElement(provelement.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelement.getName(),
								GlobalVariables.CONNECTOR_METACLASS);

						if (reqconnector == null) {
							if (reqport == null) {
								reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
										provelement.getName());
								funcClass = getRequiredPortContract(pkg, provelement.getName());
								if (funcClass != null) {
									reqport.setContract(funcClass);
								}
							}
							reqport.setIsReversed(1);
							processDelegationConnectors(providedComponent, provelement, dcomp, subcomponent);
						} else {
							logger.info("Connnector exists already");
						}
				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}
       }catch (Exception e) {
	     logger.info("Exception while creating port\n" + e.toString());
	}
		logger.info("Required Element :" + requiredelement);

	}

	private void processRequiredVariable(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
	

		try {
			flag = false;
			Boolean is_flag =flag;
			isPresent=false;
			providedComponent = null;
			RequiredVariable rc = (RequiredVariable) rie;
			String requiredvar = rc.getVariable().getName();
			AbstractComponent maincomp = ri.getComponent();
			AbstractComponent dComp = null;
			ProvidedElement provelem = ri.getInterface();
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			
			String providedElement = provelem.getName();
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity deleEntity : pi.getDelegateEntities()) {
						dComp = deleEntity.getDelegateComponent();
						String dCompName = dComp.getName();
						ProvidedInterface di = (ProvidedInterface) deleEntity.getDelegateInterface();
						
						Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
						          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
						
						for (Entry<Object, Object> entry : filtermap
								.entrySet()) {
							if (!is_flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
								if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(dComp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredvar)) {
											providedComponent = ((AbstractComponent)entry.getKey());
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dComp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												is_flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (is_flag) {
											break;
										}

									}
								}
							}
							if (is_flag) {
								break;
							}
						}

					}
				}
				
				Map<Object, Object> providedFilterMap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
				
				for (Entry<Object, Object> entry : providedFilterMap
						.entrySet()) {
					if (!is_flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
					
								if(interfacename.get(provelem.getName()) != null) {
								HashMap<String, String> intfelems = interfacename.get(provelem.getName());
								if (intfelems.containsValue(requiredvar)) {

								
									providedComponent = (AbstractComponent) entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										is_flag = true;
									} else {
										isPresent = true;
										break;
									}
								}
							}
							if (is_flag) {
								break;
							}
					
					}
					if (is_flag) {
						break;
					}
				}
				if (!is_flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!is_flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(provelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provelem.getName());
									if (intfelems.containsValue(requiredvar)) {

										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										IRPLink reqConnector = reqconnector;
										if (reqConnector == null) {
											is_flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (is_flag) {
									break;
								}
					
						}
						if (is_flag) {
							break;
						}
					}
				}
				
				if (!is_flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!is_flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						
									if(interfacename.get(provelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provelem.getName());
									if (intfelems.containsValue(requiredvar)) {

										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										IRPLink requiredConnector = reqconnector;
										if (requiredConnector == null) {
											is_flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (is_flag) {
									break;
								}
					
						}
						if (is_flag) {
							break;
						}
					}
				}
				if ((is_flag) && (providedComponent != null) ){
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforComponents(provelem, providedComponent, maincomp,
									rc.getVariable().getName(), swcomponent);
						}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforPlugins(provelem, pluginComponent, maincomp);
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforTemplates(provelem, pluginTemplateComponent, maincomp);
				}
			}

		} catch (

		Exception e) {
		
			logger.info("Error in creating connectors");
		}

	}

	@SuppressWarnings("null")
	private void processRequiredFunction(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
	

		try {
			match = false;
			flag = false;
			isPresent = false;
			providedComponent = null;
			AbstractComponent dcomp = null;
			RequiredFunction rc = (RequiredFunction) rie;
			String reqfuncion = rc.getFunction().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelement = ri.getInterface();
			String providedElement = provelement.getName();
			Plugin pluginComponent = null;

			PluginTemplate pluginTemplateComponent = null;
		
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelement.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (!flag) {
					if (provelement instanceof DelegateInterface) {
						DelegateInterface pi = (DelegateInterface) provelement;
						for (DelegateEntity die : pi.getDelegateEntities()) {
							dcomp = die.getDelegateComponent();
							String dCompName = dcomp.getName();
							reqconnector = (IRPLink) swcomponent.findNestedElement(
									dcomp.getName() + "__" + provelement.getName(), GlobalVariables.CONNECTOR_METACLASS);
							if (reqconnector == null) {
								ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
								
								Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
								          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue())); 
								
								
								for (Entry<Object, Object> entry : filtermap
										.entrySet()) {
									if (!flag && !isPresent) {
										@SuppressWarnings("unchecked")
										HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
										if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(dcomp.getName())) {
											for (Entry<String, HashMap<String, String>> intfname : interfacename
													.entrySet()) {
												HashMap<String, String> intfs = intfname.getValue();
												if (intfs.containsValue(reqfuncion))
												{
													providedComponent = ((AbstractComponent)entry.getKey());
													provelement = di;
													reqconnector = (IRPLink) swcomponent.findNestedElement(
															dcomp.getName() + "__" + provelement.getName(),
															GlobalVariables.CONNECTOR_METACLASS);
													if (reqconnector == null) 
													{
														flag = true;
													} else 
													{
														isPresent = true;
														break;}
												}
												if (flag) {
													break;
												}

											}
										}
									}
									if (flag || isPresent) {
										break;
									}
								}

							} else {
								flag = true;
							}
						}
					}
					
					Map<Object, Object> providedFilterMap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
					
					for (Entry<Object, Object> entry : providedFilterMap
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
					
									if(interfacename.get(provelement.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provelement.getName());
									if (intfelems.containsValue(reqfuncion)) {

										providedComponent = (AbstractComponent) entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelement.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag)
								{
									break;
								}
						
						}
						if (flag || isPresent) 
						{
							break;
						}
					}
				}

				if (!flag) {
					Map<Object, Object> providedFilterMap = pluginTemplateInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
					for (Entry<Object, Object> entry : providedFilterMap
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = (HashMap<String, HashMap<String, String>>) entry.getValue();
						
									if(interfacename.get(provelement.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provelement.getName());
									if (intfelems.containsValue(reqfuncion)) {

								
										pluginTemplateComponent = (PluginTemplate) entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelement.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										IRPLink connector = reqconnector;
										if (connector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
					
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entrySet : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entrySet.getValue();
						
									if(interfacename.get(provelement.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(provelement.getName());
									if (intfelems.containsValue(reqfuncion)) {

										pluginComponent = entrySet.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginComponent.getName() + "__" + provelement.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
						
						}
						if (flag) {
							break;
						}
					}
				}
				if ((flag || isPresent) && (providedComponent != null)) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelement.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforComponents(provelement, providedComponent, maincomp,
									rc.getFunction().getName(), swcomponent);
						}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelement.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforPlugins(provelement, pluginComponent, maincomp);
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelement.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforTemplates(provelement, pluginTemplateComponent, maincomp);
				}
			}
		} catch (Exception e) {
		
			logger.info("Error in creating connectors");
		}

	}

	private void createportsforTemplates(ProvidedElement provelem, PluginTemplate providedComponent,
			AbstractComponent maincomp) {
		
		if (provelem instanceof ProvidedInterface) {
			reqport = (IRPPort) swcomponent.findNestedElement(provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) swcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processTopLevelSubConnectors(providedComponent, maincomp, provelem, swcomponent);
				} else {
					processTopLevelConnectors(providedComponent, maincomp, provelem, swcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}
		} else if (provelem instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provelem).getDelegateEntities()) {
				AbstractComponent dcomp = die.getDelegateComponent();
				ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();

				reqport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
						GlobalVariables.LINK_TAG);
				if (di.getName().equalsIgnoreCase(provelem.getName())
						&& dcomp.getName().equalsIgnoreCase(providedComponent.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									provelem.getName());
							funcClass = getRequiredPortContract(pkg, provelem.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}

						reqport.setIsReversed(1);
						IRPModelElement fpartOwner = null;
						IRPInstance fpart = null;
						fpartOwner = findSourceandDestination(providedComponent.getName());
						if (fpartOwner != null) {
							fpart = findPart(fpartOwner);
						}
						if (fpart == null) {
							fpartOwner = pkg.findNestedElementRecursive(provelem.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fpartOwner != null) {
								fpart = findPart(fpartOwner);
							}
						}
						if(fpartOwner!=null) {
						IRPPort fromport = (IRPPort) fpartOwner.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if (reqconnector == null && fpart != null && fromport != null) {
							reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, dcomp.getName(),
									provelem.getName() + "__" + provelem.getName());
						}
						}
					} else {
						logger.info("Connector already exists");
					}
				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

	}

	private void processTopLevelConnectors(PluginTemplate abscomp, AbstractComponent maincomp, ProvidedElement provelem,
			IRPUnit swcomponent) {

		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(maincomp.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElementRecursive(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			try {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, abscomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}

		if (abscomp instanceof SubComponent) {
			IRPModelElement mainElem = findSourceandDestination(abscomp.getName());
			IRPInstance mainpart = findPart(mainElem);
			IRPPort mainport = (IRPPort) mainElem.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainport == null) {
				mainport = (IRPPort) mainElem.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
				funcClass = getRequiredPortContract(pkg, provelem.getName());
				if (funcClass != null) {
					mainport.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainElem.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				try {
				addConnector(mainElem, fpart, mainpart, fromport, mainport, abscomp.getName(),
						provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
		if (abscomp instanceof PluginTemplate) {
			IRPModelElement mainElement = pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
			IRPInstance mainpart = findPart(mainElement);
			IRPPort mainport = (IRPPort) mainElement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainport == null) {
				mainport = (IRPPort) mainElement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
				funcClass = getRequiredPortContract(pkg, provelem.getName());
				if (funcClass != null) {
					mainport.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainElement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				try {
				addConnector(mainElement, fpart, mainpart, fromport, mainport, abscomp.getName(),
						provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}

	}

	private void processTopLevelSubConnectors(PluginTemplate subcomp, AbstractComponent abscomp,
			ProvidedElement provelem, IRPUnit swcomponent) {

		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(abscomp.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {

				fpart = findPart(fpartowner);
			}
		}
		if(fpartowner!=null) {
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (fromport == null) {
			fromport = (IRPPort) fpartowner.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				fromport.setContract(funcClass);
			}
		}
		fromport.setIsReversed(1);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) tpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			try {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, subcomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
		IRPModelElement mainelement = pkg.findNestedElementRecursive(subcomp.getName(),
				GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}
		}
		IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(subcomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			try {
			addConnector(mainelement, fpart, mainpart, fromport, mainport, subcomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
	}

	}

	private void createportsforPlugins(ProvidedElement provElem, Plugin providedComponent,
			AbstractComponent maincomp) {

		if (provElem instanceof ProvidedInterface) {
			reqport = (IRPPort) swcomponent.findNestedElement(provElem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) swcomponent.findNestedElement(
					providedComponent.getName() + "__" + provElem.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provElem.getName());
					funcClass = getRequiredPortContract(pkg, provElem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processTopLevelSubConnectors(providedComponent, maincomp, provElem, swcomponent);
				} else {
					processTopLevelConnectors(providedComponent, maincomp, provElem, swcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}

		} else if (provElem instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provElem).getDelegateEntities()) {
				AbstractComponent dcomp = die.getDelegateComponent();
				ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
				reqport = (IRPPort) swcomponent.findNestedElement(provElem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provElem.getName() + "__" + provElem.getName(),
						GlobalVariables.LINK_TAG);
				if (di.getName().equalsIgnoreCase(provElem.getName())
						&& dcomp.getName().equalsIgnoreCase(providedComponent.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									provElem.getName());
							funcClass = getRequiredPortContract(pkg, provElem.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}

						reqport.setIsReversed(1);
						IRPModelElement fromPartowner = null;
						IRPInstance fpart = null;
						fromPartowner = findSourceandDestination(providedComponent.getName());
						if (fromPartowner != null) {
							fpart = findPart(fromPartowner);
						}
						if (fpart == null) {
							fromPartowner = pkg.findNestedElementRecursive(provElem.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fromPartowner != null) {
								fpart = findPart(fromPartowner);
							}
						}
						if(fromPartowner!=null) {
						IRPPort fromport = (IRPPort) fromPartowner.findNestedElement(provElem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) swcomponent.findNestedElement(provElem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if (reqconnector == null && fpart != null && fromport != null) {
							reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, dcomp.getName(),
									provElem.getName() + "__" + provElem.getName());
						}
					}
					} else {
						logger.info("Connector already exists");
					}
				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

	}

	private void processTopLevelConnectors(Plugin abscomp, AbstractComponent maincomp, ProvidedElement provElement,
			IRPUnit swcomponent) {

		IRPModelElement fromPartOwner = null;
		IRPInstance fpart = null;
		fromPartOwner = findSourceandDestination(maincomp.getName());
		if (fromPartOwner != null) {
			fpart = findPart(fromPartOwner);
		}
		if (fpart == null) {
			fromPartOwner = pkg.findNestedElementRecursive(provElement.getName(), GlobalVariables.CLASS_METACLASS);
			if (fromPartOwner != null) {
				fpart = findPart(fromPartOwner);
			}
		}
		IRPPort fromport = (IRPPort) fromPartOwner.findNestedElement(provElement.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartOwner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartOwner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provElement.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			try {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, abscomp.getName(),
					provElement.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}

		if (abscomp instanceof SubComponent) {
			IRPModelElement mainelement = findSourceandDestination(abscomp.getName());
			IRPInstance mainpart = findPart(mainelement);
			IRPPort mainport = (IRPPort) mainelement.findNestedElement(provElement.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainport == null) {
				mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provElement.getName());
				funcClass = getRequiredPortContract(pkg, provElement.getName());
				if (funcClass != null) {
					mainport.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(
					abscomp.getName() + "__" + provElement.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				try {
				addConnector(mainelement, fpart, mainpart, fromport, mainport, abscomp.getName(),
						provElement.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
		if (abscomp instanceof PluginTemplate) {
			IRPModelElement mainElement = pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
			IRPInstance mainpart = findPart(mainElement);
			IRPPort mainPort = (IRPPort) mainElement.findNestedElement(provElement.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainPort == null) {
				mainPort = (IRPPort) mainElement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provElement.getName());
				funcClass = getRequiredPortContract(pkg, provElement.getName());
				if (funcClass != null) {
					mainPort.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainElement.findNestedElement(
					abscomp.getName() + "__" + provElement.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				try {
				addConnector(mainElement, fpart, mainpart, fromport, mainPort, abscomp.getName(),
						provElement.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
	}

	private void processTopLevelSubConnectors(Plugin subcomp, AbstractComponent abscomp, ProvidedElement provelem,
			IRPUnit swcomponent) {

		IRPModelElement fPartOwner = null;
		IRPInstance fpart = null;
		fPartOwner = findSourceandDestination(abscomp.getName());
		if (fPartOwner != null) {
			fpart = findPart(fPartOwner);
		}
		if (fpart == null) {
			fPartOwner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fPartOwner != null) {

				fpart = findPart(fPartOwner);
			}
		}
		if(fPartOwner!=null) {
		IRPPort fromport = (IRPPort) fPartOwner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (fromport == null) {
			fromport = (IRPPort) fPartOwner.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				fromport.setContract(funcClass);
			}
		}
		
		fromport.setIsReversed(1);
		IRPModelElement tpartOwner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartOwner);
		IRPPort toport = (IRPPort) tpartOwner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			try {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, subcomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
		IRPModelElement mainelem = findSourceandDestination(subcomp.getName());
		IRPInstance mainpart = findPart(mainelem);
		IRPPort mainport = (IRPPort) mainelem.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelem.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}
		}
		IRPLink mainConnector = (IRPLink) mainelem.findNestedElement(subcomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			try {
			addConnector(mainelem, fpart, mainpart, fromport, mainport, subcomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
	}
	}

	private void createportsforComponents(ProvidedElement provelem, AbstractComponent providedComponent, AbstractComponent maincomp, String requiredelement,
			IRPUnit swcomponent) {

		if (provelem instanceof ProvidedInterface) {
			reqport = (IRPPort) swcomponent.findNestedElement(provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
			
			IRPPort reqport_ = (IRPPort) swcomponent.findNestedElement(requiredelement, GlobalVariables.PROXY_PORT_METACLASS);

			reqconnector = (IRPLink) swcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				if (reqport_ == null) {
					reqport_ = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							requiredelement);
					if (funcClass != null) {
						reqport_.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				reqport_.setIsReversed(1);
				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processTopLevelSubConnectors(providedComponent, provelem, swcomponent);
				} else {
					processTopLevelConnectors(providedComponent, maincomp, provelem, swcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}
		} else if (provelem instanceof DelegateInterface) {
			reqport = (IRPPort) swcomponent.findNestedElement(providedComponent.getName() + "__" + provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) swcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
					GlobalVariables.LINK_TAG);
			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							providedComponent.getName() + "__" + provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				IRPModelElement fpartowner = null;
				IRPInstance fpart = null;
				fpartowner = findSourceandDestination(providedComponent.getName());
				if (fpartowner != null) {
					fpart = findPart(fpartowner);
				}
				if(fpartowner != null) {
				IRPPort fromport = (IRPPort) fpartowner.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);

				if (fromport == null) {
					fromport = (IRPPort) fpartowner.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							providedComponent.getName() + "__" + provelem.getName());
				}
				IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
				IRPInstance tpart = findPart(tpartowner);
				IRPPort toport = (IRPPort) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
				if (reqconnector == null) {
					if (providedComponent instanceof SubComponent) {
						processDelegations(providedComponent, maincomp, provelem, swcomponent);
					} else {
						try {
						reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport,
								providedComponent.getName(), provelem.getName() + "__" + provelem.getName());
						}catch(Exception e) {
							logger.info(e.getMessage());
						}
					}
				}
			}

			} else {
				logger.info("Connector already exists");
			}

		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

		logger.info("Required Element :" + requiredelement);
	}

	private void processDelegations(AbstractComponent providedComponent, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit swcomponent) {

		reqport = (IRPPort) swcomponent.findNestedElement(providedComponent.getName() + "__" + provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		reqconnector = (IRPLink) swcomponent.findNestedElement(
				providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);

		if (reqport == null) {
			reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
					providedComponent.getName() + "__" + provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				reqport.setContract(funcClass);
			}
		}
		IRPModelElement fpartowner = null;
		IRPInstance frompart = null;
		fpartowner = findSourceandDestination(swcomponent.getName());
		if (fpartowner != null) {
			frompart = findPart(fpartowner);
		}
		if (frompart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				frompart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement topartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(topartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null && fromport != null && frompart != null) {
			reqconnector = addConnector(swcomponent, frompart, tpart, fromport, toport, providedComponent.getName(),
					provelem.getName());
		}
		IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainPort = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainPort == null) {
			mainPort = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainPort.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(
				providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			try {
			addConnector(swcomponent, tpart, mainpart, toport, mainPort, providedComponent.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
		if (!maincomp.getName().equalsIgnoreCase(providedComponent.getName())) {
			IRPUnit deleElement = (IRPUnit) findSourceandDestination(providedComponent.getName());
			IRPInstance delegatepart = findPart(deleElement);
			IRPPort delegateport = (IRPPort) deleElement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) deleElement.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				try {
				addConnector(deleElement, mainpart, delegatepart, mainPort, delegateport,
						providedComponent.getName(), provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
	}

	private void processRequiredPort(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
	

		try {
			flag = false;
			isPresent=false;
			providedComponent = null;
			RequiredPort rc = (RequiredPort) rie;
			String requiredport = rc.getPort().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement providedelem = ri.getInterface();
			AbstractComponent dcomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String providedElement = providedelem.getName();
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + providedelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (providedelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) providedelem;
					for (DelegateEntity delegateEntity : pi.getDelegateEntities()) {
						dcomp = delegateEntity.getDelegateComponent();
						String dCompName = dcomp.getName();
						ProvidedInterface di = (ProvidedInterface) delegateEntity.getDelegateInterface();
						
						Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
						          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
						
						for (Entry<Object, Object> entry : filtermap
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> intfName = (HashMap<String, HashMap<String, String>>) entry.getValue();
								if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : intfName.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredport)) {
											providedComponent = ((AbstractComponent)entry.getKey());
											providedelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + providedelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (flag) {
											break;}

									}
								}}
							if (flag || isPresent) {
								break;
							}
						}

					}
				}
				
				Map<Object, Object> provIntfFilterMap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
				
				for (Entry<Object, Object> entry : provIntfFilterMap
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> intfName = (HashMap<String, HashMap<String, String>>) entry.getValue();
					
								if(intfName.get(providedelem.getName()) != null) {
								HashMap<String, String> intfelems = intfName.get(providedelem.getName());
								if (intfelems.containsValue(requiredport)) {


									providedComponent = (AbstractComponent) entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + providedelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									IRPLink requiConnector = reqconnector;
									if (requiConnector == null) {
										flag = true;
									} else {
										isPresent = true;
										break;
									}
								}
							}
							if (flag || isPresent) {
								break;
							}
				
					}
					if (flag || isPresent) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
					
									if(interfacename.get(providedelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(providedelem.getName());
									if (intfelems.containsValue(requiredport)) {

										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + providedelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										IRPLink ReqConector = reqconnector;
										if (ReqConector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
					
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entryset : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entryset.getValue();
						
									if(interfacename.get(providedelem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(providedelem.getName());
									if (intfelems.containsValue(requiredport)) {
										
										pluginComponent = entryset.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginComponent.getName() + "__" + providedelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
						
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if ((flag || isPresent) && (providedComponent != null) ) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + providedelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforComponents(providedelem, providedComponent, maincomp,
									rc.getPort().getName(), swcomponent);
						}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + providedelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforPlugins(providedelem, pluginComponent, maincomp);
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + providedelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforTemplates(providedelem, pluginTemplateComponent, maincomp);
				}
			}

		} catch (Exception e) {
			
			logger.info("Error in creating connectors");
		}

	}

	private void processRequiredConstant(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
	
		try {
			flag = false;
			providedComponent = null;
		isPresent=false;
			RequiredConstant rc = (RequiredConstant) rie;
			String requiredconstant = rc.getConstant().getName();
			AbstractComponent maincomp = ri.getComponent();
			AbstractComponent dcomp = null;
			ProvidedElement providedElem = ri.getInterface();
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			String providedElement = providedElem.getName();
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + providedElem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (providedElem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) providedElem;
					for (DelegateEntity deleEntity : pi.getDelegateEntities()) {
						dcomp = deleEntity.getDelegateComponent();
						String dCompName = dcomp.getName();
						ProvidedInterface di = (ProvidedInterface) deleEntity.getDelegateInterface();
						
						Map<Object, Object> filtermap = providedInterfaces.entrySet().stream().filter(map -> map.getKey().getName().equalsIgnoreCase(dCompName)) 
						          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
						
						for (Entry<Object, Object> entry : filtermap
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename =  (HashMap<String, HashMap<String, String>>) entry.getValue();
								if (((AbstractComponent)entry.getKey()).getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfName : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfName.getValue();
										if (intfs.containsValue(requiredconstant)) {
											providedComponent = ((AbstractComponent)entry.getKey());
											providedElem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + providedElem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}

					}
				}
				
				Map<Object, Object> provFilterMap = providedInterfaces.entrySet().stream().filter(x->x.getValue().containsKey(providedElement)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
				
				for (Entry<Object, Object> entryset : provFilterMap
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacenameMap = (HashMap<String, HashMap<String, String>>) entryset.getValue();
					
								if(interfacenameMap.get(providedElem.getName()) != null) {
								HashMap<String, String> intfelems = interfacenameMap.get(providedElem.getName());
								if (intfelems.containsValue(requiredconstant)) {

								
									providedComponent = (AbstractComponent) entryset.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + providedElem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									IRPLink connector = reqconnector;
									if (connector == null)
									{
										flag = true;
									} else {
										isPresent = true;
										break;
									}

								}
							}
							if (flag) 
							{
								break;
							}
					
					}
					if (flag || isPresent) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entryset : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> intfname = entryset.getValue();
						
									if(intfname.get(providedElem.getName()) != null) {
									HashMap<String, String> intfelems = intfname.get(providedElem.getName());
									if (intfelems.containsValue(requiredconstant)) {

									
										pluginTemplateComponent = entryset.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + providedElem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										IRPLink reuiredConnector = reqconnector;
										if (reuiredConnector == null)
										{
											flag = true;
										} else 
										{
											isPresent = true;
											break;
										}
									}
								}
								if (flag) 
								{
									break;
								}
					
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag && !isPresent)  {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
					
									if(interfacename.get(providedElem.getName()) != null) {
									HashMap<String, String> intfelems = interfacename.get(providedElem.getName());
									if (intfelems.containsValue(requiredconstant)) {

										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginComponent.getName() + "__" + providedElem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										IRPLink requiredConnector = reqconnector;
										if (requiredConnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
				
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if ((flag || isPresent) && (providedComponent != null)) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + providedElem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforComponents(providedElem, providedComponent, maincomp,
									rc.getConstant().getName(), swcomponent);
						}
				}
			}

			if (flag) {
				if (pluginComponent != null) {
					reqconnector = (IRPLink) swcomponent.findNestedElement(
							pluginComponent.getName() + "__" + providedElem.getName(), GlobalVariables.LINK_TAG);
					if (reqconnector == null) {
						createportsforPlugins(providedElem, pluginComponent, maincomp);
					}
				}
				if (pluginTemplateComponent != null) {
					reqconnector = (IRPLink) swcomponent.findNestedElement(
							pluginTemplateComponent.getName() + "__" + providedElem.getName(), GlobalVariables.LINK_TAG);
					if (reqconnector == null) {
						createportsforTemplates(providedElem, pluginTemplateComponent, maincomp);
					}
				}
			}
		} catch (Exception e) {
			
			logger.info("Error in creating connectors");
		}

	}

	private void processTopLevelSubConnectors(AbstractComponent subcomp,
			ProvidedElement provelem, IRPUnit swcomponent) {
	

		String main_comp = null;
		for (Entry<String, List<String>> entry : subcomponenthashmap.entrySet()) {
			List<String> subcomponentname = entry.getValue();
			if (subcomponentname.contains(providedComponent.getName())) {
				main_comp = entry.getKey();
				break;
			}
		}
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(main_comp);
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {

				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (fromport == null) {
			fromport = (IRPPort) fpartowner.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				fromport.setContract(funcClass);
			}
		}
		fromport.setIsReversed(1);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		if (tpartowner == null) {
			tpartowner = subcluster.findNestedElementRecursive(swcomponent.getName(), GlobalVariables.CLASS_METACLASS);
		}
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) tpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			try {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, subcomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
		IRPModelElement mainelement = findSourceandDestination(subcomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainPort = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainPort == null) {
			mainPort = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainPort.setContract(funcClass);
			}
		}
		IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(subcomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			try {
			 addConnector(mainelement, fpart, mainpart, fromport, mainPort, subcomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}

	}

	private void processPlugin(AbstractComponent abscomp, ProvidedElement provelem) {

		try {
			IRPModelElement pluginelemname = null;;
			IRPModelElement fpartelem = findSourceandDestination(abscomp.getName());
			if (fpartelem != null) {
				IRPCollection plugins = fpartelem.getNestedElementsByMetaClass("Class ", 0);
				for (Object pluginelem : plugins.toList()) {
					pluginelemname = (IRPModelElement) pluginelem;
					if (pluginelemname.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.PLUGIN_METACLASS)) {
						IRPPort reqPort = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPLink reqConnector = (IRPLink) swcomponent.findNestedElement(
								pluginelemname.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);

						if (reqPort == null) {
							reqPort = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									provelem.getName());
							funcClass = (IRPClass) getIntfBlockFunctionClas(pkg, reqPort, abscomp, provelem, prj);
							if (funcClass != null) {
								reqPort.setContract(funcClass);
							}
						}

						reqPort.setIsReversed(1);
						if (reqConnector == null) {
							IRPModelElement fpartowner = pkg.findNestedElement(pluginelemname.getName(),
									GlobalVariables.PLUGIN_METACLASS);
							IRPInstance fpart = findPart(fpartowner);
							IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
									GlobalVariables.PROXY_PORT_METACLASS);
							IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
							IRPInstance tpart = findPart(tpartowner);
							IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
									GlobalVariables.PROXY_PORT_METACLASS);
							if (reqConnector == null) {
								 addConnector(swcomponent, fpart, tpart, fromport, toport,
										abscomp.getName(), provelem.getName());
							}
						}

					}

				}
			}
		} catch (Exception e) {
			
			logger.info("Exception while processing Plugin" + plugin.getName());
		}
	}

	private IRPLink addConnector(IRPModelElement fpartowner, IRPInstance fpart, IRPInstance tpart, IRPPort fromport,
			IRPPort toport, String component_name, String interface_name) {
		try {
			IRPLink existingconnector = (IRPLink) fpartowner.findNestedElement(component_name + "__" + interface_name,
					GlobalVariables.CONNECTOR_METACLASS);
			if (existingconnector == null) {
				IRPLink reqConnector = fpart.addLinkToElement(tpart, null, fromport, toport);
				reqConnector.changeTo(GlobalVariables.CONNECTOR_METACLASS);
				reqConnector.setOwner(fpartowner);
				reqConnector.setName(component_name + "__" + interface_name);
			}
		} catch (Exception e) {
			logger.info("Connector already exists");
		}
		return reqconnector;
	}

	private IRPModelElement findSourceandDestination(String componentname) {
	
		String fpath = fullPathMap.get(componentname);
		IRPModelElement fpartowner = pkg.findElementsByFullName(fpath, GlobalVariables.COMPONENT_METACLASS);
		if(fpath != null)
		{
			if (fpartowner == null) {
				fpartowner = pkg.findElementsByFullName(fpath, GlobalVariables.CLASS_METACLASS);
			}
			if (fpartowner == null) {
				fpartowner = pkg.findElementsByFullName(fpath, GlobalVariables.PLUGIN_METACLASS);
			}
			if (fpartowner == null) {
				fpartowner = pkg.findElementsByFullName(fpath, GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
			}
		}
		if((componentname != null) && (fpartowner == null) )
		{
				fpartowner = pkg.findNestedElementRecursive(componentname, GlobalVariables.COMPONENT_METACLASS);
		}
		return fpartowner;
	}

	private void processDelegationConnectors(AbstractComponent abscomp, ProvidedElement provelem,
			AbstractComponent dcomp, IRPUnit subcomponent) {
		IRPModelElement fPartowner = null;
		IRPInstance frompart = null;
		fPartowner = findSourceandDestination(subcomponent.getName());
		if (fPartowner != null) {
			frompart = findPart(fPartowner);
		}
		if (frompart == null) {
			fPartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fPartowner != null) {
				frompart = findPart(fPartowner);
			}
		}
		IRPPort fromport = (IRPPort) fPartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort tport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (tport == null) {
			tport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				tport.setContract(funcClass);
			}
		}
		tport.setIsReversed(1);
		if (reqconnector == null) {
			try {
			reqconnector = addConnector(subcomponent, frompart, tpart, fromport, tport, abscomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
		IRPModelElement mainelement = findSourceandDestination(abscomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(abscomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			try {
			addConnector(swcomponent, tpart, mainpart, tport, mainport, abscomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
		IRPUnit delegateelement = (IRPUnit) findSourceandDestination(dcomp.getName());
		IRPInstance delegatepart = findPart(delegateelement);
		IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPLink delegateconnector = (IRPLink) delegateelement
				.findNestedElement(dcomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
		if (delegateconnector == null) {
			try {
			addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
					abscomp.getName(), provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
	

	}

	private void processTopLevelConnectors(AbstractComponent abscomp, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit swcomponent) {
		
		IRPModelElement fpartOwner = null;
		IRPInstance fpart = null;
		fpartOwner = findSourceandDestination(maincomp.getName());
		if (fpartOwner != null) {
			fpart = findPart(fpartOwner);
		}
		if (fpart == null) {
			fpartOwner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartOwner != null) {
				fpart = findPart(fpartOwner);
			}
		}
		IRPPort fromport = (IRPPort) fpartOwner.findNestedElementRecursive(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		if (tpartowner == null) {
			tpartowner = subcluster.findNestedElementRecursive(swcomponent.getName(), GlobalVariables.CLASS_METACLASS);
		}
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			try {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, abscomp.getName(),
					provelem.getName());
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}

		if (abscomp instanceof SubComponent) {
			IRPModelElement mainele = pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.COMPONENT_METACLASS);
			IRPInstance mainpart = findPart(mainele);
			IRPPort mainport = (IRPPort) mainele.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainport == null) {
				mainport = (IRPPort) mainele.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
				funcClass = getRequiredPortContract(pkg, provelem.getName());
				if (funcClass != null) {
					mainport.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainele.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				try {
				addConnector(mainele, fpart, mainpart, fromport, mainport, abscomp.getName(),
						provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
		if (abscomp instanceof PluginTemplate) {
			IRPModelElement mainEle = pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
			IRPInstance mainpart = findPart(mainEle);
			IRPPort mainport = (IRPPort) mainEle.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainport == null) {
				mainport = (IRPPort) mainEle.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
				funcClass = getRequiredPortContract(pkg, provelem.getName());
				if (funcClass != null) {
					mainport.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainEle.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				try {
				addConnector(mainEle, fpart, mainpart, fromport, mainport, abscomp.getName(),
						provelem.getName());
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
		}

	}

	private IRPInstance findPart(IRPModelElement me) {

		for (Object o : me.getReferences().toList()) {
			if ((o instanceof IRPInstance) && (((IRPInstance) o).getUserDefinedMetaClass().equals("Object"))) {
					return (IRPInstance) o;				
			}
		}
		return null;
	}

	private void processSubProvidedInterfaceBlockAndPort(AccessibleInterface ai, boolean elementPresent, SubComponent subComponent) {
		try {
		if (!elementPresent) {
			if (ai instanceof DelegateInterface) {
				subintfblck = (IRPClass) subsubcomponent.findNestedElement("_" + ((AccessibleInterface) ai).getName(),
						GlobalVariables.DELEGATEINTERFACE_METACLASS);
				if (subintfblck == null) {
					subintfblck = (IRPClass) subsubcomponent.addNewAggr(GlobalVariables.DELEGATEINTERFACE_METACLASS,
							"_" + ((AccessibleInterface) ai).getName());
				}
				port = (IRPPort) subsubcomponent.getOwner().getOwner().findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (port == null) {
					port = (IRPPort) subsubcomponent.getOwner().getOwner().addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							((AccessibleInterface) ai).getName());
				}
				funcClass = (IRPClass) getPortContract(pkg, port, ai, prj);
				port.setContract(funcClass);
			} else {
				subintfblck = (IRPClass) subsubcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
				if (subintfblck == null) {
					subintfblck = (IRPClass) subsubcomponent.addNewAggr(GlobalVariables.INTERFACE_BLOCK_METACLASS,
							((AccessibleInterface) ai).getName());
				}

				port = (IRPPort) subsubcomponent.getOwner().getOwner().findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (port == null) {
					port = (IRPPort) subsubcomponent.getOwner().getOwner().addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							((AccessibleInterface) ai).getName());
				}
				funcClass = (IRPClass) getIntfBlockFunctionClass(pkg, port, (IRPProject) prj);
				port.setContract(funcClass);
			}
		}
		} catch (Exception e) {			
			logger.info("Exception while processing Provided Elements of Component" + subComponent.getName() + "\n" + e.toString());
		}
	}

	public void openProject(String selectedFilePath, boolean isGuiOpen, String rhpExePath) {
		File prj_file = new File(selectedFilePath);
		if (prj_file.exists()) {
			if(!isGuiOpen){
	               app = RhapsodyAppServer.createRhapsodyApplication();
	               prj = app.openProject(selectedFilePath);
	        }else{
		      try {
			       rhpProces   = Runtime.getRuntime().exec(rhpExePath+"\\" + "rhapsody.exe -hiddenui");
			        while (app == null)
			        {
			          
				      logger.info("Waiting for Rhapsody...");
				      Thread.sleep(1000);
				      app = RhapsodyAppServer.getActiveRhapsodyApplication();
			          logger.info("Got Rhapsody Instance...");
			         
			        }
	            prj = app.openProject(selectedFilePath);
	            Thread.sleep(10000);
	            logger.info("Rhapsody model is opened...");
		}catch (IOException | InterruptedException e) {
			logger.error("Unable to get Rhapsody instance or unable to open Project");
			 Thread.currentThread().interrupt();
			}
	    }
			pkg = (IRPPackage) prj.findNestedElement("Model", GlobalVariables.PACKAGE_METACLASS);
			if (pkg == null) {
				pkg = (IRPPackage) prj.addNewAggr(GlobalVariables.PACKAGE_METACLASS, "Model");
			}
			compositeStereotype = (IRPStereotype) prj.findNestedElementRecursive(GlobalVariables.COMPOSITE_METACLASS,
					GlobalVariables.STEREOTYPE_METACLASS);
			thirdpartyStereotype = (IRPStereotype) prj.findNestedElementRecursive(GlobalVariables.THIRDPARTY_METACLASS,
					GlobalVariables.STEREOTYPE_METACLASS);

		}
	}

	public void processPartition(Partition p, Software sw) {

		partition = (IRPUnit) checkifElementExists(pkg, ((Partition) p).getName(), GlobalVariables.PACKAGE_METACLASS);

		if (partition == null) {
			partition = (IRPUnit) addElementtoModel(pkg, GlobalVariables.PACKAGE_METACLASS, ((Partition) p).getName());
		} else {
			logger.info("Partition : " + p.getName() + " already exists");
		}

		subpartition = (IRPClass) checkifElementExists(partition, ((Partition) p).getName(),
				GlobalVariables.PARTITION_METACLASS);
		if (subpartition == null) {
			subpartition = (IRPUnit) addElementtoModel(partition, GlobalVariables.PARTITION_METACLASS,
					((Partition) p).getName());
			String description = p.getDesc();
			String label = p.getLongName();
			subpartition.setDescription(description);
			subpartition.setDisplayName(label);
		}

		softwarePart = (IRPInstance) checkifElementExists(software,
				GlobalVariables.PART_KEYWORD + ((Partition) p).getName(), GlobalVariables.PART_USER_METACLASS);
		if (softwarePart == null) {
			softwarePart = (IRPInstance) addElementtoModel(software, GlobalVariables.PART_USER_METACLASS,
					GlobalVariables.PART_KEYWORD + ((Partition) p).getName());
			softwarePart.setOtherClass((IRPClassifier) subpartition);
		}
	}

	public void processCluster(Cluster cl, Partition p) {
		try {
			cluster = (IRPUnit) checkifElementExists(partition, ((Cluster) cl).getName(),
					GlobalVariables.PACKAGE_METACLASS);

			if (cluster == null) {
				cluster = (IRPUnit) addElementtoModel(partition, GlobalVariables.PACKAGE_METACLASS,
						((Cluster) cl).getName());
			} else {
				logger.info("Cluster : " + cl.getName() + " already exists");
			}

			subcluster = (IRPClass) checkifElementExists(cluster, ((Cluster) cl).getName(),
					GlobalVariables.CLUSTER_METACLASS);
			if (subcluster == null) {
				subcluster = (IRPUnit) addElementtoModel(cluster, GlobalVariables.CLUSTER_METACLASS,
						((Cluster) cl).getName());
				String description = cl.getDesc();
				String label = cl.getLongName();
				subcluster.setDescription(description);
				subcluster.setDisplayName(label);

			}
			
			IRPTag rl = (IRPTag) checkifElementExists(subcluster, GlobalVariables.ARCHITECT_TAG,
					GlobalVariables.TAG_METACLASS);
			if (rl == null) {
				rl = (IRPTag) addElementtoModel(subcluster, GlobalVariables.TAG_METACLASS,
						GlobalVariables.ARCHITECT_TAG);
				String archi = cl.getArchitect();
				if (archi != null) {
					subcluster.setTagValue(rl, archi);
				}
			}
			IRPTag reqid = (IRPTag) checkifElementExists(subcluster, GlobalVariables.DOCID_TAG,
					GlobalVariables.TAG_METACLASS);
			if (reqid == null) {
				addElementtoModel(subcluster, GlobalVariables.TAG_METACLASS,
						GlobalVariables.DOCID_TAG);

			}
			partitionPart = (IRPInstance) checkifElementExists(subpartition,
					GlobalVariables.PART_KEYWORD + ((Cluster) cl).getName(), GlobalVariables.PART_USER_METACLASS);
			if (partitionPart == null) {
				partitionPart = (IRPInstance) subpartition.addNewAggr(GlobalVariables.PART_USER_METACLASS,
						GlobalVariables.PART_KEYWORD + ((Cluster) cl).getName());
				partitionPart.setOtherClass((IRPClassifier) subcluster);
			}
		} catch (Exception e) {
			
			logger.info("Error in processing Cluster" + cl.getName());
		}
	}

	@SuppressWarnings({ "unused", "null" })
	public IRPClass getIntfBlockFunctionClass(IRPModelElement owner, IRPPort p, IRPProject app) {
		IRPCollection groups = null;
		IRPModelElement lastOwner = null;
		if (owner != null) {			
				lastOwner = owner;
			if (lastOwner.equals(owner)) {
				// get the interface block from the map
				if (groups == null) {
					groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
					funcClass = getFuncClassInterfaceBlock(p, groups);

				} else {
					funcClass = getFuncClassInterfaceBlock(p, groups);
					String funcname = funcClass.getName();
				}
			} 
			else {
				groups.empty();
				groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
				funcClass = getFuncClassInterfaceBlock(p, groups);
			}
		}
		if (funcClass == null) {
			funcClass = app.findClass(lastOwner.getName());
		}
		return funcClass;
	}

	private IRPClass getRequiredPortContract(IRPModelElement owner, String intfblck) {
		
		funcClass = (IRPClass) owner.findNestedElementRecursive(intfblck, GlobalVariables.INTERFACE_BLOCK_METACLASS);
		if (funcClass == null) {
			funcClass = (IRPClass) owner.findNestedElementRecursive("d_" + intfblck,
					GlobalVariables.DELEGATEINTERFACE_METACLASS);
		}
		return funcClass;
	}

	private IRPClass getPortContractforDelegate(IRPModelElement owner, IRPClass intfblck) {
		
		if (intfblck != null) {
			funcClass = (IRPClass) owner.findNestedElementRecursive(intfblck.getName(),
					GlobalVariables.DELEGATEINTERFACE_METACLASS);
		}
		return funcClass;

	}

	@SuppressWarnings({ "unused", "null" })
	public IRPClass getPortContract(IRPModelElement owner, IRPPort p, ProvidedElement provelem, IRPProject app) {
		IRPCollection groups = null;
		IRPModelElement lastOwner = null;
		if (owner != null) {
				lastOwner = owner;
			if (lastOwner.equals(owner)) {
				// get the interface block7 from the map
				if (groups == null) {
					groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
					funcClass = getPortContractFuncClass(p, provelem, groups);

				} else {
					funcClass = getPortContractFuncClass(p, provelem, groups);
					String funcname = funcClass.getName();
				}
			} else {
				groups.empty();
				groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
				funcClass = getPortContractFuncClass(p, provelem, groups);
			}
		}
		if (funcClass == null) {
			funcClass = app.findClass(lastOwner.getName());
		}
		return funcClass;
	}

	private IRPClass getPortContractFuncClass(IRPPort p, ProvidedElement provelem,
			IRPCollection groups) {
		IRPClass funClass = null;
		for (int i = 1; i <= groups.getCount(); i++) {
			IRPClass group = (IRPClass) groups.getItem(i);
			if (group.getUserDefinedMetaClass() == null) {
				logger.info("null for " + p.getName());
			}

			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.INTERFACE_BLOCK_METACLASS)) {
				funClass = (IRPClass) group.findNestedElementRecursive(provelem.getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
			}
			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.DELEGATEINTERFACE_METACLASS)) {
				funClass = (IRPClass) group.findNestedElement(provelem.getName(),
						GlobalVariables.DELEGATEINTERFACE_METACLASS);
			}

			if (funClass != null) {
				break;
			}

		}
		return funClass;
	}

	@SuppressWarnings({ "unused", "null" })
	public IRPClass getIntfBlockFunctionClas(IRPModelElement owner, IRPPort p, AbstractComponent abscomp,
			ProvidedElement provelem, IRPProject app) {

		IRPCollection groups = null;
		IRPModelElement lastOwner = null;
		String ownername = owner.getName();

				lastOwner = owner;
			
			if (lastOwner.equals(owner)) {
				// get the interface block from the map
				String lastownername = lastOwner.getName();
				if (groups == null) {
					groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
					funcClass = getFuncClasInterfaceBlock(provelem, groups);

				} else {
					funcClass = getFuncClasInterfaceBlock(provelem, groups);
					String funcname = funcClass.getName();
				}
			} else {
				groups.empty();
				groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
				funcClass = getFuncClasInterfaceBlock(provelem, groups);
			}
		if (funcClass == null) {
			funcClass = app.findClass(lastOwner.getName());
		}
		return funcClass;
	}

	private IRPClass getFuncClassInterfaceBlock(IRPPort p, IRPCollection groups) {
		IRPClass funClass = null;
		for (int i = 1; i <= groups.getCount(); i++) {
			IRPClass group = (IRPClass) groups.getItem(i);
			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.INTERFACE_BLOCK_METACLASS)) {
				funClass = (IRPClass) group.findNestedElementRecursive(p.getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
			}
			if (funClass != null) {
				break;
			}
		}
		return funClass;
	}

	private IRPClass getFuncClasInterfaceBlock(ProvidedElement provelem, IRPCollection groups) {
		IRPClass funClass = null;
		@SuppressWarnings("unchecked")
		List<IRPModelElement> groupList = groups.toList();
		IRPModelElement group = groupList.stream().filter(x -> provelem.getName().equalsIgnoreCase(x.getName()))
				.findAny().orElse(null);
		if (group != null) {
			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.PLUGIN_TEMPLATE_METACLASS)) {
				funClass = (IRPClass) group.findNestedElementRecursive(provelem.getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
			}
			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.INTERFACE_BLOCK_METACLASS)) {
				funClass = (IRPClass) group.findNestedElementRecursive(provelem.getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
			}
			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.DELEGATEINTERFACE_METACLASS)) {
				funClass = (IRPClass) group.findNestedElementRecursive(provelem.getName(),
						GlobalVariables.DELEGATEINTERFACE_METACLASS);
			}
		}

		return funClass;
	}

	public boolean hasConveyed(IRPFlow flow, IRPModelElement conveyed) {

		IRPCollection co = flow.getConveyed();
		for (int i = 1; i <= co.getCount(); ++i) {

			Object o = co.getItem(i);

			if ((o instanceof IRPModelElement) && (conveyed.equals(o)) ) {
					return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @return
	 */
	public static CidlCollectorHandler getInstance() {

		// Only instantiate the object when needed.
		if (cidlCollectorInstance == null) {
			cidlCollectorInstance = new CidlCollectorHandler();
		}
		return cidlCollectorInstance;
	}
	/**
	 *
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private boolean createFile(File fileName) throws IOException {
		return fileName.createNewFile();
	}
/**
 *
 * @param directory
 * @return
 */
	public boolean createDirectory(File directory) {
		return directory.mkdir();

	}
	/**
	 *
	 */
	public void saveAndClose() {
		if (app != null) {
			if(prj.isModified() == 1) {
			app.saveAll();}
			app.activeProject().close();
			app.quit();
			if(rhpProces != null)
				rhpProces.destroy();
			logger.info("Rhapsody Instance is closed...");
		}
	}
}
