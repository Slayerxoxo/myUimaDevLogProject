package linkInterMessageDetector.linkDetection;

import java.util.Set;

import linkInterMessageDetector.mbox2lexicalChain.LexicalChain;;

public interface LexicalChainsNetworkModel {
	
	public void add_chain(String messageId, LexicalChain lexicalChain);
	
	public Set<LexicalChain> getMessageChains(String messageId);
	
	public void save(String filename);
}
