//region_Copyright

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

//endregion

package frc.robot;

import java.sql.Time;

//navx imports
import com.kauailabs.navx.frc.*;
//spark max/neos imports
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Compressor;

//region_Imports

//regular imports
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
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
      public CANSparkMax m_Feeder = new CANSparkMax(4, MotorType.kBrushless); //positive power for in, negative power for out //OG 7
      public CANSparkMax m_Tilting = new CANSparkMax(5, MotorType.kBrushless); //positive power for up, negative power for down //OG 5
      public CANSparkMax m_TopShooter = new CANSparkMax(11, MotorType.kBrushless); //positive power for out //OG 11
      public CANSparkMax m_BotShooter = new CANSparkMax(10, MotorType.kBrushless); //negative power for out //OG 10
      public CANSparkMax m_ControlPanel = new CANSparkMax(13, MotorType.kBrushless); //when facing robot's control panel wheel from front of bot, positive power spins ccw and negative power spins cw //OG 8
      public CANSparkMax m_Climb = new CANSparkMax(3, MotorType.kBrushless); //OG 3
      public CANSparkMax m_LeftWinch = new CANSparkMax(9, MotorType.kBrushless); //OG 9
      public CANSparkMax m_RightWinch = new CANSparkMax(2, MotorType.kBrushless); //OG 4

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

    // Limit Switch input
    public DigitalInput limitSwitch = new DigitalInput(0);
    public boolean engaged;
    public int limitCounter;
    public boolean hasActivated = false;
    public boolean hookerUpState = false;
    public boolean hookerDownState = false;
    public boolean pullGameState = false;
    //public Timer timer;

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
      public Solenoid s_LeftIntake = new Solenoid(5);
      public Solenoid s_RightIntake = new Solenoid(7);
      public Solenoid s_ControlPanel = new Solenoid(4);
      //public Compressor c = new Compressor(0);

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
      public boolean intakeOn = false;
      public boolean shooterOn = false;
      public boolean pullingOn = false;  
      public int i = 0; 

      //gear switching
        //public boolean lowGear=true;
        public boolean lowGear;
        //public boolean switchGears;

      //intake booleans
        public boolean isExtended;

      //ball counting variables
        public boolean oldBallBoolean = false;
        public boolean newBallBoolean = false;
        public boolean ballDebounceBoolean = false;
        public int ballCounter = 0;
        public int ballIndex = 0;

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
   m_Feeder.setIdleMode(CANSparkMax.IdleMode.kBrake);
    extendShooter();
    e_Right1.setPosition(0);
    e_Right2.setPosition(0);
    e_Left1.setPosition(0);
    e_Left1.setPosition(0);
    autoCase = 1; 
  }

  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putNumber("AutoCase", autoCase);
    //SmartDashboard.putNumber("right encoder 1 ", e_Right1.getPosition());
    //SmartDashboard.putNumber("right encoder 2 ", e_Right2.getPosition());
    //SmartDashboard.putNumber("left encoder 1 ", e_Left1.getPosition());
    //SmartDashboard.putNumber("left encoder 2 ", e_Left2.getPosition());

    // switch (autoCase){
    //   case 1:
    //     Timer.delay(2);
    //   case 2:
    //     align();
    //     autoCase++;
    //     break;
    //   case 3:
    //     shootingBalls(false);
    //     autoCase++;
    //     break;
    //   case 4:
    //     Timer.delay(3);
    //     autoCase++;
    //     break;
    //   case 5:
    //     pc_Feeder.setReference(200, ControlType.kPosition);
    //     autoCase++;
    //     break;
    //   default:
    // }

    switch (autoCase){
      case 1:
        Timer.delay(2);
        autoCase++;
        break;
      case 2:
        driveStraight(1, 1000);


    }
  }

  @Override
  public void teleopInit() {
    lowGear = true; 
    m_Feeder.setIdleMode(CANSparkMax.IdleMode.kBrake);
    extendShooter();
    shootingBalls(true);
    m_Feeder.set(0);
    e_Right1.setPosition(0);
    e_Right2.setPosition(0);
    e_Left1.setPosition(0);
    e_Left2.setPosition(0);
    m_DriveTrain.stopMotor();
    ballIndex = 0;
    limitCounter = 0;
    engaged = false;
    hasActivated = false;
    isExtended = false;
  }

  @Override
  public void teleopPeriodic() {
    //gettingVision();
    joystickControl();
    /*//if/else series controlling drivetrain motors
    if (j_Right.getTrigger()){
      visionTracking();
    }
    else {
      joystickControl();
      gearSwitching();
    }*/
    SmartDashboard.putBoolean("HookerUp", hookerUpState);
    boolean switchState = limitSwitch.get();
    
    if(engaged == true && switchState == true){
      engaged = false;
    }
    
    if (j_Right.getTrigger()){
      gearSwitching(); 
    }

    if (j_Right.getRawButton(3)){
      extendIntake();
    }

    if (j_Right.getRawButton(4)){
      retractIntake();
    }
  
    if (j_Left.getRawButton(3)){
      e_Tilting.setPosition(0);
      pc_Tilting.setReference(1, ControlType.kPosition);
    }

    if (j_Left.getRawButton(4)){
      e_Tilting.setPosition(0);
      pc_Tilting.setReference(-1, ControlType.kPosition);
    }

    if (j_Operator.getRawButtonPressed(1)){
      intakeOn = intake(intakeOn);
    }

    if (j_Operator.getRawButton(2)){
      feeder();
    }

    if (j_Operator.getRawButton(3)){
      shooterOn = shootingBalls(shooterOn);
      ballIndex = 0;
    }

    if (j_Operator.getRawButton(4)){
      ejectIntake();
      ballIndex = ballIndex - 1;
    }

    if (j_Operator.getRawButton(5)){
      e_Feeder.setPosition(0);
      ejectFeeder();
    }

    if (j_Operator.getRawButton(6)){
      align();
    }

    if (j_Operator.getRawButton(7)){
      extendShooter();
    }

    if (j_Operator.getRawButton(8)){
      hookerUpState = hookerUp(hookerUpState);
    }
  
    if (j_Operator.getRawButton(9)){
      e_Climb.setPosition(0);
      hookerDownState = hookerDown(hookerDownState);
      //pullGame(400);
    }

    if (j_Operator.getRawButton(10)){
      pullGameState = pullGame(pullGameState);
      //retractIntake();
    }

    if (j_Operator.getRawButton(11)){
      extendIntake();
    }
    
    if(switchState == false && ballIndex < 5){
      if(engaged == false){
        ballIndex++;
        engaged = true;
        hasActivated = true;
        //now = timer.get();
      }
    if (limitCounter >= 2){
      e_Feeder.setPosition(0); 
      feeder();
      hasActivated = false;
      limitCounter = 0;
    }
    if( hasActivated == true){
      limitCounter++;
    }
    }


    //if/else series controlling intaking and shooting balls
    /*if (j_Operator.getRawButton(1)){
      intakingBalls();
      oldBallBoolean = newBallBoolean;
    }
    else if (j_Operator.getRawButton(2)) {
      shootingBalls();
    }
    else {
      intake();
      readyToFeed = false;
      m_BotShooter.setIdleMode(CANSparkMax.IdleMode.kBrake);
      m_TopShooter.setIdleMode(CANSparkMax.IdleMode.kBrake);
      m_TopShooter.stopMotor();
      m_BotShooter.stopMotor();
    } */

    /*if(j_Operator.getRawButton(7)){
      if(targetColor == 0){
        controlPanelRevolution();
      }
      else{
        controlPanelColorSpin();
      }
    }*/

    //climb();
    //gameData();
    //controlPanelExtend();
    //tiltingControl();
    //ballCounterReset();
    //lidarDistance();
    //colorFinder();

    //region_SmartDashboard
      //values that are being put into smart dashboard
      //SmartDashboard.putNumber("right joy", j_Right.getY());
      //SmartDashboard.putNumber("left joy", j_Left.getY());
      //SmartDashboard.putBoolean("Do I see ball", interruptSensor.get());
      //SmartDashboard.putNumber("right encoder 1 ", e_Right1.getPosition());
      //SmartDashboard.putNumber("right encoder 2 ", e_Right2.getPosition());
      //SmartDashboard.putNumber("left encoder 1 ", e_Left1.getPosition());
      //SmartDashboard.putNumber("left encoder 2 ", e_Left2.getPosition());
      SmartDashboard.putNumber("feeder position", e_Feeder.getPosition());
      //SmartDashboard.putNumber("feeder velocity", e_Feeder.getVelocity());
      SmartDashboard.putNumber("ball counter", ballCounter);
      SmartDashboard.putBoolean("Intake LS", switchState);
      SmartDashboard.putNumber("Ball Count Index", ballIndex);
      SmartDashboard.putBoolean("Engaged", engaged);
      SmartDashboard.putNumber("iterations", limitCounter);
      //SmartDashboard.putBoolean("Intake Extended", isExtended);
      SmartDashboard.putBoolean("intake", intakeOn);
      SmartDashboard.putNumber("Feeder Encoders", e_Feeder.getPosition());
      //SmartDashboard.putNumber("top motor velocity", e_TopShooter.getVelocity());
      //SmartDashboard.putNumber("bot motor velocity", e_BotShooter.getVelocity());
      //SmartDashboard.putNumber("tilting encoder", e_Tilting.getPosition());
      //SmartDashboard.putNumber("Chameleon Yaw", chameleon_Yaw);
      //SmartDashboard.putNumber("Distance", dist);
      //SmartDashboard.putBoolean("Lowgear", lowGear);
      SmartDashboard.putBoolean("clmib mode", climbMode);
      SmartDashboard.putBoolean("extend clmib mode", extendClimbMode);

      //SmartDashboard.putBoolean("control panel extended", controlPanelExtended);
      
      if (chameleon_Yaw > -2 && chameleon_Yaw < 2){
        SmartDashboard.putBoolean("Aligned", true);
      }
      else {
        SmartDashboard.putBoolean("Aligned", false);
      }

    //endregion
  }
 
  public void retractIntake() {
    s_LeftIntake.set(false);
    s_RightIntake.set(false);
  }

  

  public boolean hookerUp(boolean hookerOn) {
    SmartDashboard.putNumber("climb", e_Climb.getPosition());
    if (hookerOn == false){
    pc_Climb.setReference(-20, ControlType.kVelocity);
    hookerOn = true;
    }else{
      pc_Climb.setReference(0, ControlType.kVelocity);
      hookerOn = false;
    }
    return hookerOn;
    
  }

  public boolean hookerDown(boolean hookerOn) {
    if (hookerOn == false){
    pc_Climb.setReference(20, ControlType.kVelocity);
    hookerOn = true;
    }
    else{
      pc_Climb.setReference(0, ControlType.kVelocity);
      hookerOn = false;
    }
    return hookerOn;

  }

  public boolean pullGame(boolean pullGameOn) {
    if (pullGameOn == false){
      pc_RightWinch.setReference(20, ControlType.kVelocity);
      pullGameOn = true;
    }
    else{
      pc_RightWinch.setReference(0, ControlType.kVelocity);
      pullGameOn = false;
    }
    return pullGameOn;
  }

  public void extendIntake(){
      s_LeftIntake.set(true);
      s_RightIntake.set(true);
  }


  public void feeder() {
    e_Feeder.setPosition(0);
    pc_Feeder.setReference(45, ControlType.kPosition);
  }

  public void ejectIntake() {
    //if(intakeExtended == true){
        m_Intake.set(-1);
        intakeOn = true;
      //}
  }

  public void ejectFeeder() {
    pc_Feeder.setReference(-30, ControlType.kPosition);
  }

  @Override
  public void testInit() {
    autoCounter = 0;
    navX.zeroYaw();
    e_Left1.setPosition(0);
    e_Left2.setPosition(0);
    e_Right1.setPosition(0);
    e_Right2.setPosition(0);
    e_Climb.setPosition(0);
    e_Feeder.setPosition(0);
  
  }


  @Override
  public void testPeriodic() {
    /*if(j_Operator.getRawButtonPressed(1)){
      intake();
    }*/
    pc_Feeder.setReference(100, ControlType.kPosition);
    SmartDashboard.putNumber("climb", e_Climb.getPosition());
    //SmartDashboard.putNumber("RightWinch", e_RightWinch.getPosition());
    SmartDashboard.putNumber("Intak", e_Intake.getPosition());
    SmartDashboard.putNumber("feeder", e_Feeder.getPosition());
  }

  //region_Methods
    public void gettingVision(){
      chameleonVision = ntwrkInst.getTable("chameleon-vision");
      visionTable = chameleonVision.getSubTable("VisionTable");
      chameleon_Yaw = visionTable.getEntry("targetYaw").getDouble(0);
      controlPanelVision = ntwrkInst.getTable("Vision Table");
      areaRed = controlPanelVision.getEntry("AreaRed").getDouble(0);
      areaGreen = controlPanelVision.getEntry("AreaGreen").getDouble(0);
      areaBlue = controlPanelVision.getEntry("AreaBlue").getDouble(0);
      areaYel = controlPanelVision.getEntry("AreaYellow").getDouble(0);

    }  

    public void joystickControl(){ //method for implementing our lowgear/highgear modes into our driver controls
      if(lowGear){
        m_DriveTrain.tankDrive(j_Left.getY() * .7, -j_Right.getY() * .7);
        //m_DriveTrain.tankDrive(j_XboxController.getY(Hand.kLeft)*.75, -j_XboxController.getY(Hand.kRight)*.75);
      }
      else{
        m_DriveTrain.tankDrive(j_Left.getY(), -j_Right.getY());
        //m_DriveTrain.tankDrive(j_XboxController.getY(Hand.kLeft), -j_XboxController.getY(Hand.kRight));
      }
    }

    /*public void gearSwitching(){ //method for switching our bot to lowgear(less sensitive) or highgear(speedyboi)
      if(j_Right.getRawButton(2) && switchGears || j_XboxController.getTriggerAxis(Hand.kLeft) >.5 && switchGears ){
        if(lowGear){
          lowGear = false;
          switchGears = false;
        }
        else{
          lowGear = true;
          switchGears = false;
        }
      }
      else if(j_Right.getRawButton(2) || j_XboxController.getTriggerAxis(Hand.kLeft) > .5){
        switchGears = true;
      }
    }*/
    public void gearSwitching() {
      if (lowGear = false){
        m_DriveTrain.tankDrive(j_Left.getY() * .7, -j_Right.getY() * .7);
        lowGear = true;
      }
      else{
        m_DriveTrain.tankDrive(j_Left.getY(), -j_Right.getY());
        lowGear = false;
      }
    }

    public boolean intake(boolean intakeOn){ //method for spinning our intake and for ejecting it
      //if(intakeExtended == true){
        if (intakeOn == false){
          m_Intake.set(.3);
          intakeOn = true;
        }
        else{
          m_Intake.set(0);
          intakeOn = false;
        }
        return intakeOn;
      //}
      
      /*else{
        if(j_Operator.getRawButton(5)){
          m_Feeder.set(j_Operator.getY());
          m_Intake.set(-1);
        }
        else{
          m_Intake.set(0);
          m_Feeder.stopMotor();
        }
      }
      if(intakeExtended){
        s_LeftIntake.set(true);
        s_RightIntake.set(true);
        if(j_Operator.getRawButton(3)){
          intakeExtended = false;
        }
      }
      else{
        s_LeftIntake.set(false);
        s_RightIntake.set(false);
      }*/
    }    

    public void visionTracking() {
      if (chameleon_Yaw < -2) {
        pc_Right1.setReference(-500, ControlType.kVelocity);
        pc_Right2.setReference(-500, ControlType.kVelocity);
        pc_Left1.setReference(-500, ControlType.kVelocity);
        pc_Left2.setReference(-500, ControlType.kVelocity);

      }

      else if (chameleon_Yaw > 2) {
        pc_Right1.setReference(500, ControlType.kVelocity);
        pc_Right2.setReference(500, ControlType.kVelocity);
        pc_Left1.setReference(500, ControlType.kVelocity);
        pc_Left2.setReference(500, ControlType.kVelocity);
      }

      else {
        m_Left1.stopMotor();
        m_Left2.stopMotor();
        m_Right1.stopMotor();
        m_Right2.stopMotor();
      }
    }
  
    // public void intakingBalls() {
    //   newBallBoolean = interruptSensor.get();
    //   if(oldBallBoolean != newBallBoolean && newBallBoolean == true && ballDebounceBoolean == false){
    //     //j_XboxController.setRumble(RumbleType.kLeftRumble, .5);
    //     ballCounter++;
    //     if(ballCounter <= 3) {
    //       e_Feeder.setPosition(0);
    //       pc_Feeder.setReference(120, ControlType.kPosition);
    //     }
    //     else if (ballCounter > 3 && ballCounter < 5){
    //       e_Feeder.setPosition(0);
    //       pc_Feeder.setReference(0, ControlType.kPosition);
    //     }
    //   }
    //   else if (newBallBoolean == true){
    //     m_Intake.set(.25);
    //   }
    //   else if (ballDebounceBoolean == true){
    //     ballDebounceBoolean = false;
    //   }

    //   else if (oldBallBoolean == true && newBallBoolean == false){
    //     ballDebounceBoolean = true;
    //   }

    //   else{
    //     m_Intake.set(1);
    //     //j_XboxController.setRumble(RumbleType.kLeftRumble, 0);
    //   } 
      
    //   if (ballCounter > 3){
    //     Timer.delay(.5);
    //     intakeExtended = false;
    //     s_LeftIntake.set(false);
    //     s_RightIntake.set(false);
    //   }
    //   else {
    //     intakeExtended = true;
    //     s_LeftIntake.set(true);
    //     s_RightIntake.set(true);
    //   }
    // } 
    
    public boolean shootingBalls(boolean shooterOn) {
      m_BotShooter.setIdleMode(CANSparkMax.IdleMode.kCoast);
      m_TopShooter.setIdleMode(CANSparkMax.IdleMode.kCoast);

      if (shooterOn == false){
        
        if (e_BotShooter.getVelocity() > -5350){
          m_BotShooter.set(-1);
          readyToFeed = true;
        }
        else {
          m_BotShooter.set(0);
        }
        if (e_TopShooter.getVelocity() < 5350){
          m_TopShooter.set(1);
        }
        else {
          m_TopShooter.set(0);
        }
        // if (readyToFeed == true){
        // m_Feeder.set(1);
        // }
        shooterOn = true;
      }

      else {
        //m_Feeder.stopMotor();
        m_BotShooter.setIdleMode(CANSparkMax.IdleMode.kCoast);
        m_TopShooter.setIdleMode(CANSparkMax.IdleMode.kCoast);
        shooterOn = false;
      }
      return shooterOn;
    }
    
    
    public void tiltingControl() {
      if (j_Operator.getRawButton(9)){
        pc_Tilting.setReference(25, ControlType.kPosition);
      }
  
      else if (j_Operator.getRawButton(8)){
        pc_Tilting.setReference(0, ControlType.kPosition);
      }
    }
    
    public void ballCounterReset() { 
      if (j_Operator.getRawButton(4)){
        ballCounter = 0;
      }
    }
    
    public void lidarDistance() {
      if(lidarSensor.get() < 1){
        dist = 0;
      }
      else{
        dist = (lidarSensor.getPeriod()*1000000.0/10.0) - off; //convert to distance. sensor is high 10 us for every centimeter. 
      }
    }

    public void colorFinder() {
      if (areaBlue > areaGreen && areaBlue > areaRed && areaBlue > areaYel){
        currentColor = 1;
        }
        else if (areaGreen > areaBlue && areaGreen > areaRed && areaGreen > areaYel){
       currentColor = 2;
        }
        else if (areaRed > areaGreen && areaRed > areaBlue && areaRed > areaYel){
       currentColor = 3;
        }
        else if (areaYel > areaGreen && areaYel > areaRed && areaYel > areaBlue){
       currentColor = 4;
        }
        else{
       currentColor = 0;
        }
    }

    public void controlPanelRevolution() {
      if (sawColor == true && currentColor == targetColor){
        revolutionCount++;
        sawColor = false;
      }
      
      if (currentColor != targetColor){
        sawColor = true;
      }
        
      if (revolutionCount < 6){
        m_ControlPanel.set(-.69);
      }
      else if (revolutionCount > 8){
        m_ControlPanel.stopMotor();
      }
      else{
        m_ControlPanel.stopMotor();
      }

    }

    public void controlPanelColorSpin() {
      if (currentColor == targetColor){
        m_ControlPanel.stopMotor();
      }
      else if (targetColor == 4){
        m_ControlPanel.set(-.30);
      }
      else{
        m_ControlPanel.set(-Math.abs(currentColor - targetColor) / controlPanelConstant);
      }
    }

    public void controlPanelExtend(){ 
      if(j_Operator.getRawButton(6) && extendControlPanel){
        if(controlPanelExtended){
          controlPanelExtended = false;
          extendControlPanel = false;
        }
        else{
          controlPanelExtended = true;
          extendControlPanel = false;
        }
      }
      else{
        extendControlPanel = true;
      }
    }

    public void gameData(){
      gameData = DriverStation.getInstance().getGameSpecificMessage();
      if(gameData.length() > 0){
        switch (gameData.charAt(0))
        {
          case 'B' :
            targetColor = 1;
            break;
          case 'G' :
            targetColor = 2;
            break;
          case 'R' :
            targetColor = 3;
            break;
          case 'Y' :
            targetColor = 4;
            break;
          default :
            break;
        }
      } 
      else {
        targetColor = 0;
      }
    }

    public void climb(){
      if(j_Operator.getRawButton(10) && extendClimber){
        if(extendClimbMode){
          extendClimber = false;
          extendClimbMode = false;
        }
        else{
          extendClimber = false;
          extendClimbMode = true;
        }
      }
      else{
        extendClimber = true;
      }

      if(j_Operator.getRawButton(11) && switchClimbMode){
        if(climbMode){
          switchClimbMode = false;
          climbMode = false;
        }
        else{
          switchClimbMode = false;
          climbMode = true;
        }
      }
      else{
        switchClimbMode = true;
      }

      if (climbMode == false) {
        m_LeftWinch.stopMotor();
        m_RightWinch.stopMotor();
      }
      else {
        m_LeftWinch.set(-j_Operator.getY());
        m_RightWinch.set(j_Operator.getY());
      }

      if (extendClimbMode == false) {
        m_Climb.stopMotor();
      }
      else {
        m_Climb.set(j_Operator.getY());
      }

    } 

	public void driveStraight(double feet, double speed){

      //change encoder distance to feet (I didn't write this lmao -R)
      double encoderFeet = feet * 6.095233693;

      //flip speed if negative
      if (feet < 0) {
        speed = -speed;
      }

      //check by absolute value to make sure pos is changing the right distance
      if(Math.abs(e_Left1.getPosition()) < encoderFeet || Math.abs(e_Left2.getPosition()) < encoderFeet || Math.abs(e_Right1.getPosition()) < encoderFeet || Math.abs(e_Right2.getPosition()) < encoderFeet){
        
        // left needs to be opposite of right
        pc_Left1.setReference(speed, ControlType.kVelocity);
        pc_Left2.setReference(speed, ControlType.kVelocity);
        pc_Right1.setReference(-speed, ControlType.kVelocity);
        pc_Right2.setReference(-speed, ControlType.kVelocity);
      }
      else{

        autoCase++;
        //stop the motors and reset the encoder counts for the following methods
        m_DriveTrain.stopMotor();
        e_Right1.setPosition(0);
        e_Right2.setPosition(0);
        e_Left1.setPosition(0);
        e_Left2.setPosition(0);

        //increment upward

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
    public void extendShooter(){
      pc_Tilting.setReference(60, ControlType.kPosition);
    }
    public double getLimelight(){
      NetworkTable limeTable = ntwrkInst.getTable("limelight");
      double rawLimeX = limeTable.getEntry("tx").getDouble(0.5);
      return rawLimeX;
    }
    public boolean shootBallsWithAccuracy(){
      double finalValue = getLimelight();
      boolean isAligned = false;
      if (Math.abs(finalValue) < 1){
        //m_DriveTrain.stopMotor();
        e_Left1.setPosition(0);
        e_Left2.setPosition(0);
        e_Right1.setPosition(0);
        e_Right2.setPosition(0);
        isAligned = true;
      }
      else if (finalValue < 0){
        pc_Left1.setReference(-1000, ControlType.kVelocity);
        pc_Left2.setReference(-1000, ControlType.kVelocity);
        pc_Right1.setReference(-1000, ControlType.kVelocity);
        pc_Right2.setReference(-1000, ControlType.kVelocity);
        isAligned = false;
      }
      else if(finalValue > 0){
        pc_Left1.setReference(1000, ControlType.kVelocity);
        pc_Left2.setReference(1000, ControlType.kVelocity);
        pc_Right1.setReference(1000, ControlType.kVelocity);
        pc_Right2.setReference(1000, ControlType.kVelocity);
        isAligned = false;
      }
      return isAligned;
      }
    public void align(){
      double xValue = getLimelight();
      boolean isAligned = false;
      if (Math.abs(xValue) <= 1){
        isAligned = true;
      }
      else{
        isAligned = false;
      }
      while (isAligned == false){
        isAligned = shootBallsWithAccuracy();
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
    
    //end region
}
