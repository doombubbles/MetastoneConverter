import java.util.*;

public class Card {

   static final List<String> POTENTIAL_ATTRIBUTES = Arrays.asList("TAUNT", "LIFESTEAL", "RUSH", "CHARGE", "ECHO", "POISONOUS", "COMBO",
           "STEALTH", "WINDFURY", "DIVINE_SHIELD", "CHARGE", "CANNOT_ATTACK", "MAGNETIC", "UNTARGETABLE_BY_SPELLS");
   static final List<String> BAD_WORDS = Arrays.asList("gain", "have", "your", "has", "a", "it", "with", "friendly", "enemy", "and");

   int mana = 0;
   int attack = -1;
   int health = -1;
   int durability = -1;
   String name = "";
   String tribe = "";
   String type = "NONE";
   String rarity = "";
   String heroClass = "";
   String text = "";

    boolean battlecry = false;
    boolean deathrattle = false;
    boolean trigger = false;
    boolean combo = false;
    Map<String, String> attributes = new HashMap<>();

    Card(Scanner input) {
        name = input.nextLine();
        while (input.hasNextLine()) {
            String line = input.nextLine();
            if (line.toLowerCase().startsWith("mana") || line.toLowerCase().startsWith("cost")) {
                mana = Integer.parseInt(line.substring(line.indexOf(":") + 2));
            } else if (line.toLowerCase().startsWith("att")) {
                attack = Integer.parseInt(line.substring(line.indexOf(":") + 2));
            } else if (line.toLowerCase().startsWith("health") || line.toLowerCase().startsWith("hp") || line.toLowerCase().startsWith("defense")) {
                health = Integer.parseInt(line.substring(line.indexOf(":") + 2));
            } else if (line.toLowerCase().startsWith("durability")) {
                durability = Integer.parseInt(line.substring(line.indexOf(":") + 2));
            } else if (line.toLowerCase().startsWith("tribe") || line.toLowerCase().startsWith("race")) {
                tribe = line.substring(line.indexOf(":") + 2);
            } else if (line.toLowerCase().startsWith("rar")) {
                rarity = line.substring(line.indexOf(":") + 2);
            } else if (line.toLowerCase().startsWith("class")) {
                heroClass = convertHeroClass(line.substring(line.indexOf(":") + 2));
            } else if (line.toLowerCase().startsWith("text")) {
                text = line.substring(line.indexOf(":") + 2);
            } else if (line.toLowerCase().startsWith("type")) {
                type = line.substring(line.indexOf(":") + 2);
            }
        }
    }

    public String convertHeroClass(String s) {
        s = s.toUpperCase();
        switch (s) {
            case "PALADIN":
                return "GOLD";
            case "WARLOCK":
                return "VIOLET";
            case "MAGE":
                return "BLUE";
            case "SHAMAN":
                return "SILVER";
            case "DRUID":
                return "BROWN";
            case "PRIEST":
                 return "WHITE";
            case "HUNTER":
                return "GREEN";
            case "WARRIOR":
                return "RED";
            case "ROGUE":
                return "BLACK";
        }
        return s;
    }

    public void findRelevantBits(List<String> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equalsIgnoreCase("Battlecry")) {
                if (tokens.size() > i + 1) {
                    if (tokens.get(i + 1).equalsIgnoreCase("and")) {
                        battlecry = true;
                        attributes.putIfAbsent("BATTLECRY", "true");
                    }
                }
            } else if (token.equalsIgnoreCase("Battlecry:")) {
                battlecry = true;
                attributes.putIfAbsent("BATTLECRY", "true");
            } else if (token.equalsIgnoreCase("Deathrattle:")) {
                deathrattle = true;
                attributes.putIfAbsent("DEATHRATTLES", "true");
            } else if (token.equalsIgnoreCase("After") || token.equalsIgnoreCase("Whenever") || token.equalsIgnoreCase("While") || token.equalsIgnoreCase("At")) {
                trigger = true;
            } else if (token.startsWith("Overload:") || token.startsWith("overload:")) {
                attributes.put("OVERLOAD", token.substring(token.indexOf(" ") + 1));
            } else if (token.startsWith("Spell Damage")) {
                attributes.put("SPELL_DAMAGE", token.substring(token.indexOf("+") + 1));
            } else if (token.equalsIgnoreCase("Combo:")) {
                battlecry = true;
                combo = true;
            }
            for (String attribute : POTENTIAL_ATTRIBUTES) {
                if (token.equalsIgnoreCase(attribute.replace("_", " ") + ".")
                        || token.equalsIgnoreCase(attribute.replace("_", " ") + ",")
                        || token.equalsIgnoreCase(attribute.replace("_", " "))) {
                    if (i == 0) {
                        attributes.putIfAbsent(attribute, "true");
                    } else if (i > 0) {
                        if (!BAD_WORDS.contains(tokens.get(i - 1).toLowerCase())) {
                            attributes.putIfAbsent(attribute, "true");
                        }
                    }
                }
            }
        }
    }

}
