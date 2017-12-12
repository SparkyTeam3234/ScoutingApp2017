/*
Author: Cameron Philo
Date Created: 1/20/2017
Last Edited: 3/19/2017
Use: This app is created for use by FIRST Robotics Teams, mentors, students, volunteers, or fanatics to collect data about FRC competition matches
General Overview: This app is created with a series of basic ButtonViews that, when clicked, will increase a specific integer counter and then display that
        count to a TextView. Once a match is over, pressing the End Match button will save this data in a txt file, and then reset all counters and displays

Special Thanks:
Jacob Paivarinta for helping a Java rookie to improve the buttons from the original if statements, and helping me to get through the headache of exporting a file
David Cherba for mentoring me through my time in FIRST and providing an older scouting app to use as a reference when exporting the txt file
 */
package org.lowellrobotics.scoutingapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.Calendar;
import java.util.Properties;

import static java.util.Calendar.*;

public class MainActivity extends AppCompatActivity {

    //buttons
    Button autoHigh;
    Button autoLow;
    Button autoGear;
    Button autoCross;
    Button teleHigh;
    Button teleLow;
    Button teleGear;
    Button defRating;
    Button penalty;
    Button yellow;
    Button red;
    Button deadRobot;
    Button undo;
    Button endMatch;
    Button autoSide;
    Button climb;
    Button comments;

    //TextView for Counters
    TextView autoHighScore;
    TextView autoLowScore;
    TextView autoGearScore;
    TextView autoCrossScore;
    TextView teleHighScore;
    TextView teleLowScore;
    TextView teleGearScore;
    TextView defScore;
    TextView penaltyNum;
    TextView yellowNum;
    TextView redNum;
    TextView robotState;
    TextView autoSideScore;
    TextView climbScore;

    //Edit text fields
    EditText teamNum;
    EditText matchNum;

    //counters for num of times pressed
    int autoHighCnt = 0;
    int autoLowCnt = 0;
    int autoGearCnt = 0;
    int autoCrossCnt = 0;
    int teleHighCnt = 0;
    int teleLowCnt = 0;
    int teleGearCnt = 0;
    int defRateCnt = 0;
    int penaltyCnt = 0;
    int yellowCnt = 0;
    int redCnt = 0;
    int autoSideCnt = 0;
    int climbCnt = 0;

    //state trackers
    int deadRobotState = 0;
    int lastActionState = 0; //used to track the last action. A state of 0 means no action was performed since the last submission
    String my_text;

    //File saving
    PrintWriter prw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        //Set the items to their views

        //counters in text views
        autoHighScore = (TextView) findViewById(R.id.autoHighCount);
        autoLowScore = (TextView) findViewById(R.id.autoLowCount);
        autoGearScore = (TextView) findViewById(R.id.autoGearCount);
        autoCrossScore = (TextView) findViewById(R.id.autoCrossCount);
        teleHighScore = (TextView) findViewById(R.id.teleHighCount);
        teleLowScore = (TextView) findViewById(R.id.teleLowCount);
        teleGearScore = (TextView) findViewById(R.id.teleGearCount);
        defScore = (TextView) findViewById(R.id.defRate);
        penaltyNum = (TextView) findViewById(R.id.penaltyCount);
        yellowNum = (TextView) findViewById(R.id.yellowCount);
        redNum = (TextView) findViewById(R.id.redCount);
        robotState = (TextView) findViewById(R.id.robot_dead);
        autoSideScore = (TextView) findViewById(R.id.autoSideGear);
        climbScore = (TextView) findViewById(R.id.teleClimbCount);

        //edit text fields
        teamNum = (EditText) findViewById(R.id.team_num);
        matchNum = (EditText) findViewById(R.id.match_num);

        //buttons and their click actions
        autoHigh = (Button) findViewById(R.id.auto_high);
        autoHigh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                autoHighCnt++;
                updateCounter(1);
            }
        });

        autoLow = (Button) findViewById(R.id.auto_low);
        autoLow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                autoLowCnt++;
                updateCounter(2);
            }
        });

        autoGear = (Button) findViewById(R.id.auto_gear);
        autoGear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                autoGearCnt++;
                updateCounter(3);
            }
        });

        autoSide = (Button) findViewById(R.id.auto_side);
        autoSide.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                autoSideCnt++;
                updateCounter(11);
            }
        });

        autoCross = (Button) findViewById(R.id.auto_cross);
        autoCross.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                autoCrossCnt++;
                updateCounter(4);
            }
        });

        teleHigh = (Button) findViewById(R.id.tele_high);
        teleHigh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                teleHighCnt++;
                updateCounter(5);
            }
        });

        teleLow = (Button) findViewById(R.id.tele_low);
        teleLow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                teleLowCnt++;
                updateCounter(6);
            }
        });

        teleGear = (Button) findViewById(R.id.tele_gear);
        teleGear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                teleGearCnt++;
                updateCounter(7);
            }
        });

        defRating = (Button) findViewById(R.id.def);
        defRating.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                defRateCnt++;
                if (defRateCnt > 5) {
                    defRateCnt = 0; //resets the defensive rating on a 5 number cycle
                }
                updateCounter();
            }
        });

        penalty = (Button) findViewById(R.id.penalty);
        penalty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                penaltyCnt++;
                updateCounter(8);
            }
        });

        yellow = (Button) findViewById(R.id.yellow);
        yellow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                yellowCnt++;
                updateCounter(9);
            }
        });

        red = (Button) findViewById(R.id.red);
        red.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                redCnt++;
                updateCounter(10);
            }
        });

        deadRobot = (Button) findViewById(R.id.robot_dead);
        deadRobot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (deadRobotState == 0) {
                    deadRobotState = 1; //state of 1 means auto was NOT performed
                    robotState.setText("Robot Died");
                } else {
                    deadRobotState = 0; //state of 0 means auto WAS performed
                    robotState.setText(R.string.dead);
                }
            }
        });

        climb = (Button) findViewById(R.id.tele_climb);
        climb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                climbCnt++;
                updateCounter(12);
            }
        });

        comments = (Button) findViewById(R.id.comments_button);
        comments.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                commentInput();
            }
        });


        undo = (Button) findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Not really an undo button, but subtracts the from the total of the last input
                //uses the lastActionState number to track which input was last
                if (lastActionState == 1) {
                    if (autoHighCnt > 0) {
                        autoHighCnt = autoHighCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                } else if (lastActionState == 2) {
                    if (autoLowCnt > 0) {
                        autoLowCnt = autoLowCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                } else if (lastActionState == 3) {
                    if (autoGearCnt > 0) {
                        autoGearCnt = autoGearCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                } else if (lastActionState == 4) {
                    if (autoCrossCnt > 0) {
                        autoCrossCnt = autoCrossCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                } else if (lastActionState == 5) {
                    if (teleHighCnt > 0) {
                        teleHighCnt = teleHighCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                } else if (lastActionState == 6) {
                    if (teleLowCnt > 0) {
                        teleLowCnt = teleLowCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                } else if (lastActionState == 7) {
                    if (teleGearCnt > 0) {
                        teleGearCnt = teleGearCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                } else if (lastActionState == 8) {
                    if (penaltyCnt > 0) {
                        penaltyCnt = penaltyCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                } else if (lastActionState == 9) {
                    if (yellowCnt > 0) {
                        yellowCnt = yellowCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                } else if (lastActionState == 10) {
                    if (redCnt > 0) {
                        redCnt = redCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                } else if (lastActionState == 11) {
                    if (autoSideCnt > 0) {
                        autoSideCnt = autoSideCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                } else if (lastActionState == 12) {
                    if (climbCnt > 0) {
                        climbCnt = climbCnt - 1;
                    } else {
                        lastActionState = 0;
                    }
                }
                updateCounter();
            }
        });

        endMatch = (Button) findViewById(R.id.end_match);
        endMatch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //end of match case where file is saved and the fields are reset for next match
                //makes sure storage exists
                isExternalWriteable();

                //checks the API level of the device to determine if permissions will work, or if they need to be asked
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        //call a method to check that the user actually wants to save, which then calls output if end if confirmed
                        confirmEndMatch();
                    } else {
                        requestPermission(); // Code for permission
                        //put call into the request method for when permission granted
                    }
                } else {
                    //samecall
                    confirmEndMatch();
                }
            }
        });
    }

    public void updateCounter(int lastActionState) {
        //higher counter update function, sets the lastActionState tracker appropriately and then calls the normal update
        this.lastActionState = lastActionState;
        updateCounter();
    }

    public void updateCounter() {
        //base counter update function, sets the text fields to their integer scores
        autoHighScore.setText(Integer.toString(autoHighCnt));
        autoLowScore.setText(Integer.toString(autoLowCnt));
        autoGearScore.setText(Integer.toString(autoGearCnt));
        autoCrossScore.setText(Integer.toString(autoCrossCnt));
        teleHighScore.setText(Integer.toString(teleHighCnt));
        teleLowScore.setText(Integer.toString(teleLowCnt));
        teleGearScore.setText(Integer.toString(teleGearCnt));
        defScore.setText(Integer.toString(defRateCnt));
        penaltyNum.setText(Integer.toString(penaltyCnt));
        yellowNum.setText(Integer.toString(yellowCnt));
        redNum.setText(Integer.toString(redCnt));
        autoSideScore.setText(Integer.toString(autoSideCnt));
        climbScore.setText(Integer.toString(climbCnt));
    }

    private void output() {
        //function to create a new file and save the input values
        //name created with the match number as the unique identifier
        String fnx = teamNum.getText().toString() + "_matchlog_" + matchNum.getText().toString() + ".txt";
        StringBuilder sb = new StringBuilder();
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //sets the file to save in the device's downloads sections
        sb.append(path);
        File myfile = new File(path, fnx); //creates a file variable off of name and location
        sb.append(myfile);
        sb.append("1 "); //no idea what that is for, I used some old app codes for this section
        try {
            FileOutputStream os = new FileOutputStream(myfile, true);
            prw = new PrintWriter(os);
            //save in format
            //teamNum,autoHigh,autoLow,autoGear,autoSide,autoCross,teleHigh,teleLow,teleGear,climb,defRating,penalty,yellow,red,robotDeadState
            prw.printf(teamNum.getText().toString() + "," + matchNum.getText().toString() + "," + Integer.toString(autoHighCnt) + "," + Integer.toString(autoLowCnt) + "," + Integer.toString(autoGearCnt) + "," + Integer.toString(autoSideCnt) + "," + Integer.toString(autoCrossCnt) + "," + Integer.toString(teleHighCnt) + "," + Integer.toString(teleLowCnt) + "," + Integer.toString(teleGearCnt) + "," + Integer.toString(climbCnt) + "," + Integer.toString(defRateCnt) + "," + Integer.toString(penaltyCnt) + "," + Integer.toString(yellowCnt) + "," + Integer.toString(redCnt) + "," + Integer.toString(deadRobotState)+ "," + my_text);
            prw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (myfile.isHidden()) {
            //checks is the file is hidden, which it never seems to be, even though it's extremely dificult to access
            System.out.println("The damn file is hidden");
        }
        //Reset things now that the files is saved
        //resetting counters and state tracker
        autoHighCnt = 0;
        autoLowCnt = 0;
        autoGearCnt = 0;
        autoCrossCnt = 0;
        teleHighCnt = 0;
        teleLowCnt = 0;
        teleGearCnt = 0;
        defRateCnt = 0;
        penaltyCnt = 0;
        yellowCnt = 0;
        redCnt = 0;
        autoSideCnt = 0;
        climbCnt = 0;
        deadRobotState = 0;

        //re-displays the original texts on buttons and text boxes
        robotState.setText(R.string.dead);
        teamNum.setText("");
        matchNum.setText("");

        updateCounter(0);
    }

    public void isExternalWriteable() {
        //checks to make sure external storage is accessible, only used as a system output check
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            System.out.println("Can write to storage");
        }
    }

    private boolean checkPermission() {
        //Checks if permission was given for external writing
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        //asks for external permissions, only necessary for Android 6.0 or higher (API 23)
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Toast.makeText(MainActivity.this, "Write External Storage permission allows for saving the data in an accessible txt file. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //Saves the fact that permission was granted for external writing
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    confirmEndMatch();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public void confirmEndMatch () {
        new AlertDialog.Builder(this)
                .setTitle(R.string.end_match_title)
                .setMessage(R.string.end_match_confirm)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with file saving
                        output();
                        //remove dialoug
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        //remove dialoug
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void commentInput() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.comments_title);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alert.setView(input);

        // Set up the buttons
        alert.setPositiveButton(R.string.save_comments, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20)});
                my_text = input.getText().toString();
                if (my_text.length() > 50) {
                    my_text = my_text.substring(0,50);
                }
            }
        });
        alert.setNegativeButton(R.string.cancel_comments, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alert.show();
    }
}


