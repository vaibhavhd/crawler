package eait.assignment;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class eaitCrawler {

    public static void main(String[] args)
    {
    	int N, i=1;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the value N (To get top N websites)");
        N = sc.nextInt();
        
        Engine engine = new Engine();
        Set<String> sites = engine.importAllWebsites();

        for(String site : sites)
        	engine.startSearch(site);
        
        Map<String, Integer> result = (TreeMap)engine.getTopNWebsites();

        for(String link : result.keySet()) {
        	System.out.println("Website Rank #" + i++ + " " + link);
        	if(i == N + 1)
        		break;
        }
        System.out.println("COMPLTETED!");
    }
}