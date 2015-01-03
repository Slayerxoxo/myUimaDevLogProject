package linkInterMessageDetector.linkDetection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import linkInterMessageDetector.zim2collocationNetwork.CollocationNetworkModel;
import linkInterMessageDetector.linkDetection.LexicalChainsNetworkModel;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceAccessException;

import common.types.Token;

import linkInterMessageDetector.mbox2lexicalChain.LexicalChain;
import my.types.Message;

public class LexicalChainsNetworkBuilderAE extends JCasAnnotator_ImplBase {

	public final static String RES_LC_KEY = "LC_Key";
	@ExternalResource(key = RES_LC_KEY)
	private LexicalChainsNetworkModel lexicalChainsNetwork;
	
	public final static String RES_CN_KEY = "CN_Key";
	@ExternalResource(key = RES_CN_KEY)
	private CollocationNetworkModel collocationNetwork;
	
	public final static String resourceDestFilename = "lexicalChains.txt";
	
	public final static double THRESHOLD = 5.0;
	
	@Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {

		try {
			collocationNetwork = (CollocationNetworkModel) getContext().getResourceObject(RES_CN_KEY);
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			System.out.println("Erreur du collocationNetwork");
		}
		try {
			lexicalChainsNetwork = (LexicalChainsNetworkModel) getContext().getResourceObject(RES_LC_KEY);
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			System.out.println("Erreur du lexicalChain");
		}
		
		Message message = JCasUtil.selectSingle(aJCas, Message.class);
		if (message.getId() != null)
		{
			String id = message.getId().substring(1, message.getId().length()-1);
			Map<LexicalChain,Integer> chainMap = new HashMap<LexicalChain,Integer>();
			for (Token token : JCasUtil.selectCovered(Token.class, message)) {
				String word1 = token.getCoveredText().toLowerCase();
				Double max_score = 0.0;
				LexicalChain max_chain = new LexicalChain();
				Set<LexicalChain> oldChains = new HashSet<LexicalChain>();
				for (LexicalChain lexicalChain : chainMap.keySet()) {
					chainMap.put(lexicalChain,chainMap.get(lexicalChain)+1);
					if(chainMap.get(lexicalChain) >= 20) {
						oldChains.add(lexicalChain);
					}
					Double average = 0.0;
					for (String word2 : lexicalChain.getLexicalChain()) {
						average += collocationNetwork.getScore(word1, word2);	
					}
					average /= lexicalChain.size();
					if (average > max_score) {
						max_score = average;
						max_chain = lexicalChain;
					}
				}
				if (chainMap.isEmpty() || max_score < THRESHOLD) {
					LexicalChain lexicalChain = new LexicalChain();
					lexicalChain.addItem(word1);
					chainMap.put(lexicalChain,0);
				}
				else {
					max_chain.addItem(word1);
					chainMap.put(max_chain,0);
					if (oldChains.contains(max_chain)) {
						oldChains.remove(max_chain);
					}
				}
				for (LexicalChain old : oldChains) {
					chainMap.remove(old);
					lexicalChainsNetwork.add_chain(id, old);
				}
			}
			for (LexicalChain lexicalChain : chainMap.keySet()) {
				lexicalChainsNetwork.add_chain(id, lexicalChain);
			}		
		}
	}
	
	@Override
	public void collectionProcessComplete()  throws AnalysisEngineProcessException {
		lexicalChainsNetwork.save(resourceDestFilename);
		System.out.println("collectionProcessComplete pour LCBuilder");
	}
}
