package net.xytra.wobmail.manager;

import javax.mail.Folder;
import javax.mail.MessagingException;

import com.webobjects.foundation.NSArray;


public class Pop3MailSession extends AbstractMailSession
{
	private Folder inboxFolder = null;

	public Pop3MailSession(String username, String password) {
		super(username, password);
	}

	// Folders
	public Folder obtainOpenInboxFolder() throws MessagingException
	{
		// Deschedule closeSessionTask
		cancelCloseSessionTask();

		if (inboxFolder == null)
			inboxFolder = getStore().getFolder("INBOX");
		
		if (!inboxFolder.isOpen())
			inboxFolder.open(Folder.READ_WRITE);

		// Reschedule closeSessionTask
		scheduleCloseSessionTask();

		return (inboxFolder);
	}

	protected String getMailProtocolName() {
		return ("pop3");
	}

	@Override
	protected void forgetOpenFolders() {
		inboxFolder = null;
	}

	@Override
	protected NSArray getOpenFolders() {
		if (inboxFolder == null) {
			return (NSArray.EmptyArray);
		} else {
			return (new NSArray(inboxFolder));
		}
	}

}
