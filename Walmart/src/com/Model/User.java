package com.Model;

import java.util.HashMap;
import java.util.Map;

public class User {
	private String productString;
	private String apiKey;
	private Map<String, Double> listofReviews = new HashMap<String, Double>();

	public String getapiKey() {
		return apiKey;
	}

	public void setProductString(String productString) {
		this.productString = productString;
	}

	public String getProductString() {
		return productString;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public Map<String, Double> getListofReview() {
		return listofReviews;
	}

	public void setListofReview(Map<String, Double> listofReviews) {
		this.listofReviews = listofReviews;
	}
}
