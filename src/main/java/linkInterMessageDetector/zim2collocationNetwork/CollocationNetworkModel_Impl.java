/**
 * 
 */
package linkInterMessageDetector.zim2collocationNetwork;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import common.util.MiscUtil;

/**
 * Implementation of the resource model for collocation network
 * No resource actually loaded 
 */
public final class CollocationNetworkModel_Impl implements CollocationNetworkModel, SharedResourceObject {
	private Map<String,Map<String,Double>> collocationNetwork = new TreeMap <String, Map<String, Double>>();

	final static private double nullValue = 0.0;
	final static private double initialValue = 1.0;
	final static private double incrementValue = 1.0;
	
	private String fieldSeparator = "\t";
	private Boolean isSaved = false;
	private Boolean alreadyLoaded = false;
	
	@Override
    public synchronized void load(DataResource aData) throws ResourceInitializationException {
   	 // TODO : more tests
   	 InputStream inStr = null;
   	 try {
   		 // open input stream to data
   		 inStr = aData.getInputStream();
   		 System.out.println("toto");
   		 // read each line
   		 BufferedReader reader = new BufferedReader(new InputStreamReader(inStr));
   		 String line;
   		 while ((line = reader.readLine()) != null) {
   			 String[] c = line.trim().split(fieldSeparator);
   			 if(c.length == 3){
   				if(!collocationNetwork.containsKey(c[0])){
   					collocationNetwork.put(c[0], new TreeMap<String, Double>());
   				}
   				collocationNetwork.get(c[0]).put(c[1], Double.valueOf(c[2]));
   			 }
   		 }
   	 } catch (IOException e) {
   		 throw new ResourceInitializationException(e);
   	 } finally {
   		 if (inStr != null) {
   			 try {
   				 inStr.close();
   			 } catch (IOException e) {
   			 }
   		 }
   	 }
   	 isSaved=false;
    }

	
	@Override
	public Boolean contains(String word) {
		return collocationNetwork.containsKey(word);
	}
	
	@Override
	public Set<String> getCollocated(String word) {
		return collocationNetwork.get(word).keySet();
	}
	
	private void association(String word1, String word2){
		if(!collocationNetwork.containsKey(word1)){
			collocationNetwork.put(word1, new TreeMap<String, Double>());
	   	}
	   	if(!collocationNetwork.get(word1).containsKey(word2)){
	   		collocationNetwork.get(word1).put(word2, nullValue);
	   	}
	   	collocationNetwork.get(word1).put(word2,collocationNetwork.get(word1).get(word2) + incrementValue);
	}

	@Override
	public void inc(String word1, String word2) {
		word1 = word1.toLowerCase();
	   	word2 = word2.toLowerCase();
		association(word1, word2);
	   	association(word2, word1);
	    isSaved=false;
	}
	
	@Override
	public Double getScore(String word1, String word2) {
		Double score = nullValue;
		if(collocationNetwork.containsKey(word1) && collocationNetwork.get(word1).containsKey(word2)){
			score = collocationNetwork.get(word1).get(word2);
		}
		return score;
	}
	
	@Override
	public Integer getCollocatedSize(String word1) {
		Integer size = 0;
		if(collocationNetwork.containsKey(word1)){
			size = collocationNetwork.get(word1).size();
		}
		return size;
	}
	
	@Override
	public Integer size() {
		return collocationNetwork.size();
	}
	
	@Override
	public void echo() {
		for(String word1 : collocationNetwork.keySet()){
			for(String word2 : collocationNetwork.get(word1).keySet()){
				System.out.println("(" + word1 + ", " + ") = " + collocationNetwork.get(word1).get(word2));
			}
		}
	}
	
	@Override
	public synchronized void save(String filename){
		if(!isSaved){    
			StringBuffer stringbuffer = new StringBuffer();
	   		for(String word1 : collocationNetwork.keySet()){
	   			for(String word2 : getCollocated(word1)){
	   				stringbuffer.append(word1);
	   				stringbuffer.append(fieldSeparator);
	   				stringbuffer.append(word2);
	   				stringbuffer.append(fieldSeparator);
	   				stringbuffer.append(collocationNetwork.get(word1).get(word2));
	   				stringbuffer.append("\n");
	   			}
	   		}
	   		MiscUtil.writeToFS(stringbuffer.toString(), filename);
	   		isSaved=true;
	   	}
	}
}
