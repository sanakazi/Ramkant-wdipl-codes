package com.debalink.webservices;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import android.content.ContentValues;
import android.util.Log;
import com.debalink.models.CategoryModel;
import com.debalink.models.CommentModel;
import com.debalink.models.CountryModel;
import com.debalink.models.DiscussionModel;
import com.debalink.models.FeedsModel;
import com.debalink.models.HeadlinesModel;
import com.debalink.models.MessageModel;
import com.debalink.models.NotificationModel;
import com.debalink.models.OwnHeadlineModel;
import com.debalink.models.PollOptionModel;
import com.debalink.models.PublicationModel;
import com.debalink.models.RecommendedModel;
import com.debalink.models.SearchModel;
import com.debalink.models.SubscribersModel;
import com.debalink.models.UserModel;
import com.debalink.utils.Contant;

public class HttpRequest {
	String URL = "http://192.168.1.225:99/Service.asmx";
	String NAMESPACE = "http://tempuri.org/";

	public String getDataFromServer(ContentValues cv, String methodName) throws SocketException, SocketTimeoutException, XmlPullParserException, IOException {
		String response = null;

		SoapObject request = new SoapObject(NAMESPACE, methodName);
		request = addRequestParameter(request, cv);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 2 * 60 * 1000);

		androidHttpTransport.call(NAMESPACE + methodName, envelope);
		Log.i("Data", envelope.getResponse().toString());
		response = envelope.getResponse().toString();

		return response;
	}

	public SoapObject getSoapObjectFromServer(ContentValues cv, String methodName) throws SocketException, SocketTimeoutException, XmlPullParserException, IOException {
		SoapObject response = null;

		SoapObject request = new SoapObject(NAMESPACE, methodName);
		if (cv != null)
			request = addRequestParameter(request, cv);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 5 * 60 * 1000);

		androidHttpTransport.call(NAMESPACE + methodName, envelope);
		// Log.i("Data", envelope.getResponse().toString());

		response = (SoapObject) envelope.getResponse();

		return response;
	}

	public SoapObject parseLoginResponse(SoapObject response) {

		SoapObject newDataSetObject = (SoapObject) response.getProperty("NewDataSet");

		SoapObject tableObject = (SoapObject) newDataSetObject.getProperty(0);

		return tableObject;
	}

	public ArrayList<HeadlinesModel> parseHeadlinesResponse(SoapObject response) {
		ArrayList<HeadlinesModel> headlinesList = new ArrayList<HeadlinesModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<HeadlinesModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			HeadlinesModel headlines = new HeadlinesModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			headlines.setHeadlineId(data.getPropertySafelyAsString("HeadlineId", ""));
			headlines.setDescription(data.getPropertySafelyAsString("Description", ""));
			headlines.setProfilePic(Contant.PIC_URL + data.getPropertySafelyAsString("ProfilePic", ""));

			Log.i("pic url", headlines.getProfilePic());

			headlines.setIsActive(data.getPropertySafelyAsString("IsActive", ""));
			headlines.setUserNameHeadline(data.getPropertySafelyAsString("UserNameHeadline", ""));
			headlines.setDateTime(data.getPropertySafelyAsString("HeadlineDate", ""));
			headlines.setUserId(data.getPropertySafelyAsString("UserId", ""));
			headlinesList.add(headlines);
		}

		return headlinesList;
	}

	public ArrayList<OwnHeadlineModel> parseOwnHeadlinesResponse(SoapObject response) {
		ArrayList<OwnHeadlineModel> headlinesList = new ArrayList<OwnHeadlineModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<OwnHeadlineModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			OwnHeadlineModel headlines = new OwnHeadlineModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			headlines.setSrNo(data.getPropertySafelyAsString("SrNo", ""));
			headlines.setiD(data.getPropertySafelyAsString("ID", ""));
			headlines.setName(data.getPropertySafelyAsString("Name", ""));

			headlines.setCnt(data.getPropertySafelyAsString("Cnt", ""));
			headlines.setType(data.getPropertySafelyAsString("Type", ""));

			headlinesList.add(headlines);
		}

		return headlinesList;
	}

	public ArrayList<DiscussionModel> parseDiscussinResponse(SoapObject response) {
		ArrayList<DiscussionModel> discussionsList = new ArrayList<DiscussionModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<DiscussionModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			DiscussionModel discussion = new DiscussionModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);

			discussion.setDiscussionId(data.getPropertySafelyAsString("DiscussionId", ""));
			discussion.setDiscussinTitle(data.getPropertySafelyAsString("DiscussionTitle", ""));
			discussion.setUserId(data.getPropertySafelyAsString("UserId", ""));
			discussion.setCategoryId(data.getPropertySafelyAsString("CategoryId", ""));
			discussion.setLockPassword(data.getPropertySafelyAsString("LockPassword", ""));
			discussion.setLockKey(data.getPropertySafelyAsString("lockkey", ""));
			discussion.setCategoryName(data.getPropertySafelyAsString("CategoryName", ""));
			discussion.setCommentCnt(data.getPropertySafelyAsString("CommentCnt", ""));
			discussion.setLikeCnt(data.getPropertySafelyAsString("LikeCnt", ""));
			discussion.setUserName(data.getPropertySafelyAsString("UserName", ""));

			if (discussion.getUserName().equals("")) {
				discussion.setUserName(data.getPropertySafelyAsString("CreatedBy", ""));
			}

			discussion.setUserNameComment(data.getPropertySafelyAsString("UserNameComment", ""));
			discussion.setNoOfView(data.getPropertySafelyAsString("NoOfView", ""));

			discussion.setDisplayFlag(data.getPropertySafelyAsString("DisplayFlag", ""));
			discussion.setDisplayImageUrl(Contant.DISCUSSION_PIC_URL + data.getPropertySafelyAsString("DisplayImageUrl", ""));
			discussion.setCreatedDate(data.getPropertySafelyAsString("CreatedDate", ""));
			discussion.setIsReportFlag(data.getPropertySafelyAsString("IsReportFlag", ""));
			discussion.setIslikeStatus(data.getPropertySafelyAsString("IslikeStatus", ""));
			discussion.setProfilePic(Contant.PIC_URL + data.getPropertySafelyAsString("ProfilePic", ""));

			try {

				if (discussion.getLockPassword().equalsIgnoreCase("anyType{}")) {
					discussion.setLockPassword("");
				}

				discussion.setActive(Boolean.parseBoolean(data.getPropertySafelyAsString("IsActive", "")));
			} catch (Exception e) {

			}

			discussionsList.add(discussion);
		}

		return discussionsList;
	}

	public ArrayList<HeadlinesModel> parsePinsafeHeadlinesResponse(SoapObject response) {
		ArrayList<HeadlinesModel> headlinesList = new ArrayList<HeadlinesModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<HeadlinesModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			HeadlinesModel headlines = new HeadlinesModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			headlines.setHeadlineId(data.getPropertySafelyAsString("HeadlineId", ""));
			headlines.setDescription(data.getPropertySafelyAsString("Description", ""));
			headlines.setProfilePic(Contant.PIC_URL + data.getPropertySafelyAsString("Photourl", ""));

			Log.i("pic url", headlines.getProfilePic());

			headlines.setIsActive(data.getPropertySafelyAsString("IsActive", ""));
			headlines.setUserNameHeadline(data.getPropertySafelyAsString("OwnerName", ""));
			headlines.setDateTime(data.getPropertySafelyAsString("HeadlineDate", ""));
			headlines.setUserId(data.getPropertySafelyAsString("UserId", ""));
			headlinesList.add(headlines);
		}

		return headlinesList;
	}

	public ArrayList<DiscussionModel> parsePinsafeDiscussinResponse(SoapObject response) {
		ArrayList<DiscussionModel> discussionsList = new ArrayList<DiscussionModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<DiscussionModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			DiscussionModel discussion = new DiscussionModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);

			discussion.setDiscussionId(data.getPropertySafelyAsString("DiscussionId", ""));
			discussion.setDiscussinTitle(data.getPropertySafelyAsString("DiscussionTitle", ""));
			discussion.setUserId(data.getPropertySafelyAsString("UserId", ""));
			discussion.setCategoryId(data.getPropertySafelyAsString("CategoryId", ""));
			discussion.setLockPassword(data.getPropertySafelyAsString("LockPassword", ""));
			discussion.setLockKey(data.getPropertySafelyAsString("lockkey", ""));
			discussion.setCategoryName(data.getPropertySafelyAsString("CategoryName", ""));
			discussion.setCommentCnt(data.getPropertySafelyAsString("CommentCnt", ""));
			discussion.setLikeCnt(data.getPropertySafelyAsString("LikeCnt", ""));
			discussion.setUserName(data.getPropertySafelyAsString("OwnerName", ""));

			if (discussion.getUserName().equals("")) {
				discussion.setUserName(data.getPropertySafelyAsString("CreatedBy", ""));
			}

			discussion.setUserNameComment(data.getPropertySafelyAsString("UserNameComment", ""));
			discussion.setNoOfView(data.getPropertySafelyAsString("NoOfView", ""));

			discussion.setDisplayFlag(data.getPropertySafelyAsString("DisplayFlag", ""));
			discussion.setDisplayImageUrl(Contant.DISCUSSION_PIC_URL + data.getPropertySafelyAsString("DisplayImageUrl", ""));
			discussion.setCreatedDate(data.getPropertySafelyAsString("CreatedDate", ""));
			discussion.setIsReportFlag(data.getPropertySafelyAsString("IsReportFlag", ""));
			discussion.setIslikeStatus(data.getPropertySafelyAsString("IslikeStatus", ""));
			discussion.setProfilePic(Contant.PIC_URL + data.getPropertySafelyAsString("ProfilePic", ""));

			try {
				discussion.setActive(Boolean.parseBoolean(data.getPropertySafelyAsString("IsActive", "")));
			} catch (Exception e) {

			}

			discussionsList.add(discussion);
		}

		return discussionsList;
	}

	public ArrayList<RecommendedModel> parseRecommendDetailedResponse(SoapObject response, String type) {
		ArrayList<RecommendedModel> discussionsList = new ArrayList<RecommendedModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<RecommendedModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			RecommendedModel dataModel = new RecommendedModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);

			if (type.equalsIgnoreCase("publication")) {
				dataModel.setDRecommendId(data.getPropertySafelyAsString("PRecommendId", ""));
				dataModel.setUserId(data.getPropertySafelyAsString("UserId", ""));
				dataModel.setPublicationId(data.getPropertySafelyAsString("PublicationId", ""));
				dataModel.setPublicationTitle(data.getPropertySafelyAsString("PublicationName", ""));
				dataModel.setDisplayImageUrl(Contant.PUBLICATION_PIC_URL + data.getPropertySafelyAsString("DisplayImageUrl", ""));
				dataModel.setCreatedDate(data.getPropertySafelyAsString("CreatedDate", ""));
				dataModel.setCategoryName(data.getPropertySafelyAsString("CategoryName", ""));
				dataModel.setSenderName(data.getPropertySafelyAsString("SenderName", ""));
				dataModel.setReceiverName(data.getPropertySafelyAsString("ReceiverName", ""));
				dataModel.setReceiverID(data.getPropertySafelyAsString("ReceiverID", ""));
				dataModel.setCategoryName(data.getPropertySafelyAsString("CategoryName", ""));
				dataModel.setDescription(data.getPropertySafelyAsString("Description", ""));
				dataModel.setType(type);
			} else {

				dataModel.setDRecommendId(data.getPropertySafelyAsString("DRecommendId", ""));
				dataModel.setUserId(data.getPropertySafelyAsString("UserId", ""));
				dataModel.setDiscussionId(data.getPropertySafelyAsString("DiscussionId", ""));
				dataModel.setDiscussionTitle(data.getPropertySafelyAsString("DiscussionTitle", ""));
				dataModel.setDisplayImageUrl(Contant.DISCUSSION_PIC_URL + data.getPropertySafelyAsString("DisplayImageUrl", ""));
				dataModel.setCreatedDate(data.getPropertySafelyAsString("CreatedDate", ""));
				dataModel.setCategoryName(data.getPropertySafelyAsString("CategoryName", ""));
				dataModel.setSenderName(data.getPropertySafelyAsString("SenderName", ""));
				dataModel.setReceiverName(data.getPropertySafelyAsString("ReceiverName", ""));
				dataModel.setReceiverID(data.getPropertySafelyAsString("ReceiverID", ""));
				dataModel.setCategoryName(data.getPropertySafelyAsString("CategoryName", ""));
				dataModel.setHeadlineId(data.getPropertySafelyAsString("HeadlineId", ""));
				dataModel.setDescription(data.getPropertySafelyAsString("Description", ""));
				dataModel.setType(type);
			}

			discussionsList.add(dataModel);
		}

		return discussionsList;
	}

	public ArrayList<UserModel> parseUserList(SoapObject response) {
		ArrayList<UserModel> userList = new ArrayList<UserModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<UserModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			UserModel user = new UserModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);

			if (!data.hasProperty("SubcriberRequestcnt")) {

				user.setUserId(data.getPropertySafelyAsString("ReceiverId", ""));
				user.setUserProfilePic(Contant.PIC_URL + data.getPropertySafelyAsString("PhotoUrl", ""));
				user.setUserName(data.getPropertySafelyAsString("Name", ""));

				userList.add(user);
			}
		}

		return userList;
	}

	public ArrayList<PublicationModel> parsePinsafepublicationResponse(SoapObject response) {
		ArrayList<PublicationModel> publicationsList = new ArrayList<PublicationModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<PublicationModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			PublicationModel publication = new PublicationModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);

			publication.setPublicationId(data.getPropertySafelyAsString("PublicationId", ""));
			publication.setUserId(data.getPropertySafelyAsString("UserId", ""));
			publication.setPublicationName(data.getPropertySafelyAsString("PublicationName", ""));
			publication.setLockPassword(data.getPropertySafelyAsString("LockPassword", ""));
			publication.setLockKey(data.getPropertySafelyAsString("lockkey", ""));
			publication.setNoOfView(data.getPropertySafelyAsString("NoOfView", ""));
			publication.setCommentCnt(data.getPropertySafelyAsString("CommentCnt", ""));
			publication.setAverageRating(data.getPropertySafelyAsString("AverageRating", "0"));
			publication.setPublicationMainContain(data.getPropertySafelyAsString("PublicationMainContain", ""));
			publication.setDisplayImageUrl(Contant.PUBLICATION_PIC_URL + data.getPropertySafelyAsString("DisplayImageUrl", ""));
			publication.setCreatedDate(data.getPropertySafelyAsString("CreatedDate", ""));
			publication.setCreatedBy(data.getPropertySafelyAsString("OwnerName", ""));
			publication.setCategoryName(data.getPropertySafelyAsString("CategoryName", ""));

			publicationsList.add(publication);
		}

		return publicationsList;
	}

	public ArrayList<PublicationModel> parsePublicationResponse(SoapObject response) {
		ArrayList<PublicationModel> publicationsList = new ArrayList<PublicationModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<PublicationModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			PublicationModel publication = new PublicationModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);

			publication.setPublicationId(data.getPropertySafelyAsString("PublicationId", ""));
			publication.setUserId(data.getPropertySafelyAsString("UserId", ""));
			publication.setPublicationName(data.getPropertySafelyAsString("PublicationName", ""));
			publication.setLockPassword(data.getPropertySafelyAsString("LockPassword", ""));
			publication.setLockKey(data.getPropertySafelyAsString("lockkey", ""));
			publication.setNoOfView(data.getPropertySafelyAsString("NoOfView", ""));
			publication.setCommentCnt(data.getPropertySafelyAsString("CommentCnt", ""));
			publication.setAverageRating(data.getPropertySafelyAsString("AverageRating", "0"));
			publication.setPublicationMainContain(data.getPropertySafelyAsString("PublicationMainContain", ""));
			publication.setDisplayImageUrl(Contant.PUBLICATION_PIC_URL + data.getPropertySafelyAsString("DisplayImageUrl", ""));
			publication.setCreatedDate(data.getPropertySafelyAsString("CreatedDate", ""));
			publication.setCreatedBy(data.getPropertySafelyAsString("CreatedBy", ""));
			publication.setCategoryName(data.getPropertySafelyAsString("CategoryName", ""));

			try {

				if (publication.getLockPassword().equalsIgnoreCase("anyType{}")) {
					publication.setLockPassword("");
				}

				publication.setActive(Boolean.parseBoolean(data.getPropertySafelyAsString("isactive", "")));
				// publication.setDownload(Boolean.parseBoolean(data.getPropertySafelyAsString("CategoryName",
				// "")));
			} catch (Exception e) {
				e.printStackTrace();
			}

			publicationsList.add(publication);
		}

		return publicationsList;
	}

	// parseFeedsResponse
	public ArrayList<FeedsModel> parseFeedsResponse(SoapObject response) {
		ArrayList<FeedsModel> feedsList = new ArrayList<FeedsModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<FeedsModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			FeedsModel feeds = new FeedsModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);

			feeds.setDocumentId(data.getPropertySafelyAsString("DocumentId", ""));
			feeds.setUserId(data.getPropertySafelyAsString("UserId", ""));
			feeds.setDocumentTitle(data.getPropertySafelyAsString("DocumentTitle", ""));
			feeds.setProfilePic(Contant.PIC_URL + data.getPropertySafelyAsString("ProfilePic", ""));
			feeds.setName(data.getPropertySafelyAsString("Name", ""));
			feeds.setAction(data.getPropertySafelyAsString("Action", ""));
			feeds.setCreateDate(data.getPropertySafelyAsString("CreateDate", ""));
			feeds.setDisplayImageUrl(data.getPropertySafelyAsString("DisplayImageUrl", "0"));
			feeds.setImage(data.getPropertySafelyAsString("Image", ""));
			feeds.setType(data.getPropertySafelyAsString("type", ""));
			feedsList.add(feeds);
		}

		return feedsList;
	}

	public ArrayList<SubscribersModel> parseSubscribersListResponse(SoapObject response) {
		ArrayList<SubscribersModel> subscribersList = new ArrayList<SubscribersModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<SubscribersModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			SubscribersModel subscriber = new SubscribersModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);

			if (!data.hasProperty("SubcriberRequestcnt")) {

				subscriber.setUserId(data.getPropertySafelyAsString("UserId", ""));
				subscriber.setName(data.getPropertySafelyAsString("Name", ""));
				subscriber.setProfilePicUrl(Contant.PIC_URL + data.getPropertySafelyAsString("PhotoUrl", ""));
				subscriber.setUserName(data.getPropertySafelyAsString("UserName", ""));
				subscriber.setEmailId(data.getPropertySafelyAsString("EmailId", ""));

				subscribersList.add(subscriber);
			}
		}

		return subscribersList;
	}

	public ArrayList<NotificationModel> parseNotificationResponse(SoapObject response) {
		ArrayList<NotificationModel> notificationsList = new ArrayList<NotificationModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<NotificationModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			NotificationModel notification = new NotificationModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);

			notification.setUserId(data.getPropertySafelyAsString("UserId", ""));
			notification.setUniqueId(data.getPropertySafelyAsString("UniqueId", ""));
			notification.setProfilePic(Contant.PIC_URL + data.getPropertySafelyAsString("ProfilePic", ""));
			notification.setUserName(data.getPropertySafelyAsString("UserName", ""));
			notification.setDocumentId(data.getPropertySafelyAsString("DocumentId", ""));
			notification.setType1(data.getPropertySafelyAsString("Type1", ""));
			notification.setCreateDate(data.getPropertySafelyAsString("CreateDate", ""));
			notification.setDocumentTitle(data.getPropertySafelyAsString("DocumentTitle", ""));
			notification.setPubOrDis(data.getPropertySafelyAsString("PubOrDis", ""));

			notificationsList.add(notification);
		}

		return notificationsList;
	}

	public ArrayList<MessageModel> parseMessageResponse(SoapObject response) {
		ArrayList<MessageModel> messagesList = new ArrayList<MessageModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<MessageModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			MessageModel messages = new MessageModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			messages.setMessageId(data.getPropertySafelyAsString("MessageId", ""));
			messages.setFrom(data.getPropertySafelyAsString("From", ""));
			messages.setTo(data.getPropertySafelyAsString("To", ""));
			messages.setDate(data.getPropertySafelyAsString("Date", ""));
			messages.setSubject(data.getPropertySafelyAsString("Subject", ""));
			messages.setIsRead(data.getPropertySafelyAsString("IsRead", ""));
			messages.setReceiverIsRead(data.getPropertySafelyAsString("ReceiverIsRead", ""));

			messagesList.add(messages);
		}

		return messagesList;
	}

	public ArrayList<SearchModel> parseGlobalSearchResponse(SoapObject response) {
		ArrayList<SearchModel> messagesList = new ArrayList<SearchModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<SearchModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			SearchModel message = new SearchModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			message.setType(data.getPropertySafelyAsString("Tpy", ""));
			message.setId(data.getPropertySafelyAsString("Id", ""));
			message.setName(data.getPropertySafelyAsString("Name", ""));
			message.setDescriptions(data.getPropertySafelyAsString("Descriptions", ""));
			message.setUserId(data.getPropertySafelyAsString("UserId", ""));
			message.setCategoryId(data.getPropertySafelyAsString("CategoryId", ""));
			message.setCategoryName(data.getPropertySafelyAsString("CategoryName", ""));
			message.setNoOfView(data.getPropertySafelyAsString("NoOfView", ""));
			message.setIsActive(data.getPropertySafelyAsString("IsActive", ""));
			message.setDisplayFlag(data.getPropertySafelyAsString("DisplayFlag", ""));
			message.setDisplayImageUrl(data.getPropertySafelyAsString("DisplayImageUrl", ""));
			message.setCreatedDate(data.getPropertySafelyAsString("CreatedDate", ""));
			message.setModifiedDate(data.getPropertySafelyAsString("ModifiedDate", ""));
			message.setIslikeStatus(data.getPropertySafelyAsString("IslikeStatus", ""));
			message.setProfilePic(Contant.PIC_URL + data.getPropertySafelyAsString("ProfilePic", ""));
			message.setPassword(data.getPropertySafelyAsString("LockPassword", ""));

			if (message.getPassword().equals("0")) {
				message.setPassword("");
			}
			messagesList.add(message);
		}

		return messagesList;
	}

	public ArrayList<CategoryModel> parseCategoryList(SoapObject response) {
		ArrayList<CategoryModel> categoryList = new ArrayList<CategoryModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<CategoryModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			CategoryModel cat = new CategoryModel();
			cat.setCategoryId(data.getPropertySafelyAsString("CategoryId", ""));
			cat.setCategoryName(data.getPropertySafelyAsString("CategoryName", ""));
			categoryList.add(cat);
		}

		return categoryList;
	}

	public ArrayList<CountryModel> parseCountryList(SoapObject response) {
		ArrayList<CountryModel> ccountryList = new ArrayList<CountryModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<CountryModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			CountryModel country = new CountryModel();
			country.setCountryId(data.getPropertySafelyAsString("CountryId", ""));
			country.setCountryName(data.getPropertySafelyAsString("CountryName", ""));
			country.setCountryCode(data.getPropertySafelyAsString("CountyCode", ""));
			ccountryList.add(country);
		}

		return ccountryList;
	}

	public ArrayList<CommentModel> parseDiscussionCommentsResponse(SoapObject response) {
		ArrayList<CommentModel> messagesList = new ArrayList<CommentModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<CommentModel>();
		}

		for (int i = newDataSetObject.getPropertyCount() - 1; i >= 0; i--) {
			CommentModel comments = new CommentModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			comments.setCommentId(data.getPropertySafelyAsString("DCommentId", ""));
			comments.setDiscussionId(data.getPropertySafelyAsString("DiscussionId", ""));
			comments.setSenderId(data.getPropertySafelyAsString("SenderId", ""));
			comments.setDescription(data.getPropertySafelyAsString("Description", ""));
			comments.setUserName(data.getPropertySafelyAsString("UserNameD", ""));
			comments.setCreatedDate(data.getPropertySafelyAsString("CreatedDate", ""));
			comments.setCommentCnt(data.getPropertySafelyAsString("CommentCnt", ""));
			comments.setUserNameComment(data.getPropertySafelyAsString("UserNameComment", ""));
			comments.setProfilePic(Contant.PIC_URL + data.getPropertySafelyAsString("ProfilePic", ""));

			messagesList.add(comments);
		}

		return messagesList;
	}

	public ArrayList<CommentModel> parsePublicationCommentsResponse(SoapObject response) {
		ArrayList<CommentModel> messagesList = new ArrayList<CommentModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<CommentModel>();
		}

		for (int i = newDataSetObject.getPropertyCount() - 1; i >= 0; i--) {
			CommentModel comments = new CommentModel();
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			comments.setCommentId(data.getPropertySafelyAsString("PCommentId", ""));
			comments.setDiscussionId(data.getPropertySafelyAsString("PublicationId", ""));
			comments.setSenderId(data.getPropertySafelyAsString("SenderId", ""));
			comments.setDescription(data.getPropertySafelyAsString("Description", ""));
			comments.setUserName(data.getPropertySafelyAsString("UserNameP", ""));
			comments.setCreatedDate(data.getPropertySafelyAsString("CreatedDate", ""));
			comments.setCommentCnt(data.getPropertySafelyAsString("CommentCnt", ""));
			comments.setUserNameComment(data.getPropertySafelyAsString("UserNameCommentPub", ""));
			comments.setProfilePic(Contant.PIC_URL + data.getPropertySafelyAsString("ProfilePic", ""));

			messagesList.add(comments);
		}

		return messagesList;
	}

	public ArrayList<PollOptionModel> parsePollList(SoapObject response) {
		ArrayList<PollOptionModel> pollList = new ArrayList<PollOptionModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<PollOptionModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {
			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			PollOptionModel poll = new PollOptionModel();
			poll.setIsVote(data.getPropertySafelyAsString("isVote", ""));
			poll.setVoteCount(data.getPropertySafelyAsString("pollcount", ""));
			poll.setVoteName(data.getPropertySafelyAsString("VoteName", ""));
			poll.setVoteTypeId(data.getPropertySafelyAsString("DVoteTypeId", ""));

			if (poll.getIsVote().equalsIgnoreCase("1")) {
				poll.setSelected(true);
			}

			pollList.add(poll);
		}

		return pollList;
	}

	public String uploadFile(String METHOD_NAME, ContentValues cv, byte[] byteArr) throws SocketException, SocketTimeoutException, XmlPullParserException, IOException {

		// String METHOD_NAME = "SaveProfilePicture";

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request = addRequestParameter(request, cv);
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		// byte[] b = baos.toByteArray();

		//
		// request.addProperty("docname", name);

		request.addProperty("docbinaryarray", byteArr);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		new MarshalBase64().register(envelope);
		envelope.setOutputSoapObject(request);

		// Says that the soap webservice is a .Net service
		envelope.dotNet = true;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 2 * 60 * 1000);

		androidHttpTransport.call(NAMESPACE + METHOD_NAME, envelope);
		String response = envelope.getResponse().toString();

		return response;

	}

	private SoapObject addRequestParameter(SoapObject request, ContentValues cv) {
		if (cv != null) {
			for (int i = 0; i < cv.size(); i++) {
				Iterator<String> keys = cv.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					request.addProperty(key, cv.get(key));
				}
			}
		}

		return request;
	}
}
