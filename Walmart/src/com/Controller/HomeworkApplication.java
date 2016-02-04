package com.Controller;
import com.Model.*;
import com.View.*;
import com.HelperFunctions.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
 
/**
 * Walmart Homework
 * @author Akhilesh.Kumar
 * @date 03/February/2016
 */
public class HomeworkApplication {
	private User user;
	private UserView view;
	
	public HomeworkApplication(User user, UserView view){
	      this.user = user;
	      this.view = view;
	   }
 
	public void RunAPI() throws ParserConfigurationException, SAXException, ParseException 
    {
        String keyId = user.getapiKey();
        String queryToken = user.getProductString();
        String productSearch = "http://api.walmartlabs.com/v1/search?format=xml&apiKey=";
        String query = "&query=";
        String productRecommendation ="http://api.walmartlabs.com/v1/nbp?format=xml&apiKey=";
        String itemId ="&itemId=";
        String productSearchResponse =null;
        ArrayList<String> productRecommendationList = null;
        String productReviewCall ="http://api.walmartlabs.com/v1/reviews/";
        String review = "?format=xml&apiKey=";
        Map<String, Double> productRatings = new HashMap<String, Double>();
 
        String URLProductSearchCall = productSearch + keyId + query + queryToken;
         
        try
        {
        	productSearchResponse = httpGetProduct(URLProductSearchCall);
        	System.out.println("Got Response from Product Search API:");
        	System.out.println("Item ID of first product is: " +productSearchResponse );
        	System.out.println("Using the first product ID to call Product Recommendation API:");
        } 
        catch (IOException e) 
        {
            // Handle exception here, currently just printing stack trace
            e.printStackTrace();
        }
        
        String URLProductRecommendationCall = productRecommendation + keyId + itemId + productSearchResponse;
        
        try
        {
        	productRecommendationList = httpGetProductResponse(URLProductRecommendationCall);
            System.out.println("Received the response from Product Recommendation API:");
        } 
        catch (IOException e) 
        {
            // Handle exception here, currently just printing stack trace
            e.printStackTrace();
        }
        
        try
        {
             productRatings = httpGetReview(productReviewCall, review, keyId, productRecommendationList);
             System.out.println("Received the response from Product Review API:");
        } 
        catch (IOException e) 
        {
            // Handle exception here, currently just printing stack trace
            e.printStackTrace();
        }
        // Pass the values of ranked reviews to User class
        user.setListofReview(productRatings);
     // Display the  ranked reviews using the View class.
        view.printProductRatings(user.getListofReview());
    }
     
    /**
     * Call URL and buffer response into a String
     * @param urlString URL to be called
     * @return String The response XML String
     * @throws IOException
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */
    public static String httpGetProduct(String urlString) throws IOException, ParserConfigurationException, SAXException 
    {
          URL url = new URL(urlString);
          HttpURLConnection conn =
              (HttpURLConnection) url.openConnection();
 
          // Check for successful response code or throw error
          if (conn.getResponseCode() != 200) 
          {
            throw new IOException(conn.getResponseMessage());
          }
 
          // Buffer the result into a string
          BufferedReader buffrd = new BufferedReader(
              new InputStreamReader(conn.getInputStream()));
          StringBuilder sb = new StringBuilder();
          String line;
          while ((line = buffrd.readLine()) != null) 
          {
            sb.append(line);
          }
 
          buffrd.close();
          conn.disconnect();
          String xml = sb.toString();
          //Parse the XML response and extract the first itemID.
          DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
          InputSource src = new InputSource();
          src.setCharacterStream(new StringReader(xml));

          Document doc = (Document) builder.parse(src);
          String itemId = ((org.w3c.dom.Document) doc).getElementsByTagName("itemId").item(0).getTextContent();
          return itemId;
          
    }
     
    /**
     * Call URL and buffer response into a String
     * @param urlString URL to be called
     * @return String The response XML String
     * @throws MalformedURLException 
     * @throws IOException
     * @throws ParseException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */
    /**
     * @param urlString
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static ArrayList<String> httpGetProductResponse(String urlString) throws IOException, ParseException 
    {
          URL url = new URL(urlString);
          HttpURLConnection conn =
              (HttpURLConnection) url.openConnection();
 
          // Check for successful response code or throw error
          if (conn.getResponseCode() != 200) 
          {
            throw new IOException(conn.getResponseMessage());
          }
          BufferedReader buffrd = new BufferedReader(
                  new InputStreamReader(conn.getInputStream()));
              StringBuilder sb = new StringBuilder();
              String line;
              while ((line = buffrd.readLine()) != null) 
              {
                sb.append(line);
              }
     
              buffrd.close();
              conn.disconnect();
              String content = sb.toString();
          //Save the json response in three.json file
          String path = "three.json";
          Files.write( Paths.get(path), content.getBytes(), StandardOpenOption.CREATE);
          //Extract the parentItem ID from the JSON file.
          Scanner filename = new Scanner(new File("three.json"));
          ArrayList<String> al=new ArrayList<>();
          while(filename.hasNext())
          {
        	  String ak=filename.next();
        	  if(ak.contains("\"parentItemId\":"))
        	  {
        		  String s1= "";
        		  Pattern MY_PATTERN = Pattern.compile("\"parentItemId\":(.*?)\\,");
        				  Matcher m = MY_PATTERN.matcher(ak);
        				  while (m.find()) {
        				      s1 = m.group(1);
        				  }
        				  
        		  al.add(s1);
        	  }
        	  
          }
          filename.close();
          //Use only the top 10 parentItemID from the JSON file.
          ArrayList<String>all = new ArrayList<String>();
          if(al.size()>=10)
          {
          all = new ArrayList<String> (al.subList(0, 10));
          }
          else
          {System.out.println("No of Product Recommendation returned is less than 10");
          all = new ArrayList<String> (al.subList(0, al.size()));
          }
          return (all);
    }
    
    
    /**
     * Call URL and buffer response into a String
     * @param urlString URL to be called
     * @return String The response XML String
     * @throws IOException
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */

	public static Map<String,Double> httpGetReview(String urlString, String review, String keyId, ArrayList<String> productRecommendationList) throws IOException, ParserConfigurationException, SAXException 
    {
    	String reviewURL = null;
    	Map<String, Double> productRatings = new HashMap<String, Double>();
    	for(int i=0;i<productRecommendationList.size();i++)
    	{ reviewURL = urlString + productRecommendationList.get(i).toString() + review + keyId;
          URL url = new URL(reviewURL);
          HttpURLConnection conn =
              (HttpURLConnection) url.openConnection();
 
          // Check for successful response code or throw error
          if (conn.getResponseCode() != 200) 
          {
            throw new IOException(conn.getResponseMessage());
          }
 
          // Buffer the result into a string
          BufferedReader buffrd = new BufferedReader(
              new InputStreamReader(conn.getInputStream()));
          StringBuilder sb = new StringBuilder();
          String line;
          while ((line = buffrd.readLine()) != null) 
          {
            sb.append(line);
          }
 
          buffrd.close();
          conn.disconnect();
          String xml = sb.toString();
          //Parse the xml and extract itemID and corresponding averageOverallrating. Save the two in a HashMap.
          DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
          InputSource src = new InputSource();
          src.setCharacterStream(new StringReader(xml));
          Document doc = (Document) builder.parse(src);
          String item = ((org.w3c.dom.Document) doc).getElementsByTagName("itemId").item(0).getTextContent();
          String itemRating = ((org.w3c.dom.Document) doc).getElementsByTagName("averageOverallRating").item(0).getTextContent();
          productRatings.put(item, Double.parseDouble(itemRating));
    }
    	//Sort the HashMap using its value ( averageOverallratings)
        Map<String, Double> sortedMap = new TreeMap<String, Double>(new HelperFunctions(productRatings));
		sortedMap.putAll(productRatings);
		return sortedMap;
    	    	
    }
}