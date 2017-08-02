
package code;

import lejos.*;
import lejos.utility.*;
import lejos.hardware.Button;
import lejos.hardware.motor.*;
import lejos.robotics.SampleProvider;

public class Main {

    public static void main(String[] args) {
        final MyRobot robot = new MyRobot();
        Simulation.setTrailColor("yellow");
        for (int __index__1 = 0; __index__1 < 4; __index__1++) {
            // Drive inner perimeter of square or rectangle.
            // Assuming FTC field has 4 sides with 90 degree corners.
            robot.getGyro().reset();
            Console.print("Walk the line");
            Button.LEDPattern(1);
            // Stop before reaching wall
            // 40cm Setpoint assumes no  proportional control in the drive power block.
            while (!(Math.round(getSamples(robot.getUltrasonic().getDistanceMode(), 0) * 1000) / 10 < 40)) {
                // Steering setpoint of 0 to drive a straight line.
                // Adjust steering if gyro indicates drifting off course.
                motorMove(getSteerValue((0 + getSamples(robot.getGyro().getAngleMode(), 0)), 100, true), 1, robot.getLeftMotor());
                motorMove(getSteerValue((0 + getSamples(robot.getGyro().getAngleMode(), 0)), 100, false), 1, robot.getRightMotor());
            }
            Console.print("Slowing down");
            Button.LEDPattern(0);
            Button.LEDPattern(5);
            // Stop before starting 90 degree turn
            motorMove(Math.round(0 * 7.2), 1, robot.getLeftMotor());
            motorMove(Math.round(0 * 7.2), 1, robot.getRightMotor());
            while (!(0 == robot.getLeftMotor().getTachoCount())) {
                robot.getLeftMotor().resetTachoCount();
                Delay.msDelay(100); // Wait until fully stopped for 100 ms
                Console.print(robot.getLeftMotor().getTachoCount());
            }
            Console.print("Start turn");
            robot.getGyro().reset();
            Button.LEDPattern(0);
            // Turn 90 degrees (setpoint for pivot)
            while (!(90 == Math.abs(getSamples(robot.getGyro().getAngleMode(), 0)))) {
                robot.getLeftMotor().resetTachoCount(); // Set max power/speed at 45.
                // Allow for power to go negative to correct overshot of setpoint.
                // left turn
                motorMove(getSteerValue(-100, (45 - getSamples(robot.getGyro().getAngleMode(), 0) / 2), true), 1, robot.getLeftMotor());
                motorMove(getSteerValue(-100, (45 - getSamples(robot.getGyro().getAngleMode(), 0) / 2), false), 1, robot.getRightMotor());
                Console.print(getSamples(robot.getGyro().getAngleMode(), 0));
            }
        
        }
        Button.LEDPattern(2);
        // Ensure completely stopped
        motorMove(Math.round(0 * 7.2), 1, robot.getLeftMotor());
        motorMove(Math.round(0 * 7.2), 1, robot.getRightMotor());
    }

    protected static float getSamples(SampleProvider provider,int index) {
        float[] samples = new float[provider.sampleSize()];
        provider.fetchSample(samples,0);
        return samples[index];
    }

    protected static int getSteerValue(double steering,double speed,boolean isLeft) {
        double factor = 1;
        if (steering > 100)
            steering = 100;
        else if (steering < -100)
            steering = -100;
        if (steering > 0 && !isLeft)
            factor = (1 - steering / 50.0);
        else if (steering < 0 && isLeft)
            factor = (steering / 50.0 + 1);
        return (int)(factor * speed * 7.2);
    }

    protected static void motorMove(long speed,int direction,BaseRegulatedMotor motor) {
        motor.setSpeed(speed);
        if (speed * direction > 0)
            motor.forward();
        else
            motor.backward();
    }
}
