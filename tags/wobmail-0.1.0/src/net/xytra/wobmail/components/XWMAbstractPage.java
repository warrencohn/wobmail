package net.xytra.wobmail.components;

import com.webobjects.appserver.WOContext;

import er.extensions.ERXNonSynchronizingComponent;

public class XWMAbstractPage extends ERXNonSynchronizingComponent
{
	public XWMAbstractPage(WOContext context) {
		super(context);
	}

	public String pageWrapperName() {
		return (XWMPageWrapper.class.getName());
	}

}
