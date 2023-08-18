
import java.util.List;
import java.util.HashMap;

public class StupidBackoffLanguageModel implements LanguageModel {

    private HashMap<String, Integer> unigram; //key = w(i)
    private HashMap<String, Integer> bigram; //key = w(i-1)+w(i)
    int count;

    /** Initialize your data structures in the constructor. */
    public StupidBackoffLanguageModel(HolbrookCorpus corpus) {
        unigram = new HashMap<String, Integer>();
        bigram = new HashMap<String, Integer>();
        count = 0;
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
        //System.out.println(count +":"+unigram.size()+":"+bigram.size());
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
                score += probBigram(wa, w);
            }else{
                score += probUnigram(w);
            }
        }
        return score;
    }
    
    public double probUnigram(String w){
        Integer cw = unigram.get(w);
        
        return Math.log((cw==null?0:cw) + 1) - Math.log(count + unigram.size());
    }
    
    public double probBigram(String wa, String w){
        String waw = wa +"->"+ w;
        Integer cwa = unigram.get(wa);
        Integer cwaw = bigram.get(waw);
        
         if(cwaw!=null && cwaw>0)
            return Math.log(cwaw) - Math.log(cwa);
         
         return Math.log(0.4) + probUnigram(w);
    }
}
