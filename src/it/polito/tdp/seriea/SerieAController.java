/**
 * Sample Skeleton for 'SerieA.fxml' Controller Class
 */

package it.polito.tdp.seriea;

import java.util.List;
import it.polito.tdp.seriea.model.Model;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class SerieAController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxSeason"
    private ChoiceBox<Season> boxSeason; // Value injected by FXMLLoader

    @FXML // fx:id="boxTeam"
    private ChoiceBox<Team> boxTeam; // Value injected by FXMLLoader

    @FXML
    private Button btnDominio;


    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    
    public void setModel(Model model) {
		this.model = model;
		
		boxSeason.getItems().addAll(model.getAllSeason());
		btnDominio.setDisable(true);
	}

	@FXML
    void handleCarica(ActionEvent event) {

		txtResult.clear();
		
		Season stagione= boxSeason.getValue();
		if(stagione==null) {
			txtResult.setText("Seleionare una stagione!!");
			return;
		}
		
		model.creaGrafo(stagione);
		
		//popolo la seconda lista con le squadre e sblocco pulsante
		boxTeam.getItems().clear();
    	boxTeam.getItems().addAll(model.getCurrentTeams());
    	btnDominio.setDisable(false);
    	
    	//stampo la classifica
		List<Team> result= model.getClassifica();
    	
		if(result==null) {
			txtResult.setText("Impossibile creare grafo");
		}else {
			txtResult.setText("Classifica campionato: ");
			for(Team t: result) {
				txtResult.appendText("\n"+t.toString());
			}
		}
			
    }

    @FXML
    void handleDomino(ActionEvent event) {
    	
    	txtResult.clear();
    	Season stagione= boxSeason.getValue();
    	
    	//this.handleCarica(event); per selezioni successive
    	
    	List<Team> domino = model.calcolaDomino() ;
    	txtResult.appendText(String.format("\nMiglior DOMINO calcolato nell'anno %s ha lunghezza: %d \n", stagione.getDescription(), domino.size()));
    	for(Team t:domino) {
    		txtResult.appendText(t.getTeam()+"\n");
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxSeason != null : "fx:id=\"boxSeason\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert boxTeam != null : "fx:id=\"boxTeam\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnDominio != null : "fx:id=\"btnDominio\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'SerieA.fxml'.";
    }
}
