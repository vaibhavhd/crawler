package eait.assignment;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Engine
{
  private static final int CRAWLING_LIMIT = 1;
  private Set<String> visited;
  private List<String> toVisit;
  private Map<String, Integer> incomingLinks;

  public Engine() {
	  this.incomingLinks = new HashMap<String, Integer>();
  }
  
  /**
   * Initiates the crawler for the parentUrl and the URLs contained in it.
   * The limit of number of HTML pages to be crawled is set by CRAWLING_LIMIT.
   * @param parentUrl: The primary URL that will undergo a ranking test.
   */
  public void startSearch(String parentUrl)
  {
	  visited = new HashSet<String>();
	  toVisit = new LinkedList<String>();
	  System.out.println("Starting crawl for " + parentUrl);
      while(this.visited.size() < CRAWLING_LIMIT)
      {
          String currentUrl;
          Crawler leg = new Crawler();
          if(this.toVisit.isEmpty()) {
              currentUrl = parentUrl;
              this.toVisit.add(parentUrl);
              this.visited.add(parentUrl);
          }
          else {
              currentUrl = this.nextUrl();
          }
          
          if(currentUrl == null)
        	  return;
          leg.crawl(this.incomingLinks ,currentUrl); 

          this.toVisit.addAll(leg.getLinks());
      }
  }


  /**
   * Check and return the unvisited nested URL
   * @return A new URL to be crawled
   */
  private String nextUrl()
  {
      String nextUrl;

      do
      {
          nextUrl = this.toVisit.remove(0);
      } while(this.visited.contains(nextUrl) && this.toVisit.size() != 0);
      
      if(this.toVisit.size() == 0)
    	  return null;
      this.visited.add(nextUrl);
      return nextUrl;
  }
  
  public Set<String> importAllWebsites()
  {
	  return Crawler.crawlEaitLink(this.incomingLinks);
  }
  
  Map<String, Integer> getTopNWebsites() {
	Set<String> result = new HashSet<String>();  
	Map sortedLinks = this.sortMapByIncomingLinks(incomingLinks);
	return sortedLinks;
  }
  
  Map sortMapByIncomingLinks(Map incomingLinks) {
	  Map sorted = new TreeMap(new LinksComparator(incomingLinks));
	  sorted.putAll(incomingLinks);
	  return sorted;
  }
  
  /*
   * subClass LinksComparator:
   * Use: Sorts the links Map based on the values.
   * Values are no of incoming edges to the parent link.
   */
  class LinksComparator implements Comparator {
	Map map;
	
	public LinksComparator (Map map) {
		this.map = map;
	}
	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		Comparable valueA = (Comparable) map.get(arg0);
		Comparable valueB = (Comparable) map.get(arg1);
		return valueB.compareTo(valueA);
	}
	  
  }
}
