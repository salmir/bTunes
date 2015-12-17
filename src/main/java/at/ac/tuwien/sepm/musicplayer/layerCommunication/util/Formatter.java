package at.ac.tuwien.sepm.musicplayer.layerCommunication.util;

import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.info.InfoOwner;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.InfoType;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.entity.type.Type;
import at.ac.tuwien.sepm.musicplayer.layerCommunication.util.EntityFormatter;

import java.util.Collection;

/**
 * Created by Lena Lenz.
 */
public class Formatter extends EntityFormatter {
    public static final Formatter TO_STRING_FORMATTER = new Formatter(", ", "=", "'");
    private static final String DEFAULT_TO_STRING_SEPARATOR = ", ";
    private static final String DEFAULT_TO_STRING_COLON = "=";

    private static final String DEFAULT_TO_READABLE_SEPARATOR = ",\n";
    private static final String DEFAULT_TO_READABLE_COLON = ": ";

    private static final String DEFAULT_QUOTE = "'";

    private EntityFormatter headlineFormatter;
    private String separator;

    public Formatter(String separator, String colon){
        this(null, separator, colon, "");
    }

    public Formatter(String separator, String colon, String quote){
        this(null, separator, colon, quote);
    }

    public Formatter(EntityFormatter headlineFormatter, String separator, String colon){
        this(headlineFormatter, separator, colon, "");
    }

    public Formatter(EntityFormatter headlineFormatter, String separator, String colon, String quote){
        super(colon, quote);
        this.headlineFormatter = headlineFormatter;
        this.separator = separator;
    }

    public String format(Collection<? extends InfoOwner> infoOwners){
        return format(infoOwners, false);
    }

    public String format(Collection<? extends InfoOwner> infoOwners, boolean leadingSeparator){
        return append(infoOwners, new StringBuilder(), leadingSeparator).toString();
    }

    public StringBuilder append(Collection<? extends InfoOwner> infoOwners, StringBuilder builder){
        return append(infoOwners, builder, false);
    }

    public StringBuilder append(Collection<? extends InfoOwner> infoOwners, StringBuilder builder, boolean leadingSeparator){
        for(InfoOwner infoOwner : infoOwners){
            if(leadingSeparator){
                builder.append(separator);
            }else{
                leadingSeparator = true;
            }
            append(infoOwner, builder);
        }
        return builder;
    }

    public String format(InfoOwner infoOwner){
        return append(infoOwner, new StringBuilder()).toString();
    }

    public StringBuilder append(InfoOwner infoOwner, StringBuilder builder){
        EntityFormatter headFormatter = headlineFormatter!=null?headlineFormatter:this;
        return appendInfos(infoOwner, headFormatter.appendSingle(infoOwner, builder), true);
    }

    public String formatInfos(InfoOwner infoOwner){
        return formatInfos(infoOwner, false);
    }

    public String formatInfos(InfoOwner infoOwner, boolean leadingSeparator){
        return appendInfos(infoOwner, new StringBuilder(), leadingSeparator).toString();
    }

    private StringBuilder appendInfos(InfoOwner infoOwner, StringBuilder builder, boolean leadingSeparator){
        for(InfoType infoType : Type.INFO_TYPES){
            if(infoOwner.has(infoType)){
                if(leadingSeparator){
                    builder.append(separator);
                }else{
                    leadingSeparator = true;
                }
                appendSingle(infoOwner.get(infoType), builder);
            }
        }
        return builder;
    }
}
