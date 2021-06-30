//region_Copyright

  /*----------------------------------------------------------------------------*/
  /* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
  /* Open Source Software - may be modified and shared by FRC teams. The code   */
  /* must be accompanied by the FIRST BSD license file in the root directory of */
  /* the project.                                                               */
  /*----------------------------------------------------------------------------*/

//endregion

package frc.robot;

//navx imports
import com.kauailabs.navx.frc.*;
//spark max/neos imports
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

//region_Imports

//regular imports
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

  //endregion

public class Robot extends TimedRobot {

  //region_Variables

    //joysticks
      public Joystick j_Left = new Joystick(0);
      public Joystick j_Right = new Joystick(1);
      public Joystick j_Operator = new Joystick(2);
      public XboxController j_XboxController = new XboxController(4);

    //neos
      public CANSparkMax m_Left1 = new CANSparkMax(42, MotorType.kBrushless); //OG 12 
      public CANSparkMax m_Right1 = new CANSparkMax(61, MotorType.kBrushless); //OG 1


    //neo encoders
      public CANEncoder e_Left1 = m_Left1.getEncoder(); //positive forward for Left
      public CANEncoder e_Right1 = m_Right1.getEncoder(); //negative forward for right
 

    //neo pidcontrollers
      public CANPIDController pc_Left1 = m_Left1.getPIDController();
      public CANPIDController pc_Right1 = m_Right1.getPIDController();
      

    //neo controllers
      public SpeedControllerGroup m_Left = new SpeedControllerGroup(m_Left1);
      public SpeedControllerGroup m_Right = new SpeedControllerGroup(m_Right1);
      public DifferentialDrive m_DriveTrain = new DifferentialDrive(m_Left, m_Right); //negative power makes bot move forward, positive power makes bot move packwards

    //tuning variables
      public double kP_Left1, kI_Left1, kD_Left1, kIz_Left1, kFF_Left1;
      public double kP_Right1, kI_Right1, kD_Right1, kIz_Right1, kFF_Right1;
      
    //solenoid variables
      public Solenoid s_LeftIntake = new Solenoid(7);
      public Solenoid s_RightIntake = new Solenoid(5);
      public Solenoid s_ControlPanel = new Solenoid(4);

    //navx variables
      public AHRS navX = new AHRS(SPI.Port.kMXP);
      public float imu_Yaw;

      //gear switching
        public boolean lowGear=true;
        public boolean switchGears;

      //gamedata
        public String gameData;

      //variables for auto phase
        public int autoCase;
        public int autoCounter = 0;
        public boolean resetYaw = false;
        public Boolean checkedYaw = false;
     
  //endregion
 
  @Override
  public void robotInit() {
    m_Left.setInverted(true);
    m_Right.setInverted(false);

    //region_SettingPidVariables
      kP_Left1 = .0001;
      kI_Left1 = 0;
      kD_Left1 = 0.01;
      kIz_Left1 = 0;
      kFF_Left1 = .0001746724891;
      
      kP_Right1 = .0001;
      kI_Right1 = 0;
      kD_Right1 = 0.01;
      kIz_Right1 = 0;
      kFF_Right1 = .0001746724891;
      
     
    //endregion

    //region_SettingPidValues
      pc_Left1.setP(kP_Left1);
      pc_Left1.setI(kI_Left1);
      pc_Left1.setD(kD_Left1);
      pc_Left1.setIZone(kIz_Left1);
      pc_Left1.setFF(kFF_Left1);

      pc_Right1.setP(kP_Right1);
      pc_Right1.setI(kI_Right1);
      pc_Right1.setD(kD_Right1);
      pc_Right1.setIZone(kIz_Right1);
      pc_Right1.setFF(kFF_Right1);
    //endregion

  }

  @Override
  public void autonomousInit() {
    
  }

  @Override
  public void autonomousPeriodic() {
    
  }

   @Override
  public void teleopInit() {
    e_Right1.setPosition(0);
    e_Left1.setPosition(0);
  }

  @Override
  public void teleopPeriodic() {
	  

  @Override
  public void testInit() {
   
  }


  @Override
  public void testPeriodic() {

  }

 
public void driveStraight(double feet, double speed){
      double encoderFeet = feet * 6.095233693;
      if(e_Left1.getPosition() < encoderFeet || e_Right1.getPosition() > -encoderFeet){
        pc_Left1.setReference(speed, ControlType.kVelocity);
        pc_Right1.setReference(-speed, ControlType.kVelocity);
      }
      else{
        m_DriveTrain.stopMotor();
        e_Right1.setPosition(0);
        e_Left1.setPosition(0);
        //resets the encoder counts for the following methods

        autoCounter ++;

      }
    }
    
	public void driveBack(double feet, double speed){
      double encoderFeet = feet * 6.095233693;
      if(e_Left1.getPosition() > -encoderFeet || e_Right1.getPosition() < encoderFeet ||){
        pc_Left1.setReference(-speed, ControlType.kVelocity);
        pc_Right1.setReference(speed, ControlType.kVelocity);

      }
      else{
        m_DriveTrain.stopMotor();
        e_Right1.setPosition(0);
        e_Left1.setPosition(0);
        //resets the encoder counts for the following methods

        autoCounter ++;

      }
    }
  
    public void rightTurn(double targetAngle){
      if(resetYaw == false){
        navX.zeroYaw();
        resetYaw = true;
      }
      double actualYaw = Math.abs(navX.getYaw() % 360);
      if (Math.abs(actualYaw - targetAngle) < 8){
        pc_Left1.setReference(0, ControlType.kVelocity);
        pc_Right1.setReference(0, ControlType.kVelocity);
        e_Right1.setPosition(0);
        e_Left1.setPosition(0);

        navX.reset();
        autoCounter++; 
      }
      else{
        pc_Left1.setReference(1000, ControlType.kVelocity);
        pc_Right1.setReference(1000, ControlType.kVelocity);


      }

    }

    public void leftTurn(double targetAngle){
      if(resetYaw == false){
        navX.zeroYaw();
        resetYaw = true;
      }
      double actualYaw = Math.abs(navX.getYaw() % 360);
      if (Math.abs(actualYaw - targetAngle) < 6){
        pc_Left1.setReference(0, ControlType.kVelocity);
        pc_Right1.setReference(0, ControlType.kVelocity);
        e_Right1.setPosition(0);
        e_Left1.setPosition(0);
        resetYaw = false;
        autoCounter ++;
      }
      else{
        pc_Left1.setReference(-1000, ControlType.kVelocity);
        pc_Right1.setReference(-1000, ControlType.kVelocity);
      }
    }
    
   

  






