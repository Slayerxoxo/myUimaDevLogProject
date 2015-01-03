/**
 * 
 */

package linkInterMessageDetector.linkDetection;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;
import linkInterMessageDetector.wordSegmenter.StopWordModel_Impl;
import linkInterMessageDetector.zim2collocationNetwork.CollocationNetworkModel_Impl;
import linkInterMessageDetector.linkDetection.LinkerAE;
import linkInterMessageDetector.mbox2lexicalChain.MBoxMessageParserAE;
import linkInterMessageDetector.wordSegmenter.WordSegmenterAE;
import linkInterMessageDetector.mbox2lexicalChain.MboxReaderCR;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ExternalResourceDescription;

/**
 * Illustrate how to configure 
 * and run annotators with the shared model object.
 */
public class MboxWF {

	public static void main(String[] args) throws Exception {

		// Creation of the external resource description
		ExternalResourceDescription stopWordsResourceDesc = createExternalResourceDescription(
				StopWordModel_Impl.class,
				"file:resourceManagement/data/stopwords-fr.txt");
	
		ExternalResourceDescription collocationNetworkResourceDesc = createExternalResourceDescription(
				CollocationNetworkModel_Impl.class,
				"output/testCollocationNetwork.tsv");
		
		ExternalResourceDescription lexicalChainsResourceDesc = createExternalResourceDescription(
				LexicalChainsNetworkModel_Impl.class,
				"");
		
		ExternalResourceDescription threadResourceDesc = createExternalResourceDescription(
				ThreadModel_Impl.class,
				"file:data/thread-messageId.digest");

		AnalysisEngineDescription parserAED = createEngineDescription(
				MBoxMessageParserAE.class,
				MBoxMessageParserAE.PARAM_DEST_DIR, 
				"data/ubuntu-fr.mbox");

		AnalysisEngineDescription segmenterAED = createEngineDescription(
				WordSegmenterAE.class, 
				WordSegmenterAE.RES_KEY, 
				stopWordsResourceDesc);
		
		AnalysisEngineDescription lcAED = createEngineDescription(
				LexicalChainsNetworkBuilderAE.class,
				LexicalChainsNetworkBuilderAE.RES_CN_KEY, collocationNetworkResourceDesc,
				LexicalChainsNetworkBuilderAE.resourceDestFilename, "lexicalChains.txt",
				LexicalChainsNetworkBuilderAE.RES_LC_KEY, lexicalChainsResourceDesc);
				
		AnalysisEngineDescription linkerAED = createEngineDescription(
				LinkerAE.class,
				LinkerAE.RES_LC_KEY, lexicalChainsResourceDesc,
				LinkerAE.RES_Thread_KEY, threadResourceDesc,
				LinkerAE.resultsFilename, "results.txt");
	
		CollectionReaderDescription crd = createReaderDescription(
				MboxReaderCR.class,
				MboxReaderCR.PARAM_MBOX_SRCPATH, "data/ubuntu-fr.mbox", 
				MboxReaderCR.PARAM_LANGUAGE, "fr",
				MboxReaderCR.PARAM_ENCODING, "iso-8859-1");

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(parserAED);
		builder.add(segmenterAED, CAS.NAME_DEFAULT_SOFA, "bodyCas");
		builder.add(lcAED, CAS.NAME_DEFAULT_SOFA, "bodyCas");
		builder.add(linkerAED, CAS.NAME_DEFAULT_SOFA, "bodyCas");
		
		// Run the pipeline
		SimplePipeline.runPipeline(crd,builder.createAggregateDescription()); //aaed
	}


}

