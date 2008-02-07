package net.xytra.wobmail.export;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Part;

import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSTimestamp;

public class PartExportVisitor implements ExportVisitor
{
	private Part part;

	private NSData fileContent;
	private String fileName;
	private String fileType;

	public PartExportVisitor(Part part)
	{
		this.part = part;
	}

	public NSData getFileContent()
	{
		if (this.fileContent == null)
			this.fileContent = getFileContentInternal();

		return (this.fileContent);
	}

	protected NSData getFileContentInternal()
	{
		NSData content;
		try
		{
			int size = this.part.getSize();
			if (size < 1)
				size = 10240;

			content = new NSData(this.part.getInputStream(), size);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			content = NSData.EmptyData;
		}
		catch (MessagingException e)
		{
			e.printStackTrace();
			content = NSData.EmptyData;
		}

		return (content);
	}

	public String getFileName()
	{
		if (this.fileName == null)
			this.fileName = getFileNameInternal();

		return (this.fileName);
	}

	protected String getFileNameInternal()
	{
		String name;
		try
		{
			name = part().getFileName();
		}
		catch (MessagingException e)
		{
			e.printStackTrace();
			name = "(Undefined)";
		}

		if ((name == null) || (name.trim().length() == 0))
			name = "Part" + new NSTimestamp().getTime() + '.' + defaultPartFileExtension();

		return (name);
	}

	public String getFileType()
	{
		if (this.fileType == null)
		{
			try
			{
				this.fileType = this.part.getContentType().split(";")[0];
			}
			catch (MessagingException e)
			{
				e.printStackTrace();
				this.fileType = "text/plain";
			}
		}

		return (this.fileType);
	}

	protected String defaultPartFileExtension()
	{
		String s = getFileType();

		if ("text/plain".equals(s))
			return ("txt");

		return (s.split("/")[1]);
	}

	protected Part part()
	{
		return (this.part);
	}

}
