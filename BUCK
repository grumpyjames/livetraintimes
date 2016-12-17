android_resource(
  name = 'res',
  res = 'res',
  package = 'net.digihippo.ltt',
  visibility = [ 'PUBLIC' ],
)

prebuilt_jar(
  name = 'guava',
  binary_jar = 'libs/guava.jar',
  visibility = [ 'PUBLIC' ],
)

prebuilt_jar(
  name = 'jodatime',
  binary_jar = 'libs/joda-time-2.7.jar',
  visibility = [ 'PUBLIC' ],
)


prebuilt_jar(
  name = 'android-support',
  binary_jar = 'libs/android-support-v13.jar',
  visibility = [ 'PUBLIC' ],
)

prebuilt_jar(
  name = 'jsr-305',
  binary_jar = 'libs/jsr305.jar',
  visibility = [ 'PUBLIC' ],
)

prebuilt_jar(
  name = 'ksoap',
  binary_jar = 'libs/ksoap2-android-assembly-3.6.0-jar-with-dependencies.jar',
  visibility = [ 'PUBLIC' ],
)

keystore(
  name = 'keystore',
  store = 'livetraintimesii.jks',
  properties = '../../keystore.properties'
)

# Building this rule will produce a file named messenger.apk.
android_binary(
  name = 'livetraintimes',
  manifest = 'AndroidManifest.xml',
  keystore = ':keystore',
  package_type = 'debug',
  deps = [
    ':res',
    '//src/main/java/net/digihippo/ltt/android:livetraintimes',
  ],
)