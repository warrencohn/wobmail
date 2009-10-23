package net.xytra.wobmail.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import er.extensions.ERXLogger;

import net.xytra.wobmail.application.Application;

@Deprecated
public class Pop3SessionManager
{
	private static Pop3SessionManager _instance = new Pop3SessionManager();

	private Map entries;
	private Timer sessionTimer;

	private Pop3SessionManager() {
		this.entries = new HashMap();
		this.sessionTimer = new Timer("Pop3SessionManager Timer");
	}

	public static Pop3SessionManager instance() {
		return (_instance);
	}

	public void registerEntry(String sessionId, String username, String password) throws MessagingException
	{
		SessionManagerEntry entry = new SessionManagerEntry(username, password);

		entries.put(sessionId, entry);
//		System.err.println(entries.keySet().size());
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
//		System.err.println(entries.keySet().size());
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

		private TimerTask closeSessionTask;
		private Folder inboxFolder;
		private Session mailSession;
		private Store store;

		public SessionManagerEntry(String username, String password)
		{
			this.username = username;
			this.password = password;
		}

		synchronized public void closeSession()
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

				this.inboxFolder = null;
			}
		}

		protected Session getSession()
		{
			if (this.mailSession == null)
				this.mailSession = Session.getInstance(new Properties());

			return (this.mailSession);
		}

		protected Store getStore() throws MessagingException
		{
			if (this.store == null)
				this.store = getSession().getStore("pop3");

			if (!this.store.isConnected())
			{
				this.store.connect(
						((Application)Application.application()).getDefaultIncomingMailServerAddress(),
						this.username,
						this.password);
			}

			return (this.store);
		}

		public MimeMessage obtainNewMimeMessage()
		{
			return (new MimeMessage(this.mailSession));
		}

		public Folder obtainOpenInboxFolder() throws MessagingException
		{
			// Deschedule closeSessionTask
			cancelCloseSessionTask();

			if (this.inboxFolder == null)
				this.inboxFolder = getStore().getFolder("INBOX");
			
			if (!this.inboxFolder.isOpen())
				this.inboxFolder.open(Folder.READ_WRITE);

			// Reschedule closeSessionTask
			scheduleCloseSessionTask();

			return (this.inboxFolder);
		}

		/* closeSessionTask-related methods */
		public void cancelCloseSessionTask()
		{
			if (this.closeSessionTask == null)
				return;

			ERXLogger.log.debug("cancelCloseSessionTask() at " + System.currentTimeMillis());
			this.closeSessionTask.cancel();
			this.closeSessionTask = null;
		}

		public void scheduleCloseSessionTask()
		{
			if (this.closeSessionTask != null)
				return;

			ERXLogger.log.debug("scheduleCloseSessionTask() at " + System.currentTimeMillis());
			this.closeSessionTask = new CloseStoreTimerTask(this); 
			sessionTimer.schedule(this.closeSessionTask, 30000l);			
		}
	}

	private class CloseStoreTimerTask extends TimerTask
	{
		private SessionManagerEntry session;

		public CloseStoreTimerTask(SessionManagerEntry session)
		{
			this.session = session;
		}

		@Override
		public void run()
		{
			ERXLogger.log.debug("Closing the session!");
			this.session.closeSession();
		}
	}

}
