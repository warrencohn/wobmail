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

	// Folder contents
	private NSMutableArray<MessageRow> allMessageRows = null;
	private NSMutableArray<MessageRow> inboxMessageRows = null;
	private NSMutableArray<MessageRow> trashMessageRows = null;

	public MessageRow getMessageRowForFolderWithName(int index, String folderName) throws MessagingException {
		return (getMessageRowsForFolderWithName(folderName).objectAtIndex(index));
	}

	public NSArray<MessageRow> getMessageRowsForFolderWithName(String folderName) throws MessagingException {
		if (MailSession.INBOX_FOLDER_NAME.equals(folderName)) {
			return (getInboxMessageRows());
		} else if (MailSession.TRASH_FOLDER_NAME.equals(folderName)) {
			return (getTrashMessageRows());
		} else {
			throw (new MailSessionException("Cannot get MessageRow objects for specified folderName as such a folder does not exist"));
		}
	}

	public int getNumberMessagesInFolderWithName(String folderName) throws MessagingException {
		return (getMessageRowsForFolderWithName(folderName).size());
	}

	protected NSArray<MessageRow> getInboxMessageRows() throws MessagingException {
		if (inboxMessageRows == null) {
			Enumeration<MessageRow> en1 = getAllMessageRows().objectEnumerator();
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

	protected NSArray<MessageRow> getTrashMessageRows() throws MessagingException {
		if (trashMessageRows == null) {
			Enumeration<MessageRow> en1 = getAllMessageRows().objectEnumerator();
			NSMutableArray<MessageRow> rows = new NSMutableArray<MessageRow>();

			while (en1.hasMoreElements()) {
				MessageRow mr = en1.nextElement();
				if (mr.isDeleted()) {
					rows.addObject(mr);
				}
			}

			trashMessageRows = rows;
		}

 		return (trashMessageRows);
	}

	protected NSArray<MessageRow> getAllMessageRows() throws MessagingException {
		if (allMessageRows == null) {
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
		trashMessageRows = null;
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
