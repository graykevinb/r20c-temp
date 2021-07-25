package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import frc.robot.Subsystem;

public class Hopper extends Subsystem { 
    public WPI_TalonSRX greenIntakeMotor;
    public WPI_TalonSRX turretFeederMotor;

    public Hopper(int queingID, int button, Joystick stick, int greenIntakeID, int turretFeederID) {
        super(queingID, button, stick);
        greenIntakeMotor = new WPI_TalonSRX(greenIntakeID);
        turretFeederMotor = new WPI_TalonSRX(turretFeederID);
    }

    @Override
    public void activate() {
        motor.set(-0.3);
        turretFeederOn();
        SmartDashboard.putBoolean("Hopper On", true);
    }

    @Override
    public void deactivate() {
        motor.stopMotor();
        turretFeederOff();
        SmartDashboard.putBoolean("Hopper On", false);
    }

    public void turretFeederOn(){
        turretFeederMotor.set(0.5);
    }

    public void turretFeederOff(){
        turretFeederMotor.stopMotor();
    }
    public void greenIntakeOn(){
        greenIntakeMotor.set(0.3);
    }
    public void greenIntakeOff(){
        greenIntakeMotor.stopMotor();
    }
}