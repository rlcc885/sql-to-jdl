package org.blackdread.sqltojava.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.util.ResourceUtils;

@ConstructorBinding
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private static final Logger log = LoggerFactory.getLogger(ApplicationProperties.class);

    /**
     * Database name to export to JDL
     */
    private final String databaseToExport;
    private final List<String> databaseObjectPrefix;
    private final Boolean addTableNameJdl;
    private final UndefinedJdlTypeHandlingEnum undefinedTypeHandling;
    private final List<String> ignoredTableNames;

    private final Export export;

    private final List<String> reservedList;

    @SuppressWarnings("unchecked")
    public ApplicationProperties(
        final String databaseToExport,
        List<String> databaseObjectPrefix,
        Boolean addTableNameJdl,
        String undefinedTypeHandling,
        final List<String> ignoredTableNames,
        final Export export,
        final String reservedKeywords
    ) {
        log.info("Loading ApplicationProperties...");
        this.databaseToExport = databaseToExport;
        this.databaseObjectPrefix = databaseObjectPrefix;
        this.undefinedTypeHandling = UndefinedJdlTypeHandlingEnum.valueOf(undefinedTypeHandling);
        this.addTableNameJdl = Optional.of(addTableNameJdl).orElse(false);
        this.ignoredTableNames = ignoredTableNames;
        this.export = export;
        this.reservedList =
            JsonParserFactory
                .getJsonParser()
                .parseMap(keywordsAsJson(reservedKeywords))
                .values()
                .stream()
                .map(obj -> (List<String>) obj)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public String getDatabaseToExport() {
        return databaseToExport;
    }

    public List<String> getIgnoredTableNames() {
        return ignoredTableNames;
    }

    public Export getExport() {
        return export;
    }

    public List<String> getReservedList() {
        return reservedList;
    }

    public Boolean getAddTableNameJdl() {
        return addTableNameJdl;
    }

    public UndefinedJdlTypeHandlingEnum getUndefinedTypeHandling() {
        return undefinedTypeHandling;
    }

    public List<String> getDatabaseObjectPrefix() {
        return databaseObjectPrefix;
    }

    public static class Export {

        private final Path path;

        private final String type;

        public Export(final Path path, final String type) {
            this.path = path;
            this.type = type;
        }

        public Path getPath() {
            return path;
        }

        public String getType() {
            return type;
        }
    }

    private String keywordsAsJson(String file) {
        try {
            Path path = ResourceUtils.getFile(file).toPath();
            return String.join(" ", Files.readAllLines(path));
        } catch (IOException e) {
            return "{\"key\": []}";
        }
    }
}
