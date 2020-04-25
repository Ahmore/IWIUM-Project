package adrobot;

import java.io.FileInputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.rules.InvalidRuleSessionException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

import robocode.*;


public class ADRobot extends Robot {

    StatelessRuleSession ruleSession = null;
    List<Object> props = null;
    boolean configExecuted = false;
    String robotState;
    Turn turn = new Turn(0);

    public void setRobotState(String newState) {
        System.out.println("Entering " + newState + " state");
        this.robotState = newState;
    }

    public String getRobotState() {
        return this.robotState;
    }


    public void configureRules() {

        try {
            // Load the rule service provider of the reference implementation.
            // Loading this class will automatically register this provider with
            // the provider manager.
            Class.forName("org.jruleengine.RuleServiceProviderImpl");

            // get the rule service provider from the provider manager
            RuleServiceProvider serviceProvider = RuleServiceProviderManager.getRuleServiceProvider("org.jruleengine");

            // get the RuleAdministrator
            RuleAdministrator ruleAdministrator = serviceProvider.getRuleAdministrator();
            System.out.println("\nAdministration API\n");
            System.out.println("Acquired RuleAdministrator: " + ruleAdministrator);

			// get an input stream to a test XML ruleset
			InputStream inStream = new FileInputStream("C:\\robocode\\robots\\adrobot\\roborules.xml");
			System.out.println("Acquired InputStream to roborules.xml: " + inStream);

            // parse the ruleset from the XML document
            RuleExecutionSet res1 = ruleAdministrator.getLocalRuleExecutionSetProvider(null)
                    .createRuleExecutionSet(inStream, null);
            inStream.close();
            System.out.println("Loaded RuleExecutionSet: " + res1);

            // register the RuleExecutionSet
            String uri = res1.getName();
            ruleAdministrator.registerRuleExecutionSet(uri, res1, null);
            System.out.println("Bound RuleExecutionSet to URI: " + uri);

            // Get a RuleRuntime and invoke the rule engine.
            System.out.println("\nRuntime API\n");

            RuleRuntime ruleRuntime = serviceProvider.getRuleRuntime();
            System.out.println("Acquired RuleRuntime: " + ruleRuntime);

            // create a StatefulRuleSession
            ruleSession = (StatelessRuleSession) ruleRuntime.createRuleSession(uri, new HashMap<Object, Object>(),
                    RuleRuntime.STATELESS_SESSION_TYPE);

            props = new ArrayList<Object>();
            props.add(this);
            props.add(turn);
//			ruleSession.addObject(this);
//			ruleSession.addObject(turnAngle);

        } catch (NoClassDefFoundError e) {
            if (e.getMessage().indexOf("Exception") != -1) {
                System.err.println("Error: The Rule Engine Implementation could not be found.");
            } else {
                System.err.println("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Run method
     */
    public void run() {
        if (!configExecuted) {
            System.out.println("Executing configure rules ...");
            configureRules();
            configExecuted = false;
        }

        int errorBoundForPosition = 40;
        int errorBoundForAngle = 20;
        int angle = 90;

        setRobotState("run");
        while (true) {
            try {
                ruleSession.executeRules(props);

                if (getBattleFieldWidth() - getX() < errorBoundForPosition) {
                    // right band
                    if (getBattleFieldHeight() - getY() < errorBoundForPosition) {
                        // top-right corner
                        if (getGunHeading() > 45 && getGunHeading() < 270 - errorBoundForAngle)
                            turn.setAngle(angle);
                        else
                            turn.setAngle(-2 * angle);
                    } else if (getY() < errorBoundForPosition) {
                        // bottom-right corner
                        if (getGunHeading() > 135 && getGunHeading() < 360 - errorBoundForAngle)
                            turn.setAngle(angle);
                        else
                            turn.setAngle(-2 * angle);
                    } else {
                        // in right middle
                        if (getGunHeading() > 90 && getGunHeading() < 360 - errorBoundForAngle)
                            turn.setAngle(angle);
                        else
                            turn.setAngle(-2 * angle);
                    }
                } else if (getX() < errorBoundForPosition) {
                    // left band
                    if (getBattleFieldHeight() - getY() < errorBoundForPosition) {
                        // top-left corner
                        if (getGunHeading() > 315 || getGunHeading() < 180 - errorBoundForAngle)
                            turn.setAngle(angle);
                        else
                            turn.setAngle(-2 * angle);
                    } else if (getY() < errorBoundForPosition) {
                        // bottom-left corner
                        if (getGunHeading() > 225 || getGunHeading() < 90 - errorBoundForAngle)
                            turn.setAngle(angle);
                        else
                            turn.setAngle(-2 * angle);
                    } else {
                        // in left middle
                        if (getGunHeading() > 270 || getGunHeading() < 180 - errorBoundForAngle)
                            turn.setAngle(angle);
                        else
                            turn.setAngle(-2 * angle);
                    }
                } else if (getBattleFieldHeight() - getY() < errorBoundForPosition) {
                    // top band
                    if (getGunHeading() > 45 && getGunHeading() < 270 - errorBoundForAngle)
                        turn.setAngle(angle);
                    else
                        turn.setAngle(-2 * angle);
                } else if (getY() < errorBoundForPosition) {
                    // bottom band
                    if (getGunHeading() > 135 && getGunHeading() < 360 - errorBoundForAngle)
                        turn.setAngle(angle);
                    else
                        turn.setAngle(-2 * angle);
                } else {
                    // in middle
                    if (getGunHeading() > 90 && getGunHeading() < 360 - errorBoundForAngle)
                        turn.setAngle(angle);
                    else
                        turn.setAngle(-2 * angle);
                }

            } catch (InvalidRuleSessionException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fire when we see a robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        setRobotState("onScannedRobot");
        try {
            ruleSession.executeRules(props);
        } catch (InvalidRuleSessionException | RemoteException e1) {
            e1.printStackTrace();
        }
        setRobotState("run");
    }

    /**
     * We were hit! Turn perpendicular to the bullet, so our seesaw might avoid
     * a future shot.
     */
    public void onHitByBullet(HitByBulletEvent e) {
        setRobotState("onHitByBullet");
        try {
            turn.setAngle(90 - e.getBearing());
            ruleSession.executeRules(props);
        } catch (InvalidRuleSessionException | RemoteException e1) {
            e1.printStackTrace();
        }
        setRobotState("run");
    }

    public void onHitWall(HitWallEvent e) {
        setRobotState("onHitWall");
        try {
            turn.setAngle(90 - e.getBearing());
            ruleSession.executeRules(props);
        } catch (InvalidRuleSessionException | RemoteException e1) {
            e1.printStackTrace();
        }
        setRobotState("run");
    }

    public void onHitRobot(HitRobotEvent e) {
        setRobotState("onHitRobot");
        try {
            if (e.isMyFault()) {
                turn.setAngle(180);
                ruleSession.executeRules(props);
            }
        } catch (InvalidRuleSessionException | RemoteException e1) {
            e1.printStackTrace();
        }
        setRobotState("run");
    }

    public void onBattleEnded(BattleEndedEvent e) {
        try {
            ruleSession.release();
            System.out.println("ruleSession released.");
        } catch (InvalidRuleSessionException | RemoteException e1) {
            e1.printStackTrace();
        }
    }
}
