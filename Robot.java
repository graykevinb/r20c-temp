/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

import java.io.Console;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DigitalInput;

//import frc.robot.Intake;
//import frc.robot.Hopper;
//import frc.robot.Button;
//import frc.robot.RobotState;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private CANSparkMax leftFrontMotor;
  private CANSparkMax leftBackMotor;

  private CANSparkMax rightFrontMotor;
  private CANSparkMax rightBackMotor;

  private final Joystick driveStick = new Joystick(0);
  private final Joystick operatorJoy = new Joystick(1);
  DifferentialDrive driveTrain;

  private final Button button10 = new Button();
  private final Button buttonA = new Button();

  private Timer autoTimer = new Timer();
  public Hopper hopperSystem;
  public Intake intakeSystem;
  public Turret turret;
  public StateMachine stateMachine;

  boolean intakeToggleState = false;
  boolean hopperToggleState = false;

  int intakeButton = 2;
  int shootButton = 1;
  int queueingButton = 5;
  int turretUpButton = 6;
  int turretDownButton = 7;
  int shooterSpeedDownButton = 8;
  int shooterSpeedUpButton = 9;
  
  DigitalInput di_q_switch = new DigitalInput(0);
  
  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   * 
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    leftFrontMotor = new CANSparkMax(1, MotorType.kBrushless);
    leftBackMotor = new CANSparkMax(2, MotorType.kBrushless);

    rightFrontMotor = new CANSparkMax(3, MotorType.kBrushless);
    rightBackMotor = new CANSparkMax(4, MotorType.kBrushless);

    SpeedControllerGroup leftDriveTrainGroup = new SpeedControllerGroup(leftFrontMotor, leftBackMotor);
    SpeedControllerGroup rightDriveTrainGroup = new SpeedControllerGroup(rightFrontMotor, rightBackMotor);

    driveTrain = new DifferentialDrive(leftDriveTrainGroup, rightDriveTrainGroup);
    hopperSystem = new Hopper(ID.QUEING, 2, operatorJoy, ID.GREEN_INTAKE, ID.TURRET_FEEDER);
    intakeSystem = new Intake(ID.BLACK_INTAKE, 3, operatorJoy, ID.EXTEND_SOLENOID, ID.RETRACT_SOLENOID);
    turret = new Turret();
    stateMachine = new StateMachine(hopperSystem, intakeSystem, turret, operatorJoy);

    UsbCamera camera1 = CameraServer.getInstance().startAutomaticCapture(0);
    camera1.setResolution(360, 240);
    camera1.setFPS(15);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
      SmartDashboard.putBoolean("Q-Switch", di_q_switch.get());
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    stateMachine.autonomous = true;
    autoTimer.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
    case kCustomAuto:
     if (autoTimer.get() < 0.7){
        driveTrain.arcadeDrive(-0.5, 0);
      }
      else {
        stateMachine.update();
      }
      break;
    case kDefaultAuto:
    default:
      if (autoTimer.get() < 7.0 && autoTimer.get() > 5.0){
        driveTrain.arcadeDrive(0.5, 0);
      }
      else {
        driveTrain.arcadeDrive(0, 0);
        autoTimer.stop();
        break;
      }
      break;
    }
  }
  @Override
  public void teleopInit() {
      // This is called once when the robot first enters teleoperated mode
      stateMachine.autonomous = false;
  }
  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    /* Drive by Joystick; Toggle Reverse or Inverted Drive */
    if (driveStick.getRawButtonPressed(Button.A)) {
      buttonA.state = !buttonA.state;
    }
    /*if (buttonA.state){
      driveTrain.arcadeDrive(driveStick.getRawAxis(Axis.LEFT_STICK_Y) * -1, driveStick.getRawAxis(Axis.RIGHT_STICK_X));
    } else if (!buttonA.state) {
      driveTrain.arcadeDrive(driveStick.getRawAxis(Axis.LEFT_STICK_Y), driveStick.getRawAxis(Axis.RIGHT_STICK_X));
    }*/
    driveTrain.arcadeDrive(driveStick.getY(), driveStick.getX());

    /*if (Math.abs(operatorJoy.getRawAxis(0)) > 0.5) {
      turret.rotateByJoystick(operatorJoy.getRawAxis(0));
    } else {
      turret.rotateByJoystick(0);
    }*/

    if (operatorJoy.getRawButtonPressed(turretUpButton)) {
      turret.raise();
    }

    if(operatorJoy.getRawButtonPressed(turretDownButton)) {
      turret.lower();
    }
    
    /* Increment & Decrement Turret Speed */
    if (operatorJoy.getRawButtonPressed(shooterSpeedUpButton)) {
      turret.setShooterSpeed(turret.shooterSpeed + 0.1);
    } else if (operatorJoy.getRawButtonPressed(shooterSpeedDownButton)) {
      turret.setShooterSpeed(turret.shooterSpeed - 0.1);
    }

    /* Manual Shoot Override */
    if (operatorJoy.getRawButton(shootButton) == true) { // Trigger to shoot
      /*stateMachine.currentState = RobotState.SHOOT;
      stateMachine.shoot = true
      stateMachine.timer.start();*/
      turret.activate();
    } else {
      turret.deactivate();
    }


    // Intake System Controls
    //intakeSystem.update();
    boolean intakeButtonPressed = operatorJoy.getRawButtonPressed(intakeButton);
    if (intakeButtonPressed) {
      intakeToggleState = !intakeToggleState;
    }

    if (intakeToggleState) {
      intakeSystem.intakeExtend();
      intakeSystem.activate();
      hopperSystem.greenIntakeOn();
      //hopperSystem.activate();
    } else {
      //hopperSystem.deactivate();
      intakeSystem.deactivate();
      intakeSystem.intakeRetract();
      hopperSystem.greenIntakeOff();
    }

    // Hopper Controls
    boolean hopperButton = operatorJoy.getRawButtonPressed(queueingButton);
    if (hopperButton) {
      hopperToggleState = !hopperToggleState;
    } 

    if (hopperToggleState || di_q_switch.get()){
      hopperSystem.activate();
    } else {
      hopperSystem.deactivate();
    }    
    

    double turretAxis = operatorJoy.getRawAxis(0);
    turret.rotateByJoystick(turretAxis, 0.25);
    stateMachine.update();

    //SmartDashboard.putNumber("Ball Count", stateMachine.ballCount);
    SmartDashboard.putNumber("Left Shooter Speed", turret.getLeftShooterSpeed());
    SmartDashboard.putNumber("Right Shooter Speed", turret.getRightShooterSpeed());
    //SmartDashboard.putNumber("Speed Dial", operatorJoy.getRawAxis(2)*50000);
    SmartDashboard.putBoolean("Reverse", buttonA.state);
}

  
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

}