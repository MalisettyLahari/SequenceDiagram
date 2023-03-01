package com.contiautomotive.architecture.tool.diagrams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
/**
 * 
 * EMF Descritpion of all the  scheduling elements.All the two levels calls are stored in there respective Lists.
 * The information is then used to calculate the alternate or optional block size. 
 * 
 * @author  uidg8159 
 * @version 1 Jun 2, 2016
 *
 */
public class ScheduleCalls {
	String mainfuncname = null;
	String secondFuncName = null;
	int ifseqCount = 0;
	Map<String, List<String>> firstIfCalls = new HashMap<>();
	Set<String> componentBlocks = new TreeSet<>();
	HashMap<String, String> requiredIntfs = new HashMap<>();

	public Set<String> getComponentBlocks() {
		return componentBlocks;
	}

	public void setComponentBlocks(Set<String> componentBlocks) {
		this.componentBlocks = componentBlocks;
	}
	List<String> firstElseIfCalls = new ArrayList<>();
	List<String> firstElseCalls = new ArrayList<>();
	Map<String, List<String>> secondIfCalls = new HashMap<>();
	List<String> secondElseCalls = new ArrayList<>();
	List<String> secondElseIfCalls = new ArrayList<>();
	boolean alternativeblock = false;

	public boolean isAlternativeblock() {
		return alternativeblock;
	}

	public void setAlternativeblock(boolean alternativeblock) {
		this.alternativeblock = alternativeblock;
	}

	public int getIfseqCount() {
		return ifseqCount;
	}

	public void setIfseqCount(int ifseqCount) {
		this.ifseqCount = ifseqCount;
	}


	public String getMainfuncname() {
		return mainfuncname;
	}

	public Map<String, String> getRequiredIntfs() {
		return requiredIntfs;
	}

	public ScheduleCalls() {
		
	}

	public void setMainfuncname(String mainfuncname) {
		this.mainfuncname = mainfuncname;
	}

	public String getSecondFuncName() {
		return secondFuncName;
	}

	public void setSecondFuncName(String secondFuncName) {
		this.secondFuncName = secondFuncName;
	}

	public Map<String, List<String>> getFirstIfCalls() {
		return firstIfCalls;
	}

	public void setFirstIfCalls(Map<String, List<String>> firstIfCalls) {
		this.firstIfCalls = firstIfCalls;
	}

	public List<String> getFirstElseIfCalls() {
		return firstElseIfCalls;
	}

	public void setFirstElseIfCalls(List<String> firstElseIfCalls) {
		this.firstElseIfCalls = firstElseIfCalls;
	}

	public List<String> getFirstElseCalls() {
		return firstElseCalls;
	}

	public void setFirstElseCalls(List<String> firstElseCalls) {
		this.firstElseCalls = firstElseCalls;
	}

	public Map<String, List<String>> getSecondIfCalls() {
		return secondIfCalls;
	}

	public void setSecondIfCalls(Map<String, List<String>> secondIfCalls) {
		this.secondIfCalls = secondIfCalls;
	}

	public List<String> getSecondElseCalls() {
		return secondElseCalls;
	}

	public void setSecondElseCalls(List<String> secondElseCalls) {
		this.secondElseCalls = secondElseCalls;
	}

	public List<String> getSecondElseIfCalls() {
		return secondElseIfCalls;
	}

	public void setSecondElseIfCalls(List<String> secondElseIfCalls) {
		this.secondElseIfCalls = secondElseIfCalls;
	}
}
