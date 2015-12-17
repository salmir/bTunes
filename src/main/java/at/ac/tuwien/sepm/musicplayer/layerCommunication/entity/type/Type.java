package at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Genre;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.daoEntity.*;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.InfoOwner;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.Info;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by Lena Lenz.
 */
public class Type {
    private static final Set<InfoType> infoTypesMutable = new HashSet<>();
    public static final SongType SONG = new SongType();
    public static final AlbumType ALBUM = new AlbumType();
    public static final ArtistType ARTIST = new ArtistType();
    public static final PlaylistType PLAYLIST = new PlaylistType();
    public static final GenreType GENRE = new GenreType();
    public static final Set<InfoType> INFO_TYPES;
    public static final StatisticType STATISTIC = new StatisticType();


    static{
        INFO_TYPES = Collections.unmodifiableSet(infoTypesMutable);
    }

    public static class InfoTypeImpl<A extends Info> implements InfoType<A>{
        private String name;

        public InfoTypeImpl(String name) {
            this.name = name;
            infoTypesMutable.add(this);
        }

        @Override
        public String getName(){
            return name;
        }
    }

    public static class OwnerTypeImpl<A extends InfoOwner> implements OwnerType<A>{
        private String name;

        public OwnerTypeImpl(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class EntityTypeImpl<A extends Entity> implements EntityType<A>{
        private String name;

        public EntityTypeImpl(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

//    public static class OwnerInfoTypeImpl<A extends Info, B extends InfoOwner> implements OwnerInfoType<A, B> {
//        private String name;
//
//        public OwnerInfoTypeImpl(String name) {
//            this.name = name;
//        }
//
//        @Override
//        public String getName(){
//            return name;
//        }
//    }

    public static class SongType extends OwnerTypeImpl<Song>{
        private SongType(){super("Song");}

    }

    public static class AlbumType extends InfoTypeImpl<Album>{
        private AlbumType(){super("Album");}
    }

    public static class ArtistType extends InfoTypeImpl<Artist>{
        private ArtistType(){super("Artist");}
    }

    public static class PlaylistType extends EntityTypeImpl<Playlist>{
        private PlaylistType(){super("Playlist");}

    }

    public static class GenreType extends InfoTypeImpl<Genre>{
        private GenreType(){super("Genre");}
    }

    public static class StatisticType extends EntityTypeImpl<Statistic> {
        public StatisticType() {
            super("Statistic");
        }
    }
}
