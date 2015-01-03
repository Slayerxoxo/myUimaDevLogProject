package linkInterMessageDetector.linkDetection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import common.util.MiscUtil;

import linkInterMessageDetector.mbox2lexicalChain.LexicalChain;;

public final class LexicalChainsNetworkModel_Impl implements LexicalChainsNetworkModel, SharedResourceObject {
	private Map<String,Set<LexicalChain>> chains = new HashMap<String,Set<LexicalChain>>();

	public void add_chain(String messageId, LexicalChain lexicalChain) {
		if (!chains.containsKey(messageId))
			chains.put(messageId, new HashSet<LexicalChain>());
		chains.get(messageId).add(lexicalChain);
	}

	@Override
	public synchronized void save(String filename) {
		MiscUtil.writeToFS(toString(),filename);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (String messageId : chains.keySet()) {
			stringBuilder.append(">>> "+messageId+"\n");
			for (LexicalChain chain : chains.get(messageId)) {
				for (String word : chain.getLexicalChain()) {
					stringBuilder.append(word).append(" ");
				}
				stringBuilder.append("\n");
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public void load(DataResource aData) throws ResourceInitializationException {
		chains = new HashMap<String,Set<LexicalChain>>();
	}

	@Override
	public Set<LexicalChain> getMessageChains(String messageId) {
		if (chains.containsKey(messageId))
			return chains.get(messageId);
		else
			return new HashSet<LexicalChain>();
	}

}