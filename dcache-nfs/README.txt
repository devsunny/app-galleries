Big Data Development in windows Platform without Admin rights

1. Download HDP http://public-repo-1.hortonworks.com/HDP-Win/2.2/2.2-latest/hdp-2.2-latest-GA.zip
2. Extract the zip file
3. Extract files from Microsoft MSI package:
	  msiexec /a hdp-2.2.0.0.winpkg.msi TARGETDIR=C:\tmp\winHadoopPackages /qn (It could take a while, be patient)
	  or
	  msiexec /a hdp-2.2.0.0.winpkg.msi TARGETDIR=C:\tmp\winHadoopPackages /qb (You will see GUI popup)
4. You should have the following directory structure at C:\tmp\winHadoopPackages
		winHadoopPackages
			|
			|---------HadoopPackages
            |             |
            |             |--------hdp-2.2.0.0-winpkg.zip
            |---------HadoopSetupTools
 
 5. Unzip winHadoopPackages\HadoopPackages\hdp-2.2.0.0-winpkg.zip, you should have the following packages in resources directory
 	
 	datafu-1.2.0.2.2.0.0-2041.winpkg.zip                      
 	failovercontroller-1.0.0.winpkg.zip
	falcon-0.6.0.2.2.0.0-2041.winpkg.zip                      
	flume-1.5.1.2.2.0.0-2041.winpkg.zip
	hadoop-2.6.0.2.2.0.0-2041.winpkg.zip                      
	hbase-0.98.4.2.2.0.0-2041-hadoop2.winpkg.zip
	hive-0.14.0.2.2.0.0-2041.winpkg.zip  
	knox-0.5.0.2.2.0.0-2041.winpkg.zip                        
	mahout-0.9.0.2.2.0.0-2041.winpkg.zip
	microsoft-sqoop-connector-1.4.5.2.2.0.0-2041.winpkg.zip   
	oozie-4.1.0.2.2.0.0-2041.winpkg.zip
	phoenix-4.2.0.2.2.0.0-2041.winpkg.zip                     
	pig-0.14.0.2.2.0.0-2041.winpkg.zip
	ranger-0.4.0.2.2.0.0-2041.winpkg.zip                      
	slider-0.51.0.2.2.0.0-2041.winpkg.zip
	sqoop-1.4.5.2.2.0.0-2041.winpkg.zip                       
	storm-0.9.3.2.2.0.0-2041.winpkg.zip
	tez-0.5.2.2.2.0.0-2041.winpkg.zip   
	zookeeper-3.4.6.2.2.0.0-2041.winpkg.zip
	                      
	winpkg.cmd
	winpkg.ps1                                                
	winpkg.utils.psm1
	installHelper2.exe
	
 6. extract the product you want to install to installation directory, product package is in the resources directory
 
 7. Setup Hadoop, follow single noe cluster setup at http://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/SingleCluster.html
 
 8. Add HADOOP_HOME environment variable and %HADOOP_HOME%\bin to PATH veriable, make sure java.exe (Java 7) is in the PATH also.
 	
 	you can modify %HADOOP_HOME%\sbin\start-dfs.cmd and  %HADOOP_HOME%\sbin\start-yarn.cmd and add the following to the file
 	
 	SET PATH=%HADOOP_BIN_PATH%\..\bin;%PATH%
 	
 9. Format the namenode "hadoop namenode -format" (This command may not be able to terminate the JVM properly, you have to use Windows Task Manager to kill the java.exe process)
 
 10. run the following command to start up Hadoop
 	 %HADOOP_HOME%\sbin\start-dfs.cmd
 	 %HADOOP_HOME%\sbin\start-yarn.cmd
 
 11. In your eclipse project's application's run configuration add HADOOP_HOME environment variable if need
 
 
 
 	
 	
 	
 	
 
 
 
            
 