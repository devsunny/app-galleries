package com.asksunny.odbc;


public class StackTraceUtils {

	public static String buildStackTrace(Class<?> hideStopClass,
			String hideStopMethodName, Class<?> showStopClass,
			String showStopMethodName) {
		Exception ex = new Exception();
		return buildStackTrace(ex, hideStopClass, hideStopMethodName,
				showStopClass, showStopMethodName);
	}
	
	public static String buildStackTrace(Class<?> hideStopClass,
			String hideStopMethodName, Class<?> showStopClass
			) {
		Exception ex = new Exception();
		return buildStackTrace(ex, hideStopClass, hideStopMethodName,
				showStopClass, null);
	}
	

	public static String buildStackTrace(Class<?> showStopClass,
			String showStopMethodName) {
		Exception ex = new Exception();
		return buildStackTrace(ex, StackTraceUtils.class, "buildStackTrace",
				showStopClass, showStopMethodName);
	}

	public static String buildStackTrace() {
		Exception ex = new Exception();
		return buildStackTrace(ex, StackTraceUtils.class, "buildStackTrace",
				null, null);
	}
	
	public static String buildStackTrace(Throwable t) {		
		return buildStackTrace(t, null, null, null, null);
	}

	public static String buildStackTrace(Throwable t, Class<?> hideStopClass,
			String hideStopMethodName, Class<?> showStopClass,
			String showStopMethodName) {
		StringBuilder buf = new StringBuilder();
		buf.append(String.format("Invoker stack trace:%n"));
		boolean found = (hideStopClass == null) ? true : false;
		StackTraceElement[] elms = t.getStackTrace();
		for (int i = 0; i < elms.length; i++) {
			String methodName = elms[i].getMethodName();
			String className = elms[i].getClassName();
			if (!found && className.equals(hideStopClass.getName())) {
				if(hideStopMethodName== null || methodName.equals(hideStopMethodName)){
					found = true;
				}				
			} else if (found) {
				if (showStopClass != null && showStopClass.getName().equals(className)) {					
					if (showStopMethodName == null
							|| showStopMethodName.equals(methodName)) {						
						break;
					}
				}
				buf.append(String.format("%s.%s:[%d]%n", className,
						methodName, elms[i].getLineNumber()));
			}
		}
		return buf.toString();
	}

	private StackTraceUtils() {

	}

}
