package org.sensors2.osc.sensors;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.nfc.NfcAdapter;
import android.os.Build;

import org.sensors2.osc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by thomas on 05.11.14.
 */
public class Parameters extends org.sensors2.common.sensors.Parameters {
    public static final int BT_SENSOR = Integer.MIN_VALUE + 1;
    private final String oscPrefix;
    private final String name;
    private static final String NFC_PREFIX = "nfc";
    private static final String GEOLOCATION_PREFIX = "location";
    private static final String BLUETOOTH_PREFIX = "bt";

    private Parameters(String oscPrefix, String name, int sensorType) {
        super(sensorType);
        this.name = name;
        this.oscPrefix = oscPrefix;
    }

    public Parameters(Sensor sensor, Context applicationContext) {
        super(sensor);
        switch (sensor.getType()) {
            // 1 int TYPE_ACCELEROMETER A constant describing an accelerometer sensor type.
            case 1:
                this.name = getString(R.string.sensor_accelerometer, applicationContext);
                this.oscPrefix = "accelerometer";
                break;
            // 2 int TYPE_MAGNETIC_FIELD A constant describing a magnetic field sensor type.
            case 2:
                this.name = getString(R.string.sensor_magnetic_field, applicationContext);
                this.oscPrefix = "magneticfield";
                break;
            // 3 int TYPE_ORIENTATION This constant was deprecated in API level 8. use SensorManager.getOrientation() instead.
            case 3:
                this.name = getString(R.string.sensor_orientation, applicationContext);
                this.oscPrefix = "orientation";
                break;
            // 4 int TYPE_GYROSCOPE A constant describing a gyroscope sensor type
            case 4:
                this.name = getString(R.string.sensor_gyroscope, applicationContext);
                this.oscPrefix = "gyroscope";
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
                this.oscPrefix = "gravity";
                break;
            // 10 int TYPE_LINEAR_ACCELERATION A constant describing a linear acceleration sensor type.
            case 10:
                this.name = getString(R.string.sensor_linear_acceleration, applicationContext);
                this.oscPrefix = "linearacceleration";
                break;
            // 11 int TYPE_ROTATION_VECTOR A constant describing a rotation vector sensor type.
            case 11:
                this.name = getString(R.string.sensor_rotation_vector, applicationContext);
                this.oscPrefix = "rotationvector";
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
                this.oscPrefix = "magneticfielduncalibrated";
                break;
            // 15 int TYPE_GAME_ROTATION_VECTOR A constant describing an uncalibrated rotation vector sensor type.
            case 15:
                this.name = getString(R.string.sensor_game_rotation_vector, applicationContext);
                this.oscPrefix = "gamerotationvector";
                break;
            // 16 int TYPE_GYROSCOPE_UNCALIBRATED A constant describing an uncalibrated gyroscope sensor type.
            case 16:
                this.name = getString(R.string.sensor_gyroscope_uncalibrated, applicationContext);
                this.oscPrefix = "gyroscopeuncalibrated";
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
                this.oscPrefix = "georotationvector";
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
            // TYPE_DEVICE_ORIENTATION
            case 27:
                this.name = getString(R.string.sensor_type_device_orientation, applicationContext);
                this.oscPrefix = "deviceorientation";
                break;
            // TYPE_POSE_6DOF
            case 28:
                this.name = getString(R.string.sensor_type_pose_6dof, applicationContext);
                this.oscPrefix = "pose6dof";
                break;
            // TYPE_STATIONARY_DETECT
            case 29:
                this.name = getString(R.string.sensor_type_stationary_detect, applicationContext);
                this.oscPrefix = "stationarydetect";
                break;
            // TYPE_MOTION_DETECT
            case 30:
                this.name = getString(R.string.sensor_type_motion_detect, applicationContext);
                this.oscPrefix = "motiondetect";
                break;
            // TYPE_HEART_BEAT
            case 31:
                this.name = getString(R.string.sensor_type_heart_beat, applicationContext);
                this.oscPrefix = "heartbeat";
                break;
            // TYPE_LOW_LATENCY_OFFBODY_DETECT
            case 34:
                this.name = getString(R.string.sensor_type_low_latency_offbody_detect, applicationContext);
                this.oscPrefix = "lowlatencyoffbodydetect";
                break;
            // TYPE_ACCELEROMETER_UNCALIBRATED
            case 35:
                this.name = getString(R.string.sensor_type_accelorometer_uncalibrated, applicationContext);
                this.oscPrefix = "accelerometeruncalibrated";
                break;
            // TYPE_HINGE_ANGLE
            case 36:
                this.name = getString(R.string.sensor_type_hinge_angle, applicationContext);
                this.oscPrefix = "hingeangle";
                break;
            // TYPE_HEAD_TRACKER
            case 37:
                this.name = getString(R.string.sensor_type_head_tracker, applicationContext);
                this.oscPrefix = "headtracker";
                break;
            // TYPE_ACCELEROMETER_LIMITED_AXES
            case 38:
                this.name = getString(R.string.sensor_type_accelerometer_limited_axes, applicationContext);
                this.oscPrefix = "accelerometerlimitedaxes";
                break;
            // TYPE_GYROSCOPE_LIMITED_AXES
            case 39:
                this.name = getString(R.string.sensor_type_gyroscope_limited_axes, applicationContext);
                this.oscPrefix = "gyroscopelimitedaxes";
                break;
            // TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED
            case 40:
                this.name = getString(R.string.sensor_type_accelerometer_limited_axes_uncalibrated, applicationContext);
                this.oscPrefix = "accelerometerlimitedaxesuncalibrated";
                break;
            // TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED
            case 41:
                this.name = getString(R.string.sensor_type_gyroscope_limited_axes_uncalibrated, applicationContext);
                this.oscPrefix = "gyroscopelimitedaxesuncalibrated";
                break;
            // TYPE_HEADING
            case 42:
                this.name = getString(R.string.sensor_type_heading, applicationContext);
                this.oscPrefix = "heading";
                break;

            default:
                this.name = sensor.getName();
                this.oscPrefix = Integer.toString(sensor.getType());
                break;
            //throw new IllegalArgumentException();
        }
    }

    public Parameters(NfcAdapter nfcAdapter, Context applicationContext) {
        super(nfcAdapter);
        this.name = getString(R.string.sensor_nfc, applicationContext);
        this.oscPrefix = NFC_PREFIX;
    }

    public static List<Parameters> GetSensors(SensorManager sensorManager, Context applicationContext) {
        List<Parameters> parameters = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // add bluetooth
            parameters.add(new org.sensors2.osc.sensors.Parameters(BLUETOOTH_PREFIX, "Bluetooth", BT_SENSOR));
        }
        // add geolocation
        parameters.add(new org.sensors2.osc.sensors.Parameters(GEOLOCATION_PREFIX, getString(R.string.text_guide_geo_headline, applicationContext),org.sensors2.common.sensors.Parameters.GEOLOCATION));
        // add device sensors
        List<Integer> addedSensors = new ArrayList<>();
        for (Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            // Sensors may be listed twice: wake up and non wake up, see https://github.com/SensorApps/Sensors2OSC/issues/17
            int sensorType = sensor.getType();
            if (addedSensors.contains(sensorType)) {
                continue;
            }
            addedSensors.add(sensorType);
            parameters.add(new org.sensors2.osc.sensors.Parameters(sensor, applicationContext));
        }
        // 3: TYPE_ORIENTATION This constant was deprecated in API level 8. use SensorManager.getOrientation() instead.
        // We need 1 (accelerometer) and 2 (magnetic field) to use it.
        if (!addedSensors.contains(3) && addedSensors.contains(1) && addedSensors.contains(2)) {
            parameters.add(createFakeOrientationSensor(applicationContext));
        }
        if (addedSensors.contains(1) && addedSensors.contains(2)) {
            parameters.add(createInclinationSensor(applicationContext));
        }
        return orderParameters(parameters);
    }

    /// First return calibrated sensors, then uncalibrated sensors, then unknown sensors
    private static List<Parameters> orderParameters(List<Parameters> parameters) {
        List<Parameters> ordered = new ArrayList<>();
        List<Parameters> uncalibrated = new ArrayList<>();
        List<Parameters> unknown = new ArrayList<>();

        // See https://developer.android.com/reference/android/hardware/Sensor
        List<Integer> uncalibratedSensorIds = Arrays.asList(40, 35, 15, 41, 16, 14);
        for (Parameters param : parameters){
            // unknown sensor OSC prefixes use sensor IDs as address, so they start with a number.
            char c =  param.getOscPrefix().charAt(0);
            if (c >= '0' && c <= '9') {
                unknown.add(param);
            } else if (uncalibratedSensorIds.contains(param.getSensorType())){
                uncalibrated.add(param);
            } else {
                ordered.add(param);
            }
        }
        ordered.addAll(uncalibrated);
        ordered.addAll(unknown);
        return ordered;
    }

    private static Parameters createFakeOrientationSensor(Context applicationContext) {
        return new Parameters("orientation", getString(R.string.sensor_orientation, applicationContext), FAKE_ORIENTATION);
    }

    private static Parameters createInclinationSensor(Context applicationContext) {
        return new Parameters("inclination", getString(R.string.sensor_inclination, applicationContext), INCLINATION);
    }

    private static String getString(int stringId, Context context) {
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