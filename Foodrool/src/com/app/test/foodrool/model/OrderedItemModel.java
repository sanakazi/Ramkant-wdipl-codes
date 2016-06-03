package com.app.test.foodrool.model;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderedItemModel {
	private String itemId,itemName,quantity;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public static OrderedItemModel parseResult(JSONObject jObj)throws JSONException {
		OrderedItemModel item = new OrderedItemModel();
		item.setItemId(jObj.getString("FoodItemId"));
		item.setItemName(jObj.getString("FoodItemName"));
		item.setQuantity(jObj.getString("Quantity"));
		
		return item;
	}
}
