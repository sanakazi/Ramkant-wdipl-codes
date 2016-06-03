package com.debalink.models;

public class DiscussionModel {
	private String discussionId,discussinTitle,userId, categoryId, lockPassword, lockKey, categoryName,
			commentCnt, likeCnt, userName, userNameComment, noOfView,
			displayFlag, displayImageUrl, createdDate, isReportFlag,
			islikeStatus, profilePic;
	private boolean isActive,isDownload;

	public String getDiscussionId() {
		return discussionId;
	}

	public void setDiscussionId(String discussionId) {
		this.discussionId = discussionId;
	}

	public String getDiscussinTitle() {
		return discussinTitle;
	}

	public void setDiscussinTitle(String discussinTitle) {
		this.discussinTitle = discussinTitle;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getLockPassword() {
		return lockPassword;
	}

	public void setLockPassword(String lockPassword) {
		this.lockPassword = lockPassword;
	}

	public String getLockKey() {
		return lockKey;
	}

	public void setLockKey(String lockKey) {
		this.lockKey = lockKey;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCommentCnt() {
		return commentCnt;
	}

	public void setCommentCnt(String commentCnt) {
		this.commentCnt = commentCnt;
	}

	public String getLikeCnt() {
		return likeCnt;
	}

	public void setLikeCnt(String likeCnt) {
		this.likeCnt = likeCnt;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserNameComment() {
		return userNameComment;
	}

	public void setUserNameComment(String userNameComment) {
		this.userNameComment = userNameComment;
	}

	public String getNoOfView() {
		return noOfView;
	}

	public void setNoOfView(String noOfView) {
		this.noOfView = noOfView;
	}

	

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

	public String getDisplayFlag() {
		return displayFlag;
	}

	public void setDisplayFlag(String displayFlag) {
		this.displayFlag = displayFlag;
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

	public String getIsReportFlag() {
		return isReportFlag;
	}

	public void setIsReportFlag(String isReportFlag) {
		this.isReportFlag = isReportFlag;
	}

	public String getIslikeStatus() {
		return islikeStatus;
	}

	public void setIslikeStatus(String islikeStatus) {
		this.islikeStatus = islikeStatus;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
}
