package com.whatever.tunester.database.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = TrackMeta.Deserializer.class)
public class TrackMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String filename;

    private Integer size;

    private Integer bitRate;

    private Double duration;

    private String artist;

    private String title;

    private String album;

    private String genre;

    private Integer track;

    private String date;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private TrackMetaComment trackMetaComment;

    protected static class Deserializer extends StdDeserializer<TrackMeta> {
        public Deserializer() {
            this(null);
        }

        public Deserializer(Class<?> valueClass) {
            super(valueClass);
        }

        @Override
        public TrackMeta deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonNode node = parser.getCodec().readTree(parser);
            JsonNode format = node.get("format");

            TrackMetaBuilder trackMetaBuilder = TrackMeta.builder();

            if (format == null) {
                return trackMetaBuilder.build();
            }

            String absoluteFilename = getText(format, "filename");

            trackMetaBuilder
                .filename(absoluteFilename != null ? Path.of(absoluteFilename).getFileName().toString() : null)
                .size(parseInt(getText(format, "size")))
                .bitRate(parseInt(getText(format, "bit_rate")))
                .duration(parseDouble(getText(format, "duration")));

            JsonNode tags = format.get("tags");

            if (tags == null) {
                return trackMetaBuilder.build();
            }

            trackMetaBuilder
                .artist(getText(tags, "artist"))
                .title(getText(tags, "title"))
                .album(getText(tags, "album"))
                .genre(getText(tags, "genre"))
                .track(parseInt(getText(tags, "track")))
                .date(getText(tags, "date"));

            String commentJson = getText(tags, "comment");

            try {
                trackMetaBuilder.trackMetaComment(new ObjectMapper().readValue(commentJson, TrackMetaComment.class));
            } catch (IllegalArgumentException | MismatchedInputException | JsonParseException ignored) {
                // ignored
            } catch (Exception e) {
                e.printStackTrace();
            }

            return trackMetaBuilder.build();
        }

        private String getText(JsonNode node, String field) {
            try {
                return node.get(field).asText();
            } catch (NullPointerException e) {
                return null;
            }
        }

        private Integer parseInt(String value) {
            try {
                return Integer.parseInt(value);
            } catch (NullPointerException | NumberFormatException e) {
                return null;
            }
        }

        private Double parseDouble(String value) {
            try {
                return Double.parseDouble(value);
            } catch (NullPointerException | NumberFormatException e) {
                return null;
            }
        }
    }
}
