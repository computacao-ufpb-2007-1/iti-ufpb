package jogoshannon.client;

import com.google.gwt.event.shared.HandlerManager;

import jogoshannon.client.event.TentativaEvent;
import jogoshannon.shared.Frase;

public class ModeloFrase {
	
	private String frase;
	private int ponteiroLetra;
	private int contagemDesafios;
	private int tentativas[];
	
	public ModeloFrase(String frase) {
		super();
		this.frase = frase;
		this.ponteiroLetra = 0;
		this.contagemDesafios = Frase.QUANTIDADE_LETRAS.length;
		this.tentativas = new int[contagemDesafios];
	}

	public void atualiza (char tentativa, HandlerManager eventos) {
		
		if (acabou()) {
			return; //nao ha o que fazer.
		}
		
		++tentativas[ponteiroLetra];
		int indice = Frase.QUANTIDADE_LETRAS[ponteiroLetra];
		char esperado = frase.charAt(indice);
		
		
		if (esperado == tentativa) {
			++ponteiroLetra;
			eventos.fireEvent(new TentativaEvent(true));
		} else {
			eventos.fireEvent(new TentativaEvent(true));
		}
		
	}
	
	public String getFraseParcial () {
		int indice = Frase.QUANTIDADE_LETRAS[ponteiroLetra];
		return frase.substring(0, indice);
	}
	
	public boolean acabou () {
		return ponteiroLetra >= contagemDesafios;
	}
	
}
