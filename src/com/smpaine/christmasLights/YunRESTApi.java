package com.smpaine.christmasLights;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class YunRESTApi {

	private StringBuffer query = null;
	int origLen = 0;
	int retryCount = 2;
	
	public YunRESTApi(String host) {
		origLen = host.trim().length();
		query = new StringBuffer();
		query.append(host);
	}
	
	public boolean sendCommand(String theQuery) {
		
        boolean recvdData = false;
        
		try {
			query.append(theQuery);
			//System.out.println("YunRESTApi: Setting URL: " + query.toString());
			URL url = new URL(query.toString());
	
	        //get result
	        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	        String l = null;
	        while ((l=br.readLine())!=null) {
	            //System.out.println(l);
	            recvdData = true;
	        }
	        br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println("Connection error: " + e.getMessage());
		}
		
		this.query.setLength(origLen);
		return recvdData;
	}
	
	public void initLights() {
		//refresh();
		int counter=0;
		while (!sendCommand("/arduino/refresh")) {
			// Retry
			if (++counter > 10) {
				break;
			}
		}
	}
	
	public void initDemo() {
		int counter=0;
		
		allOff();
		
		while (!sendCommand("/arduino/demo")) {
			// Retry
			if (++counter > retryCount) {
				break;
			}
		}
	}
	
	public void allOn() {
		int counter=0;
		while (!sendCommand("/arduino/allOn")) {
			// Retry
			if (++counter > retryCount) {
				break;
			}
		}
		//refresh();
	}
	
	public void allOff() {
		int counter=0;
		while (!sendCommand("/arduino/clear")) {
			// Retry
			if (++counter > retryCount) {
				break;
			}
		}
		//refresh();
	}
	
	public void refresh() {
		int counter=0;
		while (!sendCommand("/arduino/refresh")) {
			// Retry
			if (++counter > retryCount) {
				break;
			}
		}
	}
	
	public void setLed(String led, String brightness) {
		int counter=0;
		while (!sendCommand("/arduino/led/" + led + "/" + brightness)) {
			// Retry
			if (++counter > retryCount) {
				break;
			}
		}
	}
	
	public void setGroup(String group, String brightness) {
		int counter=0;
		while (!sendCommand("/arduino/group/" + group + "/" + brightness)) {
			// Retry
			if (++counter > retryCount) {
				break;
			}
		}
	}
	
}
