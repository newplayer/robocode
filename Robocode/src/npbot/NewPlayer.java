package npbot;

import java.awt.Color;

import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class NewPlayer extends AdvancedRobot {
	 int count = 0;
     String trackName;
     double gunTurnAmt;
    
     double elore = Double.POSITIVE_INFINITY;
     int elore_count = 0;
    
     public void run()
     {              
    	 
    	 setColors(Color.RED,Color.BLACK,Color.WHITE,Color.BLACK,Color.MAGENTA);
    	 
             while (true)
             {              
                     if (trackName!=null)
                     {                      
                             // ha nem látjuk a célt, akkor meg kell keresni!
                             count++;
                            
                             if (count>1)
                             {
                                     setTurnGunLeft(10);
                             }
                             if (count >5)
                             {
                                     setTurnGunRight(10);
                             }
                             if (count > 15)
                             {
                                     // elvesztettük a célt, újat kell találnunk
                                    
                                     setTurnGunRight(Double.POSITIVE_INFINITY);
                                     //setAhead(0);
                                     trackName = null;
                             }
                     }
                     else
                     {
                            
                             // nincsen cél, keresni kell!
                             setTurnGunRight(Double.POSITIVE_INFINITY);
                             setAhead(elore);
                     }
                     execute();
             }
     }
    
     public void onScannedRobot(ScannedRobotEvent e)
     {
             if (trackName == null)
             {
                     trackName = e.getName();
             }
            
             setFireBullet(1);

             count = 0;
            
             gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
             setTurnGunRight(gunTurnAmt);
            
             setTurnRight(e.getBearing());
            
             execute();
             scan();
     }
    
     public void onHitByBullet(HitByBulletEvent e)
     {
             //ha nincsen senki akit követhetnénk, akkor legyen az új cél az aki eltalált
             if (trackName == null)
             {
                     trackName = e.getName();
             }
            
             // esetlegesen jövő újabb töltények elől próbáljunk meg elkanyarodni
             // nagyon egyszerű módszer, bekanyarodunk abba az irányba ahonnan a lövés jött
             //setTurnLeft(e.getBearing());
            
             gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
             setTurnGunRight(gunTurnAmt);
             setTurnRight(e.getBearing());
            
             setAhead(elore);
             execute();
     }
    
     public void onHitWall(HitWallEvent event)
     {
             // ha falnak megyünk, akkor egy Y manőver után haladunk tovább
            
             // tolatás
             setBack(Double.POSITIVE_INFINITY);
             if (event.getBearing()>0 && event.getBearing()<90)
             {
                     setTurnLeft(90-event.getBearing());
                     if (trackName != null) // ha van befogva cél, akkor megpróbáljuk nyomonkövetni
                             setTurnGunRight(90-event.getBearing());
             }
             else
             {
                     setTurnRight(90-event.getBearing());
                     if (trackName != null)
                             setTurnGunLeft(90-event.getBearing());
             }
             // addig tolat amíg a kanyart meg nem teszi
             execute();
             waitFor(new TurnCompleteCondition(this));
            
             // majd előre haladás
             setAhead(Double.POSITIVE_INFINITY);
             if (event.getBearing()>0 && event.getBearing()<90)
             {
                     setTurnLeft(90-event.getBearing());
                     if (trackName != null)
                             setTurnGunRight(90-event.getBearing());
             }
             else
             {
                     setTurnRight(90-event.getBearing());
                     if (trackName != null)
                             setTurnGunLeft(90-event.getBearing());
             }
             // addig megy előre míg a kanyart meg nem teszi
             waitFor(new TurnCompleteCondition(this));
            
             // ez a waitFor nem a legjobb módszer... addig nem müködik más míg ezt be nem fejezni
            
             setAhead(elore);
             execute();
     }
    

     public void onHitRobot(HitRobotEvent event)
     {
             // ha eddig nemvolt célunk, akkor cél felvétele
             if (trackName == null)
             {
                     trackName = event.getName();
             }
            
             // ha a robotunk nekimegy egy másik robotnak, akkor kanyarodás
             if (event.isMyFault())
             {
                     if (event.getBearing()>0 && event.getBearing()<90)
                     {
                             setTurnLeft(45);
                     }
                     else
                     {
                             setTurnRight(45);
                     }
                     execute();
             }
             else
             {
                     // ha nem mi okoztuk, akkor próbáljunk meg ráfordulni a célra
                     setTurnRight(event.getBearing());
             }
            
             gunTurnAmt = normalRelativeAngleDegrees(event.getBearing() + (getHeading() - getRadarHeading()));
             setTurnGunRight(gunTurnAmt);
            
             setAhead(elore);
             execute();
            
     }
}
