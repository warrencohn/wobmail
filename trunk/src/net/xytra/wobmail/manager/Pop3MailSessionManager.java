package net.xytra.wobmail.manager;

import java.util.HashMap;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class Pop3MailSessionManager implements MailSessionManager
{
	private static Pop3MailSessionManager _instance = new Pop3MailSessionManager();

	private Map entries;

	private Pop3MailSessionManager() {
		this.entries = new HashMap();
	}

	public static Pop3MailSessionManager instance() {
		return (_instance);
	}

	// Registration
	public void deregisterEntry(String sessionId)
	{
		if (sessionId == null)
			return;

		MailSession entry = (MailSession)entries.get(sessionId);
		if (entry == null)
			return;

		entry.closeSession();
		entries.remove(sessionId);
//		System.err.println(entries.keySet().size());
	}

	public void registerMailSession(String sessionId, String username, String password) throws MessagingException
	{
		Pop3MailSession entry = new Pop3MailSession(username, password);

		entries.put(sessionId, entry);
	}

	// Folders
	public Folder obtainOpenInboxFor(String sessionId) throws MessagingException
	{
		if (sessionId == null)
			return (null);

		Pop3MailSession entry = (Pop3MailSession)entries.get(sessionId);
		if (entry == null)
			return (null);

		return (entry.obtainOpenInboxFolder());
	}

	// Messages
	public MimeMessage obtainNewMimeMessageFor(String sessionId)
	{
		if (sessionId == null)
			return (null);

		Pop3MailSession entry = (Pop3MailSession)entries.get(sessionId);
		if (entry == null)
			return (null);

		return (entry.obtainNewMimeMessage());
	}

}
