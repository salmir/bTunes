package at.ac.tuwien.sepm.musicplayer.layerCommunication.util;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.Entity;

/**
 * Created by Lena Lenz.
 */
public class EntityFormatter {
    private String colon;
    private String quote;

    public EntityFormatter(String colon) {
        this(colon, "");
    }

    public EntityFormatter(String colon, String quote) {
        this.colon = colon;
        this.quote = quote;
    }

    public String formatSingle(Entity entity){
        return appendSingle(entity, new StringBuilder()).toString();
    }

    public StringBuilder appendSingle(Entity entity, StringBuilder builder){
        return builder.append(entity.getEntityType().getName()).append(colon).append(quote).append(entity.getName()).append(quote);
    }
}
