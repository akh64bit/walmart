package com.HelperFunctions;
import java.util.Comparator;
import java.util.Map;
/* This function sorts the Map according to its key*/
public class HelperFunctions implements Comparator {
 
	Map<String, Double> map;
 
	public HelperFunctions(Map<String, Double> map) {
		this.map = map;
	}
 
	@Override
	public int compare(Object keyA, Object keyB) {
		Comparable valueA = (Comparable) map.get(keyA);
		Comparable valueB = (Comparable) map.get(keyB);
		return valueB.compareTo(valueA);
	}
}
