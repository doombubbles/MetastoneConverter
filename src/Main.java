import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.util.*;
import java.util.List;


public class Main {

    //public static final String SET = "WITCHWOOD";
    //public static final String PATH = "C:\\Users\\James Gale\\Documents\\metastone\\cards\\";

    public static void main(String[] args) {
        String set = new File("").getAbsolutePath();
        set = set.substring(set.lastIndexOf("\\") + 1).toUpperCase();
        Scanner input;
        try {
            //input = new Scanner(new File(PATH + SET.toLowerCase() + "\\stuff.txt"));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            input = new Scanner((String) clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor));
        } catch (UnsupportedFlavorException e) {
            JOptionPane.showMessageDialog(null, "Not a String copied!");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "I don't even know");
            e.printStackTrace();
            return;
        }
        Card card = new Card(input);
        List<String> tokens = tokenizeTextLine(card.text);
        card.findRelevantBits(tokens);
        PrintStream output = getOutputFile(card);
        printCardToFile(card, output, set);

        JOptionPane.showMessageDialog(null, "Successfully built " + card.name);

    }

    //convert text line into list of tokens
    public static List<String> tokenizeTextLine(String text) {
        List<String> tokens = new ArrayList<>();
        text = text.toLowerCase();
        if (text.contains("can't be targeted by spells or hero powers")) {
            text = text.replace("can't be targeted by spells or hero powers", "");
            tokens.add("untargetable by spells");
        }
        if (text.contains("divine shield")) {
            text = text.replace("divine shield", "");
            tokens.add("divine shield");
        }
        if (text.contains("cant attack") || text.contains("can't attack")) {
            text= text.replace("cant attack", "").replace("can't attack", "");
            tokens.add("cannot attack");
        }
        Scanner textLine = new Scanner(text);
        while (textLine.hasNext()) {
            String token = textLine.next();
            if (token.equalsIgnoreCase("overload:") && textLine.hasNext()) {
                String nextToken = textLine.next();
                token = "Overload: " + nextToken.replace("(", "").replace(")", "").replace(".", "");
            } else if (token.equalsIgnoreCase("spell") && textLine.hasNext()) {
                String nextToken = textLine.next();
                if (nextToken.equalsIgnoreCase("damage") && textLine.hasNext()) {
                    token = "Spell Damage " + textLine.next();
                } else tokens.add(nextToken);
            }

            tokens.add(token);
        }
        return tokens;
    }

    public static PrintStream getOutputFile(Card card) {
        String fileNameFormat = card.name.toLowerCase().replace(" ", "_").replace("'", "").replace("-", "_").replace("!", "");
        PrintStream output = null;
        try {
            Scanner hmmmFile = new Scanner(new File(card.heroClass + "\\" + card.type.toLowerCase() + "_" + fileNameFormat + ".json"));
        } catch (FileNotFoundException e) {
            try {
                output = new PrintStream(new File(card.heroClass + "\\" + card.type.toLowerCase() + "_" + fileNameFormat + ".json"));
            } catch (FileNotFoundException f) {
                System.out.println("Hmmm lol rip");
                f.printStackTrace();
            }
        }

        if (card.heroClass.equalsIgnoreCase("Neutral")) {
            card.heroClass = "ANY";
        }
        return output;
    }

    public static void printCardToFile(Card card, PrintStream output, String set) {
        output.println("{");
        output.println("  \"name\": \"" + card.name + "\",");
        output.println("  \"baseManaCost\": " + card.mana + ",");
        output.println("  \"type\": \"" + card.type.toUpperCase() + "\",");
        if (card.type.equalsIgnoreCase("Minion")) {
            output.println("  \"baseAttack\": " + card.attack + ",");
            output.println("  \"baseHp\": " + card.health + ",");
        } else if (card.type.equalsIgnoreCase("weapon")) {
            output.println("  \"damage\": " + card.attack + ",");
            output.println("  \"durability\": " + card.durability + ",");
        }
        output.println("  \"heroClass\": \"" + card.heroClass.toUpperCase() + "\",");
        output.println("  \"rarity\": \"" + card.rarity.toUpperCase() + "\",");
        if (!card.tribe.isEmpty()) {
            output.println("  \"race\": \"" + card.tribe.toUpperCase() + "\",");
        }
        output.println("  \"description\": \"" + card.text + "\",");
        if (card.type.equalsIgnoreCase("Spell")) {
            output.println("  \"targetSelection\": \"\",");
            output.println("  \"spell\": {");
            output.println("    \"class\": \"\"");
            output.println("  },");
        }
        if (card.battlecry) {
            output.println("  \"battlecry\": {");
            output.println("    \"targetSelection\": \"\",");
            output.println("    \"spell\": {");
            output.println("      \"class\": \"\"");
            output.print("      }");
            if (card.combo) {
                output.println(",");
                output.println("      \"condition\": {");
                output.println("        \"class\": \"ComboCondition\"");
                output.println("      }");
            } else output.println("");
            output.println("  },");
        }
        if (card.trigger) {
            output.println("  \"trigger\": {");
            output.println("    \"eventTrigger\": {");
            output.println("      \"class\": \"\"");
            output.println("    },");
            output.println("    \"spell\": {");
            output.println("      \"class\": \"\"");
            output.println("    }");
            output.println("  },");
        }
        if (card.deathrattle) {
            output.println("  \"deathrattle\": {");
            output.println("    \"class\": \"\"");
            output.println("  },");
        }
        if (!card.attributes.isEmpty()) {
            output.println("  \"attributes\": {");
            int i = 0;
            for (String attribute : card.attributes.keySet()) {
                output.print("    \"" + attribute.toUpperCase() + "\": " + card.attributes.get(attribute));
                i++;
                output.println(i < card.attributes.size() ? "," : "");
            }
            output.println("  },");
        }
        output.println("  \"collectible\": true,");
        output.println("  \"set\": \"" + set + "\",");
        output.println("  \"fileFormatVersion\": 1");
        output.println("}");
    }
}
