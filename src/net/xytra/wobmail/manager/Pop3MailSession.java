package net.xytra.wobmail.manager;

import java.util.Enumeration;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import net.xytra.wobmail.misc.MessageRow;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;


public class Pop3MailSession extends AbstractMailSession
{
	private Folder inboxFolder = null;

	public Pop3MailSession(String username, String password) {
		super(username, password);
	}

	// Folders
	synchronized protected Folder obtainOpenInboxFolder() throws MessagingException
	{
		if (inboxFolder == null) {
			inboxFolder = getOpenStore().getFolder("INBOX");
		}

		inboxFolder = obtainOpenFolder(inboxFolder);

		return (inboxFolder);
	}

	@Override
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

	// Folder contents
	private NSMutableArray<MessageRow> allMessageRows = null;
	private NSMutableArray<MessageRow> inboxMessageRows = null;

	public NSArray<MessageRow> getMessageRowsForFolderWithName(String folderName, boolean forceReload) throws MessagingException {
		if (MailSession.INBOX_FOLDER_NAME.equals(folderName)) {
			return (getInboxMessageRows(forceReload));
		} else {
			throw (new MailSessionException("Cannot get MessageRow objects for specified folderName as such a folder does not exist"));
		}
	}

	protected NSArray<MessageRow> getInboxMessageRows(boolean forceReload) throws MessagingException {
		System.err.println("getInboxMessageRows with forceReload="+forceReload);
		if (forceReload || (inboxMessageRows == null)) {
			Enumeration<MessageRow> en1 = getAllMessageRows(forceReload).objectEnumerator();
			NSMutableArray<MessageRow> rows = new NSMutableArray<MessageRow>();

			while (en1.hasMoreElements()) {
				MessageRow mr = en1.nextElement();
				if (!mr.isDeleted()) {
					rows.addObject(mr);
				}
			}

			inboxMessageRows = rows;
		}

 		return (inboxMessageRows);
	}

	protected NSArray<MessageRow> getAllMessageRows(boolean forceReload) throws MessagingException {
		if (forceReload || (allMessageRows == null)) {
			Message[] messages = obtainOpenInboxFolder().getMessages();
			NSMutableArray<MessageRow> messageRowsArray = new NSMutableArray<MessageRow>();

			// Let's get each message in a wrapper and keep it all for future use:
			for (int i=0; i<messages.length; i++) {
				messageRowsArray.addObject(new MessageRow(messages[i]));
			}
			
			allMessageRows = messageRowsArray;
		}

		return (allMessageRows);
	}

	// Messages
	public void moveMessageRowsToFolderWithName(NSArray<MessageRow> messageRows, String folderName) throws MessagingException {
		if (MailSession.INBOX_FOLDER_NAME.equals(folderName)) {
			moveMessageRowsToInbox(messageRows);
		} else if (MailSession.TRASH_FOLDER_NAME.equals(folderName)) {
			moveMessageRowsToTrash(messageRows);
		} else {
			throw (new MailSessionException("Cannot move to folder with specified folderName as folder does not exist."));
		}

		// Reset these two cached lists as they are no longer valid after a move
		inboxMessageRows = null;
	}

	protected void moveMessageRowsToInbox(NSArray<MessageRow> messageRows) throws MessagingException {
		setDeletedFlagOnMessageRows(messageRows, false);
	}

	protected void moveMessageRowsToTrash(NSArray<MessageRow> messageRows) throws MessagingException {
		setDeletedFlagOnMessageRows(messageRows, true);
	}

	protected void setDeletedFlagOnMessageRows(NSArray<MessageRow> messageRows, boolean isDeleted) throws MessagingException {
		Enumeration<MessageRow> en1 = messageRows.objectEnumerator();

		while (en1.hasMoreElements()) {
			en1.nextElement().setIsDeleted(isDeleted);
		}
	}

}
