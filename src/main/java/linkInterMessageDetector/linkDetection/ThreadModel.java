package linkInterMessageDetector.linkDetection;

import java.util.Set;


public interface ThreadModel {
	
	public Integer getThreadId(String messageId);
	
	public Set<String> getMessagesFromThread(Integer id);
}
