package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Algorithm;
import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.drivetrain.Drivetrain;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Localizer;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Pedro Pathing constants for the robot.
 *
 * Every value here is produced by an AutoTune procedure (see {@link Tuning}). After running a
 * procedure at http://192.168.43.1:10158, open its "Java" tab and paste the generated config over
 * the matching one below.
 *
 * Tuning order (https://pedropathing.com/docs/pathing/tuning):
 *   1. Mecanum Tuner    -> drivetrainConfig
 *   2. Pinpoint Tuner   -> localizerConfig
 *   3. Foresight Tuner  -> foresightConfig
 */
public class Constants {
    // Motor names match the robot configuration (Control Hub ports 0-3). Directions are carried
    // over from the old RobotControl code; confirm them with the Mecanum Tuner.
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("front_left_drive");
        c.frontRightName.set("front_right_drive");
        c.backLeftName.set("back_left_drive");
        c.backRightName.set("back_right_drive");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.manualBrakeMode.set(true);
    });

    // goBILDA Pinpoint odometry computer. Add it to the robot configuration as "pinpoint" on an
    // I2C port other than 0. The offsets and directions are placeholders until the Pinpoint Tuner
    // is run.
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(0.0);
        c.yPodOffset.set(0.0);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    // Filled in by the Foresight Tuner. Until then, path following (create(), Hold/Line/Curve
    // tests) is unavailable, but driving and localization work.
    public static ForesightConfig foresightConfig = new ForesightConfig(c -> {
    });

    public static Drivetrain drivetrain(HardwareMap h) {
        return new Mecanum(h, drivetrainConfig);
    }

    // To use a different localizer, change it here; Tuning and the TeleOps all go through this.
    public static Localizer localizer(HardwareMap h) {
        return new PinpointLocalizer(h, localizerConfig);
    }

    public static Algorithm algorithm() {
        return new Foresight(foresightConfig);
    }

    public static Follower create(HardwareMap h) {
        return new Follower(localizer(h), drivetrain(h), algorithm());
    }
}
