package com.View;

import java.util.Map;

public class UserView {
	 public void printProductRatings(Map<String, Double> productRatings){
	      System.out.println("List of Ranked Recommendation ");
	      for (Map.Entry<String,Double> entry : productRatings.entrySet()) {
        	  String key = entry.getKey();
        	  Double value = entry.getValue();
        	  System.out.println("Item is: " + key + " and its rating is:" +value);
        	}
}
}
