package it.polito.tdp.seriea.model;

public class Team {
	
	private String team ;
	
	//aggiunta per avere il valore finale della classifica
	private int punti;

	public Team(String team) {
		super();
		this.team = team;
		punti=0;
	}

	
	@Override
	public String toString() {
		return "Squadra: "+ team+" Punti: "+punti;
	}
	
	public int getPunti() {
		return punti;
	}

	public void setPunti(int punti) {
		this.punti += punti;
	}

	public void Azzera() {
		this.punti=0;
	}
	
	

	/**
	 * @return the team
	 */
	public String getTeam() {
		return team;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(String team) {
		this.team = team;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Team other = (Team) obj;
		if (team == null) {
			if (other.team != null)
				return false;
		} else if (!team.equals(other.team))
			return false;
		return true;
	}





	
	

}
