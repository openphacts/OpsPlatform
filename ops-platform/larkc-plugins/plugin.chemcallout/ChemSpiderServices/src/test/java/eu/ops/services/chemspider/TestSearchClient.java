package eu.ops.services.chemspider;

import java.util.List;
import java.net.URI;

import javax.xml.bind.JAXBContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import eu.ops.services.chemspider.model.ArrayOfInt;
import eu.ops.services.chemspider.model.ERequestStatus;

public class TestSearchClient {
	private final Log log = LogFactory.getLog(TestSearchClient.class);
	private static SearchClient client; 
	private static String OPS_TOKEN = "5d749a0a-f4b0-444b-8287-aba2c2800ebaXt";
	private static String CHEMSPIDER_WS = "http://inchi.chemspider.com/Search.asmx";
	private static String ACCOLATE = "O=S(=O)(c1ccccc1C)NC(=O)c2ccc(c(OC)c2)Cc4c3cc(ccc3n(c4)C)NC(=O)OC5CCCC5";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new SearchClient();
		client.setServiceUrl(new URI(CHEMSPIDER_WS));
		client.setToken(OPS_TOKEN);
		client.setJaxbContext(JAXBContext.newInstance(ERequestStatus.class, ArrayOfInt.class));
	}

	@Test
	public void testSimilaritySearch() {
		String rid = client.similaritySearch(ACCOLATE, "Tanimoto", .99);
		log.info("RID="+rid);
		while (true) {
			pause(1);
			ERequestStatus status = client.getAsyncSearchStatus(rid);
			log.info("Status="+status.name());
			if (status == ERequestStatus.RESULT_READY) {
				List<Integer> results = client.getAsyncSearchResult(rid);
				for (Integer csid : results) {
					log.info("CSID: "+csid);
				}
				return;
			}
		}
	}
	
	@Ignore
	@Test
	public void testAsyncStatus() {
		ERequestStatus status = client.getAsyncSearchStatus("99f01835-0e98-4df6-88d9-8d5517f2fae5");
		log.info("Status="+status);
	}
	
	private void pause(long s) {
		try {
			Thread.currentThread().sleep(s * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
