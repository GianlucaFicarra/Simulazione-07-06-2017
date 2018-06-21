package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.sun.corba.se.impl.orbutil.graph.Graph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {

	private SerieADAO dao;
	private List<Match> match;
	
	private List<Team> team;
	private TeamIdMap teamIdMap;
	private List<Team> teamStagione;
	
	// grafo delle partite (relativo alla stagione selezionata)
	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo;
	
	//PUNTO 2
	private List<Team> camminoMigliore; //la ricorsiva aggiorna questa soluz, la inizializzo giù per tentativi successivi
	private Set<DefaultWeightedEdge> usedEdges ; //set di archi-->non voglio archi ripetuti nel cammino
	
	
	
	public Model() {
		dao= new SerieADAO();
		
		match = new LinkedList();
		
		/*popolo lista team con tutte le squadre sfruttando la idmap 
		 * per avere gli oggetti squadra e non solo l'id*/
		teamIdMap = new TeamIdMap();
		team = dao.listTeams(teamIdMap); 
		
		teamStagione = new LinkedList(); //sara la lista con le squadre di quella sola stagione
	}


	public List<Season> getAllSeason() { //per popolare tendina stagioni
		return dao.listSeasons();
	}


	public void creaGrafo(Season stagione) {
		teamStagione = new LinkedList(); //inizzializzo nel caso di operazioni successive;
		
		grafo = new SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//calcolo i vertici e gli archi da considerare
		match = dao.getMatchesFromSeason(teamIdMap, stagione);
		
		for(Match m: match) {
			teamStagione.add(m.getHomeTeam());
			teamStagione.add(m.getAwayTeam());
		}
	
		//aggiungo i vertici(squadre che hanno giocato in quella stagione)
		Graphs.addAllVertices(grafo, team);
		
		//Il peso tra TeamA e TeamB: +1 se TeamA vince, 0 se hanno pareggiato, -1 se TeamA ha perso.
		for(Match m: match) {
			
			Team teamHome = m.getHomeTeam();
			Team teamAway = m.getAwayTeam();
			
			int peso=0;
			
				
				switch (m.getFtr()) { //carattere che indica l'esito della partita
				case "H":
					peso = +1;
					break;
					
				case "A":
					peso = -1;
					break;
					
				case "D":
					peso = 0;
					break;
					
				default:
					throw new IllegalArgumentException("Errore interno: risultato non valido = " + m.getFtr());
	
				}
	
			
				Graphs.addEdge(grafo, teamHome, teamAway, peso);
			
		}
		
		
	}
	
	public List<Team> getClassifica() {
		// azzero i punteggi
		for (Team t : grafo.vertexSet())
			t.Azzera();

		// considero ogni partita
		for (DefaultWeightedEdge e : grafo.edgeSet()) {
			Team home = grafo.getEdgeSource(e);
			Team away = grafo.getEdgeTarget(e);
			
			switch ((int) grafo.getEdgeWeight(e)) {
			case +1:
				home.setPunti(3);
				break;
			case -1:
				away.setPunti(3);
				break;
			case 0:
				home.setPunti(1);
				away.setPunti(1);
				break;
			}
		}


		//ordino al volo in ordine dal punto più alto al più piccolo
		Collections.sort(team, new Comparator<Team>(){
			
			public int compare(Team t1, Team t2) { //ordine decrescente di punti
				return t2.getPunti()-t1.getPunti();
			}
			
		});

		return team;
	}
	
	
	public List<Team> getCurrentTeams() { //ordino squadre in ordine alfabetico per metterle nella box
		Collections.sort(team, new Comparator<Team>(){
					
					public int compare(Team t1, Team t2) { //ordine alfabetico
						return t1.getTeam().compareTo(t2.getTeam());
					}
					
				});
		
		return team;
		
	}


	//TeamA, TeamC, TeamK, se nella partita TeamA-TeamC è risultato vincitore TeamA, e nella partita TeamC-TeamK è risultato vincitore TeamC.
	public List<Team> calcolaDomino() { //avvia ricorsione
		
		List<Team> parziale = new ArrayList();
		this.camminoMigliore =new ArrayList();
		this.usedEdges = new HashSet<>() ;
		
		
			/***ATTENZIONE***/
		/**
		 * Elimina dei vertici dal grafo per renderlo
		 * gestibile dalla ricorsione.
		 * Nella soluzione "vera" questa istruzione va rimossa
		 * (però l'algoritmo non termina in tempi umani).
		 */
		this.riduciGrafo(8);
		
		
		for(Team primo: grafo.vertexSet()) { //avvio ricorsione da ogni vertice del grafo
			parziale.add(primo);
			ricorsiva(parziale, primo, 1);
			parziale.remove(primo); //lo rimuovo per avviarla da un altro
		}
			
		return camminoMigliore;
	}
	
	//parto dal primo(che devo passare per averne traccia) e da questo vedo di prendere il percorso più lungo
	private void ricorsiva(List<Team> parziale, Team primo, int step) {
		
		// controlla se ho migliorato il cammino
		if(parziale.size()> camminoMigliore.size()){
			this.camminoMigliore= new ArrayList<>(parziale); //deepcopy perchè voglio la più lunga
		}
		
		//devo vatutare i suoi archi e prendere quelli con peso +1 perche partita vinta
		for(DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(primo)) {
			                 //opp: Graphs.neighborListOf(grafo, primo);
		
			Team t2 = grafo.getEdgeTarget(e) ;
			
			// verifico che l'arco sia relativo ad una partita vinta
			// e che non sia ancora stato utilizzato (non contenuto in usededges)
			
			if(grafo.getEdgeWeight(e) == +1 && !this.usedEdges.contains(e)) {
				                   // provo ad attraversare l'arco
				parziale.add(t2) ;
				usedEdges.add(e) ;
				
				//esploro questo ramo 
				ricorsiva(parziale, t2, step+1);
				
				//avviata la ricorsione torno indietro eliminando l'ultimo arco per esplorare altro percorso
				usedEdges.remove(e) ;
				parziale.remove(parziale.size()-1) ; // togli l'ultimo aggiunto
				     
				         // Attenzione: parziale.remove(t2) ; non funziona perché t2 può comparire più di una volta
			}
		}
	}


	/**
	 * cancella dei vertici dal grafo in modo che la sua dimensione
	 * sia solamente pari a {@code dim} vertici
	 * @param dim
	 */
	private void riduciGrafo(int dim) {
		Set<Team> togliere = new HashSet<>() ;
		
		Iterator<Team> iter = grafo.vertexSet().iterator() ;
		for(int i=0; i<grafo.vertexSet().size()-dim; i++) {
			togliere.add(iter.next()) ;
		}
		grafo.removeAllVertices(togliere) ;
		System.err.println("Attenzione: cancello dei vertici dal grafo");
		System.err.println("Vertici rimasti: "+grafo.vertexSet().size()+"\n");
	}

}
