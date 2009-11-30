package net.xytra.wobmail.manager;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import net.xytra.wobmail.misc.MessageRow;

import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.foundation.ERXArrayUtilities;


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
	protected NSArray<Folder> getOpenFolders() {
		if (inboxFolder == null) {
			return (NSArray.EmptyArray);
		} else {
			return (new NSArray<Folder>(inboxFolder));
		}
	}

	// Folder contents
	public NSArray<MessageRow> getMessageRowsForFolder(String folderName) throws MessagingException {
		return (getMessageRowsForFolder(folderName, false));
	}

	private NSArray<MessageRow> cachedSortedInboxMessageRows = null;

	public NSArray<MessageRow> getMessageRowsForFolder(String folderName, boolean forceReload) throws MessagingException {
		if (!INBOX_FOLDER_NAME.equals(folderName)) {
			throw (new MailSessionException("Cannot get MessageRow objects for specified folderName as such a folder does not exist"));
		}

		if (forceReload || (cachedSortedInboxMessageRows == null)) {
			NSArray unsortedMessageRows = getFreshUnsortedMessageRowsForInbox();
			cachedSortedInboxMessageRows = getMessageRowsSortedForFolder(unsortedMessageRows, INBOX_FOLDER_NAME);
		}

		return (cachedSortedInboxMessageRows);
	}

	protected NSArray<MessageRow> getMessageRowsSortedForFolder(NSArray unsortedMessageRows, String folderName) {
		return (ERXArrayUtilities.sortedArraySortedWithKey(
				unsortedMessageRows,
				sortKeyForFolder(folderName),
				isReverseSortForFolder(folderName) ?
						EOSortOrdering.CompareCaseInsensitiveDescending :
						EOSortOrdering.CompareCaseInsensitiveAscending));
	}

	protected NSArray<MessageRow> getFreshUnsortedMessageRowsForInbox() throws MessagingException {
		NSArray unsortedMessageRows;

		// Only allow one such access at a time through this session
		synchronized (this) {
			// Get all messages from INBOX 
			Message[] messages = obtainOpenInboxFolder().getMessages();
			NSMutableArray<MessageRow> messageRowsArray = new NSMutableArray<MessageRow>();

			// Let's get each message in a wrapper and keep it all for future use:
			for (int i=0; i<messages.length; i++) {
				messageRowsArray.addObject(new MessageRow(messages[i]));
			}
			
			unsortedMessageRows = messageRowsArray.immutableClone();
		}

		return (unsortedMessageRows);
	}

	protected NSArray<MessageRow> getCachedMessageRowArrayForFolder(String folderName) {
		if (MailSession.INBOX_FOLDER_NAME.equals(folderName)) {
			return (cachedSortedInboxMessageRows);
		} else {
			throw (new MailSessionException("Cannot set MessageRow objects for specified folderName as such a folder does not exist"));
		}
	}

	protected void setCachedMessageRowArrayForFolder(NSArray<MessageRow> messageRows, String folderName) {
		if (MailSession.INBOX_FOLDER_NAME.equals(folderName)) {
			cachedSortedInboxMessageRows = messageRows;
		} else {
			throw (new MailSessionException("Cannot set MessageRow objects for specified folderName as such a folder does not exist"));
		}
	}

	/**
	 * Sort the specified folder's message list and return the newly sorted list.
	 * Sort the specified folder's message list using specified sorting key; do
	 * a reverse sort if reverseSort is true.
	 *
	 * @param folderName Name of the folder whose messages will be sorted.
	 * @param sortKey Key representing which message property by which to sort.
	 * @param reverseSort Whether to reverse sort.
	 */
	public void sortMessageRowsForFolderSortedWithKey(String folderName, String sortKey, boolean reverseSort) throws MessagingException {
		String currentSortKey = sortKeyForFolder(folderName);
		boolean currentReverseSort = isReverseSortForFolder(folderName);

		// Set the new parameters:
		setSortKeyForFolder(sortKey, folderName);
		setReverseSortForFolder(reverseSort, folderName);

		if (!currentSortKey.equals(sortKey)) {
			// Sort key has changed, so re-sort according to new parameters:
			setCachedMessageRowArrayForFolder(
					getMessageRowsSortedForFolder(
							getCachedMessageRowArrayForFolder(folderName),
							folderName),
					folderName);
		} else if (reverseSort != currentReverseSort) {
			// Sort key hasn't changed.  Only reverse if that has changed:
			setCachedMessageRowArrayForFolder(
					ERXArrayUtilities.reverse(getCachedMessageRowArrayForFolder(folderName)),
					folderName);
		}
	}

	private Map folderNameToSortKeyMap = Collections.synchronizedMap(new HashMap<String, String>());
	private Map folderNameToReverseSortMap = Collections.synchronizedMap(new HashMap<String, Boolean>());

	public String sortKeyForFolder(String folderName) {
		String sortKey = (String)folderNameToSortKeyMap.get(folderName);

		if (sortKey == null) {
			// Set sort column to Date Sent as default:
			sortKey = MessageRow.DATE_SENT_SORT_FIELD;

			folderNameToSortKeyMap.put(folderName, sortKey);
		}

		return (sortKey);
	}

	protected void setSortKeyForFolder(String sortKey, String folderName) {
		folderNameToSortKeyMap.put(folderName, sortKey);
	}

	public boolean isReverseSortForFolder(String folderName) {
		Boolean isReverseSort = (Boolean)folderNameToReverseSortMap.get(folderName);

		if (isReverseSort == null) {
			// Set reverse to false as default:
			isReverseSort = Boolean.FALSE;

			folderNameToReverseSortMap.put(folderName, isReverseSort);
		}

		return (isReverseSort.booleanValue());
	}

	protected void setReverseSortForFolder(boolean reverse, String folderName) {
		folderNameToReverseSortMap.put(folderName, Boolean.valueOf(reverse));
	}

	// Messages
	public void moveMessageRowsToFolder(NSArray<MessageRow> messageRows, String folderName) throws MessagingException {
		throw (new MailSessionException("Unimplemented"));
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
