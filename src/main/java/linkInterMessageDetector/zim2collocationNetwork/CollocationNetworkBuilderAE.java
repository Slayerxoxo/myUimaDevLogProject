/**
 * 
 */
package linkInterMessageDetector.zim2collocationNetwork;


import java.util.ArrayList;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import common.types.Token;


/**
 * Annotator that builds collocation 
 * 
 */
public class CollocationNetworkBuilderAE extends JCasAnnotator_ImplBase {

	public final static String RES_KEY = "aKey";
	@ExternalResource(key = RES_KEY)
	private CollocationNetworkModel collocationNetwork;

	final static Integer WINDOW_SIZE = 2;
	
	public static final String PARAM_RESOURCE_DEST_FILE = "resourceDestFilename";
	@ConfigurationParameter(name = PARAM_RESOURCE_DEST_FILE, mandatory = true, defaultValue="/tmp/collocationNetwork.csv")
	private String resourceDestFilename;
	
	// Size 
	public static final String PARAM_WINDOW_SIZE = "windowSize";
	@ConfigurationParameter(name = PARAM_WINDOW_SIZE, mandatory = false, defaultValue="3")
	private Integer windowSize;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
	   	 // Prints the instance ID to the console - this proves the same instance
	   	 // of the SharedModel is used in both Annotator instances.
	   	 System.out.println(getClass().getSimpleName() + ": " + collocationNetwork);
	   	 ArrayList<Token> tokens = new ArrayList<Token>(JCasUtil.select(aJCas, Token.class));
	   	 for (int i = 0; i < tokens.size(); i++){
	   		 String current = tokens.get(i).getCoveredText().toLowerCase();
	   		 for (int j = 1; j < windowSize && i+j < tokens.size(); j++) {
	   				 String collocated = tokens.get(i+j).getCoveredText().toLowerCase();
	   				 collocationNetwork.inc(current, collocated);
	   		 }
	   	 }
	}

	
	@Override
	public void collectionProcessComplete()  throws AnalysisEngineProcessException {
		// Prints the instance ID to the console - this proves the same instance
		// of the SharedModel is used in both Annotator instances.
		System.out.println(getClass().getSimpleName() + ": " + collocationNetwork);
		collocationNetwork.save(resourceDestFilename);
	}
}