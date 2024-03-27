package com.bank.adapter;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.custom.exceptions.BankingException;

public class JSONAdapter {

	public static <E> JSONArray transactionTOJson(List<E> list) throws BankingException {
		JSONArray arr = new JSONArray();
		for (E element : list) {
			JSONObject obj;
			obj = new JSONObject(element);
			arr.put(obj);
		}
		return arr;
	}

	public static JSONObject objToJSONObject(Object obj) throws BankingException {
		return new JSONObject(obj);
	}

	public static <K, V> JSONObject mapToJSON(Map<K, V> map) {
		return new JSONObject(map);
	}
}