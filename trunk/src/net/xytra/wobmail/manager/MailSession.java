package net.xytra.wobmail.manager;

import javax.mail.MessagingException;

import net.xytra.wobmail.misc.MessageRow;

import com.webobjects.foundation.NSArray;

public interface MailSession {
	public static final String INBOX_FOLDER_NAME = "Inbox";
	public static final String TRASH_FOLDER_NAME = "Trash";

	public void closeSession();

	public MessageRow getMessageRowForFolderWithName(int index, String folderName) throws MessagingException;

	public NSArray<MessageRow> getMessageRowsForFolderWithName(String folderName) throws MessagingException;

	public int getNumberMessagesInFolderWithName(String folderName) throws MessagingException;

	public void moveMessageRowToFolderWithName(MessageRow messageRow, String folderName) throws MessagingException;

	public void moveMessageRowsToFolderWithName(NSArray<MessageRow> messageRows, String folderName) throws MessagingException;

}
