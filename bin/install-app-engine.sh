#/bin/sh

APP_ENGINE_VERSION="1.3.0"
APP_ENGINE_SDK_URL="http://googleappengine.googlecode.com/files/appengine-java-sdk-${APP_ENGINE_VERSION}.zip"

APP_ENGINE_SDK_FILE="/tmp/`basename $APP_ENGINE_SDK_URL`"
APP_ENGINE_SDK_DIR="`dirname $APP_ENGINE_SDK_FILE`/`basename $APP_ENGINE_SDK_FILE .zip`"

if [ ! -f $APP_ENGINE_SDK_FILE ]; then
    echo "Downloading $APP_ENGINE_SDK_URL ..."
    wget --output-document $APP_ENGINE_SDK_FILE $APP_ENGINE_SDK_URL
fi

if [ ! -d $APP_ENGINE_SDK_DIR ]; then
    unzip -d `dirname $APP_ENGINE_SDK_FILE` $APP_ENGINE_SDK_FILE
fi

mvn install:install-file -Dfile="$APP_ENGINE_SDK_DIR/lib/impl/appengine-local-runtime.jar" -DgroupId=com.google \
 -DartifactId=appengine-local-runtime -Dversion=$APP_ENGINE_VERSION -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile="$APP_ENGINE_SDK_DIR/lib/impl/appengine-api-stubs.jar" -DgroupId=com.google \
 -DartifactId=appengine-api-stubs -Dversion=$APP_ENGINE_VERSION -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile="$APP_ENGINE_SDK_DIR/lib/user/appengine-api-1.0-sdk-1.3.0.jar" -DgroupId=com.google \
 -DartifactId=appengine-api-1.0-sdk -Dversion=$APP_ENGINE_VERSION -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile="$APP_ENGINE_SDK_DIR/lib/user/appengine-api-labs-1.3.0.jar" -DgroupId=com.google \
 -DartifactId=appengine-api-labs -Dversion=$APP_ENGINE_VERSION -Dpackaging=jar -DgeneratePom=true


