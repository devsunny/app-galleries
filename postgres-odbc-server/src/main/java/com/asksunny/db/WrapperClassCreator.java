package com.asksunny.db;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;

public class WrapperClassCreator {

	public WrapperClassCreator() {		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		String className = args.length>0?args[0]:ResultSet.class.getName();
		Class<?> clazz = Class.forName(className);
		
		System.out.println(String.format("\tprotected %s wrappedObject = null; ",clazz.getName()));
		
		
		
		
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];	
			Class<?> retType = method.getReturnType();
			StringBuilder innerBuf = new StringBuilder();
			if(retType!=void.class){
				innerBuf.append("return ");
			}
			innerBuf.append("wrappedObject.");
			innerBuf.append( method.getName()).append("(");			
			String retName = retType.isArray()?retType.getSimpleName():retType.getName();
			System.out.print(String.format("\tpublic %2$s %1$s(", method.getName(), retName));
			
			
			Type[] types  = method.getGenericParameterTypes();
			Class<?>[] paramTypes = method.getParameterTypes();
			
			for (int j = 0; j < types.length; j++) {	
				
				if(types[j] instanceof ParameterizedType){
					ParameterizedType paramType =(ParameterizedType) types[j];
					System.out.print(String.format("%s%s", paramTypes[j].getName(),  "<"));
					Type[]  intypes = paramType.getActualTypeArguments();
					for (int k = 0; k < intypes.length; k++) {
						if(intypes[k] instanceof ParameterizedType){
							System.out.print(intypes[k].toString());
						}else{
							System.out.print(intypes[k].toString());
						}
					}
					System.out.print(">");
				}else if(paramTypes[j].isArray()){
					System.out.print(paramTypes[j].getSimpleName());
				}else{
					System.out.print(paramTypes[j].getName());
				}
				innerBuf.append(String.format(" arg%d", j));
				System.out.print(String.format(" arg%d", j));
				if(j<types.length-1){
					System.out.print(", ");
					innerBuf.append(", ");
				}
				
//				if(types[j].getClass().isPrimitive()){
//					System.out.println(types[j].toString());
//				}else if(types[j].getClass()==Class.class) {
//					System.out.println("Hellllllll");
//				}else{
//					
//				}
				
			}
			System.out.print(") ");
			innerBuf.append(");");
			Class<?>[] exTypes  = method.getExceptionTypes();
			if(exTypes!=null && exTypes.length>0){
				System.out.print(" throws ");
				for (int j = 0; j < exTypes.length; j++) {
					System.out.print(exTypes[j].getName());
					if(j<exTypes.length-1) System.out.print(", ");
				}
			}
			
			System.out.println("{ ");
			System.out.println("\t\t" + innerBuf.toString());
			System.out.println("\t} ");
		}

	}

}
