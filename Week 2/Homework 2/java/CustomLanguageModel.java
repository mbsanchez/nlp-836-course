
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CustomLanguageModel implements LanguageModel {

    private HashMap<String, Integer> unigram; //key = w(i)
    private HashMap<String, Integer> bigram;  //key = w(i-1)w(i)
    private HashMap<String, Integer> trigram; //key = w(i-2)w(i-1)w(i)
    int count;

    /** Initialize your data structures in the constructor. */
    public CustomLanguageModel(HolbrookCorpus corpus) {
        unigram = new HashMap<String, Integer>();
        bigram = new HashMap<String, Integer>();
        trigram = new HashMap<String, Integer>();
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

                //count w(i) ocurrencies -> unigram
                if (!unigram.containsKey(w)) 
                    unigram.put(w, 0);
                unigram.put(w, unigram.get(w) + 1);

                //bigram and trigram
                if (i > 0) {
                    String wa = sentence.get(i - 1).getWord() + "->" + w;
                    //bigram w(i-1)w(i)
                    if (!bigram.containsKey(wa)) 
                        bigram.put(wa, 0);
                    bigram.put(wa, bigram.get(wa) + 1);
                    
                    //trigram
                    if (i > 1) {
                        wa = sentence.get(i - 2).getWord() + "->" + wa;
                        //triagram w(i-2)w(i-1)w(i)
                        if(!trigram.containsKey(wa))
                            trigram.put(wa, 0);
                        trigram.put(wa, trigram.get(wa)+1);
                    }
                }
                count++;
            }
        }
    }
    
    /** Takes a corpus and trains your language model. 
     * Compute any counts or other corpus statistics in this function.
     */
    /*public void train(HolbrookCorpus corpus) {
        String w, w1, w2;
        
        //unigrams
        for (Sentence sentence : corpus.getData()) { // iterate over sentences
            for (int i = 0; i < sentence.size(); i++) { // iterate over words
                w = sentence.get(i).getWord();
                
                if(!unigram.containsKey(w))
                    unigram.put(w, 0);
                unigram.put(w, unigram.get(w)+1);
            }
            count++;
        }

        //generate UNK
        HashSet<String> delKeys = new HashSet<String>();
        Integer unkCount= new Integer(0);
        for(String key: unigram.keySet()){
            if(unigram.get(key).intValue()<=1){
                unkCount+=unigram.get(key);
                delKeys.add(key);
            }
        }               
        //delete keys with low frequency
        for(String key: delKeys){
            unigram.remove(key);
        }
        //put unk token
        unigram.put("UNK", unkCount);
        
        //build bigrams and trigrams
        for (Sentence sentence : corpus.getData()) { // iterate over sentences
            for (int i = 1; i < sentence.size(); i++) { // iterate over words
                w = sentence.get(i).getWord(); 
                w1 = sentence.get(i-1).getWord(); 
                
                //verifiy w is unigram
                if(!unigram.containsKey(w))
                    w = "UNK";
                //verify w1 is unigram
                if(!unigram.containsKey(w1))
                    w1 = "UNK";

                String wa = w1 + "->" + w;
                //bigram w(i-1)w(i)
                if (!bigram.containsKey(wa)) 
                    bigram.put(wa, 0);
                bigram.put(wa, bigram.get(wa) + 1);

                //trigram
                if (i > 1) {
                    w2 = sentence.get(i - 2).getWord();
                    
                    //verify w2 is unigram
                    if(!unigram.containsKey(w2))
                        w2 = "UNK";
                    wa =  w2 + "->" + wa;
                    //trigram w(i-2)w(i-1)w(i)
                    if(!trigram.containsKey(wa))
                        trigram.put(wa, 0);
                    trigram.put(wa, trigram.get(wa)+1);
                }
            }
        }
    }*/


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
            if(i<1){ //unigram
                score+= probUnigram(w);
            }
            else if(i<2){ //bigram
                wa = sentence.get(i-1);
                score += probBigram(wa, w);
            }
            else{ //trigram
                wa = sentence.get(i-1);
                waw = sentence.get(i-2);
                score += probTrigram(waw, wa, w);
            }
            
        }
        return score;
    }
    
    public double probUnigram(String w){
        w = (!unigram.containsKey(w))?"UNK":w;
        Integer cw = unigram.get(w);
        
        return Math.log((cw==null?0:cw) + 0.00001) - Math.log(count + 0.00001*unigram.size());
    }
    
    public double probBigram(String wa, String w){
        w = (!unigram.containsKey(w))?"UNK":w;       
        wa = (!unigram.containsKey(wa))?"UNK":wa;
        
        Integer cwa = unigram.get(wa);
        Integer cwaw = bigram.get(wa+"->"+w);
               
        if(cwaw!=null && cwaw>0)
            return Math.log(cwaw) - Math.log(cwa);
         
         return Math.log(0.4) + probUnigram(w);
    }
    
    public double probTrigram(String wa2, String wa1, String w){
        w = (!unigram.containsKey(w))?"UNK":w;       
        wa1 = (!unigram.containsKey(wa1))?"UNK":wa1;
        wa2 = (!unigram.containsKey(wa2))?"UNK":wa2;
        
        Integer cwa = bigram.get(wa1+"->"+w);
        Integer cwaw = trigram.get(wa2+"->"+wa1+"->"+w);
        
        if(cwaw!=null && cwaw>0)
            return Math.log(cwaw) - Math.log(cwa);
         
         return Math.log(0.4) + probBigram(wa1, w);
    }
}
