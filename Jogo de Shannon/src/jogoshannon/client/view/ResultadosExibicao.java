package jogoshannon.client.view;

import jogoshannon.client.presenter.ResultadosApresentador;
import jogoshannon.client.util.ConjuntoUsuarios;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.LineChart;

public class ResultadosExibicao extends Composite 
implements ResultadosApresentador.Exibicao {

    private static ResultadosExibicaoUiBinder uiBinder = GWT
            .create(ResultadosExibicaoUiBinder.class);

    interface ResultadosExibicaoUiBinder extends
            UiBinder<Widget, ResultadosExibicao> {
    }
    
    interface TabelaCss extends CssResource {
        String tituloTabela();
        String corpoTabela();
        String primeiraColuna();
        String zeroZero();
    }

    private static final int LINHA_TITULO = 0;
    private static final int OFFSET_LINHA = 1;

    private static final int LEGENDA_COLUNA = 0;
    private static final int OFFSET_COLUNA = 1;

    private static final int ENTROPIA_MIN_END_OFFSET = 1;
    private static final int ENTROPIA_MAX_END_OFFSET = 2;
    
    @UiField 
    TabelaCss style; 
    
    @UiField
    protected FlowPanel usuarios;
    
    @UiField
    protected ListBox listaExperimentos;
    
    /*
    @UiField
    protected TextBox entradaId;
    
    @UiField
    protected Button botaoAdicionar;
    */
    
    @UiField
    protected FlexTable tabelaTentativas;
    
    @UiField
    protected Image carregando;
    
    @UiField
    protected SimplePanel painelGrafico;
    
    private ConjuntoUsuarios conjUsuarios;
    private int maxLinha;
    
    private double entropiaMax[];
    private double entropiaMin[];
    
    public ResultadosExibicao (SimpleEventBus eventos) {
        initWidget(uiBinder.createAndBindUi(this));
        conjUsuarios = new ConjuntoUsuarios(eventos);
        listaExperimentos.addItem("Selecione o experimento");
        reset();
    }
    
    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void adicionarId(long id) {
        if (id != 0) {
            UsuarioWidget novo = new UsuarioWidget(id);
            novo.ativaBotaoRemover(true);
            boolean foi = conjUsuarios.adiciona(novo);

            if (foi) {
                usuarios.add(novo);
            }

        }
        
    }

    @Override
    public void removerId(long id) {
        UsuarioWidget cara = conjUsuarios.remover(id);
        if (cara != null) {
            usuarios.remove(cara);
        }
    }

    @Override
    public void setCarregandoId(long id, boolean carregando) {
        UsuarioWidget cara = conjUsuarios.get(id);
        if (cara != null) {
            cara.setCarregando(carregando);
        }
    }

    private String doubleToString(double numero) {
        return NumberFormat.getFormat("0.000").format(numero);
    }
    
    private void legendar (int linha, String texto) {
        Label legenda = new Label(texto);
        legenda.setStylePrimaryName(style.primeiraColuna());
        legenda.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        tabelaTentativas.setWidget(OFFSET_LINHA + linha, LEGENDA_COLUNA,
                legenda);
    }

    private static double[] copyArray (double[] orig) {
        double result[] = new double[orig.length];
        for (int i = 0; i < orig.length; ++i) {
            result[i] = orig[i];
        }
        return result;
    }
    
    @Override
    public void atualizaEntropiaMinima(int linha, double[] dados) {
        linha += ENTROPIA_MIN_END_OFFSET;
        legendar(linha, "Entropia Mínima");
        for (int i = 0; i < dados.length; ++i) {
            Label texto = new Label(doubleToString(dados[i]));
            texto.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            tabelaTentativas.setWidget(OFFSET_LINHA + linha, OFFSET_COLUNA + i,
                    texto);
        }
        entropiaMin = copyArray(dados);
    }

    @Override
    public void atualizaEntropiaMaxima(int linha, double[] dados) {
        linha += ENTROPIA_MAX_END_OFFSET;
        legendar(linha, "Entropia Máxima");
        for (int i = 0; i < dados.length; ++i) {
            Label texto = new Label(doubleToString(dados[i]));
            texto.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            tabelaTentativas.setWidget(OFFSET_LINHA + linha, OFFSET_COLUNA + i,
                    texto);
        }
        entropiaMax = copyArray(dados);
    }

    @Override
    public void atualizarLinha(int linha, int[] dados) {
        maxLinha = Math.max(maxLinha, linha);
        legendar(linha, ""+(linha+1));
        
        for (int i = 0; i < dados.length; ++i) {
            Label texto = new Label(dados[i] == 0 ? "" : "" + dados[i]);
            texto.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            tabelaTentativas.setWidget(OFFSET_LINHA + linha, OFFSET_COLUNA + i,
                    texto);
        }
    }

    @Override
    public void setTitulosTabela(String[] titulos) {
        for (int i = 0; i < titulos.length; ++i) {
            tabelaTentativas.setText(LINHA_TITULO, OFFSET_COLUNA + i,
                    titulos[i]);
        }
    }
    
    @Override
    public void adicionarExperimento(String nome) {
        listaExperimentos.addItem(nome);
    }

    @Override
    public HasChangeHandlers getListaExperimentos() {
        return listaExperimentos;
    }
    
    @Override
    public void reset() {
        for (Widget w : conjUsuarios.getValores()) {
            usuarios.remove(w);
        }
        conjUsuarios.clear();
        tabelaTentativas.removeAllRows();
        tabelaTentativas.getRowFormatter().addStyleName(0, style.tituloTabela());
        tabelaTentativas.addStyleName(style.corpoTabela());
        tabelaTentativas.getCellFormatter().setStyleName(0, 0, style.zeroZero());
    }

    @Override
    public int getExperimentoSelecionado() {
        return listaExperimentos.getSelectedIndex() - 1;
    }
    
    @Override
    public void setExperimentosCarregando(boolean estaCarregando) {
        carregando.setVisible(estaCarregando);
    }
    
    @Override
    public void plotar () {
        VisualizationUtils.loadVisualizationApi(new Runnable() {
            @Override
            public void run() {
                plotarImpl();
            }
        } , LineChart.PACKAGE);
    }
    
    private void plotarImpl () {
        LineChart.Options opt = LineChart.Options.create();
        opt.setWidth(600);
        opt.setHeight(450);
        opt.setSmoothLine(true);
        opt.setLegend(LegendPosition.RIGHT);
        
        DataTable dados = DataTable.create();
        dados.addColumn(ColumnType.STRING, "Letras exibidas");
        dados.addColumn(ColumnType.NUMBER, "Entropia máxima");
        dados.addColumn(ColumnType.NUMBER, "Entropia Mínima");
        dados.addRows(entropiaMin.length);
        for (int i = 0; i < entropiaMin.length; ++i) {
            dados.setValue(i, 0, tabelaTentativas.getText(LINHA_TITULO, OFFSET_COLUNA + i));
            dados.setValue(i, 1, entropiaMax[i]);
            dados.setValue(i, 2, entropiaMin[i]);
        }
        
        LineChart grafico = new LineChart(dados, opt);
        painelGrafico.setWidget(grafico);
        
    }
    
    

}