package main.java.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import spoon.processing.AbstractManualProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.filter.TypeFilter;

public class ClassProcessor extends AbstractManualProcessor{

	private static List<String> mainClasses = new ArrayList<String>();
		
	public List<String> getMainClasses() {
		return mainClasses;
	}

	@Override
	public void process() {
		//this.mainClasses = new ArrayList<String>();
		//get all classes
		List<CtClass> classes = getFactory().Package().getRootPackage().getElements(new TypeFilter(CtClass.class));
		getMainClasses(classes);
	}
	
	private void getMainClasses(List<CtClass> classes){	
		
		for(CtClass cls:classes){
			Boolean containsMain=false;
			//get all methods from class
			Class<CtMethod> filterClass = CtMethod.class;
			TypeFilter<CtMethod> statementFilter = new TypeFilter<CtMethod>(filterClass);
			List<CtMethod> methods = cls.getElements(statementFilter);
			
			for(CtMethod method:methods){				
				if(isMainMethod(method))
					containsMain = true;					
			}
			if(containsMain){
				mainClasses.add(cls.getQualifiedName());
			}
		}
	}
	
	private Boolean isMainMethod(CtMethod method){
		
		Set<ModifierKind> modifiers = method.getModifiers();
		
		if(modifiers.contains(ModifierKind.PUBLIC) && modifiers.contains(ModifierKind.STATIC)
				&& method.getSimpleName().equals("main")){
			List<CtParameter> parameters = method.getParameters();
			for(CtParameter param:parameters){
				if(param.getType().toString().equals("java.lang.String[]") && parameters.size()==1)
					return true;
				else
					return false;
			}
			return false;
		}
		else
			return false;
	}

}
