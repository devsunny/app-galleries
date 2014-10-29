package com.asksunny.io;

import java.io.File;

public abstract class URIParser {

	public URIParser() {

	}

	public static URIInfo parse(String uri) {
		URIInfo info = new URIInfo();
		String tmpUri = uri;
		int idx = tmpUri.indexOf(":/");
		if (idx != -1) {
			info.setProtocol(tmpUri.substring(0, idx));
			if (info.getProtocol().equalsIgnoreCase("file")) {
				info.setPath(tmpUri.substring(idx + 2));
				tmpUri = null;
			} else {
				tmpUri = tmpUri.substring(idx + 3);
			}
		}
		if (tmpUri == null)
			return info;
		idx = tmpUri.indexOf("/");
		String hostinfos = null;
		if (idx != -1) {
			hostinfos = tmpUri.substring(0, idx);
			tmpUri = tmpUri.substring(idx);
		} else {
			hostinfos = tmpUri;
			tmpUri = null;
		}
		String[] hosts = hostinfos.split(";|,");
		for (int i = 0; i < hosts.length; i++) {
			IPHostInfo iphostInfo = new IPHostInfo();
			String hostinfo = hosts[i];
			int uidx = hostinfo.indexOf("@");
			if (uidx != -1) {
				String userinfo = hostinfo.substring(0, uidx);
				String[] uinfos = userinfo.split(":");
				if(uinfos.length>1){
					iphostInfo.setUsername(uinfos[0]);
					iphostInfo.setPassword(uinfos[1]);
				}else{
					iphostInfo.setUsername(uinfos[0]);
				}
				hostinfo = hostinfo.substring(uidx + 1);
			}
			String[] ipinfos = hostinfo.split(":");
			if (ipinfos.length > 1) {
				iphostInfo.setHostname(ipinfos[0]);
				iphostInfo.setPort(Integer.valueOf(ipinfos[1]));
			} else {
				iphostInfo.setHostname(ipinfos[0]);
			}

			info.getHostinfos().add(iphostInfo);
		}
		if (tmpUri == null)
			return info;

		File f = new File(tmpUri);

		info.setFilename(f.getName());
		info.setDirectory(f.getParent());

		return info;
	}

}
