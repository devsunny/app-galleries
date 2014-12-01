package org.dcache.chimera;

public class PathTraceUtility {

	public PathTraceUtility() {		
	}
	
	public static void trace()
	{
		Exception ex = new Exception();
		ex.fillInStackTrace();
		ex.printStackTrace();
		
	}

}
