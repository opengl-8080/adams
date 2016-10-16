package adams.infrastructure.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class SequenceNumberGenerator {
    private static final File BASE_DIR = new File("./config/sequences");
    private final File file;
    private long counter = 0;

    public SequenceNumberGenerator(String name) {
        if (!BASE_DIR.exists()) {
            BASE_DIR.mkdirs();
        }

        this.file = new File(BASE_DIR, name + ".txt");

        if (!this.file.exists()) {
            this.save();
        }

        try {
            String count = Files.readAllLines(this.file.toPath()).get(0);
            this.counter = Long.parseLong(count);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    synchronized public long nextval() {
        this.counter++;
        this.save();
        return this.counter;
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(String.valueOf(this.counter));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
