package com.debalink.models;

public class PollOptionModel {
	private String voteTypeId,voteName,isVote,voteCount;
	private boolean isSelected;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getVoteTypeId() {
		return voteTypeId;
	}

	public void setVoteTypeId(String voteTypeId) {
		this.voteTypeId = voteTypeId;
	}

	public String getVoteName() {
		return voteName;
	}

	public void setVoteName(String voteName) {
		this.voteName = voteName;
	}

	public String getIsVote() {
		return isVote;
	}

	public void setIsVote(String isVote) {
		this.isVote = isVote;
	}

	public String getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(String voteCount) {
		this.voteCount = voteCount;
	}
}
