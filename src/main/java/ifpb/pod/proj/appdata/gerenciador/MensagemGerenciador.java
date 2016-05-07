/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ifpb.pod.proj.appdata.gerenciador;

import ifpb.pod.proj.appdata.repositorio.GDriveRepositorio;
import ifpb.pod.proj.appdata.repositorio.BibliotecaArquivos;
import ifpb.pod.proj.appdata.repositorio.DBoxRepositorio;
import ifpb.pod.proj.appdata.repositorio.Repositorio;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Serializer;

/**
 *
 * @author José Marcondes do Nascimento Junior
 */
public class MensagemGerenciador {

    public String cadastrarMensagem(String usrId, String dataTime, String grupoId, String conteudo) throws Exception {
        Repositorio repositorio = new GDriveRepositorio();
        UUID id = UUID.randomUUID();

        Builder builder = new Builder();
        InputStream is = new FileInputStream(repositorio.downloadFile(BibliotecaArquivos.MENSAGENS));

        Document doc = builder.build(is);

        List<Map<String, String>> list = listarMensagens();

        HashMap<String, String> map = new HashMap();
        map.put("id", id.toString());
        map.put("usuarioId", usrId);
        map.put("dataTime", dataTime);
        map.put("grupoId", grupoId);
        map.put("conteudo", conteudo);

        list.add(map);

        escreverMensagem(list);

        System.out.println(list);
        return id.toString();
    }

    public void cadastrarMensagemUsuario(String msgId, List<Map<String, String>> usrs, String status) throws Exception {
        UUID id = UUID.randomUUID();
        List<Map<String, String>> list = listarMensagensUsuario();

        for (Map<String, String> usr : usrs) {
            HashMap<String, String> map = new HashMap();
            map.put("id", id.toString());
            map.put("mensagemId", msgId);
            map.put("usuarioId",usr.get("email"));
            map.put("status", status);
            list.add(map);
        }

        escreverMensagemUsuario(list);

        System.out.println(list);
    }

    public List<Map<String, String>> listarMensagens() throws Exception {
        Builder builder = new Builder();
        Repositorio repositorio = new GDriveRepositorio();
        InputStream is = new FileInputStream(repositorio.downloadFile(BibliotecaArquivos.MENSAGENS));
        Document doc = builder.build(is);
        Element root = doc.getRootElement();
        Elements childs = root.getChildElements("mensagem");
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < childs.size(); i++) {
            Element atual = childs.get(i);
            HashMap<String, String> map = new HashMap<>();
            String idValue = atual.getChild(1).getValue();
            String userId = atual.getChild(3).getValue();
            String dataTime = atual.getChild(5).getValue();
            String grupoId = atual.getChild(7).getValue();
            String conteudo = atual.getChild(9).getValue();
            map.put("id", idValue);
            map.put("usuarioId", userId);
            map.put("dataTime", dataTime);
            map.put("grupoId", grupoId);
            map.put("conteudo", conteudo);
            list.add(map);
        }
        return list;
    }

    public void escreverMensagem(List<Map<String, String>> list) throws Exception {
        Repositorio repositorio = new GDriveRepositorio();
        Element root = new Element("mensagens");
        for (Map<String, String> map : list) {
            Element mensagemEl = new Element("mensagem");
            Element idEl = new Element("id");
            idEl.appendChild(map.get("id"));
            Element usuarioIdEl = new Element("usuarioId");
            usuarioIdEl.appendChild(map.get("usuarioId"));
            Element dataTimeEl = new Element("dataTime");
            dataTimeEl.appendChild(map.get("dataTime"));
            Element grupoIdEl = new Element("grupoId");
            grupoIdEl.appendChild(map.get("grupoId"));
            Element contentEl = new Element("conteudo");
            contentEl.appendChild(map.get("conteudo"));
            mensagemEl.appendChild(idEl);
            mensagemEl.appendChild(usuarioIdEl);
            mensagemEl.appendChild(dataTimeEl);
            mensagemEl.appendChild(grupoIdEl);
            mensagemEl.appendChild(contentEl);
            root.appendChild(mensagemEl);
        }

        Document doc = new Document(root);

        File usrFile = new File(this.getClass().getResource("/uploads/mensagens.xml").getFile());

        try {
            Serializer serializer = new Serializer(new FileOutputStream(usrFile), "UTF-8");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.write(doc);
        } catch (IOException ex) {
            System.err.println(ex);
        }

        repositorio.updateFile(usrFile, BibliotecaArquivos.MENSAGENS);
    }

    public void escreverMensagemUsuario(List<Map<String, String>> list) throws Exception {
        Repositorio repositorio = new DBoxRepositorio();

        Element root = new Element("mensagensUsuario");
        for (Map<String, String> map : list) {
            Element mensagemUsuarioEl = new Element("mensagemUsuario");

            Element idEl = new Element("id");
            idEl.appendChild(map.get("id"));

            Element msgId = new Element("mensagemId");
            msgId.appendChild(map.get("mensagemId"));

            Element usuarioIdEl = new Element("usuarioId");
            usuarioIdEl.appendChild(map.get("usuarioId"));

            Element statusEl = new Element("status");
            statusEl.appendChild(map.get("status"));

            mensagemUsuarioEl.appendChild(idEl);
            mensagemUsuarioEl.appendChild(msgId);
            mensagemUsuarioEl.appendChild(usuarioIdEl);
            mensagemUsuarioEl.appendChild(statusEl);

            root.appendChild(mensagemUsuarioEl);
        }

        Document doc = new Document(root);

        File usrFile = new File(this.getClass().getResource("/uploads/mensagem_usuario.xml").getFile());

        try {
            Serializer serializer = new Serializer(new FileOutputStream(usrFile), "UTF-8");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.write(doc);
        } catch (IOException ex) {
            System.err.println(ex);
        }

        repositorio.updateFile(usrFile, BibliotecaArquivos.MENSAGEM_USUARIO);
    }

    public List<Map<String, String>> listarMensagensUsuario() throws Exception {
        Builder builder = new Builder();
        Repositorio repositorio = new DBoxRepositorio();
        InputStream is = new FileInputStream(repositorio.downloadFile(BibliotecaArquivos.MENSAGEM_USUARIO));
        Document doc = builder.build(is);

        Element root = doc.getRootElement();
        Elements childs = root.getChildElements("mensagemUsuario");
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < childs.size(); i++) {
            Element atual = childs.get(i);
            HashMap<String, String> map = new HashMap<>();
            String id = atual.getChild(1).getValue();
            String mensagemId = atual.getChild(3).getValue();
            String usuarioId = atual.getChild(5).getValue();
            String status = atual.getChild(7).getValue();
            map.put("id", id);
            map.put("mensagemId", mensagemId);
            map.put("usuarioId", usuarioId);
            map.put("status", status);
            list.add(map);
        }
        return list;
    }

    public List<Map<String, String>> listarMensagensPendentes() throws Exception {
        List<Map<String, String>> all = this.listarMensagensUsuario();
        all.removeIf((Map<String, String> t) -> {
            return !t.get("status").equals("pendente");
        });
        return all;
    }

    public void estadoNotificado(String id) throws Exception {
        List<Map<String, String>> all = listarMensagensUsuario();
        for (Map<String, String> map : all) {
            if (map.get("id").equals(id)) {
                map.put("status", "notificado");
            }
        }
        escreverMensagemUsuario(all);
    }
}
