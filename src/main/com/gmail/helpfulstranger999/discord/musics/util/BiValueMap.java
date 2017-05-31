package com.gmail.helpfulstranger999.discord.musics.util;

import java.util.HashMap;

import com.google.common.collect.Maps;

public class BiValueMap <K, V, A>{
	
	protected HashMap<K, V> mapOne;
	protected HashMap<K, A> mapTwo;

	public BiValueMap() {
		mapOne = Maps.newHashMap();
		mapTwo = Maps.newHashMap();
	}
	
	public void put (K key, V value, A anotherValue) {
		mapOne.put(key, value);
		mapTwo.put(key, anotherValue);
	}
	
	public V getValue1 (K key) {
		return mapOne.get(key);
	}
	
	public A getValue2 (K key) {
		return mapTwo.get(key);
	}
	
	public boolean containsKey (K key) {
		boolean v = mapOne.containsKey(key);
		boolean a = mapTwo.containsKey(key);
		boolean t = v && a;
		return t;
	}
	
	public boolean containsValue (V value) {
		return mapOne.containsValue(value);
	}
	
	public boolean containsValue2 (A value) {
		return mapTwo.containsValue(value);
	}
	
	public boolean updateValue1 (K key, V value) {
		return mapOne.replace(key, mapOne.get(key), value);
	}
	
	public boolean updateValue2 (K key, A anotherValue) {
		return mapTwo.replace(key, mapTwo.get(key), anotherValue);
	}
	
	public void remove (K key) {
		mapOne.remove(key);
		mapTwo.remove(key);
	}
	
	public void removeValue1 (V value) {
		mapTwo.remove(mapOne.get(value));
		mapOne.remove(value);
	}
	
	public void removeValue2 (A anotherValue) {
		mapOne.remove(mapTwo.get(anotherValue));
		mapTwo.remove(anotherValue);
	}
	
	public int size () {
		return mapOne.size();
	}
	
	public static <K, V, A> BiValueMap<K, V, A> create() {
		return new BiValueMap<K, V, A>();
	}

}
