#!/bin/sh

PROJNAME=wobmail
JARNAME=$PROJNAME

rm -rf $PROJNAME $PROJNAME.war &&
mkdir $PROJNAME &&
cd $PROJNAME &&
jar xvf ../../dist/$PROJNAME.war &&
cd $PROJNAME.woa/Contents/Resources &&
mv ../Info.plist ./ &&
cd .. &&
jar xvf Resources/Java/$JARNAME.jar &&
rm -f Resources/Java/$JARNAME.jar &&
mv Resources/Java/*.jar ../../WEB-INF/lib/ &&
rm -rf MacOS META-INF Resources/Java UNIXW ebServerResources Windows web.xml &&
jar cf A$PROJNAME.jar * &&
rm -f ../../WEB-INF/lib/$PROJNAME.jar &&
mv A$PROJNAME.jar ../../WEB-INF/lib/ &&
cd ../.. &&
rm -rf $PROJNAME.woa &&
cd WEB-INF/lib &&
ln ~/Roots/ERExtensions-4.0.jar &&
ln /Library/WebObjects/lib/JavaEOAccess.jar &&
ln /Library/WebObjects/lib/JavaEOControl-fixed.jar &&
ln /Library/WebObjects/lib/JavaEOProject.jar &&
ln /Library/WebObjects/lib/JavaFoundation-fixed.jar &&
ln /Library/WebObjects/lib/JavaJDBCAdaptor.jar &&
ln ~/Roots/JavaWOExtensions-4.0.jar &&
ln /Library/WebObjects/lib/JavaWOJSPServlet.jar &&
ln /Library/WebObjects/lib/JavaWebObjects-fixed.jar &&
ln /Library/WebObjects/lib/JavaXML.jar &&
ln /Library/WebObjects/Extensions/log4j-1.2.8.jar &&
cd ../.. &&
rm -f WEB-INF/web.xml &&
ln ../web.xml WEB-INF/web.xml &&
mv tlds WEB-INF/ &&
mkdir -p WEB-INF/classes &&
jar cf $PROJNAME.war WEB-INF &&
mv $PROJNAME.war ../ &&
cd .. &&
rm -rf $PROJNAME
