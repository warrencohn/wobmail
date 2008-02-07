package net.xytra.wobmail.misc;

import java.util.Date;

import com.webobjects.foundation.NSKeyValueCoding;

public class MessageRow implements NSKeyValueCoding
{
	private int messageNumber;
	private Date dateSent;
	private String sender;
	private String subject;

	public MessageRow(int messageNumber, Date dateSent, String sender, String subject)
	{
		this.messageNumber = messageNumber;
		this.dateSent = dateSent;
		this.sender = sender;
		this.subject = subject;
	}

	public int getMessageNumber() { return (this.messageNumber); }
	public Date getDateSent() { return (this.dateSent); }
	public String getSender() { return (this.sender); }
	public String getSubject() { return (this.subject); }

	public void takeValueForKey(Object value, String key) {
		NSKeyValueCoding.DefaultImplementation.takeValueForKey(this, value, key);
	}

	public Object valueForKey(String key) {
		return (NSKeyValueCoding.DefaultImplementation.valueForKey(this, key));
	}

}
