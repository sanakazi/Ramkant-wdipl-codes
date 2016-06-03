package com.debalink.models;

public class MessageModel {
	private String messageId, from,to, date, subject, isRead, receiverIsRead;
	private boolean isSelected=false,isMarked=false;
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getTo() {
		return to;
	}

	public boolean isMarked() {
		return isMarked;
	}

	public void setMarked(boolean isMarked) {
		this.isMarked = isMarked;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	public String getReceiverIsRead() {
		return receiverIsRead;
	}

	public void setReceiverIsRead(String receiverIsRead) {
		this.receiverIsRead = receiverIsRead;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return from+" "+subject+" "+isRead;
	}
}
