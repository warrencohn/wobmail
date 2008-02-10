package net.xytra.wobmail.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import net.xytra.wobmail.application.Application;

import com.webobjects.foundation.NSTimestamp;

public class Pop3SessionManager
{
	private static Pop3SessionManager _instance = new Pop3SessionManager();

	private Map entries;

	private Pop3SessionManager() {
		this.entries = new HashMap();
	}

	public static Pop3SessionManager instance() {
		return (_instance);
	}

	public void registerEntry(String sessionId, String username, String password) throws MessagingException
	{
		SessionManagerEntry entry = new SessionManagerEntry(username, password);
		entry.connectToStore();

		entries.put(sessionId, entry);
		System.err.println(entries.keySet().size());
	}

	public void deregisterEntry(String sessionId)
	{
		if (sessionId == null)
			return;

		SessionManagerEntry entry = (SessionManagerEntry)entries.get(sessionId);
		if (entry == null)
			return;

		entry.closeSession();
		entries.remove(sessionId);
		System.err.println(entries.keySet().size());
	}

	public MimeMessage obtainNewMimeMessageFor(String sessionId)
	{
		if (sessionId == null)
			return (null);

		SessionManagerEntry entry = (SessionManagerEntry)entries.get(sessionId);
		if (entry == null)
			return (null);

		return (entry.obtainNewMimeMessage());
	}

	public Folder obtainOpenInboxFor(String sessionId) throws MessagingException
	{
		if (sessionId == null)
			return (null);

		SessionManagerEntry entry = (SessionManagerEntry)entries.get(sessionId);
		if (entry == null)
			return (null);

		return (entry.obtainOpenInboxFolder());
	}

	private class SessionManagerEntry
	{
		private String username;
		private String password;
		private NSTimestamp lastUsed = null;
		private Folder inboxFolder;
		private Session mailSession;
		private Store store;

		public SessionManagerEntry(String username, String password)
		{
			this.username = username;
			this.password = password;
		}

		public void connectToStore() throws MessagingException
		{
			this.mailSession = Session.getInstance(new Properties());
			this.store = this.mailSession.getStore("pop3");
			this.store.connect(
					((Application)Application.application()).getDefaultIncomingMailServerAddress(),
					this.username,
					this.password);
		}

		public MimeMessage obtainNewMimeMessage()
		{
			return (new MimeMessage(this.mailSession));
		}

		public Folder obtainOpenInboxFolder() throws MessagingException
		{
			if (this.inboxFolder == null)
			{
				Folder folder = this.store.getFolder("INBOX");
				folder.open(Folder.READ_WRITE);

				this.inboxFolder = folder;
			}

			this.lastUsed = new NSTimestamp();

			return (this.inboxFolder);
		}

		public void closeSession()
		{
			if (this.inboxFolder != null)
			{
				try {
					if (this.inboxFolder.isOpen())
						this.inboxFolder.close(true);
				}
				catch (MessagingException e) {
					e.printStackTrace();
				}

				try {
					this.inboxFolder.getStore().close();
				}
				catch (MessagingException e) {}
			}
		}

		public NSTimestamp lastUsed()
		{
			return (this.lastUsed);
		}
	}

}
