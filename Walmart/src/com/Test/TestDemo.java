package com.Test;
import com.Model.*;
import com.View.*;

import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import com.Controller.*;
public class TestDemo {
	 public static void main(String[] args) throws ParserConfigurationException, SAXException, ParseException {

	      //fetch student record based on his roll no from the database
		 String productString = args[0];
	      User user  = retriveUser(productString);

	      //Create a view : to write student details on console
	      UserView view = new UserView();

	      HomeworkApplication controller = new HomeworkApplication(user, view);

	      controller.RunAPI();
	   }

	   private static User retriveUser(String productString){
	      User userInit = new User();
	      userInit.setApiKey("xqedndw5ramnhv7ymtqkresw");
	      userInit.setProductString(productString);
	      return userInit;
	   }
}
