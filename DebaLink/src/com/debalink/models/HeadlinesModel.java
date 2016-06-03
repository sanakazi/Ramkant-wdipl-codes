package com.debalink.models;

public class HeadlinesModel {
	private String headlineId,userId, dateTime, description, isActive,
			userNameHeadline, profilePic;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHeadlineId() {
		return headlineId;
	}

	public void setHeadlineId(String headlineId) {
		this.headlineId = headlineId;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getUserNameHeadline() {
		return userNameHeadline;
	}

	public void setUserNameHeadline(String userNameHeadline) {
		this.userNameHeadline = userNameHeadline;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
}
