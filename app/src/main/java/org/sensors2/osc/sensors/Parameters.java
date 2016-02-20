package org.sensors2.osc.sensors;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.nfc.NfcAdapter;

import org.sensors2.osc.R;

/**
 * Created by thomas on 05.11.14.
 */
public class Parameters extends org.sensors2.common.sensors.Parameters {
    private final String oscPrefix;
    private final String name;

    public Parameters(Sensor sensor, Context applicationContext) {
        super(sensor);
        switch (sensor.getType()) {
            // 1 int TYPE_ACCELEROMETER A constant describing an accelerometer sensor type.
            case 1:
                this.name = getString(R.string.sensor_accelerometer, applicationContext);
                this.oscPrefix = "accelerometer/";
                break;
            // 2 int TYPE_MAGNETIC_FIELD A constant describing a magnetic field sensor type.
            case 2:
                this.name = getString(R.string.sensor_magnetic_field, applicationContext);
                this.oscPrefix = "magneticfield/";
                break;
            // 3 int TYPE_ORIENTATION This constant was deprecated in API level 8. use SensorManager.getOrientation() instead.
            case 3:
                this.name = getString(R.string.sensor_orientation, applicationContext);
                this.oscPrefix = "orientation/";
                break;
            // 4 int TYPE_GYROSCOPE A constant describing a gyroscope sensor type
            case 4:
                this.name = getString(R.string.sensor_gyroscope, applicationContext);
                this.oscPrefix = "gyroscope/";
                break;
            // 5 int TYPE_LIGHT A constant describing a light sensor type.
            case 5:
                this.name = getString(R.string.sensor_light, applicationContext);
                this.oscPrefix = "light";
                break;
            // 6 int TYPE_PRESSURE A constant describing a pressure sensor type
            case 6:
                this.name = getString(R.string.sensor_pressure, applicationContext);
                this.oscPrefix = "pressure";
                break;
            // 7 int TYPE_TEMPERATURE This constant was deprecated in API level 14. use Sensor.TYPE_AMBIENT_TEMPERATURE instead.
            case 7:
                this.name = getString(R.string.sensor_temperature, applicationContext);
                this.oscPrefix = "temperature";
                break;
            // 8 int TYPE_PROXIMITY A constant describing a proximity sensor type.
            case 8:
                this.name = getString(R.string.sensor_proximity, applicationContext);
                this.oscPrefix = "proximity";
                break;
            // 9 int TYPE_GRAVITY A constant describing a gravity sensor type.
            case 9:
                this.name = getString(R.string.sensor_gravity, applicationContext);
                this.oscPrefix = "gravity/";
                break;
            // 10 int TYPE_LINEAR_ACCELERATION A constant describing a linear acceleration sensor type.
            case 10:
                this.name = getString(R.string.sensor_linear_acceleration, applicationContext);
                this.oscPrefix = "linearacceleration/";
                break;
            // 11 int TYPE_ROTATION_VECTOR A constant describing a rotation vector sensor type.
            case 11:
                this.name = getString(R.string.sensor_rotation_vector, applicationContext);
                this.oscPrefix = "rotationvector/";
                break;
            // 12 int TYPE_RELATIVE_HUMIDITY A constant describing a relative humidity sensor type.
            case 12:
                this.name = getString(R.string.sensor_relative_humidity, applicationContext);
                this.oscPrefix = "relativehumidity";
                break;
            // 13 int TYPE_AMBIENT_TEMPERATURE A constant describing an ambient temperature sensor type
            case 13:
                this.name = getString(R.string.sensor_ambient_temperature, applicationContext);
                this.oscPrefix = "ambienttemperature";
                break;
            // 14 int TYPE_MAGNETIC_FIELD_UNCALIBRATED A constant describing an uncalibrated magnetic field sensor type.
            case 14:
                this.name = getString(R.string.sensor_magnetic_field_uncalibrated, applicationContext);
                this.oscPrefix = "magneticfielduncalibrated/";
                break;
            // 15 int TYPE_GAME_ROTATION_VECTOR A constant describing an uncalibrated rotation vector sensor type.
            case 15:
                this.name = getString(R.string.sensor_game_rotation_vector, applicationContext);
                this.oscPrefix = "gamerotationvector/";
                break;
            // 16 int TYPE_GYROSCOPE_UNCALIBRATED A constant describing an uncalibrated gyroscope sensor type.
            case 16:
                this.name = getString(R.string.sensor_gyroscope_uncalibrated, applicationContext);
                this.oscPrefix = "gyroscopeuncalibrated/";
                break;
            // TYPE_SIGNIFICANT_MOTION A constant describing a significant motion trigger sensor.
            case 17:
                this.name = getString(R.string.sensor_significant_motion, applicationContext);
                this.oscPrefix = "significantmotion";
                break;
            // TYPE_STEP_COUNTER A constant describing a step detector sensor.
            case 18:
                this.name = getString(R.string.sensor_step_counter, applicationContext);
                this.oscPrefix = "stepcounter";
                break;
            // TYPE_STEP_DETECTOR A constant describing a step detector sensor.
            case 19:
                this.name = getString(R.string.sensor_step_detector, applicationContext);
                this.oscPrefix = "stepdetector";
                break;
            // TYPE_GEOMAGNETIC_ROTATION_VECTOR A constant describing a geo-magnetic rotation vector.
            case 20:
                this.name = getString(R.string.sensor_geomagnetic_rotation_vector, applicationContext);
                this.oscPrefix = "georotationvector/";
                break;
            // TYPE_HEART_RATE A constant describing a heart rate monitor.
            case 21:
                this.name = getString(R.string.sensor_heartrate, applicationContext);
                this.oscPrefix = "heartrate";
                break;
            // TYPE_TILT_DETECTOR
            case 22:
                this.name = getString(R.string.sensor_tilt_detector, applicationContext);
                this.oscPrefix = "tiltdetector";
                break;
            // TYPE_WAKE_GESTURE
            case 23:
                this.name = getString(R.string.sensor_wake_gesture, applicationContext);
                this.oscPrefix = "wakegesture";
                break;
            // TYPE_GLANCE_GESTURE
            case 24:
                this.name = getString(R.string.sensor_glance_gesture, applicationContext);
                this.oscPrefix = "glancegesture";
                break;
            // TYPE_PICK_UP_GESTURE
            case 25:
                this.name = getString(R.string.sensor_pick_up_gesture, applicationContext);
                this.oscPrefix = "pickupgesture";
                break;
            // SENSOR_TYPE_WRIST_TILT_GESTURE
            case 26:
                this.name = getString(R.string.sensor_wrist_tilt_gesture, applicationContext);
                this.oscPrefix = "wristtiltgesture";
                break;
            default:
                this.name = sensor.getName();
                this.oscPrefix = sensor.getType() + "/";
                break;
            //throw new IllegalArgumentException();
        }
    }

    public Parameters(NfcAdapter nfcAdapter, Context applicationContext) {
        super(nfcAdapter);
        this.name = getString(R.string.sensor_nfc, applicationContext);
        this.oscPrefix = "nfc";
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
