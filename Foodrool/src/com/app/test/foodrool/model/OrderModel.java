package com.app.test.foodrool.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderModel implements Serializable {
	private static final long serialVersionUID = -2038560232166947361L;
	private String orderNo, orderId, CustId, restaurantId, status, restaurantName, restaurantContactNo,
			restaurantAddress, restaurantContactNo1, restaurantContactNo2, firstName, lastName, customerMobileNo,
			customerAddress, paymentMode, instruction,orderDate,deliveryDate,deliveryCharges;
	public String getDeliveryCharges() {
		return deliveryCharges;
	}

	public void setDeliveryCharges(String deliveryCharges) {
		this.deliveryCharges = deliveryCharges;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	private double customerLng, customerLat;

	public String getRestaurantContactNo1() {
		return restaurantContactNo1;
	}

	public void setRestaurantContactNo1(String restaurantContactNo1) {
		this.restaurantContactNo1 = restaurantContactNo1;
	}

	public String getRestaurantContactNo2() {
		return restaurantContactNo2;
	}

	public void setRestaurantContactNo2(String restaurantContactNo2) {
		this.restaurantContactNo2 = restaurantContactNo2;
	}

	private boolean isCurrentOrder;

	public boolean isCurrentOrder() {
		return isCurrentOrder;
	}

	public void setCurrentOrder(boolean isCurrentOrder) {
		this.isCurrentOrder = isCurrentOrder;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCustId() {
		return CustId;
	}

	public void setCustId(String custId) {
		CustId = custId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public String getRestaurantContactNo() {
		return restaurantContactNo;
	}

	public void setRestaurantContactNo(String restaurantContactNo) {
		this.restaurantContactNo = restaurantContactNo;
	}

	public String getRestaurantAddress() {
		return restaurantAddress;
	}

	public void setRestaurantAddress(String restaurantAddress) {
		this.restaurantAddress = restaurantAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCustomerMobileNo() {
		return customerMobileNo;
	}

	public void setCustomerMobileNo(String customerMobileNo) {
		this.customerMobileNo = customerMobileNo;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public double getCustomerLat() {
		return customerLat;
	}

	public void setCustomerLat(double d) {
		this.customerLat = d;
	}

	public double getCustomerLng() {
		return customerLng;
	}

	public void setCustomerLng(double customerLng) {
		this.customerLng = customerLng;
	}

	public static OrderModel parseResult(JSONObject jObj) throws JSONException {
		OrderModel order = new OrderModel();
		order.setOrderId(jObj.optString("OrderId"));
		order.setOrderNo(jObj.optString("OrderNo"));
		order.setCustId(jObj.optString("CustId"));
		order.setStatus(jObj.optString("Status"));
		order.setRestaurantName(jObj.optString("RestaurantName"));
		order.setRestaurantId(jObj.optString("RestaurantId"));
		order.setRestaurantContactNo(jObj.optString("RestaurantContactNo"));
		order.setRestaurantContactNo1(jObj.optString("RestaurantContactNo1", "No Contact"));
		order.setRestaurantContactNo2(jObj.optString("RestaurantContactNo2", "No Contact"));
		order.setRestaurantAddress(jObj.optString("RestaurantAddress"));
		order.setFirstName(jObj.optString("FirstName"));
		order.setLastName(jObj.optString("LastName"));
		order.setCustomerAddress(jObj.optString("CustomerAddress"));
		order.setCustomerMobileNo(jObj.optString("CustomerMobileNo"));
		order.setPaymentMode(jObj.optString("PaymentMode"));
		order.setInstruction(jObj.optString("Instruction"));

		if (jObj.has("IsCurrentOrder")) {
			order.setCurrentOrder(jObj.optBoolean("IsCurrentOrder"));
		}
		order.setCustomerLat(Double.parseDouble(jObj.optString("CustomerLatitutde","0")));
		order.setCustomerLng(Double.parseDouble(jObj.optString("CustomerLongitude","0")));
		return order;
	}
	
	public static OrderModel parseHistoryResult(JSONObject jObj) throws JSONException {
		OrderModel order = new OrderModel();
		order.setOrderId(jObj.optString("orderid"));
		order.setOrderNo(jObj.optString("OrderNo"));
		order.setFirstName(jObj.optString("FirstName"));
		order.setLastName(jObj.optString("LastName"));
		order.setOrderDate(jObj.optString("OrderCreatedOn"));
		order.setDeliveryDate(jObj.optString("OrderDeliveryDate"));
		order.setRestaurantName(jObj.optString("RestaurantName"));
		order.setPaymentMode(jObj.optString("PaymentMode"));
		order.setInstruction(jObj.optString("Instruction"));
		order.setDeliveryCharges(jObj.optString("DeliveryCharges"));

		if (jObj.has("IsCurrentOrder")) {
			order.setCurrentOrder(jObj.optBoolean("IsCurrentOrder"));
		}
		order.setCustomerLat(Double.parseDouble(jObj.optString("CustomerLatitutde","0")));
		order.setCustomerLng(Double.parseDouble(jObj.optString("CustomerLongitude","0")));
		
		order.setRestaurantName(jObj.optString("RestaurantName"));
		order.setRestaurantId(jObj.optString("RestaurantId"));
		order.setRestaurantContactNo(jObj.optString("RestaurantContactNo"));
		order.setRestaurantContactNo1(jObj.optString("RestaurantContactNo1", "No Contact"));
		order.setRestaurantContactNo2(jObj.optString("RestaurantContactNo2", "No Contact"));
		order.setRestaurantAddress(jObj.optString("RestaurantAddress"));
		order.setFirstName(jObj.optString("FirstName"));
		order.setLastName(jObj.optString("LastName"));
		order.setStatus(jObj.optString("OrderStatus","Delivered"));
		order.setCustomerAddress(jObj.optString("CustomerAddress"));
		order.setCustomerMobileNo(jObj.optString("CustomerMobileNo"));
		
		return order;
	}
}
