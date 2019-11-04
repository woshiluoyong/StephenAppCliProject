package com.stephen.cli.project.library;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 名称：AbStrUtil.java 
 * 描述：json处理类.
 */
public class JsonUtil<T> {
	
	/**
	 * 
	 * 描述：将对象转化为json.
	 */
	public static String toJson(Object src) {
		String json = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			json = gson.toJson(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 
	 * 描述：将列表转化为json.
	 * @param list
	 * @return
	 */
	public static String toJson(List<?> list) {
		String json = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			json = gson.toJson(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 
	 * 描述：将json转化为列表.
	 * @param json
	 * @param typeToken new TypeToken<ArrayList<?>>() {}.getType();
	 * @return
	 */
	public static List<?> fromJson(String json,TypeToken typeToken) {
		List<?> list = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			Type type = typeToken.getType();
			list = gson.fromJson(json,type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 
	 * 描述：将json转化为对象.
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static Object fromJson(String json,Class clazz) {
		if(TextUtils.isEmpty(json))return null;
		Object obj = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
			obj = gson.fromJson(json,clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

}
