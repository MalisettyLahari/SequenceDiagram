package com.contiautomotive.read.cidl.data

import com.contiautomotive.cidl.cidl.Software
import com.contiautomotive.cidl.cidl.impl.ModelImpl
import com.contiautomotive.cidl.resourcemanager.ICidlResourceManager
import com.contiautomotive.cidl.utils.ReadCidlUtils
import java.util.Set
import org.eclipse.core.resources.IProject
import org.eclipse.emf.ecore.resource.Resource

class CollectCidlData {

	IProject project
	public ICidlResourceManager resourceManager
	Set<Resource> resources
	Software _software

	new (IProject project) {
		ReadCidlUtils::getCidlModelExtensions()
		this.project = project
		resourceManager = ReadCidlUtils::getCidlResourceManager(this.project)
		resources = resourceManager.collectResources()
		println('''Resources size: «resources.size()»''')

		var softwares = resources.map[contents].flatten.filter(typeof(ModelImpl)).map[software].filter(typeof(Software)).toList

		if (softwares.size > 1)
			throw new Exception("More than one software was found :  " + softwares.map[name].join(", "))

		_software = softwares.head as Software
		println('''Software is : «_software»''')

		if (_software === null)
			throw new Exception("no software was found.")
	}
	
	
	def getResourceManager(){
		resourceManager
	}
	def getResource(){
		resources
	}
	
}