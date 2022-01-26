package org.firstinspires.ftc.teamcode.frieght_frenzy_code;

import android.media.AudioManager;
import android.media.SoundPool;

import com.noahbres.jotai.State;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.R;

@TeleOp(name = "teleop ffTom", group = "teleop")
public class teleop_two_remotes extends LinearOpMode {
    public SoundPool mySound;
    public int honkID;
    public SoundPool mySound1;
    public int mallID;
    //imported hardware from "hardwareFF" public class:
    hardwareFF robot = new hardwareFF();

    //this is the timer used to create a toggle switch:
    ElapsedTime toggleBabyTimer = new ElapsedTime();
    ElapsedTime toggleCarousel = new ElapsedTime();
    ElapsedTime wait = new ElapsedTime();

    //this boolean keeps track of whether or not the toggle is on or off
    boolean babyMode = false;
    boolean carouselSpinning = false;

    State currentState;

    private enum State {
        NOTHING,
        SET,
        WAIT,
        FINISH
    }



    public void runOpMode() {
        //initialization code goes here
        robot.init(hardwareMap);
        mySound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0); // PSM
        honkID = mySound.load(hardwareMap.appContext, R.raw.honk, 1); // PSM
        //mySound.play(honkID,1,1,1,0,1);
        mySound1 = new SoundPool(1, AudioManager.STREAM_MUSIC, 0); // PSM
        mallID = mySound.load(hardwareMap.appContext, R.raw.mall, 1); // PSM
        //mySound.play(mallID,1,1,1,0,1);
        //robot.svoIntakeTilt.setPosition(0.5);
        currentState = State.NOTHING;
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (!isStopRequested() && opModeIsActive()) {
            //all teleop code after start button pressed goes here


            /***
             * GAMEPAD 1 CONTROLS
             */

            //make robot wheels go brrr
            if(gamepad1.left_bumper && !babyMode && toggleBabyTimer.seconds() > var.toggleWait){
                //activate baby slow mode when left bumper is pressed
                babyMode = true;
                toggleBabyTimer.reset();
            }
            if(gamepad1.left_bumper && babyMode && toggleBabyTimer.seconds() > var.toggleWait){
                //deactivate baby slow mode by pressing left bumper again
                babyMode = false;
                toggleBabyTimer.reset();
            }

            if (!babyMode) {
                robot.updateDrive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            }
            if(babyMode){
                robot.updateDrive(gamepad1.left_stick_y*0.5, gamepad1.left_stick_x*0.5, gamepad1.right_stick_x*0.5);
            }

            if(gamepad1.a){
                carouselSpinning = true;
                if(robot.alliance_switch.getState() == true){
                    robot.svoCarousel.setPower(var.fullPower);
                }
                else{
                    robot.svoCarousel.setPower(-var.fullPower);
                }

            }
            if(gamepad1.b){
                carouselSpinning = false;
                robot.svoCarousel.setPower(var.stop);
            }
            if(gamepad1.x){
                robot.svoCarousel.setPower(-var.fullPower);
            }

            /***
             * GAMEPAD 2 CONTROLS
             */

            switch(currentState) {
                case NOTHING:
                    if(gamepad2.dpad_down){
                        robot.svoIntake.setPower(var.lessPower);
                        robot.svoIntakeTilt.setPosition(var.intakeTiltCollect);
                        robot.movearm(0.7,var.groundLvl);
                        currentState = State.SET;
                    }
                    if(gamepad2.dpad_left){
                        robot.svoIntakeTilt.setPosition(var.intakeTiltCollect);
                        robot.movearm(0.7,var.firstLvl);
                        currentState = State.SET;
                    }
                    if(gamepad2.dpad_up){
                        robot.svoIntakeTilt.setPosition(var.intakeTiltCollect);
                        robot.movearm(0.7,var.secondLvl);
                        currentState = State.SET;
                    }
                    if(gamepad2.dpad_right){
                        robot.svoIntakeTilt.setPosition(var.intakeTiltHigh);
                        robot.movearm(0.7,var.thirdLvl);
                        currentState = State.SET;
                    }
                    else{
                        robot.mtrArm.setPower(gamepad2.left_stick_y);
                    }
                    break;
                case SET:
                    robot.mtrArm.setPower(0.7);
                    robot.mtrArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    currentState = State.WAIT;
                    break;
                case WAIT:
                    if(robot.mtrArm.isBusy()){

                    }else{
                        currentState = State.FINISH;
                    }
                    break;
                case FINISH:
                    robot.mtrArm.setPower(0);
                    robot.mtrArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    currentState = State.NOTHING;
                    break;
            }


            //turret spin to da right
            if(robot.rightLimit.isPressed()){
                robot.mtrTurret.setPower(-0.3);
                telemetry.addLine("REEE");
            }else {
                robot.mtrTurret.setPower(gamepad2.right_stick_x);
            }
            //turret spin to da left
            if(robot.leftLimit.isPressed()){
                robot.mtrTurret.setPower(gamepad2.right_stick_x*0.3);
            }else{
                robot.mtrTurret.setPower(gamepad2.right_stick_x);
            }


            //servo tilt down
            if(gamepad2.left_bumper){
                robot.svoIntakeTilt.setPosition(var.intakeTiltMid);
            }

            //servo tilt up
            if(gamepad2.right_bumper){
                robot.svoIntakeTilt.setPosition(var.intakeTiltCollect);
            }

            //run intake
            if(gamepad2.a){
                robot.svoIntake.setPower(var.lessPower);
                mySound.play(honkID,1,1,1,0,1);
            }
            //reverse intake
            if(gamepad2.b){
                robot.svoIntake.setPower(-0.4);
            }
            //stop intake
            if(gamepad2.x){
                robot.svoIntake.setPower(var.stop);
            }
            if(gamepad2.y){
                robot.mtrArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                robot.mtrArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }


            //we usually add some telemetry at the end to tell us useful information during testing :)
            if(babyMode){
                telemetry.addLine("baby mode activated");
            }
            else{
                telemetry.addLine("baby mode inactive");
            }


            if(robot.alliance_switch.getState() == true) {
                telemetry.addLine("red alliance");
            }
            else {
                telemetry.addLine("blue alliance");
            }
            if(robot.position_switch.getState() == true) {
                telemetry.addLine("carousel side");
            }
            else {
                telemetry.addLine("warehouse side");
            }

            telemetry.addData("top limit status", robot.topLimit.isPressed());
            telemetry.addData("bottom limit status", robot.bottomLimit.isPressed());
            telemetry.addData("right limit status", robot.rightLimit.isPressed());
            telemetry.addData("left limit status", robot.leftLimit.isPressed());


            telemetry.update();
        }



    }

}

