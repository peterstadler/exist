/*
*  eXist Open Source Native XML Database
*  Copyright (C) 2001-04 Wolfgang M. Meier (wolfgang@exist-db.org) 
*  and others (see http://exist-db.org)
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public License
*  as published by the Free Software Foundation; either version 2
*  of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program; if not, write to the Free Software
*  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
* 
*  $Id$
*/
package org.exist.http.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.BindException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.exist.StandaloneServer;
import org.exist.storage.DBBroker;
import org.mortbay.util.MultiException;

/** A test case for accessing a remote server via REST-Style Web API.
 * @author wolf
 * @author Pierrick Brihaye <pierrick.brihaye@free.fr>
 */
public class RESTServiceTest extends TestCase {

	private static StandaloneServer server = null;
	private final static String SERVER_URI = "http://localhost:8088";
	private final static String COLLECTION_URI = SERVER_URI + DBBroker.ROOT_COLLECTION + "/test";	
	private final static String RESOURCE_URI = SERVER_URI + DBBroker.ROOT_COLLECTION +  "/test/test.xml";	
	
	private final static String XML_DATA =
		"<test>" +
		"<para>\u00E4\u00E4\u00FC\u00FC\u00F6\u00F6\u00C4\u00C4\u00D6\u00D6\u00DC\u00DC</para>" +
		"</test>";
	
	private final static String XUPDATE =
		"<xu:modifications xmlns:xu=\"http://www.xmldb.org/xupdate\" version=\"1.0\">" +
		"<xu:append select=\"/test\" child=\"1\">" +
		"<para>Inserted paragraph.</para>" +
		"</xu:append>" +
		"</xu:modifications>";
	
	private final static String QUERY_REQUEST =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<query xmlns=\"http://exist.sourceforge.net/NS/exist\">" +
		"<properties>" +
		"<property name=\"indent\" value=\"yes\"/>" +
		"<property name=\"encoding\" value=\"UTF-8\"/>" +
		"</properties>" +
		"<text>" +
		"xquery version \"1.0\";" +
		"(::pragma exist:serialize indent=no ::)" +
		"//para[. = '\u00E4\u00E4\u00FC\u00FC\u00F6\u00F6\u00C4\u00C4\u00D6\u00D6\u00DC\u00DC']/text()" +
		"</text>" +
		"</query>";
	
	public RESTServiceTest(String name) {
		super(name);
	}

	protected void setUp() {
		//Don't worry about closing the server : the shutdown hook will do the job
		try {
			if (server == null) {
				server = new StandaloneServer();
				if (!server.isStarted()) {			
					try {				
						System.out.println("Starting standalone server...");
						String[] args = {};
						server.run(args);
						while (!server.isStarted()) {
							Thread.sleep(1000);
						}
					} catch (MultiException e) {
						boolean rethrow = true;
						Iterator i = e.getExceptions().iterator();
						while (i.hasNext()) {
							Exception e0 = (Exception)i.next();
							if (e0 instanceof BindException) {
								System.out.println("A server is running already !");
								rethrow = false;
								break;
							}
						}
						if (rethrow) throw e;
					}
				}
			}
	    } catch (Exception e) {            
	        fail(e.getMessage()); 
	    }
	} 			
	
	public void testPut() {
		try {
			System.out.println("--- Storing document ---");
			HttpURLConnection connect = getConnection(RESOURCE_URI);
			connect.setRequestMethod("PUT");
			connect.setDoOutput(true);
			connect.setRequestProperty("ContentType", "text/xml");
			
			Writer writer = new OutputStreamWriter(connect.getOutputStream(), "UTF-8");
			writer.write(XML_DATA);
			writer.close();
			
			connect.connect();
			int r = connect.getResponseCode();
			assertEquals("Server returned response code " + r, 200, r);
			
			doGet();
	    } catch (Exception e) {            
	        fail(e.getMessage()); 
	    }
	}
	
	public void testXUpdate() {
		try {
			HttpURLConnection connect = preparePost(XUPDATE);
			connect.connect();
			int r = connect.getResponseCode();
			assertEquals("Server returned response code " + r, 200, r);
			
			doGet();
	    } catch (Exception e) {            
	        fail(e.getMessage()); 
	    }
	}
	
	public void testQueryPost() {
		try {
			HttpURLConnection connect = preparePost(QUERY_REQUEST);
			connect.connect();
			int r = connect.getResponseCode();
			assertEquals("Server returned response code " + r, 200, r);
			
			System.out.println(readResponse(connect.getInputStream()));
	    } catch (Exception e) {            
	        fail(e.getMessage()); 
	    }
	}
	
	public void testQueryGet() {
		try {
			String uri = COLLECTION_URI + "?_query=" + URLEncoder.encode("doc('"+ DBBroker.ROOT_COLLECTION + "/test/test.xml')//para[. = '\u00E4\u00E4\u00FC\u00FC\u00F6\u00F6\u00C4\u00C4\u00D6\u00D6\u00DC\u00DC']/text()", "UTF-8");
			HttpURLConnection connect = getConnection(uri);
			connect.setRequestMethod("GET");
			connect.connect();
			
			int r = connect.getResponseCode();
			assertEquals("Server returned response code " + r, 200, r);
			
			System.out.println(readResponse(connect.getInputStream()));
	    } catch (Exception e) {            
	        fail(e.getMessage()); 
	    }
	}
	
	public void testRequestModule() {
		try {
			String uri = COLLECTION_URI + "?_query=request:request-uri()&_wrap=no";
			HttpURLConnection connect = getConnection(uri);
			connect.setRequestMethod("GET");
			connect.connect();
			
			int r = connect.getResponseCode();
			assertEquals("Server returned response code " + r, 200, r);
		
			String response = readResponse(connect.getInputStream()).trim();
			assertEquals(response, DBBroker.ROOT_COLLECTION + "/test");
			
			uri = COLLECTION_URI + "?_query=request:request-url()&_wrap=no";
			connect = getConnection(uri);
			connect.setRequestMethod("GET");
			connect.connect();
			
			r = connect.getResponseCode();
			assertEquals("Server returned response code " + r, 200, r);
			
			response = readResponse(connect.getInputStream()).trim();
			//TODO : the server name may have been renamed by the Web server
			assertEquals(response, SERVER_URI + DBBroker.ROOT_COLLECTION + "/test");	
	    } catch (Exception e) {            
	        fail(e.getMessage()); 
	    }
	}	
	
	protected void doGet() {
		try {
			System.out.println("--- Retrieving document ---");
			HttpURLConnection connect = getConnection(RESOURCE_URI);
			connect.setRequestMethod("GET");
			connect.connect();
			
			int r = connect.getResponseCode();
			assertEquals("Server returned response code " + r, 200, r);
			
			System.out.println(readResponse(connect.getInputStream()));
	    } catch (Exception e) {            
	        fail(e.getMessage()); 
	    }
	}
	
	protected HttpURLConnection preparePost(String content) {
		try {
			HttpURLConnection connect = getConnection(RESOURCE_URI);
			connect.setRequestMethod("POST");
			connect.setDoOutput(true);
			connect.setRequestProperty("ContentType", "text/xml");
			
			Writer writer = new OutputStreamWriter(connect.getOutputStream(), "UTF-8");
			writer.write(content);
			writer.close();
			
			return connect;
	    } catch (Exception e) {            
	        fail(e.getMessage()); 
	    }
	    return null;
	}
	
	protected String readResponse(InputStream is) {
		try {
			BufferedReader reader = 
				new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line;
			StringBuffer out = new StringBuffer();
			while((line = reader.readLine()) != null) {
				out.append(line);
				out.append("\r\n");
			}
			return out.toString();
	    } catch (Exception e) {            
	        fail(e.getMessage()); 
	    }
	    return null;
	}
	
	protected HttpURLConnection getConnection(String url) {
		try {
			URL u = new URL(url);
			return (HttpURLConnection)u.openConnection();
	    } catch (Exception e) {            
	        fail(e.getMessage()); 
	    }
	    return null;
	}
	
    public static void main(String[] args) {
		TestRunner.run(RESTServiceTest.class);
		//Explicit shutdown for the shutdown hook
		System.exit(0);
	}	
}
