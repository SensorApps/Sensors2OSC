**Due to Github's determination to be the "world’s leading AI-powered developer platform" this project has been moved over to [Codeberg](https://codeberg.org/Residuum/Sensors2OSC)**
# Sensors2OSC

Android app for sending sensor data and multitouch via [Open Sound Control (OSC)](http://opensoundcontrol.org/) over network to a recipient. 

Typical use case is controlling a music application from your phone or tablet.

<a href="https://sensors2.org/osc" target="_blank">Homepage</a>

## Download

<a href="https://f-droid.org/repository/browse/?fdid=org.sensors2.osc" target="_blank">
<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80"/></a>

To build the app yourself, install the [Android SDK](https://developer.android.com/studio/index.html) and [Gradle](https://gradle.org/). 

After installation run `gradle.bat` on Windows or `./gradlew` on Mac OS X and Linux.

## Contributing

If you want to contribute to this project, send a pull request.

## Translation

Translation is currently coordinated via weblate.

<a href="https://hosted.weblate.org/engage/sensors2osc/">
<img src="https://hosted.weblate.org/widgets/sensors2osc/-/app/287x66-grey.png" alt="Translation state" />
</a>

If you want to add a new language or update translations, please see the [Common](https://github.com/SensorApps/Common) library for the sensor apps. It contains additional strings.


## Example usage
There are two examples included for receiving data from Sensors2OSC, that reside in `examples`, Puredata and Python3. Both listen to port 9000.
