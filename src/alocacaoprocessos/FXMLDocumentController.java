package alocacaoprocessos;

import java.net.URL;
import java.time.LocalDateTime;
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
    private TableColumn<Processo,Integer> colDur;
    
    @FXML
    private Label txtTempo;
    @FXML
    private Label lblTempo;
    @FXML
    private Label txtCPU;
    @FXML
    private Label lblCPU;
    @FXML
    private Label txtMedia;
    @FXML
    private Label lblMedia;
    
    
    ObservableList<Processo> processos = FXCollections.observableArrayList();
    ObservableList<Processo> processosCriados = FXCollections.observableArrayList();
    ObservableList<Processo> processosAlocados = FXCollections.observableArrayList();
    ObservableList<Processo> processosFinalizados = FXCollections.observableArrayList();
    
    ObservableList<Frame> framesLivres = FXCollections.observableArrayList();
    ObservableList<Frame> framesOcupados = FXCollections.observableArrayList();
    
    ToggleGroup grupoMetodos = new ToggleGroup();
    int memoria, tamSO, m1, m2, tc1, tc2, td1, td2, qtd, ultimo;
    float cpu=0;
    Processo sistOp;
    String metodo;
    Timer tempo = new Timer();
    
    
    
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
        colDur.setCellValueFactory(
            new PropertyValueFactory<>("tempoDuracao"));
        
        tabProcessos.setItems(processosCriados);
        
        //Criando mapa de memória com o tamFisico da imagem do pente
    }
    
    public void rotina() {
        Platform.runLater(() -> {
            int tempoAtual = Integer.parseInt(txtTempo.getText()) + 1;
            txtTempo.setText(Integer.toString(tempoAtual));
            processos.forEach(proc -> {
                if (processosCriados.isEmpty() && proc.tempoCriacao <= tempoAtual) {
                    processosCriados.add(proc);
                    ultimo = proc.tempoCriacao;
                    Platform.runLater(() -> {
                        processos.remove(proc);
                    });
                } else if ((proc.tempoCriacao + ultimo) <= tempoAtual) {
                    proc.tempoCriacao = tempoAtual;
                    ultimo = proc.tempoCriacao;
                    Platform.runLater(() -> {
                        processosCriados.add(proc);
                        processos.remove(proc);
                    });
                }
            });
            processosCriados.forEach(proc -> {
                if (proc.tempoAloc <= tempoAtual && proc.status.equals("Fila")) {
                    alocaProcesso(proc, tempoAtual);
                }
            });
            processosAlocados.forEach(proc -> {
                if ( proc.status.equals("Executando") && (proc.tempoDuracao + proc.tempoAloc) <= tempoAtual) {
                    Platform.runLater(()->{
                        desalocaProcesso(proc, tempoAtual);
                    });
                }
            });
            if(processosAlocados.isEmpty() && processos.isEmpty()){
                tempo.cancel();
                calculaMedia();
            }
        });
    }
    
    public void calculaMedia(){
        int somatorio=0;
        for(Processo proc: processosFinalizados){
            somatorio += (proc.tempoAloc - proc.tempoCriacao);
        }
        float media = somatorio/qtd;
        txtMedia.setText( Float.toString(media) );
        btPlay.setDisable(false);
        
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
        
        Frame root = framesLivres.get(0);
        Frame frameSo = new Frame(0, sistOp.posFim, true, tamSO);
        Frame resto = new Frame(sistOp.posFim, root.posFim, false, memoria-tamSO);
        framesOcupados.add(frameSo);
        framesLivres.set(0,resto);
        sistOp.frame = frameSo;
        sistOp.desenho = paneSO;
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
    
    public Frame getMaisPerto(List<Frame> lista){
        Frame menor = lista.get(0);
        for(int i=0; i<framesLivres.size();i++){
            Frame frame = framesLivres.get(i);
            if(frame.posInicio<=menor.posInicio){
                menor = frame;
            }
        }
        return menor;
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
    
    
    public void ordenaFrames(){
        ObservableList<Frame> result = FXCollections.observableArrayList();
        while( !framesLivres.isEmpty() ){
            Frame menor = getMaisPerto(framesLivres);
            framesLivres.remove(menor);
            result.add(menor);
        }
        framesLivres = result;
    }
    
    public void juntaRestos(){
        for(int i=1; i<framesLivres.size(); i++){
            Frame anterior = framesLivres.get(i-1);
            Frame atual = framesLivres.get(i);
            if(anterior.posFim==atual.posInicio){
                Frame novo = new Frame(anterior.posInicio, atual.posFim, false, anterior.tamanho+atual.tamanho);
                framesLivres.remove(atual);
                framesLivres.set(framesLivres.indexOf(anterior), novo);
                i--;
            }
        }
    }
    
    public void desalocaProcesso(Processo p, int t){
        tempo.cancel();
        try{
            p.status="Finalizado";
            p.desenho.setVisible(false);
            paneMemoria.getChildren().remove(p.desenho);
            p.desenho = null;
            p.tempoConc=t;
            tabProcessos.refresh();
            
            processosFinalizados.add(p);
            processosAlocados.remove(p);
            framesOcupados.remove(p.frame);
            framesLivres.add(p.frame);
            ordenaFrames();
            juntaRestos();
            cpu-=p.porc;
            atualizaCPU();
        }
        catch(Exception e){
            desalocaProcesso(p, t);
        }
        tempo=new Timer();
        tempo.scheduleAtFixedRate( new TimerTask(){
            @Override
            public void run(){
                rotina();
            }
        },1000, 1000);
    }
    
    public void worstFit(Processo p, int tempo){
        Frame maior = null;
        boolean igual = false;
        int i=0;
        for( i=0 ; i<framesLivres.size(); i++){
            Frame f = framesLivres.get(i);
            if(f.tamFisico>=p.tamFisico){
                maior = f;
                break;
            }
        }
        if(maior!=null){
            for(i=0; i<framesLivres.size(); i++){
                Frame f = framesLivres.get(i);
                p.tamFisico = (f.tamFisico*p.tamanho)/f.tamanho;
                if(f.tamFisico>maior.tamFisico && f.tamFisico>p.tamFisico){
                    maior = f;
                }
                else if(f.tamFisico>=maior.tamFisico && f.tamFisico==p.tamFisico){
                    maior = f;
                    igual = true;
                    break;
                }
            }
            if(!igual){
                int indice = framesLivres.indexOf(maior);
                Frame aloc = new Frame(maior.posInicio, maior.posInicio+p.tamFisico, true, p.tamanho);
                Frame resto = new Frame(aloc.posFim, maior.posFim, false, maior.tamanho-p.tamanho);
                p.frame = aloc;
                framesOcupados.add(aloc);
                framesLivres.set(indice, resto);
                
                p.posInicio = aloc.posInicio;
                p.posFim = aloc.posFim;
                p.tempoAloc = tempo;
                p.status="Executando";
                tabProcessos.refresh();
                processosAlocados.add(p);
                
                desenhaProcesso(p);
                cpu+=p.porc;
                atualizaCPU();
            }
            else{
                framesOcupados.add(maior);
                framesLivres.remove(maior);
                p.posInicio = maior.posInicio;
                p.posFim = maior.posFim;
                p.tempoAloc = tempo;
                p.status="Executando";
                tabProcessos.refresh();
                
                desenhaProcesso(p);
                cpu+=p.porc;
                atualizaCPU();
            }
        }   
    }
    
    public Frame getMenorTam(Frame f, Processo p){
        Frame menor = f;
        for (Frame frame : framesLivres) {
            if(frame.tamFisico<menor.tamFisico && frame.tamFisico>=p.tamFisico){
                menor = frame;
            }
        }
        return menor;
    }

    
    public void bestFit(Processo p, int t){
        Frame best;
        for (Frame frame : framesLivres) {
            if (frame.tamFisico >= p.tamFisico) {
                best = frame;
                best = getMenorTam(best, p);
                if (best.tamanho > p.tamanho) {
                    p.tamFisico = (best.tamFisico * p.tamanho) / best.tamanho;
                    int indice = framesLivres.indexOf(best);
                    Frame aloc = new Frame(best.posInicio, best.posInicio + p.tamFisico, true, p.tamanho);
                    Frame resto = new Frame(aloc.posFim, best.posFim, false, best.tamanho - p.tamanho);
                    p.frame = aloc;
                    framesOcupados.add(aloc);
                    framesLivres.set(indice, resto);

                    p.posInicio = aloc.posInicio;
                    p.posFim = aloc.posFim;
                    p.tempoAloc = t;
                    p.status = "Executando";
                    tabProcessos.refresh();
                    processosAlocados.add(p);

                    desenhaProcesso(p);
                    cpu += p.porc;
                    atualizaCPU();
                    break;
                } else if (best.tamanho == p.tamanho) {
                    framesOcupados.add(best);
                    framesLivres.remove(best);
                    p.posInicio = best.posInicio;
                    p.posFim = best.posFim;
                    p.tempoAloc = t;
                    p.status = "Executando";
                    tabProcessos.refresh();

                    desenhaProcesso(p);
                    cpu += p.porc;
                    atualizaCPU();
                    break;
                }
                break;
            }
        }
        
    }
    
    public void firstFit(Processo p, int tempo){
        for(int i=0; i<framesLivres.size(); i++){
            Frame frame = framesLivres.get(i);
            if(frame.tamanho>p.tamanho){
                p.tamFisico = (frame.tamFisico*p.tamanho)/frame.tamanho;
                int indice = framesLivres.indexOf(frame);
                Frame aloc = new Frame(frame.posInicio, frame.posInicio+p.tamFisico, true, p.tamanho);
                Frame resto = new Frame(aloc.posFim, frame.posFim, false, frame.tamanho-p.tamanho);
                p.frame = aloc;
                framesOcupados.add(aloc);
                framesLivres.set(indice, resto);
                
                p.posInicio = aloc.posInicio;
                p.posFim = aloc.posFim;
                p.tempoAloc = tempo;
                p.status="Executando";
                tabProcessos.refresh();
                processosAlocados.add(p);
                
                desenhaProcesso(p);
                cpu+=p.porc;
                atualizaCPU();
                break;
            }
            else if(frame.tamanho==p.tamanho){
                framesOcupados.add(frame);
                framesLivres.remove(frame);
                p.posInicio = frame.posInicio;
                p.posFim = frame.posFim;
                p.tempoAloc = tempo;
                p.status="Executando";
                tabProcessos.refresh();
                
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
        logMsg("P: "+p.id+" Início: "+p.posInicio+" Fim: "+p.posFim);
    }
    
    public void alocaProcesso(Processo p, int t){
        tempo.cancel();
        switch(metodo){
            case "First Fit":
                firstFit(p, t);
                break;
            case "Best Fit":
                bestFit(p, t);
                break;
            case "Worst Fit":
                worstFit(p, t);
                break;
        }
        tempo=new Timer();
        tempo.scheduleAtFixedRate( new TimerTask(){
            @Override
            public void run(){
                rotina();
            }
        },1000, 1000);
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
        else if(qtd>20){
            logMsg("Máximo de processos é 20");
            return -1;
        }
        else{
            txtLog.clear();
            txtMedia.setText("0");
            cpu=0;
            atualizaCPU();
            ultimo = 0;
            processos.clear();
            processosCriados.clear();
            processosAlocados.clear();
            processosFinalizados.clear();
            framesLivres.clear();
            framesOcupados.clear();
            framesLivres.add(new Frame(0,631, false, memoria));
            btPlay.setDisable(true);
            logMsg(metodo);
            int tamFisico = (631*tamSO)/memoria;
            float porc = ( (float)tamSO/(float)memoria) * 100;
            if(sistOp!=null){
                sistOp.desenho.setVisible(false);
                paneMemoria.getChildren().remove(sistOp.desenho);
            }
            sistOp = new Processo(99, tamSO, 0, 0, "infinito", porc, tamFisico);
            desenhaSO();
            cpu+=sistOp.porc;
            atualizaCPU();
            gerarProcessos();
            txtTempo.setText(Integer.toString(0));
            tempo = new Timer();
            tempo.scheduleAtFixedRate( new TimerTask(){
                @Override
                public void run(){
                    rotina();
                }
            },1000, 1000);
        }
        return 0;
    }
    
}