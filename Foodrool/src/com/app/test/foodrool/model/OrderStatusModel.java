package com.app.test.foodrool.model;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderStatusModel {
	private String statusId,statusText;

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
	public static OrderStatusModel parseResult(JSONObject jObj)throws JSONException {
		OrderStatusModel item = new OrderStatusModel();
		item.setStatusId(jObj.getString("Id"));
		item.setStatusText(jObj.getString("Status"));
		
		return item;
	}
}
