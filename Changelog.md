# 0.7.0 (2022-10-16)
- Implementation for smart watches
- Added grid to multitouch
- Upgrade build for Android 14

# 0.6.0 (2022-10-16)
- Add geolocation to sensor list
- Reordering of sensors in GUI
- Update to translations

# 0.5.2 (2022-10-04)
- Small changes to colors
- Update preview images

# 0.5.1 (2022-09-14)
- Use dark mode for older devices.
- Bugfixes:
	* Use theme colors for multitouch view.
	* Prevent crash on switching between day and night while background.
	* Android 12: Prevent crash on setting to highest sensor rate

# 0.5.0 (2022-09-08)
- Switch to Material design and day/night theme
- Bugfix: sensor names can take up more space.

# 0.4.1 (2022-06-25)
- Option to block screen for blacking
- Bugfixes:
	* Android 12: Sending data does not crash anymore.
	* Android 12: Editing host and port does not crash anymore.

# 0.4.0 (2022-06-12)
- **breaking**: NFC tag values are removed.
- **breaking**: Minimum required Android version is now 4.0.1.
- Data is sent in background, screen can be turned off.
- App rotation is disabled when sending data.
- Autocomplete from history in settings.
- Enable sending OSC as bundles in settings.
- Added translations:
	* French
	* Dutch
	* Norwegian Bokmal
- Bugfixes:
	* Altering settings does not reset sending state.

# 0.3.0 (2018-03-11)
- Change of format: Instead of different messages for each dimension
  only one message with a list of float values is sent.
- Change of format: NFC tag values are sent as strings instead of
  floats.
- Views are rotatable.
- Bugfixes:
	* Orientation is sent, if device does not have a dedicated sensor,
	  but magnetic field and accelerometer are available.
	* Sensors are only listed once, even if device reports multiple
	  versions of it.

# 0.2.0 (2017-02-28)
- Added launcher icons for all sizes.
- Added information to Readme.

# 0.1.0 (2017-02-26)
- First release.
