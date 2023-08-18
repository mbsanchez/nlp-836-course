//CS124 HW6 Wikipedia Relation Extraction
//Alan Joyce (ajoyce)

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Wiki {

    private Pattern infoboxPattern = Pattern.compile("\\{\\{[ ]*[I][Nn][Ff][Oo][Bb][Oo][Xx].*?(?=\\n\\}\\}\\n)", Pattern.DOTALL);
    private Pattern namePattern = Pattern.compile("\\|[ ]*[Nn][Aa][Mm][Ee][ ]*=.*?(?=\\n\\|)", Pattern.DOTALL);
    private Pattern spousePattern = Pattern.compile("\\|[ ]*[Ss][Pp][Oo][Uu][Ss][Ee][ ]*=.*?(?=\\n\\|)", Pattern.DOTALL);
    //private Pattern isMarriedToPattern  = Pattern.compile("\\n.*?[Ii][Ss][ ]*[Mm][Aa][Rr][Rr][Ii][Ee][Dd][ ]*[Tt][Oo].*?(?=\\n)");
    private Pattern wifeOfPattern = Pattern.compile("\\n.*?[ ]+[Ww][Ii][Ff][Ee][ ]+[Oo][Ff][ ]+.*?(?=\\n)");
    private Pattern marriedPattern = Pattern.compile("\\n.*?[ ]+[Mm][Aa][Rr][Rr][Ii][Ee][Dd][ ]+.*?(?=\\n)");

    public List<String> addWives(String fileName) {
        List<String> wives = new ArrayList<String>();
        try {
            BufferedReader input = new BufferedReader(new FileReader(fileName));
            // for each line
            for (String line = input.readLine(); line != null; line = input.readLine()) {
                wives.add(line);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
        return wives;
    }
    /*
     * read through the wikipedia file and attempts to extract the matching husbands. note that you will need to provide
     * two different implementations based upon the useInfoBox flag.
     */

    public void extractFromInfobox(String pageText, String title, List<String> wives, HashMap<String, List<String>> married) {
        Matcher infobox = infoboxPattern.matcher(pageText);
        Matcher spouse;

        while (infobox.find()) {
            //System.out.println(matcher.group());
            spouse = spousePattern.matcher(infobox.group());

            if (spouse.find()) {
                for (String wife : wives) {
                    String names[] = wife.split(" +");
                    String regex = "";

                    for (String name : names) {
                        regex += ".*" + name;
                    }
                    regex = regex + ".*";
                    if (spouse.group().matches(regex)) {
                        if (!married.containsKey(wife)) {
                            married.put(wife, new ArrayList<String>());
                        }
                        married.get(wife).add(title);
                    }
                }
            }
        }
    }

    public void extractFromText(String pageText, String title, List<String> wives, HashMap<String, List<String>> married) {
        Pattern patterns[] = new Pattern[]{marriedPattern, wifeOfPattern};
        Matcher matcher;
        String titleNames[] = title.split(" +");
        String rHusband = "";

        //for (Pattern pattern : patterns) {
        //    matcher = pattern.matcher(pageText);
        //    while (matcher.find()) {

        for (int i = 0; i < titleNames.length; i++) {
            if (i > 0 && i < titleNames.length - 1) {
                continue;
            }
            rHusband += ((i != 0) ? "|" : "(") + "(" + titleNames[i] + ")";
        }
        rHusband = rHusband + "|(" + title + "))";

        for (String wife : wives) {
            String wifeNames[] = wife.split(" +");
            boolean first = true;
            String regex = "", rWife = "";

            for (String name : wifeNames) {
                rWife += ((!first)?"|":"(") +"("+wifeNames[0]+")";
                first = false;
            }
            rWife = rWife + "|(" + wife + "))";

            regex = "(" + rWife + ".*((wife of)|(is married to)).*" + rHusband + ")" + "|"
                    + "(" + rHusband + ".*( married ).*" + wife + ")" + "|" + "(" + rHusband + ".*( proposed to ).*" + rWife + ")";
            matcher = Pattern.compile(regex).matcher(pageText);

            if (matcher.find()) {
                if (!married.containsKey(wife)) {
                    married.put(wife, new ArrayList<String>());
                }
                married.get(wife).add(title);
            }
        }
        //  }
        // }
    }

    public List<String> processFile(File f, List<String> wives, boolean useInfoBox) {

        List<String> husbands = new ArrayList<String>();
        HashMap<String, List<String>> married = new HashMap<String, List<String>>();

        // Process the wiki file and fill the husbands Array
        // +1 for correct Answer, 0 for no answer, -1 for wrong answers
        // add 'No Answer' string as the answer when you dont want to answer
        BufferedReader buffer = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(f);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("page");

            for (int s = 0; s < nodeLst.getLength(); s++) {
                Node fstNode = nodeLst.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("revision");
                    Node title = (Node) ((Element) fstElmnt.getElementsByTagName("title").item(0)).getChildNodes();

                    //System.out.println(title.getTextContent());
                    //for( int e = 0; e<lstNmElmntLst.getLength(); e++){
                    Node fstRevNode = lstNmElmntLst.item(0);

                    if (fstRevNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element fstRevElmnt = (Element) fstRevNode;
                        NodeList text = fstRevElmnt.getElementsByTagName("text");
                        Element textElmnt = (Element) text.item(0);
                        Node textNm = (Node) textElmnt.getChildNodes();

                        if (useInfoBox) {
                            extractFromInfobox(textNm.getTextContent(), title.getTextContent(), wives, married);
                        } else {
                            extractFromText(textNm.getTextContent(), title.getTextContent(), wives, married);
                        }
                    }
                }
            }

            for (String wife : wives) {
                String question = "";

                if (married.containsKey(wife)) {
                    for (String husband : married.get(wife)) {
                        question += (question.isEmpty() ? "" : "|") + "Who is " + husband + "?";
                    }
                }

                if (question.isEmpty()) {
                    husbands.add("No Answer");
                } else {
                    husbands.add(question);
                }
            }
        } catch (SAXException ex) {
            Logger.getLogger(Wiki.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Wiki.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Wiki.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Wiki.class.getName()).log(Level.SEVERE, null, ex);
        }

        return husbands;
    }

    /*
     * scores the results based upon the aforementioned criteria
     */
    public void evaluateAnswers(boolean useInfoBox, List<String> husbandsLines, String goldFile) {
        int correct = 0;
        int wrong = 0;
        int noAnswers = 0;
        int score = 0;
        try {
            BufferedReader goldData = new BufferedReader(new FileReader(goldFile));
            List<String> goldLines = new ArrayList<String>();
            String line;
            while ((line = goldData.readLine()) != null) {
                goldLines.add(line);
            }
            if (goldLines.size() != husbandsLines.size()) {
                System.err.println("Number of lines in husbands file should be same as number of wives!");
                System.exit(1);
            }
            for (int i = 0; i < goldLines.size(); i++) {
                String husbandLine = husbandsLines.get(i).trim();
                String goldLine = goldLines.get(i).trim();
                boolean exampleWrong = true; // guilty until proven innocent
                if (husbandLine.equals("No Answer")) {
                    exampleWrong = false;
                    noAnswers++;
                } else { // check if correct.
                    String[] golds = goldLine.split("\\|");
                    for (String gold : golds) {
                        if (husbandLine.equals(gold)) {
                            correct++;
                            score++;
                            exampleWrong = false;
                            break;
                        }
                    }
                }
                if (exampleWrong) {
                    wrong++;
                    score--;
                }
            }
            goldData.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Correct Answers: " + correct);
        System.out.println("No Answers     : " + noAnswers);
        System.out.println("Wrong Answers  : " + wrong);
        System.out.println("Total Score    : " + score);

    }

    public static void main(String[] args) {
        String wikiFile = "../data/small-wiki.xml";
        String wivesFile = "../data/wives.txt";
        String goldFile = "../data/gold.txt";
        boolean useInfoBox = false;
        Wiki pedia = new Wiki();
        List<String> wives = pedia.addWives(wivesFile);
        List<String> husbands = pedia.processFile(new File(wikiFile), wives, useInfoBox);
        pedia.evaluateAnswers(useInfoBox, husbands, goldFile);
    }
}
