package fr.sorbonne_u.treatements;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;


public class ParseXML {

	HashMap<String, String> method;
	private static final String FILENAME = "staff.xml";
	public ParseXML() {
		// TODO Auto-generated constructor stub
	}
	
	public static HashMap<String,String>  getOperations(Document doc, String tagName, String canonicalNameReference){
				
		HashMap<String, String> methods = new HashMap<>();
		doc.getDocumentElement().normalize();
		NodeList list = doc.getElementsByTagName(tagName);
		if(list.getLength()>0)
		{
			Node node = list.item(0).getChildNodes().item(0);
			while(node!=null)
			{
				String name = node.getNodeName();
				if(!name.equals("#text"))
				{
					Element ele = (Element)(node.getChildNodes());
					NodeList parameters = ele.getElementsByTagName("body");
					if(parameters.getLength()>0)
					{

						Node attributes = parameters.item(0).getAttributes().getNamedItem("equipmentRef");
						if(attributes!=null)
							methods.put(name, parameters.item(0).getTextContent().replaceAll(attributes.getNodeValue()+".", canonicalNameReference+"."));
						else
							methods.put(name, parameters.item(0).getTextContent());
					}
				}
				node = node.getNextSibling();
			}
		}
		return methods;
	}

	
	public static HashMap<String, String> getStandardOperations(Document doc,String tagName, String canonicalNameReference){
		HashMap<String, String> methods = new HashMap<>();
		doc.getDocumentElement().normalize();
		NodeList on = doc.getElementsByTagName(tagName);
		if(on.getLength()>0)
		{
			Element ele = (Element)(on.item(0));
			NodeList parameters = ele.getElementsByTagName("body");
			if(parameters.getLength()>0)
			{
				Node attributes = parameters.item(0).getAttributes().getNamedItem("equipmentRef");
				if(attributes!=null)
					methods.put(tagName, parameters.item(0).getTextContent().replaceAll(attributes.getNodeValue()+".", canonicalNameReference+"."));
				else
					methods.put(tagName, parameters.item(0).getTextContent());
			}
		}
		return methods;
	}
	
	public static HashMap<String, String> getMethods(Document doc) {
		HashMap<String, String> methods = new HashMap<>();
		NodeList control = doc.getElementsByTagName("control-adapter");
		String canonicalRef = "";
		if(control.getLength()>0)
		{
			canonicalRef = "(("+control.item(0).getAttributes().getNamedItem("offered").getNodeValue() + ")this.offering)";
		}
		methods.putAll(getOperations(doc,"mode-control",canonicalRef));
		methods.putAll(getOperations(doc,"planning-control",canonicalRef));
		methods.putAll(getOperations(doc,"suspension-control",canonicalRef));
		methods.putAll(getStandardOperations(doc, "on", canonicalRef));
		methods.putAll(getStandardOperations(doc, "switchOn", canonicalRef));
		methods.putAll(getStandardOperations(doc, "switchOff", canonicalRef));
		return methods;
	}

	public static HashMap<String, ArrayList<String>> getParameters(Document doc) {
		HashMap<String, ArrayList<String>> parameters = new HashMap<>();
		parameters.putAll(getParametersOfOperations(doc,"mode-control"));
		parameters.putAll(getParametersOfOperations(doc,"planning-control"));
		parameters.putAll(getParametersOfOperations(doc,"suspension-control"));
		parameters.putAll(getParametersOfStandarsOperations(doc, "on"));
		parameters.putAll(getParametersOfStandarsOperations(doc, "switchOn"));
		parameters.putAll(getParametersOfStandarsOperations(doc, "switchOff"));
		return parameters;
	}

	public static HashMap<String, ArrayList<String>> getParametersOfStandarsOperations(Document doc,String tagName){
		HashMap<String,ArrayList<String>> parameters =new HashMap<>();
		doc.getDocumentElement().normalize();
		NodeList on = doc.getElementsByTagName(tagName);
		if(on.getLength()>0)
		{
			Element ele = (Element)(on.item(0));
			NodeList parametersNodes = ele.getElementsByTagName("parameter");
			if(parametersNodes.getLength()>0) {
				ArrayList<String> attributes = new ArrayList<>();
				Node node = parametersNodes.item(0);
				while (node != null) {
					Node attribute = node.getAttributes().getNamedItem("name");
					if (attribute != null)
						attributes.add(attribute.getNodeValue());
					node = node.getNextSibling();
				}
				parameters.put(tagName, attributes);
			}
		}
		return parameters;
	}

	public static HashMap<String, ArrayList<String>> getParametersOfOperations(Document doc,String tagName){
		HashMap<String,ArrayList<String>> parameters =new HashMap<>();
		doc.getDocumentElement().normalize();
		NodeList list = doc.getElementsByTagName(tagName);
		if(list.getLength()>0)
		{
			Node node = list.item(0).getChildNodes().item(0);
			while(node!=null)
			{
				String name = node.getNodeName();
				if(!name.equals("#text"))
				{
					Element ele = (Element)(node.getChildNodes());
					NodeList parametersNodes = ele.getElementsByTagName("parameter");
					if(parametersNodes.getLength()>0)
					{
						ArrayList<String> attributes = new ArrayList<>();
						Node parameterNode = parametersNodes.item(0);
						while (parameterNode != null && !parameterNode.getNodeName().equals("#text")) {
							System.out.println(parameterNode);
							Node attribute = parameterNode.getAttributes().getNamedItem("name");
							if (attribute != null)
								attributes.add(attribute.getNodeValue());
							parameterNode = parameterNode.getNextSibling();
						}
						parameters.put(name, attributes);		}
				}
				node = node.getNextSibling();
			}
		}
		return parameters;
	}

	public static ArrayList<Attribute> getAttributes(Document doc){
		ArrayList<Attribute> attributes = new ArrayList<>();
		doc.getDocumentElement().normalize();
		NodeList on = doc.getElementsByTagName("instance-var");
		if(on.getLength()>0)
		{
			Node name = on.item(0).getAttributes().getNamedItem("name");
			Node type = on.item(0).getAttributes().getNamedItem("type");
			Node value = on.item(0).getAttributes().getNamedItem("static-init");
			if(name!=null && type!=null && value!=null)
			{
				attributes.add(new Attribute(type.getNodeValue(), name.getNodeValue(), value.getNodeValue()));
			}
		}
		return attributes;
	}

	public static String  getRef(Document doc){
		NodeList parameters = doc.getElementsByTagName("body");
		int i = 0;
		while (i < parameters.getLength()) {

			Node attributes = parameters.item(0).getAttributes().getNamedItem("equipmentRef");
			if (attributes != null)
				return attributes.getNodeValue();
			i++;
		}
		return "";
	}

	public static ArrayList<String> getPackages(Document doc)
	{
		ArrayList<String> packages = new ArrayList<>();
		doc.getDocumentElement().normalize();
		NodeList on = doc.getElementsByTagName("required");
		int i = 0;
			System.out.println("gjhgghggjh"+on.getLength());
		while(i < on.getLength()) {
			Element ele = (Element) (on.item(i));
			packages.add(ele.getTextContent());
			System.out.println("package"+ele.getTextContent()+i);
			i++;
		}
		return packages;
	}

	public static XML getXmlElements(String fileName)
	{
		XML xmlElements = new XML();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(fileName));
			xmlElements.setMethods(getMethods(doc));
			xmlElements.setParametersOfOperations(getParameters(doc));
			xmlElements.setAttributes(getAttributes(doc));
			xmlElements.setPackages(getPackages(doc));
			xmlElements.setRef(getRef(doc));
			NodeList control = doc.getElementsByTagName("control-adapter");
			String type = "";
			String offered = "";
			if(control.getLength()>0)
			{
				type = control.item(0).getAttributes().getNamedItem("type").getNodeValue();
				offered = control.item(0).getAttributes().getNamedItem("offered").getNodeValue();
			}
			xmlElements.setType(type);
			xmlElements.setOffered(offered);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return xmlElements;
	}
	public static void main(String[] args) {
		Duration s=Duration.ofHours(1);
		//System.out.println(getXmlElements("src/fr/sorbonne_u/xml/washingMachine.xml").toString());
	}
		 
}