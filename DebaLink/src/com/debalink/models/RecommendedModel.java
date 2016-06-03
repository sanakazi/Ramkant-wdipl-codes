package com.debalink.models;

public class RecommendedModel {
	String DRecommendId, userId, discussionId, discussionTitle, displayImageUrl, createdDate, categoryName, senderName, receiverName,
			receiverID,headlineId,description,type,publicationId,publicationTitle,lockPassword;

	public String getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(String publicationId) {
		this.publicationId = publicationId;
	}

	public String getPublicationTitle() {
		return publicationTitle;
	}

	public void setPublicationTitle(String publicationTitle) {
		this.publicationTitle = publicationTitle;
	}

	public String getLockPassword() {
		return lockPassword;
	}

	public void setLockPassword(String lockPassword) {
		this.lockPassword = lockPassword;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDRecommendId() {
		return DRecommendId;
	}

	public void setDRecommendId(String dRecommendId) {
		DRecommendId = dRecommendId;
	}

	public String getUserId() {
		return userId;
	}

	public String getHeadlineId() {
		return headlineId;
	}

	public void setHeadlineId(String headlineId) {
		this.headlineId = headlineId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDiscussionId() {
		return discussionId;
	}

	public void setDiscussionId(String discussionId) {
		this.discussionId = discussionId;
	}

	public String getDiscussionTitle() {
		return discussionTitle;
	}

	public void setDiscussionTitle(String discussionTitle) {
		this.discussionTitle = discussionTitle;
	}

	public String getDisplayImageUrl() {
		return displayImageUrl;
	}

	public void setDisplayImageUrl(String displayImageUrl) {
		this.displayImageUrl = displayImageUrl;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverID() {
		return receiverID;
	}

	public void setReceiverID(String receiverID) {
		this.receiverID = receiverID;
	}
}
