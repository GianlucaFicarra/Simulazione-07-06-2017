package it.polito.tdp.seriea.db;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;
import it.polito.tdp.seriea.model.TeamIdMap;

public class SerieADAO {
	
	public List<Season> listSeasons() { //per popolare tendina stagioni
		String sql = "SELECT season, description FROM seasons" ;
		
		List<Season> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Season(res.getInt("season"), res.getString("description"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Team> listTeams(TeamIdMap teamIdMap) { //usata per avere tutte le squadre, uso idmap per avere oggeto team
		String sql = "SELECT team FROM teams" ;
		
		List<Team> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add(teamIdMap.get(new Team(res.getString("team")))) ;;
				//sfruttando il det della idmap, popolo la mappa con nuovi elementi e li aggiungo ain risultati
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}


	public List<Match> getMatchesFromSeason(TeamIdMap teamIdMap, Season stagione) {
		   
				String sql = "select * from matches where season=? ";
				
				List<Match> result = new LinkedList<>() ;
				Connection conn = DBConnect.getConnection() ;
				
				try {
					PreparedStatement st = conn.prepareStatement(sql) ;
					st.setInt(1, stagione.getSeason());
					ResultSet res = st.executeQuery() ;
					
					/*Oggetto match ha come parametri i due oggetti team, ma io qui ho solo d
					 * quindi devo usare trucco idmap per accedere all oggetto*/
			
					while(res.next()) {
						
						Team teamA = teamIdMap.get(res.getString("HomeTeam"));
						Team teamB = teamIdMap.get(res.getString("AwayTeam"));
						
						result.add( new Match(
								res.getInt("match_id"),
								stagione,
								teamA,
								teamB,
								res.getString("FTR")
								)) ;
					}
					
					conn.close();
					return result ;
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null ;
				}
	}

/* non usata perchè il match già torna tutto
	public List<Team> getTeamBySeason(Season stagione) {
		 
		String sql = "SELECT DISTINCT team " + 
				"FROM teams as t, matches as m " + 
				"WHERE m.HomeTeam=t.team " + 
				"AND m.Season=? " + 
				"ORDER BY t.team ASC ";
		
		List<Team> result = new LinkedList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, stagione.getSeason());
			ResultSet res = st.executeQuery() ;
			
			/*Oggetto match ha come parametri i due oggetti team, ma io qui ho solo d
			 * quindi devo usare trucco idmap per accedere all oggetto
			
			while(res.next()) {
				result.add( new Team(res.getString("team")));
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
*/

}
