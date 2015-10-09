package eu.uqasar.util.reporting;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.logging.Logger;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(Arquillian.class)
public class UtilTest extends TestCase {

	private static Logger logger = Logger.getLogger(UtilTest.class);


	@Test
	public void testCreateExpressionEditor() {
		logger.info("Testing createExpressionEditor");
		String res = Util.createExpressionEditor(null,"http://uqasar.pythonanywhere.com");
		Assert.assertNotNull(res);
		//TODO VERIFY CORRECT JSON FORMAT WHEN IT IS DEFINITIVE

	}


	@Test
	public void testRetrieveDimensionsKO() {
		logger.info("Testing retrieveDimensionsKO");
		List res = Util.retrieveDimensions("http://uqasar.pythonanywhere.com/model");
		Assert.assertNull(res);
	}


	@Test
	public void testRetrieveDimensionsJsonExc() {
		logger.info("Testing RetrieveDimensionsJsonExc");
		List res = Util.retrieveDimensions("http://uqasar.pythonanywhere.com/cubes"); //json exception
		Assert.assertNotNull(res);
		Assert.assertEquals(res, new ArrayList());

	}


	@Test
	public void testRetrieveDimensionsError() {
		logger.info("Testing retrieveDimensionsError");
		List res = Util.retrieveDimensions("http://uqasar.pythonanywhere.com/cube/sonar/model"); 
		Assert.assertNull(res);

	}

	@Test
	public void testRetrieveDimensionsOK() {
		logger.info("Testing retrieveDimensionsOK");
		List res = Util.retrieveDimensions("http://uqasar.pythonanywhere.com/cube/jira/model"); 
		Assert.assertNotNull(res);
		Assert.assertEquals("Project", res.get(0));
	}

	@Test
	public void testCreateSummaryTable() {
		logger.info("Testing createSummaryTable");
		String urlToLoad = "http://uqasar.pythonanywhere.com/cube/jira/aggregate?&cut=Project:UQASAR";
		JSONObject cuberesponse = Util.readJsonFromUrl(urlToLoad);
		String res =Util.createSummaryTable(cuberesponse);
		Assert.assertNotNull(res);
		Assert.assertNotSame("<table cellpadding='10'>", res);
	}	

	@Test
	public void testGetAvailableCubes() {
		logger.info("Testing getAvailableCubes");

		String uqasarOLAPDNS = "http://uqasar.pythonanywhere.com/";
		String  cubes = Util.getAvailableCubes(uqasarOLAPDNS);
		Assert.assertNotNull(cubes);

	}	


	@Test
	public void testGetAvailableCubesKO() {
		logger.info("Testing getAvailableCubes KO");

		String uqasarOLAPDNS = "http://uqasar.pythonanywhere/";
		try {
			Util.getAvailableCubes(uqasarOLAPDNS);
			Assert.assertTrue(false);
		} catch (Exception ex) {
			Assert.assertTrue(true);
		}

	}


	@Test
	public void testConstructCubeRetrieverURL() {
		logger.info("Testing constructCubeRetrieverURL");

		String cubeurl = "http://uqasar.pythonanywhere.com/cube/jira";
		String cuberules = "{\"condition\": \"AND\",\"rules\": [ {  \"id\": \"Project\", \"field\": \"Project\", \"type\": \"string\", \"input\": \"select\", \"operator\": \"equal\", \"value\": \"UQASAR\"}]}";
		String expected = "http://uqasar.pythonanywhere.com/cube/jira/aggregate?&cut=Project:UQASAR";

		String urlToLoad = Util.constructCubeRetrieverURL(cuberules, cubeurl);
		Assert.assertEquals(urlToLoad, expected);

	}

	@Test
	public void testConstructCubeRetrieverURLDrilldown() {
		logger.info("Testing constructCubeRetrieverURLDrilldown");

		String cubeurl = "http://uqasar.pythonanywhere.com/cube/jira";
		String cuberules = "{\"condition\": \"AND\",\"rules\": [ { \"id\": \"drilldown\", \"field\": \"drilldown\", \"type\": \"string\", \"input\": \"select\", \"operator\": \"equal\", \"value\": \"Project\" }]}";
		String expected = "http://uqasar.pythonanywhere.com/cube/jira/aggregate?&drilldown=Project";

		String urlToLoad = Util.constructCubeRetrieverURL(cuberules, cubeurl);
		Assert.assertEquals(urlToLoad, expected);

	}

}