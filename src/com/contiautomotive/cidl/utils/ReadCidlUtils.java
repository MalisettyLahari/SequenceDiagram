package com.contiautomotive.cidl.utils;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;

import com.contiautomotive.cidl.ext.CidlModelExtensions;
import com.contiautomotive.cidl.resourcemanager.ICidlResourceManager;
import com.contiautomotive.cidl.ui.internal.CidlActivator;
import com.google.inject.Injector;

public class ReadCidlUtils {
	  private ReadCidlUtils() {
		    throw new IllegalStateException("Utility class");
		  }
	public static ICidlResourceManager getCidlResourceManager(IProject project) {
		ICidlResourceManager resourceManager = getInjector().getInstance(ICidlResourceManager.class);

		resourceManager.setWorkingDirectory(project.getFullPath().toOSString());
		resourceManager.setFileExtensions(new HashSet<String>(Arrays.asList(new String[] { "cidl" })));

		return resourceManager;
	}

	public static Injector getInjector() {
		return CidlActivator.getInstance().getInjector("com.contiautomotive.cidl.Cidl");
	}

	public static CidlModelExtensions getCidlModelExtensions() {
		return getInjector().getInstance(CidlModelExtensions.class);
	}
}
