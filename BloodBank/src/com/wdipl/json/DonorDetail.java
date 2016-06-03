package com.wdipl.json;

public class DonorDetail {
	private String donarName;
	private String contactNo;
	private String bloodGroup;
	private String address;
	private boolean isAddedToFavourite;

	public boolean isAddedToFavourite() {
		return isAddedToFavourite;
	}
	public void setAddedToFavourite(boolean isAddedToFavourite) {
		this.isAddedToFavourite = isAddedToFavourite;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String id;
	public String getDonarName() {
		return donarName;
	}
	public void setDonarName(String donarName) {
		this.donarName = donarName;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getBloodGroup() {
		return bloodGroup;
	}
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	

}
