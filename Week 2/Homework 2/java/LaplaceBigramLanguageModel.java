
import java.util.List;
import java.util.HashMap;

public class LaplaceBigramLanguageModel implements LanguageModel {

    private HashMap<String, Integer> unigram; //key = w(i)
    private HashMap<String, Integer> bigram; //key = w(i-1)+w(i)
    int count = 0;

    /** Initialize your data structures in the constructor. */
    public LaplaceBigramLanguageModel(HolbrookCorpus corpus) {
        unigram = new HashMap<String, Integer>();
        bigram = new HashMap<String, Integer>();
        train(corpus);
    }

    /** Takes a corpus and trains your language model. 
     * Compute any counts or other corpus statistics in this function.
     */
    public void train(HolbrookCorpus corpus) {
        String w;
        for (Sentence sentence : corpus.getData()) { // iterate over sentences
            for (int i = 0; i < sentence.size(); i++) { // iterate over words
                w = sentence.get(i).getWord(); // get the actual word
                
                //count w(i) ocurrencies
                if (!unigram.containsKey(w))
                    unigram.put(w, 0);

                unigram.put(w, unigram.get(w)+1);

                //count w(i-1)w(i) ocurrencies
                if (i > 0) {
                    String wa = sentence.get(i-1).getWord() +"->"+ w;

                    if (!bigram.containsKey(wa))
                        bigram.put(wa, 0);
                    
                    bigram.put(wa, bigram.get(wa)+1);
                }
                count++;
            }
        }
    }

    /** Takes a list of strings as argument and returns the log-probability of the 
     * sentence using your language model. Use whatever data you computed in train() here.
     */
    public double score(List<String> sentence) {
        double score = 0.0f;
        String w, waw, wa;
        Integer cwa, cwaw, cw;
        
        for (int i = 0; i < sentence.size(); i++) { // iterate over words
            w = sentence.get(i); // get the actual word

            //probabilities
            if (i > 0) {
                wa = sentence.get(i-1);
                waw = wa +"->"+ w;
                cwa = unigram.get(wa);
                cwaw = bigram.get(waw);
                score += Math.log((cwaw == null ? 0 : cwaw) + 1) - 
                         Math.log((cwa == null ? 0 : cwa) + unigram.size());
            }else{
                cw = unigram.get(w); cw = cw==null?0:cw;
                score += Math.log(cw+1) - Math.log(count+unigram.size());
            }
        }
        return score;
    }
}
