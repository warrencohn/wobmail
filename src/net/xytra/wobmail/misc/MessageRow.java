package net.xytra.wobmail.misc;

import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Flags.Flag;

import net.xytra.wobmail.util.XWMUtils;

import com.webobjects.foundation.NSKeyValueCoding;

public class MessageRow implements NSKeyValueCoding
{
	public static final String DATE_SENT_SORT_FIELD = "dateSent";
	public static final String SENDER_SORT_FIELD = "sender"; 
	public static final String SUBJECT_SORT_FIELD = "subject";

	private Message message;

	private Date dateSent;
	private Boolean isDeleted;
	private Number messageNumber;
	private String sender;
	private String subject;

	private boolean isSelected = false;

	public MessageRow(Message message) {
		this.message = message;
	}

	public Date getDateSent() throws MessagingException {
		if (dateSent == null) {
			dateSent = message.getSentDate();
		}

		return (dateSent);
	}

	public Message getMessage() {
		return (message);
	}

	public int getMessageNumber() {
		if (messageNumber == null) {
			messageNumber = Integer.valueOf(message.getMessageNumber());
		}

		return (messageNumber.intValue());
	}

	public String getSender() throws MessagingException {
		if (sender == null) {
			sender = XWMUtils.fromAddressesAsStringForMessage(message);
		}

		return (sender);
	}

	public String getSubject() throws MessagingException {
		if (subject == null) {
			subject = message.getSubject();
		}

		return (subject);
	}

	public boolean isDeleted() throws MessagingException {
		if (isDeleted == null) {
			isDeleted = Boolean.valueOf(message.isSet(Flag.DELETED));
		}

		return (isDeleted.booleanValue());
	}

	public void setIsDeleted(boolean value) throws MessagingException {
		message.setFlag(Flag.DELETED, value);

		// Update the cached version
		isDeleted = Boolean.valueOf(value);
	}

	public boolean isSelected() {
		return (isSelected);
	}

	public void setIsSelected(boolean value) {
		isSelected = value;
	}

	// Static utility
	/**
	 * @param key The key to test.
	 * @return true if <code>key</code> is one of the valid sorting keys
	 */
	public static boolean isSortKeyValid(String key) {
		return ((key != null) &&
				(DATE_SENT_SORT_FIELD.equals(key) ||
				 SENDER_SORT_FIELD.equals(key) ||
				 SUBJECT_SORT_FIELD.equals(key)));
	}

	// NSKeyValueCoding stuff
	public void takeValueForKey(Object value, String key) {
		NSKeyValueCoding.DefaultImplementation.takeValueForKey(this, value, key);
	}

	public Object valueForKey(String key) {
		return (NSKeyValueCoding.DefaultImplementation.valueForKey(this, key));
	}

}
