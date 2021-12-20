package fr.sorbonne_u.treatements;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.waterHeater.WaterHeaterCI;
import fr.sorbonne_u.interfaces.SuspensionEquipmentControlCI;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;

public class ConnectorGenerator {

	public ConnectorGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	public static Class<?> makeConnectorClassJavassist(String connectorCanonicalClassName, 
			Class<?> connectorSuperclass,
			Class<?> connectorImplementedInterface,
			Class<?> offeredInterface,
			HashMap<String,String> methodNamesMap,
			HashMap<String, ArrayList<String>> parametres, ArrayList<Attribute> attributes,
													   ArrayList<String> packages) throws Exception {
		ClassPool pool = ClassPool.getDefault() ;
		for(String packag : packages)
			pool.importPackage(packag);
		CtClass cs = pool.get(connectorSuperclass.getCanonicalName()) ;
		CtClass cii = pool.get(connectorImplementedInterface.getCanonicalName()) ;
		CtClass oi = pool.get(offeredInterface.getCanonicalName()) ;
		CtClass connectorCtClass = pool.makeClass(connectorCanonicalClassName) ;
		connectorCtClass.setSuperclass(cs) ;

		for(Attribute attribute : attributes) {
			String att = "protected "+attribute.getType()+" "+attribute.getName()+";";
			System.out.println(att);
			CtField ct = CtField.make(att, connectorCtClass);
			connectorCtClass.addField(ct);
		}

		String attIntialisation ="";
		for(Attribute attribute : attributes) {
			attIntialisation = attribute.getName()+" = "+attribute.getValue()+";";
			System.out.println(attIntialisation);
		}

		CtConstructor defaultConstructor = CtNewConstructor.make("public " + connectorCtClass.getSimpleName() + "() {\nsuper();\n" +
				 attIntialisation+"}", connectorCtClass);
		connectorCtClass.addConstructor(defaultConstructor);

		
		
		/*------------------------Methodes----------------------------*/
		System.out.println(connectorImplementedInterface.getMethods().length);
		Method[] methodsToImplement = connectorImplementedInterface.getMethods() ;
		for (int i = 0 ; i < methodsToImplement.length ; i++) {
			String source = "public " ;
			source += methodsToImplement[i].getReturnType().getName() + " " ;
			source += methodsToImplement[i].getName() + "(" ;
			Class<?>[] pt = methodsToImplement[i].getParameterTypes() ;
			if(methodsToImplement[i].getName()=="setMode")
			System.out.println( methodsToImplement[i].getParameters()[0].getAnnotatedType());
			for (int j = 0 ; j < pt.length ; j++) {
				String pName = parametres.get(methodsToImplement[i].getName()).get(j);
				source += pt[j].getCanonicalName() + " " + pName ;
				if (j < pt.length - 1) {
					source += ", " ;
			}
		}
		source += ")" ;
		Class<?>[] et = methodsToImplement[i].getExceptionTypes() ;
		if (et != null && et.length > 0) {
			source += " throws " ;
			for (int z = 0 ; z < et.length ; z++) {
				source += et[z].getCanonicalName() ;
				if (z < et.length - 1) {
					source += "," ;
				}
			}
		}
		source += "\n{" ;
		source += methodNamesMap.get(methodsToImplement[i].getName())+"\n}";
		System.out.println(source);
		CtMethod theCtMethod = CtMethod.make(source, connectorCtClass) ;
		connectorCtClass.addMethod(theCtMethod) ;
		//System.out.println("test");
	}
	connectorCtClass.setInterfaces(new CtClass[]{cii}) ;
	cii.detach() ; cs.detach() ; oi.detach() ;
	Class<?> ret = connectorCtClass.toClass() ;
	connectorCtClass.detach() ;
	return ret ;
	}
	
	public static Class generateClass(String className, String methodName, String methodBody)
		      throws CannotCompileException {
		    ClassPool pool = ClassPool.getDefault();
		    CtClass cc = pool.makeClass(className);

		    StringBuffer method = new StringBuffer();
		    method.append("public double emergency() throws java.lang.Exception ")
		          .append("{")
		          .append("if(true)")
					.append("{return 1.0;}")
					.append("return 1.0;")
		          .append("}");

		    cc.addMethod(CtMethod.make(method.toString(), cc));

		    return cc.toClass();
		  }
	public static void main(String[] args) throws Exception {
			XML xmlElements = ParseXML.getXmlElements("staff.xml");
		//	System.out.println(xmlElements.toString());
			Class clazz=makeConnectorClassJavassist("fr.sorbonne_u.components.waterHeater.waterHeateConnector",
					AbstractConnector.class, SuspensionEquipmentControlCI.class, Class.forName("fr.sorbonne_u.components.waterHeater.WaterHeaterCI"), xmlElements.getMethods(), xmlElements.getParametersOfOperations(), xmlElements.getAttributes(), xmlElements.getPackages());
			//System.out.println(clazz.getConstructors()[0]);
			//generateClass("test", "abc", "System.out.println(\"gdcgsh\")");
			// Use our static method to make a magic
		//	Class clazz = generateClass("test", "abc", "System.out.println(\"gdcgsh\")");
			// Create a new instance of our newly generated class
			Object obj = clazz.newInstance();
			//Find our method in generated class
			Method method = clazz.getDeclaredMethod("upMode");
			// And finally invoke it on instance
		//	System.out.println("packae"+clazz.getDeclaredFields()[0]);
		//	System.out.println(clazz.toString());
		/*}catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("inconsistent stack height " + e.getMessage());
		}*/
	}
}

