package org.sensors2.osc.sensors;

import android.content.Context;
import android.content.res.Resources;

import org.sensors2.osc.R;

/**
 * Created by thomas on 05.11.14.
 */
public class Parameters implements org.sensors2.common.sensors.Parameters {
	private final int sensorType;
	private final int dimensions;
	private final String oscPrefix;
	private final String name;

	@Override
	public int getSensorType() {
		return this.sensorType;
	}

	@Override
	public int getDimensions() {
		return this.dimensions;
	}

	public Parameters(int sensorType, Context applicationContext){
		this.sensorType = sensorType;
		switch (sensorType){
			// 1 int TYPE_ACCELEROMETER A constant describing an accelerometer sensor type.
			case 1:
				this.dimensions = 3;
				this.name = getString(R.string.sensor_accelerometer, applicationContext);
				this.oscPrefix = "accelerometer/";
				break;
			// 2 int TYPE_MAGNETIC_FIELD A constant describing a magnetic field sensor type.
			case 2:
				this.dimensions = 3;
				this.name = getString(R.string.sensor_magnetic_field, applicationContext);
				this.oscPrefix = "magneticfield/";
				break;
			// 3 int TYPE_ORIENTATION This constant was deprecated in API level 8. use SensorManager.getOrientation() instead.
			case 3:
				this.dimensions = 3;
				this.name = getString(R.string.sensor_orientation, applicationContext);
				this.oscPrefix = "orientation/";
				break;
			// 4 int TYPE_GYROSCOPE A constant describing a gyroscope sensor type
			case 4:
				this.dimensions = 3;
				this.name = getString(R.string.sensor_gyroscope, applicationContext);
				this.oscPrefix = "gyroscope/";
				break;
			// 5 int TYPE_LIGHT A constant describing a light sensor type.
			case 5:
				this.dimensions = 1;
				this.name = getString(R.string.sensor_light, applicationContext);
				this.oscPrefix = "light/";
				break;
			// 6 int TYPE_PRESSURE A constant describing a pressure sensor type
			case 6:
				this.dimensions = 1;
				this.name = getString(R.string.sensor_pressure, applicationContext);
				this.oscPrefix = "pressure/";
				break;
			// 7 int TYPE_TEMPERATURE This constant was deprecated in API level 14. use Sensor.TYPE_AMBIENT_TEMPERATURE instead.
			case 7:
				this.dimensions = 1;
				this.name = getString(R.string.sensor_temperature, applicationContext);
				this.oscPrefix = "temperature/";
				break;
			// 8 int TYPE_PROXIMITY A constant describing a proximity sensor type.
			case 8:
				this.dimensions = 1;
				this.name = getString(R.string.sensor_proximity, applicationContext);
				this.oscPrefix = "proximity/";
				break;
			// 9 int TYPE_GRAVITY A constant describing a gravity sensor type.
			case 9:
				this.dimensions = 3;
				this.name = getString(R.string.sensor_gravity, applicationContext);
				this.oscPrefix = "gravity/";
				break;
			// 10 int TYPE_LINEAR_ACCELERATION A constant describing a linear acceleration sensor type.
			case 10:
				this.dimensions = 5;
				this.name = getString(R.string.sensor_linear_acceleration, applicationContext);
				this.oscPrefix = "linearacceleration/";
				break;
			// 11 int TYPE_ROTATION_VECTOR A constant describing a rotation vector sensor type.
			case 11:
				this.dimensions = 4;
				this.name = getString(R.string.sensor_rotation_vector, applicationContext);
				this.oscPrefix = "rotationvector/";
				break;
			// 12 int TYPE_RELATIVE_HUMIDITY A constant describing a relative humidity sensor type.
			case 12:
				this.dimensions = 1;
				this.name = getString(R.string.sensor_relative_humidity, applicationContext);
				this.oscPrefix = "relativehumidity/";
				break;
			// 13 int TYPE_AMBIENT_TEMPERATURE A constant describing an ambient temperature sensor type
			case 13:
				this.dimensions = 1;
				this.name = getString(R.string.sensor_absolute_humidity, applicationContext);
				this.oscPrefix = "absolutehumidity/";
				break;
			// 14 int TYPE_MAGNETIC_FIELD_UNCALIBRATED A constant describing an uncalibrated magnetic field sensor type.
			case 14:
				this.dimensions = 6;
				this.name = getString(R.string.sensor_magnetic_field_uncalibrated, applicationContext);
				this.oscPrefix = "magneticfielduncalibrated/";
				break;
			// 15 int TYPE_GAME_ROTATION_VECTOR A constant describing an uncalibrated rotation vector sensor type.
			case 15:
				this.dimensions = 3;
				this.name = getString(R.string.sensor_game_rotation_vector, applicationContext);
				this.oscPrefix = "gamerotationvector/";
				break;
			// 16 int TYPE_GYROSCOPE_UNCALIBRATED A constant describing an uncalibrated gyroscope sensor type.
			case 16:
				this.dimensions = 6;
				this.name = getString(R.string.sensor_gyroscope_uncalibrated, applicationContext);
				this.oscPrefix = "gyroscopeuncalibrated/";
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	private String getString(int stringId, Context context) {
		Resources res = context.getResources();
		return res.getString(stringId);
	}

	public String getOscPrefix() {
		return oscPrefix;
	}

	public String getName() {
		return name;
	}
}
