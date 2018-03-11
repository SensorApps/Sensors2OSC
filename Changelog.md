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
- Added laucher icons for all sizes.
- Added information to Readme.

# 0.1.0 (2017-02-26)
- First release.
