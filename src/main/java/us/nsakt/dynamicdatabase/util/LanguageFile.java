package us.nsakt.dynamicdatabase.util;

import com.google.common.collect.Maps;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Class to help manage the language file.
 *
 * @author skipperguy12
 *         <p/>
 *         Some information from Bukkit forums
 */
public class LanguageFile {
    private final Map<String, String> messages = Maps.newHashMap();
    private final LanguageEnum language;

    public LanguageFile(LanguageEnum language) {
        this.language = language;
        try {
            messages.clear();
            List<String> lines = FileUtils.readLines(new File(DynamicDatabasePlugin.getInstance().getDataFolder(), language.getAbbreviation() + ".lang"));
            for (String line : lines) {
                String[] split = line.split("=");
                messages.put(split[0], split[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LanguageEnum getLocale(Player p) {
        try {
            Object ep = getMethod("getHandle", p.getClass()).invoke(p, (Object[]) null);
            Field f = ep.getClass().getDeclaredField("locale");
            f.setAccessible(true);
            String language = (String) f.get(ep);
            LanguageEnum code = getByAbbreviation(language);
            return code;
        } catch (Exception exc) {
            exc.printStackTrace();
            return getByAbbreviation("en_CA");
        }
    }

    private static Method getMethod(String name, Class<?> clazz) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(name)) return m;
        }
        return null;
    }

    public static LanguageEnum getByAbbreviation(String code) {
        for (LanguageEnum l : LanguageEnum.values()) {
            if (l.getAbbreviation().equalsIgnoreCase(code)) return l;
        }
        return LanguageEnum.ENGLISH;
    }

    public String get(String key, Object... args) {
        String message = messages.containsKey(key) ? messages.get(key) : "No message found for '" + key + "'!";
        for (int i = 0; i < args.length; i++)
            message = message.replace("{" + i + "}", args[i].toString());
        return message;
    }

    public static enum LanguageEnum {
        AUSTRALIAN_ENGLISH("Australian English", "en_CA"), AFRIKAANS("Afrikaans", "af_ZA"), ARABIC("العربية", "ar_SA"), BULGARIAN("Български", "bg_BG"), CATALAN("Català", "ca_ES"), CZECH("Čeština", "cs_CZ"), CYMRAEG("Cymraeg", "cy_GB"), DANISH("Dansk", "da_DK"), GERMAN("Deutsch", "de_DE"), GREEK("Ελληνικά", "el_GR"), CANADIAN_ENGLISH("Canadian English", "en_CA"), ENGLISH("English", "en_CA"), PIRATE_SPEAK("Pirate Speak", "en_PT"), ESPERANTO("Esperanto", "eo_EO"), ARGENTINEAN_SPANISH("Español Argentino", "es_AR"), SPANISH("Español", "es_ES"), MEXICO_SPANISH("Español México", "es_MX"), URUGUAY_SPANISH("Español Uruguay", "es_UY"), VENEZUELA_SPANISH("Español Venezuela", "es_VE"), ESTONIAN("Eesti", "et_EE"), EUSKARA("Euskara", "eu_ES"), ENGLISH1("زبان انگلیسی", "en_CA"), FINNISH("Suomi", "fi_FI"), TAGALOG("Tagalog", "fil_PH"), FRENCH_CA("Français", "fr_CA"), FRENCH("Français", "fr_FR"), GAEILGE("Gaeilge", "ga_IE"), GALICIAN("Galego", "gl_ES"), HEBREW("עברית", "he_IL"), ENGLISH2("अंग्रेज़ी", "en_CA"), CROATIAN("Hrvatski", "hr_HR"), HUNGARIAN("Magyar", "hu_HU"), ARMENIAN("Հայերեն", "hy_AM"), BAHASA_INDONESIA("Bahasa Indonesia", "id_ID"), ICELANDIC("Íslenska", "is_IS"), ITALIAN("Italiano", "it_IT"), JAPANESE("日本語", "ja_JP"), GEORGIAN("ქართული", "ka_GE"), KOREAN("한국어", "ko_KR"), KERNEWEK("Kernewek", "kw_GB"), ENGLISH3("अंग्रेज़ी", "en_CA"), LINGUA_LATINA("Lingua latina", "la_LA"), LETZEBUERGESCH("Lëtzebuergesch", "lb_LU"), LITHUANIAN("Lietuvių", "lt_LT"), LATVIAN("Latviešu", "lv_LV"), MALAY_NZ("Bahasa Melayu", "mi_NZ"), MALAY_MY("Bahasa Melayu", "ms_MY"), MALTI("Malti", "mt_MT"), NORWEGIAN("Norsk", "nb_NO"), DUTCH("Nederlands", "nl_NL"), NORWEGIAN_NYNORSK("Norsk nynorsk", "nn_NO"), NORWEGIAN1("Norsk", "no_NO"), OCCITAN("Occitan", "oc_FR"), PORTUGUESE_BR("Português", "pt_BR"), PORTUGUESE_PT("Português", "pt_PT"), QUENYA("Quenya", "qya_AA"), ROMANIAN("Română", "ro_RO"), RUSSIAN("Русский", "ru_RU"), ENGLISH4("Angličtina", "en_CA"), SLOVENIAN("Slovenščina", "sl_SI"), SERBIAN("Српски", "sr_SP"), SWEDISH("Svenska", "sv_SE"), THAI("ภาษาไทย", "th_TH"), tlhIngan_Hol("tlhIngan Hol", "tlh_AA"), TURKISH("Türkçe", "tr_TR"), UKRAINIAN("Українська", "uk_UA"), VIETNAMESE("Tiếng Việt", "vi_VI"), SIMPLIFIED_CHINESE("简体中文", "zh_CN"), TRADITIONAL_CHINESE("繁體中文", "zh_TW"), POLISH("Polski", "pl_PL");

        private final String languageName;
        private final String abbreviation;

        LanguageEnum(String languageName, String abbreviation) {
            this.languageName = languageName;
            this.abbreviation = abbreviation;
        }

        public String getLanguageName() {
            return languageName;
        }

        public String getAbbreviation() {
            return abbreviation;
        }
    }
}