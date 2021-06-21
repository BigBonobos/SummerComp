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
      public CANSparkMax m_Left2 = new CANSparkMax(60, MotorType.kBrushless); //OG 13
      public CANSparkMax m_Right1 = new CANSparkMax(61, MotorType.kBrushless); //OG 1
      public CANSparkMax m_Right2 = new CANSparkMax(62, MotorType.kBrushless); //OG 2
      public CANSparkMax m_Intake = new CANSparkMax(8, MotorType.kBrushless); //negative power for in, positive power for out //OG 6
      public CANSparkMax m_Feeder = new CANSparkMax(6, MotorType.kBrushless); //positive power for in, negative power for out //OG 7
      public CANSparkMax m_Tilting = new CANSparkMax(5, MotorType.kBrushless); //positive power for up, negative power for down //OG 5
      public CANSparkMax m_TopShooter = new CANSparkMax(11, MotorType.kBrushless); //positive power for out //OG 11
      public CANSparkMax m_BotShooter = new CANSparkMax(10, MotorType.kBrushless); //negative power for out //OG 10
      public CANSparkMax m_ControlPanel = new CANSparkMax(13, MotorType.kBrushless); //when facing robot's control panel wheel from front of bot, positive power spins ccw and negative power spins cw //OG 8
      public CANSparkMax m_Climb = new CANSparkMax(3, MotorType.kBrushless); //OG 3
      public CANSparkMax m_LeftWinch = new CANSparkMax(9, MotorType.kBrushless); //OG 9
      public CANSparkMax m_RightWinch = new CANSparkMax(4, MotorType.kBrushless); //OG 4

    //neo encoders
      public CANEncoder e_Left1 = m_Left1.getEncoder(); //positive forward for Left
      public CANEncoder e_Left2 = m_Left2.getEncoder();
      public CANEncoder e_Right1 = m_Right1.getEncoder(); //negative forward for right
      public CANEncoder e_Right2 = m_Right2.getEncoder();
      public CANEncoder e_Intake = m_Intake.getEncoder(); //negative when intaking
      public CANEncoder e_Feeder = m_Feeder.getEncoder(); //positive when intaking
      public CANEncoder e_Tilting = m_Tilting.getEncoder(); //negative when leaning back
      public CANEncoder e_TopShooter = m_TopShooter.getEncoder(); //positive when shooting ball out
      public CANEncoder e_BotShooter = m_BotShooter.getEncoder(); //negative when shooting ball out
      public CANEncoder e_ControlPanel = m_ControlPanel.getEncoder(); //positive when ccw, negative when cw
      public CANEncoder e_Climb = m_Climb.getEncoder();
      public CANEncoder e_LeftWinch = m_LeftWinch.getEncoder();
      public CANEncoder e_RightWinch = m_RightWinch.getEncoder();



    //neo pidcontrollers
      public CANPIDController pc_Left1 = m_Left1.getPIDController();
      public CANPIDController pc_Left2 = m_Left2.getPIDController();
      public CANPIDController pc_Right1 = m_Right1.getPIDController();
      public CANPIDController pc_Right2 = m_Right2.getPIDController();
      public CANPIDController pc_Intake = m_Intake.getPIDController();
      public CANPIDController pc_Feeder = m_Feeder.getPIDController();
      public CANPIDController pc_Tilting = m_Tilting.getPIDController();
      public CANPIDController pc_TopShooter = m_TopShooter.getPIDController();
      public CANPIDController pc_BotShooter = m_BotShooter.getPIDController();
      public CANPIDController pc_ControlPanel = m_ControlPanel.getPIDController();
      public CANPIDController pc_Climb = m_Climb.getPIDController();
      public CANPIDController pc_LeftWinch = m_LeftWinch.getPIDController();
      public CANPIDController pc_RightWinch = m_RightWinch.getPIDController();



    //neo controllers
      public SpeedControllerGroup m_Left = new SpeedControllerGroup(m_Left1, m_Left2);
      public SpeedControllerGroup m_Right = new SpeedControllerGroup(m_Right1, m_Right2);
      public DifferentialDrive m_DriveTrain = new DifferentialDrive(m_Left, m_Right); //negative power makes bot move forward, positive power makes bot move packwards

    //tuning variables
      public double kP_Left1, kI_Left1, kD_Left1, kIz_Left1, kFF_Left1;
      public double kP_Left2, kI_Left2, kD_Left2, kIz_Left2, kFF_Left2; 
      public double kP_Right1, kI_Right1, kD_Right1, kIz_Right1, kFF_Right1;
      public double kP_Right2, kI_Right2, kD_Right2, kIz_Right2, kFF_Right2;
      public double kP_Feeder, kI_Feeder, kD_Feeder, kIz_Feeder, kFF_Feeder;
      public double kP_Tilting, kI_Tilting, kD_Tilting, kIz_Tilting, kFF_Tilting;
      public double kP_TopShooter, kI_TopShooter, kD_TopShooter, kIz_TopShooter, kFF_TopShooter;
      public double kP_BotShooter, kI_BotShooter, kD_BotShooter, kIz_BotShooter, kFF_BotShooter;
      public double kP_ControlPanel, kI_ControlPanel, kD_ControlPanel, kIz_ControlPanel, kFF_ControlPanel;
      public double kP_Climb, kI_Climb, kD_Climb, kIz_Climb, kFF_Climb;

    //solenoid variables
      public Solenoid s_LeftIntake = new Solenoid(7);
      public Solenoid s_RightIntake = new Solenoid(5);
      public Solenoid s_ControlPanel = new Solenoid(4);

    //navx variables
      public AHRS navX = new AHRS(SPI.Port.kMXP);
      public float imu_Yaw;

    //vision variables
      public NetworkTableInstance ntwrkInst = NetworkTableInstance.getDefault();
      public NetworkTable visionTable;
      public NetworkTable chameleonVision;
      public NetworkTable VisionPi;
      //public NetworkTableEntry xEntry;
      public double Coordinate_X;
      public double Coordinate_Y;
      public double chameleon_Yaw;
      public double chameleon_Pitch;
      public NetworkTable controlPanelVision;
      public double areaRed;
      public double areaGreen;
      public double areaBlue;
      public double areaYel;

    //sensors
      public DigitalInput interruptSensor = new DigitalInput(1);
      public Counter lidarSensor = new Counter(9);
      final double off  = 10; //offset for sensor. test with tape measure
      public double dist;

    //logic variables

      //gear switching
        public boolean lowGear=true;
        public boolean switchGears;
      
      //intake booleans
        public boolean intakeExtended = false;

      //ball counting variables
        public boolean oldBallBoolean = false;
        public boolean newBallBoolean = false;
        public boolean ballDebounceBoolean = false;
        public int ballCounter = 0;

      //shooting booleans
        public boolean readyToFeed = false;

      //controlpanel variables
        public int targetColor;
        public int currentColor;
        public int revolutionCount = 0;
        public boolean sawColor = true; 
        public double controlPanelConstant = 6.9;
        public boolean controlPanelExtended = false;
        public boolean extendControlPanel;

      //gamedata
        public String gameData;

      //climb variables
        public boolean climbMode = false;
        public boolean extendClimbMode = false;
        public boolean switchClimbMode;
        public boolean extendClimber;

      //variables for auto phase
        public int autoCase;
        public int autoCounter = 0;
        public boolean resetYaw = false;

        public Boolean checkedYaw = false;
        public String GalacticColor;
     


  //endregion
 
  @Override
  public void robotInit() {
    e_Tilting.setPosition(0);
    m_Left.setInverted(true);
    m_Right.setInverted(false);
    m_Feeder.setIdleMode(CANSparkMax.IdleMode.kCoast);
    lidarSensor.setMaxPeriod(1.00); //set the max period that can be measured
    lidarSensor.setSemiPeriodMode(true); //Set the counter to period measurement
    lidarSensor.reset();

    //region_SettingPidVariables
      kP_Left1 = .0001;
      kI_Left1 = 0;
      kD_Left1 = 0.01;
      kIz_Left1 = 0;
      kFF_Left1 = .0001746724891;

      kP_Left2 = .0001;
      kI_Left2 = 0;
      kD_Left2 = 0.01;
      kIz_Left2 = 0;
      kFF_Left2 = .0001746724891;
      
      kP_Right1 = .0001;
      kI_Right1 = 0;
      kD_Right1 = 0.01;
      kIz_Right1 = 0;
      kFF_Right1 = .0001746724891;
      
      kP_Right2 = .0001;
      kI_Right2 = 0;
      kD_Right2 = 0.01;
      kIz_Right2 = 0;
      kFF_Right2 = .0001746724891;
        
      kP_Feeder = .5;
      kI_Feeder = 0;
      kD_Feeder = 0;
      kIz_Feeder = 0;
      kFF_Feeder = 0;
      
      kP_Tilting = 1;
      kI_Tilting = 0;
      kD_Tilting = 0;
      kIz_Tilting = 0;
      kFF_Tilting = 0;
      
      kP_TopShooter = .00025;
      kI_TopShooter = 0;
      kD_TopShooter = 0.01;
      kIz_TopShooter = 0;
      kFF_TopShooter = .00017969;
      
      kP_BotShooter = .00035;
      kI_BotShooter = 0;
      kD_BotShooter = .0001;
      kIz_BotShooter = 0;
      kFF_BotShooter = .00018501;
      
      kP_ControlPanel = 1;
      kI_ControlPanel = 0;
      kD_ControlPanel = 0;
      kIz_ControlPanel = 0;
      kFF_ControlPanel = 0;

      kP_Climb = 1;
      kI_Climb = 0;
      kD_Climb = 0;
      kIz_Climb = 0;
      kFF_Climb = 0;
      

    //endregion

    //region_SettingPidValues
      pc_Left1.setP(kP_Left1);
      pc_Left1.setI(kI_Left1);
      pc_Left1.setD(kD_Left1);
      pc_Left1.setIZone(kIz_Left1);
      pc_Left1.setFF(kFF_Left1);

      pc_Left2.setP(kP_Left2);
      pc_Left2.setI(kI_Left2);
      pc_Left2.setD(kD_Left2);
      pc_Left2.setIZone(kIz_Left2);
      pc_Left2.setFF(kFF_Left2);

      pc_Right1.setP(kP_Right1);
      pc_Right1.setI(kI_Right1);
      pc_Right1.setD(kD_Right1);
      pc_Right1.setIZone(kIz_Right1);
      pc_Right1.setFF(kFF_Right1);

      pc_Right2.setP(kP_Right2);
      pc_Right2.setI(kI_Right2);
      pc_Right2.setD(kD_Right2);
      pc_Right2.setIZone(kIz_Right2);
      pc_Right2.setFF(kFF_Right2);

      pc_Feeder.setP(kP_Feeder);
      pc_Feeder.setI(kI_Feeder);
      pc_Feeder.setD(kD_Feeder);
      pc_Feeder.setIZone(kIz_Feeder);
      pc_Feeder.setFF(kFF_Feeder);
      pc_Feeder.setOutputRange(-.69, .69);

      pc_Tilting.setP(kP_Tilting);
      pc_Tilting.setI(kI_Tilting);
      pc_Tilting.setD(kD_Tilting);
      pc_Tilting.setIZone(kIz_Tilting);
      pc_Tilting.setFF(kFF_Tilting);
      pc_Tilting.setOutputRange(-.5, .5);

      pc_TopShooter.setP(kP_TopShooter);
      pc_TopShooter.setI(kI_TopShooter);
      pc_TopShooter.setD(kD_TopShooter);
      pc_TopShooter.setIZone(kIz_TopShooter);
      pc_TopShooter.setFF(kFF_TopShooter);

      pc_BotShooter.setP(kP_BotShooter);
      pc_BotShooter.setI(kI_BotShooter);
      pc_BotShooter.setD(kD_BotShooter);
      pc_BotShooter.setIZone(kIz_BotShooter);
      pc_BotShooter.setFF(kFF_BotShooter);

      pc_ControlPanel.setP(kP_ControlPanel);
      pc_ControlPanel.setI(kI_ControlPanel);
      pc_ControlPanel.setD(kD_ControlPanel);
      pc_ControlPanel.setIZone(kIz_ControlPanel);
      pc_ControlPanel.setFF(kFF_ControlPanel);

      pc_Climb.setP(kP_Climb);
      pc_Climb.setI(kIz_Climb);
      pc_Climb.setD(kD_Climb);
      pc_Climb.setIZone(kIz_Climb);
      pc_Climb.setFF(kFF_Climb);
      pc_Climb.setOutputRange(-.30, .30);

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
 
  }

  @Override
  public void teleopPeriodic() {
    
  }

  @Override
  public void testInit() {
   
  }


  @Override
  public void testPeriodic() {

  }

 
	public void driveStraight(double feet, double speed){
      double encoderFeet = feet * 6.095233693;
      if(e_Left1.getPosition() < encoderFeet || e_Left2.getPosition() < encoderFeet || e_Right1.getPosition() > -encoderFeet || e_Right2.getPosition() > -encoderFeet){
        pc_Left1.setReference(speed, ControlType.kVelocity);
        pc_Left2.setReference(speed, ControlType.kVelocity);
        pc_Right1.setReference(-speed, ControlType.kVelocity);
        pc_Right2.setReference(-speed, ControlType.kVelocity);
      }
      else{
        m_DriveTrain.stopMotor();
        e_Right1.setPosition(0);
        e_Right2.setPosition(0);
        e_Left1.setPosition(0);
        e_Left2.setPosition(0);
        //resets the encoder counts for the following methods

        autoCounter ++;

      }
    }
    
	public void driveBack(double feet, double speed){
      double encoderFeet = feet * 6.095233693;
      if(e_Left1.getPosition() > -encoderFeet || e_Left2.getPosition() > -encoderFeet || e_Right1.getPosition() < encoderFeet || e_Right2.getPosition() < encoderFeet){
        pc_Left1.setReference(-speed, ControlType.kVelocity);
        pc_Left2.setReference(-speed, ControlType.kVelocity);
        pc_Right1.setReference(speed, ControlType.kVelocity);
        pc_Right2.setReference(speed, ControlType.kVelocity);
      }
      else{
        m_DriveTrain.stopMotor();
        e_Right1.setPosition(0);
        e_Right2.setPosition(0);
        e_Left1.setPosition(0);
        e_Left2.setPosition(0);
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
        pc_Left2.setReference(0, ControlType.kVelocity);
        pc_Right1.setReference(0, ControlType.kVelocity);
        pc_Right2.setReference(0, ControlType.kVelocity);
        e_Right1.setPosition(0);
        e_Right2.setPosition(0);
        e_Left1.setPosition(0);
        e_Left2.setPosition(0);
        navX.reset();
        autoCounter++; 
      }
      else{
        pc_Left1.setReference(1000, ControlType.kVelocity);
        pc_Left2.setReference(1000, ControlType.kVelocity);
        pc_Right1.setReference(1000, ControlType.kVelocity);
        pc_Right2.setReference(1000, ControlType.kVelocity);

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
        pc_Left2.setReference(0, ControlType.kVelocity);
        pc_Right1.setReference(0, ControlType.kVelocity);
        pc_Right2.setReference(0, ControlType.kVelocity);
        e_Right1.setPosition(0);
        e_Right2.setPosition(0);
        e_Left1.setPosition(0);
        e_Left2.setPosition(0);
        resetYaw = false;
        autoCounter ++;
      }
      else{
        pc_Left1.setReference(-1000, ControlType.kVelocity);
        pc_Left2.setReference(-1000, ControlType.kVelocity);
        pc_Right1.setReference(-1000, ControlType.kVelocity);
        pc_Right2.setReference(-1000, ControlType.kVelocity);
      }
    }
    
   

  






