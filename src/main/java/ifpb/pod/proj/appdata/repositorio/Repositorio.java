/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ifpb.pod.proj.appdata.repositorio;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author José Marcondes do Nascimento Junior
 */
public interface Repositorio {

    void updateFile(File fl, BibliotecaArquivos b) throws Exception;
    File downloadFile(BibliotecaArquivos b) throws Exception;
}
