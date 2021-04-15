package com.vandendaelen.nicephore.thread;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.vandendaelen.nicephore.config.NicephoreConfig;
import com.vandendaelen.nicephore.util.Reference;
import com.vandendaelen.nicephore.util.Util;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class InitThread extends Thread {
    private static final File
            DESTINATION = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), String.format("mods%snicephore", File.separator)),
            REFERENCES_JSON = new File(DESTINATION, String.format("%sreferences.json", File.separator)),
            OXIPNG_ZIP = new File(DESTINATION, String.format("%soxipng.zip", File.separator)),
            ECT_ZIP = new File(DESTINATION, String.format("%sect.zip", File.separator));

    @Override
    public void run() {
        NicephoreConfig config = AutoConfig.getConfigHolder(NicephoreConfig.class).getConfig();

        if (config.isOptimisedOutput()) {
            if (Files.exists(DESTINATION.toPath())) {

                {
                    try {
                        Optional<Response> response = getResponse(getJsonReader(REFERENCES_JSON));

                        if (response.isPresent()) {
                            Reference.Command.OXIPNG = response.get().oxipng_command;
                            Reference.Command.ECT = response.get().ect_command;

                            Reference.File.OXIPNG = response.get().oxipng_file;
                            Reference.File.ECT = response.get().ect_file;

                            Reference.Version.OXIPNG = response.get().oxipng_version;
                            Reference.Version.ECT = response.get().ect_version;
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }

                {
                    try {
                        final Optional<Response> response = getResponse(getJsonReader(Reference.DOWNLOADS_URLS, REFERENCES_JSON));

                        if (response.isPresent()) {
                            if (!Reference.Version.OXIPNG.equals(response.get().oxipng_version)) {
                                Reference.Version.OXIPNG = response.get().oxipng_version;
                                downloadAndExtract(response.get().oxipng, OXIPNG_ZIP);
                            }

                            if (!Reference.Version.ECT.equals(response.get().ect_version)) {
                                Reference.Version.ECT = response.get().ect_version;
                                downloadAndExtract(response.get().ect, ECT_ZIP);
                            }
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                freshInstall();
            }
        }

    }

    private static void downloadAndExtract(String url, File zip){
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(zip)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            unzip(zip.getAbsolutePath(), DESTINATION.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void unzip(String zipFilePath, String destDir) {
        final File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();
        final FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                final FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Optional<Response> getResponse(final JsonReader reader) {
        final Gson gson = new Gson();
        final Type collectionType = new TypeToken<Collection<Response>>() {}.getType();
        final Collection<Response> responses = gson.fromJson(reader, collectionType);
        final Optional<Response> response = responses.stream().filter(response1 -> response1.platform.equals(Util.getOS().name())).findFirst();
        return response;
    }

    private JsonReader getJsonReader(String URL, final File file) throws IOException {
        FileUtils.copyURLToFile(new URL(URL), file);
        return getJsonReader(file);
    }

    private JsonReader getJsonReader(final File file) throws FileNotFoundException {
        return new JsonReader(new FileReader(file));
    }

    private final void freshInstall() {
        try {
            Files.createDirectory(DESTINATION.toPath());
            final Optional<Response> response = getResponse(getJsonReader(Reference.DOWNLOADS_URLS, REFERENCES_JSON));

            Reference.Command.OXIPNG = response.get().oxipng_command;
            Reference.Command.ECT = response.get().ect_command;

            Reference.File.OXIPNG = response.get().oxipng_file;
            Reference.File.ECT = response.get().ect_file;

            Reference.Version.OXIPNG = response.get().oxipng_version;
            Reference.Version.ECT = response.get().ect_version;

            downloadAndExtract(response.get().oxipng, OXIPNG_ZIP);
            downloadAndExtract(response.get().ect, ECT_ZIP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Response{
        String platform;
        String oxipng, oxipng_file, oxipng_command, oxipng_version;
        String ect, ect_file, ect_command, ect_version;
    }
}
