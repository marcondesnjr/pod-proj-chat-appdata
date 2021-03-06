package ifpb.pod.proj.appdata.transaction;

import ifpb.pod.proj.appdata.gerenciador.GrupoGerenciador;
import ifpb.pod.proj.appdata.gerenciador.MensagemGerenciador;
import ifpb.pod.proj.appdata.gerenciador.MensagemUsuarioGerenciador;
import ifpb.pod.proj.appdata.gerenciador.TXTNotificacao;
import ifpb.pod.proj.appdata.gerenciador.UsuarioGerenciador;
import java.util.List;
import java.util.Map;

/**
 *
 * @author José Marcondes do Nascimento Junior
 */
public class Operador {

    public Map<String, String> usuarioByEmailSenha(String email, String senha) throws Exception {
        TransactionManager txm = new TxManager();
        try {
            txm.prepareAll();
            return new UsuarioGerenciador().usuarioByEmailSenha(email, senha);
        } catch (Exception ex) {
            txm.rollbackAll();
            throw ex;
        }
    }

    public void cadastrarUsuario(String nome, String email, String senha) throws Exception {
        TransactionManager txm = new TxManager();
        try {
            txm.prepareAll();
            new UsuarioGerenciador().cadastrarUsuario(nome, email, senha);
            txm.commitAll();
        } catch (Exception ex) {
            txm.rollbackAll();
            throw ex;
        }
    }

    public boolean escreverMensagem(String usrId, String dateTime, String grupoId, String conteudo) throws Exception {

        TransactionManager txm = new TxManager();
        try {
            txm.prepareAll();
            String id = new MensagemGerenciador().cadastrarMensagem(usrId, dateTime, grupoId, conteudo);
            List<Map<String, String>> list = new UsuarioGerenciador().listaUsuarioPorGrupo(grupoId);
            list.removeIf((Map<String, String> t) -> {
                return t.get("email").equals(usrId);
            });
            new MensagemUsuarioGerenciador().cadastrarMensagemUsuario(id, list, "pendente");
            txm.commitAll();
            return true;
        } catch (Exception ex) {
            txm.rollbackAll();
            throw ex;
        }
    }

    public void entrarGrupo(String usrEmail, String groupId) throws Exception {
        TransactionManager txm = new TxManager();
        try {
            txm.prepareAll();
            GrupoGerenciador grupoGerenciador = new GrupoGerenciador();
            Map<String, String> grupo = grupoGerenciador.grupoById(groupId);
            if (grupo != null) {
                grupoGerenciador.entrarGrupo(usrEmail, groupId);
            }
            txm.commitAll();
        } catch (Exception ex) {
            txm.rollbackAll();
            throw ex;
        }
    }

    public List<Map<String, String>> listarMensagensPendentes() throws Exception {
        TransactionManager txm = new TxManager();
        try {
            txm.prepareAll();
            MensagemUsuarioGerenciador mensagemGerenciador = new MensagemUsuarioGerenciador();
            List<Map<String, String>> resp = mensagemGerenciador.listarMensagensPendentes();
            txm.commitAll();
            return resp;
        } catch (Exception ex) {
            txm.rollbackAll();
            throw ex;
        }
    }

    public List<Map<String, String>> listarMensagens() throws Exception {
        TransactionManager txm = new TxManager();
        try {
            txm.prepareAll();
            MensagemGerenciador mensagemGerenciador = new MensagemGerenciador();

            List<Map<String, String>> resp = mensagemGerenciador.listarMensagens();
            txm.commitAll();
            return resp;
        } catch (Exception ex) {
            txm.rollbackAll();
            throw ex;
        }
    }

    public String criarNotificacao(String text) throws Exception {
        return new TXTNotificacao().criarNotificacao(text);

    }

    public void alterarEstadoNotificado(String id) throws Exception {
        TransactionManager txm = new TxManager();
        try {
            txm.prepareAll();
            new MensagemUsuarioGerenciador().estadoNotificado(id);
            txm.commitAll();
        } catch (Exception ex) {
            txm.rollbackAll();
            throw ex;
        }
    }

    public String getNotificacao(String token) throws Exception {
        String resp = new TXTNotificacao().recuperarByToken(token);
        return resp;
    }

    public void excluirUsuario(String email) throws Exception {
        TransactionManager txm = new TxManager();
        try {
            txm.prepareAll();
            new UsuarioGerenciador().excluirUsuario(email);
            new GrupoGerenciador().excluirTodosGrupo(email);
            new MensagemUsuarioGerenciador().excluirMsgParaUsuario(email);
            txm.commitAll();
        } catch (Exception ex) {
            txm.rollbackAll();
            throw ex;
        }
    }

}
