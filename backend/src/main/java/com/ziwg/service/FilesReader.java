package com.ziwg.service;

import com.ziwg.model.db.Section;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//todo refactor this class
@Log4j2
@Service
public class FilesReader {

    public static final String ZIP_FILE_EXTENSION = ".zip";
    private final String zipsPath;

    @Autowired
    public FilesReader(@Value("${ziplocation}") String zipsPath) {
        this.zipsPath = zipsPath;
    }

    public Boolean unzipResult(String zipPath) {
        try (ZipFile file = new ZipFile(zipPath + ZIP_FILE_EXTENSION)) {
            log.info("Unzipping file: " + zipPath + ZIP_FILE_EXTENSION);
            FileSystem fileSystem = FileSystems.getDefault();
            //Get file entries
            Enumeration<? extends ZipEntry> entries = file.entries();

            //We will unzip files in this folder
            String uncompressedDirectory = zipPath + "/";
            Files.createDirectory(fileSystem.getPath(uncompressedDirectory));

            //Iterate over entries
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                //If directory then create a new directory in uncompressed folder
                if (entry.isDirectory()) {
                    log.info("Creating Directory:" + uncompressedDirectory + entry.getName());
                    Files.createDirectories(fileSystem.getPath(uncompressedDirectory + entry.getName()));
                }
                //Else create the file
                else {
                    InputStream is = file.getInputStream(entry);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    String uncompressedFileName = uncompressedDirectory + entry.getName();
                    Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
                    Files.createFile(uncompressedFilePath);
                    FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
                    while (bis.available() > 0) {
                        fileOutput.write(bis.read());
                    }
                    fileOutput.close();
                }
            }
        } catch (IOException e) {
            log.error(e);
        }
        return true;
    }

    public List<Section> getDataFromZip(String zipPath) {
        unzipResult(zipPath);
        List<Section> sections = Collections.emptyList();
        try (Stream<Path> paths = Files.walk(Paths.get(zipPath))) {
            sections = getSectionsFromPaths(paths);
        } catch (IOException | IllegalStateException e) {
            log.error("Error while trying to read the file.", e);
        } finally {
            deleteFile(new File(zipPath + ZIP_FILE_EXTENSION));
            deleteFiles(zipPath);
        }
        return sections;
    }

    public void deleteFiles(String directoryToBeDeleted) {
        File contentDirectory = new File(directoryToBeDeleted);
        File[] allContents = contentDirectory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteFiles(file.getPath());
            }
        }
        deleteFile(contentDirectory);
    }

    private void deleteFile(File contentDirectory) {
        log.info("Deleting file: " + contentDirectory.getPath());
        contentDirectory.delete();
    }

    public String saveZip(String fileHandler, byte[] zipAsBytes) throws IOException {
        String zipFilePath = zipsPath + fileHandler;
        Files.write(Paths.get(zipFilePath + ZIP_FILE_EXTENSION), zipAsBytes);
        return zipFilePath;
    }

    private List<Section> getSectionsFromPaths(Stream<Path> paths) {
        return paths.filter(Files::isRegularFile)
                .sorted()
                .map(this::readFile)
                .map(XmlParser::convertXmlToSectionObject)
                .collect(Collectors.toList());
    }

    private String readFile(Path path) {
        try {
            log.info("Reading content of file: " + path);
            return Files.readString(path);
        } catch (IOException | InvalidParameterException e) {
            log.error("Exception during reading file", e);
            return null;
        }
    }
}
