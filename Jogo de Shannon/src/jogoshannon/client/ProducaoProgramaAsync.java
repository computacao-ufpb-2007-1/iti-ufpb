package jogoshannon.client;

import jogoshannon.shared.ConjuntoFrasesStub;
import jogoshannon.shared.ExperimentoStub;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProducaoProgramaAsync {

    void getConjuntosFrases(AsyncCallback<ConjuntoFrasesStub[]> callback);

    void getExperimentos(AsyncCallback<ExperimentoStub[]> callback);

    void putExperimento(ExperimentoStub exp, AsyncCallback<Long> callback);

    void getUploadUrl(String titulo, String autor, String descricao,
            AsyncCallback<String> callback);

}
