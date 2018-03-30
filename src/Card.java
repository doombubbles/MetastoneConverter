import java.util.*;

public class Card {

   static final List<String> POTENTIAL_ATTRIBUTES = Arrays.asList("TAUNT", "LIFESTEAL", "RUSH", "CHARGE", "ECHO", "POISONOUS", "COMBO",
           "STEALTH", "WINDFURY", "DIVINE_SHIELD", "CHARGE", "CANNOT_ATTACK");
   static final List<String> BAD_WORDS = Arrays.asList("gain", "have", "your", "has", "a");

   int mana = 0;
   int attack = -1;
   int health = -1;
   String name = "";
   String tribe = "";
   String type = "";
   String rarity = "";
   String heroClass = "";
   String text = "";

    boolean battlecry = false;
    boolean deathrattle = false;
    boolean trigger = false;
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
            } else if (line.toLowerCase().startsWith("tribe") || line.toLowerCase().startsWith("race")) {
                tribe = line.substring(line.indexOf(":") + 2);
            } else if (line.toLowerCase().startsWith("rar")) {
                rarity = line.substring(line.indexOf(":") + 2);
            } else if (line.toLowerCase().startsWith("class")) {
                heroClass = line.substring(line.indexOf(":") + 2);
            } else if (line.toLowerCase().startsWith("text")) {
                text = line.substring(line.indexOf(":") + 2);
            } else if (line.toLowerCase().startsWith("type")) {
                type = line.substring(line.indexOf(":") + 2);
            }
        }
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
            } else if (token.equalsIgnoreCase("After") || token.equalsIgnoreCase("Whenever") || token.equalsIgnoreCase("While")) {
                trigger = true;
            }
            for (String attribute : POTENTIAL_ATTRIBUTES) {
                if (token.equalsIgnoreCase(attribute.replace("_", " ") + ".")) {
                    attributes.putIfAbsent(attribute, "true");
                } else if (token.equalsIgnoreCase(attribute.replace("_", " "))) {
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
