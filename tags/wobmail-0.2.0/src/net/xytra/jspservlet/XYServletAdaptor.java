package net.xytra.jspservlet;

import javax.servlet.ServletException;

import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSLog;
import com.webobjects.jspservlet.WOServletAdaptor;

import er.extensions.ERXApplication;

public class XYServletAdaptor extends WOServletAdaptor
{
	public XYServletAdaptor() throws ServletException {
		super();
	}

	public void init() throws ServletException {
		try {
			ERXApplication.setup(new String[0]);
		} catch (NSForwardException e) {
			NSLog.err.appendln(e.stackTrace());
		}
		super.init();
	}
}
