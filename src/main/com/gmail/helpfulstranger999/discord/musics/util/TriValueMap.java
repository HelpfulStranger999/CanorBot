package com.gmail.helpfulstranger999.discord.musics.util;

import java.util.HashMap;

import com.google.common.collect.Maps;

public class TriValueMap <K, V, A, T>{
	
	protected HashMap<K, V> mapOne;
	protected HashMap<K, A> mapTwo;
	protected HashMap<K, T> mapThree;

	public TriValueMap() {
		mapOne = Maps.newHashMap();
		mapTwo = Maps.newHashMap();
		mapThree = Maps.newHashMap();
	}
	
	public void put (K key, V value, A anotherValue, T  thirdValue) {
		mapOne.put(key, value);
		mapTwo.put(key, anotherValue);
		mapThree.put(key, thirdValue);
	}
	
	public V getValue1 (K key) {
		return mapOne.get(key);
	}
	
	public A getValue2 (K key) {
		return mapTwo.get(key);
	}
	
	public T getValue3 (K key) {
		return mapThree.get(key);
	}
	
	public boolean containsKey (K key) {
		boolean v = mapOne.containsKey(key);
		boolean a = mapTwo.containsKey(key);
		boolean t = mapThree.containsKey(key);
		boolean total = ((v && a) && t);
		return total;
	}
	
	public boolean containsValue (V value) {
		return mapOne.containsValue(value);
	}
	
	public boolean containsValue2 (A value) {
		return mapTwo.containsValue(value);
	}
	
	public boolean containsValue3 (T value) {
		return mapThree.containsValue(value);
	}
	
	public boolean updateValue1 (K key, V value) {
		return mapOne.replace(key, mapOne.get(key), value);
	}
	
	public boolean updateValue2 (K key, A anotherValue) {
		return mapTwo.replace(key, mapTwo.get(key), anotherValue);
	}
	
	public boolean updateValue3 (K key, T thirdValue) {
		return mapThree.replace(key, mapThree.get(key), thirdValue);
	}
	
	public void remove (K key) {
		mapOne.remove(key);
		mapTwo.remove(key);
		mapThree.remove(key);
	}
	
	public void removeValue1 (V value) {
		mapTwo.remove(mapOne.get(value));
		mapThree.remove(mapOne.get(value));
		mapOne.remove(value);
	}
	
	public void removeValue2 (A anotherValue) {
		mapOne.remove(mapTwo.get(anotherValue));
		mapThree.remove(mapTwo.get(anotherValue));
		mapTwo.remove(anotherValue);
	}
	
	public void removeValue3 (T thirdValue) {
		mapOne.remove(mapThree.get(thirdValue));
		mapTwo.remove(mapThree.get(thirdValue));
		mapThree.remove(thirdValue);
	}
	
	public int size () {
		return mapOne.size();
	}
	
	public static <K, V, A, T> TriValueMap<K, V, A, T> create() {
		return new TriValueMap<K, V, A, T>();
	}

}
