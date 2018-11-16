package alocacaoprocessos;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FXMLDocumentController implements Initializable{

    @FXML
    private Label lblMemoria;

    @FXML
    private Pane paneMemoria;

    @FXML
    private Button btPlay;

    @FXML
    private RadioButton radioFirst;

    @FXML
    private Label lblTD1;

    @FXML
    private RadioButton radioWorst;

    @FXML
    private TextField txtM1;

    @FXML
    private Label lblTC1;

    @FXML
    private Label lblTD2;

    @FXML
    private TextField txtM2;

    @FXML
    private Label lblTC2;

    @FXML
    private TextField txtTc2;

    @FXML
    private TextField txtMemoria;

    @FXML
    private TextField txtTC1;

    @FXML
    private TextField txtTD2;

    @FXML
    private Label lblSO;

    @FXML
    private TextField txtTD1;

    @FXML
    private TextField txtSO;

    @FXML
    private AnchorPane panePrincipal;

    @FXML
    private Label lblM1;

    @FXML
    private Label lblM2;

    @FXML
    private RadioButton radioBest;
    
    @FXML
    private ImageView imgMemoria;
    
    
    @FXML
    private Label lblQtd;
    
    @FXML
    private TextField txtQtd;
    
    @FXML
    private TableView<Processo> tabProcessos;
    
    @FXML
    private TextArea txtLog;
    
    @FXML
    private TableColumn<Processo,Integer> colId;
    
    @FXML
    private TableColumn<Processo,Integer> colTam;
    @FXML
    private TableColumn<Processo, String> colStat;
    @FXML
    private TableColumn<Processo, Float> colPorc;
    @FXML
    private TableColumn<Processo,Integer> colCria;
    @FXML
    private TableColumn<Processo,Integer> colAloc;
    @FXML
    private TableColumn<Processo,Integer> colConc;
    
    @FXML
    private Label txtTempo;
    @FXML
    private Label lblTempo;
    @FXML
    private Label txtCPU;
    @FXML
    private Label lblCPU;
    
    ObservableList<Processo> processos = FXCollections.observableArrayList();
    ObservableList<Processo> processosCriados = FXCollections.observableArrayList();
    ToggleGroup grupoMetodos = new ToggleGroup();
    int memoria, tamSO, m1, m2, tc1, tc2, td1, td2, qtd;
    float cpu=0;
    Processo sistOp;
    String metodo;
    int ultimo;
    Timer tempo = new Timer();
    ObservableList<Frame> mapa = FXCollections.observableArrayList();
    
    
    TimerTask tick = new TimerTask(){
        @Override
        public void run(){
            Platform.runLater(()->{
                int tempoAtual = Integer.parseInt(txtTempo.getText())+1;
                txtTempo.setText(Integer.toString(tempoAtual));
                processos.forEach(proc -> {
                    if(processosCriados.isEmpty() && proc.tempoCriacao == tempoAtual){
                        processosCriados.add(proc);
                        ultimo = proc.tempoCriacao;
                        Platform.runLater(()->{
                            processos.remove(proc);
                            alocaProcesso(proc, tempoAtual);
                        });
                    }
                    else if( (proc.tempoCriacao + ultimo)==tempoAtual ){
                        proc.tempoCriacao = tempoAtual;
                        ultimo = proc.tempoCriacao;
                        Platform.runLater(()->{
                            processosCriados.add(proc);
                            processos.remove(proc);
                            alocaProcesso(proc, tempoAtual);
                        });
                    }
                });
                processosCriados.forEach(proc -> {
                    if( proc.tempoDuracao+proc.tempoAloc <= tempoAtual && proc.status=="Alocado"){
                        logMsg(proc.id+" Desalocado!");
                        proc.status="Finalizado";
                        Platform.runLater(()->{
                            desalocaProcesso(proc, tempoAtual);
                        });
                    }
                    else if( proc.tempoAloc==tempoAtual && proc.status=="Fila"){
                        alocaProcesso(proc, tempoAtual);
                    }
                });
            });
        }
    };
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Configurando os radio buttons
        radioBest.setToggleGroup(grupoMetodos);
        radioFirst.setToggleGroup(grupoMetodos);
        radioWorst.setToggleGroup(grupoMetodos);
        
        //Configurando tabela
        colId.setCellValueFactory(
            new PropertyValueFactory<>("id"));
        colTam.setCellValueFactory(
            new PropertyValueFactory<>("tamanho"));
        colStat.setCellValueFactory(
            new PropertyValueFactory<>("status"));
        colPorc.setCellValueFactory(
            new PropertyValueFactory<>("porc"));
        colCria.setCellValueFactory(
            new PropertyValueFactory<>("tempoCriacao"));
        colAloc.setCellValueFactory(
            new PropertyValueFactory<>("tempoAloc"));
        colConc.setCellValueFactory(
            new PropertyValueFactory<>("tempoConc"));
        
        tabProcessos.setItems(processosCriados);
        
        //Criando mapa de memória com o tamFisico da imagem do pente
        mapa.add(new Frame(0,631, false, memoria));
    }
    
    public void desenhaSO(){
        sistOp.posFim = (631*tamSO)/memoria;
        StackPane paneSO;
        paneSO = new StackPane();
        paneSO.setStyle("-fx-background-color: #ff0000; -fx-border-color: black;");
        paneSO.setMinHeight(113);
        paneSO.setMinWidth(sistOp.posFim);
        paneSO.setOpacity(0.7);
        Text id = new Text("SO");
        id.setFont(Font.font("Verdana", 20));
        id.setFill(Color.WHITE);
        paneSO.getChildren().add(id);
        
        Frame root = mapa.get(0);
        Frame frameSo = new Frame(0, sistOp.posFim, true, tamSO);
        Frame resto = new Frame(sistOp.posFim, root.posFim, false, memoria-tamSO);
        mapa.set(0,frameSo);
        mapa.add(resto);
        
        Platform.runLater(()->{
            paneMemoria.getChildren().add(paneSO);
            paneSO.relocate(sistOp.posInicio, 101);
        });
    }

    
    public void logMsg(String msg){
        Platform.runLater(() -> {
            txtLog.appendText("\n["+LocalDateTime.now().getHour()+":" + LocalDateTime.now().getMinute()+":"
            + LocalDateTime.now().getSecond()+"] - " + msg);
        });
    }
    
    //Função que gera processos aleatórios
    public void gerarProcessos(){
        for(int i=0; i<qtd; i++){
            String status = "Fila";
            int tamanho = (int) (Math.random() * (m2-m1) + m1);
            int tc = (int) (Math.random() * (tc2-tc1)) + tc1;
            int td = (int) (Math.random() * (td2-td1)) + td1;
            float porc = ( (float)tamanho/(float)memoria) * 100;
            int tamFisico = (631*tamanho)/memoria;
            Processo p = new Processo(i, tamanho, tc, td, status, porc, tamFisico);
            processos.add(p);
        }
    }
    
    public void juntaRestos(){
        for (int i = 2; i < mapa.size(); i++) {
            Frame atual = mapa.get(i);
            Frame anterior = mapa.get(i-1);
            if (!anterior.ocupado && !atual.ocupado) {
                Frame novo = new Frame(anterior.posInicio, atual.posFim, false, anterior.tamanho + atual.tamanho);
                mapa.set(mapa.indexOf(anterior), novo);
                mapa.remove(atual);
                anterior = novo;
                logMsg("Juntei restos");
            }
        }
        logMsg("Frames: " + mapa.size());
    }
    
    public void desalocaProcesso(Processo p, int tempo){
        Platform.runLater(()->{
            try{
                p.status="Desalocado";
                p.desenho.setVisible(false);
                paneMemoria.getChildren().remove(p.desenho);
                p.desenho = null;
                p.tempoConc=tempo;
                p.frame.ocupado = false;
            }
            catch(Exception e){}
        });
        cpu-=p.porc;
        atualizaCPU();
        juntaRestos();
    }
    
    public void firstFit(Processo p, int tempo){
        for(int i=0; i<mapa.size(); i++){
            Frame frame = mapa.get(i);
            if(!(frame.ocupado) && frame.tamanho>p.tamanho){
                p.tamFisico = (frame.tamFisico*p.tamanho)/frame.tamanho;
                int indice = mapa.indexOf(frame);
                Frame aloc = new Frame(frame.posInicio, frame.posInicio+p.tamFisico, true, p.tamanho);
                Frame resto = new Frame(frame.posInicio+p.tamFisico, frame.posFim, false, frame.tamanho-p.tamanho);
                p.frame = aloc;
                mapa.set(indice, aloc);
                mapa.add(indice+1, resto);
                
                p.posInicio = frame.posInicio;
                p.posFim = frame.posInicio+p.tamFisico;
                p.tempoAloc = tempo;
                p.status="Alocado";
                
                desenhaProcesso(p);
                cpu+=p.porc;
                atualizaCPU();
                break;
            }
            else if(!(frame.ocupado) && frame.tamanho==p.tamanho){
                int indice = mapa.indexOf(frame);
                mapa.set(indice, new Frame(frame.posInicio, frame.posInicio+p.tamFisico, true, p.tamanho));
                p.posInicio = frame.posInicio;
                p.posFim = frame.posFim;
                p.tempoAloc = tempo;
                p.status="Alocado";
                
                desenhaProcesso(p);
                cpu+=p.porc;
                atualizaCPU();
                break;
            }
        }
    }
    
    public void desenhaProcesso(Processo p){
        StackPane paneProc;
        paneProc = new StackPane();
        paneProc.setStyle("-fx-background-color: #0000ff; -fx-border-color:black");
        paneProc.setMinHeight(113);
        paneProc.setMinWidth( p.tamFisico );
        paneProc.setOpacity(0.7);
        
        Text id = new Text( Integer.toString(p.id) );
        id.setFont(Font.font("Verdana", 20));
        id.setFill(Color.WHITE);
        paneProc.getChildren().add(id);
        
        p.desenho = paneProc;
        Platform.runLater(()->{
            paneMemoria.getChildren().add(paneProc);
            paneProc.relocate(p.posInicio, 101);
        });
    }
    
    public void alocaProcesso(Processo p, int tempo){
        switch(metodo){
            case "First Fit":
                firstFit(p, tempo);
        }
    }
    
    public void atualizaCPU(){
        txtCPU.setText( Float.toString(cpu) );
    }
    
    public int iniciar(){
        //Captura de variáveis
        try{
            qtd = Integer.parseInt(txtQtd.getText());
            memoria = Integer.parseInt(txtMemoria.getText());
            tamSO = Integer.parseInt(txtSO.getText());
            m1 = Integer.parseInt(txtM1.getText());
            m2 = Integer.parseInt(txtM2.getText());
            tc1 = Integer.parseInt(txtTC1.getText());
            tc2 = Integer.parseInt(txtTc2.getText());
            td1 = Integer.parseInt(txtTD1.getText());
            td2 = Integer.parseInt(txtTD2.getText());
            RadioButton selected = (RadioButton) grupoMetodos.getSelectedToggle();
            metodo = selected.getText();
        }
        catch(Exception err){
            logMsg("Preencha todos os campos e apenas com números!");
            return -1;
        }
        //Checagem de valores
        if(tamSO>=memoria){
            logMsg("SO deve ser menor que a memória");
            return -1;
        }
        else if(m2>memoria){
            logMsg("M2 não pode ser maior que a memória");
            return -1;
        }
        else if(qtd>15){
            logMsg("Máximo de processos é 15");
            return -1;
        }
        else{
            btPlay.setDisable(true);
            logMsg(metodo);
            int tamFisico = (631*tamSO)/memoria;
            float porc = ( (float)tamSO/(float)memoria) * 100;
            sistOp = new Processo(99, tamSO, 0, 0, "infinito", porc, tamFisico);
            desenhaSO();
            cpu+=sistOp.porc;
            atualizaCPU();
            gerarProcessos();
            tempo.scheduleAtFixedRate(tick, 1000, 1000);
        }
        return 0;
    }
    
}