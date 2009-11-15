package net.xytra.wobmail.export;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import net.xytra.wobmail.util.XWMUtils;

import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSTimestamp;


public class MimeMessageExportVisitor extends PartExportVisitor
{
	public MimeMessageExportVisitor(MimeMessage part) {
		super(part);
	}

	protected MimeMessage mimeMessage()
	{
		return ((MimeMessage)part());
	}

	protected NSData getFileContentInternal()
	{
		NSData content = null;
		try
		{
			content = XWMUtils.fullMimeMessageSource(mimeMessage());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (MessagingException e)
		{
			e.printStackTrace();
		}

		if (content == null)
			return (NSData.EmptyData);

		return (content);
	}

	protected String getFileNameInternal()
	{
		String name;
		try
		{
			name = mimeMessage().getFileName();
		}
		catch (MessagingException e)
		{
			e.printStackTrace();
			name = "(Undefined)";
		}

		if ((name == null) || (name.trim().length() == 0))
		{
			String subject = null;
			try
			{
				subject = mimeMessage().getSubject();
			}
			catch (MessagingException e)
			{
				e.printStackTrace();
			}

			if ((subject != null) && (subject.trim().length() > 0))
				name = subject.trim().substring(0, Math.min(16, subject.length())).replaceAll("\\W", "_") + ".eml";
			else
				name = "Part" + new NSTimestamp().getTime() + ".eml";
		}

		return (name);
	}

}
