
import java.util.HashMap;
import java.util.List;

public class LaplaceUnigramLanguageModel implements LanguageModel {

    private HashMap<String, Integer> table;
    private int count;

    /** Initialize your data structures in the constructor. */
    public LaplaceUnigramLanguageModel(HolbrookCorpus corpus) {
        table = new HashMap<String, Integer>();
        count = 0;

        train(corpus);
    }

    /** Takes a corpus and trains your language model. 
     * Compute any counts or other corpus statistics in this function.
     */
    public void train(HolbrookCorpus corpus) {
        String word;
        for (Sentence sentence : corpus.getData()) { // iterate over sentences
            for (Datum datum : sentence) { // iterate over words
                word = datum.getWord(); // get the actual word
                if (!table.containsKey(word))
                    table.put(word, 0); 
                
                table.put(word, table.get(word)+1);
                count++;
            }
        }
    }

    /** Takes a list of strings as argument and returns the log-probability of the 
     * sentence using your language model. Use whatever data you computed in train() here.
     */
    public double score(List<String> sentence) {
        double score = 0.0;

        for (String word : sentence) { // iterate over words in the sentence
            Integer value = table.get(word);
            score += Math.log((value == null ? 0 : value) + 1) - Math.log(count + table.size());
        }
        return score;
    }
}
