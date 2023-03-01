package com.contiautomotive.architecture.tool.diagrams;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.*;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com.contiautomotive.architecture.tool.handlers.CidlCollectorHandler;
import com.contiautomotive.cidl.cidl.AlternativeBlock;
import com.contiautomotive.cidl.cidl.AlternativeBlockReference;
import com.contiautomotive.cidl.cidl.ElseBlock;
import com.contiautomotive.cidl.cidl.ElseIfBlock;
import com.contiautomotive.cidl.cidl.HeaderFileStrap;
import com.contiautomotive.cidl.cidl.HexConstant;
import com.contiautomotive.cidl.cidl.IfBlock;
import com.contiautomotive.cidl.cidl.OptionalControlStructure;
import com.contiautomotive.cidl.cidl.ProvidedFunction;
import com.contiautomotive.cidl.cidl.SchedulingBlock;
import com.contiautomotive.cidl.cidl.SchedulingCall;
import com.contiautomotive.cidl.cidl.SchedulingElement;
import com.contiautomotive.cidl.cidl.SchedulingParameterRef;
import com.contiautomotive.cidl.cidl.SchedulingSequence;
import com.contiautomotive.cidl.cidl.StringConstant;
import com.contiautomotive.cidl.cidl.impl.AlternativeBlockImpl;
import com.contiautomotive.cidl.cidl.impl.ArrayTypeImpl;
import com.contiautomotive.cidl.cidl.impl.BaseTypeImpl;
import com.contiautomotive.cidl.cidl.impl.DefineDefImpl;
import com.contiautomotive.cidl.cidl.impl.EnumeratorImpl;
import com.contiautomotive.cidl.cidl.impl.FunctionPointerTypeImpl;
import com.contiautomotive.cidl.cidl.impl.NumericTypeImpl;
import com.contiautomotive.cidl.cidl.impl.ParameterImpl;
import com.contiautomotive.cidl.cidl.impl.PointerTypeImpl;
import com.contiautomotive.cidl.cidl.impl.SchedulingSequenceImpl;
import com.contiautomotive.cidl.cidl.impl.StructTypeImpl;
import com.contiautomotive.cidl.cidl.impl.StubTypeImpl;
import com.contiautomotive.common.GlobalVariables;
import com.contiautomotive.fme.cvm.fme.FeatureType;
import com.contiautomotive.strapbase.fREx.BinaryOperation;
import com.contiautomotive.strapbase.fREx.Expression;
import com.contiautomotive.strapbase.fREx.IntegerConstant;
import com.contiautomotive.strapbase.fREx.Operator;
import com.contiautomotive.strapbase.fREx.RealConstant;
import com.contiautomotive.strapbase.fREx.StrapRef;
import com.contiautomotive.strapbase.fREx.UnaryOperation;
import com.contiautomotive.strapbase.fREx.impl.HexConstantImpl;
import com.contiautomotive.strapbase.fREx.impl.IntegerConstantImpl;
import com.contiautomotive.strapbase.fREx.impl.RealConstantImpl;
import com.google.common.base.Objects;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPClassifierRole;
import com.telelogic.rhapsody.core.IRPCollaboration;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPGraphEdge;
import com.telelogic.rhapsody.core.IRPGraphElement;
import com.telelogic.rhapsody.core.IRPGraphNode;
import com.telelogic.rhapsody.core.IRPGraphicalProperty;
import com.telelogic.rhapsody.core.IRPInteractionOccurrence;
import com.telelogic.rhapsody.core.IRPInteractionOperand;
import com.telelogic.rhapsody.core.IRPInteractionOperator;
import com.telelogic.rhapsody.core.IRPInterfaceItem;
import com.telelogic.rhapsody.core.IRPMessage;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPSequenceDiagram;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPUnit;

public class GenerateSequenceDiagram {
	private static final Logger logger = LogManager.getLogger(GenerateSequenceDiagram.class);
	Set<String> validSequenceDiagrams = null;
	int lastXPos;
	int lastYPos;
	int lastYPosAltInSequence = 0;
	String optCtrlStruStrapName = null;
	private boolean isEnvLowlength = false;
	private String envName = "";
	private boolean isConraintAvailable = false;
    private static String cidlComponentFileName = null;
	private static GenerateSequenceDiagram singleInstance = null;
	IRPSequenceDiagram sequenceDiagram =null;
	String callingComponent =null;
	IRPModelElement swcomponent =null;
	String fromComp = null;
	String funName = null;
	String toComp = null;
	String arguments = null;

	private GenerateSequenceDiagram() {

	}

	public static GenerateSequenceDiagram getInstance() {
		if (singleInstance == null)
			singleInstance = new GenerateSequenceDiagram();
		return singleInstance;
	}

	public void checkScheduleDiagram(IRPUnit swcomponent, ProvidedFunction pf, IRPProject prj, Map<String, String> requiredIntfs,
			List<String> cIDLDiagramElements, Map<String, String> whenConditionMap,
			String componentFileName) {
		cidlComponentFileName = componentFileName;
		funName =pf.getName();
		ScheduleCalls sc = checkScheduledElements(pf, requiredIntfs, cIDLDiagramElements, whenConditionMap);
		String inconsistency = null;
		String diaName = pf.getName();
		String desc = pf.getDesc();
		sequenceDiagram = (IRPSequenceDiagram) swcomponent.findNestedElement(diaName,
				GlobalVariables.SEQUENCE_DIAGRAM_METACLASS);
		boolean recreate = recreateDiagram(cIDLDiagramElements, whenConditionMap);
		if (sequenceDiagram == null) {
			repair(swcomponent, pf, prj,  inconsistency, recreate, sc, whenConditionMap);
		} else if (recreate) {
			inconsistency = "ScheduleDiagramNotCorrect";
			repair(swcomponent, pf, prj,  inconsistency, recreate, sc, whenConditionMap);
		} else {
			if(desc != null) {
			checkDescriptionInNote(desc);}
			logger.info(" ScheduleView Diagram already present for : " + pf.getName() + " for the component  => "
					+ swcomponent.getFullPathName());
		}
	}

	private void checkDescriptionInNote(String originalDesc) {
		IRPCollection diagcoll = (IRPCollection) sequenceDiagram.getGraphicalElements();
		for (int i = 1; i <= diagcoll.getCount(); ++i) {
			Object o = diagcoll.getItem(i);
			if (o instanceof IRPGraphNode) {
				IRPGraphNode gele = (IRPGraphNode) o;
				for (Object obj : gele.getAllGraphicalProperties().toList()) {
					IRPGraphicalProperty prop = (IRPGraphicalProperty) obj;

					if("Text".equalsIgnoreCase(prop.getKey()) && prop.getValue().contains("Schedule :")
							&& prop.getValue().contains("Description :"))	{
						if(originalDesc.equals("")){
							originalDesc = originalDesc.replace(""," ");}
						String text = prop.getValue();
						String[] splitDesc = text.split("Description : ");
						if(splitDesc[1] != null) {
						String checkedDesc = splitDesc[1];
						if(!originalDesc.equals(checkedDesc)) {
							logger.info("Update description in note");
							text = text.replace("Description : "+checkedDesc, "Description : "+originalDesc);
							gele.setGraphicalProperty("Text", text);
						}
						}						
					}
				}
			}
		}
		
	}

	private void repair(IRPUnit swcomponent, ProvidedFunction pf, IRPUnit prj, String inconsistency, boolean recreate,
			ScheduleCalls sc, Map<String, String> whenConditionMap) {
		
		String description = pf.getDesc();
		Map<String, Integer> compLocations = new LinkedHashMap<>();
		String function = pf.getName();
		try {
				// deleting the diagram if the inconsistency is to recreate the diagams.
				if (inconsistency != null && inconsistency.equalsIgnoreCase("ScheduleDiagramNotCorrect")) {
					boolean recreateFlag = recreate;
					if (recreateFlag) {
						IRPModelElement elemToDelete = swcomponent.findNestedElementRecursive(function,
								GlobalVariables.SEQUENCE_DIAGRAM_METACLASS);
						boolean isdeleted = clearAllSDiagrams((IRPSequenceDiagram) elemToDelete);
						if (!isdeleted) {
							logger.info("ScheduleView Diagram : " + function
									+ " could not be deleted.Cannot Recreate the diagram.");
						}
					}
				}
				Map<String, Object> mapping = new HashMap<>();
					String diaName = pf.getName();
					
					IRPModelElement owner = prj.getProject();
					logger.info("Generating ScheduleView Diagram for : " + diaName + " for the component  => "
							+ swcomponent.getFullPathName());
					 sequenceDiagram = (IRPSequenceDiagram) swcomponent.addNewAggr(GlobalVariables.SEQUENCE_DIAGRAM_METACLASS,
							diaName);

					sequenceDiagram.setPropertyValue("SequenceDiagram.General.CleanupRealized", "True");
					IRPCollaboration irpColb = sequenceDiagram.getLogicalCollaboration();
					callingComponent = createblocks(irpColb, sequenceDiagram, mapping, sc, owner, swcomponent,
							compLocations);
					addSchedulingElementsToDiagram(pf,sc, compLocations, mapping,whenConditionMap);
					// add stereotype
					IRPStereotype sequenceDiagStereotype = (IRPStereotype) prj.findNestedElementRecursive(
							GlobalVariables.SCHEDULE_DIAGRAM_METACLASS, GlobalVariables.STEREOTYPE_METACLASS);
					if (sequenceDiagStereotype != null) {
						sequenceDiagram.addSpecificStereotype(sequenceDiagStereotype);
					}
					// add note schedule
					addNoteSchedule(compLocations, sequenceDiagram, swcomponent, function,description);
		} catch (Exception e) {
			logger.info("Exception in adding note\n" + e.toString());
		}
	}

	private void addNoteSchedule(Map<String, Integer> compLocations, IRPSequenceDiagram sequenceDiagram,
			 IRPUnit swcomponent, String function, String description) {
		int noteLoc = 0;
		int maxValueInMap = (Collections.max(compLocations.values())); // This will return max value in the Hashmap
		for (Entry<String, Integer> entry : compLocations.entrySet()) { // Itrate through hashmap
			if (entry.getValue() == maxValueInMap) {
				noteLoc = entry.getValue(); // Print the key with max value
			}
		}
		if (noteLoc == 0) {
			noteLoc = 600;
		} else if (isEnvLowlength) {
			noteLoc = noteLoc + 350;
			isEnvLowlength = false;
		}
		if (!envName.isEmpty()) {
			logger.info("Component and Function Name:  " + envName);
		}

		IRPGraphNode noteText = sequenceDiagram.addNewNodeByType("Note", noteLoc + 200, 50, 415, 215);
//		noteText.getModelObject()
		StringBuilder note = new StringBuilder();
		note.append(" Schedule : ");
		note.append("\n");
		note.append("\n");
		note.append(" Function   : ");
		note.append(function);
		note.append("\n");
		note.append(" Component  : ");
		note.append(swcomponent.getName());
		note.append("\n");
		note.append(" cIDL File  : ");
		note.append(cidlComponentFileName);
		note.append("\n");
		note.append(" Generated  : ");
		// date
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
		String formattedDate = sdf.format(date);
		note.append(formattedDate);
		note.append("\n");
		note.append(" Description : ");
		if(description.equals("")) {
			note.append(" ");}
			else {
				note.append(description);}
		noteText.setGraphicalProperty("Text", note.toString());
	}

	private void addSchedulingElementsToDiagram(ProvidedFunction pf, ScheduleCalls sc,
			Map<String, Integer> compLocations,  Map<String, Object> mapping, Map<String, String> whenConditionMap) {
		swcomponent = sequenceDiagram.getOwner();
		fromComp = swcomponent.getName();
		lastXPos = 80;
		lastYPos = 250;
		lastYPosAltInSequence = 0;
		boolean sequenceFlag = false;
		IRPCollaboration irpColb = sequenceDiagram.getLogicalCollaboration();
		String diaName = pf.getName();
		if (!diaName.equalsIgnoreCase(sequenceDiagram.getName())) {
			return;
		}
		boolean alternateFlag = false;
		boolean alternatFlagForAltBlockInseq = false;
		int altHeight = 0;
		int seqBlockhght = 0;

		SchedulingBlock scheduleFun = pf.getScheduling();

		if (scheduleFun != null) {
			try {	
				
				createMainMessage(20,150, 100, 150, mapping, sc);

				EList<SchedulingElement> sclList = scheduleFun.getSchedulingElements();
				for (SchedulingElement schedulingElement : sclList) {
					if (schedulingElement instanceof SchedulingCall) {
						// checking the position as we need to add the message the below the
						// optional/alternate block.
						if (alternateFlag) {
							if (lastYPos < altHeight)
								lastYPos = altHeight;
							alternateFlag = false;
						} else if (sequenceFlag) {
							if (lastYPos < seqBlockhght)
								lastYPos = seqBlockhght;
							sequenceFlag = false;
						}
						lastYPos = lastYPos + 60;
						int i = 10;
						funName = ((SchedulingCall) schedulingElement).getFunction().getReference().getName();
						arguments = "";
						String schedulingElementParamters = "";
						StringBuilder argument = new StringBuilder();
						for (SchedulingParameterRef p : ((SchedulingCall) schedulingElement).getFunction()
								.getParameters()) {
							if (p instanceof SchedulingParameterRef) {
								SchedulingParameterRef param = (SchedulingParameterRef) p;
								EObject paramRef = param.getRef();
								EObject paramConstant = param.getConstant();
								schedulingElementParamters = getFunctionArguments(paramRef, paramConstant);
							}
							argument = argument.append(schedulingElementParamters);
							arguments = argument.toString();
							arguments = arguments.substring(0, arguments.length() - 1);
						}
						toComp = "";
						if (sc.getRequiredIntfs().containsKey(funName)) {
							toComp = sc.getRequiredIntfs().get(funName);
							if (compLocations.containsKey(toComp)) {
								int xloc4 = compLocations.get(toComp);								
								createmessage(lastXPos, lastYPos, xloc4, lastYPos,
										mapping, sc, whenConditionMap);
							} else {
								logger.info(toComp + " is not available in the model.");
							}
						}
						if (toComp.equals("")) {
							toComp = swcomponent.getName();							
							createmessage(lastXPos, lastYPos, lastXPos,
									lastYPos + i + 20, mapping, sc, whenConditionMap);

							lastYPos = lastYPos + i + 20;
						}
					}
					if (schedulingElement instanceof SchedulingSequence) {
						sequenceFlag = true;
						EList<SchedulingElement> seqIMplementation = ((SchedulingSequence) schedulingElement)
								.getSchedulingElements();
						int count = seqIMplementation.size();
						IRPInteractionOperator iop = irpColb.addInteractionOperator();
						iop.setInteractionType("seq");

						int xpos = 70;
						lastYPos = lastYPos + 30;
						int max = Collections.max(compLocations.values());
						int width = max + 100;
						int height = (70 * count) + 80;
						if (lastYPos <= seqBlockhght) {
							if (lastYPos < seqBlockhght)
								lastYPos = seqBlockhght + 30;
						} else if (alternateFlag) {
							if (lastYPos < altHeight)
								lastYPos = altHeight+30;
							alternateFlag = false;
						}
						sequenceDiagram.addNewNodeForElement(iop, xpos, lastYPos + 20, width, height);
						setInteractionConstraint(irpColb, "", "seq");
						seqBlockhght = lastYPos + 20 + height;

						for (SchedulingElement testscheElement : seqIMplementation) {
							if (testscheElement instanceof SchedulingCall) {
								lastYPos = lastYPos + 60;
								int i = 10;
								funName = ((SchedulingCall) testscheElement).getFunction().getReference()
										.getName();
								arguments = "";
								String testscheElementParameters = "";
								StringBuilder argument = new StringBuilder();
								for (SchedulingParameterRef p : ((SchedulingCall) testscheElement).getFunction()
										.getParameters()) {
									if (p instanceof SchedulingParameterRef) {
										SchedulingParameterRef param = (SchedulingParameterRef) p;
										EObject paramRef = param.getRef();
										EObject paramConstant = param.getConstant();
										testscheElementParameters = getFunctionArguments(paramRef, paramConstant);
									}
									argument = argument.append(testscheElementParameters);
									arguments = argument.toString();
									arguments = arguments.substring(0, arguments.length() - 1);
								}
								toComp = "";
								if (sc.getRequiredIntfs().containsKey(funName)) {
									toComp = sc.getRequiredIntfs().get(funName);
									if (compLocations.containsKey(toComp)) {
										int xloc5 = compLocations.get(toComp);
										createmessage(lastXPos, lastYPos, xloc5,
												lastYPos, mapping, sc, whenConditionMap);
									} else {
										logger.info(toComp + " is not available in the model.");
									}

								}
								if (toComp.equals("")) {
									toComp = swcomponent.getName();									
									createmessage(lastXPos, lastYPos, lastXPos,
											lastYPos + i + 20, mapping, sc, whenConditionMap);

									lastYPos = lastYPos + i + 20;
								}
								lastYPosAltInSequence = lastYPos;
							}
                         else if (testscheElement instanceof AlternativeBlockImpl) {
								schedulingElement = testscheElement;
								alternatFlagForAltBlockInseq = true;
								iop.deleteFromProject();
								lastYPos = 20;
							}
						}

					}
					if (schedulingElement instanceof AlternativeBlockImpl) {
						AlternativeBlock abi = (AlternativeBlock) schedulingElement;
						alternateFlag = true;
						String interactionType = null;
						int width = 0;
						int height = 0;
						int xpos = 70;
						List<String> secondIFCalls = new ArrayList<>();
						Map<String, List<String>> secondMap = sc.getSecondIfCalls();
						for (Entry<String, List<String>> entry : secondMap.entrySet()) {
							secondIFCalls.addAll(entry.getValue());
						}
						int firstIfCount = 0;
						if (schedulingElement instanceof AlternativeBlockImpl) {
							IfBlock ifObj = abi.getIf();

							EList<SchedulingElement> expList = ifObj.getIfExp();
							for (SchedulingElement subSchedule : expList) {
								if (subSchedule instanceof SchedulingCall) {
									firstIfCount++;
								}
							}
						}
						if (abi.getElseIfs().size() != 0) {
							interactionType = "alt";
						} else if (abi.getElse() != null) {
							interactionType = "alt";
						} else if (alternatFlagForAltBlockInseq) {
							interactionType = "seq";
						} else {
							interactionType = "opt";
						}

						int count = getOperatorSize(interactionType, firstIfCount, sc);

						String level = "first";
						if (level.equalsIgnoreCase("second")) {
							height = (70 * count) + 180;
							int max = Collections.max(compLocations.values());
							width = max + 100;
						}
						if (level.equalsIgnoreCase("first")) {
							height = (70 * count) + 190;
							int max = Collections.max(compLocations.values());
							width = max + 100;
						}

						if (schedulingElement instanceof AlternativeBlockImpl) {
							IfBlock ifObj = abi.getIf();
							if (ifObj != null) {

								IRPInteractionOperator iop = irpColb.addInteractionOperator();
								iop.setInteractionType(interactionType);
								if (sequenceFlag) {
									if (lastYPos < seqBlockhght)
										lastYPos = seqBlockhght + 30;
									sequenceFlag = false;
								}
                                if(alternatFlagForAltBlockInseq ) {
                                	lastYPos=lastYPos - 100;
                                }
								if (alternateFlag && lastYPos < altHeight) {
										lastYPos = altHeight;
								}
								altHeight = lastYPos + height + 30 + 20;

								// setting the constraint name
								AlternativeBlockReference abr = ifObj.getCondition();
								ProvidedFunction pfRef = abr.getReference();
								String constraintName = "";
								if (pfRef.eResource() != null) {
									ProvidedFunction prvFunc = (ProvidedFunction) EcoreUtil.resolve(pfRef,
											pfRef.eResource().getResourceSet());
									constraintName = prvFunc.getName();
								}
								logger.info("ConstraintName: " + constraintName + " CompName:  " + irpColb.getName());
								IRPInteractionOperand intrcOperandZero = (IRPInteractionOperand) iop
										.addNewAggr(GlobalVariables.InteractionOperand_METACLASS, constraintName);
								intrcOperandZero.setInteractionConstraint(constraintName);
								intrcOperandZero.setDisplayName(constraintName);
								lastYPos = lastYPos + 60;
								sequenceDiagram.addNewNodeForElement(iop, xpos, lastYPos, width, height);
								if (pfRef.eResource() != null) {
									// set the constraint name
									IRPInteractionOperator iopLevelSecond = null;
									// collecting sub-sequence calls
									EList<SchedulingElement> expList = ifObj.getIfExp();
									for (SchedulingElement subSchedule : expList) {
										if (subSchedule instanceof AlternativeBlock) {
											AlternativeBlock ab = (AlternativeBlock) subSchedule;
											// taking second if block.
											IfBlock ifblck = ab.getIf();
											if (ifblck != null) {
												AlternativeBlockReference abRef = ifblck.getCondition();
												ProvidedFunction provFunc = abRef.getReference();
												if (provFunc.eResource() != null) {
													ProvidedFunction func = (ProvidedFunction) EcoreUtil
															.resolve(provFunc, provFunc.eResource().getResourceSet());
													String secondLevelConstraintName = "";
													secondLevelConstraintName = func.getName();
													// going for secong level..
													if (!secondIFCalls.isEmpty()) {
														// add a new interaction operator.
														if (!sc.getSecondElseCalls().isEmpty()) {
															interactionType = "alt";
														} else {
															interactionType = "opt";
														}

														iopLevelSecond = irpColb.addInteractionOperator();
														iopLevelSecond.setInteractionType(interactionType);
														IRPInteractionOperand intrcOperandLevelSecond = (IRPInteractionOperand) iopLevelSecond
																.addNewAggr(GlobalVariables.InteractionOperand_METACLASS,
																		secondLevelConstraintName);
														intrcOperandLevelSecond
																.setInteractionConstraint(secondLevelConstraintName);
														intrcOperandLevelSecond
																.setDisplayName(secondLevelConstraintName);
														// calculate secondblock height
														int innercount = secondIFCalls.size()
																+ sc.getSecondElseCalls().size()
																+ sc.getSecondElseIfCalls().size();
														height = (innercount * 70) + 60;
														lastYPos = lastYPos + 50;
														sequenceDiagram.addNewNodeForElement(iopLevelSecond, xpos + 70,
																lastYPos, width - 110, height); // removed -100
														// create message
														lastYPos = lastYPos + 10;

														EList<SchedulingElement> ifList = ifblck.getIfExp();
														for (SchedulingElement ele : ifList) {
															if (ele instanceof SchedulingCall) {
																// generating secondlevel
																SchedulingCall scheduleCall = (SchedulingCall) ele;
																if (scheduleCall.getFunction().getReference() != null) {
																	funName = scheduleCall.getFunction()
																			.getReference().getName();
																	arguments = "";
																	String eleParameters = "";
																	StringBuilder argument = new StringBuilder();
																	for (SchedulingParameterRef p : scheduleCall
																			.getFunction().getParameters()) {
																		if (p instanceof SchedulingParameterRef) {
																			SchedulingParameterRef param = (SchedulingParameterRef) p;
																			EObject paramRef = param.getRef();
																			EObject paramConstant = param.getConstant();
																			eleParameters = getFunctionArguments(paramRef,
																					paramConstant);
																		}
																		argument = argument.append(eleParameters);
																		arguments = argument.toString();
																		arguments = arguments.substring(0,
																				arguments.length() - 1);
																	}

																	toComp = "";
																	lastYPos = lastYPos + 35;
																	int i = 10;
																	if (sc.getRequiredIntfs().containsKey(funName)) {
																		toComp = sc.getRequiredIntfs().get(funName);
																		if (compLocations.containsKey(toComp)) {
																			int xloc6 = compLocations.get(toComp);
																			if (!constraintName.isEmpty()) {
																				lastYPos = lastYPos + 20;
																				isConraintAvailable = true;
																			}
																			createmessage(lastXPos,
																					lastYPos, xloc6 , lastYPos,
																					mapping, sc, whenConditionMap);
																			
																		} else {
																			logger.info(toComp
																					+ " is not available in the model.");
																		}

																	}
																	if (toComp.equals("")) {
																		toComp = swcomponent.getName();
																		if (!constraintName.isEmpty()) {
																			isConraintAvailable = true;
																		}
																		createmessage(100, lastYPos, 100,
																				lastYPos + 20 + i, mapping, sc,
																				whenConditionMap);
																	}

																	lastYPos = lastYPos + i + 40;
																}
															}
														}

													}
												}

											} // second if block over
												// second else block..
											ElseBlock eb = ab.getElse();
											if (eb != null) {

												// draw the operator..
												IRPInteractionOperand operaElse = (IRPInteractionOperand) iopLevelSecond
														.addNewAggr(GlobalVariables.InteractionOperand_METACLASS, "");
												operaElse.setInteractionConstraint("else");
												sequenceDiagram.addNewNodeByType("Interaction Operand", xpos + 70,
														lastYPos + 30, 500, 350);
												String constraintFuncName = "else";
												setInteractionConstraint(irpColb, constraintFuncName,
														"alt");
											
												lastYPos = lastYPos + 10;

												EList<SchedulingElement> elseExp = eb.getElseExp();
												for (SchedulingElement elsSchedEle : elseExp) {
													if (elsSchedEle instanceof SchedulingCall) {
														SchedulingCall scheduleCallElse = (SchedulingCall) elsSchedEle;
														funName = scheduleCallElse.getFunction().getReference()
																.getName();
														arguments = "";
														String schedEleParameters = "";
														StringBuilder argument = new StringBuilder();
														for (SchedulingParameterRef p : scheduleCallElse.getFunction()
																.getParameters()) {
															if (p instanceof SchedulingParameterRef) {
																SchedulingParameterRef param = (SchedulingParameterRef) p;
																EObject paramRef = param.getRef();
																EObject paramConstant = param.getConstant();
																schedEleParameters = getFunctionArguments(paramRef, paramConstant);
															}
															argument = argument.append(schedEleParameters);
															arguments = argument.toString();
															arguments = arguments.substring(0, arguments.length() - 1);
														}

														toComp = "";
														int i = 10;
														lastYPos = lastYPos + 35;
														if (sc.getRequiredIntfs().containsKey(funName)) {
															toComp = sc.getRequiredIntfs().get(funName);
															if (compLocations.containsKey(toComp)) {
																int xloc7 = compLocations.get(toComp);
																if (!constraintName.isEmpty()) {
																	lastYPos = lastYPos + 20;
																	isConraintAvailable = true;
																}
																createmessage(lastXPos, lastYPos,
																		xloc7 , lastYPos, mapping, sc,
																		whenConditionMap);
															} else {
																logger.info(
																		toComp + " is not available in the model.");
															}

														}
														if (toComp.equals("")) {
															toComp = swcomponent.getName();
															if (!constraintName.isEmpty()) {
																isConraintAvailable = true;
															}
															createmessage(100, lastYPos, 100,
																	lastYPos + 20 + i, mapping, sc, whenConditionMap);
																	
														}

														lastYPos = lastYPos + i + 40;
													}
												}
											} // second else block over
										} 
										else if (subSchedule instanceof SchedulingCall) {

											// collect first level if calls
											SchedulingCall scheduleCall = (SchedulingCall) subSchedule;
											int i = 10;
											funName = scheduleCall.getFunction().getReference().getName();
											arguments = "";
											String subScheduleEleParameters = "";
											StringBuilder argument = new StringBuilder();
											for (SchedulingParameterRef p : scheduleCall.getFunction()
													.getParameters()) {
												if (p instanceof SchedulingParameterRef) {
													SchedulingParameterRef param = (SchedulingParameterRef) p;
													EObject paramRef = param.getRef();
													EObject paramConstant = param.getConstant();
													subScheduleEleParameters = getFunctionArguments(paramRef, paramConstant);
												}
												if (!subScheduleEleParameters.equals("")) {
													argument = argument.append(subScheduleEleParameters);
													arguments = argument.toString();
													arguments = arguments.substring(0, arguments.length() - 1);
												}
											}
											lastYPos = lastYPos + 50;
											toComp="";
											if (sc.getRequiredIntfs().containsKey(funName)) {
												toComp = sc.getRequiredIntfs().get(funName);

												if (compLocations.containsKey(toComp)) {
													int xloc8 = compLocations.get(toComp);
													if (!constraintName.isEmpty()) 
													{
														lastYPos = lastYPos + 20;
														isConraintAvailable = true;
													}
													createmessage(lastXPos, lastYPos, xloc8 ,
															lastYPos, mapping, sc, whenConditionMap);
												}
												else 
												{
													logger.info(toComp + " is not available in the model.");
												}
											}
											if (toComp.equals("")) 
											{
												toComp = swcomponent.getName();
												if (!constraintName.isEmpty()) {
													isConraintAvailable = true;
												}
												createmessage(100, lastYPos, 100, lastYPos + 20 + i, mapping, sc,
														whenConditionMap);
											}
													
										} else if (subSchedule instanceof SchedulingSequence) {
											EList<SchedulingElement> seqIMplementation = ((SchedulingSequence) subSchedule)
													.getSchedulingElements();
											int seqCount = seqIMplementation.size();
											sequenceFlag = true;
											IRPInteractionOperator iopSeq = irpColb.addInteractionOperator();
											iopSeq.setInteractionType("seq");
											lastYPos = lastYPos + 30;
											int max = Collections.max(compLocations.values());
											int wid = max + 100;
											int len = (70 * seqCount) + 80;
											sequenceDiagram.addNewNodeForElement(iop, xpos + 15, lastYPos + 20,
													wid - 15, len);
											setInteractionConstraint(irpColb, "", "seq");
											seqBlockhght = lastYPos + 20 + len;

											for (SchedulingElement testscheElement : seqIMplementation) {

												if (testscheElement instanceof SchedulingCall) {
													lastYPos = lastYPos + 50;
													int i = 10;
													funName = ((SchedulingCall) testscheElement).getFunction()
															.getReference().getName();
													arguments = "";
													String parameters1 = "";
													StringBuilder argument1 = new StringBuilder();
													for (SchedulingParameterRef p : ((SchedulingCall) testscheElement)
															.getFunction().getParameters()) {
														if (p instanceof SchedulingParameterRef) {
															SchedulingParameterRef param1 = (SchedulingParameterRef) p;
															EObject paramRef1 = param1.getRef();
															EObject paramConstant1 = param1.getConstant();
															parameters1 = getFunctionArguments(paramRef1, paramConstant1);
														}
														argument1 = argument1.append(parameters1);
														arguments = argument1.toString();
														arguments = arguments.substring(0, arguments.length() - 1);
													}
													toComp = "";
													if (sc.getRequiredIntfs().containsKey(funName)) {
														toComp = sc.getRequiredIntfs().get(funName);
														if (compLocations.containsKey(toComp)) {
															int xloc1 = compLocations.get(toComp);
															if (!constraintName.isEmpty()) {
																lastYPos = lastYPos + 20;
																isConraintAvailable = true;
															}
															createmessage(lastXPos,lastYPos, xloc1, lastYPos, mapping, sc,
																	whenConditionMap);
														} else {
															logger.info(toComp + " is not available in the model.");
														}
													}
													if (toComp.equals("")) {
														toComp = swcomponent.getName();
														if (!constraintName.isEmpty()) {
															lastYPos = lastYPos + 20;
															isConraintAvailable = true;
														}
														createmessage(lastXPos, lastYPos, lastXPos, lastYPos + i + 20,
																mapping, sc, whenConditionMap);

														lastYPos = lastYPos + i + 20;
													}
												}
											}
										}
									}
								} // first level if end..
									// FIRST LEVEL ELSEIF
								EList<ElseIfBlock> elseIF = abi.getElseIfs();
								for (ElseIfBlock elsSchedEle : elseIF) {
									IRPInteractionOperand opera1 = (IRPInteractionOperand) iop
											.addNewAggr(GlobalVariables.InteractionOperand_METACLASS, "");
									opera1.setInteractionConstraint(
											elsSchedEle.getCondition().getReference().getName());
									lastYPos = lastYPos + 30;
									sequenceDiagram.addNewNodeByType("Interaction Operand", xpos + 70, lastYPos, 500,
											350);
									// set interactionConstarint
									String constraintFuncName = elsSchedEle.getCondition().getReference().getName();
									setInteractionConstraint(irpColb, constraintFuncName, "alt");
									//
									EList<SchedulingElement> elseifElist = elsSchedEle.getElseifExp();
									for (SchedulingElement elseIFCalls : elseifElist) {

										SchedulingCall elseIFSchedule = (SchedulingCall) elseIFCalls;

										funName = elseIFSchedule.getFunction().getReference().getName();
										arguments = "";
										String elseIFSchedEleParameters = "";
										StringBuilder argument = new StringBuilder();
										for (SchedulingParameterRef p : elseIFSchedule.getFunction().getParameters()) {
											if (p instanceof SchedulingParameterRef) {
												SchedulingParameterRef param = (SchedulingParameterRef) p;
												EObject paramRef = param.getRef();
												EObject paramConstant = param.getConstant();
												elseIFSchedEleParameters = getFunctionArguments(paramRef, paramConstant);
											}
											argument = argument.append(elseIFSchedEleParameters);
											arguments = argument.toString();
											arguments = arguments.substring(0, arguments.length() - 1);
										}
										toComp = "";
										int i = 10;
										lastYPos = lastYPos + 50;
										if (sc.getRequiredIntfs().containsKey(funName)) {
											toComp = sc.getRequiredIntfs().get(funName);
											if (compLocations.containsKey(toComp)) {
												int xloc2 = compLocations.get(toComp);
												if (!constraintName.isEmpty()) {
													lastYPos = lastYPos + 20;
													isConraintAvailable = true;
												}
												createmessage(lastXPos, lastYPos, xloc2 , lastYPos, mapping,
														sc, whenConditionMap);
											} else {
												logger.info(toComp + " is not available in the model.");
											}
										}
										if (toComp.equals("")) {
											toComp = swcomponent.getName();
											if (!constraintName.isEmpty()) {
												isConraintAvailable = true;
											}
											createmessage(lastXPos, lastYPos, lastXPos, lastYPos + 20 + i, mapping,
													sc, whenConditionMap);
										}
									}
								}
								// first level else
								ElseBlock eb = abi.getElse();
								if (eb != null) {
									EList<SchedulingElement> elseExp = eb.getElseExp();
									IRPInteractionOperand opera1 = (IRPInteractionOperand) iop
											.addNewAggr(GlobalVariables.InteractionOperand_METACLASS, "");
									opera1.setInteractionConstraint(sc.getSecondFuncName());
									lastYPos = lastYPos + 30;
									sequenceDiagram.addNewNodeByType("Interaction Operand", xpos + 70, lastYPos, 500,
											350);
									String constraintFuncName = "else";
									setInteractionConstraint(irpColb, constraintFuncName, "alt");
									for (SchedulingElement elsSchedEle : elseExp) {
										if (elsSchedEle instanceof SchedulingCall) {
											SchedulingCall scheduleCall = (SchedulingCall) elsSchedEle;

											funName = scheduleCall.getFunction().getReference().getName();
											arguments = "";
											String elseSchedEleParameters = "";
											StringBuilder argument = new StringBuilder();
											for (SchedulingParameterRef p : scheduleCall.getFunction()
													.getParameters()) {
												if (p instanceof SchedulingParameterRef) {
													SchedulingParameterRef param = (SchedulingParameterRef) p;
													EObject paramRef = param.getRef();
													EObject paramConstant = param.getRef();
													elseSchedEleParameters = getFunctionArguments(paramRef, paramConstant);
												}
												argument = argument.append(elseSchedEleParameters);
												arguments = argument.toString();
												arguments = arguments.substring(0, arguments.length() - 1);
											}

											toComp = "";
											int i = 10;
											lastYPos = lastYPos + 50;
											if (sc.getRequiredIntfs().containsKey(funName)) {
												toComp = sc.getRequiredIntfs().get(funName);
												if (compLocations.containsKey(toComp)) {
													int xloc3 = compLocations.get(toComp);
													if (!constraintName.isEmpty()) {
														lastYPos = lastYPos + 20;
														isConraintAvailable = true;
													}
													createmessage(lastXPos, lastYPos, xloc3 ,
															lastYPos, mapping, sc, whenConditionMap);
												} else {
													logger.info(toComp + " is not available in the model.");
												}
											}
											if (toComp.equals("")) {
												toComp = swcomponent.getName();
												if (!constraintName.isEmpty()) {
													isConraintAvailable = true;
												}
												createmessage(lastXPos, lastYPos, lastXPos, lastYPos + 20 + i,
														mapping, sc, whenConditionMap);
											}

										}
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				logger.info("Exception while adding elements to schedule diagram\n" + e.toString());
			}
		}
	}

	private String getFunctionArguments(EObject paramRef, EObject paramConst) {
		String parameter = "";
		if (paramRef instanceof DefineDefImpl) {
			parameter = getInstance((DefineDefImpl) paramRef);
		} else if (paramRef instanceof EnumeratorImpl) {
			parameter = getInstance((EnumeratorImpl) paramRef);
		} else if (paramRef instanceof ParameterImpl) {
			parameter = getInstance((ParameterImpl) paramRef);
		} else if (paramRef instanceof FunctionPointerTypeImpl) {
			parameter = getInstance((FunctionPointerTypeImpl) paramRef);
		} else if (paramRef instanceof PointerTypeImpl) {
			parameter = getInstance((PointerTypeImpl) paramRef);
		} else if (paramRef instanceof ArrayTypeImpl) {
			parameter = getInstance((ArrayTypeImpl) paramRef);
		} else if (paramRef instanceof StructTypeImpl) {
			parameter = getInstance((StructTypeImpl) paramRef);
		} else if (paramRef instanceof StubTypeImpl) {
			parameter = getInstance((StubTypeImpl) paramRef);
		} else if (paramConst instanceof NumericTypeImpl) {
			parameter = getInstance((NumericTypeImpl) paramConst);
		} else if (paramRef instanceof BaseTypeImpl) {
			parameter = getInstance((BaseTypeImpl) paramRef);
		} else if (paramConst instanceof IntegerConstantImpl) {
			parameter = getInstance((IntegerConstantImpl) paramConst);
		} else if (paramConst instanceof RealConstantImpl) {
			parameter = getInstance((RealConstantImpl) paramConst);
		} else if (paramConst instanceof HexConstantImpl) {
			parameter = getInstance((HexConstantImpl) paramConst);
		} else {
			throw new IllegalArgumentException("Unhandled parameter types: ");
		}
		return parameter;
	}

	private String getInstance(HexConstantImpl param) {
		StringConcatenation builder = new StringConcatenation();
		String val = param.getVal();
		builder.append(val + ",");
		return builder.toString();
	}

	private String getInstance(RealConstantImpl param) {
		StringConcatenation builder = new StringConcatenation();
		double val = param.getVal();
		builder.append(val + ",");
		return builder.toString();
	}

	private String getInstance(IntegerConstantImpl param) {
		StringConcatenation builder = new StringConcatenation();
		BigInteger val = param.getVal();
		builder.append(val + ",");
		return builder.toString();
	}

	private String getInstance(BaseTypeImpl param) {
		String argument = "";
		StringBuilder builder = new StringBuilder();
		argument = param.getName();
		builder.append(argument + ",");
		return builder.toString();
	}

	private String getInstance(NumericTypeImpl param) {
		String argument = "";
		StringBuilder builder = new StringBuilder();
		argument = param.getName();
		builder.append(argument + ",");
		return builder.toString();
	}

	private String getInstance(StubTypeImpl param) {
		String argument = "";
		StringBuilder builder = new StringBuilder();
		argument = param.getName();
		builder.append(argument + ",");
		return builder.toString();
	}

	private String getInstance(StructTypeImpl param) {
		String argument = "";
		StringBuilder builder = new StringBuilder();
		argument = param.getName();
		builder.append(argument + ",");
		return builder.toString();
	}

	private String getInstance(ArrayTypeImpl param) {
		String argument = "";
		StringBuilder builder = new StringBuilder();
		argument = param.getName();
		builder.append(argument + ",");
		return builder.toString();
	}

	private String getInstance(PointerTypeImpl param) {
		String argument = "";
		StringBuilder builder = new StringBuilder();
		argument = param.getName();
		builder.append(argument + ",");
		return builder.toString();
	}

	private String getInstance(FunctionPointerTypeImpl param) {
		String argument = "";
		StringBuilder builder = new StringBuilder();
		argument = param.getName();
		builder.append(argument + ",");
		return builder.toString();
	}

	private String getInstance(EnumeratorImpl param) {
		String argument = "";
		StringBuilder builder = new StringBuilder();
		argument = param.getName();
		builder.append(argument + ",");
		return builder.toString();
	}

	private String getInstance(ParameterImpl param) {
		String argument = "";
		StringBuilder builder = new StringBuilder();
		argument = param.getName();
		builder.append(argument + ",");
		return builder.toString();
	}

	protected String getInstance(DefineDefImpl param) {
		String argument = "";
		StringBuilder builder = new StringBuilder();
		argument = param.getName();
		builder.append(argument + ",");
		return builder.toString();
	}

	private IRPGraphEdge createmessage(int x1, int y1, int x2, int y2, Map<String, Object> mapping, ScheduleCalls sc,
			Map<String, String> whenConditionMap) {
		Object toObj = null;
		Map<String, String> reqIntfs = sc.getRequiredIntfs();
		if (reqIntfs.containsKey(funName)) {
			toObj = mapping.get(reqIntfs.get(funName));
		}
		Object fromObj = mapping.get(fromComp);
		if (toObj == null)
			toObj = mapping.get(toComp);
		IRPClassifierRole src = classifierRoleFromMapping(fromObj);
		IRPGraphElement srcGraphEle = graphElementFromMapping(fromObj);
		IRPClassifierRole trg = classifierRoleFromMapping(toObj);
		IRPGraphElement trgGraphgEle = graphElementFromMapping(toObj);
		IRPMessage msg = sequenceDiagram.getLogicalCollaboration().addMessage(getInterfaceItem(trg, funName), arguments,
				src, trg);
		msg.setReturnValue("");
		msg.setName(funName);

		if (whenConditionMap.get(funName) != null) {
			String constraintName = whenConditionMap.get(funName);

			if (!src.getName().equalsIgnoreCase(trg.getName())) {
				if (src.getName().equalsIgnoreCase("ENV")) {
					x1 = x1 - 95;
					isEnvLowlength = true;
					envName = src.getName() + "  FunctionName: " + funName;
				}
				int length = constraintName.length() * 7;
				length = length + 120;
				addConstraintNameInDiagram(funName, constraintName, sequenceDiagram, x1, y1 - 30, x2 + length, 45);
			} else {
				if (src.getName().equalsIgnoreCase("ENV")) {
					isEnvLowlength = true;
					envName = src.getName() + "  FunctionName: " + funName;
				}
				int length = constraintName.length() * 7;
				length = length + 120;
				int height = y2 - y1;
				addConstraintNameInDiagram(funName, constraintName, sequenceDiagram, x1, y1 - 30, x2 + length,
						height + 45);
			}
			y1 = y1 + 4;
		}
		if (isConraintAvailable) {
			y2 = y2 + 4;
			y1 = y1 + 4;
			lastYPos = lastYPos + 30;
			isConraintAvailable = false;
		}
		sequenceDiagram.save(1);
		return sequenceDiagram.addNewEdgeForElement(msg, (IRPGraphNode) srcGraphEle, x1, y1, (IRPGraphNode) trgGraphgEle, x2, y2);

	}
	
	private IRPGraphEdge createMainMessage(int x1, int y1, int x2, int y2,
			Map<String, Object> mapping, ScheduleCalls sc) {
		Object toObj = null;
		Map<String, String> reqIntfs = sc.getRequiredIntfs();
		if (reqIntfs.containsKey(funName)) {
			toObj = mapping.get(reqIntfs.get(funName));
		}
		Object fromObj = mapping.get(callingComponent);
		if (toObj == null)
			toObj = mapping.get(fromComp); // Env to main component
		IRPClassifierRole src = classifierRoleFromMapping(fromObj);
		IRPGraphElement srcGraphEle = graphElementFromMapping(fromObj);
		IRPClassifierRole trg = classifierRoleFromMapping(toObj);
		IRPGraphElement trgGraphgEle = graphElementFromMapping(toObj);
		IRPMessage msg = sequenceDiagram.getLogicalCollaboration().addMessage(getInterfaceItem(trg, funName), "",
				src, trg);
		msg.setReturnValue("");
		msg.setName(funName);
		sequenceDiagram.save(1);
		return sequenceDiagram.addNewEdgeForElement(msg, (IRPGraphNode) srcGraphEle, x1, y1, (IRPGraphNode) trgGraphgEle, x2, y2);

		
	}


	private IRPInterfaceItem getInterfaceItem(IRPClassifierRole role, String func) {

		return (IRPInterfaceItem) role.getFormalClassifier().findNestedElement(func, "Operation");
	}

	private IRPGraphElement graphElementFromMapping(Object objFrom) {

		return (IRPGraphElement) (((Object[]) objFrom)[0]);
	}

	private IRPClassifierRole classifierRoleFromMapping(Object obj) {

		return (IRPClassifierRole) (((Object[]) obj)[1]);
	}

	private void setInteractionConstraint(IRPCollaboration irpColb,
			String constraintFuncName, String callingClass) {
		List<?> coll = irpColb.getInteractionOperators().toList();
		for (Object object : coll) {
			if (object instanceof IRPInteractionOperator) {

				String interactionType = "";
				interactionType = ((IRPInteractionOperator) object).getInteractionType();
				if (interactionType.equalsIgnoreCase(callingClass)) {
					List<?> intOperand = ((IRPInteractionOperator) object).getInteractionOperands().toList();
					for (Object obj : intOperand) {
						if (obj instanceof IRPInteractionOperand) {
							String constraintCondition = ((IRPInteractionOperand) obj).getInteractionConstraint();
							if (constraintCondition.equalsIgnoreCase("condition"))
								((IRPInteractionOperand) obj).setInteractionConstraint(constraintFuncName);
						}
					}
				}
			}
		}
	}

	private void addConstraintNameInDiagram(String func, String constraintName,
			IRPSequenceDiagram seqDiagram, int x1, int yAxis, int x2, int hight) {
		IRPCollaboration irpColb = seqDiagram.getLogicalCollaboration();
		IRPInteractionOperator constrainOperator = irpColb.addInteractionOperator();
		constrainOperator.setInteractionType("opt");
		IRPInteractionOperand intrcOperandLevelSecond = (IRPInteractionOperand) constrainOperator
				.addNewAggr(GlobalVariables.InteractionOperand_METACLASS, func);
		intrcOperandLevelSecond.setInteractionConstraint(constraintName);
		intrcOperandLevelSecond.setDisplayName(constraintName);
		seqDiagram.addNewNodeForElement(constrainOperator, x1 + 45, yAxis, x2, hight);
	}

	private String createblocks(IRPCollaboration irpColb, IRPSequenceDiagram sequenceDiagram,
			Map<String, Object> mapping, ScheduleCalls sc, IRPModelElement owner, IRPUnit swcomponent,
			Map<String, Integer> compLocations) {

		String classfierRoleComponent = "ENV";
		IRPClassifierRole irpClasRole = irpColb.addSystemBorder();
		IRPGraphElement graphEnv = sequenceDiagram.addNewNodeForElement(irpClasRole, 20, 20, 70, 800);
		mapping.put("ENV", mapping(graphEnv, irpClasRole));

		Set<String> blockList = sc.getComponentBlocks();
		int i = 1;
		if (i == 1) {
			IRPClassifier classes = (IRPClassifier) owner.findNestedElementRecursive(swcomponent.getName(),
					GlobalVariables.COMPONENT_METACLASS);

			if (classes != null) {
				IRPClassifierRole clasRole = irpColb.addClassifierRole(classes.getName(), classes);
				String componentName = classes.getName();
				int componentSize = componentName.length();
				if (componentSize > 1 && componentSize <= 7) {
					IRPGraphNode graphNode = sequenceDiagram.addNewNodeForElement(clasRole, i * (20 + 140), 20, 80, 800);
					graphNode.setGraphicalProperty("Text", ":" + classes.getName()); // need to be named by the class's name.
																				// Otherwise the class is created
					mapping.put(classes.getName(), mapping(graphNode, clasRole));
				} else if (componentSize > 7 && componentSize <= 14) {
					IRPGraphNode graphEle = sequenceDiagram.addNewNodeForElement(clasRole, i * (20 + 140), 20, 110, 800);
					graphEle.setGraphicalProperty("Text", ":" + classes.getName()); 
					mapping.put(classes.getName(), mapping(graphEle, clasRole));
				} else if (componentSize > 14 && componentSize <= 20) {
					IRPGraphNode graphelem = sequenceDiagram.addNewNodeForElement(clasRole, i * (20 + 140), 20, 140, 800);
					graphelem.setGraphicalProperty("Text", ":" + classes.getName());
					mapping.put(classes.getName(), mapping(graphelem, clasRole));
				} else if (componentSize > 20 && componentSize <= 28) {
					IRPGraphNode graphNodeEle = sequenceDiagram.addNewNodeForElement(clasRole, i * (20 + 140), 20, 160, 800);
					graphNodeEle.setGraphicalProperty("Text", ":" + classes.getName());
					mapping.put(classes.getName(), mapping(graphNodeEle, clasRole));
				} else if (componentSize > 28 && componentSize <= 35) {
					IRPGraphNode node = sequenceDiagram.addNewNodeForElement(clasRole, i * (20 + 140), 20, 175, 800);
					node.setGraphicalProperty("Text", ":" + classes.getName()); 
					mapping.put(classes.getName(), mapping(node, clasRole));
				}
				compLocations.put(classes.getName(), i * (20 + 140));
				i++;
			}
		}
		for (String compName : blockList) {

			// if compName is same as the item, then we dont want tocreate another block so
			// checking if its the same than don't add it.
			if (compName.equals(swcomponent.getName())) {
				continue;
			}
			IRPClassifier classes = (IRPClassifier) owner.findNestedElementRecursive(compName,
					GlobalVariables.COMPONENT_METACLASS);
			if (classes == null) {
				classes = (IRPClassifier) owner.getOwner().findNestedElementRecursive(compName,
						GlobalVariables.COMPONENT_METACLASS);
			}
			if (classes != null) {
				IRPClassifierRole clasRole = irpColb.addClassifierRole(classes.getName(), classes);
				String componentName = classes.getName();
				int componentSize = componentName.length();

				if (componentSize > 1 && componentSize <= 7) {
					IRPGraphNode ge1 = sequenceDiagram.addNewNodeForElement(clasRole, i * (20 + 140), 20, 80, 800);
					ge1.setGraphicalProperty("Text", ":" + classes.getName());// need to be named by the class's name.
					                                                           // Otherwise the class is created
					mapping.put(classes.getName(), mapping(ge1, clasRole));
				} else if (componentSize > 7 && componentSize <= 14) {
					IRPGraphNode ge2 = sequenceDiagram.addNewNodeForElement(clasRole, i * (20 + 140), 20, 110, 800);
					ge2.setGraphicalProperty("Text", ":" + classes.getName());
					mapping.put(classes.getName(), mapping(ge2, clasRole));
				} else if (componentSize > 14 && componentSize <= 20) {
					IRPGraphNode ge3 = sequenceDiagram.addNewNodeForElement(clasRole, i * (20 + 140), 20, 135, 800);
					ge3.setGraphicalProperty("Text", ":" + classes.getName());
					mapping.put(classes.getName(), mapping(ge3, clasRole));
				} else if (componentSize > 20 && componentSize <= 28) {
					IRPGraphNode ge4 = sequenceDiagram.addNewNodeForElement(clasRole, i * (20 + 140), 20, 155, 800);
					ge4.setGraphicalProperty("Text", ":" + classes.getName()); 
					mapping.put(classes.getName(), mapping(ge4, clasRole));
				} else if (componentSize > 28 && componentSize <= 35) {
					IRPGraphNode ge5 = sequenceDiagram.addNewNodeForElement(clasRole, i * (20 + 140), 20, 175, 800);
					ge5.setGraphicalProperty("Text", ":" + classes.getName());
					mapping.put(classes.getName(), mapping(ge5, clasRole));
				}
				compLocations.put(compName, i * (20 + 140));
				i++;
	
			}
			// } // else block
		}
		return classfierRoleComponent;
	}

	private Object mapping(IRPGraphElement ge, IRPClassifierRole cr) {

		return new Object[] { ge, cr };
	}

	private boolean clearAllSDiagrams(IRPSequenceDiagram sequenceDiagram) {
		boolean isdeleted = true;
		if (sequenceDiagram != null) {
			try {
				if (checkStereoTypeExists(GlobalVariables.SCHEDULE_DIAGRAM_METACLASS, sequenceDiagram)) {
					sequenceDiagram.deleteFromProject();
					isdeleted = true;
				}
			} catch (Exception e) {
				isdeleted = false;
			}

		}
		return isdeleted;
	}

	public static boolean checkStereoTypeExists(String strName, IRPModelElement elem) {
		if (elem != null) {
			IRPCollection stereoTypesColl = elem.getStereotypes();

			for (Object o : stereoTypesColl.toList()) {
				String name = ((IRPStereotype) o).getName();
				if (strName.equalsIgnoreCase(name))
					return true;
			}
		}
		return false;
	}

	private boolean recreateDiagram(List<String> cIDLDiagramElements,
			Map<String, String> whenConditionMap) {
		boolean recreate = false;
		if (sequenceDiagram != null) {
			List<String> diaElements = collectAllDiagramElements(sequenceDiagram);
			List<String> constrainList = whenConditionMap.values().stream().collect(Collectors.toList());
			cIDLDiagramElements.addAll(constrainList);
			Collection<?> subtractResult = CollectionUtils.subtract(cIDLDiagramElements, diaElements);
			if (!subtractResult.isEmpty()) {
				recreate = true;
			}
		}
		return recreate;
	}

	private List<String> collectAllDiagramElements(IRPSequenceDiagram sequenceDiagram) {
		List<String> diagramElements = new LinkedList<>();
		// getting all the graphical elements from the model.
		IRPCollection diagcoll = sequenceDiagram.getGraphicalElements();
		for (int i = 1; i <= diagcoll.getCount(); ++i) {
			Object o = diagcoll.getItem(i);
			
			if (o instanceof IRPGraphElement) {
				IRPGraphElement gele = (IRPGraphElement) o;
				if (!hasInvalidUDMC(gele.getModelObject()) && gele.getModelObject() != null) {
						if (gele.getModelObject() instanceof IRPClassifierRole) {
							IRPClassifierRole irpRole = (IRPClassifierRole) gele.getModelObject();
							IRPClassifier formalRole = irpRole.getFormalClassifier();
							if (formalRole != null) {
								diagramElements.add(formalRole.getName());
							}
						} 
						
						else if (gele.getModelObject() instanceof IRPInteractionOperator) {
							IRPInteractionOperator irpRole = (IRPInteractionOperator) gele.getModelObject();
							String interactionType = irpRole.getInteractionType();
							if (interactionType != null && interactionType.equalsIgnoreCase("opt")) {
							IRPCollection operands =irpRole.getInteractionOperands();
							Object operand=operands.toList().get(0);
							IRPInteractionOperand constraint = (IRPInteractionOperand) operand;
							String constraintName = constraint.getInteractionConstraint();
							diagramElements.add(constraintName);
							}
						} 
						else {
							diagramElements.add(gele.getModelObject().getName());
						}
				}
			}
		}
		return diagramElements;
	}

	private boolean hasInvalidUDMC(IRPModelElement gele) {
		boolean result = false;
		if ((gele != null) && (gele instanceof IRPInteractionOccurrence || gele instanceof IRPInteractionOperand
				|| (gele.getName().equalsIgnoreCase("ENV")))) {			
				result = true;
		}
		return result;

	}

	public static boolean listsAreEquivelent(List<String> cIDLElements, List<String> modelElements) {
		boolean recreate = false;
		for (String str : modelElements) {
			if (modelElements.contains(str)) {
				cIDLElements.remove(str);
			}
		}
		if (!cIDLElements.isEmpty()) {
			recreate = true;
		}
		return recreate;
	}

	public ScheduleCalls checkScheduledElements(ProvidedFunction pf, Map<String, String> requiredIntfs,
			List<String> cIDLDiagramElements, Map<String, String> whenConditionMap) {
		lastXPos = 80;
		lastYPos = 250;
		lastYPosAltInSequence = 0;
		HashMap<String, List<String>> seqListMap = new HashMap<>();
		TreeSet<String> componentBlocks = new TreeSet<>();
		ScheduleCalls sc = new ScheduleCalls();
		int ifSeqCount = 0;
		SchedulingBlock scheduleFun = pf.getScheduling();
		if (scheduleFun != null) {
			cIDLDiagramElements.add(pf.getName());
			List<String> seqcalls = new ArrayList<>();
			EList<SchedulingElement> sclList = scheduleFun.getSchedulingElements();
			try {
				for (SchedulingElement schedulingElement : sclList) {
					if (schedulingElement instanceof SchedulingCall) {
						String funcName = ((SchedulingCall) schedulingElement).getFunction().getReference().getName();
						OptionalControlStructure optionalControlStructure = (OptionalControlStructure) ((SchedulingCall) schedulingElement)
								.getStrapControlStructure();
						if (optionalControlStructure != null) {
							Expression exp = ((OptionalControlStructure) optionalControlStructure)
									.getStrapExpression();
							optCtrlStruStrapName = toCString(exp);
							whenConditionMap.put(funcName, optCtrlStruStrapName);
						}
						cIDLDiagramElements.add(funcName);
						if (requiredIntfs.containsKey(funcName)) {
							String component = requiredIntfs.get(funcName);
							componentBlocks.add(component);
							if (!cIDLDiagramElements.contains(component)) {
								cIDLDiagramElements.add(component);}
						}
						String scheduleFunc = ((SchedulingCall) schedulingElement).getFunction().getReference()
								.getName();
						if (scheduleFunc != null && !scheduleFunc.isEmpty())
							seqcalls.add(((SchedulingCall) schedulingElement).getFunction().getReference().getName());
						seqListMap.put(pf.getName(), seqcalls);
					}
					if (schedulingElement instanceof SchedulingSequenceImpl) {
						EList<SchedulingElement> seqIMplementation = ((SchedulingSequenceImpl) schedulingElement)
								.getSchedulingElements();

						for (SchedulingElement testscheElement : seqIMplementation) {
							if (testscheElement instanceof SchedulingCall) {

								String funcName = ((SchedulingCall) testscheElement).getFunction().getReference()
										.getName();
								OptionalControlStructure optionalControlStructure = (OptionalControlStructure) ((SchedulingCall) testscheElement)
										.getStrapControlStructure();
								if (optionalControlStructure != null) {
									Expression expr = ((OptionalControlStructure) optionalControlStructure)
											.getStrapExpression();
									optCtrlStruStrapName = toCString(expr);
									whenConditionMap.put(funcName, optCtrlStruStrapName);
								}
								cIDLDiagramElements.add(funcName);
								if (requiredIntfs.containsKey(funcName)) {
									String comp = requiredIntfs.get(funcName);
									componentBlocks.add(comp);
									if (!cIDLDiagramElements.contains(comp)) {
										cIDLDiagramElements.add(comp);}
								}

							}
							else if(testscheElement instanceof AlternativeBlockImpl) {
								schedulingElement = testscheElement;	
							}
						}

					}

					if (schedulingElement instanceof AlternativeBlockImpl) {
						// one way
						sc.setAlternativeblock(true);
						HashMap<String, List<String>> innerCalls = new HashMap<>();
						AlternativeBlock abi = (AlternativeBlock) schedulingElement;
						IfBlock ifObj = abi.getIf();
						if (ifObj != null) {
							AlternativeBlockReference abr = ifObj.getCondition();
							ProvidedFunction pfRef = abr.getReference();
							if (pfRef.eResource() != null) {
								ProvidedFunction pfunc = (ProvidedFunction) EcoreUtil.resolve(pfRef,
										pfRef.eResource().getResourceSet());
								if (pfunc != null)
									sc.setMainfuncname(pfunc.getName());
								// collecting sub-sequence calls
								EList<SchedulingElement> expList = ifObj.getIfExp();
								List<String> firstIFCalls = new ArrayList<>();
								HashMap<String, List<String>> firstIfMaps = new HashMap<>();
								for (SchedulingElement subSchedule : expList) {
									if (subSchedule instanceof AlternativeBlock) {
										AlternativeBlock ab = (AlternativeBlock) subSchedule;
										// taking second if block.
										IfBlock ifblck = ab.getIf();
										if (ifblck != null) {
											AlternativeBlockReference abRef = ifObj.getCondition();
											ProvidedFunction provFunc = abRef.getReference();
											if (provFunc.eResource() != null) {
												ProvidedFunction providedFunc = (ProvidedFunction) EcoreUtil
														.resolve(pfRef, pfRef.eResource().getResourceSet());
												if (providedFunc != null) {
													String secondFunc = providedFunc.getName();
													EList<SchedulingElement> ifList = ifblck.getIfExp();
													List<String> secondSeqCalls = new ArrayList<>();
													for (SchedulingElement ele : ifList) {
														if (ele instanceof SchedulingCall) {
															SchedulingCall scheduleCall = (SchedulingCall) ele;
															if (scheduleCall.getFunction().getReference() != null) {
																String funcName = scheduleCall.getFunction()
																		.getReference().getName();
																OptionalControlStructure optionalControlStructure = (OptionalControlStructure) ((SchedulingCall) scheduleCall)
																		.getStrapControlStructure();
																if (optionalControlStructure != null) {
																	Expression expression = ((OptionalControlStructure) optionalControlStructure)
																			.getStrapExpression();
																	optCtrlStruStrapName = toCString(expression);
																	whenConditionMap.put(funcName, optCtrlStruStrapName);
																}
																cIDLDiagramElements.add(funcName);
																if (requiredIntfs.containsKey(funcName)
																		&& funcName != null) {
																	String comp = requiredIntfs.get(funcName);
																	componentBlocks.add(comp);
																	if (!cIDLDiagramElements.contains(comp)) {
																		cIDLDiagramElements.add(comp);}
																}
																if (funcName != null)
																	secondSeqCalls.add(funcName);
															}
														}
														// call for another instanceof Alternativeblock.
													}
													// store it in secondif calls.
													innerCalls.put(secondFunc, secondSeqCalls);
													sc.getSecondIfCalls().putAll(innerCalls);
												}
											}
										}
										// getelse
										ElseBlock eb = ab.getElse();
										if (eb != null) {
											List<String> alteranteElseCalls = new ArrayList<>();
											EList<SchedulingElement> elseExp = eb.getElseExp();
											for (SchedulingElement elsSchedEle : elseExp) {
												if (elsSchedEle instanceof SchedulingCall) {
													SchedulingCall scheduleCall = (SchedulingCall) elsSchedEle;
													if (scheduleCall.getFunction().getReference() != null) {
													
													String funcName = scheduleCall.getFunction().getReference()
															.getName();
													OptionalControlStructure optionalControlStructure = (OptionalControlStructure) ((SchedulingCall) scheduleCall)
															.getStrapControlStructure();
													if (optionalControlStructure != null) {
														Expression strap = ((OptionalControlStructure) optionalControlStructure)
																.getStrapExpression();
														optCtrlStruStrapName = toCString(strap);
														whenConditionMap.put(funcName, optCtrlStruStrapName);
													}
													cIDLDiagramElements.add(funcName);
													if (requiredIntfs.containsKey(funcName)) {
														String compName = requiredIntfs.get(funcName);
														componentBlocks.add(compName);
														if (!cIDLDiagramElements.contains(compName)) {
															cIDLDiagramElements.add(compName);}
													}
													if (funcName != null)
														alteranteElseCalls.add(funcName);
													}
												}
											}
											// put in list
											sc.getSecondElseCalls().addAll(alteranteElseCalls);
										}
										// elseif
										EList<ElseIfBlock> elseIF = ab.getElseIfs();
										if (elseIF != null && elseIF.size() == 0) {
											List<String> alteranteElseIFCalls = new ArrayList<>();
											for (ElseIfBlock elsSchedEle : elseIF) {
												String constraintFuncName = elsSchedEle.getCondition().getReference()
														.getName();
												cIDLDiagramElements.add(constraintFuncName);
												if (elsSchedEle instanceof SchedulingCall) {
													SchedulingCall scheduleCall = (SchedulingCall) elsSchedEle;
													if (scheduleCall.getFunction().getReference() != null) {
														
													String funcName = scheduleCall.getFunction().getReference()
															.getName();
													OptionalControlStructure optionalControlStructure = (OptionalControlStructure) ((SchedulingCall) scheduleCall)
															.getStrapControlStructure();
													if (optionalControlStructure != null) {
														Expression strapExp = ((OptionalControlStructure) optionalControlStructure)
																.getStrapExpression();
														optCtrlStruStrapName = toCString(strapExp);
														whenConditionMap.put(funcName, optCtrlStruStrapName);
													}
													cIDLDiagramElements.add(funcName);
													if (requiredIntfs.containsKey(funcName)) {
														String componentName = requiredIntfs.get(funcName);
														componentBlocks.add(componentName);
														if (!cIDLDiagramElements.contains(componentName)) {
															cIDLDiagramElements.add(componentName);}
													}
													if (funcName != null)
														alteranteElseIFCalls.add(funcName);
												}
												}
											}
											// put in list
											sc.getSecondElseIfCalls().addAll(alteranteElseIFCalls);
										}
									} else if (subSchedule instanceof SchedulingCall) {
										// collect first level
										SchedulingCall scheduleCall = (SchedulingCall) subSchedule;
										if (scheduleCall.getFunction().getReference() != null) {
							
										String funcName = scheduleCall.getFunction().getReference().getName();
										OptionalControlStructure optionalControlStructure = (OptionalControlStructure) ((SchedulingCall) scheduleCall)
												.getStrapControlStructure();
										if (optionalControlStructure != null) {
											Expression condi = ((OptionalControlStructure) optionalControlStructure)
													.getStrapExpression();
											optCtrlStruStrapName = toCString(condi);
											whenConditionMap.put(funcName, optCtrlStruStrapName);
										}
										cIDLDiagramElements.add(funcName);
										if (requiredIntfs.containsKey(funcName)) {
											String funComp = requiredIntfs.get(funcName);
											componentBlocks.add(funComp);
											if (!cIDLDiagramElements.contains(funComp)) {
												cIDLDiagramElements.add(funComp);}
										}
									
										firstIFCalls.add(funcName);
										}
									} else if (subSchedule instanceof SchedulingSequenceImpl) {

										EList<SchedulingElement> seqIMplementation = ((SchedulingSequenceImpl) subSchedule)
												.getSchedulingElements();
										
										ifSeqCount = seqIMplementation.size();
										for (SchedulingElement testscheElement : seqIMplementation) {
											if (testscheElement instanceof SchedulingCall) {

												String funcName = ((SchedulingCall) testscheElement).getFunction()
														.getReference().getName();
												OptionalControlStructure optionalControlStructure = (OptionalControlStructure) ((SchedulingCall) testscheElement)
														.getStrapControlStructure();
												if (optionalControlStructure != null) {
													Expression strapExpression = ((OptionalControlStructure) optionalControlStructure)
															.getStrapExpression();
													optCtrlStruStrapName = toCString(strapExpression);
													whenConditionMap.put(funcName, optCtrlStruStrapName);
												}
												cIDLDiagramElements.add(funcName);
												if (requiredIntfs.containsKey(funcName)) {
													String comp = requiredIntfs.get(funcName);
													componentBlocks.add(comp);
													if (!cIDLDiagramElements.contains(comp)) {
														cIDLDiagramElements.add(comp);}
												}

											}
										}

									}
									firstIfMaps.put(pfunc.getName(), firstIFCalls);
									sc.getFirstIfCalls().putAll(firstIfMaps);
									sc.setIfseqCount(ifSeqCount);
								}
							}
						}
						// first level else block
						ElseBlock eb = abi.getElse();
						if (eb != null) {
							List<String> firstElseCalls = new ArrayList<>();
							EList<SchedulingElement> elseExp = eb.getElseExp();
							for (SchedulingElement elsSchedEle : elseExp) {
								// first level
								if (elsSchedEle instanceof SchedulingCall) {
									SchedulingCall scheduleCall = (SchedulingCall) elsSchedEle;
									if (scheduleCall.getFunction().getReference() != null) {
										
									String funcName = scheduleCall.getFunction().getReference().getName();
									OptionalControlStructure optionalControlStructure = (OptionalControlStructure) ((SchedulingCall) scheduleCall)
											.getStrapControlStructure();
									if (optionalControlStructure != null) {
										Expression expression1 = ((OptionalControlStructure) optionalControlStructure)
												.getStrapExpression();
										optCtrlStruStrapName = toCString(expression1);
										whenConditionMap.put(funcName, optCtrlStruStrapName);
									}
									cIDLDiagramElements.add(funcName);
									if (requiredIntfs.containsKey(funcName)) {
										String comp1 = requiredIntfs.get(funcName);
										componentBlocks.add(comp1);
										if (!cIDLDiagramElements.contains(comp1)) {
											cIDLDiagramElements.add(comp1);}
									}
									firstElseCalls.add(funcName);
									}
								}
							}
							// put in list
							sc.getFirstElseCalls().addAll(firstElseCalls);
						}
						// first level elseif block
						EList<ElseIfBlock> elseIF = abi.getElseIfs();
						if (elseIF != null && elseIF.size() != 0) {
							List<String> firstElseIFCalls = new ArrayList<>();

							for (ElseIfBlock elsSchedEle : elseIF) {
								EList<SchedulingElement> elseifSchedule = elsSchedEle.getElseifExp();
								for (SchedulingElement seElseIF : elseifSchedule) {
									if (seElseIF instanceof SchedulingCall) {
										SchedulingCall scheduleCall = (SchedulingCall) seElseIF;
										if (scheduleCall.getFunction().getReference() != null) {
											
										String funcName = scheduleCall.getFunction().getReference().getName();
										OptionalControlStructure optionalControlStructure = (OptionalControlStructure) ((SchedulingCall) scheduleCall)
												.getStrapControlStructure();
										if (optionalControlStructure != null) {
											Expression expression2 = ((OptionalControlStructure) optionalControlStructure)
													.getStrapExpression();
											optCtrlStruStrapName = toCString(expression2);
											whenConditionMap.put(funcName, optCtrlStruStrapName);
										}
										cIDLDiagramElements.add(funcName);
										if (requiredIntfs.containsKey(funcName)) {
											String comp2 = requiredIntfs.get(funcName);
											componentBlocks.add(comp2);
											if (!cIDLDiagramElements.contains(comp2)) {
												cIDLDiagramElements.add(comp2);}
										}
										firstElseIFCalls.add(funcName);
									}
									}
								}

							}
							// put in list
							sc.getFirstElseCalls().addAll(firstElseIFCalls);
						}
					}
				}
			} catch (Exception e) {
				logger.info("Exception while getting schedulin elements\n" + e.toString());
			}
			sc.getComponentBlocks().addAll(componentBlocks);
		}
		sc.getRequiredIntfs().putAll(requiredIntfs);

		return sc;

	}

	private String toCString(final EObject ex) {
		if (ex instanceof HexConstant) {
			return strapExpression((HexConstant) ex);
		} else if (ex instanceof StringConstant) {
			return strapExpression((StringConstant) ex);
		} else if (ex instanceof IntegerConstant) {
			return strapExpression((IntegerConstant) ex);
		} else if (ex instanceof RealConstant) {
			return strapExpression((RealConstant) ex);
		} else if (ex instanceof FeatureType) {
			return strapExpression((FeatureType) ex);
		} else if (ex instanceof BinaryOperation) {
			return strapExpression((BinaryOperation) ex);
		} else if (ex instanceof StrapRef) {
			return strapExpression((StrapRef) ex);
		} else if (ex instanceof UnaryOperation) {
			return strapExpression((UnaryOperation) ex);
		} else if (ex instanceof HeaderFileStrap) {
			return strapExpression((HeaderFileStrap) ex);
		} 
		else {
			throw new IllegalArgumentException("Unhandled parameter types: " + Arrays.<Object>asList(ex).toString());
		}
	}

	protected String strapExpression(final BinaryOperation ex) {
		StringConcatenation builder = new StringConcatenation();
		String leftExp = this.toCString(ex.getLeft());
		builder.append(leftExp);
		builder.append(" ");
		CharSequence operandString = this.getOperandString(ex.getOp());
		builder.append(operandString);
		builder.append(" ");
		String rightExp = this.toCString(ex.getRight());
		builder.append(rightExp);
		return builder.toString();
	}

	protected String strapExpression(final UnaryOperation ex) {
		StringConcatenation builder = new StringConcatenation();
		CharSequence operandString = this.getOperandString(ex.getOp());
		builder.append(operandString);
		String op = this.toCString(ex.getOperand());
		builder.append(op);
		return builder.toString();
	}

	protected String strapExpression(final IntegerConstant ex) {
		StringConcatenation builder = new StringConcatenation();
		BigInteger val = ex.getVal();
		builder.append(val);
		return builder.toString();
	}


	protected String strapExpression(final RealConstant ex) {
		StringConcatenation builder = new StringConcatenation();
		double val = ex.getVal();
		builder.append(val);
		return builder.toString();
	}

	protected String strapExpression(final StringConstant ex) {
		StringConcatenation builder = new StringConcatenation();
		String val = ex.getVal();
		builder.append(val);
		return builder.toString();
	}

	protected String strapExpression(final HexConstant ex) {
		StringConcatenation builder = new StringConcatenation();
		String val = ex.getVal();
		builder.append(val);
		return builder.toString();
	}

	protected String strapExpression(final StrapRef ex) {
		StringConcatenation builder = new StringConcatenation();		
			EObject strap = ex.getStrap();
			if ((strap instanceof FeatureType)) {
				String trim = NodeModelUtils.findActualNodeFor(ex).getText().trim();
				builder.append(trim);
			} else {
				String cString = this.toCString(ex.getStrap());
				builder.append(cString);
			}		
		return builder.toString();
	}

	protected String strapExpression(final HeaderFileStrap ex) {
		StringConcatenation builder = new StringConcatenation();
		String name = ex.getName();
		builder.append(name);
		return builder.toString();
	}

	protected String strapExpression(final FeatureType ex) {
		StringConcatenation builder = new StringConcatenation();
		String name = ex.getName();
		builder.append(name);
		return builder.toString();
	}

	public CharSequence getOperandString(final Operator op) {
		StringConcatenation builder = new StringConcatenation();		
			boolean andOp = Objects.equal(op, Operator.AND_FR);
			if (andOp) {
				builder.append("&&");
			} else {
				if ((Objects.equal(op, Operator.OR_FR) || Objects.equal(op, Operator.OR_FR2))) {
					builder.append("||");
				} else {
					boolean notOp = Objects.equal(op, Operator.NEGATION_FR);
					if (notOp) {
						builder.append("!");
					} else {
						builder.append(op);
					}
				}
			}		
		return builder;
	}

	private int getOperatorSize(String interactionType, int firstIfCount, ScheduleCalls sc) {
		int size = 100;
		// get the size of interaction operator..
		List<String> firstIFCalls = new ArrayList<>();
		Map<String, List<String>> firstMap = sc.getFirstIfCalls();
		for (Entry<String, List<String>> entry : firstMap.entrySet()) {
			firstIFCalls.addAll(entry.getValue());
		}
		List<String> secondIFCalls = new ArrayList<>();
		Map<String, List<String>> secondMap ;
		secondMap = sc.getSecondIfCalls();
		for (Entry<String, List<String>> entry : secondMap.entrySet()) {
			secondIFCalls.addAll(entry.getValue());

		}
		if (interactionType.equalsIgnoreCase("opt")) {
			size = firstIfCount + secondIFCalls.size();
		} else if (interactionType.equalsIgnoreCase("alt")) {
			size = firstIfCount + sc.getFirstElseIfCalls().size() + sc.getFirstElseCalls().size() + secondIFCalls.size()
					+ sc.getSecondElseCalls().size() + sc.getSecondElseIfCalls().size() + sc.getIfseqCount();
		} else if (interactionType.equalsIgnoreCase("seq")) {
			size = firstIfCount;
		}

		return size;
	}

}
