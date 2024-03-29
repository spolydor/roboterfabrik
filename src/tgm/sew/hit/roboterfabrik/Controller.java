package tgm.sew.hit.roboterfabrik;

import org.apache.commons.cli.ParseException;

import java.util.LinkedList;

/**
 * Created by Patrick on 29.09.14.
 */
public class Controller {

	public static void main(String[] args) throws ParseException{
		new CLI(args).parse();
		Logging.verzFestlegen(getLogsVerzeichnis(args));
		LinkedList<Monteur> monteurLinkedList = new LinkedList<Monteur>();
		Sekretariat sekretariat = new Sekretariat(Controller.getAnzahlMonteure(args));

		Lagermitarbeiter lagermit = new Lagermitarbeiter(Controller.getLagerVerzeichnis(args));
		Lieferant lieferant = new Lieferant(Controller.getLagerVerzeichnis(args));

		TimerWD timer= new TimerWD(Controller.getLaufzeit(args));

		int[] ids = sekretariat.getUniqueIDs();

		lieferant.liefern(Controller.getAnzahlLieferanten(args));
		boolean exceptionthrown =false;
		int lastmonteurstarted=0;
		do {
			//try {
			lieferant.liefern(Controller.getAnzahlLieferanten(args));
			lagermit.readFile();
			Roboter[] fertigeroboter = new Roboter[Controller.getAnzahlMonteure(args)];
			int i=0;
			boolean firsttimerun=true;
			while(i < Controller.getAnzahlMonteure(args)){
				try {
					exceptionthrown = false;
					if(lastmonteurstarted <= i) {
						monteurLinkedList.add(new Monteur(ids[i]));
						monteurLinkedList.get(i).start();
					}
					monteurLinkedList.get(i).setBauteile(lagermit.getAlleBenoetigtenRoboterTeile());
					Logging.writeLog("Mitarbeiter-ID: "+monteurLinkedList.get(i).getID()+" hat die Bauteile: 2Arme, 2Augen, Kettenantrieb, Rumpf angefordert.");
					lagermit.readFile();
					int robID = sekretariat.getId();
					monteurLinkedList.get(i).bauen(robID);
					Logging.writeLog("Mitarbeiter-ID: "+monteurLinkedList.get(i).getID() +" hat einen Roboter mit der ID \"Threadee-ID" +robID +"\" gebaut.");
					fertigeroboter[i] = monteurLinkedList.get(i).getRoboter();
				} catch (ArrayIndexOutOfBoundsException e) {
					/*
					int z = i;
					//i= Controller.getAnzahlMonteure(args)-1;
					for (int o = 0; o < z-1; o++) {
						lagermit.writeFile(fertigeroboter[o]);
						exceptionthrown = true;
						if(timer.tokeepRunning() == false){
							exceptionthrown = false;
						}
					}
					//break;
					*/
				}

				i++;
			}
			lastmonteurstarted=i+1;
		}while(exceptionthrown == true);
		do {
			int i =0;
			try {
				lieferant.liefern(Controller.getAnzahlLieferanten(args));
				for (i = 0; i < Controller.getAnzahlMonteure(args); i++) {
					Roboter fertigrobo = monteurLinkedList.get(i).getRoboter();
					lagermit.writeFile(fertigrobo);
					lagermit.readFile();
					monteurLinkedList.get(i).setBauteile(lagermit.getAlleBenoetigtenRoboterTeile());
					int robID = sekretariat.getId();
					monteurLinkedList.get(i).bauen(robID);
					Logging.writeLog("Mitarbeiter-ID: "+monteurLinkedList.get(i).getID() +" hat einen Roboter mit der ID \"Threadee-ID" +robID +"\" gebaut.");

				}
			}catch (ArrayIndexOutOfBoundsException s){
				Logging.writeLog("Mitarbeiter-ID: " +monteurLinkedList.get(i).getID() +" hat nicht genug Bauteile bekommen.");
			}
		}while( timer.tokeepRunning() == true);
	}

	/**
	 *
	 * @param args
	 * @return
	 */
	private static String getLagerVerzeichnis(String[] args){
		String lager= "--lager";
		for(int i = 0;i<args.length;i++){
			if(lager.equals(args[i])){
				return args[i+1];
			}

		}
		return "fehler";

	}

	/**
	 *
	 * @param args
	 * @return
	 */
	private static int getAnzahlLieferanten(String[] args){
		String lieferanten= "--lieferanten";
		for(int i = 0;i<args.length;i++){
			if(lieferanten.equals(args[i])){
				return Integer.parseInt(args[i+1]);
			}

		}
		return 0;

	}

	/**
	 *
	 * @param args
	 * @return
	 */
	private static int getAnzahlMonteure(String[] args){
		String monteure= "--monteure";
		for(int i = 0;i<args.length;i++){
			if(monteure.equals(args[i])){
				return Integer.parseInt(args[i+1]);
			}

		}
		return 0;

	}

	/**
	 *
	 * @param args
	 * @return
	 */
	private static int getLaufzeit(String[] args){
		String laufzeit= "--laufzeit";
		for(int i = 0;i<args.length;i++){
			if(laufzeit.equals(args[i])){
				return Integer.parseInt(args[i+1]);
			}

		}
		return 0;

	}
	
	private static String getLogsVerzeichnis(String[] args){
		String lager= "--logs";
		for(int i = 0;i<args.length;i++){
			if(lager.equals(args[i])){
				return args[i+1];
			}

		}
		return "fehler";

	}
}
