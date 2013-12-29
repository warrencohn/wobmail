package net.xytra.wobmail.manager;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import net.xytra.wobmail.application.Session;

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
	}

	public void registerMailSession(Session session, String username, String password) throws MessagingException
	{
		Pop3MailSession entry = new Pop3MailSession(username, password);

		// Try a login before we do anything else
		entry.keepConnectionOpen(true);
		
		// Logging in to server worked, remember mail session
		entries.put(session.sessionID(), entry);

		// Setup the mail session in the WO Session for easy access
		session.setMailSession(entry);
	}

}