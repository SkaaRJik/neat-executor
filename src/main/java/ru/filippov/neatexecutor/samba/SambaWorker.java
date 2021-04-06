package ru.filippov.neatexecutor.samba;

import jcifs.smb.*;

import java.io.BufferedInputStream;
import java.io.IOException;

public class SambaWorker {

    // Путь к сетевой папке с которой будем работать
    private final String NETWORK_FOLDER;
    private final NtlmPasswordAuthentication AUTH;

    public SambaWorker(String url, String sharedDirectory, String userName, String password) throws IOException {
        this.NETWORK_FOLDER = String.format("smb://%s/%s/", url, sharedDirectory);

        this.AUTH = new NtlmPasswordAuthentication("", userName, password);

        this.handshake();
    }

    private void handshake() throws IOException {
        SmbFile file = new SmbFile(NETWORK_FOLDER, AUTH);
        if(!file.canRead()){
            throw new IOException(String.format("SAMBA: Can't read the folder: %s", NETWORK_FOLDER));
        }
        /*if(!file.canWrite()){
            throw new IOException(String.format("SAMBA: Can't write into the folder: %s", NETWORK_FOLDER));
        }*/

    }

    // Читаем
    public byte[] readFile(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder(NETWORK_FOLDER);
        stringBuilder.append(fileName);
        // Ресолвим путь назначения в SmbFile
        SmbFile file = new SmbFile(stringBuilder.toString(), AUTH);

        byte[] fileBytes = null;

        if(file.canRead()){
            file.connect();
            BufferedInputStream in = new BufferedInputStream(new SmbFileInputStream(file));
            fileBytes = in.readAllBytes();
        } else {
            throw new IOException(String.format("SAMBA: Can't read the file: %s", stringBuilder.toString()));
        }
        return fileBytes;

    }

    //Записываем
    public boolean writeBytesArray(String fileName, byte[] bytes) throws IOException {
        StringBuilder stringBuilder = new StringBuilder(NETWORK_FOLDER);
        stringBuilder.append(fileName);

        SmbFileOutputStream destFileName = new SmbFileOutputStream(
                new SmbFile(stringBuilder.toString(), AUTH));

        destFileName.write(bytes);
        destFileName.flush();
        /*InputStream streamToWrite = new ByteArrayInputStream(bytes);
        // Ну и копируем все из исходного потока в поток назначения.
        BufferedReader brl = new BufferedReader(
                new InputStreamReader(streamToWrite));
        String b = null;
        while((b=brl.readLine())!=null){
            destFileName.write(b.getBytes());
        }
        destFileName.flush();*/


        return true;
    }

}
