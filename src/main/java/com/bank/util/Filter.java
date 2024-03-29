package com.bank.util;

public class Filter {

//	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//
//	public static void filterByDate(JSONArray arr, Date filterDate) {
//		for(JSONObject obj : arr) {
//			long time = ((JSONObject) obj).getLong("Time");
//			String date = dateFormat.format(TimeUtil.getDateTime(time));
//			if (!date.equals(filterDate)) {
//				arr.remove(getIndex(arr, (JSONObject)obj));
//			}
//		}
//	}
//
//	public static void filterByType(JSONArray arr, String filterType) {
//		for(JSONObject obj : arr) {
//			String type = ((JSONObject) obj).getString("Type");
//			if (!type.equals(filterType)) {
//				arr.remove(getIndex(arr, (JSONObject)obj));
//			}
//		}
//	}
//	
//	private static int getIndex(JSONArray jsonArray, JSONObject objectToFind) {
//        int length = jsonArray.length();
//        for (int i = 0; i < length; i++) {
//            try {
//                JSONObject currentObject = jsonArray.getJSONObject(i);
//                if (currentObject.similar(objectToFind)) {
//                    return i;
//                }
//            } catch (JSONException e) {
//            }
//        }
//        return -1;
//    }
}
