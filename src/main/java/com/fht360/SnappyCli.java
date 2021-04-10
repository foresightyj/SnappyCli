package com.fht360;

import com.beust.jcommander.Parameter;
import org.xerial.snappy.Snappy;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class SnappyCli {
    private static void uncompress(Path path, boolean keepOriginal) throws IOException {
        RandomAccessFile randomFile = new RandomAccessFile(path.toString(), "r");
        int fileLength = (int) randomFile.length();
        randomFile.seek(0);
        byte[] bytes = new byte[fileLength];
        randomFile.read(bytes);
        byte[] uncompressed = Snappy.uncompress(bytes);
        String result = new String(uncompressed, "UTF-8");
        String fileName = path.getFileName().toString();
        String newFileName = fileName.replace(".snappy", ".json");
        String outPath = path.toString().replace(fileName, newFileName);
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(outPath), StandardCharsets.UTF_8)) {
            writer.write(result);
        }
        randomFile.close();
        if (!keepOriginal) {
            Files.delete(path);
        }
    }

    public static void main(String[] a) throws IOException {
        SnappyCliArg args = SnappyCliArg.fromArgs(a);
        if (args.directory != null) {
            final int[] count = {0};
            try (Stream<Path> paths = Files.walk(Paths.get(args.directory))) {
                paths.forEach(p -> {
                    String path = p.toString();
                    if (Files.isRegularFile(p) && path.endsWith(".snappy")) {
                        try {
                            uncompress(p, args.keepOriginal);
                            count[0]++;
                        } catch (IOException e) {
                            System.out.printf("Failed to uncompress: %s, because: %s", path, e.getMessage());
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Processed " + count[0] + " snappy files");
        } else if (args.file != null) {
            try {
                uncompress(Paths.get(args.file), args.keepOriginal);
                System.out.println("Processed 1 snappy file");
            } catch (IOException e) {
                System.out.printf("Failed to uncompress file: %s, because: %s", args.file, e.getMessage());
            }
        }
    }

}
