package at.ac.tuwien.sepm.musicplayer.presentation;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.dto.PlaylistDTO;
import java.util.Comparator;

/**
 * Created by marjaneh.
 */

public class AlphanumComparator implements Comparator<PlaylistDTO>{

    public int compare(PlaylistDTO p1, PlaylistDTO p2) {
        String name1 = p1.getName().toLowerCase();
        String name2 = p2.getName().toLowerCase();

        String[] p1Parts = name1.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        String[] p2Parts = name2.toLowerCase().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        int i = 0;

        while(i < p1Parts.length && i < p2Parts.length){

            if(p1Parts[i].compareTo(p2Parts[i]) == 0){
               i++;
            }else{
                try{
                    int intP1 = Integer.parseInt(p1Parts[i]);
                    int intP2 = Integer.parseInt(p2Parts[i]);

                    int diff = intP1 - intP2;
                    if(diff == 0){
                        i++;
                    }else{
                        return diff;
                    }
                }catch(Exception ex){
                    return name1.compareTo(name2);
                }
            }
        }

        if(name1.length() < name2.length()){
            return -1;
        }else if(name1.length() > name2.length()){
            return 1;
        }else{
            return 0;
        }
    }
}
