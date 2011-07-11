/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zion;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResultsRow;

import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

/**
 *
 * @author ribadas
 */
public class Djah extends TeamRobot {

    public static String RULES_FILE = "rules.drl";
    public static String CONSULTA_ACCIONES = "query";
    
    private KnowledgeBuilder kbuilder;
    private KnowledgeBase kbase;   // Base de conocimeintos
    private StatefulKnowledgeSession ksession;  // Memoria activa
    private Vector<FactHandle> referenciasHechosActuales = new Vector<FactHandle>();
	private int missedCount;

    
    public Djah(){
    }
    
    @Override
    public void run() {
    	
    	setBodyColor(Color.green);
		setGunColor(Color.blue);
		setRadarColor(Color.red);
		setScanColor(Color.yellow);
		setBulletColor(Color.red);
    	
    	DEBUG.habilitarModoDebug(System.getProperty("robot.debug", "true").equals("true"));    	

    	// Crear Base de Conocimiento y cargar reglas
    	crearBaseConocimiento();

        // Hacer que movimiento de tanque, radar y cañon sean independientes
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);


        while (true) {
        	DEBUG.mensaje("inicio turno");
            //cargarEventos();  // se hace en los métodos onXXXXXEvent()
            cargarEstadoRobot();
            cargarEstadoBatalla();

            // Lanzar reglas
            DEBUG.mensaje("hechos en memoria activa");
            DEBUG.volcarHechos(ksession);           
            ksession.fireAllRules();
            limpiarHechosIteracionAnterior();

            // Recuperar acciones
            Vector<Accion> acciones = recuperarAcciones();
            DEBUG.mensaje("acciones resultantes");
            DEBUG.volcarAcciones(acciones);

            // Ejecutar Acciones
            ejecutarAcciones(acciones);
        	DEBUG.mensaje("fin turno\n");
            execute();  // Informa a robocode del fin del turno (llamada bloqueante)

        }

    }


    private void crearBaseConocimiento() {
        //String ficheroReglas = System.getProperty("robot.reglas", RobotDrools.FICHERO_REGLAS);
    	String ficheroReglas = Djah.RULES_FILE;

        DEBUG.mensaje("crear base de conocimientos");
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        DEBUG.mensaje("cargar reglas desde "+ficheroReglas);
        kbuilder.add(ResourceFactory.newClassPathResource(ficheroReglas, Djah.class), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            System.err.println(kbuilder.getErrors().toString());
        }

        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        DEBUG.mensaje("crear sesion (memoria activa)");
        ksession = kbase.newStatefulKnowledgeSession();
    }



    private void cargarEstadoRobot() {
    	EstadoRobot estadoRobot = new EstadoRobot(this);
        referenciasHechosActuales.add(ksession.insert(estadoRobot));
    }

    private void cargarEstadoBatalla() {
        EstadoBatalla estadoBatalla =
                new EstadoBatalla(getBattleFieldWidth(), getBattleFieldHeight(),
                getNumRounds(), getRoundNum(),
                getTime(),
                getOthers());
        referenciasHechosActuales.add(ksession.insert(estadoBatalla));
    }

    private void limpiarHechosIteracionAnterior() {
        for (FactHandle referenciaHecho : this.referenciasHechosActuales) {
            ksession.retract(referenciaHecho);
        }
        this.referenciasHechosActuales.clear();
    }

    private Vector<Accion> recuperarAcciones() {
        Accion accion;
        Vector<Accion> listaAcciones = new Vector<Accion>();

        for (QueryResultsRow resultado : ksession.getQueryResults(Djah.CONSULTA_ACCIONES)) {
            accion = (Accion) resultado.get("accion");  // Obtener el objeto accion
            accion.setRobot(this);                      // Vincularlo al robot actual
            listaAcciones.add(accion);
            ksession.retract(resultado.getFactHandle("accion")); // Eliminar el hecho de la memoria activa
        }

        return listaAcciones;
    }

    private void ejecutarAcciones(Vector<Accion> acciones) {
        for (Accion accion : acciones) {
            accion.iniciarEjecucion();
        }
    }

    // Insertar en la memoria activa los distintos tipos de eventos recibidos 
    @Override
    public void onBulletHit(BulletHitEvent event) {
    	String hitRobot = event.getName();
    	if(isTeammate(hitRobot)){
    		turnRight(90);
    		ahead(100);
    	}
          referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onBulletHitBullet(BulletHitBulletEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
        missedCount++;
        if(missedCount > 5){
        	turnRight(Math.random() * 180);
    		ahead(Math.random() * 100);
    		missedCount = 0;
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
    	
        referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent ee) {
    	
    	if(isTeammate(ee.getName())){
    		System.out.println("Robo do mesmo time!");
    	} else {
    		referenciasHechosActuales.add(ksession.insert(ee));    		
    	}
    	double angle = Math.toRadians((getHeading() + ee.getBearing()) % 360);
    	
		 
	     // Calculate the coordinates of the robot
	     scannedX = (int)(getX() + Math.sin(angle) * ee.getDistance());
	     scannedY = (int)(getY() + Math.cos(angle) * ee.getDistance());
	}
    
    static public int getBotNumber(String name) {
     	String n = "0";
     	int low = name.indexOf("(")+1; int hi = name.lastIndexOf(")");
     	if (low >= 0 && hi >=0) { n = name.substring(low, hi); }
     	return Integer.parseInt(n);
    }
    
    static public String getBotNameWithNoNumber(String name) {
     	String n = "0";
     	int hi = name.indexOf("(");
     	if (hi >= 0) { 
     		n = name.substring(0, hi - 1); 
     	}
     	return n;
    }
    
    @Override
    public void onMessageReceived(MessageEvent event) {
    	if(event.getMessage() instanceof EnemySpotted){
    		referenciasHechosActuales.add(ksession.insert(event.getMessage()));
    	}
    }
	
	 // Paint a transparent square on top of the last scanned robot
	 public void onPaint(Graphics2D g) {
	     // Set the paint color to a red half transparent color
	     g.setColor(new Color(0x00, 0xff, 0x00, 0x80));
	 
	     // Draw a line from our robot to the scanned robot
	     g.drawLine(scannedX, scannedY, (int)getX(), (int)getY());
	 
	     // Draw a filled square on top of the scanned robot that covers it
	     g.fillRect(scannedX - 20, scannedY - 20, 40, 40);
	     
	     int pointX = (int)(getX() + Math.sin(getHeading()) * 20);
	     int pointY = (int)(getY() + Math.cos(getHeading()) * 20);
	     
	     g.setColor(new Color(0x00, 0x00, 0xff, 0x80));
	     g.drawLine(pointX, pointY, (int)getX(), (int)getY());
	 }
	
	// The coordinates of the last scanned robot
	 int scannedX = Integer.MIN_VALUE;
	 int scannedY = Integer.MIN_VALUE;


}
