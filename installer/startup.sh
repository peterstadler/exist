#!/bin/bash
# -----------------------------------------------------------------------------
# startup.sh - Start Script for Jetty + eXist
#
# $Id: startup.sh,v 1.6 2002/12/28 17:37:22 wolfgang_m Exp $
# -----------------------------------------------------------------------------

# will be set by the installer
if [ -z "$EXIST_HOME" ]; then
	EXIST_HOME="%{INSTALL_PATH}"
fi

if [ -z "$JAVA_HOME" ]; then
    JAVA_HOME="%{JAVA_HOME}"
fi

JETTY_HOME="%{JETTY_HOME}"

JAVA_CMD="$JAVA_HOME/bin/java"

OPTIONS=

if [ ! -f "$EXIST_HOME/start.jar" ]; then
	echo "Unable to find start.jar. Please set EXIST_HOME to point to your installation directory."
	exit 1
fi

OPTIONS="-Dexist.home=$EXIST_HOME"

if [ -n "$JETTY_HOME" ]; then
	OPTIONS="-Djetty.home=$JETTY_HOME $OPTIONS"
fi

# save LANG
if [ -n "$LANG" ]; then
	OLD_LANG="$LANG"
fi
# set LANG to UTF-8
LANG=en_US.UTF-8

if [ -z "$JAVA_OPTIONS" ]; then
    export JAVA_OPTIONS="-Xms16000k -Xmx256000k -Dfile.encoding=UTF-8"
fi
JAVA_ENDORSED_DIRS="$EXIST_HOME"/lib/endorsed

$JAVA_CMD $JAVA_OPTIONS $OPTIONS \
    -Djava.endorsed.dirs=$JAVA_ENDORSED_DIRS \
    -jar "$EXIST_HOME/start.jar" jetty $*

if [ -n "$OLD_LANG" ]; then
	LANG="$OLD_LANG"
fi
