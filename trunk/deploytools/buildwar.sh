#!/bin/sh

PROJNAME=wobmail
JARNAME=$PROJNAME

rm -rf $PROJNAME $PROJNAME.war &&
mkdir $PROJNAME &&
cd $PROJNAME &&
jar xvf ../../dist/$PROJNAME.war &&
cd WEB-INF/$PROJNAME.woa/Contents &&
jar xvf Resources/Java/$JARNAME.jar &&
rm -f Resources/Java/$JARNAME.jar &&
rm -rf MacOS META-INF Resources/Java UNIX Windows web.xml &&
mv Info.plist Resources/ &&
jar cf $PROJNAME.jar * &&
rm -f ../../lib/$PROJNAME.jar &&
mv $PROJNAME.jar ../../lib/ &&
cd ../.. && 
rm -rf $PROJNAME.woa &&
cd lib &&
ln ~/Roots/ERExtensions-5.0.jar &&
ln ~/Roots/ERJars-5.0.jar &&
ln /Library/WebObjects/lib/JavaEOAccess.jar &&
ln /Library/WebObjects/lib/JavaEOControl-fixed.jar &&
ln /Library/WebObjects/lib/JavaEOProject.jar &&
ln /Library/WebObjects/lib/JavaFoundation-fixed.jar &&
ln /Library/WebObjects/lib/JavaJDBCAdaptor.jar &&
ln ~/Roots/JavaWOExtensions-5.0.jar &&
ln /Library/WebObjects/lib/JavaWOJSPServlet.jar &&
ln /Library/WebObjects/lib/JavaWebObjects-fixed.jar &&
ln /Library/WebObjects/lib/JavaXML.jar &&
cd ../.. &&
rm -f WEB-INF/web.xml &&
ln ../web.xml WEB-INF/web.xml &&
jar cf $PROJNAME.war WEB-INF &&
mv $PROJNAME.war ../ &&
cd .. &&
rm -rf $PROJNAME
