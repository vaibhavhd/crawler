package eait.assignment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class Crawler
{
    private List<String> links = new LinkedList<String>();

	/**
	 * Connects to a URL and does basic error checking and error handling.
	 * Parses the HTML page and generates the links to other websites.
	 * If a link is found, the corresponding incoming link for the website is incremented. 
	 * @param incomingLinks The map containing the link and its incoming link counter.
	 * @param url The parent URL to be crawled.
	 * @return
	 */
    public void crawl(Map<String, Integer> incomingLinks, String url) {
    	
    	/* First attempt the HTTPS version. This is done as with most of the candidate websites, the HTTP version 
    	 * was redirecting the website with 301 error. If the SSL version of website does not exist,
    	 * make an HTTP request.
    	 */
    	Set<String> nestedLinks = getSecureRequest("https://www." + url);
    	if (nestedLinks == null)
    		nestedLinks = getUnsecureRequest("https://www." + url);
    	
        for(String pageLink : nestedLinks)
        {
        	//System.out.println(pageLink);
        	Set<String> linkSet = incomingLinks.keySet();
        	for(String linkKey : linkSet) {
        		/*
        		 * Check if the parent URL contains link to the other candidate URLs.
        		 * Also, avoid counting the links to self for the incoming counter.
        		 */
        		if(pageLink.contains(linkKey) && !pageLink.contains(url)) {
        			//System.out.println(pageLink + " CONTAINS  " + linkKey);
        			int old = incomingLinks.get(linkKey);
        			incomingLinks.put(linkKey, old+1);
        			break;
        		}
        	}
            this.links.add(pageLink);
        }
    }

    /**
     * Generate all the candidate URLs for ranking.
     * Parses the EAIT assignment link and performs
     * string manipulation and returns the result URLs
     * @param incomingLinks The URLs are the key to the map.
     * @return A set containing all the URLs need to ranked.
     */
    static Set<String> crawlEaitLink(Map incomingLinks) {
  	  Set<String> sites = new HashSet<String>();
      for(int page_no = 0; page_no < 20 ; page_no++) {
		  String eaitPage = "http://topsites.eaiti.com/?page=" + page_no;
          Set<String> urlSet = getUnsecureRequest(eaitPage);
		  //System.out.println("Found (" + linksOnPage.size() + ") links");
          for(String link : urlSet)
          {
        	  if(link.startsWith("/site/"))
        	  {
        		  String key = link.substring(6);
        		  sites.add(key);
        	  }
          }
      }
      for(String site : sites)
    	  incomingLinks.put(site, 0);
      return sites;

    }
    public List<String> getLinks()
    {
        return this.links;
    }
    
    private static Set<String> getUnsecureRequest(String URL) {
    	Set<String> urls = new HashSet<String>();
    	try {
	        
	        URL url = new URL(URL);
	 
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setInstanceFollowRedirects(true);
			con.setConnectTimeout(4000);
			con.setReadTimeout(4000);
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
			con.setRequestProperty("Accept-Charset", "UTF-8");
	 
			//System.out.println("Response Code : " + con.getResponseCode());
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();
	 
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();
	 
			Pattern p1 = Pattern.compile("<a href=\"(.*?)\">");
			Matcher m = p1.matcher(response.toString());
			while(m.find()) {
				   try {
					   //System.out.println(m.group(1));
					   urls.add(m.group(1));
				   }
				   catch (IllegalStateException exp) {
					   
				   }
			}

			Pattern p2 = Pattern.compile("<a href='(.*?)'>");
			Matcher m2 = p2.matcher(response.toString());
			while(m2.find()) {
			   try {
				   //System.out.println(m.group(1));
				   urls.add(m2.group(1));
			   }
			   catch (IllegalStateException exp) {
				   
			   }
			}

    	}
    	catch (IOException ioe) {
    		
    	}
    	return urls;
    }
    
    private static Set<String> getSecureRequest(String URL) {
    	Set<String> urls = new HashSet<String>();
    	try {
	        
	        URL url = new URL(URL);
	 
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setInstanceFollowRedirects(true);
			con.setConnectTimeout(4000);
			con.setReadTimeout(4000);
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
			con.setRequestProperty("Accept-Charset", "UTF-8");
	 
			//System.out.println("Response Code : " + con.getResponseCode());
			int respCode = con.getResponseCode();
			if(respCode > 299)
				return null;
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();
	 
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();
	 
			Pattern p1 = Pattern.compile("<a href=\"(.*?)\">");
			Matcher m = p1.matcher(response.toString());
			while(m.find()) {
				   try {
					   //System.out.println(m.group(1));
					   urls.add(m.group(1));
				   }
				   catch (IllegalStateException exp) {
					   
				   }
			}

			Pattern p2 = Pattern.compile("<a href='(.*?)'>");
			Matcher m2 = p2.matcher(response.toString());
			while(m2.find()) {
			   try {
				   //System.out.println(m.group(1));
				   urls.add(m2.group(1));
			   }
			   catch (IllegalStateException exp) {
				   
			   }
			}

    	}
    	catch (IOException ioe) {
    		return null;
    	}
    	
    	return urls;
    }

}