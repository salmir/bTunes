package at.ac.tuwien.sepm.musicplayer.service.retriever;

import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import at.ac.tuwien.sepm.musicplayer.exceptions.ValidationException;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.Song;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.service.SongService;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by marjaneh.
 */
public class SongLyricsRetrieverImpl implements SongLyricsRetriever {
    private static Logger logger = Logger.getLogger(SongLyricsRetrieverImpl.class);

    @Override
    public void retrieveInfo(Song song, SongService service) throws ServiceException {
        String lyrics = retrieveLyrics(song);
        try {
            service.persistLyrics(song.getId(), lyrics);
            song.setLyrics(lyrics);
        } catch (ValidationException e) {
            logger.error("invalid lyrics");
            throw new ServiceException("invalid lyrics");
        }
    }

    private String retrieveLyrics(Song song) {
       if (song == null) {
           return "";
       }

        String artistName = song.get(Type.ARTIST).getName().toLowerCase();
        artistName = prepareName(artistName);

        String songName = song.getName().toLowerCase();
        songName = prepareName(songName);

        String url = "http://www.azlyrics.com/lyrics/" + artistName + "/" + songName + ".html";

        String lyrics = "";
        try {
            URL newUrl = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(newUrl.openStream()));
            String line;

            while (!(reader.readLine()).contains("<!-- Usage of azlyrics.com")) {
            }

            while (!(line = reader.readLine()).equals("</div>")) {
                lyrics += line;
            }

            reader.close();
        } catch (MalformedURLException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
        String parsedLyrics = HtmlParser.parseHtmlToLyrics(lyrics);
        return parsedLyrics;
    }

    private String prepareName(String name) {
        name = name.replace(" ", "");
        name = name.replace("-", "");
        name = name.replace("'", "");

        if (name.contains("[official")) {
            int startIndex = name.indexOf("[");
            name = name.substring(0, startIndex);
        }
        if (name.contains("(official")) {
            int startIndex = name.indexOf("(");
            name = name.substring(0, startIndex);
        }
        if (name.contains("ft.")) {
            int startIndex = name.indexOf("ft.");
            name = name.substring(0, startIndex);
        }
        if (name.contains("feat.")) {
            int startIndex = name.indexOf("feat.");
            name = name.substring(0, startIndex);
        }
        name = name.replace(".", "");
        name = name.replace("(", "");
        name = name.replace(")", "");
        name = name.replace("[", "");
        name = name.replace("]", "");

        return name;
    }
}
